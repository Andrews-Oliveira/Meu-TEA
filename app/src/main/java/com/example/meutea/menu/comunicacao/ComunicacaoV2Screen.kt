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
import com.example.meutea.ui.TeaGlassPanel
import com.example.meutea.ui.TeaPalette
import com.example.meutea.ui.TeaWallpaper
import com.example.meutea.ui.rememberTeaTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    "agua" -> R.drawable.tea_pic_agua
    "comer" -> R.drawable.tea_pic_comer
    "banheiro" -> R.drawable.tea_pic_banheiro
    "ajuda" -> R.drawable.tea_pic_ajuda
    "feliz" -> R.drawable.tea_pic_feliz
    "triste" -> R.drawable.tea_pic_triste
    "medo" -> R.drawable.tea_medo
    "raiva" -> R.drawable.tea_raiva
    "cansaco" -> R.drawable.tea_pic_cansaco
    "dor" -> R.drawable.tea_pic_dor
    "silencio" -> R.drawable.tea_pic_silencio
    "pausa" -> R.drawable.tea_pic_pausa
    "sim" -> R.drawable.tea_pic_sim
    "nao" -> R.drawable.tea_pic_nao
    "escolher" -> R.drawable.tea_pic_escolher
    "parar" -> R.drawable.tea_pic_parar
    "oi" -> R.drawable.tea_pic_oi
    "obrigado" -> R.drawable.tea_pic_obrigado
    "desculpa" -> R.drawable.tea_pic_desculpa
    "repetir" -> R.drawable.tea_pic_repetir
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComunicacaoV2Screen(navController: NavHostController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current
    val temaVisual = rememberTeaTheme()
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

    Box(modifier = Modifier.fillMaxSize()) {
        TeaWallpaper(temaVisual = temaVisual, modifier = Modifier.fillMaxSize(), tint = 0.06f)
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text("Minha comunicação", color = Color.White,
                            fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("‹ Voltar", color = Color.White)
                        }
                    },
                    actions = {
                        TextButton(onClick = { mostrarVozes = true }) {
                            Text("◖ Voz", color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xDF093A70)
                    )
                )
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                Spacer(Modifier.height(85.dp))
                TeaGlassPanel(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                    dark = true
                ) {
                    Text("MINHA FRASE", style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFC0FFEE), fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(5.dp))
                    Text(
                        if (frases.isEmpty()) "Toque nas figuras para se expressar"
                        else frases.joinToString(". "),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { falar(frases.joinToString(". ")) },
                            enabled = frases.isNotEmpty() && vozDisponivel,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE6F8FF),
                                contentColor = TeaPalette.ink
                            )
                        ) { Text("▶ Ouvir tudo") }
                        OutlinedButton(
                            onClick = { frases.clear() },
                            enabled = frases.isNotEmpty(),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
                        ) { Text("Limpar") }
                    }
                }
                aviso?.let {
                    Text(it, color = Color.White, modifier = Modifier
                        .padding(horizontal = 17.dp, vertical = 5.dp))
                }
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categorias) { item ->
                        FilterChip(
                            selected = categoria == item,
                            onClick = { categoria = item },
                            label = { Text(item) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xE8F8FDFF),
                                labelColor = TeaPalette.ink,
                                selectedContainerColor = TeaPalette.ocean,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 17.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Toque para falar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White)
                    TextButton(onClick = { criar = true }, enabled = uid != null) {
                        Text("+ Meu cartão", color = Color.White,
                            fontWeight = FontWeight.Bold)
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 145.dp),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 36.dp),
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
                            shape = RoundedCornerShape(21.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xF2E9F9FF)
                            ),
                            border = BorderStroke(1.5.dp, Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(9.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val picture = faceDrawable(item.id)
                                if (picture != null) {
                                    Image(
                                        painterResource(picture),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth().height(109.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.size(94.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF8CE9E5), Color(0xFF7FBCEE))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(item.simbolo, fontSize = 42.sp)
                                    }
                                }
                                Spacer(Modifier.height(7.dp))
                                Text(
                                    item.texto,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TeaPalette.ink,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                if (customizados.any { it.id == item.id }) {
                                    TextButton(onClick = { edicao = item }) {
                                        Text("Editar", color = TeaPalette.ocean)
                                    }
                                }
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
