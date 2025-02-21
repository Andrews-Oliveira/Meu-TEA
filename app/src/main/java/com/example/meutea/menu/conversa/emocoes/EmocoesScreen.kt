package com.example.meutea.menu.conversa.emocoes

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.meutea.R
import com.example.meutea.menu.conversa.comidas.TopBar
import java.util.*

@Composable
fun EmocoesScreen(navController: NavHostController) {
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale("pt", "BR")
                tts.value?.setSpeechRate(0.9f)
            }
        }
    }

    Scaffold(
        topBar = { TopBar(navController, "Emoções") },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                OptionsGrid(tts.value)
            }
        }
    }
}

@Composable
fun OptionsGrid(tts: TextToSpeech?) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { OptionCard("EU ESTOU FELIZ", R.drawable.arroz, tts) }
        item { OptionCard("EU ESTOU TRISTE", R.drawable.feijao, tts) }
        item { OptionCard("EU ESTOU COM RAIVA", R.drawable.prato, tts) }
        item { OptionCard("EU QUERO DORMIR", R.drawable.carne, tts) }
        item { OptionCard("AQUI ESTÁ MUITO BARULHO", R.drawable.carne, tts) }
        item { OptionCard("EU TE AMO", R.drawable.carne, tts) }
    }
}

@Composable
fun OptionCard(text: String, imageRes: Int, tts: TextToSpeech?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD0DCF3)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                tts?.stop()
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = text,
                modifier = Modifier
                    .size(150.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }
    }
}