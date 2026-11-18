package com.example.meutea.menu.jogos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class JogosLogicaTest {
    @Test
    fun quebraCabecaNuncaSorteiaPeçasDuplicadas() {
        repeat(50) { seed ->
            val board = novoQuebraCabeca(Random(seed))
            assertEquals((0..8).toList(), board.sorted())
            assertNotEquals(listOf(1,2,3,4,5,6,7,8,0), board)
        }
    }

    @Test
    fun vizinhosRespeitamBordasDoQuebraCabeca() {
        assertEquals(setOf(1, 3), vizinhosDe(0, 3).toSet())
        assertEquals(setOf(1, 3, 5, 7), vizinhosDe(4, 3).toSet())
        assertEquals(setOf(5, 7), vizinhosDe(8, 3).toSet())
    }

    @Test
    fun cacaPalavrasAceitaAdjacentesMasNaoSaltos() {
        assertTrue(caminhoValido(emptyList(), 0))
        assertTrue(caminhoValido(listOf(0), 1))
        assertTrue(caminhoValido(listOf(0, 1), 2))
        assertFalse(caminhoValido(listOf(0, 1), 9))
        assertFalse(caminhoValido(listOf(0, 1), 0))
        assertFalse(caminhoValido(listOf(7), 8))
        assertFalse(caminhoValido(listOf(0), 16))
    }

    @Test
    fun cobrinhaNaoPermiteVirar180Graus() {
        assertFalse(direcaoPermitida("cima", "baixo"))
        assertFalse(direcaoPermitida("esquerda", "direita"))
        assertTrue(direcaoPermitida("cima", "esquerda"))
        assertTrue(direcaoPermitida("direita", "baixo"))
    }
}
