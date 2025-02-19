package com.example.meutea.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meutea.R

@Composable
fun MenuPrincipalScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fundo com imagem borrada
        Image(
            painter = painterResource(id = R.drawable.imghome_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(12.dp),
            contentScale = ContentScale.Crop
        )

        // Overlay escuro para reduzir a claridade do fundo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_meutea),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp, 170.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bem-vindo",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botões de Menu
            MenuButton(
                text = "CARTEIRA DIGITAL",
                textColor = Color.White,
                iconId = R.drawable.outline_person_24,
                color = Color(0xFF2F9ACC),
                onClick = { navController.navigate("CarteirinhaScreen") }
            )

            MenuButton(
                text = "CONVERSA",
                textColor = Color.White,
                iconId = R.drawable.baseline_chat_24,
                color = Color(0xFF48AC51),
                onClick = { navController.navigate("conversa") }
            )

            MenuButton(
                text = "ROTINA",
                textColor = Color.White,
                iconId = R.drawable.baseline_today_24,
                color = Color(0xFFF1C27B),
                onClick = { navController.navigate("rotina") }
            )
        }
    }
}




@Composable
fun MenuButton(
    text: String,
    iconId: Int,
    color: Color,
    textColor: Color = Color.White, // Alterado para branco para melhor contraste
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = text,
                fontSize = 22.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMenuPrincipalScreen() {
    MenuPrincipalScreen(navController = rememberNavController())
}