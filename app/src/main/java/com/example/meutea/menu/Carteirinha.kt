package com.example.meutea.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meutea.R

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
    var filiacao by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var emitidoEm by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0072CE))
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Adiciona rolagem
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            Text(
                text = "CIPTEA",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "CARTEIRA DE IDENTIFICAÇÃO DA PESSOA COM TEA",
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    EditableTextFieldRow("Nome:", nome) { nome = it }
                    EditableTextFieldRow("CPF:", cpf) { cpf = it }
                    EditableTextFieldRow("RG:", rg) { rg = it }
                    EditableTextFieldRow("Nascimento:", nascimento) { nascimento = it }
                    EditableTextFieldRow("Naturalidade:", naturalidade) { naturalidade = it }
                    EditableTextFieldRow("UF:", uf) { uf = it }
                    EditableTextFieldRow("CID:", cid) { cid = it }
                    EditableTextFieldRow("Tipo Sanguíneo:", tipoSanguineo) { tipoSanguineo = it }
                    EditableTextFieldRow("Endereço:", endereco) { endereco = it }
                    EditableTextFieldRow("Filiação:", filiacao) { filiacao = it }
                    EditableTextFieldRow("Telefone:", telefone) { telefone = it }
                    EditableTextFieldRow("Emitido em:", emitidoEm) { emitidoEm = it }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rodapé
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ATENDIMENTO PRIORITÁRIO",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vivaautismo.com.br",
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "DOCUMENTO VÁLIDO POR 8 ANOS",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun EditableTextFieldRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(0.3f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CarteirinhaScreenPreview() {
    val navController = rememberNavController()
    CarteirinhaScreen(navController = navController)
}