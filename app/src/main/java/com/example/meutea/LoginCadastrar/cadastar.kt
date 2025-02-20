package com.example.meutea.LoginCadastrar

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meutea.R
import com.example.meutea.datasource.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    // Instanciando o DataSource
    val dataSource = DataSource()

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagem de fundo com o efeito de blur
        Image(
            painter = painterResource(id = R.drawable.img_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            contentScale = ContentScale.Crop
        )

        // Camada escura com fundo borrado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
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
                    .padding(top = 32.dp, bottom = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Button(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .padding(start = 1.dp)
                        .height(37.dp)
                        .fillMaxWidth(0.3f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xABFFEB3B)),
                    shape = MaterialTheme.shapes.medium
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
                                try {
                                    val user = dataSource.signUp(nome.value, email.value, senha.value)
                                    if (user != null) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                            onCadastrarClicked(nome.value, email.value, senha.value)
                                            navController.popBackStack() // Volta para a tela anterior
                                        }
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
                    shape = RoundedCornerShape(12.dp)
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
