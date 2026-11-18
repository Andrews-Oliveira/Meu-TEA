package com.example.meutea.menu.careiradigital

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.auth.FirebaseAuth
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
    var salvando by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Nunca permitir edição de uma conta diferente da sessão atual.
    LaunchedEffect(userId) {
        if (FirebaseAuth.getInstance().currentUser?.uid != userId) {
            errorMessage = "Acesse sua conta para editar a identificação."
            isLoading = false
            return@LaunchedEffect
        }
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
    } else if (usuario == null) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(errorMessage ?: "Não foi possível carregar a identificação.",
                color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
        }
    } else {
        usuario?.let { user ->
            var nome by remember { mutableStateOf(user.nome) }
            var cpf by remember { mutableStateOf(user.cpf ?: "") }
            var rg by remember { mutableStateOf(user.rg ?: "") }
            var nascimento by remember { mutableStateOf(user.nascimento ?: "") }
            var genero by remember { mutableStateOf(user.genero) }
            var tipoSanguineo by remember { mutableStateOf(user.tipoSanguineo) }
            var telefone by remember { mutableStateOf(user.telefone ?: "") }
            var email by remember { mutableStateOf(user.email ?: "") }
            var endereco by remember { mutableStateOf(user.endereco ?: "") }
            var estado by remember { mutableStateOf(user.estado) }
            var cid by remember { mutableStateOf(user.cid ?: "") }
            var naturalidade by remember { mutableStateOf(user.naturalidade) }
            var responsavel by remember { mutableStateOf(user.responsavelLegal) }
            var emergencia by remember { mutableStateOf(user.contatoEmergencia) }
            var comunicacao by remember { mutableStateOf(user.preferenciaComunicacao) }
            var observacoes by remember { mutableStateOf(user.observacoesApoio) }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Minha identificação", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF123B62))
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
                            .padding(horizontal = 16.dp)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F1F7))
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Edite suas informações",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF103855),
                                    fontWeight = FontWeight.ExtraBold)
                                Spacer(Modifier.height(5.dp))
                                Text("Os campos de apoio são opcionais. Seus dados não são uma CIPTEA oficial.",
                                    color = Color(0xFF3B5C71),
                                    style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Text("Identificação", color = Color(0xFF123B62),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth())
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

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Rede de apoio (opcional)", color = Color(0xFF123B62),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                        CustomTextField("Nome do responsável ou pessoa de referência", responsavel) {
                            responsavel = it.take(100)
                        }
                        CustomTextField("Telefone de emergência", emergencia) {
                            emergencia = it.take(30)
                        }
                        CustomTextField("Como prefere se comunicar", comunicacao) {
                            comunicacao = it.take(160)
                        }
                        CustomTextField("Observações de apoio (se desejar)", observacoes) {
                            observacoes = it.take(280)
                        }
                        Text("Compartilhe apenas o necessário. A fotografia é alterada na tela da carteirinha.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF48677B),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
                        errorMessage?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
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
                                        if (nome.isBlank()) {
                                            errorMessage = "Informe o nome para salvar."
                                            return@launch
                                        }
                                        if (FirebaseAuth.getInstance().currentUser?.uid != userId) {
                                            errorMessage = "Sessão inválida. Faça login novamente."
                                            return@launch
                                        }
                                        if (salvando) return@launch
                                        salvando = true
                                        errorMessage = null
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
                                                "naturalidade" to naturalidade,
                                                "responsavelLegal" to responsavel.trim(),
                                                "contatoEmergencia" to emergencia.trim(),
                                                "preferenciaComunicacao" to comunicacao.trim(),
                                                "observacoesApoio" to observacoes.trim()
                                            )

                                            FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                                                .update(updatedData).await()

                                            // 🔹 Voltar para a tela de visualização da carteirinha
                                            navController.navigate("carteirinhaViewScreen/$userId") {
                                                popUpTo("editarCarteirinha/{usuarioId}") { inclusive = true }
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "Não foi possível salvar as informações. Verifique sua conexão."
                                        } finally {
                                            salvando = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f),
                                enabled = !salvando
                            ) {
                                Text(if (salvando) "Salvando..." else "Salvar", color = Color.White)
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
        colors = OutlinedTextFieldDefaults.colors(
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
