package com.example.meutea.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.meutea.models.Usuario
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@Composable
fun CarteirinhaViewScreen(navController: NavController, usuarioId: String) {
    val db = Firebase.firestore
    var usuario by remember { mutableStateOf<Usuario?>(null) }

    // Carrega os dados do Firestore
    LaunchedEffect(usuarioId) {
        db.collection("usuarios").document(usuarioId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    usuario = document.toObject(Usuario::class.java)
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao recuperar dados: ${e.message}")
            }
    }

    if (usuario != null) {
        // Exibe os dados do usuário
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Nome: ${usuario?.nome}", fontSize = 18.sp)
            Text(text = "CPF: ${usuario?.cpf}", fontSize = 18.sp)
            Text(text = "RG: ${usuario?.rg}", fontSize = 18.sp)
            Text(text = "Nascimento: ${usuario?.nascimento}", fontSize = 18.sp)
            Text(text = "Naturalidade: ${usuario?.naturalidade}", fontSize = 18.sp)
            Text(text = "UF: ${usuario?.uf}", fontSize = 18.sp)
            Text(text = "CID: ${usuario?.cid}", fontSize = 18.sp)
            Text(text = "Tipo Sanguíneo: ${usuario?.tipoSanguineo}", fontSize = 18.sp)
            Text(text = "Endereço: ${usuario?.endereco}", fontSize = 18.sp)
            Text(text = "Telefone: ${usuario?.telefone}", fontSize = 18.sp)
            Image(painter = rememberImagePainter(data = usuario?.imageUrl), contentDescription = "Imagem do Perfil")
        }
    } else {
        // Exibe um carregamento enquanto os dados não são recuperados
        CircularProgressIndicator()
    }
}
