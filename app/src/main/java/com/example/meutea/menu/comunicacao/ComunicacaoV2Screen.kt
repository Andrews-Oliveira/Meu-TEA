package com.example.meutea.menu.comunicacao

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.meutea.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.util.UUID

// A lista inicial funciona offline. Os cartões adicionados são sincronizados no Firestore.
data class ComunicacaoCard(
    val id: String = "",
    val texto: String = "",
    val categoria: String = "Necessidades",
    val simbolo: String = "◉"
)

private val categorias = listOf("Tudo", "Necessidades", "Emoções", "Conforto", "Escolhas", "Convivência")
private val iniciais = listOf(
    ComunicacaoCard("agua", "Quero água", "Necessidades", "💧"),
    ComunicacaoCard("comer", "Estou com fome", "Necessidades", "🍽"),
    ComunicacaoCard("banheiro", "Preciso ir ao banheiro", "Necessidades", "🚻"),
    ComunicacaoCard("ajuda", "Preciso de ajuda", "Necessidades", "✋"),
    ComunicacaoCard("feliz", "Estou feliz", "Emoções", "☺"),
    ComunicacaoCard("triste", "Estou triste", "Emoções", "☹"),
    ComunicacaoCard("medo", "Estou com medo", "Emoções", "◔"),
    ComunicacaoCard("raiva", "Estou com raiva", "Emoções", "◕"),
    ComunicacaoCard("cansaco", "Estou cansado", "Conforto", "☾"),
    ComunicacaoCard("dor", "Estou com dor", "Conforto", "♡"),
    ComunicacaoCard("silencio", "Preciso de silêncio", "Conforto", "♫"),
    ComunicacaoCard("pausa", "Quero fazer uma pausa", "Conforto", "Ⅱ"),
    ComunicacaoCard("sim", "Sim", "Escolhas", "✓"),
    ComunicacaoCard("nao", "Não", "Escolhas", "✕"),
    ComunicacaoCard("escolher", "Quero escolher", "Escolhas", "◇"),
    ComunicacaoCard("parar", "Quero parar", "Escolhas", "■"),
    ComunicacaoCard("oi", "Olá!", "Convivência", "☀"),
    ComunicacaoCard("obrigado", "Obrigado!", "Convivência", "♥"),
    ComunicacaoCard("desculpa", "Não entendi", "Convivência", "?"),
    ComunicacaoCard("repetir", "Pode repetir?", "Convivência", "↻")
)

private fun faceDrawable(id: String): Int? = when (id) {
    "feliz" -> R.drawable.tea_feliz
    "triste" -> R.drawable.tea_triste
    "medo" -> R.drawable.tea_medo
    "raiva" -> R.drawable.tea_raiva
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComunicacaoV2Screen(navController: NavHostController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("meutea_fala", 0) }
    var customizados by remember(uid) { mutableStateOf(emptyList<ComunicacaoCard>()) }
    var categoria by remember { mutableStateOf("Tudo") }
    var aviso by remember { mutableStateOf<String?>(null) }
    var edicao by remember { mutableStateOf<ComunicacaoCard?>(null) }
    var criar by remember { mutableStateOf(false) }
    val frases = remember { mutableStateListOf<String>() }
    var engine by remember { mutableStateOf<TextToSpeech?>(null) }
    var vozDisponivel by remember { mutableStateOf(false) }
    var vozEscolhida by remember { mutableStateOf(prefs.getString("voz", "") ?: "") }
    var ritmoCalmo by remember { mutableStateOf(prefs.getBoolean("ritmo_calmo", true)) }
    var mostrarVozes by remember { mutableStateOf(false) }

    // Libera o motor de fala quando a tela é fechada; não há acesso a voz antes da inicialização.
    DisposableEffect(context) {
        val tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val current = engine
                val supported = current?.setLanguage(Locale.forLanguageTag("pt-BR"))
                vozDisponivel = supported != null &&
                    supported != TextToSpeech.LANG_NOT_SUPPORTED &&
                    supported != TextToSpeech.LANG_MISSING_DATA
            } else {
                aviso = "A voz deste aparelho não está disponível. Verifique o mecanismo de síntese de voz."
            }
        }
        engine = tts
        onDispose {
            tts.stop()
            tts.shutdown()
            engine = null
            vozDisponivel = false
        }
    }

    val vozes = remember(engine, vozDisponivel) {
        if (!vozDisponivel) emptyList() else engine?.voices
            ?.filter { it.locale.language == "pt" }
            ?.sortedWith(compareByDescending<Voice> { it.quality }.thenBy { it.name })
            ?: emptyList()
    }
    LaunchedEffect(vozEscolhida, vozes) {
        vozes.firstOrNull { it.name == vozEscolhida }?.let { engine?.voice = it }
    }

    DisposableEffect(uid) {
        if (uid == null) {
            aviso = "Entre na sua conta para salvar cartões personalizados."
            onDispose { }
        } else {
            val listener = FirebaseFirestore.getInstance().collection("usuarios")
                .document(uid).collection("comunicacao")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        aviso = "Falha ao sincronizar cartões. Os cartões básicos continuam disponíveis."
                    } else {
                        customizados = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(ComunicacaoCard::class.java)?.copy(id = doc.id)
                        }?.sortedBy { it.texto } ?: emptyList()
                    }
                }
            onDispose { listener.remove() }
        }
    }

    fun falar(texto: String) {
        if (!vozDisponivel) {
            aviso = "Voz indisponível. Instale uma voz em português nas configurações do aparelho."
            return
        }
        engine?.apply {
            setSpeechRate(if (ritmoCalmo) 0.88f else 1.0f)
            setPitch(1.0f)
            speak(texto, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
        }
    }

    val visiveis = (iniciais + customizados).filter {
        categoria == "Tudo" || it.categoria == categoria
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Minha comunicação") },
            navigationIcon = {
                TextButton(onClick = { navController.popBackStack() }) { Text("Voltar") }
            },
            actions = { TextButton(onClick = { mostrarVozes = true }) { Text("Voz") } },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("Minha frase", style = MaterialTheme.typography.labelLarge)
                    Text(
                        if (frases.isEmpty()) "Toque nas figuras para se expressar"
                        else frases.joinToString(". "),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { falar(frases.joinToString(". ")) },
                            enabled = frases.isNotEmpty() && vozDisponivel
                        ) { Text("Ouvir tudo") }
                        OutlinedButton(onClick = { frases.clear() }, enabled = frases.isNotEmpty()) {
                            Text("Limpar")
                        }
                    }
                }
            }
            aviso?.let {
                Text(it, color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp))
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { item ->
                    FilterChip(
                        selected = categoria == item,
                        onClick = { categoria = item },
                        label = { Text(item) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Toque para falar", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                TextButton(onClick = { criar = true }, enabled = uid != null) {
                    Text("+ Meu cartão")
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 145.dp),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(visiveis, key = { it.id }) { item ->
                    Card(
                        onClick = {
                            falar(item.texto)
                            if (frases.size < 5) frases.add(item.texto)
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val picture = faceDrawable(item.id)
                            if (picture != null) {
                                Image(
                                    painterResource(picture),
                                    contentDescription = null,
                                    modifier = Modifier.size(88.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(88.dp).background(
                                        MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp)
                                    ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(item.simbolo, fontSize = 42.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(item.texto, fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center)
                            if (customizados.any { it.id == item.id }) {
                                TextButton(onClick = { edicao = item }) { Text("Editar cartão") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarVozes) {
        AlertDialog(
            onDismissRequest = { mostrarVozes = false },
            title = { Text("Minha voz") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Escolha uma voz disponível em português. A idade e o gênero não definem automaticamente a voz de ninguém.")
                    FilterChip(
                        selected = ritmoCalmo,
                        onClick = {
                            ritmoCalmo = !ritmoCalmo
                            prefs.edit().putBoolean("ritmo_calmo", ritmoCalmo).apply()
                        },
                        label = { Text(if (ritmoCalmo) "Ritmo tranquilo" else "Ritmo normal") }
                    )
                    Text("Vozes instaladas ou disponíveis no aparelho:")
                    if (vozes.isEmpty()) Text("Nenhuma voz detectada.")
                    vozes.take(8).forEachIndexed { index, voice ->
                        FilterChip(
                            selected = voice.name == vozEscolhida,
                            onClick = {
                                vozEscolhida = voice.name
                                prefs.edit().putString("voz", vozEscolhida).apply()
                                engine?.voice = voice
                                falar("Olá! Esta é minha voz.")
                            },
                            label = { Text("Voz " + (index + 1) +
                                if (voice.isNetworkConnectionRequired) " • internet" else " • offline") }
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { mostrarVozes = false }) { Text("Concluir") } }
        )
    }

    val editor = edicao
    if (criar || editor != null) {
        var texto by remember(editor?.id, criar) { mutableStateOf(editor?.texto ?: "") }
        var grupo by remember(editor?.id, criar) { mutableStateOf(editor?.categoria ?: "Necessidades") }
        var salvando by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { if (!salvando) { criar = false; edicao = null } },
            title = { Text(if (editor == null) "Criar cartão" else "Editar meu cartão") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = texto,
                        onValueChange = { texto = it.take(100) },
                        label = { Text("O que a voz deve dizer?") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Categoria")
                    categorias.drop(1).forEach { opcao ->
                        FilterChip(
                            selected = grupo == opcao,
                            onClick = { grupo = opcao },
                            label = { Text(opcao) }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = texto.isNotBlank() && !salvando && uid != null,
                    onClick = {
                        val safeUid = uid ?: return@Button
                        salvando = true
                        val id = editor?.id ?: UUID.randomUUID().toString()
                        FirebaseFirestore.getInstance().collection("usuarios").document(safeUid)
                            .collection("comunicacao").document(id)
                            .set(ComunicacaoCard(id, texto.trim(), grupo, "◉"))
                            .addOnSuccessListener { criar = false; edicao = null; aviso = null }
                            .addOnFailureListener { aviso = "Não foi possível salvar seu cartão." }
                            .addOnCompleteListener { salvando = false }
                    }
                ) { Text("Salvar") }
            },
            dismissButton = {
                Row {
                    if (editor != null) {
                        TextButton(onClick = {
                            val safeUid = uid ?: return@TextButton
                            FirebaseFirestore.getInstance().collection("usuarios").document(safeUid)
                                .collection("comunicacao").document(editor.id)
                                .delete()
                                .addOnSuccessListener { edicao = null }
                                .addOnFailureListener { aviso = "Não foi possível excluir o cartão." }
                        }) { Text("Excluir") }
                    }
                    TextButton(onClick = { criar = false; edicao = null }) { Text("Cancelar") }
                }
            }
        )
    }
}
