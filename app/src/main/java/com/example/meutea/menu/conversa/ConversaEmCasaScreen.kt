package com.example.meutea.menu.conversa


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.R

@Composable
fun ConversaEmCasaScreen(navController: NavController) { // ✅ `navController` é obrigatório
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0DCF3))
            .padding(16.dp)
    ) {
        TopBar(navController)
        Spacer(modifier = Modifier.height(16.dp))
        OptionsGrid(navController)
    }
}

// ✅ Topo da Tela com Botão de Voltar
@Composable
fun TopBar(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { navController.popBackStack() }) { // ✅ Agora não é `null`
            Icon(
                painter = painterResource(id = R.drawable.ic_back_foreground),
                contentDescription = "Voltar",
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Em Casa",
            fontSize = 32.sp,
            color = Color.Black
        )
    }
}

// ✅ Cards de Opções (Agora navegam para detalhes)
@Composable
fun OptionsGrid(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OptionCard(navController, "GOSTEI", R.drawable.gostei)
        OptionCard(navController, "QUERO", R.drawable.quero)
        OptionCard(navController, "PEGUE", R.drawable.pegar)
        OptionCard(navController, "FAÇO", R.drawable.faco)
    }
}

// ✅ Cartão Individual com Navegação
@Composable
fun OptionCard(navController: NavController, text: String, imageRes: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFCDE0F9)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("conversaDetalhadaScreen/$text") // ✅ Agora navega corretamente
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = text, fontSize = 36.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = text,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}