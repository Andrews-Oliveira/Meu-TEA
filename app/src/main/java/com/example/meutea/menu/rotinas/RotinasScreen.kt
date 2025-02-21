package com.example.meutea.menu.rotinas

import com.example.meutea.menu.conversa.comidas.TopBar

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.example.meutea.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

val db = FirebaseFirestore.getInstance()

data class RotinaAtividade(
    val id: String = UUID.randomUUID().toString(),
    val data: String,
    val diaSemana: String,
    val horario: String,
    val descricao: String
)


@Composable
fun RotinaGrid(tts: TextToSpeech?, atividades: List<RotinaAtividade>, context: Context) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(atividades) { atividade ->
            RotinaCard(atividade, tts, context) { id ->
                deleteFromFirestore(id)
            }
        }
    }
}

@Composable
fun RotinaCard(atividade: RotinaAtividade, tts: TextToSpeech?, context: Context, onDelete: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("${atividade.data} - ${atividade.diaSemana} - ${atividade.horario} - ${atividade.descricao}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onDelete(atividade.id) }) {
                Text("Excluir")
            }
        }
    }
}

fun deleteFromFirestore(id: String) {
    db.collection("Projeto 01.1").document(id).delete()
        .addOnSuccessListener { Log.d("Firestore", "Atividade excluída com sucesso!") }
        .addOnFailureListener { e -> Log.w("Firestore", "Erro ao excluir atividade", e) }
}
@Composable
fun RotinasScreen(navController: NavHostController) {
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    val rotinaAtividades = remember { mutableStateListOf<RotinaAtividade>() }
    var data by remember { mutableStateOf("") }
    var diaSemana by remember { mutableStateOf("Segunda-feira") }
    var horario by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale("pt", "BR")
                tts.value?.setSpeechRate(0.9f)
            }
        }
    }

    Scaffold(
        topBar = { TopBar(navController, "Rotina") },
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
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.9f)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it },
                    label = { Text("Data (DD/MM/AAAA)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                DiaSemanaDropdown(diaSemana) { diaSemana = it }
                OutlinedTextField(
                    value = horario,
                    onValueChange = { horario = it },
                    label = { Text("Horário (HH:MM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição da Atividade") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (data.isNotEmpty() && horario.isNotEmpty() && descricao.isNotEmpty()) {
                            val atividade = RotinaAtividade(data = data, diaSemana = diaSemana, horario = horario, descricao = descricao)
                            rotinaAtividades.add(atividade)
                            saveToFirestore(atividade)
                            data = ""
                            horario = ""
                            descricao = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Adicionar Atividade", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                RotinaGrid(tts.value, rotinaAtividades, context)
            }
        }
    }
}

fun saveToFirestore(atividade: RotinaAtividade) {
    db.collection("Projeto 01.1").document(atividade.id).set(atividade)
        .addOnSuccessListener { Log.d("Firestore", "Atividade salva com sucesso!") }
        .addOnFailureListener { e -> Log.w("Firestore", "Erro ao salvar atividade", e) }
}
@Composable
fun DiaSemanaDropdown(selectedDay: String, onDaySelected: (String) -> Unit) {
    val diasSemana = listOf("Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado", "Domingo")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedDay)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            diasSemana.forEach { dia ->
                DropdownMenuItem(text = { Text(dia) }, onClick = {
                    onDaySelected(dia)
                    expanded = false
                })
            }
        }
    }
}
