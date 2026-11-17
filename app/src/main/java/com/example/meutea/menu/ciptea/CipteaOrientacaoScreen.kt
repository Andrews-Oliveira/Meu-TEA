package com.example.meutea.menu.ciptea

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

// URL oficial da SEPcD Amazonas. Não existe integração de emissão na API deste app.
private const val ORIENTACAO_AM =
    "https://www.sepcd.am.gov.br/carteira-de-identificacao-da-pessoa-com-transtorno-do-espectro-autista-ciptea/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipteaOrientacaoScreen(navController: NavHostController) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val prefs = remember(context, uid) {
        context.getSharedPreferences("ciptea_local_" + (uid ?: "anonimo"), 0)
    }
    var documento by remember(uid) { mutableStateOf(prefs.getString("uri_documento", "") ?: "") }

    // O usuário escolhe seu documento PDF/imagem no armazenamento do dispositivo.
    // Guardamos SOMENTE a URI autorizada localmente. Não enviamos documentos para Firebase.
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                documento = uri.toString()
                prefs.edit().putString("uri_documento", documento).apply()
                Toast.makeText(context, "Arquivo vinculado neste aparelho.", Toast.LENGTH_LONG).show()
            } catch (e: SecurityException) {
                Toast.makeText(context, "Permissão negada para este arquivo.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun abrir(link: Uri, mime: String? = null) {
        try {
            val intent = if (mime == null) {
                Intent(Intent.ACTION_VIEW, link)
            } else {
                Intent(Intent.ACTION_VIEW).setDataAndType(link, mime)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Não foi possível abrir. Instale um visualizador compatível.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("CIPTEA e identificação") },
            navigationIcon = {
                TextButton(onClick = { navController.popBackStack() }) { Text("Voltar") }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Documento oficial, sem confusão",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold)
                Text(
                    "A CIPTEA é emitida pelo órgão público responsável. O Meu TEA não emite, valida nem substitui esse documento.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Solicitar no Amazonas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                        Text("1. Instale o aplicativo oficial indicado pela SEPcD: SASI.")
                        Text("2. Utilize o código SPCDAM no SASI.")
                        Text("3. Faça o cadastro e anexe os documentos exigidos.")
                        Text("4. Aguarde a orientação oficial para biometria e retirada.")
                        Text("Documentos geralmente exigidos: foto 3×4, RG/CIN, CPF, comprovante de residência, laudo médico e tipagem sanguínea. A lista pode mudar: confirme no órgão oficial.")
                        Button(onClick = { abrir(Uri.parse(ORIENTACAO_AM)) }) {
                            Text("Consultar orientações da SEPcD")
                        }
                    }
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Guardar minha CIPTEA já emitida",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                        Text("Selecione um PDF ou imagem oficial que você já possui. O arquivo continua no dispositivo, sem upload.")
                        OutlinedButton(
                            onClick = {
                                picker.launch(arrayOf("application/pdf", "image/jpeg", "image/png"))
                            },
                            enabled = uid != null
                        ) { Text(if (documento.isBlank()) "Selecionar arquivo" else "Trocar arquivo") }
                        if (documento.isNotBlank()) {
                            Button(onClick = {
                                val uri = Uri.parse(documento)
                                val mime = context.contentResolver.getType(uri) ?: "application/pdf"
                                abrir(uri, mime)
                            }) { Text("Abrir meu documento") }
                            TextButton(onClick = {
                                documento = ""
                                prefs.edit().remove("uri_documento").apply()
                            }) { Text("Desvincular do app") }
                        }
                        Text(
                            "Este vínculo é local e depende da permissão de acesso ao arquivo. " +
                                "Um novo aparelho não terá automaticamente uma cópia.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
