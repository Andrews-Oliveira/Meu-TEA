package com.example.meutea.menu.careiradigital

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.meutea.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private val ink = Color(0xFF153450)
private val secondaryInk = Color(0xFF48667B)
private val navy = Color(0xFF12395F)
private val teal = Color(0xFF087D86)
private val stroke = Color(0xFFCCE0EB)

/**
 * Cartão privado do Meu TEA: inspirado na organização visual de uma identificação,
 * mas SEM brasão, numeração oficial, alegação de validade ou QR de autenticação fictício.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteirinhaViewScreen(navController: NavController, userId: String?) {
    val context = LocalContext.current
    val uidAtual = FirebaseAuth.getInstance().currentUser?.uid
    val db = remember { FirebaseFirestore.getInstance() }
    val scope = rememberCoroutineScope()

    var usuario by remember(userId) { mutableStateOf<Usuario?>(null) }
    var carregando by remember(userId) { mutableStateOf(true) }
    var erro by remember(userId) { mutableStateOf<String?>(null) }
    var aviso by remember { mutableStateOf<String?>(null) }
    var excluirDialog by remember { mutableStateOf(false) }
    var mostrandoVerso by rememberSaveable(userId) { mutableStateOf(false) }
    var mostrarDetalhes by rememberSaveable(userId) { mutableStateOf(false) }
    var compartilharDialog by remember { mutableStateOf(false) }
    var excluindo by remember { mutableStateOf(false) }
    var fotoRevisao by remember(userId) { mutableIntStateOf(0) }
    var fotoCamera by remember { mutableStateOf<File?>(null) }
    var uriCamera by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(userId, uidAtual) {
        carregando = true
        erro = null
        if (userId.isNullOrBlank() || uidAtual != userId) {
            erro = "Faça login na conta correspondente para acessar esta identificação."
            carregando = false
            return@LaunchedEffect
        }
        try {
            val doc = db.collection("usuarios").document(userId).get().await()
            if (doc.exists() && doc.getBoolean("carteira_digital") == true) {
                usuario = doc.toObject(Usuario::class.java)
                if (usuario == null) erro = "Não foi possível ler os dados da identificação."
            } else {
                erro = "Você ainda não criou sua identificação pessoal."
            }
        } catch (e: Exception) {
            erro = "Não foi possível carregar a identificação. Verifique a conexão."
        } finally {
            carregando = false
        }
    }

    val foto by produceState<Bitmap?>(initialValue = null, userId, fotoRevisao) {
        value = if (userId != null && userId == uidAtual) {
            withContext(Dispatchers.IO) { FotoIdentificacao.ler(context, userId) }
        } else null
    }

    fun salvarFoto(uri: Uri, arquivoTemporario: File? = null) {
        val id = userId ?: return
        if (uidAtual != id) return
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    FotoIdentificacao.salvar(context, id, uri)
                } finally {
                    arquivoTemporario?.delete()
                }
            }
            if (result) {
                fotoRevisao++
                aviso = "Fotografia salva somente neste aparelho."
            } else {
                aviso = "Não foi possível salvar a imagem. Escolha outra fotografia."
            }
        }
    }

    val escolherFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        fotoSelecionada -> fotoSelecionada?.let { salvarFoto(it) }
    }

    val tirarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        sucesso ->
        val uri = uriCamera
        if (sucesso && uri != null) {
            salvarFoto(uri, fotoCamera)
        } else {
            fotoCamera?.delete()
            aviso = "A captura foi cancelada."
        }
        fotoCamera = null
        uriCamera = null
    }

    val referenciaLocal = remember(userId) {
        if (userId.isNullOrBlank()) "—"
        else {
            val bytes = MessageDigest.getInstance("SHA-256")
                .digest(userId.toByteArray(Charsets.UTF_8))
            "MT-" + bytes.take(5).joinToString("") {
                (it.toInt() and 0xFF).toString(16).padStart(2, '0')
            }.uppercase()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF1F6FA),
        topBar = {
            TopAppBar(
                title = { Text("Minha identificação", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Voltar", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = navy)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            when {
                carregando -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center), color = teal
                )
                erro != null -> Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(erro.orEmpty(), color = ink, style = MaterialTheme.typography.bodyLarge)
                    if (userId == uidAtual && !userId.isNullOrBlank()) {
                        Button(onClick = { navController.navigate("carteirinhaScreen/" + userId) }) {
                            Text("Cadastrar identificação")
                        }
                    }
                }
                usuario != null -> {
                    val pessoa = usuario!!
                    Column(
                        modifier = Modifier.fillMaxSize().widthIn(max = 680.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                "IDENTIFICAÇÃO PESSOAL",
                                color = teal,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "Meu cartão",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = navy
                            )
                            Text(
                                "Um cartão de apoio com as suas informações, " +
                                    "para mostrar apenas quando você quiser.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = secondaryInk
                            )
                        }
                        ControleLadosCarteira(
                            verso = mostrandoVerso,
                            onLado = { mostrandoVerso = it }
                        )
                        CarteiraPremium(
                            pessoa = pessoa,
                            retrato = foto,
                            codigoInterno = referenciaLocal,
                            verso = mostrandoVerso
                        )
                        Text(
                            if (mostrandoVerso) "Verso: informações de apoio e contatos."
                            else "Frente: foto e dados essenciais. Toque em Verso para continuar.",
                            modifier = Modifier.fillMaxWidth(),
                            color = secondaryInk,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )

                        OutlinedButton(
                            onClick = { mostrarDetalhes = !mostrarDetalhes },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                            shape = RoundedCornerShape(15.dp),
                            border = BorderStroke(1.3.dp, stroke),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = navy
                            )
                        ) {
                            Text(
                                if (mostrarDetalhes) "Ocultar dados cadastrais"
                                else "Ver documentos e endereço",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (mostrarDetalhes) {
                            DetalhesCadastraisPremium(pessoa)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(21.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, stroke),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(11.dp)
                            ) {
                                Text(
                                    if (foto == null) "Personalize com sua foto"
                                    else "Atualizar fotografia",
                                    color = navy,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    "Escolha uma imagem ou use a câmera. " +
                                        "A fotografia fica apenas neste aparelho.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = secondaryInk
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { escolherFoto.launch("image/*") },
                                        modifier = Modifier.weight(1f).heightIn(min = 54.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.4.dp, teal),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color.White,
                                            contentColor = teal
                                        )
                                    ) { Text("Galeria", fontWeight = FontWeight.Bold) }
                                    Button(
                                        onClick = {
                                            try {
                                                val dir = File(context.cacheDir, "tea_camera")
                                                    .apply { mkdirs() }
                                                val temp = File.createTempFile(
                                                    "identificacao_", ".jpg", dir
                                                )
                                                val uri = FileProvider.getUriForFile(
                                                    context,
                                                    context.packageName + ".fileprovider", temp
                                                )
                                                fotoCamera = temp
                                                uriCamera = uri
                                                tirarFoto.launch(uri)
                                            } catch (e: Exception) {
                                                aviso = "Não foi possível abrir a câmera."
                                            }
                                        },
                                        modifier = Modifier.weight(1f).heightIn(min = 54.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = teal)
                                    ) { Text("Tirar foto", fontWeight = FontWeight.Bold) }
                                }
                                if (foto != null) {
                                    TextButton(onClick = {
                                        val id = userId ?: return@TextButton
                                        FotoIdentificacao.excluir(context, id)
                                        fotoRevisao++
                                        aviso = "Foto removida."
                                    }) { Text("Remover foto", color = Color(0xFF9E4054)) }
                                }
                            }
                        }

                        aviso?.let { message ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFE1F3EF),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(message, modifier = Modifier.padding(13.dp), color = navy)
                            }
                        }

                        Button(
                            onClick = { navController.navigate("editarCarteirinha/" + userId) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = navy)
                        ) {
                            Text("Editar meus dados", fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium)
                        }
                        OutlinedButton(
                            onClick = { compartilharDialog = true },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, teal),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = teal,
                                containerColor = Color.White
                            )
                        ) {
                            Text("Compartilhar resumo de apoio",
                                fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = { excluirDialog = true },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) {
                            Text("Excluir minha identificação", color = Color(0xFFA33F4F),
                                fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "O Meu TEA não emite a CIPTEA. Para solicitar a carteira oficial, " +
                                "procure o órgão competente do seu estado.",
                            color = secondaryInk,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 26.dp)
                        )
                    }
                }
            }
        }
    }

    if (compartilharDialog && usuario != null) {
        val resumo = resumoSeguroCarteira(usuario!!)
        AlertDialog(
            onDismissRequest = { compartilharDialog = false },
            title = { Text("Compartilhar resumo de apoio?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Confira exatamente o que será compartilhado. " +
                        "CPF, RG, CID, endereço e fotografia NÃO serão incluídos.",
                        color = navy)
                    Text(
                        resumo,
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFFEAF3F8), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ink
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, resumo)
                        }
                        context.startActivity(
                            Intent.createChooser(intent, "Compartilhar resumo do Meu TEA")
                        )
                    } catch (e: Exception) {
                        aviso = "Não foi possível abrir o compartilhamento."
                    }
                    compartilharDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = teal)) {
                    Text("Compartilhar")
                }
            },
            dismissButton = {
                TextButton(onClick = { compartilharDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (excluirDialog) {
        AlertDialog(
            onDismissRequest = { if (!excluindo) excluirDialog = false },
            title = { Text("Excluir identificação pessoal?") },
            text = { Text("O cartão e a fotografia local serão excluídos. Sua conta Meu TEA será preservada.") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = userId ?: return@Button
                        if (uidAtual != id || excluindo) return@Button
                        excluindo = true
                        aviso = null
                        val limpeza = mapOf(
                            "carteira_digital" to false,
                            "cpf" to FieldValue.delete(),
                            "rg" to FieldValue.delete(),
                            "nascimento" to FieldValue.delete(),
                            "genero" to FieldValue.delete(),
                            "tipoSanguineo" to FieldValue.delete(),
                            "telefone" to FieldValue.delete(),
                            "endereco" to FieldValue.delete(),
                            "estado" to FieldValue.delete(),
                            "cid" to FieldValue.delete(),
                            "naturalidade" to FieldValue.delete(),
                            "estadoCivil" to FieldValue.delete(),
                            "responsavelLegal" to FieldValue.delete(),
                            "contatoEmergencia" to FieldValue.delete(),
                            "preferenciaComunicacao" to FieldValue.delete(),
                            "observacoesApoio" to FieldValue.delete()
                        )
                        scope.launch {
                            try {
                                db.collection("usuarios").document(id).update(limpeza).await()
                                withContext(Dispatchers.IO) {
                                    FotoIdentificacao.excluir(context, id)
                                }
                                excluirDialog = false
                                navController.navigate("menuPrincipalScreen/" + id) {
                                    popUpTo(navController.graph.id) { inclusive = false }
                                    launchSingleTop = true
                                }
                            } catch (e: Exception) {
                                aviso = "Não foi possível excluir a identificação. Tente novamente."
                                excluirDialog = false
                            } finally {
                                excluindo = false
                            }
                        }
                    },
                    enabled = !excluindo,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E3E50))
                ) { Text(if (excluindo) "Excluindo..." else "Excluir cartão") }
            },
            dismissButton = {
                TextButton(onClick = { excluirDialog = false }, enabled = !excluindo) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TituloSecaoCarteira(texto: String) {
    Text(
        texto,
        color = navy,
        fontWeight = FontWeight.ExtraBold,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun DadoCarteira(label: String, value: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().background(
            Color(0xFFF0F6FA), RoundedCornerShape(12.dp)
        ).padding(horizontal = 11.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(label.uppercase(), color = secondaryInk,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold)
        Text(value?.takeIf { it.isNotBlank() } ?: "Não informado",
            color = ink,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold)
    }
}

// Preserva os formatos dos dados antigos sem alterar o Firestore.
fun formatCpf(cpf: String?): String =
    cpf?.replace(Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"), "$1.$2.$3-$4")
        ?: "Não informado"

fun formatDate(date: String?): String =
    date?.replace(Regex("(\\d{2})(\\d{2})(\\d{4})"), "$1/$2/$3")
        ?: "Não informado"

fun formatPhone(phone: String?): String =
    phone?.replace(Regex("(\\d{2})(\\d{5})(\\d{4})"), "($1) $2-$3")
        ?: "Não informado"

fun formatRg(rg: String?): String =
    rg?.replace(Regex("(\\d{2})(\\d{3})(\\d{3})"), "$1.$2.$3")
        ?: "Não informado"
