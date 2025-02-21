package com.example.meutea.LoginCadastrar

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meutea.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onBackClicked: () -> Unit,
    onContinuarClicked: (String, String, Boolean) -> Unit,
    onCadastrarClicked: () -> Unit
) {
    val context = LocalContext.current
    val email = rememberSaveable { mutableStateOf("") }
    val senha = rememberSaveable { mutableStateOf("") }
    val lembrarMe = rememberSaveable { mutableStateOf(false) }
    val showResetPasswordDialog = remember { mutableStateOf(false) }
    var isLinkPressed by remember { mutableStateOf(false) } // Estado para mudar cor do link

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fundo desfocado
        Image(
            painter = painterResource(id = R.drawable.img_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            contentScale = ContentScale.Crop
        )

        // Sobreposição escura para melhorar a legibilidade
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()), // Permite rolagem
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f)) // Ajuste superior

            // Logo responsivo
            Image(
                painter = painterResource(id = R.mipmap.ic_meutea),
                contentDescription = "Logo MeuTEA",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f) // Mantém proporção quadrada
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Título
            Text(
                text = "Boas-vindas!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Faça login com seu email cadastrado",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Campo de e-mail
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email", color = Color.White) },
                textStyle = TextStyle(color = Color.White),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de senha
            OutlinedTextField(
                value = senha.value,
                onValueChange = { senha.value = it },
                label = { Text("Senha", color = Color.White) },
                textStyle = TextStyle(color = Color.White),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Link "Esqueci minha senha" que muda de cor ao pressionar
            TextButton(
                onClick = {
                    isLinkPressed = true
                    showResetPasswordDialog.value = true
                }
            ) {
                Text(
                    text = "Esqueci minha senha",
                    color = if (isLinkPressed) Color(0xFF64B5F6) else Color.White,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.4f)) // **Menos espaço para os botões subirem**

            // Botão de login
            Button(
                onClick = {
                    if (email.value.isNotEmpty() && senha.value.isNotEmpty()) {
                        coroutineScope.launch {
                            LoginWithFirebase(
                                email = email.value,
                                senha = senha.value,
                                navController = navController,
                                context = context
                            )
                        }
                    } else {
                        Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xABFFEB3B))
            ) {
                Text("LOGIN", color = Color.White)
            }

            // **Texto abaixo do botão de login incentivando a criação de conta**
            Text(
                text = "Ainda não tem uma conta? Crie uma agora!",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Botão de cadastro
            Button(
                onClick = { onCadastrarClicked() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("CRIAR CONTA", color = Color.White)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Diálogo para redefinir senha
        if (showResetPasswordDialog.value) {
            ResetPasswordDialog(
                context = context,
                onDismiss = {
                    showResetPasswordDialog.value = false
                    isLinkPressed = false
                }
            )
        }
    }
}
suspend fun LoginWithFirebase(email: String, senha: String, navController: NavController, context: Context) {
    try {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        // 🔹 Tenta autenticar o usuário
        val user = try {
            auth.signInWithEmailAndPassword(email, senha).await().user
        } catch (e: Exception) {
            Log.e("LoginDebug", "Erro ao autenticar: ${e.message}")
            Toast.makeText(context, "Email ou senha incorretos!", Toast.LENGTH_SHORT).show()
            return
        }

        if (user != null) {
            Log.d("LoginDebug", "Usuário autenticado com sucesso: ${user.email}, UID: ${user.uid}")

            // 🔹 Verifica se o usuário já existe no Firestore
            val userDocRef = db.collection("usuarios").document(user.uid)
            val userDoc = userDocRef.get().await()

            if (!userDoc.exists()) {
                Log.e("LoginDebug", "Usuário não encontrado no Firestore, criando documento...")

                // Criar documento do usuário no Firestore com carteira_digital como false
                val novoUsuario = mapOf(
                    "uid" to user.uid,
                    "email" to user.email,
                    "nome" to (user.displayName ?: ""),
                    "carteira_digital" to false
                )

                db.collection("usuarios").document(user.uid).set(novoUsuario).await()
                Log.d("LoginDebug", "Usuário salvo no Firestore.")
            }

            // 🔹 Verificar se o usuário já preencheu a carteira digital
            val temCarteira = userDoc.getBoolean("carteira_digital") ?: false

            // ✅ Se já tiver carteira digital, vai para `CarteirinhaViewScreen`
            if (temCarteira) {
                // Se o usuário tem uma conta, sempre vai para o Menu Principal primeiro
                Log.d("LoginDebug", "Usuário autenticado. Redirecionando para MenuPrincipalScreen.")
                navController.navigate("menuPrincipalScreen/${user.uid}") {
                    popUpTo("loginScreen") { inclusive = true } // Remove a tela de login da pilha
                }

            } else {
                // ❌ Se não tiver carteira digital, ele continua indo para `MenuPrincipalScreen`
                Log.d("LoginDebug", "Usuário sem carteira, indo para MenuPrincipalScreen.")
                navController.navigate("menuPrincipalScreen/${user.uid}") {
                    popUpTo("loginScreen") { inclusive = true }
                }
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Erro inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
        Log.e("LoginDebug", "Erro inesperado: ${e.message}")
    }
}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(
        navController = navController,
        onBackClicked = {},
        onContinuarClicked = { _, _, _ -> },
        onCadastrarClicked = {}
    )
}

// Função para enviar link de redefinição de senha via Firebase
@Composable
fun ResetPasswordDialog(context: Context, onDismiss: () -> Unit) {
    val email = rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Redefinir Senha") },
        text = {
            Column {
                Text("Digite seu email para receber o link de redefinição de senha.")
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.value)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Email de redefinição enviado!", Toast.LENGTH_LONG).show()
                            onDismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erro: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
            ) {
                Text("Enviar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
