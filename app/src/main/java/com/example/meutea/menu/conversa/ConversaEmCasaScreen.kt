package com.example.meutea.menu.conversa

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.meutea.R
import java.util.*

@Composable
fun ConversaEmCasaScreen(context: Context, navController: NavHostController) {
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.value?.setLanguage(Locale("pt", "BR"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    println("Erro: Português não suportado no dispositivo!")
                }
            } else {
                println("Erro ao inicializar TTS!")
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
            topBar = { TopBar(navController, "Em Casa") }, // ✅ Agora recebe o título corretamente
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
                OptionsGrid(tts.value)
            }
        }
    }
}

// ✅ Barra Superior com Botão "Voltar"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController, s: String) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF4A90E2)), // 🔹 Azul mais profissional
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
                modifier = Modifier.padding(start = 8.dp) // 🔹 Ajuste no espaçamento
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24), // 🔹 Ícone mais elegante
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
        }
    )
}


// ✅ Lista de Opções com `LazyColumn` para rolagem
@Composable
fun OptionsGrid(tts: TextToSpeech?) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { OptionCard("GOSTEI", R.drawable.gostei, tts) }
        item { OptionCard("QUERO", R.drawable.quero, tts) }
        item { OptionCard("PEGUE", R.drawable.pegar, tts) }
        item { OptionCard("FAÇO", R.drawable.faco, tts) }
    }
}

// ✅ Cartão Individual com Text-to-Speech (Fala ao Clicar)
@Composable
fun OptionCard(text: String, imageRes: Int, tts: TextToSpeech?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFCDE0F9)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = text,
                modifier = Modifier
                    .size(100.dp)
                    .clickable { tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, fontSize = 32.sp, color = Color.Black)
        }
    }
}
