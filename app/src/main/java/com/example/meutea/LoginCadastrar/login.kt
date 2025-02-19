package com.example.meutea.LoginCadastrar

import DataSource
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
import kotlinx.coroutines.launch

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

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Image(
                painter = painterResource(id = R.mipmap.ic_meutea),
                contentDescription = "Logo MeuTEA",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Boas-vindas!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Faça login com seu email cadastrado",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )

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

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = lembrarMe.value,
                    onCheckedChange = { lembrarMe.value = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFFFEB3B),
                        uncheckedColor = Color.LightGray
                    )
                )
                Text(
                    text = "Permanecer logado",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão de login
            Button(
                onClick = {
                    if (email.value.isNotEmpty() && senha.value.isNotEmpty()) {
                        // Chama a função de login dentro da coroutine
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

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de cadastro
            Button(
                onClick = {
                    onCadastrarClicked() // Chama a função passada por parâmetro
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("CRIAR CONTA", color = Color.White)
            }
        }
    }
}

// Função de login, agora sem @Composable
suspend fun LoginWithFirebase(email: String, senha: String, navController: NavController, context: Context) {
    try {
        val dataSource = DataSource()
        val user = dataSource.signIn(email, senha)
        if (user != null) {
            navController.navigate("MenuPrincipalScreen")
        } else {
            Toast.makeText(context, "Email ou senha inválidos!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Erro ao fazer login: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()  // Isso ajudará a identificar o erro no log
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
