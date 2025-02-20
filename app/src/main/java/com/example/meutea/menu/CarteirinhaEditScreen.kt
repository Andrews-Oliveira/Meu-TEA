package com.example.meutea.menu

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore

// Função para formatar o telefone (máscara)
// No arquivo CarteirinhaEditScreen.kt
fun formatTelefoneEdit(telefone: String): String {
    return telefone.replace(Regex("(\\d{2})(\\d{4})(\\d{4})"), "($1) $2-$3")
}


// Função para remover a máscara (apenas números)
fun removeTelefoneMask(telefone: String): String {
    return telefone.replace(Regex("[^0-9]"), "")
}

@Composable
fun CarteirinhaEditScreen(navController: NavController, userId: String) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(userId) {
        FirebaseFirestore.getInstance().collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                usuario = document.toObject(Usuario::class.java)
            }
    }

    usuario?.let { user ->
        var nome by remember { mutableStateOf(user.nome) }
        var telefone by remember { mutableStateOf(user.telefone) }
        var endereco by remember { mutableStateOf(user.endereco) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Editar Carteirinha", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Nome
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Telefone com máscara
            OutlinedTextField(
                value = formatTelefoneEdit(telefone), // Use a nova função renomeada
                onValueChange = {
                    telefone = it // O valor é armazenado sem máscara
                },
                label = { Text("Telefone") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(8.dp))

            // Campo Endereço
            OutlinedTextField(
                value = endereco,
                onValueChange = { endereco = it },
                label = { Text("Endereço") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para salvar
            Button(
                onClick = {
                    // Remover a máscara antes de salvar
                    val telefoneSemMascara = removeTelefoneMask(telefone)

                    FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                        .update(mapOf("nome" to nome, "telefone" to telefoneSemMascara, "endereco" to endereco))
                        .addOnSuccessListener {
                            navController.popBackStack()
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072CE))
            ) {
                Text("Salvar Alterações", color = Color.White)
            }
        }
    }
}
