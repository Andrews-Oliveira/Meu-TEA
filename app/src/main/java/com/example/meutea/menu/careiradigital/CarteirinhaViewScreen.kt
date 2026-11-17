package com.example.meutea.menu.careiradigital

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import com.example.meutea.R
import com.example.meutea.models.Usuario

@Composable
fun CarteirinhaViewScreen(navController: NavController, userId: String?) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteErrorMessage by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(userId) {
        if (userId.isNullOrEmpty()) {
            errorMessage = "Nenhuma carteirinha encontrada."
            isLoading = false
            return@LaunchedEffect
        }

        try {
            val document = db.collection("usuarios").document(userId).get().await()
            if (document.exists() && document.getBoolean("carteira_digital") == true) {
                usuario = document.toObject(Usuario::class.java)
            } else {
                errorMessage = "Carteirinha ainda não cadastrada."
            }
        } catch (e: Exception) {
            errorMessage = "Erro ao buscar dados: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fundo com imagem borrada
        Image(
            painter = painterResource(id = R.drawable.imghome_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(12.dp),
            contentScale = ContentScale.Crop
        )

        // Overlay escuro para melhorar a legibilidade
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(color = Color.White)
                errorMessage != null -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(errorMessage!!, fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("carteirinhaScreen/$userId") }) {
                        Text("Criar Carteirinha")
                    }
                }
                usuario != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 📌 Cabeçalho do CIPTEA
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_meutea),
                                contentDescription = "Fita Autismo",
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Carteirinha pessoal",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Registro interno do MeuTEA — não substitui a CIPTEA emitida pelo órgão competente.",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }

                        // 📌 Exibição dos Dados na Carteirinha
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                ExibirDadoHorizontal("NOME", usuario?.nome)
                                ExibirDadoHorizontal("CPF", formatCpf(usuario?.cpf))
                                ExibirDadoHorizontal("RG", formatRg(usuario?.rg))
                                ExibirDadoHorizontal("UF", usuario?.estado)
                                ExibirDadoHorizontal("NASCIMENTO", formatDate(usuario?.nascimento))
                                ExibirDadoHorizontal("NATURALIDADE", usuario?.naturalidade)
                                ExibirDadoHorizontal("CID", usuario?.cid)
                                ExibirDadoHorizontal("SEXO", usuario?.genero)
                                ExibirDadoHorizontal("TIPO SANGUÍNEO", usuario?.tipoSanguineo)
                                ExibirDadoHorizontal("ENDEREÇO", usuario?.endereco)
                                ExibirDadoHorizontal("TELEFONE", formatPhone(usuario?.telefone))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 📌 Atendimento Prioritário
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Red, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "IDENTIFICAÇÃO PESSOAL — NÃO OFICIAL",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 📌 Botões de Ação
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(
                                onClick = {
                                    navController.navigate("editarCarteirinha/$userId")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(
                                    0xB473C70F
                                )
                                )                            ) {
                                Text("Editar", color = Color.White)
                            }

                            Button(
                                onClick = { showDeleteDialog = true }, // Ativa o diálogo de confirmação
                                colors = ButtonDefaults.buttonColors(containerColor = Color(
                                    0xE6DE1313
                                )
                                )                            ) {
                                Text("Excluir", color = Color.White)
                            }
                        }

                        deleteErrorMessage?.let { mensagem ->
                            Text(mensagem, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                navController.navigate("menuPrincipalScreen/$userId") {
                                    popUpTo("carteirinhaViewScreen/$userId") { inclusive = true } // Remove a tela da pilha
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDAA0C))

                        ){
                            Text("Voltar ao Menu", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // 🔹 Exibir o alerta de confirmação antes de excluir
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza de que deseja excluir sua carteirinha? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        userId?.let { id ->
                            isLoading = true
                            deleteErrorMessage = null
                            // Nunca apagar o documento da conta: excluir só os campos da carteirinha.
                            val limpeza = mapOf(
                                "carteira_digital" to false,
                                "cpf" to FieldValue.delete(),
                                "rg" to FieldValue.delete(),
                                "nascimento" to FieldValue.delete(),
                                "genero" to FieldValue.delete(),
                                "tipoSanguineo" to FieldValue.delete(),
                                "telefone" to FieldValue.delete(),
                                "endereco" to FieldValue.delete(),
                                "estado" to FieldValue.delete(),
                                "cid" to FieldValue.delete(),
                                "naturalidade" to FieldValue.delete(),
                                "estadoCivil" to FieldValue.delete()
                            )
                            db.collection("usuarios").document(id).update(limpeza)
                                .addOnSuccessListener {
                                    navController.navigate("menuPrincipalScreen/$id") {
                                        popUpTo("carteirinhaViewScreen/{usuarioId}") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                .addOnFailureListener {
                                    deleteErrorMessage = "Não foi possível excluir a carteirinha. Tente novamente."
                                }
                                .addOnCompleteListener {
                                    isLoading = false
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sim, Excluir", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}


// 🔹 Componente para exibir os dados lado a lado (horizontalmente)
@Composable
fun ExibirDadoHorizontal(titulo: String, valor: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$titulo:   ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0072CE), // Azul forte
        )
        Text(
            text = valor ?: "Não informado",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

// 🔹 Funções de Formatação
fun formatCpf(cpf: String?): String {
    return cpf?.replace(Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"), "$1.$2.$3-$4") ?: "Não informado"
}

fun formatDate(date: String?): String {
    return date?.replace(Regex("(\\d{2})(\\d{2})(\\d{4})"), "$1/$2/$3") ?: "Não informado"
}

fun formatPhone(phone: String?): String {
    return phone?.replace(Regex("(\\d{2})(\\d{5})(\\d{4})"), "($1) $2-$3") ?: "Não informado"
}

fun formatRg(rg: String?): String {
    return rg?.replace(Regex("(\\d{2})(\\d{3})(\\d{3})"), "$1.$2.$3") ?: "Não informado"
}
