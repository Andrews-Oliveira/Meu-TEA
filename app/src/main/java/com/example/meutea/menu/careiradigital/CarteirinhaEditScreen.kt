package com.example.meutea.menu.careiradigital

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteirinhaEditScreen(navController: NavController, userId: String) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // 🔹 Buscar os dados do usuário no Firestore
    LaunchedEffect(userId) {
        try {
            val document = FirebaseFirestore.getInstance().collection("usuarios").document(userId).get().await()
            if (document.exists()) {
                usuario = document.toObject(Usuario::class.java)
            } else {
                errorMessage = "Usuário não encontrado!"
            }
        } catch (e: Exception) {
            errorMessage = "Erro ao carregar os dados: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
        }
    } else {
        usuario?.let { user ->
            var nome by remember { mutableStateOf(user.nome) }
            var cpf by remember { mutableStateOf(user.cpf ?: "") }
            var rg by remember { mutableStateOf(user.rg ?: "") }
            var nascimento by remember { mutableStateOf(user.nascimento ?: "") }
            var genero by remember { mutableStateOf(user.genero ?: "Masculino") }
            var tipoSanguineo by remember { mutableStateOf(user.tipoSanguineo ?: "O+") }
            var telefone by remember { mutableStateOf(user.telefone ?: "") }
            var email by remember { mutableStateOf(user.email ?: "") }
            var endereco by remember { mutableStateOf(user.endereco ?: "") }
            var estado by remember { mutableStateOf(user.estado ?: "SP") }
            var cid by remember { mutableStateOf(user.cid ?: "") }
            var naturalidade by remember { mutableStateOf(user.naturalidade ?: "") }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Editar Carteirinha", fontSize = 20.sp, color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = screenWidth * 0.05f)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomTextField("Nome", nome) { nome = it }
                        CustomTextField("CPF", cpf) { cpf = it }
                        CustomTextField("RG", rg) { rg = it }
                        CustomTextField("Nascimento", nascimento) { nascimento = it }
                        CustomTextField("Gênero", genero) { genero = it }
                        CustomTextField("Tipo Sanguíneo", tipoSanguineo) { tipoSanguineo = it }
                        CustomTextField("Telefone", telefone) { telefone = it }
                        CustomTextField("E-mail", email) { email = it }
                        CustomTextField("Endereço", endereco) { endereco = it }
                        CustomTextField("Estado", estado) { estado = it }
                        CustomTextField("CID", cid) { cid = it }
                        CustomTextField("Naturalidade", naturalidade) { naturalidade = it }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🔹 Botões de ação
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            val updatedData = mapOf(
                                                "nome" to nome,
                                                "cpf" to cpf,
                                                "rg" to rg,
                                                "nascimento" to nascimento,
                                                "genero" to genero,
                                                "tipoSanguineo" to tipoSanguineo,
                                                "telefone" to telefone,
                                                "email" to email,
                                                "endereco" to endereco,
                                                "estado" to estado,
                                                "cid" to cid,
                                                "naturalidade" to naturalidade
                                            )

                                            FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                                                .update(updatedData).await()

                                            // 🔹 Voltar para a tela de visualização da carteirinha
                                            navController.navigate("carteirinhaViewScreen/$userId") {
                                                popUpTo("carteirinhaEditScreen/$userId") { inclusive = true }
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "Erro ao salvar alterações: ${e.message}"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Salvar", color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// 🔹 Função reutilizável para os campos de entrada
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Black) },
        textStyle = TextStyle(color = Color.Black),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )


}
