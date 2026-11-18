package com.example.meutea.menu.comunicacao

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.meutea.ui.TeaPalette
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

private val gruposCartoes = listOf("Necessidades", "Emoções", "Conforto", "Escolhas", "Convivência")

/**
 * Editor real de cartão CAA: foto da câmera ou galeria + frase que será falada.
 * Os cartões continuam sincronizados no Firestore; as fotos ficam apenas no aparelho.
 */
@Composable
internal fun EditorCartaoDialog(
    atual: ComunicacaoCard?,
    uid: String,
    fotos: FotosCartao,
    onClose: () -> Unit,
    onSaved: () -> Unit,
    onDelete: (ComunicacaoCard) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cardId = remember(atual?.id) { atual?.id ?: UUID.randomUUID().toString() }

    var texto by remember(cardId) { mutableStateOf(atual?.texto ?: "") }
    var grupo by remember(cardId) { mutableStateOf(atual?.categoria ?: "Necessidades") }
    var fotoPendente by remember(cardId) { mutableStateOf<Uri?>(null) }
    var excluirFoto by remember(cardId) { mutableStateOf(false) }
    var salvando by remember(cardId) { mutableStateOf(false) }
    var erro by remember(cardId) { mutableStateOf<String?>(null) }
    var confirmarExclusao by remember(cardId) { mutableStateOf(false) }
    var arquivoCaptura by remember(cardId) { mutableStateOf<File?>(null) }
    var uriCaptura by remember(cardId) { mutableStateOf<Uri?>(null) }

    val escolherFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fotoPendente = uri
            excluirFoto = false
            erro = null
        }
    }
    val tirarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { sucesso ->
        val temp = arquivoCaptura
        val photoUri = uriCaptura
        if (sucesso && temp != null && temp.length() > 0 && photoUri != null) {
            fotoPendente = photoUri
            excluirFoto = false
            erro = null
        } else {
            temp?.delete()
            erro = "A foto não foi capturada. Você pode tentar novamente ou escolher da galeria."
        }
    }

    // Os arquivos temporários são excluídos ao fechar o editor, inclusive após salvar.
    DisposableEffect(cardId) {
        val tempFolder = File(context.cacheDir, "tea_camera")
        onDispose {
            // Não excluir a foto definitiva: somente os arquivos temporários da câmera.
            tempFolder.listFiles()?.filter { it.name.startsWith("meutea_$cardId") }?.forEach {
                it.delete()
            }
        }
    }

    val preview by produceState<Bitmap?>(
        initialValue = null, cardId, fotoPendente, excluirFoto, fotos
    ) {
        value = withContext(Dispatchers.IO) {
            when {
                fotoPendente != null -> fotos.lerPreview(fotoPendente!!)
                excluirFoto -> null
                atual != null -> fotos.lerSalva(atual.id)
                else -> null
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!salvando) onClose() },
        shape = RoundedCornerShape(26.dp),
        containerColor = Color(0xFFF3FAFF),
        title = {
            Column {
                Text(if (atual == null) "Meu novo cartão" else "Editar meu cartão",
                    color = TeaPalette.ink, fontWeight = FontWeight.ExtraBold)
                Text("Transforme uma foto em uma forma de se comunicar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF466581))
            }
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(170.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFD8F6F8), Color(0xFFDCEBFF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (preview != null) {
                            Image(
                                bitmap = preview!!.asImageBitmap(),
                                contentDescription = "Foto que aparecerá no cartão",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxWidth().height(166.dp)
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("▧", style = MaterialTheme.typography.displayMedium,
                                    color = TeaPalette.ocean)
                                Text("Sua imagem aparece aqui",
                                    color = TeaPalette.ink,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center)
                            }
                        }
                    }
                    Text(
                        if (fotoPendente != null) "Nova foto selecionada"
                        else if (preview != null) "Imagem atual"
                        else "Fotografe seu cartão de emoções ou escolha uma foto",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        color = TeaPalette.ink,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            try {
                                val pasta = File(context.cacheDir, "tea_camera")
                                check(pasta.isDirectory || pasta.mkdirs())
                                val temp = File.createTempFile(
                                    "meutea_$cardId", ".jpg", pasta
                                )
                                val uri = FileProvider.getUriForFile(
                                    context, "${context.packageName}.fileprovider", temp
                                )
                                arquivoCaptura = temp
                                uriCaptura = uri
                                tirarFoto.launch(uri)
                            } catch (_: Exception) {
                                erro = "Não foi possível abrir a câmera neste aparelho."
                            }
                        },
                        enabled = !salvando,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TeaPalette.ocean)
                    ) { Text("📷 Câmera") }
                    OutlinedButton(
                        onClick = { escolherFoto.launch("image/*") },
                        enabled = !salvando,
                        modifier = Modifier.weight(1f)
                    ) { Text("Galeria") }
                }
                if (preview != null || fotoPendente != null) {
                    TextButton(
                        onClick = {
                            fotoPendente = null
                            excluirFoto = true
                        },
                        enabled = !salvando
                    ) { Text("Remover foto do cartão", color = Color(0xFFB54049)) }
                }

                Text("O que o cartão deve dizer?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TeaPalette.ink, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it.take(100) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mensagem falada") },
                    placeholder = { Text("Ex.: Estou com medo. Preciso de ajuda.") },
                    shape = RoundedCornerShape(16.dp),
                    supportingText = { Text("${texto.length}/100 caracteres") },
                    minLines = 2,
                    maxLines = 3
                )

                Text("Organizar na categoria",
                    style = MaterialTheme.typography.titleSmall,
                    color = TeaPalette.ink, fontWeight = FontWeight.Bold)
                LazyRow(
                    contentPadding = PaddingValues(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(gruposCartoes) { opcao ->
                        FilterChip(
                            selected = grupo == opcao,
                            onClick = { grupo = opcao },
                            label = { Text(opcao) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White,
                                selectedContainerColor = TeaPalette.ocean,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Text(
                    "Depois de salvar, toque na imagem para ouvir a mensagem. " +
                        "Você também poderá gravar sua própria voz para esse cartão.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF466581)
                )
                Text(
                    "As fotos ficam apenas neste aparelho, não são enviadas ao Firebase.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF466581)
                )
                erro?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = texto.isNotBlank() && !salvando,
                colors = ButtonDefaults.buttonColors(containerColor = TeaPalette.teal),
                onClick = {
                    salvando = true
                    erro = null
                    val data = ComunicacaoCard(cardId, texto.trim(), grupo, "◉")
                    FirebaseFirestore.getInstance().collection("usuarios")
                        .document(uid).collection("comunicacao").document(cardId)
                        .set(data)
                        .addOnSuccessListener {
                            scope.launch {
                                val result = withContext(Dispatchers.IO) {
                                    when {
                                        fotoPendente != null -> fotos.salvar(cardId, fotoPendente!!)
                                        excluirFoto -> {
                                            if (fotos.remover(cardId)) Result.success(Unit)
                                            else Result.failure(IllegalStateException(
                                                "Não foi possível remover a foto antiga."
                                            ))
                                        }
                                        else -> Result.success(Unit)
                                    }
                                }
                                salvando = false
                                if (result.isSuccess) {
                                    onSaved()
                                } else {
                                    erro = "A mensagem foi salva, mas a foto não. " +
                                        (result.exceptionOrNull()?.localizedMessage ?: "Tente novamente.")
                                }
                            }
                        }
                        .addOnFailureListener {
                            salvando = false
                            erro = "Não foi possível salvar o cartão. Verifique sua conexão."
                        }
                }
            ) { Text(if (salvando) "Salvando..." else "Salvar cartão") }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (atual != null) {
                    TextButton(onClick = { confirmarExclusao = true }, enabled = !salvando) {
                        Text("Excluir", color = Color(0xFFB54049))
                    }
                }
                TextButton(onClick = onClose, enabled = !salvando) {
                    Text("Cancelar")
                }
            }
        }
    )

    if (confirmarExclusao && atual != null) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir este cartão?") },
            text = { Text("A foto, a gravação local e a mensagem serão removidas.") },
            confirmButton = {
                Button(onClick = {
                    confirmarExclusao = false
                    onDelete(atual)
                }) { Text("Excluir cartão") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Voltar") }
            }
        )
    }
}
