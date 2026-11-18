package com.example.meutea.menu.comunicacao

import android.Manifest
import android.graphics.Bitmap
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.compose.foundation.Image
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    val fotosCartoes = remember(context, uid) { uid?.let { FotosCartao(context, it) } }
    var revisaoFotos by remember { mutableIntStateOf(0) }
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
    // A pessoa escolhe explicitamente o estilo da síntese disponível no aparelho.
    var estiloVoz by remember {
        mutableStateOf(prefs.getString("estilo_voz", "Serena") ?: "Serena")
    }
    // As gravações reais são privadas por UID e independem do mecanismo de TTS.
    val audiosPessoais = remember(context, uid) {
        uid?.let { AudioPessoal(context, it) }
    }
    var editorAudio by remember(uid) { mutableStateOf<ComunicacaoCard?>(null) }
    var audioGravando by remember { mutableStateOf(false) }
    var revisaoAudio by remember(uid) { mutableIntStateOf(0) }
    var pendentePermissao by remember { mutableStateOf<ComunicacaoCard?>(null) }

    val solicitarMicrofone = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permitido ->
        val cartao = pendentePermissao
        pendentePermissao = null
        if (permitido && cartao != null) {
            editorAudio = cartao
        } else if (!permitido) {
            aviso = "Microfone não autorizado. Você ainda pode usar as vozes do aparelho."
        }
    }
    val importarArquivo = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        val cartao = editorAudio
        if (uri != null && cartao != null) {
            val resultado = audiosPessoais?.importar(cartao.id, uri)
            if (resultado?.isSuccess == true) {
                revisaoAudio++
                aviso = "Áudio importado. Toque no cartão para ouvi-lo."
            } else {
                aviso = resultado?.exceptionOrNull()?.localizedMessage
                    ?: "Não foi possível importar esse áudio."
            }
        }
    }

    DisposableEffect(audiosPessoais) {
        onDispose { audiosPessoais?.fechar() }
    }

    // Limite de 15 segundos: gravação só funciona com a tela aberta.
    LaunchedEffect(audioGravando, editorAudio?.id) {
        if (audioGravando) {
            delay(15_000)
            val resultado = audiosPessoais?.finalizar()
            audioGravando = false
            if (resultado?.isSuccess == true) {
                revisaoAudio++
                aviso = "Sua voz foi salva neste aparelho."
            } else {
                aviso = resultado?.exceptionOrNull()?.localizedMessage
                    ?: "Não foi possível finalizar a gravação."
            }
        }
    }

    fun abrirEditorAudio(cartao: ComunicacaoCard) {
        if (uid == null || audiosPessoais == null) {
            aviso = "Entre na conta para criar suas gravações."
            return
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            editorAudio = cartao
        } else {
            pendentePermissao = cartao
            solicitarMicrofone.launch(Manifest.permission.RECORD_AUDIO)
        }
    }


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
            val ritmoBase = if (ritmoCalmo) 0.88f else 1.0f
            val (ritmo, tom) = when (estiloVoz) {
                "Animada" -> 1.07f to 1.10f
                "Grave" -> 0.94f to 0.90f
                else -> 1.0f to 1.0f
            }
            setSpeechRate(ritmoBase * ritmo)
            setPitch(tom)
            speak(texto, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
        }
    }

    fun reproduzirCartao(item: ComunicacaoCard) {
        if (audiosPessoais?.existe(item.id) == true) {
            engine?.stop()
            if (!audiosPessoais.reproduzir(item.id)) {
                aviso = "O áudio pessoal não pôde ser reproduzido. Usando a voz do aparelho."
                falar(item.texto)
            }
        } else {
            falar(item.texto)
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
                                if (!audioGravando) {
                                    reproduzirCartao(item)
                                    if (frases.size < 5) frases.add(item.texto)
                                }
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
                                // Fotos personalizadas são carregadas fora da thread da UI.
                                val foto by produceState<Bitmap?>(
                                    initialValue = null, item.id, revisaoFotos, fotosCartoes
                                ) {
                                    value = withContext(Dispatchers.IO) {
                                        fotosCartoes?.lerSalva(item.id)
                                    }
                                }
                                val picture = faceDrawable(item.id)
                                if (foto != null) {
                                    Image(
                                        bitmap = foto!!.asImageBitmap(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxWidth().height(109.dp)
                                    )
                                } else if (picture != null) {
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
                                val possuiAudio = revisaoAudio.let {
                                    audiosPessoais?.existe(item.id) == true
                                }
                                TextButton(onClick = { abrirEditorAudio(item) }) {
                                    Text(
                                        if (possuiAudio) "🎙 Minha voz ✓" else "🎙 Gravar voz",
                                        color = TeaPalette.ocean,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (customizados.any { it.id == item.id }) {
                                    TextButton(onClick = { edicao = item }) {
                                        Text("Editar cartão", color = TeaPalette.ocean)
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
                Column(
                    modifier = Modifier.heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Text("Sua própria voz: toque em 'Gravar voz' abaixo de um cartão. " +
                        "A gravação substitui a síntese naquele cartão e funciona sem internet.")
                    Text("Para frases novas sem gravação, escolha um estilo de voz do Android. " +
                        "Não são vozes de personagens reais.")
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf("Serena", "Animada", "Grave").forEach { modo ->
                            FilterChip(
                                selected = estiloVoz == modo,
                                onClick = {
                                    estiloVoz = modo
                                    prefs.edit().putString("estilo_voz", modo).apply()
                                    falar("Olá! Você pode me ouvir.")
                                },
                                label = { Text(modo) }
                            )
                        }
                    }
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

    editorAudio?.let { cartao ->
        val possuiAudio = revisaoAudio.let { audiosPessoais?.existe(cartao.id) == true }
        AlertDialog(
            onDismissRequest = {
                if (!audioGravando) editorAudio = null
            },
            title = { Text("Minha voz • " + cartao.texto) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Grave você dizendo esta frase, ou importe um áudio próprio ou autorizado.")
                    Text(
                        "“" + cartao.texto + "”",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (audioGravando) {
                        Text("● GRAVANDO • fale normalmente (máximo de 15 segundos).",
                            color = Color(0xFFBB3939), fontWeight = FontWeight.Bold)
                    } else if (possuiAudio) {
                        Text("Sua gravação está salva. O cartão já usa sua voz.")
                    } else {
                        Text("Este cartão ainda usa a voz do Android.")
                    }
                    Text(
                        "Privacidade: o áudio fica apenas neste aparelho, sem envio ao Firebase. " +
                            "Não é uma clonagem de voz para textos livres.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (!audioGravando) {
                        OutlinedButton(
                            onClick = { importarArquivo.launch(arrayOf("audio/*")) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Importar áudio do aparelho") }
                        if (possuiAudio) {
                            OutlinedButton(
                                onClick = {
                                    engine?.stop()
                                    if (audiosPessoais?.reproduzir(cartao.id) != true) {
                                        aviso = "Não foi possível reproduzir a gravação."
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("▶ Testar gravação") }
                            TextButton(onClick = {
                                if (audiosPessoais?.apagar(cartao.id) == true) {
                                    revisaoAudio++
                                    aviso = "Gravação excluída. O cartão voltou à voz do Android."
                                    editorAudio = null
                                } else {
                                    aviso = "Não foi possível apagar essa gravação."
                                }
                            }) { Text("Excluir gravação") }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (audioGravando) {
                        val resultado = audiosPessoais?.finalizar()
                        audioGravando = false
                        if (resultado?.isSuccess == true) {
                            revisaoAudio++
                            aviso = "Sua voz foi salva. Toque no cartão para testar."
                        } else {
                            aviso = resultado?.exceptionOrNull()?.localizedMessage
                                ?: "Não foi possível salvar a gravação."
                        }
                    } else {
                        engine?.stop()
                        val resultado = audiosPessoais?.iniciar(cartao.id)
                        if (resultado?.isSuccess == true) {
                            audioGravando = true
                        } else {
                            aviso = resultado?.exceptionOrNull()?.localizedMessage
                                ?: "Não foi possível acessar o microfone."
                        }
                    }
                }) {
                    Text(if (audioGravando) "Parar e salvar" else
                        if (possuiAudio) "Regravar" else "Começar gravação")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (audioGravando) {
                        audiosPessoais?.cancelar()
                        audioGravando = false
                    }
                    editorAudio = null
                }) { Text("Cancelar") }
            }
        )
    }

    val editor = edicao
    val currentUid = uid
    val storage = fotosCartoes
    if ((criar || editor != null) && currentUid != null && storage != null) {
        EditorCartaoDialog(
            atual = editor,
            uid = currentUid,
            fotos = storage,
            onClose = {
                criar = false
                edicao = null
            },
            onSaved = {
                revisaoFotos++
                aviso = "Cartão salvo. Toque na imagem para ouvir sua mensagem."
                criar = false
                edicao = null
            },
            onDelete = { item ->
                FirebaseFirestore.getInstance().collection("usuarios").document(currentUid)
                    .collection("comunicacao").document(item.id)
                    .delete()
                    .addOnSuccessListener {
                        audiosPessoais?.apagar(item.id)
                        storage.remover(item.id)
                        revisaoAudio++
                        revisaoFotos++
                        aviso = "Cartão e foto removidos do aparelho."
                        criar = false
                        edicao = null
                    }
                    .addOnFailureListener {
                        aviso = "Não foi possível excluir o cartão. Verifique a conexão."
                    }
            }
        )
    }
}
