package com.example.meutea.menu.rotinas

import androidx.compose.foundation.layout.Arrangement
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

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Minha rotina") },
            navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("Voltar") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "O dia no seu ritmo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Escolha, adapte ou remova cada etapa. Não existe uma rotina única para todas as pessoas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    periodos.forEach { item ->
                        FilterChip(
                            selected = periodo == item,
                            onClick = { periodo = item },
                            label = { Text(item) }
                        )
                    }
                }
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(periodo + " • " + concluidas + " de " + filtradas.size + " concluídas",
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { proporcao },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (filtradas.isNotEmpty() && concluidas == filtradas.size) {
                            Text("Você escolheu e concluiu suas atividades de hoje. Muito bem!",
                                modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(onClick = { criar = true }, enabled = uid != null) { Text("+ Nova etapa") }
                    OutlinedButton(
                        onClick = {
                            val currentUid = uid ?: return@OutlinedButton
                            val path = FirebaseFirestore.getInstance().collection("usuarios")
                                .document(currentUid).collection("rotinas")
                            val toCreate = sugestoes.filter { suggestion ->
                                atividades.none {
                                    it.titulo == suggestion.titulo && it.periodo == suggestion.periodo
                                }
                            }
                            if (toCreate.isEmpty()) {
                                erro = "As sugestões já foram adicionadas. Você pode personalizá-las."
                            } else {
                                salvandoModelos = true
                                val batch = FirebaseFirestore.getInstance().batch()
                                toCreate.forEach { suggestion ->
                                    val ref = path.document()
                                    batch.set(ref, suggestion.copy(id = ref.id))
                                }
                                batch.commit()
                                    .addOnFailureListener { erro = "Falha ao salvar sugestões." }
                                    .addOnCompleteListener { salvandoModelos = false }
                            }
                        },
                        enabled = uid != null && !salvandoModelos
                    ) { Text("Usar sugestões") }
                }
            }
            if (carregando) {
                item { Text("Carregando sua rotina...") }
            }
            erro?.let { msg ->
                item { Text(msg, color = MaterialTheme.colorScheme.error) }
            }
            if (!carregando && filtradas.isEmpty()) {
                item {
                    Text(
                        "Nenhuma etapa nesta parte do dia. Crie uma atividade ou escolha sugestões editáveis.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
            items(filtradas, key = { it.id }) { atividade ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = atividade.concluidaEm == diaAtual,
                                onCheckedChange = { marcado ->
                                    val currentUid = uid ?: return@Checkbox
                                    FirebaseFirestore.getInstance().collection("usuarios")
                                        .document(currentUid).collection("rotinas")
                                        .document(atividade.id)
                                        .update("concluidaEm", if (marcado) diaAtual else "")
                                        .addOnFailureListener {
                                            erro = "Não foi possível atualizar a etapa."
                                        }
                                }
                            )
                            Column(Modifier.weight(1f)) {
                                Text(
                                    atividade.titulo,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleMedium,
                                    textDecoration = if (atividade.concluidaEm == diaAtual)
                                        TextDecoration.LineThrough else TextDecoration.None
                                )
                                Text(atividade.horario, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TextButton(onClick = { editar = atividade }) { Text("Editar") }
                            TextButton(onClick = { excluir = atividade }) { Text("Excluir") }
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(18.dp))
                Text("As conclusões são reiniciadas a cada dia. As atividades permanecem salvas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
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
