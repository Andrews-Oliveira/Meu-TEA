package com.example.meutea.menu.jogos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.meutea.R
import com.example.meutea.ui.TeaGlassPanel
import com.example.meutea.ui.TeaPalette
import kotlin.math.abs
import kotlin.random.Random

internal fun vizinhosDe(posicao: Int, size: Int): List<Int> {
    val x = posicao % size
    val y = posicao / size
    return listOfNotNull(
        if (x > 0) posicao - 1 else null,
        if (x < size - 1) posicao + 1 else null,
        if (y > 0) posicao - size else null,
        if (y < size - 1) posicao + size else null
    )
}

internal fun novoQuebraCabeca(random: Random = Random.Default): List<Int> {
    val puzzle = (1..8).toMutableList().apply { add(0) }
    var anterior = -1
    repeat(75) {
        val zero = puzzle.indexOf(0)
        val opcoes = vizinhosDe(zero, 3).filter { it != anterior }
        val destino = (if (opcoes.isEmpty()) vizinhosDe(zero, 3) else opcoes).random(random)
        puzzle[zero] = puzzle[destino]
        puzzle[destino] = 0
        anterior = zero
    }
    if (puzzle == listOf(1,2,3,4,5,6,7,8,0)) {
        val zero = puzzle.indexOf(0)
        val next = vizinhosDe(zero,3).first()
        puzzle[zero] = puzzle[next]
        puzzle[next] = 0
    }
    return puzzle
}

/** Reconstrua uma figura original trocando peças vizinhas ao espaço vazio. */
@Composable
internal fun JogoQuebraCabeca(onBack: () -> Unit) {
    val board = remember { mutableStateListOf<Int>().apply { addAll(novoQuebraCabeca()) } }
    var movimentos by remember { mutableIntStateOf(0) }
    val pronto = board.toList() == listOf(1,2,3,4,5,6,7,8,0)
    val context = LocalContext.current
    val artwork = remember(context) { ContextCompat.getDrawable(context, R.drawable.tea_pic_feliz)?.mutate() }

    TelaJogo("Quebra-cabeça", onBack) {
        TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
            Text("Reorganize a imagem", color = Color.White,
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Toque numa peça ao lado do espaço vazio. Pode tentar quantas vezes quiser.",
                color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xF0E8F6FF)),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(if (pronto) "Imagem completa!" else "Movimentos: $movimentos",
                    color = TeaPalette.ink, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth().height(310.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(board.toList()) { pos, num ->
                        val movel = num != 0 && vizinhosDe(board.indexOf(0), 3).contains(pos)
                        Box(
                            modifier = Modifier.aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (num == 0) Color(0xFFC4DDEB) else Color(0xFF64C2D9))
                                .clickable(enabled = movel && !pronto) {
                                    val vazio = board.indexOf(0)
                                    board[vazio] = num
                                    board[pos] = 0
                                    movimentos++
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (num > 0 && artwork != null) {
                                val index = num - 1
                                Canvas(Modifier.fillMaxWidth().aspectRatio(1f)) {
                                    drawIntoCanvas { canvas ->
                                        val native = canvas.nativeCanvas
                                        native.save()
                                        native.clipRect(0f, 0f, size.width, size.height)
                                        val width = size.width.toInt()
                                        val height = size.height.toInt()
                                        val col = index % 3
                                        val row = index / 3
                                        artwork.setBounds(
                                            -col * width, -row * height,
                                            (3 - col) * width, (3 - row) * height
                                        )
                                        artwork.draw(native)
                                        native.restore()
                                    }
                                }
                            }
                            if (num > 0) {
                                Text(num.toString(),
                                    modifier = Modifier.align(Alignment.TopStart).padding(6.dp),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0A396C))
                            }
                        }
                    }
                }
            }
        }
        if (pronto) {
            TeaGlassPanel(modifier = Modifier.fillMaxWidth()) {
                Text("Você conseguiu! Pode jogar novamente ou explorar outro jogo.",
                    color = TeaPalette.ink, style = MaterialTheme.typography.titleMedium)
            }
        }
        Button(onClick = {
            board.clear()
            board.addAll(novoQuebraCabeca())
            movimentos = 0
        }, modifier = Modifier.fillMaxWidth()) { Text("Embaralhar e jogar novamente") }
    }
}

private val letras = listOf(
    "CASAXLUA",
    "PSQWERTY",
    "MORPABCD",
    "KLXYZFGH",
    "PAZKLMNO",
    "QRDTUVWX",
    "LMNAPQRS",
    "ZQPCAVBN"
)
private val palavras = listOf("CASA", "LUA", "SOL", "PAZ")

internal fun caminhoValido(selecionados: List<Int>, novo: Int, tamanho: Int = 8): Boolean {
    if (novo !in 0 until tamanho * tamanho || selecionados.contains(novo)) return false
    if (selecionados.isEmpty()) return true
    val anterior = selecionados.last()
    val dx = novo % tamanho - anterior % tamanho
    val dy = novo / tamanho - anterior / tamanho
    if (maxOf(abs(dx), abs(dy)) != 1) return false
    if (selecionados.size == 1) return true
    val primeiro = selecionados[0]
    val segundo = selecionados[1]
    return dx == segundo % tamanho - primeiro % tamanho &&
        dy == segundo / tamanho - primeiro / tamanho
}

@Composable
internal fun JogoCacaPalavras(onBack: () -> Unit) {
    val letrasGrid = remember { letras.joinToString("").toList() }
    val marcadas = remember { mutableStateListOf<Int>() }
    val encontradas = remember { mutableStateMapOf<String, List<Int>>() }
    var feedback by remember { androidx.compose.runtime.mutableStateOf("Selecione as letras em sequência.") }
    TelaJogo("Caça-palavras", onBack) {
        TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
            Text("Encontre as palavras", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = Color.White)
            Text("Toque nas letras seguidas em linha reta. Horizontal e vertical funcionam.",
                color = Color.White)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xF5E9F8FF))
        ) {
            Column(Modifier.padding(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    palavras.forEach { palavra ->
                        Text(if (encontradas.containsKey(palavra)) "✓ $palavra" else palavra,
                            color = if (encontradas.containsKey(palavra)) Color(0xFF087E70)
                                else TeaPalette.ink,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center)
                    }
                }
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    modifier = Modifier.fillMaxWidth().height(328.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(letrasGrid) { index, letra ->
                        val found = encontradas.values.any { index in it }
                        Box(
                            modifier = Modifier.aspectRatio(1f)
                                .clip(RoundedCornerShape(7.dp))
                                .background(when {
                                    found -> Color(0xFF7CE6C5)
                                    index in marcadas -> Color(0xFFFFCF64)
                                    else -> Color.White
                                })
                                .clickable {
                                    if (!caminhoValido(marcadas, index)) marcadas.clear()
                                    if (caminhoValido(marcadas, index)) marcadas.add(index)
                                    val tentativa = marcadas.joinToString("") { letrasGrid[it].toString() }
                                    val palavra = palavras.firstOrNull {
                                        !encontradas.containsKey(it) &&
                                            (tentativa == it || tentativa.reversed() == it)
                                    }
                                    if (palavra != null) {
                                        encontradas[palavra] = marcadas.toList()
                                        feedback = "Você encontrou $palavra!"
                                        marcadas.clear()
                                    } else if (tentativa.length >= 8) {
                                        marcadas.clear()
                                        feedback = "Tente outra sequência, sem pressa."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(letra.toString(),
                                color = TeaPalette.ink,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
        TeaGlassPanel(modifier = Modifier.fillMaxWidth()) {
            Text(if (encontradas.size == palavras.size) "Todas as palavras foram encontradas!"
                else feedback, color = TeaPalette.ink,
                style = MaterialTheme.typography.bodyLarge)
            Text("${encontradas.size} de ${palavras.size} palavras",
                color = TeaPalette.ocean, fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { marcadas.clear() },
                modifier = Modifier.weight(1f)) { Text("Limpar seleção") }
            Button(onClick = {
                marcadas.clear()
                encontradas.clear()
                feedback = "Selecione as letras em sequência."
            }, modifier = Modifier.weight(1f)) { Text("Reiniciar") }
        }
    }
}
