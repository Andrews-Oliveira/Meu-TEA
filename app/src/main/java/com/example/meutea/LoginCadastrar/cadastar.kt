package com.example.meutea.LoginCadastrar

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meutea.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastrarScreen(
    navController: NavController,
    onBackClicked: () -> Unit,
    onCadastrarClicked: (String, String, String) -> Unit
) {
    val nome = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagem de fundo com o efeito de blur
        Image(
            painter = painterResource(id = R.drawable.img_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),  // Aplica o efeito de borrado
            contentScale = ContentScale.Crop
        )

        // Camada escura com fundo borrado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Camada preta com opacidade para dar o efeito escuro
        )

        // Conteúdo da tela
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botão "Voltar" mais compacto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp), // Aumenta o espaço do topo
                contentAlignment = Alignment.CenterStart
            ) {
                Button(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .padding(start = 1.dp) // Pequeno espaço à esquerda
                        .height(37.dp)
                        .fillMaxWidth(0.3f), // Tamanho reduzido
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xABFFEB3B)),
                    shape = MaterialTheme.shapes.medium // Bordas arredondadas
                ) {
                    Text("Voltar", fontSize = 16.sp, color = Color.White)
                }
            }

            // Logo do app
            Image(
                painter = painterResource(id = R.mipmap.ic_meutea),
                contentDescription = "Logo MeuTEA",
                modifier = Modifier
                    .size(240.dp, 230.dp)
                    .padding(top = 16.dp)
            )

            // Título
            Text(
                text = "Boas vindas!",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Texto descritivo
            Text(
                text = "Faça o seu cadastro para acessar o aplicativo",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp, vertical = 16.dp)
            )

            // Campos de texto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp, vertical = 16.dp)
            ) {
                // Campo Nome
                OutlinedTextField(
                    value = nome.value,
                    onValueChange = { nome.value = it },
                    label = { Text("Nome", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo Email
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo Senha
                OutlinedTextField(
                    value = senha.value,
                    onValueChange = { senha.value = it },
                    label = { Text("Senha", color = Color.White) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botão de Cadastro
                Button(
                    onClick = {
                        if (nome.value.isEmpty() || email.value.isEmpty() || senha.value.isEmpty()) {
                            Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                        } else {
                            coroutineScope.launch {
                                val auth = FirebaseAuth.getInstance()
                                try {
                                    auth.createUserWithEmailAndPassword(email.value, senha.value).await()
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                        onCadastrarClicked(nome.value, email.value, senha.value)
                                        navController.popBackStack() // Volta para a tela anterior
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xABFFEB3B)),
                    shape = RoundedCornerShape(12.dp) // Bordas arredondadas
                ) {
                    Text("CRIAR", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun CadastrarScreenPreview() {
    val navController = rememberNavController()
    CadastrarScreen(
        navController = navController,
        onBackClicked = {
            println("Voltar clicado")
        },
        onCadastrarClicked = { nome, email, senha ->
            println("Cadastro realizado com sucesso! Nome: $nome, Email: $email")
        }
    )
}
