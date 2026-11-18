package com.example.meutea.menu.jogos

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meutea.ui.TeaGlassPanel
import com.example.meutea.ui.TeaPalette
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val CAMPO = 11

private fun novaComida(corpo: List<Int>, random: Random = Random.Default): Int =
    (0 until CAMPO * CAMPO).filterNot { it in corpo }.randomOrNull(random) ?: -1

private fun proximoIndice(posicao: Int, direcao: String): Int {
    val x = posicao % CAMPO
    val y = posicao / CAMPO
    return when (direcao) {
        "cima" -> if (y == 0) -1 else posicao - CAMPO
        "baixo" -> if (y == CAMPO - 1) -1 else posicao + CAMPO
        "esquerda" -> if (x == 0) -1 else posicao - 1
        "direita" -> if (x == CAMPO - 1) -1 else posicao + 1
        else -> -1
    }
}

internal fun direcaoPermitida(atual: String, nova: String): Boolean =
    nova != when (atual) {
        "cima" -> "baixo"
        "baixo" -> "cima"
        "direita" -> "esquerda"
        else -> "direita"
    }

@Composable
internal fun JogoCobrinha(onBack: () -> Unit) {
    var corpo by remember { mutableStateOf(listOf(60, 59, 58)) }
    var alimento by remember { mutableIntStateOf(25) }
    var direcao by remember { mutableStateOf("direita") }
    var jogando by remember { mutableStateOf(false) }
    var terminou by remember { mutableStateOf(false) }
    var pontos by remember { mutableIntStateOf(0) }
    var ritmo by remember { mutableIntStateOf(460) }

    LaunchedEffect(jogando, corpo, direcao, ritmo) {
        if (!jogando || terminou) return@LaunchedEffect
        delay(ritmo.toLong())
        val novo = proximoIndice(corpo.first(), direcao)
        val comeu = novo == alimento
        val colisao = novo < 0 || novo in if (comeu) corpo else corpo.dropLast(1)
        if (colisao) {
            jogando = false
            terminou = true
        } else {
            val novoCorpo = listOf(novo) + if (comeu) corpo else corpo.dropLast(1)
            corpo = novoCorpo
            if (comeu) {
                pontos++
                alimento = novaComida(novoCorpo)
                if (alimento < 0) {
                    jogando = false
                    terminou = true
                }
            }
        }
    }

    fun reiniciar() {
        corpo = listOf(60,59,58)
        alimento = 25
        direcao = "direita"
        jogando = false
        terminou = false
        pontos = 0
    }

    TelaJogo("Cobrinha", onBack) {
        TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
            Text("Leve a cobrinha até a fruta", color = Color.White,
                fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text("Controle pelo teclado de setas abaixo. Pause quando desejar.",
                color = Color.White)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xF1F1FAFF))
        ) {
            Column(Modifier.padding(12.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                    Text("Frutas: $pontos", fontWeight = FontWeight.Bold, color = TeaPalette.ink)
                    Text(if (terminou) "Fim desta partida" else
                        if (jogando) "Em movimento" else "Pausado",
                        color = TeaPalette.ocean, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(10.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(CAMPO),
                    modifier = Modifier.fillMaxWidth().height(302.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed((0 until CAMPO * CAMPO).toList()) { _, index ->
                        Box(
                            modifier = Modifier.aspectRatio(1f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(when {
                                    index == corpo.first() -> Color(0xFF078C68)
                                    index in corpo -> Color(0xFF62CC93)
                                    index == alimento -> Color(0xFFFFC05D)
                                    else -> Color(0xFFD8EEF1)
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            if (index == alimento) Text("●", color = Color(0xFFDA5754),
                                fontSize = 17.sp)
                            if (index == corpo.first()) Text("●", color = Color.White,
                                fontSize = 15.sp)
                        }
                    }
                }
            }
        }
        if (terminou) {
            TeaGlassPanel(modifier = Modifier.fillMaxWidth()) {
                Text("Você coletou $pontos fruta(s). Quer jogar novamente?",
                    color = TeaPalette.ink)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Button(onClick = { if (direcaoPermitida(direcao,"cima")) direcao = "cima" },
                modifier = Modifier.size(width = 74.dp, height = 52.dp)) { Text("↑", fontSize = 22.sp) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (direcaoPermitida(direcao, "esquerda")) direcao = "esquerda"
                }, modifier = Modifier.size(width = 74.dp, height = 52.dp)) {
                    Text("←", fontSize = 22.sp)
                }
                Button(onClick = { jogando = !jogando && !terminou },
                    enabled = !terminou,
                    modifier = Modifier.size(width = 96.dp, height = 52.dp)) {
                    Text(if (jogando) "Pausa" else "Jogar")
                }
                Button(onClick = {
                    if (direcaoPermitida(direcao, "direita")) direcao = "direita"
                }, modifier = Modifier.size(width = 74.dp, height = 52.dp)) {
                    Text("→", fontSize = 22.sp)
                }
            }
            Button(onClick = { if (direcaoPermitida(direcao,"baixo")) direcao = "baixo" },
                modifier = Modifier.size(width = 74.dp, height = 52.dp)) { Text("↓", fontSize = 22.sp) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = { ritmo = if (ritmo == 460) 650 else 460 }) {
                Text(if (ritmo == 460) "Mais devagar" else "Ritmo normal")
            }
            OutlinedButton(onClick = { reiniciar() }) { Text("Recomeçar") }
        }
    }
}

private val elementos = listOf("⭐", "🍎", "🚲", "🐢", "🎈", "🌻", "🏀", "🐟",
    "🍌", "🚗", "🦋", "🧸", "🍀", "🥕", "🪁", "🐝", "🎵", "🏡", "🍉", "🚀")
private val alvos = listOf("🍎", "🦋", "🚗", "🌻", "🐢", "🎈", "🚀", "🧸")

@Composable
internal fun JogoProcureObjeto(onBack: () -> Unit) {
    var rodada by remember { mutableIntStateOf(0) }
    var encontrou by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf("Observe os desenhos e toque no objeto solicitado.") }
    val alvo = alvos[rodada % alvos.size]
    val sorteados = remember(rodada) {
        elementos.shuffled(Random(rodada + 101)).take(19).let { chosen ->
            (chosen.filterNot { it == alvo } + alvo).shuffled(Random(rodada + 992))
        }
    }

    TelaJogo("Procure o objeto", onBack) {
        TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
            Text("Encontre este objeto", color = Color.White,
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(alvo, fontSize = 42.sp, modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
            Text("Toque na figura igual. Sem cronômetro.",
                color = Color.White, modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
        }
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xF4EBF8FF)),
            shape = RoundedCornerShape(22.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth().height(355.dp).padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                itemsIndexed(sorteados) { _, item ->
                    Box(
                        modifier = Modifier.aspectRatio(1f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(if (encontrou && item == alvo)
                                Color(0xFFB1F4D8) else Color.White)
                            .clickable {
                                if (!encontrou) {
                                    encontrou = item == alvo
                                    aviso = if (encontrou) "Encontrou! Excelente observação."
                                        else "Esse é outro objeto. Você pode procurar de novo."
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item, fontSize = 34.sp)
                    }
                }
            }
        }
        TeaGlassPanel(modifier = Modifier.fillMaxWidth()) {
            Text(aviso, style = MaterialTheme.typography.bodyLarge, color = TeaPalette.ink)
        }
        Button(onClick = {
            rodada++
            encontrou = false
            aviso = "Observe os desenhos e toque no objeto solicitado."
        }, modifier = Modifier.fillMaxWidth(),
            enabled = encontrou) { Text("Próximo objeto") }
        OutlinedButton(onClick = {
            rodada++
            encontrou = false
            aviso = "Nova disposição dos objetos. Continue no seu ritmo."
        }, modifier = Modifier.fillMaxWidth()) { Text("Trocar atividade") }
    }
}
