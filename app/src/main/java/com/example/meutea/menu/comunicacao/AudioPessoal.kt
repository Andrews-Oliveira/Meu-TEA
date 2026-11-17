package com.example.meutea.menu.comunicacao

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import java.io.File
import java.security.MessageDigest

/**
 * Gravações reais por cartão, apenas no armazenamento interno deste aparelho.
 * Não clona voz e não envia dados ao Firebase, à nuvem ou a terceiros.
 * noBackupFilesDir evita cópia automática da voz para backup remoto do Android.
 */
internal class AudioPessoal(context: Context, private val userId: String) {
    private val appContext = context.applicationContext
    private val directory = File(appContext.noBackupFilesDir, "vozes_cartoes_v1")
    private var recorder: MediaRecorder? = null
    private var pendingFile: File? = null
    private var pendingCard: String? = null
    private var player: MediaPlayer? = null

    private fun prefix(cardId: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest("$userId:$cardId".toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun gravado(cardId: String) = File(directory, "${prefix(cardId)}.m4a")
    private fun importado(cardId: String) = File(directory, "${prefix(cardId)}.audio")

    private fun arquivo(cardId: String): File? = when {
        importado(cardId).isFile -> importado(cardId)
        gravado(cardId).isFile -> gravado(cardId)
        else -> null
    }

    fun existe(cardId: String): Boolean = arquivo(cardId) != null

    private fun criarPasta() {
        if (!directory.exists() && !directory.mkdirs()) {
            throw IllegalStateException("Não foi possível criar o armazenamento privado da voz.")
        }
    }

    @Suppress("DEPRECATION")
    private fun novoGravador(): MediaRecorder = if (Build.VERSION.SDK_INT >= 31) {
        MediaRecorder(appContext)
    } else {
        MediaRecorder()
    }

    fun iniciar(cardId: String): Result<Unit> = runCatching {
        check(recorder == null) { "Finalize a gravação atual antes de começar outra." }
        check(
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        ) { "É necessário permitir o uso do microfone." }
        criarPasta()
        pararReproducao()

        val parcial = File(directory, "${prefix(cardId)}.pendente.m4a")
        parcial.delete()
        val novo = novoGravador()
        try {
            novo.setAudioSource(MediaRecorder.AudioSource.MIC)
            novo.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            novo.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            novo.setAudioEncodingBitRate(96000)
            novo.setAudioSamplingRate(44100)
            novo.setOutputFile(parcial.absolutePath)
            novo.prepare()
            novo.start()
            recorder = novo
            pendingFile = parcial
            pendingCard = cardId
        } catch (error: Exception) {
            runCatching { novo.reset() }
            runCatching { novo.release() }
            parcial.delete()
            throw error
        }
    }

    /**
     * Preserve a gravação anterior caso o áudio novo seja curto ou o salvamento falhe.
     */
    fun finalizar(): Result<Unit> {
        val atual = recorder ?: return Result.failure(IllegalStateException("Sem gravação ativa."))
        val parcial = pendingFile
        val card = pendingCard
        recorder = null
        pendingFile = null
        pendingCard = null
        return runCatching {
            try {
                atual.stop()
            } finally {
                runCatching { atual.release() }
            }
            check(parcial != null && card != null && parcial.isFile && parcial.length() > 1500) {
                "A gravação ficou vazia. Tente gravar por mais tempo."
            }
            val destino = gravado(card)
            instalarArquivo(parcial, destino)
            importado(card).delete()
        }.onFailure {
            parcial?.delete()
        }
    }

    private fun instalarArquivo(parcial: File, destino: File) {
        criarPasta()
        val copiaAnterior = File(directory, "${destino.name}.antigo")
        copiaAnterior.delete()
        if (destino.exists() && !destino.renameTo(copiaAnterior)) {
            throw IllegalStateException("Não foi possível preservar o áudio anterior.")
        }
        if (!parcial.renameTo(destino)) {
            if (copiaAnterior.exists()) copiaAnterior.renameTo(destino)
            throw IllegalStateException("Não foi possível salvar o áudio.")
        }
        copiaAnterior.delete()
    }

    fun importar(cardId: String, uri: Uri): Result<Unit> = runCatching {
        val tipo = appContext.contentResolver.getType(uri) ?: ""
        check(tipo.startsWith("audio/")) { "Selecione um arquivo de áudio válido." }
        criarPasta()
        pararReproducao()
        val parcial = File(directory, "${prefix(cardId)}.importando")
        try {
            val entrada = appContext.contentResolver.openInputStream(uri)
                ?: error("Não foi possível abrir o arquivo selecionado.")
            entrada.use { input ->
                parcial.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var total = 0L
                    while (true) {
                        val n = input.read(buffer)
                        if (n == -1) break
                        total += n
                        check(total <= 8L * 1024 * 1024) {
                            "Áudio muito grande. Escolha um arquivo com até 8 MB."
                        }
                        output.write(buffer, 0, n)
                    }
                }
            }
            check(parcial.length() > 1500L) { "Arquivo de áudio vazio." }
            instalarArquivo(parcial, importado(cardId))
            gravado(cardId).delete()
        } finally {
            parcial.delete()
        }
    }

    fun reproduzir(cardId: String): Boolean {
        val selecionado = arquivo(cardId) ?: return false
        pararReproducao()
        val novo = MediaPlayer()
        return try {
            novo.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .build()
            )
            novo.setDataSource(selecionado.absolutePath)
            novo.setOnCompletionListener { pararReproducao() }
            novo.setOnErrorListener { _, _, _ ->
                pararReproducao()
                true
            }
            novo.prepare()
            player = novo
            novo.start()
            true
        } catch (_: Exception) {
            if (player === novo) player = null
            runCatching { novo.release() }
            false
        }
    }

    fun apagar(cardId: String): Boolean {
        pararReproducao()
        val a = importado(cardId)
        val b = gravado(cardId)
        return (!a.exists() || a.delete()) && (!b.exists() || b.delete())
    }

    fun cancelar() {
        val atual = recorder
        recorder = null
        runCatching { atual?.stop() }
        runCatching { atual?.release() }
        pendingFile?.delete()
        pendingFile = null
        pendingCard = null
    }

    private fun pararReproducao() {
        val atual = player
        player = null
        runCatching { atual?.stop() }
        runCatching { atual?.release() }
    }

    fun fechar() {
        cancelar()
        pararReproducao()
    }
}
