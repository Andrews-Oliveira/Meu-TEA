package com.example.meutea.menu.jogos

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class JogosV3EngineTest {
    @Test fun puzzlesSaoPermutacoesValidasDeQuatroTamanhos() {
        (3..6).forEach{size->
            repeat(20){seed->
                val board=gerarPuzzle(size,Random(seed))
                assertEquals(size*size,board.size)
                assertEquals((0 until size*size).toList(),board.sorted())
                assertNotEquals((1 until size*size).toList()+listOf(0),board)
                // Todos os puzzles foram embaralhados por movimentos legais.
            }
        }
    }

    @Test fun cacapalavrasNuncaExibePalavraInexistente() {
        listOf("Inicial","Intermediário","Avançado").forEach{level->
            temasPalavras.forEach{theme->
                val game=gerarCacaPalavras(level,theme,1234)
                assertEquals(game.tamanho*game.tamanho,game.letras.size)
                assertTrue(game.respostas.size>=3)
                game.respostas.forEach{(word,path)->
                    assertEquals(word.length,path.size)
                    assertEquals(word,path.joinToString(""){game.letras[it].toString()})
                    assertEquals(word,descobrirPalavra(path,game.respostas))
                    assertEquals(word,descobrirPalavra(path.reversed(),game.respostas))
                    path.zipWithNext().forEach{(a,b)->
                        assertTrue(kotlin.math.abs(a%game.tamanho-b%game.tamanho)<=1)
                        assertTrue(kotlin.math.abs(a/game.tamanho-b/game.tamanho)<=1)
                    }
                }
            }
        }
    }

    @Test fun matematicaGera100QuestoesValidasEmTodasAsOperacoes() {
        listOf("Todos","+","−","×","÷").forEach{operator->
            val questions=gerarMatematica("Avançado",100,null,operator,2026)
            assertEquals(100,questions.size)
            assertEquals(100,questions.map{it.enunciado}.toSet().size)
            questions.forEach {q->
                assertEquals(4,q.opcoes.distinct().size)
                assertTrue(q.correta in q.opcoes.indices)
                assertTrue(q.opcoes[q.correta].toInt()>=0)
                assertFalse(q.explicacao.isBlank())
            }
        }
    }

    @Test fun tabuadaEscolheFatorDeUmANove() {
        (1..9).forEach{number->
            val questions=gerarMatematica("Inicial",50,number,"×",number)
            assertEquals(50,questions.size)
            assertTrue(questions.all{it.enunciado.contains(number.toString()+" × ")})
        }
    }

    @Test fun portuguesTemAteCemQuestoesComOpcoesDistintas() {
        listOf("Inicial","Intermediário","Avançado").forEach{level->
            val questions=gerarPortugues(level,100,1457)
            assertEquals(100,questions.size)
            questions.forEach {q->
                assertEquals(3,q.opcoes.distinct().size)
                assertTrue(q.correta in q.opcoes.indices)
                assertTrue(q.explicacao.isNotBlank())
            }
        }
    }

    @Test fun logicaGeraCemQuestoesComRespostasValidas() {
        listOf("Inicial","Intermediário","Avançado").forEach{level->
            val questions=gerarLogica(level,100,2026)
            assertEquals(100,questions.size)
            assertEquals(100,questions.map{it.enunciado}.toSet().size)
            questions.forEach {
                assertTrue(it.correta in it.opcoes.indices)
                assertNotEquals("",it.opcoes[it.correta])
            }
        }
    }

    @Test fun sequenciasSoAceitamCelulasAdjacentesNaMesmaDirecao() {
        assertTrue(caminhoValido(emptyList(),0,6))
        assertTrue(caminhoValido(listOf(0),1,6))
        assertTrue(caminhoValido(listOf(0,1),2,6))
        assertFalse(caminhoValido(listOf(0,1),8,6))
        assertFalse(caminhoValido(listOf(5),6,6))
    }

    @Test fun cobrinhaNaoPermiteReverterDirecaoEmUmPasso() {
        assertFalse(direcaoPermitida("esquerda","direita"))
        assertFalse(direcaoPermitida("cima","baixo"))
        assertTrue(direcaoPermitida("cima","direita"))
    }
}
