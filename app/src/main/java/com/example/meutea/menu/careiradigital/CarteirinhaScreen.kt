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
import androidx.compose.ui.text.font.FontWeight
import com.example.meutea.ui.TeaGlassPanel
import com.example.meutea.ui.TeaPalette
import com.example.meutea.ui.TeaWallpaper
import com.example.meutea.ui.rememberTeaTheme
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.menu.CustomFormattedTextField
import com.example.meutea.menu.DropdownMenuField
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FirebaseFirestore
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

    val db = FirebaseFirestore.getInstance()
    val temaVisual = rememberTeaTheme()

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

        // Grava todos os dados e o indicador de carteirinha em uma única operação.
        // Merge preserva os campos da conta, como uid e configurações existentes.
        val dados = mapOf(
            "nome" to nome,
            "cpf" to cpf,
            "rg" to rg,
            "nascimento" to nascimento,
            "genero" to genero,
            "tipoSanguineo" to tipoSanguineo,
            "telefone" to telefoneContato,
            "email" to emailContato,
            "endereco" to endereco,
            "estado" to estado,
            "cid" to cid,
            "naturalidade" to naturalidade,
            "carteira_digital" to true
        )

        db.collection("usuarios").document(usuarioId)
            .set(dados, SetOptions.merge())
            .addOnSuccessListener {
                navController.navigate("carteirinhaViewScreen/$usuarioId") {
                    popUpTo("carteirinhaScreen/{usuarioId}") { inclusive = true }
                    launchSingleTop = true
                }
            }
            .addOnFailureListener { e ->
                errorMessage = "Não foi possível salvar a carteirinha: ${e.localizedMessage ?: "tente novamente"}"
            }
            .addOnCompleteListener {
                isLoading = false
            }
    }


    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Box(Modifier.fillMaxSize()) {
        TeaWallpaper(temaVisual = temaVisual, modifier = Modifier.fillMaxSize(), tint = 0.05f)
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Minha identificação", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("‹ Voltar", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xDD0A396C))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = screenWidth * 0.05f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(54.dp))
                TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
                    Text("Seus dados, do seu jeito", color = Color.White,
                        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Crie uma identificação pessoal para consultas rápidas. " +
                            "Este registro não substitui a CIPTEA oficial.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(18.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xF7F4FAFF)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        "INFORMAÇÕES PARA IDENTIFICAÇÃO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = TeaPalette.ocean,
                        modifier = Modifier.padding(start = 18.dp, top = 16.dp)
                    )
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
                    Text(it, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                            enabled = !isLoading,
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

}