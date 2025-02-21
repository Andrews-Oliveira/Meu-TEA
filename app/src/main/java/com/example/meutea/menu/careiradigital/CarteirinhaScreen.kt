package com.example.meutea.menu.careiradigital

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.menu.CustomFormattedTextField
import com.example.meutea.menu.DropdownMenuField
import com.example.meutea.models.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
// 🔹 Lista de Estados do Brasil
val estadosDoBrasil = listOf(
    "Selecione",
    "Acre (AC)",
    "Alagoas (AL)",
    "Amapá (AP)",
    "Amazonas (AM)",
    "Bahia (BA)",
    "Ceará (CE)",
    "Distrito Federal (DF)",
    "Espírito Santo (ES)",
    "Goiás (GO)",
    "Maranhão (MA)",
    "Mato Grosso (MT)",
    "Mato Grosso do Sul (MS)",
    "Minas Gerais (MG)",
    "Pará (PA)",
    "Paraíba (PB)",
    "Paraná (PR)",
    "Pernambuco (PE)",
    "Piauí (PI)",
    "Rio de Janeiro (RJ)",
    "Rio Grande do Norte (RN)",
    "Rio Grande do Sul (RS)",
    "Rondônia (RO)",
    "Roraima (RR)",
    "Santa Catarina (SC)",
    "São Paulo (SP)",
    "Sergipe (SE)",
    "Tocantins (TO)"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CarteirinhaScreen(navController: NavController, usuarioId: String) {

    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var rg by remember { mutableStateOf("") }
    var nascimento by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("Selecione") }
    var tipoSanguineo by remember { mutableStateOf("Selecione") }
    var telefoneContato by remember { mutableStateOf("") }
    var emailContato by remember { mutableStateOf("") } // ✅ Adicionado o campo de e-mail
    var endereco by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Selecione") }
    var cid by remember { mutableStateOf("") }
    var naturalidade by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val db = Firebase.firestore

    fun validarCampos(): Boolean {
        if (nome.isBlank() || cpf.length != 11 || rg.length !in 7..9 || nascimento.length != 8 ||
            tipoSanguineo == "Selecione" || telefoneContato.length != 11 ||
            genero == "Selecione" || endereco.isBlank() || estado == "Selecione" ||
            naturalidade.isBlank() || cid.isBlank() || emailContato.isBlank() || !emailContato.contains("@")
        ) {
            errorMessage = "Preencha todos os campos corretamente!"
            return false
        }
        return true
    }

    fun salvarDadosNoFirestore() {
        if (!validarCampos()) return

        isLoading = true
        errorMessage = null

        val usuario = Usuario(
            nome = nome,
            cpf = cpf,
            rg = rg,
            nascimento = nascimento,
            genero = genero,
            tipoSanguineo = tipoSanguineo,
            telefone = telefoneContato,
            email = emailContato,
            endereco = endereco,
            estado = estado,
            cid = cid,
            naturalidade = naturalidade
        )

        db.collection("usuarios").document(usuarioId)
            .set(usuario)
            .addOnSuccessListener {
                // ✅ Atualiza `carteira_digital = true` no Firestore
                db.collection("usuarios").document(usuarioId)
                    .update("carteira_digital", true)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Carteira digital ativada para usuário: $usuarioId")
                        // ✅ Após salvar, redireciona para `CarteirinhaViewScreen`
                        navController.navigate("carteirinhaViewScreen/$usuarioId") {
                            popUpTo("CarteirinhaScreen") { inclusive = true }
                        }
                    }
            }
            .addOnFailureListener { e ->
                errorMessage = "Erro ao salvar dados: ${e.message}"
            }
            .addOnCompleteListener {
                isLoading = false
            }
    }


    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cadastro da Carteirinha", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0072CE))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = screenWidth * 0.05f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        com.example.meutea.menu.CustomTextField("Nome Completo", nome, 50) {
                            nome = it
                        }
                        CustomFormattedTextField("CPF", cpf, "###########", 11) { cpf = it }
                        CustomFormattedTextField("RG", rg, "########", 9) { rg = it }
                        CustomFormattedTextField(
                            "Data de Nascimento",
                            nascimento,
                            "##/##/####",
                            8
                        ) { nascimento = it }
                        DropdownMenuField(
                            "Sexo",
                            genero,
                            listOf("Masculino", "Feminino", "Outro")
                        ) { genero = it }
                        DropdownMenuField(
                            "Tipo Sanguíneo",
                            tipoSanguineo,
                            listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
                        ) { tipoSanguineo = it }
                        com.example.meutea.menu.CustomTextField(
                            "Naturalidade",
                            naturalidade,
                            50
                        ) { naturalidade = it }
                        com.example.meutea.menu.CustomTextField("CID", cid, 20) { cid = it }
                        com.example.meutea.menu.CustomTextField(
                            "Endereço",
                            endereco,
                            100
                        ) { endereco = it }
                        DropdownMenuField("Estado (UF)", estado, estadosDoBrasil) { estado = it }
                        com.example.meutea.menu.CustomTextField(
                            "E-mail",
                            emailContato,
                            50
                        ) { emailContato = it } // ✅ Adicionado no layout
                        CustomFormattedTextField(
                            "Telefone",
                            telefoneContato,
                            "(##)#########",
                            11
                        ) { telefoneContato = it }
                    }
                }

                // 🔹 Espaçamento extra antes dos botões
                Spacer(modifier = Modifier.height(24.dp))

                // 🔹 Exibir mensagem de erro se houver
                errorMessage?.let {
                    Text(it, color = Color.Red, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 🔹 Botões com espaço extra inferior
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp), // Garante espaço extra na parte inferior
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Voltar")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { salvarDadosNoFirestore() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Salvar")
                        }
                    }
                }
            }

        }

    }

}