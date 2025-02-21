package com.example.meutea.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meutea.R

@Composable
fun WelcomeScreen(navController: NavController) {
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f)) // Espaço antes do título (quanto menor, mais para cima fica o título)

            // Título maior para melhor visibilidade
            Text(
                text = "Bem-vindo ao",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Meu TEA",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Ajuste do tamanho do logo
            Image(
                painter = painterResource(id = R.mipmap.ic_background_meutea_foreground),
                contentDescription = "Logo MeuTEA",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Texto explicativo com melhor espaçamento
            Text(
                text = "Um aplicativo de comunicação alternativa para ajudar crianças e adolescentes com autismo a expressarem seus desejos, emoções e necessidades de forma simples e eficaz.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp)
            )

            // Esse Spacer define o espaço entre o texto e os botões
            // **Diminua esse valor para subir os botões**
            Spacer(modifier = Modifier.weight(0.5f)) // Antes estava 0.8f, agora está 0.5f para subir os botões

            // Botões reposicionados e ajustados
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 70.dp), // Reduzi a margem inferior para subir um pouco mais
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate("LoginScreen") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                        .height(45.dp), // Reduzi a altura dos botões para melhor equilíbrio
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDBB600) // Amarelo mais vibrante
                    )
                ) {
                    Text(text = "Pular", fontSize = 16.sp, color = Color.White)
                }

                Button(
                    onClick = { navController.navigate("welcomeScreen2") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                        .height(45.dp), // Reduzi a altura dos botões para melhor equilíbrio
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007BBB) // Azul mais vibrante
                    )
                ) {
                    Text(text = "Próximo", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    val navController = rememberNavController()
    WelcomeScreen(navController)
}
