package com.example.meutea.LoginCadastrar

import android.content.Context
import android.util.Log
import android.widget.Toast
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meutea.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * A marca Meu TEA é texto da interface, não parte da imagem.
 * O login usa fundo neutro e NÃO mostra personagem de qualquer tema.
 */
@Composable
fun LoginScreen(
    navController: NavController,
    onBackClicked: () -> Unit,
    onContinuarClicked: (String, String, Boolean) -> Unit,
    onCadastrarClicked: () -> Unit
) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var resetPassword by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // ImageView recebe XML shape, VectorDrawable e imagens PNG/JPG/WebP.
        // painterResource NÃO aceita <shape> e causava crash ao abrir o login.
        AndroidView(
            factory = { imageContext ->
                ImageView(imageContext).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageResource(R.drawable.tea_background_login)
                    contentDescription = null
                    importantForAccessibility = android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(46.dp))
            Text(
                "Meu TEA",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF073F74)
            )
            Text(
                "Comunicar, organizar e acolher.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF234D65),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(54.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Que bom ter você aqui!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C414C)
                    )
                    Text(
                        "Acesse sua conta para continuar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF38545C)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Seu e-mail") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        label = { Text("Sua senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(
                        onClick = { resetPassword = true },
                        modifier = Modifier.align(Alignment.End)
                    ) { Text("Esqueci minha senha") }
                    Button(
                        onClick = {
                            if (email.isBlank() || senha.isBlank()) {
                                Toast.makeText(context, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show()
                            } else {
                                coroutineScope.launch {
                                    isLoading = true
                                    try {
                                        LoginWithFirebase(
                                            email.trim(), senha, navController, context
                                        )
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(if (isLoading) "Entrando..." else "Entrar")
                    }
                    OutlinedButton(
                        onClick = onCadastrarClicked,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(15.dp),
                        enabled = !isLoading
                    ) { Text("Criar minha conta") }
                }
            }
            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onBackClicked) { Text("Voltar") }
            Spacer(Modifier.height(24.dp))
        }
        if (resetPassword) {
            ResetPasswordDialog(context = context, onDismiss = { resetPassword = false })
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
            Log.d("LoginDebug", "Usuário autenticado com sucesso.")

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
