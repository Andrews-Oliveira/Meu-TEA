package com.example.meutea.menu

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun CarteirinhaViewScreen(navController: NavController) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("usuarios").limit(1).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    usuario = documents.documents[0].toObject(Usuario::class.java)
                } else {
                    errorMessage = "Nenhuma carteirinha encontrada."
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Erro ao buscar dados: ${e.message}"
                isLoading = false
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0072CE)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else if (errorMessage != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Nenhuma carteirinha encontrada.",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("CarteirinhaScreen") }, // Ir para criar nova carteirinha
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Criar Carteirinha", fontSize = 16.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Voltar ao Menu", fontSize = 16.sp, color = Color.White)
                }
            }
        } else {
            usuario?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Exibir a carteirinha
                    Card(
                        modifier = Modifier
                            .width(350.dp)
                            .height(220.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Text(text = "Nome: ${user.nome}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "CPF: ${user.cpf}", fontSize = 14.sp)
                            Text(text = "RG: ${user.rg}", fontSize = 14.sp)
                            Text(text = "Nascimento: ${user.nascimento}", fontSize = 14.sp)
                            Text(text = "Naturalidade: ${user.naturalidade} - ${user.uf}", fontSize = 14.sp)
                            Text(text = "CID: ${user.cid}  |  Tipo Sanguíneo: ${user.tipoSanguineo}", fontSize = 14.sp)
                            Text(text = "Endereço: ${user.endereco}", fontSize = 14.sp)
                            Text(text = "Telefone: ${user.telefone}", fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Atendimento Prioritário",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Red)
                                    .padding(vertical = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botões de Ação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { navController.navigate("editarCarteirinha/${user.cpf}") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F9ACC))
                        ) {
                            Text("Editar", fontSize = 16.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                db.collection("usuarios").whereEqualTo("cpf", user.cpf)
                                    .get().addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            db.collection("usuarios").document(document.id).delete()
                                        }
                                        navController.navigate("carteirinhaViewScreen") // Atualizar tela
                                    }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Excluir", fontSize = 16.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Voltar ao Menu", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
}