package com.example.meutea.menu.careiradigital

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meutea.models.Usuario

// Identificação visual própria do aplicativo, sem símbolos governamentais.
internal object CarteiraVisual {
    val dark = Color(0xFF102C4B)
    val blue = Color(0xFF155A83)
    val teal = Color(0xFF087E83)
    val tealLight = Color(0xFFDBF5F0)
    val text = Color(0xFF183851)
    val subtle = Color(0xFF4C667B)
    val border = Color(0xFFC9DDE8)
    val paper = Color(0xFFF5F9FC)
    val gold = Color(0xFFF5C476)
}

@Composable
internal fun ControleLadosCarteira(verso: Boolean, onLado: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(Color(0xFFE1ECF3))
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf(false to "Frente", true to "Verso").forEach { (isBack, text) ->
            val active = verso == isBack
            Button(
                onClick = { onLado(isBack) },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                shape = RoundedCornerShape(13.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (active) CarteiraVisual.dark else Color.Transparent,
                    contentColor = if (active) Color.White else CarteiraVisual.dark
                ),
                elevation = if (active) ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    else null
            ) {
                Text(text, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
internal fun CarteiraPremium(
    pessoa: Usuario,
    retrato: Bitmap?,
    codigoInterno: String,
    verso: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.3.dp, CarteiraVisual.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 9.dp)
    ) {
        Column {
            CabecalhoPremiumCartao()
            Crossfade(targetState = verso, label = "frente e verso do cartão") { mostrarVerso ->
                if (mostrarVerso) VersoPremium(pessoa, codigoInterno)
                else FrentePremium(pessoa, retrato, codigoInterno)
            }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(CarteiraVisual.dark)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "IDENTIFICAÇÃO PESSOAL • NÃO SUBSTITUI A CIPTEA",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CabecalhoPremiumCartao() {
    Box(
        Modifier.fillMaxWidth().background(Brush.linearGradient(
            listOf(CarteiraVisual.dark, CarteiraVisual.blue, CarteiraVisual.teal)
        ))
    ) {
        Canvas(Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = .08f),
                radius = size.width * .36f,
                center = Offset(size.width * .99f, -size.height * .10f)
            )
            drawCircle(
                color = CarteiraVisual.gold.copy(alpha = .22f),
                radius = size.width * .19f,
                center = Offset(size.width * .75f, size.height * 1.05f)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = .16f))
                        .border(1.dp, Color.White.copy(alpha = .6f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("MEU TEA", color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge)
                    Text("Cartão pessoal de apoio à comunicação",
                        color = Color(0xFFDAF5FC),
                        style = MaterialTheme.typography.bodySmall)
                }
            }
            Surface(
                color = Color(0xFFFFE9CA),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text(
                    "USO PESSOAL  •  NÃO OFICIAL",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    color = Color(0xFF6B3E1B),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun FrentePremium(pessoa: Usuario, retrato: Bitmap?, codigo: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val fotoWidth = if (maxWidth < 350.dp) 96.dp else 118.dp
            val fotoHeight = if (maxWidth < 350.dp) 128.dp else 146.dp
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier.width(fotoWidth).height(fotoHeight)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.verticalGradient(listOf(
                            Color(0xFFD5EAF4), Color(0xFFEAF4F8)
                        )))
                        .border(1.5.dp, CarteiraVisual.border, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (retrato != null) {
                        Image(
                            bitmap = retrato.asImageBitmap(),
                            contentDescription = "Fotografia de identificação escolhida pelo usuário",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                Modifier.size(58.dp).clip(CircleShape)
                                    .background(Color.White.copy(alpha = .85f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    pessoa.nome.trim().firstOrNull()?.uppercase() ?: "?",
                                    fontSize = 36.sp, fontWeight = FontWeight.Black,
                                    color = CarteiraVisual.teal
                                )
                            }
                            Spacer(Modifier.height(7.dp))
                            Text("Adicionar foto", color = CarteiraVisual.dark,
                                fontSize = 11.sp, textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text("IDENTIFICAÇÃO PESSOAL",
                        color = CarteiraVisual.teal, fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.labelMedium)
                    Text(
                        pessoa.nome.ifBlank { "Nome não informado" },
                        color = CarteiraVisual.dark,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 4
                    )
                    HorizontalDivider(color = CarteiraVisual.border)
                    Text("CÓDIGO INTERNO DO APP",
                        color = CarteiraVisual.subtle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall)
                    Text(codigo, color = CarteiraVisual.blue,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        HorizontalDivider(color = CarteiraVisual.border)
        Text("DADOS ESSENCIAIS", color = CarteiraVisual.blue,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.labelLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoResumida("Nascimento", formatDate(pessoa.nascimento), Modifier.weight(1f))
            InfoResumida("Tipo sanguíneo", pessoa.tipoSanguineo, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoResumida("Estado / UF", pessoa.estado, Modifier.weight(1f))
            InfoResumida("Sexo / gênero", pessoa.genero, Modifier.weight(1f))
        }
        Box(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(CarteiraVisual.tealLight)
                .padding(13.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("CONTATO CADASTRADO", color = CarteiraVisual.teal,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold)
                Text(
                    formatPhone(pessoa.telefone).ifBlank { "Não informado" },
                    color = CarteiraVisual.dark,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Text(
            "Este cartão organiza informações informadas pela pessoa usuária. " +
                "Não é documento de identificação emitido pelo governo.",
            color = CarteiraVisual.subtle,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun VersoPremium(pessoa: Usuario, codigo: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Text("Informações de apoio", color = CarteiraVisual.dark,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.headlineSmall)
        Text(
            "Dados opcionais para facilitar a comunicação e o acolhimento.",
            color = CarteiraVisual.subtle,
            style = MaterialTheme.typography.bodyMedium
        )
        InfoDetalhe("Pessoa de referência", pessoa.responsavelLegal)
        InfoDetalhe("Contato de emergência", pessoa.contatoEmergencia)
        InfoDetalhe("Como prefiro me comunicar", pessoa.preferenciaComunicacao)
        InfoDetalhe("Necessidades e observações", pessoa.observacoesApoio)
        HorizontalDivider(color = CarteiraVisual.border)
        InfoDetalhe("Telefone cadastrado", formatPhone(pessoa.telefone))
        InfoDetalhe("Referência interna", codigo)
        Surface(
            color = Color(0xFFFFF1DF),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                "As necessidades de cada pessoa são individuais. Pergunte como ela prefere receber ajuda.",
                modifier = Modifier.padding(12.dp),
                color = Color(0xFF654B35),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun InfoResumida(label: String, value: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(CarteiraVisual.paper)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(label.uppercase(), color = CarteiraVisual.subtle,
            fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
        Text(
            value?.takeIf { it.isNotBlank() } ?: "Não informado",
            color = CarteiraVisual.dark,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun InfoDetalhe(label: String, value: String?) {
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(CarteiraVisual.paper)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label.uppercase(), color = CarteiraVisual.subtle,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.labelSmall)
        Text(
            value?.takeIf { it.isNotBlank() } ?: "Não informado",
            color = CarteiraVisual.dark,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
internal fun DetalhesCadastraisPremium(pessoa: Usuario) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(19.dp),
        border = BorderStroke(1.dp, CarteiraVisual.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Documentos informados", color = CarteiraVisual.dark,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoResumida("CPF", formatCpf(pessoa.cpf), Modifier.weight(1f))
                InfoResumida("RG", formatRg(pessoa.rg), Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoResumida("Naturalidade", pessoa.naturalidade, Modifier.weight(1f))
                InfoResumida("CID informado", pessoa.cid, Modifier.weight(1f))
            }
            InfoDetalhe("Endereço cadastrado", pessoa.endereco)
            Text("Os dados são fornecidos pela pessoa usuária e não são validados pelo Meu TEA.",
                color = CarteiraVisual.subtle,
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** Compartilhamento voluntário e mínimo; nunca inclui RG, CPF, CID, endereço ou fotografia. */
internal fun resumoSeguroCarteira(pessoa: Usuario): String = buildString {
    appendLine("Meu TEA — resumo pessoal de comunicação (não oficial)")
    appendLine("Nome: " + pessoa.nome.ifBlank { "Não informado" })
    if (pessoa.preferenciaComunicacao.isNotBlank()) {
        appendLine("Comunicação preferida: " + pessoa.preferenciaComunicacao)
    }
    if (pessoa.contatoEmergencia.isNotBlank()) {
        appendLine("Contato de emergência: " + pessoa.contatoEmergencia)
    }
    append("Este resumo não substitui a CIPTEA nem outro documento oficial.")
}
