package com.example.meutea.menu.conversa.dor

import android.content.Context
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
fun DorScreen(navController: NavController) {
    val context = LocalContext.current
    val textToSpeech = remember { mutableStateOf<TextToSpeech?>(null) }

    // ✅ Inicializa o Text-to-Speech
    LaunchedEffect(Unit) {
        textToSpeech.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.value?.language = Locale("pt", "BR")
            }
        }
    }

    Scaffold(
        topBar = { TopBar(navController) },
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { PainCard("ESTOU COM DOR DE CABEÇA", R.drawable.dor_cabeca, textToSpeech.value) }
                item { PainCard("ESTOU COM DOR DE GARGANTA", R.drawable.dor_garganta, textToSpeech.value) }
                item { PainCard("ESTOU COM DOR DE BARRIGA", R.drawable.dor_barriga, textToSpeech.value) }
                item { PainCard("ESTOU COM DOR NA PERNA", R.drawable.dor_perna, textToSpeech.value) }
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

// ✅ Cartão de dor com Text-to-Speech
@Composable
fun PainCard(title: String, imageRes: Int, textToSpeech: TextToSpeech?) {
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
                    .size(120.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }
    }
}
