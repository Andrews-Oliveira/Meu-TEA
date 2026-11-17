package com.example.meutea.menu.jogos

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import com.example.meutea.ui.TeaWallpaper
import com.example.meutea.ui.TeaPalette
import com.example.meutea.ui.TeaGlassPanel
import com.example.meutea.ui.rememberTeaTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.meutea.R

private data class Desafio(
    val figura: Int,
    val texto: String,
    val resposta: String,
    val opcoes: List<String>
)

// Atividade lúdica: não diagnostica nem avalia desenvolvimento.
private val desafios = listOf(
    Desafio(R.drawable.tea_pic_feliz, "Observe a expressão", "Feliz", listOf("Feliz", "Triste", "Com medo")),
    Desafio(R.drawable.tea_pic_triste, "Observe a expressão", "Triste", listOf("Feliz", "Triste", "Com raiva")),
    Desafio(R.drawable.tea_medo, "Observe a expressão", "Com medo", listOf("Com medo", "Feliz", "Triste")),
    Desafio(R.drawable.tea_raiva, "Observe a expressão", "Com raiva", listOf("Feliz", "Com medo", "Com raiva"))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JogosV2Screen(navController: NavHostController) {
    var modo by remember { mutableStateOf("Emoções") }
    var indice by remember(modo) { mutableIntStateOf(0) }
    var escolha by remember(modo, indice) { mutableStateOf<String?>(null) }
    var etapa by remember(modo) { mutableIntStateOf(0) }
    val desafio = desafios[indice]
    val temaVisual = rememberTeaTheme()

    Box(Modifier.fillMaxSize()) {
        TeaWallpaper(temaVisual = temaVisual, modifier = Modifier.fillMaxSize(), tint = 0.04f)
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Jogos tranquilos", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("‹ Voltar", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xDD0A396C)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 90.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
                    Text("Explorar sem pressa",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White)
                    Text(
                        "Aqui não há pontos nem penalidades. Você pode parar a qualquer momento.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Emoções", "Pausa").forEach { tab ->
                        FilterChip(selected = modo == tab, onClick = { modo = tab },
                            label = { Text(tab) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xE7F7FDFF),
                                selectedContainerColor = Color(0xFF00A5B3),
                                selectedLabelColor = Color.White,
                                labelColor = TeaPalette.ink
                            ))
                    }
                }
            }
            if (modo == "Emoções") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xF4EAF8FF)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Cartão " + (indice + 1) + " de " + desafios.size,
                                style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(12.dp))
                            Image(
                                painter = painterResource(desafio.figura),
                                contentDescription = "Desenho de uma expressão facial",
                                modifier = Modifier.size(190.dp)
                            )
                            Spacer(Modifier.height(14.dp))
                            Text("Qual palavra combina com a expressão?",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center)
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        desafio.opcoes.forEach { opcao ->
                            OutlinedButton(
                                onClick = { escolha = opcao },
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                border = BorderStroke(1.5.dp, Color.White),
                                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (escolha == opcao)
                                        Color(0xFF00A5B3) else Color(0xF2F3FBFF),
                                    contentColor = if (escolha == opcao)
                                        Color.White else TeaPalette.ink
                                )
                            ) { Text(opcao) }
                        }
                    }
                }
                if (escolha != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xF1E4F0FE)
                            )
                        ) {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(
                                    if (escolha == desafio.resposta)
                                        "Essa é uma forma de reconhecer a expressão!"
                                    else "Uma interpretação possível deste desenho é: " + desafio.resposta + ".",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    "Pessoas podem expressar sentimentos de maneiras diferentes.",
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                    item {
                        Button(onClick = {
                            indice = (indice + 1) % desafios.size
                            escolha = null
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text(if (indice == desafios.lastIndex) "Recomeçar" else "Próximo cartão")
                        }
                    }
                }
            } else {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Pausa voluntária", style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(18.dp))
                            val mensagens = listOf(
                                "Encontre um lugar onde você se sinta confortável.",
                                "Se for agradável, observe a respiração sem forçar.",
                                "Você pode relaxar as mãos e os ombros.",
                                "Pronto. Volte quando quiser."
                            )
                            Text(mensagens[etapa], textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(22.dp))
                            Button(onClick = { etapa = (etapa + 1) % mensagens.size }) {
                                Text(if (etapa == mensagens.lastIndex) "Recomeçar" else "Próximo passo")
                            }
                        }
                    }
                }
                item {
                    Text(
                        "Se essa atividade causar desconforto, encerre-a. Não substitui acompanhamento profissional.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    }

}
