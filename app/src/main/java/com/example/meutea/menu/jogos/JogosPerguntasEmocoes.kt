package com.example.meutea.menu.jogos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meutea.R
import com.example.meutea.ui.TeaGlassPanel
import com.example.meutea.ui.TeaPalette

internal data class Pergunta(
    val enunciado: String,
    val opcoes: List<String>,
    val correta: Int,
    val explicacao: String
)

private val matematica = mapOf(
    "Inicial" to listOf(
        Pergunta("Quanto é 3 + 4?", listOf("6","7","8"), 1,
            "Juntando 3 objetos e mais 4, temos 7."),
        Pergunta("Quanto é 10 − 6?", listOf("4","5","6"), 0,
            "Retirando 6 de 10, restam 4."),
        Pergunta("Qual número vem após 19?", listOf("18","21","20"), 2,
            "Depois do 19, vem 20."),
        Pergunta("Quanto é 2 × 3?", listOf("5","6","7"), 1,
            "Dois grupos de três são seis."),
        Pergunta("Quanto é metade de 12?", listOf("6","4","8"), 0,
            "12 dividido em duas partes iguais dá 6 em cada.")
    ),
    "Intermediário" to listOf(
        Pergunta("Quanto é 24 + 18?", listOf("32","42","44"), 1,
            "24 + 10 = 34; mais 8 = 42."),
        Pergunta("Quanto é 9 × 7?", listOf("63","56","72"), 0,
            "Nove grupos de sete têm 63."),
        Pergunta("Quanto é 81 ÷ 9?", listOf("8","10","9"), 2,
            "9 multiplicado por 9 é 81."),
        Pergunta("Qual é 25% de 80?", listOf("15","20","25"), 1,
            "25% é um quarto. 80 dividido por quatro é 20."),
        Pergunta("Qual é o perímetro de um quadrado de lado 6?",
            listOf("24","36","12"), 0, "Quatro lados de 6 somam 24.")
    ),
    "Avançado" to listOf(
        Pergunta("Resolva: 3x + 5 = 20", listOf("x = 7","x = 5","x = 4"), 1,
            "3x = 15, então x = 5."),
        Pergunta("Quanto é 2³ + 3²?", listOf("13","17","25"), 1,
            "2³ = 8 e 3² = 9. A soma é 17."),
        Pergunta("Qual é a média de 6, 8, 10 e 12?",
            listOf("9","10","8"), 0,
            "A soma é 36; dividindo por quatro obtemos 9."),
        Pergunta("Uma compra de R$ 200 tem desconto de 15%. Quanto fica?",
            listOf("R$ 185","R$ 170","R$ 160"), 1,
            "15% de 200 são R$ 30. Restam R$ 170."),
        Pergunta("Quanto é 3/4 de 120?", listOf("60","80","90"), 2,
            "120 dividido por quatro é 30. Três partes dão 90.")
    )
)

private val portugues = mapOf(
    "Inicial" to listOf(
        Pergunta("Qual palavra nomeia um animal?", listOf("Gato","Mesa","Porta"), 0,
            "Gato é um animal."),
        Pergunta("Qual é a primeira letra de BOLA?", listOf("A","B","L"), 1,
            "A palavra BOLA começa com B."),
        Pergunta("Qual palavra rima com PÃO?", listOf("Casa","Chão","Mesa"), 1,
            "Pão e chão têm som final parecido."),
        Pergunta("Qual palavra indica uma cor?", listOf("Lápis","Azul","Caderno"), 1,
            "Azul é uma cor."),
        Pergunta("Complete: Eu ___ água.", listOf("bebo","somos","andam"), 0,
            "O verbo beb(o) concorda com eu.")
    ),
    "Intermediário" to listOf(
        Pergunta("Qual é o plural de papel?", listOf("Papéis","Papeis","Papel"), 0,
            "Papel fica papéis no plural."),
        Pergunta("Qual é o antônimo de rápido?", listOf("Veloz","Lento","Ágil"), 1,
            "Lento é o oposto de rápido."),
        Pergunta("Qual palavra está acentuada corretamente?",
            listOf("Voce","Você","Vocé"), 1,
            "Você recebe acento circunflexo no ê."),
        Pergunta("Qual frase é uma pergunta?",
            listOf("Estou feliz.","Que horas são?","Que lindo!"), 1,
            "Perguntas terminam com ponto de interrogação."),
        Pergunta("Qual é o sinônimo de contente?",
            listOf("Feliz","Bravo","Cansado"), 0,
            "Contente e feliz têm sentidos próximos.")
    ),
    "Avançado" to listOf(
        Pergunta("Em 'Embora chovesse, saí', 'embora' expressa:",
            listOf("Causa","Concessão","Finalidade"), 1,
            "A oração admite uma condição contrária ao esperado."),
        Pergunta("Qual frase usa crase corretamente?",
            listOf("Fui à escola.","Fui à pé.","Fui à Manaus."), 0,
            "A preposição a se une ao artigo a de escola."),
        Pergunta("Qual é o sujeito de 'Chegaram cedo os alunos'?",
            listOf("Cedo","Os alunos","Chegaram"), 1,
            "Quem chegou cedo? Os alunos."),
        Pergunta("Qual alternativa é uma metáfora?",
            listOf("Ele corre como o vento.","O tempo é um rio.","Choveu ontem."),
            1, "O tempo é comparado diretamente a um rio."),
        Pergunta("Qual palavra contém um dígrafo?",
            listOf("Chave","Aula","Pato"), 0,
            "CH representa um único som em chave.")
    )
)

private val logica = mapOf(
    "Inicial" to listOf(
        Pergunta("Qual vem depois? 🔴 🔵 🔴 🔵 ...",
            listOf("🔵","🔴","🟢"), 1, "As cores alternam vermelho e azul."),
        Pergunta("Complete: 2, 4, 6, ...",
            listOf("7","8","9"), 1, "A sequência aumenta de dois em dois."),
        Pergunta("Se hoje é segunda, amanhã é...",
            listOf("Terça","Domingo","Quarta"), 0,
            "O dia depois de segunda é terça."),
        Pergunta("Qual não pertence ao grupo?",
            listOf("Maçã","Banana","Cadeira"), 2,
            "Cadeira não é fruta."),
        Pergunta("🟨 🟩 🟨 🟩 ...",
            listOf("🟨","🟩","🟥"), 0,
            "O padrão repete amarelo e verde.")
    ),
    "Intermediário" to listOf(
        Pergunta("Continue: 3, 6, 12, 24, ...",
            listOf("36","48","30"), 1,
            "Cada termo é o dobro do anterior."),
        Pergunta("Todo gato é mamífero. Um gato é...",
            listOf("Mamífero","Peixe","Planta"), 0,
            "A premissa afirma que todos os gatos são mamíferos."),
        Pergunta("Continue: A, C, E, G, ...",
            listOf("H","I","J"), 1,
            "Pulamos uma letra do alfabeto por vez."),
        Pergunta("Cinco pessoas apertam as mãos uma vez, em pares. Quantos apertos?",
            listOf("5","10","20"), 1,
            "Há 5×4/2 = 10 pares."),
        Pergunta("Uma caixa tem 4 bolas azuis e 3 verdes. Total?",
            listOf("7","6","8"), 0,
            "Quatro mais três é sete.")
    ),
    "Avançado" to listOf(
        Pergunta("Continue: 1, 1, 2, 3, 5, 8, ...",
            listOf("11","13","15"), 1,
            "A sequência de Fibonacci soma os dois termos anteriores."),
        Pergunta("Se P implica Q e P é verdadeiro, então...",
            listOf("Q é verdadeiro","Q é falso","Nada pode ser dito"), 0,
            "P implica Q e P verdadeiro permitem concluir Q."),
        Pergunta("Qual número não segue o padrão? 4, 9, 16, 25, 30",
            listOf("16","25","30"), 2,
            "Os demais são quadrados perfeitos consecutivos."),
        Pergunta("Em um grupo de 6, quantos pares diferentes podem ser formados?",
            listOf("15","12","36"), 0,
            "6×5/2 = 15 pares distintos."),
        Pergunta("Todos A são B. Alguns B são C. É certo que alguns A são C?",
            listOf("Sim","Não necessariamente","Nunca"), 1,
            "As informações não garantem interseção entre A e C.")
    )
)

@Composable
internal fun JogoPerguntas(titulo: String, tipo: String, onBack: () -> Unit) {
    val perguntas = when (tipo) {
        "matematica" -> matematica
        "portugues" -> portugues
        else -> logica
    }
    var nivel by remember { mutableStateOf("Inicial") }
    var indice by remember(nivel, tipo) { mutableIntStateOf(0) }
    var resposta by remember(nivel, indice) { mutableIntStateOf(-1) }
    var acertos by remember(nivel) { mutableIntStateOf(0) }
    val lista = perguntas[nivel].orEmpty()
    val atual = lista[indice.coerceIn(lista.indices)]
    val terminou = indice >= lista.lastIndex && resposta >= 0

    TelaJogo(titulo, onBack) {
        TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
            Text("Aprenda no seu ritmo", color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold)
            Text("Escolha a dificuldade. Erros fazem parte da aprendizagem.",
                color = Color.White)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            perguntas.keys.forEach { etapa ->
                OutlinedButton(
                    onClick = { nivel = etapa },
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        containerColor = if (nivel == etapa) TeaPalette.mint else Color(0xF0FFFFFF),
                        contentColor = TeaPalette.ink
                    )
                ) { Text(etapa, style = MaterialTheme.typography.labelMedium) }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xF4EFF9FF))
        ) {
            Column(Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Questão ${indice + 1} de ${lista.size}",
                        color = TeaPalette.ocean, fontWeight = FontWeight.Bold)
                    Text("$acertos acerto(s)", color = TeaPalette.ink)
                }
                LinearProgressIndicator(
                    progress = { (indice + 1f) / lista.size },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(atual.enunciado,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold, color = TeaPalette.ink)
                atual.opcoes.forEachIndexed { i, op ->
                    OutlinedButton(
                        onClick = {
                            if (resposta < 0) {
                                resposta = i
                                if (i == atual.correta) acertos++
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(57.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = when {
                                resposta >= 0 && i == atual.correta -> Color(0xFFBFF1DB)
                                resposta == i -> Color(0xFFFFE2C5)
                                else -> Color.White
                            },
                            contentColor = TeaPalette.ink
                        ),
                        border = BorderStroke(1.dp, Color(0xFFBAD3E8))
                    ) {
                        Text(op, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (resposta >= 0) {
                    Text(if (resposta == atual.correta) "Muito bem!" else "Vamos entender juntos:",
                        color = TeaPalette.ocean,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text(atual.explicacao, color = TeaPalette.ink)
                    if (terminou) {
                        Text("Você concluiu este nível. Acertou $acertos de ${lista.size}.",
                            color = TeaPalette.ink, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Button(
            onClick = {
                if (terminou) {
                    indice = 0
                    acertos = 0
                } else {
                    indice++
                }
                resposta = -1
            },
            enabled = resposta >= 0,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (terminou) "Recomeçar nível" else "Próxima questão") }
    }
}

private data class Emocao(val imagem: Int, val descricao: String, val nome: String, val opcoes: List<String>)
private val emocoes = listOf(
    Emocao(R.drawable.tea_pic_feliz, "Observe os olhos e a boca.",
        "Feliz", listOf("Feliz","Triste","Com medo")),
    Emocao(R.drawable.tea_pic_triste, "Essa pessoa está expressando uma emoção.",
        "Triste", listOf("Feliz","Triste","Com raiva")),
    Emocao(R.drawable.tea_medo, "Qual emoção o desenho sugere?",
        "Com medo", listOf("Triste","Com medo","Feliz")),
    Emocao(R.drawable.tea_raiva, "Qual emoção está representada?",
        "Com raiva", listOf("Feliz","Com raiva","Com medo"))
)

@Composable
internal fun JogoEmocoes(onBack: () -> Unit) {
    var indice by remember { mutableIntStateOf(0) }
    var resposta by remember(indice) { mutableStateOf<String?>(null) }
    val atual = emocoes[indice]
    TelaJogo("Explorar emoções", onBack) {
        TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
            Text("Observe a expressão", color = Color.White,
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Cada pessoa pode demonstrar emoções de forma diferente.",
                color = Color.White)
        }
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xF2EAF8FF)),
            shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Imagem ${indice + 1} de ${emocoes.size}", color = TeaPalette.ocean)
                Spacer(Modifier.height(10.dp))
                Image(painterResource(atual.imagem), contentDescription = null,
                    modifier = Modifier.size(170.dp))
                Text(atual.descricao, color = TeaPalette.ink,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center)
            }
        }
        atual.opcoes.forEach { opcao ->
            OutlinedButton(
                onClick = { resposta = opcao },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    containerColor = if (resposta == opcao) TeaPalette.mint else Color(0xF4FFFFFF),
                    contentColor = TeaPalette.ink
                )
            ) { Text(opcao) }
        }
        if (resposta != null) {
            TeaGlassPanel(modifier = Modifier.fillMaxWidth()) {
                Text("Uma interpretação possível deste desenho é: ${atual.nome}.",
                    color = TeaPalette.ink)
                Text("Não é possível determinar o sentimento de alguém apenas por seu rosto.",
                    color = TeaPalette.ink)
            }
        }
        Button(onClick = { indice = (indice + 1) % emocoes.size },
            enabled = resposta != null, modifier = Modifier.fillMaxWidth()) {
            Text(if (indice == emocoes.lastIndex) "Recomeçar" else "Próximo desenho")
        }
    }
}

@Composable
internal fun JogoPausa(onBack: () -> Unit) {
    val instrucoes = listOf(
        "Escolha um lugar onde você se sinta confortável.",
        "Se gostar, observe sua respiração sem alterar o ritmo.",
        "Você pode descansar as mãos, olhar ao redor ou fechar os olhos.",
        "Quando desejar, retome sua atividade ou continue descansando."
    )
    var etapa by remember { mutableIntStateOf(0) }
    TelaJogo("Pausa tranquila", onBack) {
        TeaGlassPanel(dark = true, modifier = Modifier.fillMaxWidth()) {
            Text("Seu momento de descanso", color = Color.White,
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Você decide quanto tempo ficar. Nenhum exercício é obrigatório.",
                color = Color.White)
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xF3E4F8F2)),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌿", fontSize = 64.sp)
                Spacer(Modifier.height(20.dp))
                Text(instrucoes[etapa], color = TeaPalette.ink,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                Text("Etapa ${etapa + 1} de ${instrucoes.size}",
                    color = TeaPalette.ocean)
            }
        }
        Button(onClick = { etapa = (etapa + 1) % instrucoes.size },
            modifier = Modifier.fillMaxWidth()) {
            Text(if (etapa == instrucoes.lastIndex) "Recomeçar" else "Próxima etapa")
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Encerrar agora")
        }
    }
}
