package com.example.meutea.menu.conversa

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meutea.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversaScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController) }, // ✅ Agora usa um TopBar mais elegante
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 🔹 Fundo com imagem borrada
            Image(
                painter = painterResource(id = R.drawable.imghome_background),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(10.dp),
                contentScale = ContentScale.Crop
            )

            // 🔹 Overlay escuro para melhorar a visibilidade
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Conteúdo rolável
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { CategoryTitle("Dia a Dia") }
                    item {
                        ConversationCategoryCard(
                            title = "Em casa",
                            imageRes = R.mipmap.ic_emcasa_carteirinha_foreground,
                            backgroundColor = Color(0xFFD3E3FC),
                            onClick = { navController.navigate("conversaEmCasaScreen") }
                        )
                    }
                    item {
                        ConversationCategoryCard(
                            title = "Emoções",
                            imageRes = R.mipmap.ic_emocoes_carteirinha_foreground,
                            backgroundColor = Color(0xFF6394F0),
                            onClick = { navController.navigate("EmocoesScreen") }
                        )
                    }
                    item {
                        ConversationCategoryCard(
                            title = "Necessidades",
                            imageRes = R.mipmap.ic_banheiro_foreground,
                            backgroundColor = Color(0xFF847DB0),
                            onClick = { navController.navigate("NecessidadesScreen") }
                        )
                    }
                    item { CategoryTitle("Rotinas") }
                    item {
                        ConversationCategoryCard(
                            title = "Dor",
                            imageRes = R.mipmap.ic_dordecabeca_foreground,
                            backgroundColor = Color(0xFF7FA99F),
                            onClick = { navController.navigate("DorScreen") }

                        )
                    }
                    item {
                        ConversationCategoryCard(
                            title = "Comidas",
                            imageRes = R.mipmap.ic_comidas_foreground,
                            backgroundColor = Color(0xFFEBD0B1),
                            onClick = { navController.navigate("ComidasScreen") }

                        )
                    }
                }
            }
        }
    }
}

// ✅ Barra Superior com Botão "Voltar" e Título Centralizado
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF4A90E2)), // 🔹 Azul moderno
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center // 🔹 Garante que o título fique centralizado
            ) {
                Text(
                    text = "Comunicação",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Voltar",
                    tint = Color.Black
                )
            }
        }
    )
}

// ✅ Título de categoria
@Composable
fun CategoryTitle(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    )
}

// ✅ Cartão de Categoria de Conversa
@Composable
fun ConversationCategoryCard(title: String, imageRes: Int, backgroundColor: Color, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ✅ Preview da Tela de Conversas
@Preview(showBackground = true)
@Composable
fun ConversaScreenPreview() {
    ConversaScreen(navController = rememberNavController())
}
