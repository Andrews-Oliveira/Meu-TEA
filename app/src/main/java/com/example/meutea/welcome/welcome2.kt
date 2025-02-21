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
fun WelcomeScreen2(navController: NavController) {
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

        // Sobreposição escura para melhorar contraste
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.2f)) // Adiciona espaço antes do logo

            // Logo ajustado para diferentes telas
            Image(
                painter = painterResource(id = R.mipmap.ic_meutea_24_foreground),
                contentDescription = "Logo MeuTEA",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Texto de boas-vindas
            Text(
                text = "Divirta-se enquanto aprende a se comunicar através de imagens e exercícios sociais.\n\nJunte-se a nós para crescer e aprender!",
                fontSize = 18.sp, // Ajustado para melhor leitura
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Imagem ilustrativa ajustada
            Image(
                painter = painterResource(id = R.mipmap.ic_imagetea_foreground),
                contentDescription = "Imagem MeuTEA",
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .aspectRatio(1.2f) // Mantém a proporção sem cortar
            )

            Spacer(modifier = Modifier.weight(0.6f)) // Ajuste dinâmico do espaço antes dos botões

            // Botões ajustados
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 55.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate("welcomeScreen") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                        .height(45.dp), // Altura ajustada para melhor clique
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xC60ABBDE) // Azul vibrante
                    )
                ) {
                    Text(text = "Voltar", fontSize = 16.sp, color = Color.White)
                }

                Button(
                    onClick = { navController.navigate("loginScreen") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                        .height(45.dp), // Altura ajustada para melhor clique
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xABFFEB3B) // Amarelo vibrante
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
private fun WelcomeScreen2Preview() {
    val navController = rememberNavController()
    WelcomeScreen2(navController)
}
