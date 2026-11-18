package com.example.meutea.menu.careiradigital

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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

        // A orientação EXIF deve ser aplicada ANTES da recodificação, já que
        // os metadados da fotografia original não são copiados.
        val orientation = runCatching {
            resolutor.openInputStream(uri).use { stream ->
                if (stream == null) ExifInterface.ORIENTATION_NORMAL
                else ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }
        }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)
        val angle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val oriented = if (angle != 0f) Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height,
            Matrix().apply { postRotate(angle) }, true
        ) else bitmap

        val dir = pasta(context, uid).apply { mkdirs() }
        val temp = File(dir, "retrato.tmp")
        try {
            temp.outputStream().use { output ->
                check(oriented.compress(Bitmap.CompressFormat.JPEG, 88, output))
            }
            val destination = File(dir, "retrato.jpg")
            if (destination.exists()) check(destination.delete())
            check(temp.renameTo(destination))
        } finally {
            if (oriented !== bitmap) oriented.recycle()
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
