package com.example.meutea.LoginCadastrar

import android.content.Context
import android.util.Log
import android.widget.Toast
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meutea.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Tela de login sem personagem, com marca acessível e formulário legível.
 * A persistência da conta é gerenciada pelo FirebaseAuth e pela SessionGate.
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
    var mostrarSenha by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var resetPassword by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { imageContext ->
                ImageView(imageContext).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageResource(R.drawable.tea_login_abstract)
                    importantForAccessibility = android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xC9003264),
                        Color(0x4A005B99),
                        Color(0x06054D87),
                        Color(0x72023666)
                    )
                )
            )
        )
        Column(
            modifier = Modifier.fillMaxSize().imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 19.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.linearGradient(
                            listOf(Color(0xFF00C6C4), Color(0xFF1972C7))
                        ))
                        .border(1.5.dp, Color.White, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MT", color = Color.White, fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.size(13.dp))
                Column {
                    Text("Meu TEA",
                        color = Color.White, fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold)
                    Text("Comunicação • Rotina • Autonomia",
                        color = Color.White, style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(52.dp))
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(
                    1.4.dp, Color.White.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xF9F7FCFF))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    Box(
                        Modifier.fillMaxWidth().height(7.dp)
                            .background(Brush.horizontalGradient(listOf(
                                Color(0xFF00B7C4), Color(0xFF217FCB), Color(0xFF9A84F2)
                            )))
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(
                            horizontal = 20.dp, vertical = 17.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Bem-vindo de volta",
                            color = Color(0xFF113761),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold)
                        Text("Entre para continuar de onde parou.",
                            color = Color(0xFF46617B),
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        Text("E-mail", color = Color(0xFF16375B),
                            fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; erro = null },
                            placeholder = { Text("seuemail@exemplo.com") },
                            label = { Text("Seu e-mail") },
                            leadingIcon = { Text("✉", fontSize = 22.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0077AE),
                                unfocusedBorderColor = Color(0xFF91B7CA),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color(0xFF113761),
                                unfocusedTextColor = Color(0xFF113761)
                            )
                        )
                        Text("Senha", color = Color(0xFF16375B),
                            fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = senha,
                            onValueChange = { senha = it; erro = null },
                            label = { Text("Sua senha") },
                            placeholder = { Text("Digite sua senha") },
                            leadingIcon = { Text("●", color = Color(0xFF5481AD)) },
                            trailingIcon = {
                                TextButton(onClick = { mostrarSenha = !mostrarSenha }) {
                                    Text(if (mostrarSenha) "Ocultar" else "Ver",
                                        color = Color(0xFF006D9F))
                                }
                            },
                            visualTransformation = if (mostrarSenha) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0077AE),
                                unfocusedBorderColor = Color(0xFF91B7CA),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color(0xFF113761),
                                unfocusedTextColor = Color(0xFF113761)
                            )
                        )
                        TextButton(
                            onClick = { resetPassword = true },
                            modifier = Modifier.align(Alignment.End)
                        ) { Text("Esqueci minha senha", color = Color(0xFF006E9F)) }
                        erro?.let { message ->
                            Text(message, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                        Button(
                            onClick = {
                                if (email.isBlank() || senha.isBlank()) {
                                    erro = "Informe seu e-mail e sua senha."
                                } else {
                                    coroutineScope.launch {
                                        isLoading = true
                                        try {
                                            LoginWithFirebase(
                                                email.trim(), senha, navController, context
                                            )
                                        } finally { isLoading = false }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            shape = RoundedCornerShape(17.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF006E91)
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White, strokeWidth = 2.dp
                                )
                            } else {
                                Text("Entrar no Meu TEA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                        OutlinedButton(
                            onClick = onCadastrarClicked,
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            shape = RoundedCornerShape(17.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF0A5687)
                            )
                        ) {
                            Text("Criar minha conta", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Ao entrar, sua sessão permanece ativa neste aparelho. Use Sair quando desejar.",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.background(
                    Color(0xA9003264), RoundedCornerShape(12.dp)
                ).padding(10.dp)
            )
            TextButton(onClick = onBackClicked) {
                Text("Conhecer o Meu TEA", color = Color.White)
            }
            Spacer(Modifier.height(24.dp))
        }
        if (resetPassword) {
            ResetPasswordDialog(context = context, onDismiss = { resetPassword = false })
        }
    }
}

suspend fun LoginWithFirebase(
    email: String, senha: String, navController: NavController, context: Context
) {
    val auth = FirebaseAuth.getInstance()
    val user = try {
        auth.signInWithEmailAndPassword(email, senha).await().user
    } catch (e: Exception) {
        Log.w("MeuTeaLogin", "Falha de autenticação", e)
        Toast.makeText(context, "Não foi possível entrar. Confira e-mail, senha e conexão.",
            Toast.LENGTH_LONG).show()
        return
    }
    if (user == null) {
        Toast.makeText(context, "Não foi possível autenticar sua conta.",
            Toast.LENGTH_LONG).show()
        return
    }

    // O usuário autenticado entra independentemente da conexão com Firestore.
    // Isso evita exigir a senha novamente quando o cache está disponível.
    navController.navigate("menuPrincipalScreen/${user.uid}") {
        popUpTo("loginScreen") { inclusive = true }
        launchSingleTop = true
    }

    // Reparação opcional de perfil, sem bloquear acesso ao próprio app.
    try {
        val userDocRef = FirebaseFirestore.getInstance().collection("usuarios").document(user.uid)
        val doc = userDocRef.get().await()
        if (!doc.exists()) {
            userDocRef.set(mapOf(
                "uid" to user.uid,
                "email" to (user.email ?: ""),
                "nome" to (user.displayName ?: ""),
                "carteira_digital" to false,
                "tema_visual" to "masculino"
            )).await()
        }
    } catch (e: Exception) {
        Log.w("MeuTeaLogin", "Perfil não sincronizado (sessão continua autenticada)", e)
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
