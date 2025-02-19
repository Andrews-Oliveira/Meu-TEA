package com.example.meutea.LoginCadastrar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
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

@Composable
fun LoginScreen(
    navController: NavController,
    onBackClicked: () -> Unit,
    onContinuarClicked: (String, String) -> Unit,
    onCadastrarClicked: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }

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

        // Overlay escuro para reduzir a claridade
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
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = senha.value,
                onValueChange = { senha.value = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onContinuarClicked(email.value, senha.value) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xABFFEB3B))
            ) {
                Text("LOGIN")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { onCadastrarClicked() }
            ) {
                Text("Criar uma conta", color = Color.White, fontSize = 16.sp)
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
        onBackClicked = {},
        onContinuarClicked = { _, _ -> },
        onCadastrarClicked = {}
    )
}
