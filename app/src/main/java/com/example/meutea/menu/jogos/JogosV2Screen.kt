package com.example.meutea.menu.jogos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.meutea.ui.TeaGlassPanel
import com.example.meutea.ui.TeaPalette
import com.example.meutea.ui.TeaWallpaper
import com.example.meutea.ui.rememberTeaTheme

private data class JogoItem(
    val id: String,
    val titulo: String,
    val descricao: String,
    val icone: String,
    val cores: List<Color>
)

private val catalogo = listOf(
    JogoItem("quebra", "Quebra-cabeça", "Reconstrua uma figura", "🧩",
        listOf(Color(0xFF188AC8), Color(0xFF35C0DB))),
    JogoItem("palavras", "Caça-palavras", "Encontre palavras na grade", "🔎",
        listOf(Color(0xFF13A992), Color(0xFF4BCEAD))),
    JogoItem("cobra", "Cobrinha", "Mova e colete as frutas", "🐍",
        listOf(Color(0xFF559D49), Color(0xFF95CF5F))),
    JogoItem("objetos", "Procure o objeto", "Localize o desenho solicitado", "🔭",
        listOf(Color(0xFFDB8C22), Color(0xFFF0C35D))),
    JogoItem("matematica", "Matemática", "Desafios com números", "✚",
        listOf(Color(0xFF8B6BDF), Color(0xFFC09BEE))),
    JogoItem("portugues", "Português", "Leitura e palavras", "📚",
        listOf(Color(0xFFCA598E), Color(0xFFF09CB9))),
    JogoItem("logica", "Lógica", "Padrões e sequências", "🧠",
        listOf(Color(0xFF387ACB), Color(0xFF56B8EF))),
    JogoItem("emocoes", "Emoções", "Descubra expressões", "🙂",
        listOf(Color(0xFFCF8255), Color(0xFFF2B278))),
    JogoItem("pausa", "Pausa tranquila", "Uma atividade sem pressa", "🌿",
        listOf(Color(0xFF168D83), Color(0xFF5FC6A6)))
)

@Composable
fun JogosV2Screen(navController: NavHostController) {
    var game by rememberSaveable { mutableStateOf("") }
    when (game) {
        "quebra" -> JogoQuebraCabeca { game = "" }
        "palavras" -> JogoCacaPalavras { game = "" }
        "cobra" -> JogoCobrinha { game = "" }
        "objetos" -> JogoProcureObjeto { game = "" }
        "matematica" -> JogoPerguntas("Matemática", "matematica") { game = "" }
        "portugues" -> JogoPerguntas("Português", "portugues") { game = "" }
        "logica" -> JogoPerguntas("Lógica", "logica") { game = "" }
        "emocoes" -> JogoEmocoes { game = "" }
        "pausa" -> JogoPausa { game = "" }
        else -> JogosCatalogo(onBack = { navController.popBackStack() }, onSelect = { game = it })
    }
}

/**
 * Um único invólucro visual para todos os jogos. Cada modo possui controles
 * grandes, orientação curta, opção de voltar e nenhuma publicidade.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TelaJogo(
    titulo: String,
    onBack: () -> Unit,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val theme = rememberTeaTheme()
    Box(Modifier.fillMaxSize()) {
        TeaWallpaper(theme, modifier = Modifier.fillMaxSize(), tint = 0.12f)
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(titulo, fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    navigationIcon = {
                        TextButton(onClick = onBack) { Text("‹ Voltar", color = Color.White) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xE009386D)
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        }
    }
}

@Composable
private fun JogosCatalogo(onBack: () -> Unit, onSelect: (String) -> Unit) {
    val theme = rememberTeaTheme()
    Box(Modifier.fillMaxSize()) {
        TeaWallpaper(theme, modifier = Modifier.fillMaxSize(), tint = 0.12f)
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(Color(0xE009386D))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("‹ Voltar", color = Color.White) }
                Text("Jogos e descobertas",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold)
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp),
                contentPadding = PaddingValues(15.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
                        Text("Escolha o que deseja explorar",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(5.dp))
                        Text("Sem anúncios, sem pressão e com liberdade para parar.",
                            color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                items(catalogo, key = { it.id }) { item ->
                    Card(
                        onClick = { onSelect(item.id) },
                        shape = RoundedCornerShape(23.dp),
                        border = BorderStroke(1.5.dp, Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xF2F6FCFF))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier.fillMaxWidth().height(92.dp)
                                    .clip(RoundedCornerShape(17.dp))
                                    .background(Brush.linearGradient(item.cores)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.icone, fontSize = 44.sp)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(item.titulo,
                                color = TeaPalette.ink, fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center)
                            Text(item.descricao,
                                color = Color(0xFF375775),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 5.dp))
                        }
                    }
                }
            }
        }
    }
}
