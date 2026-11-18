package com.example.meutea.menu.careiradigital

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

/**
 * Foto opcional em armazenamento PRIVADO, separada do documento Firestore.
 * Não solicita acesso amplo à galeria; somente a imagem escolhida via seletor.
 * Faz nova codificação para remover metadados da imagem original.
 */
internal object FotoIdentificacao {
    private fun pasta(context: Context, uid: String): File {
        require(uid.isNotBlank() && uid.matches(Regex("[A-Za-z0-9_-]+"))) {
            "Identificador inválido"
        }
        return File(context.filesDir, "identificacao_pessoal/" + uid)
    }

    fun salvar(context: Context, uid: String, uri: Uri): Boolean = runCatching {
        val resolutor = context.contentResolver
        val config = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolutor.openInputStream(uri).use { stream ->
            requireNotNull(stream) { "Não foi possível ler a imagem." }
            BitmapFactory.decodeStream(stream, null, config)
        }
        require(config.outWidth > 0 && config.outHeight > 0) { "Imagem inválida." }

        var sample = 1
        while (config.outWidth / sample > 1200 || config.outHeight / sample > 1200) {
            sample *= 2
        }
        val bitmap = resolutor.openInputStream(uri).use { stream ->
            requireNotNull(stream) { "Imagem indisponível." }
            BitmapFactory.decodeStream(stream, null,
                BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: error("Não foi possível abrir a fotografia.")

        val dir = pasta(context, uid).apply { mkdirs() }
        val temp = File(dir, "retrato.tmp")
        try {
            temp.outputStream().use { output ->
                check(bitmap.compress(Bitmap.CompressFormat.JPEG, 88, output))
            }
            val destination = File(dir, "retrato.jpg")
            if (destination.exists()) check(destination.delete())
            check(temp.renameTo(destination))
        } finally {
            bitmap.recycle()
            if (temp.exists()) temp.delete()
        }
        true
    }.getOrDefault(false)

    fun ler(context: Context, uid: String): Bitmap? = runCatching {
        BitmapFactory.decodeFile(File(pasta(context, uid), "retrato.jpg").absolutePath)
    }.getOrNull()

    fun excluir(context: Context, uid: String) {
        runCatching { pasta(context, uid).deleteRecursively() }
    }
}
