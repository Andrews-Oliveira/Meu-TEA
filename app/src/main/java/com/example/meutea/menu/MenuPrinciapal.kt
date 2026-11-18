package com.example.meutea.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.R
import com.example.meutea.ui.TeaGlassPanel
import com.example.meutea.ui.TeaPalette
import com.example.meutea.ui.TeaWallpaper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private data class HomeAction(
    val title: String,
    val subtitle: String,
    val drawable: Int,
    val palette: List<Color>,
    val destination: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalScreen(navController: NavController, usuarioId: String) {
    val auth = FirebaseAuth.getInstance()
    val firestore = remember { FirebaseFirestore.getInstance() }
    var loadingCarteira by remember { mutableStateOf(false) }
    var carteiraError by remember { mutableStateOf<String?>(null) }
    var temaVisual by remember(usuarioId) { mutableStateOf("masculino") }
    var mostrarTema by remember { mutableStateOf(false) }
    var salvandoTema by remember { mutableStateOf(false) }
    var erroTema by remember { mutableStateOf<String?>(null) }

    DisposableEffect(usuarioId) {
        if (auth.currentUser?.uid != usuarioId) {
            onDispose { }
        } else {
            val listener = firestore.collection("usuarios").document(usuarioId)
                .addSnapshotListener { document, error ->
                    if (error == null && document != null) {
                        temaVisual = if (document.getString("tema_visual") == "feminino") "feminino"
                            else "masculino"
                    }
                }
            onDispose { listener.remove() }
        }
    }

    LaunchedEffect(usuarioId) {
        if (auth.currentUser?.uid != usuarioId) {
            navController.navigate("loginScreen") {
                popUpTo(navController.graph.id) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    val firstName = auth.currentUser?.displayName?.trim()?.split(" ")?.firstOrNull()
        ?.takeIf { it.isNotBlank() } ?: "você"
    val actions = remember {
        listOf(
            HomeAction("Comunicar", "Dizer o que sente, pensa e precisa",
                R.drawable.tea_action_chat,
                listOf(Color(0xFF00B6E8), Color(0xFF0766C9)), "comunicacaoV2"),
            HomeAction("Minha rotina", "Manhã, tarde e noite no seu ritmo",
                R.drawable.tea_action_calendar,
                listOf(Color(0xFF17C4B5), Color(0xFF007D8D)), "RotinasScreen"),
            HomeAction("Jogos tranquilos", "Explorar emoções e fazer escolhas",
                R.drawable.tea_action_game,
                listOf(Color(0xFF9C86FA), Color(0xFF6359DD)), "jogosV2"),
            HomeAction("CIPTEA oficial", "Saiba como solicitar seu documento",
                R.drawable.tea_action_cert,
                listOf(Color(0xFF169CFA), Color(0xFF125AB1)), "cipteaOrientacao")
        )
    }

    Box(Modifier.fillMaxSize()) {
        TeaWallpaper(temaVisual = temaVisual, modifier = Modifier.fillMaxSize())
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Início", color = Color.White, fontWeight = FontWeight.Bold) },
                    actions = {
                        TextButton(onClick = { mostrarTema = true }) {
                            Text("Tema", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                        TextButton(onClick = {
                            auth.signOut()
                            navController.navigate("loginScreen") {
                                popUpTo(navController.graph.id) { inclusive = false }
                                launchSingleTop = true
                            }
                        }) { Text("Sair", color = Color.White, fontWeight = FontWeight.SemiBold) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xD90A396C)
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 17.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                // Deixa a ilustração respirar: nome não fica preso no topo do desenho.
                item { Spacer(Modifier.height(128.dp)) }
                item {
                    TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
                        Text("Olá, $firstName!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Spacer(Modifier.height(5.dp))
                        Text("Seu jeito de se comunicar importa. Escolha o que deseja fazer.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White)
                    }
                }
                items(actions) { action ->
                    HomeActionCard(action) { navController.navigate(action.destination) }
                }
                item {
                    HomeActionCard(
                        HomeAction(
                            "Minha identificação",
                            if (loadingCarteira) "Carregando identificação..."
                            else "Cartão pessoal — não é documento oficial",
                            R.drawable.tea_action_id,
                            listOf(Color(0xFF12CEC1), Color(0xFF087F98)), ""
                        )
                    ) {
                        if (loadingCarteira || auth.currentUser?.uid != usuarioId) return@HomeActionCard
                        loadingCarteira = true
                        carteiraError = null
                        firestore.collection("usuarios").document(usuarioId).get()
                            .addOnSuccessListener { doc ->
                                val route = if (doc.getBoolean("carteira_digital") == true) {
                                    "carteirinhaViewScreen/"
                                } else "carteirinhaScreen/"
                                navController.navigate(route + usuarioId)
                            }
                            .addOnFailureListener {
                                carteiraError = "Não foi possível carregar a identificação."
                            }
                            .addOnCompleteListener { loadingCarteira = false }
                    }
                    carteiraError?.let {
                        Text(it, color = Color.White, modifier = Modifier.padding(6.dp))
                    }
                }
                item {
                    TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
                        Text("Aqui você é ouvido, respeitado e acolhido.",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyLarge)
                        Text("Cada atividade pode ser adaptada ao seu tempo.",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }

    if (mostrarTema) {
        AlertDialog(
            onDismissRequest = { if (!salvandoTema) mostrarTema = false },
            title = { Text("Qual ilustração você prefere?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("A aparência é uma preferência pessoal. Troque quando quiser.")
                    listOf("masculino" to "Com menino", "feminino" to "Com menina")
                        .forEach { (id, description) ->
                            Card(
                                onClick = {
                                    if (salvandoTema || auth.currentUser?.uid != usuarioId) return@Card
                                    val anterior = temaVisual
                                    temaVisual = id  // mudança imediata e visível
                                    salvandoTema = true
                                    firestore.collection("usuarios").document(usuarioId)
                                        .update("tema_visual", id)
                                        .addOnSuccessListener { mostrarTema = false }
                                        .addOnFailureListener {
                                            temaVisual = anterior
                                            erroTema = "Não foi possível salvar o tema. Confira a conexão."
                                        }
                                        .addOnCompleteListener { salvandoTema = false }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (temaVisual == id)
                                        TeaPalette.mint else Color(0xFFF1F8FC)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(if (id == "masculino") "🌿" else "🌸", fontSize = 25.sp)
                                    Spacer(Modifier.size(12.dp))
                                    Text(description, fontWeight = FontWeight.Bold,
                                        color = TeaPalette.ink)
                                    Spacer(Modifier.weight(1f))
                                    if (temaVisual == id) Text("✓", color = TeaPalette.ocean)
                                }
                            }
                        }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarTema = false }, enabled = !salvandoTema) {
                    Text(if (salvandoTema) "Salvando..." else "Fechar")
                }
            }
        )
    }
    erroTema?.let { message ->
        AlertDialog(
            onDismissRequest = { erroTema = null },
            title = { Text("Erro ao alterar tema") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { erroTema = null }) { Text("OK") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeActionCard(action: HomeAction, onClick: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color(0xF1E8F6FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(62.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(Brush.linearGradient(action.palette)),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(action.drawable), contentDescription = null,
                    modifier = Modifier.size(40.dp))
            }
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(action.title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold, color = TeaPalette.ink)
                Spacer(Modifier.height(3.dp))
                Text(action.subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF305175))
            }
            Text("›", fontSize = 30.sp, color = TeaPalette.ocean)
        }
    }
}
