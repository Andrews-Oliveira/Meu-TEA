package com.example.meutea.menu.comunicacao

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.security.MessageDigest
import kotlin.math.max

/**
 * Fotografias reais dos cartões no armazenamento privado do dispositivo.
 *
 * Não envia imagens ao Firestore ou a serviços de IA. O nome do arquivo usa
 * SHA-256(UID:cardId) para que duas contas nunca compartilhem a mesma foto.
 * Fotos em noBackupFilesDir não entram no backup automático do Android.
 */
internal class FotosCartao(context: Context, private val uid: String) {
    private val appContext = context.applicationContext
    private val directory = File(appContext.noBackupFilesDir, "fotos_cartoes_v1")

    private fun chave(cardId: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest("$uid:$cardId".toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    private fun arquivo(cardId: String): File = File(directory, "${chave(cardId)}.jpg")

    fun existe(cardId: String): Boolean = arquivo(cardId).isFile

    fun remover(cardId: String): Boolean {
        val foto = arquivo(cardId)
        return !foto.exists() || foto.delete()
    }

    fun lerSalva(cardId: String): Bitmap? {
        val file = arquivo(cardId)
        if (!file.isFile) return null
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)
        if (options.outWidth <= 0 || options.outHeight <= 0) return null
        val amostra = amostragem(options.outWidth, options.outHeight, 720)
        return BitmapFactory.decodeFile(
            file.absolutePath, BitmapFactory.Options().apply { inSampleSize = amostra }
        )
    }

    /** Prévia de foto de câmera ou galeria. Sem assumir posse da URI. */
    fun lerPreview(uri: Uri): Bitmap? = abrirAmostrada(uri, 640, ignorarRotacao = false)

    private fun amostragem(width: Int, height: Int, limite: Int): Int {
        var sample = 1
        while (max(width / sample, height / sample) > limite * 2) sample *= 2
        return sample
    }

    private fun tamanhoDeclarado(uri: Uri): Long? {
        if (uri.scheme != "content") return null
        return appContext.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
            ?.use { cursor: Cursor ->
                if (!cursor.moveToFirst()) return@use null
                val idx = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (idx < 0 || cursor.isNull(idx)) null else cursor.getLong(idx)
            }
    }

    private fun orientacao(uri: Uri): Int = runCatching {
        appContext.contentResolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
        } ?: ExifInterface.ORIENTATION_NORMAL
    }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

    private fun abrirAmostrada(uri: Uri, limite: Int, ignorarRotacao: Boolean): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        appContext.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        } ?: return null
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val bitmap = appContext.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, BitmapFactory.Options().apply {
                inSampleSize = amostragem(bounds.outWidth, bounds.outHeight, limite)
            })
        } ?: return null

        if (ignorarRotacao) return bitmap
        val rotation = when (orientacao(uri)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val adjusted = if (rotation != 0f) {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height,
                Matrix().apply { postRotate(rotation) }, true)
        } else bitmap

        val scale = minOf(1f, limite.toFloat() / max(adjusted.width, adjusted.height))
        if (scale == 1f) return adjusted
        return Bitmap.createScaledBitmap(adjusted,
            max(1, (adjusted.width * scale).toInt()),
            max(1, (adjusted.height * scale).toInt()), true)
    }

    /** Salva uma cópia compactada local, preservando a anterior em caso de falha. */
    fun salvar(cardId: String, uri: Uri): Result<Unit> = runCatching {
        val tamanho = tamanhoDeclarado(uri)
        check(tamanho == null || tamanho in 1..30_000_000L) {
            "A foto deve ter até 30 MB. Selecione outra imagem."
        }
        val image = abrirAmostrada(uri, 1200, ignorarRotacao = false)
            ?: error("Não foi possível ler esta foto. Escolha uma imagem JPG ou PNG.")
        check(directory.isDirectory || directory.mkdirs()) {
            "Não foi possível criar a pasta de imagens."
        }
        val destination = arquivo(cardId)
        val draft = File(directory, "${chave(cardId)}.novo")
        val backup = File(directory, "${chave(cardId)}.anterior")
        try {
            draft.outputStream().use { output ->
                check(image.compress(Bitmap.CompressFormat.JPEG, 86, output)) {
                    "Não foi possível compactar a foto."
                }
            }
            check(draft.length() > 0) { "A foto ficou vazia." }
            backup.delete()
            if (destination.exists() && !destination.renameTo(backup)) {
                error("Não foi possível preservar a imagem anterior.")
            }
            if (!draft.renameTo(destination)) {
                if (backup.isFile) backup.renameTo(destination)
                error("Não foi possível salvar a foto.")
            }
            backup.delete()
        } finally {
            draft.delete()
        }
    }
}
