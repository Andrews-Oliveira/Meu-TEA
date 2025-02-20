package com.example.meutea.menu

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.menu.edits.EditableTextFieldRow
import com.example.meutea.models.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun CarteirinhaScreen(navController: NavController) {
    // Estados dos campos
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var rg by remember { mutableStateOf("") }
    var nascimento by remember { mutableStateOf("") }
    var naturalidade by remember { mutableStateOf("") }
    var uf by remember { mutableStateOf("") }
    var cid by remember { mutableStateOf("") }
    var tipoSanguineo by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val db = Firebase.firestore

    // Funções de formatação dos campos
    fun formatInput(input: String, maxLength: Int): String = input.take(maxLength)

    fun formatCpfInput(input: String): String =
        input.replace(Regex("[^\\d]"), "").take(11)
            .replace(Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"), "$1.$2.$3-$4")

    fun formatTelefoneInput(input: String): String =
        input.replace(Regex("[^\\d]"), "").take(11)
            .replace(Regex("(\\d{2})(\\d{5})(\\d{4})"), "($1) $2-$3")

    fun formatNascimentoInput(input: String): String =
        input.replace(Regex("[^\\d]"), "").take(8)
            .replace(Regex("(\\d{2})(\\d{2})(\\d{4})"), "$1/$2/$3")

    // Validação dos campos
    fun validarCampos(): Boolean {
        return nome.isNotBlank() &&
                cpf.length == 14 &&
                rg.isNotBlank() &&
                nascimento.isNotBlank() &&
                naturalidade.isNotBlank() &&
                uf.isNotBlank() &&
                cid.isNotBlank() &&
                tipoSanguineo.isNotBlank() &&
                endereco.isNotBlank() &&
                telefone.length == 15
    }

    // Função para salvar os dados no Firestore
    fun salvarDadosNoFirestore() {
        if (!validarCampos()) {
            errorMessage = "Preencha todos os campos corretamente!"
            return
        }
        isLoading = true
        errorMessage = null

        val usuario = Usuario(
            nome = nome,
            cpf = cpf,
            rg = rg,
            nascimento = nascimento,
            naturalidade = naturalidade,
            uf = uf,
            cid = cid,
            tipoSanguineo = tipoSanguineo,
            endereco = endereco,
            telefone = telefone,
            imageUrl = "" // Sem imagem, pode ficar vazio
        )

        db.collection("usuarios")
            .add(usuario)
            .addOnSuccessListener { docRef ->
                Log.d("Firestore", "Usuário salvo com sucesso com ID: ${docRef.id}")
                navController.navigate("CarteirinhaViewScreen/${docRef.id}")
            }
            .addOnFailureListener { e ->
                errorMessage = "Erro ao salvar dados: ${e.message}"
            }
            .addOnCompleteListener {
                isLoading = false
            }
    }

    // Layout utilizando Jetpack Compose
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0072CE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EditableTextFieldRow("Nome:", nome, { nome = formatInput(it, 50) })
                    EditableTextFieldRow("CPF:", cpf, { cpf = formatCpfInput(it) }, isNumeric = true)
                    EditableTextFieldRow("RG:", rg, { rg = formatInput(it, 15) }, isNumeric = true)
                    EditableTextFieldRow("Nascimento:", nascimento, { nascimento = formatNascimentoInput(it) })
                    EditableTextFieldRow("Naturalidade:", naturalidade, { naturalidade = formatInput(it, 20) })
                    EditableTextFieldRow("UF:", uf, { uf = formatInput(it, 2) })
                    EditableTextFieldRow("CID:", cid, { cid = formatInput(it, 20) })
                    EditableTextFieldRow("Tipo Sanguíneo:", tipoSanguineo, { tipoSanguineo = formatInput(it, 4) })
                    EditableTextFieldRow("Endereço:", endereco, { endereco = formatInput(it, 50) })
                    EditableTextFieldRow("Telefone:", telefone, { telefone = formatTelefoneInput(it) }, isNumeric = true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { salvarDadosNoFirestore() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072CE))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(text = "Avançar", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}