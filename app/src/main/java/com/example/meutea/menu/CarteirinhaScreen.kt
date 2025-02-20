package com.example.meutea.menu

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.meutea.menu.edits.EditableTextFieldRow
import com.example.meutea.models.Usuario
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage


@Composable
fun CarteirinhaScreen(navController: NavController) {
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
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    val db = Firebase.firestore
    val storage = Firebase.storage.reference

    fun formatInput(input: String, maxLength: Int): String {
        return input.take(maxLength) // Limita o tamanho do texto
    }

    fun formatCpfInput(input: String): String {
        return input.replace(Regex("[^\\d]"), "").take(11)
            .replace(Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"), "$1.$2.$3-$4")
    }

    fun formatTelefoneInput(input: String): String {
        return input.replace(Regex("[^\\d]"), "").take(11)
            .replace(Regex("(\\d{2})(\\d{5})(\\d{4})"), "($1) $2-$3")
    }

    fun formatNascimentoInput(input: String): String {
        return input.replace(Regex("[^\\d]"), "").take(8) // Limita a 8 dígitos (ddmmyyyy)
            .replace(Regex("(\\d{2})(\\d{2})(\\d{4})"), "$1/$2/$3") // Formata como dd/mm/aaaa
    }


    fun validarCampos(): Boolean {
        return nome.isNotBlank() && cpf.length == 14 && rg.isNotBlank() && nascimento.isNotBlank() &&
                naturalidade.isNotBlank() && uf.isNotBlank() && cid.isNotBlank() && tipoSanguineo.isNotBlank() &&
                endereco.isNotBlank() && telefone.length == 15
    }

    fun salvarDadosNoFirestore() {
        if (!validarCampos()) {
            errorMessage = "Preencha todos os campos corretamente!"
            return
        }

        if (imageUri != null) {
            val fileName = "profile_pics/${System.currentTimeMillis()}.jpg"
            val fileRef = storage.child(fileName)

            fileRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
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
                            imageUrl = uri.toString()
                        )

                        db.collection("usuarios")
                            .add(usuario)
                            .addOnSuccessListener { docRef ->
                                navController.navigate("carteirinhaViewScreen/${docRef.id}")
                            }
                            .addOnFailureListener { e ->
                                errorMessage = "Erro ao salvar dados: ${e.message}"
                            }
                    }
                }
                .addOnFailureListener { e ->
                    errorMessage = "Erro ao fazer upload da imagem: ${e.message}"
                }
        } else {
            errorMessage = "Selecione uma imagem!"
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0072CE))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.LightGray, CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberImagePainter(data = imageUri),
                        contentDescription = "Imagem do Perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = "Adicionar Foto", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072CE))
            ) {
                Text(text = "Avançar", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}
