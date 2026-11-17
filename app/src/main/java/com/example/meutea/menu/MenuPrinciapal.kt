package com.example.meutea.menu

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private data class HomeAction(
    val title: String,
    val subtitle: String,
    val symbol: String,
    val color: Color,
    val destination: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalScreen(navController: NavController, usuarioId: String) {
    val auth = FirebaseAuth.getInstance()
    var loadingCarteira by remember { mutableStateOf(false) }
    var carteiraError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(usuarioId) {
        if (auth.currentUser?.uid != usuarioId) {
            navController.navigate("loginScreen") {
                popUpTo("welcomeScreen") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val firstName = auth.currentUser?.displayName?.trim()?.split(" ")?.firstOrNull()
        ?.takeIf { it.isNotBlank() } ?: "você"

    val actions = listOf(
        HomeAction("Comunicar", "Dizer o que sente, pensa e precisa", "◉", Color(0xFFD9F2EE), "comunicacaoV2"),
        HomeAction("Minha rotina", "Manhã, tarde e noite no seu ritmo", "☀", Color(0xFFFFEBCF), "RotinasScreen"),
        HomeAction("Jogos tranquilos", "Explorar emoções e fazer escolhas", "◇", Color(0xFFECE4FB), "jogosV2"),
        HomeAction("CIPTEA oficial", "Saiba como solicitar seu documento", "▣", Color(0xFFDFEAF9), "cipteaOrientacao")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu TEA", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = {
                        auth.signOut()
                        navController.navigate("loginScreen") {
                            popUpTo("welcomeScreen") { inclusive = true }
                            launchSingleTop = true
                        }
                    }) { Text("Sair") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text(
                        "Olá, " + firstName + "!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Seu jeito de se comunicar importa. Escolha o que deseja fazer.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(actions) { action ->
                HomeActionCard(action) {
                    navController.navigate(action.destination)
                }
            }
            item {
                Card(
                    onClick = {
                        if (loadingCarteira || auth.currentUser?.uid != usuarioId) return@Card
                        loadingCarteira = true
                        carteiraError = null
                        FirebaseFirestore.getInstance().collection("usuarios").document(usuarioId).get()
                            .addOnSuccessListener { doc ->
                                val route = if (doc.getBoolean("carteira_digital") == true) {
                                    "carteirinhaViewScreen/"
                                } else {
                                    "carteirinhaScreen/"
                                }
                                navController.navigate(route + usuarioId)
                            }
                            .addOnFailureListener {
                                carteiraError = "Não foi possível abrir sua identificação. Verifique a conexão."
                            }
                            .addOnCompleteListener { loadingCarteira = false }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(54.dp).background(
                                MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)
                            ),
                            contentAlignment = Alignment.Center
                        ) { Text("ID", fontSize = 21.sp, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.size(14.dp))
                        Column {
                            Text("Minha identificação", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                if (loadingCarteira) "Carregando..." else "Cartão pessoal — não é documento oficial",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                carteiraError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Todas as pessoas merecem ser ouvidas. Ajuste as atividades às suas preferências e necessidades.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeActionCard(action: HomeAction, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(60.dp).background(
                    action.color, RoundedCornerShape(18.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(action.symbol, fontSize = 30.sp, color = Color(0xFF254B50))
            }
            Spacer(Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(action.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(3.dp))
                Text(action.subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", fontSize = 30.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
