package com.example.meutea.LoginCadastrar

import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.R

//@OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.ui.text.font.FontFamily

@Composable
fun LoginScreen(
    navController: NavController,
    onBackClicked: () -> Unit,
    onContinuarClicked: (String, String) -> Unit,
    onCadastrarClicked: () -> Unit
) {
    // Estados para os campos de texto
    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }

    // Defina um TextStyle personalizado
    val textStyle = TextStyle(
        fontFamily = FontFamily.Default, // Use a fonte desejada
        fontWeight = FontWeight.Normal,  // Pode ser Bold, Medium, etc.
        fontSize = 16.sp,                // Tamanho da fonte
        color = Color.Black              // Cor do texto
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0ABBDE), Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botão de voltar
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back_foreground),
                    contentDescription = "Voltar",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Imagem central
            Image(
                painter = painterResource(id = R.mipmap.ic_meutea),
                contentDescription = "Logo MeuTEA",
                modifier = Modifier
                    .size(240.dp, 230.dp)
                    .padding(top = 32.dp)
            )

            // Título "Boas vindas!"
            Text(
                text = "Boas vindas!",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xE6000000),
                modifier = Modifier.padding(top = 16.dp)
            )

            // Texto descritivo
            Text(
                text = "Faça o seu login com o email cadastrado para acessar o aplicativo",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
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
                // Campo Email
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.email_24),
                            contentDescription = "Email"
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = textStyle // Aplica o TextStyle personalizado
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Senha
                OutlinedTextField(
                    value = senha.value,
                    onValueChange = { senha.value = it },
                    label = { Text("Senha") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.lock_24),
                            contentDescription = "Senha"
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = textStyle // Aplica o TextStyle personalizado
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botão "CONTINUAR"
            Button(
                onClick = {
                    onContinuarClicked(email.value, senha.value)
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(65.dp)
                    .padding(top = 15.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF42B4E9)
                )
            ) {
                Text(
                    text = "CONTINUAR",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            // Botão "CADASTRAR"
            Button(
                onClick = onCadastrarClicked,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(65.dp)
                    .padding(top = 15.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5D8C4C)
                )
            ) {
                Text(
                    text = "CADASTRAR",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()

    LoginScreen(
        navController = navController,
        onBackClicked = {
            println("Voltar clicado")
        },
        onContinuarClicked = { email, senha ->
            println("Continuar clicado: $email, $senha")
        },
        onCadastrarClicked = {
            println("Cadastrar clicado")
        }
    )
}