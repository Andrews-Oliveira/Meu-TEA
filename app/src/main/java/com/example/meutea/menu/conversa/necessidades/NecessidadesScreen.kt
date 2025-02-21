package com.example.meutea.menu.conversa.necessidades

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.meutea.R
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NecessidadesScreen(navController: NavController) {
    val context = LocalContext.current
    val textToSpeech = remember { mutableStateOf<TextToSpeech?>(null) }

    // ✅ Inicializa o Text-to-Speech
    LaunchedEffect(Unit) {
        textToSpeech.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.value?.language = Locale("pt", "BR")
                textToSpeech.value?.setSpeechRate(0.9f)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
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

        Scaffold(
            topBar = { TopBar(navController) },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item { NecessidadeCard("SIM", R.drawable.gostei, textToSpeech.value) }
                    item { NecessidadeCard("NÃO", R.drawable.naogostei, textToSpeech.value) }
                    item { NecessidadeCard("AJUDA", R.drawable.ajuda, textToSpeech.value) }
                    item { NecessidadeCard("QUERO FAZER O 1", R.drawable.xixi, textToSpeech.value) }
                    item { NecessidadeCard("QUERO FAZER O 2", R.drawable.fazer_o_dois, textToSpeech.value) }
                }
            }
        }
    }
}

// ✅ Barra Superior com Botão "Voltar"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF4A90E2)),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Necessidades",
                    fontSize = 22.sp,
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

// ✅ Cartão de Necessidades com Text-to-Speech
@Composable
fun NecessidadeCard(title: String, imageRes: Int, textToSpeech: TextToSpeech?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD0DCF3)),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                textToSpeech?.stop()
                textToSpeech?.speak(title, TextToSpeech.QUEUE_FLUSH, null, null)
            }
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(150.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }
    }
}
