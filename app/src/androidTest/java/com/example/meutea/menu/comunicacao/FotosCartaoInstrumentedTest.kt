package com.example.meutea.menu.comunicacao

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.FileProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/**
 * Testa a cópia da imagem para o armazenamento privado e o isolamento por UID.
 * Requer execução em emulador/aparelho: :app:connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class FotosCartaoInstrumentedTest {
    @Test
    fun fotoDaCameraPersisteApenasNoPerfilDono() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pasta = File(context.cacheDir, "tea_camera").apply { mkdirs() }
        val arquivo = File(pasta, "teste-${UUID.randomUUID()}.jpg")
        val imagem = Bitmap.createBitmap(160, 120, Bitmap.Config.ARGB_8888)
        imagem.eraseColor(Color.rgb(30, 160, 190))
        arquivo.outputStream().use {
            assertTrue(imagem.compress(Bitmap.CompressFormat.JPEG, 90, it))
        }
        val uri = FileProvider.getUriForFile(context,
            "${context.packageName}.fileprovider", arquivo)
        val uid = UUID.randomUUID().toString()
        val id = "cartao-exemplo"
        val dona = FotosCartao(context, uid)
        val outraConta = FotosCartao(context, "outra-" + uid)
        try {
            assertTrue(dona.salvar(id, uri).isSuccess)
            assertTrue(dona.existe(id))
            assertNotNull(dona.lerSalva(id))
            assertFalse(outraConta.existe(id))
            assertTrue(dona.remover(id))
            assertFalse(dona.existe(id))
        } finally {
            dona.remover(id)
            arquivo.delete()
        }
    }
}
