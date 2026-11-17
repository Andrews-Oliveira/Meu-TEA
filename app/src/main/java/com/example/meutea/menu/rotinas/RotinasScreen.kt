package com.example.meutea.menu.rotinas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.meutea.R
import com.example.meutea.ui.TeaGlassPanel
import com.example.meutea.ui.TeaPalette
import com.example.meutea.ui.TeaWallpaper
import com.example.meutea.ui.rememberTeaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// A rotina é recorrente: a conclusão vale só para o dia atual, sem apagar o histórico da atividade.
data class AtividadeDiaria(
    val id: String = "",
    val titulo: String = "",
    val horario: String = "",
    val periodo: String = "Manhã",
    val concluidaEm: String = ""
)

private val periodos = listOf("Manhã", "Tarde", "Noite")

private fun imagemDaRotina(titulo: String): Int {
    val value = titulo.lowercase(Locale.ROOT)
    return when {
        "café" in value || "almo" in value || "lanche" in value || "jantar" in value ->
            R.drawable.tea_pic_comer
        "higiene" in value || "banho" in value || "dente" in value ->
            R.drawable.tea_pic_banheiro
        "acordar" in value || "dormir" in value || "descanso" in value ->
            R.drawable.tea_pic_cansaco
        "pausa" in value || "tranquilo" in value ->
            R.drawable.tea_pic_pausa
        "atividade" in value || "brincar" in value ->
            R.drawable.tea_pic_feliz
        else -> R.drawable.tea_pic_escolher
    }
}


private val sugestoes = listOf(
    AtividadeDiaria(titulo = "Acordar com calma", horario = "07:00", periodo = "Manhã"),
    AtividadeDiaria(titulo = "Higiene pessoal", horario = "07:20", periodo = "Manhã"),
    AtividadeDiaria(titulo = "Café da manhã", horario = "07:40", periodo = "Manhã"),
    AtividadeDiaria(titulo = "Organizar o dia", horario = "08:00", periodo = "Manhã"),
    AtividadeDiaria(titulo = "Almoçar", horario = "12:00", periodo = "Tarde"),
    AtividadeDiaria(titulo = "Fazer uma pausa", horario = "13:00", periodo = "Tarde"),
    AtividadeDiaria(titulo = "Atividade escolhida", horario = "15:00", periodo = "Tarde"),
    AtividadeDiaria(titulo = "Lanche", horario = "16:30", periodo = "Tarde"),
    AtividadeDiaria(titulo = "Jantar", horario = "19:00", periodo = "Noite"),
    AtividadeDiaria(titulo = "Higiene da noite", horario = "20:00", periodo = "Noite"),
    AtividadeDiaria(titulo = "Momento tranquilo", horario = "20:30", periodo = "Noite"),
    AtividadeDiaria(titulo = "Preparar para dormir", horario = "21:00", periodo = "Noite")
)

private fun hoje(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

internal fun horarioValido(value: String): Boolean {
    val parts = value.split(":")
    if (parts.size != 2 || parts.any { it.length != 2 }) return false
    val hh = parts[0].toIntOrNull() ?: return false
    val mm = parts[1].toIntOrNull() ?: return false
    return hh in 0..23 && mm in 0..59
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RotinasScreen(navController: NavHostController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val temaVisual = rememberTeaTheme()
    var atividades by remember(uid) { mutableStateOf(emptyList<AtividadeDiaria>()) }
    var periodo by remember { mutableStateOf("Manhã") }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }
    var criar by remember { mutableStateOf(false) }
    var editar by remember { mutableStateOf<AtividadeDiaria?>(null) }
    var excluir by remember { mutableStateOf<AtividadeDiaria?>(null) }
    var salvandoModelos by remember { mutableStateOf(false) }
    val diaAtual = hoje()

    DisposableEffect(uid) {
        if (uid == null) {
            carregando = false
            erro = "Entre na sua conta para organizar suas rotinas."
            onDispose { }
        } else {
            val registro = FirebaseFirestore.getInstance()
                .collection("usuarios").document(uid).collection("rotinas")
                .addSnapshotListener { snapshot, exception ->
                    carregando = false
                    if (exception != null) {
                        erro = "Não foi possível sincronizar a rotina. Verifique sua conexão."
                    } else {
                        atividades = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(AtividadeDiaria::class.java)?.copy(id = doc.id)
                        }?.sortedWith(compareBy({ periodos.indexOf(it.periodo) }, { it.horario })) ?: emptyList()
                        erro = null
                    }
                }
            onDispose { registro.remove() }
        }
    }

    val filtradas = atividades.filter { it.periodo == periodo }
    val concluidas = filtradas.count { it.concluidaEm == diaAtual }
    val proporcao = if (filtradas.isEmpty()) 0f else concluidas.toFloat() / filtradas.size

    Box(Modifier.fillMaxSize()) {
        TeaWallpaper(temaVisual = temaVisual, modifier = Modifier.fillMaxSize(), tint = 0.04f)
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Minha rotina", color = Color.White,
                        fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("‹ Voltar", color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xDF093A70)
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 92.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                item {
                    TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
                        Text("O dia no seu ritmo",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Escolha, adapte ou remova cada etapa. A rotina acompanha suas necessidades.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xDA0A7AB1)),
                        shape = RoundedCornerShape(25.dp),
                        border = BorderStroke(1.5.dp, Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            periodos.forEach { item ->
                                val ativo = periodo == item
                                val emoji = when (item) {
                                    "Manhã" -> "☀"
                                    "Tarde" -> "◕"
                                    else -> "☾"
                                }
                                FilterChip(
                                    selected = ativo,
                                    onClick = { periodo = item },
                                    label = {
                                        Text("$emoji $item",
                                            fontWeight = FontWeight.Bold)
                                    },
                                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                        containerColor = Color.Transparent,
                                        labelColor = Color.White,
                                        selectedContainerColor = Color(0xFFF0FAFF),
                                        selectedLabelColor = TeaPalette.ink
                                    ),
                                    border = BorderStroke(1.dp,
                                        if (ativo) Color.White else Color(0xFF7ADEFF))
                                )
                            }
                        }
                    }
                }
                item {
                    TeaGlassPanel(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(64.dp)
                                    .clip(RoundedCornerShape(17.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF008BB8), Color(0xFF36C9CD))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (periodo == "Manhã") "☀" else
                                    if (periodo == "Tarde") "◕" else "☾",
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineMedium)
                            }
                            Spacer(Modifier.size(13.dp))
                            Column(Modifier.weight(1f)) {
                                Text("$periodo • $concluidas de ${filtradas.size} concluídas",
                                    color = TeaPalette.ink,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    progress = { proporcao },
                                    modifier = Modifier.fillMaxWidth().height(10.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    color = TeaPalette.teal,
                                    trackColor = Color(0xFFB9D9EB)
                                )
                            }
                        }
                        if (filtradas.isNotEmpty() && concluidas == filtradas.size) {
                            Text("Você concluiu as etapas de hoje. Muito bem!",
                                modifier = Modifier.padding(top = 10.dp),
                                color = TeaPalette.ink,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { criar = true },
                            enabled = uid != null,
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = TeaPalette.teal
                            )
                        ) { Text("+ Nova etapa", fontWeight = FontWeight.Bold) }
                        OutlinedButton(
                            onClick = {
                                val currentUid = uid ?: return@OutlinedButton
                                val path = FirebaseFirestore.getInstance().collection("usuarios")
                                    .document(currentUid).collection("rotinas")
                                val toCreate = sugestoes.filter { suggestion ->
                                    atividades.none {
                                        it.titulo == suggestion.titulo &&
                                            it.periodo == suggestion.periodo
                                    }
                                }
                                if (toCreate.isEmpty()) {
                                    erro = "As sugestões já foram adicionadas. Edite conforme necessário."
                                } else {
                                    salvandoModelos = true
                                    val batch = FirebaseFirestore.getInstance().batch()
                                    toCreate.forEach { suggestion ->
                                        val ref = path.document()
                                        batch.set(ref, suggestion.copy(id = ref.id))
                                    }
                                    batch.commit()
                                        .addOnFailureListener {
                                            erro = "Não foi possível adicionar sugestões."
                                        }
                                        .addOnCompleteListener { salvandoModelos = false }
                                }
                            },
                            enabled = uid != null && !salvandoModelos,
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xEFF7FCFF),
                                contentColor = TeaPalette.ink
                            ),
                            border = BorderStroke(1.3.dp, Color.White)
                        ) { Text("✦ Sugestões", fontWeight = FontWeight.Bold) }
                    }
                }
                if (carregando) {
                    item {
                        Text("Carregando suas atividades...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge)
                    }
                }
                erro?.let { msg ->
                    item {
                        TeaGlassPanel(modifier = Modifier.fillMaxWidth()) {
                            Text(msg, color = TeaPalette.ink)
                        }
                    }
                }
                if (!carregando && filtradas.isEmpty()) {
                    item {
                        TeaGlassPanel(modifier = Modifier.fillMaxWidth()) {
                            Text("Nenhuma etapa neste período.",
                                color = TeaPalette.ink,
                                fontWeight = FontWeight.ExtraBold)
                            Text("Crie uma etapa pessoal ou use sugestões para começar.",
                                color = TeaPalette.ink)
                        }
                    }
                }
                items(filtradas, key = { it.id }) { atividade ->
                    val marcada = atividade.concluidaEm == diaAtual
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (marcada)
                                Color(0xF2D8F8E9) else Color(0xF3EAF8FF)
                        ),
                        border = BorderStroke(1.4.dp, Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth().animateContentSize()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(76.dp)
                                    .clip(RoundedCornerShape(17.dp))
                                    .background(Color(0xFFA9E9EC)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(imagemDaRotina(atividade.titulo)),
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp)
                                )
                            }
                            Spacer(Modifier.size(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    atividade.titulo,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TeaPalette.ink,
                                    style = MaterialTheme.typography.titleMedium,
                                    textDecoration = if (marcada)
                                        TextDecoration.LineThrough else TextDecoration.None
                                )
                                Spacer(Modifier.height(5.dp))
                                Text("◷ " + atividade.horario,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF135B9A),
                                    fontWeight = FontWeight.Bold)
                                Row {
                                    TextButton(onClick = { editar = atividade }) {
                                        Text("Editar", color = TeaPalette.ocean)
                                    }
                                    TextButton(onClick = { excluir = atividade }) {
                                        Text("Excluir", color = Color(0xFFAF3F55))
                                    }
                                }
                            }
                            Checkbox(
                                checked = marcada,
                                onCheckedChange = { marcado ->
                                    val currentUid = uid ?: return@Checkbox
                                    FirebaseFirestore.getInstance().collection("usuarios")
                                        .document(currentUid).collection("rotinas")
                                        .document(atividade.id)
                                        .update("concluidaEm", if (marcado) diaAtual else "")
                                        .addOnFailureListener {
                                            erro = "Não foi possível atualizar a etapa."
                                        }
                                },
                                colors = androidx.compose.material3.CheckboxDefaults.colors(
                                    checkedColor = TeaPalette.teal
                                )
                            )
                        }
                    }
                }
                item {
                    TeaGlassPanel(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "A conclusão é marcada por dia. Suas etapas permanecem salvas.",
                            color = TeaPalette.ink,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }

    val atual = editar
    if (criar || atual != null) {
        var titulo by remember(criar, atual?.id) { mutableStateOf(atual?.titulo ?: "") }
        var hora by remember(criar, atual?.id) { mutableStateOf(atual?.horario ?: "08:00") }
        var faixa by remember(criar, atual?.id) { mutableStateOf(atual?.periodo ?: periodo) }
        var salvando by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { if (!salvando) { criar = false; editar = null } },
            title = { Text(if (atual == null) "Nova etapa" else "Editar etapa") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        titulo, onValueChange = { titulo = it.take(70) },
                        label = { Text("Atividade") }, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        hora, onValueChange = { hora = it.take(5) },
                        label = { Text("Horário (HH:mm)") }, modifier = Modifier.fillMaxWidth()
                    )
                    Text("Momento do dia")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        periodos.forEach { item ->
                            FilterChip(selected = faixa == item, onClick = { faixa = item },
                                label = { Text(item) })
                        }
                    }
                    if (!horarioValido(hora)) {
                        Text("Use um horário válido, como 08:30.",
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = titulo.isNotBlank() && horarioValido(hora) && uid != null && !salvando,
                    onClick = {
                        val currentUid = uid ?: return@Button
                        val id = atual?.id ?: UUID.randomUUID().toString()
                        val atividade = AtividadeDiaria(
                            id = id, titulo = titulo.trim(), horario = hora,
                            periodo = faixa, concluidaEm = atual?.concluidaEm ?: ""
                        )
                        salvando = true
                        FirebaseFirestore.getInstance().collection("usuarios")
                            .document(currentUid).collection("rotinas")
                            .document(id).set(atividade)
                            .addOnSuccessListener { criar = false; editar = null; erro = null }
                            .addOnFailureListener { erro = "Não foi possível salvar a etapa." }
                            .addOnCompleteListener { salvando = false }
                    }
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { criar = false; editar = null }) { Text("Cancelar") }
            }
        )
    }

    excluir?.let { atividade ->
        AlertDialog(
            onDismissRequest = { excluir = null },
            title = { Text("Excluir etapa?") },
            text = { Text("A etapa será removida da sua rotina.") },
            confirmButton = {
                Button(onClick = {
                    val currentUid = uid ?: return@Button
                    FirebaseFirestore.getInstance().collection("usuarios")
                        .document(currentUid).collection("rotinas").document(atividade.id)
                        .delete()
                        .addOnSuccessListener { excluir = null }
                        .addOnFailureListener { erro = "Não foi possível excluir a etapa." }
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { excluir = null }) { Text("Cancelar") }
            }
        )
    }
}
