package com.example.meutea.menu.jogos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.meutea.R
import kotlin.random.Random

private data class Cena(
    val emocao:String,val figura:Int,val historia:String,val frase:String,val apoio:String
)
private val cenarios=listOf(
    Cena("Alegria",R.drawable.tea_pic_feliz,"Você terminou uma atividade de que gostou.",
        "Estou feliz com o que fiz.","Compartilhar a alegria pode ser agradável."),
    Cena("Tristeza",R.drawable.tea_pic_triste,"O passeio esperado foi cancelado.",
        "Estou triste. Quero conversar.","É válido ficar triste e pedir companhia."),
    Cena("Medo",R.drawable.tea_medo,"Um barulho inesperado assustou alguém.",
        "Estou com medo. Pode ficar comigo?","Respeite o medo e ofereça segurança."),
    Cena("Raiva",R.drawable.tea_raiva,"Mudaram os planos sem avisar.",
        "Estou irritado. Preciso de uma pausa.","Avisar mudanças com antecedência pode ajudar."),
    Cena("Cansaço",R.drawable.tea_pic_cansaco,"O dia foi cheio de atividades.",
        "Estou cansado. Quero descansar.","O descanso é uma necessidade legítima."),
    Cena("Dor",R.drawable.tea_pic_dor,"Você sente um desconforto no corpo.",
        "Estou com dor. Preciso de ajuda.","Uma dor deve ser levada a sério."),
    Cena("Silêncio",R.drawable.tea_pic_silencio,"O ambiente está muito barulhento.",
        "Quero um lugar mais silencioso.","Preferências sensoriais devem ser respeitadas."),
    Cena("Pausa",R.drawable.tea_pic_pausa,"A atividade ficou intensa.",
        "Preciso de um tempo para mim.","Pausas não significam fracasso."),
    Cena("Ajuda",R.drawable.tea_pic_ajuda,"Uma tarefa parece muito difícil.",
        "Pode me ajudar com esta parte?","Pedir apoio demonstra autonomia."),
    Cena("Escolha",R.drawable.tea_pic_escolher,"Há várias brincadeiras disponíveis.",
        "Quero escolher minha atividade.","Oferecer escolhas respeita preferências."),
    Cena("Gratidão",R.drawable.tea_pic_obrigado,"Alguém ofereceu apoio.",
        "Obrigado por me ajudar.","A gratidão pode ser expressa de muitas maneiras."),
    Cena("Confusão",R.drawable.tea_pic_repetir,"Uma instrução foi dada rápido demais.",
        "Pode repetir devagar?","Repetir instruções com clareza pode ajudar."),
    Cena("Felicidade",R.drawable.tea_pic_feliz,"Você encontrou uma pessoa querida.",
        "Que bom te ver!","As relações são diferentes para cada pessoa."),
    Cena("Incerteza",R.drawable.tea_pic_escolher,"Você ainda não decidiu o que quer.",
        "Ainda não sei. Pode me dar tempo?","Não é obrigatório decidir imediatamente."),
    Cena("Tristeza",R.drawable.tea_pic_triste,"Um objeto importante foi perdido.",
        "Estou chateado com a perda.","Escutar e ajudar a procurar pode fazer diferença."),
    Cena("Alívio",R.drawable.tea_pic_pausa,"Uma tarefa difícil terminou.",
        "Agora me sinto mais tranquilo.","As sensações podem mudar ao longo do dia."),
    Cena("Carinho",R.drawable.tea_pic_oi,"Alguém quer demonstrar afeto.",
        "Gosto de estar perto de você.","Afeto não exige abraços ou contato físico."),
    Cena("Incomodo",R.drawable.tea_pic_dor,"A etiqueta da roupa está incomodando.",
        "Essa roupa está me incomodando.","Ajustar o ambiente pode trazer conforto."),
    Cena("Entusiasmo",R.drawable.tea_pic_feliz,"Um jogo favorito vai começar.",
        "Estou animado para jogar!","Interesses pessoais podem trazer muita alegria."),
    Cena("Concentração",R.drawable.tea_pic_silencio,"Você está fazendo uma atividade que exige atenção.",
        "Quero me concentrar agora.","Respeite a maneira de concentrar-se da pessoa."),
    Cena("Frustração",R.drawable.tea_raiva,"Um projeto não saiu como você esperava.",
        "Estou frustrado. Quero tentar depois.","É aceitável parar, ajustar e recomeçar."),
    Cena("Dúvida",R.drawable.tea_pic_repetir,"A explicação deixou uma pergunta.",
        "Não entendi esta parte.","Perguntas ajudam a construir conhecimento."),
    Cena("Orgulho",R.drawable.tea_pic_feliz,"Você aprendeu algo novo.",
        "Estou orgulhoso de mim!","Reconhecer esforço é importante."),
    Cena("Segurança",R.drawable.tea_pic_ajuda,"Você está em um lugar novo.",
        "Pode me mostrar onde fica a saída?","Antecipar informações pode trazer segurança.")
)
private val nomesEmocao=listOf("Alegria","Tristeza","Medo","Raiva","Cansaço","Dor",
    "Silêncio","Pausa","Ajuda","Escolha","Gratidão","Confusão","Alívio","Carinho")

@Composable internal fun JogoEmocoes(onBack:()->Unit) {
    var nivel by remember{mutableStateOf("Inicial")}
    var total by remember{mutableIntStateOf(20)}
    var ready by remember{mutableStateOf(false)}
    var round by remember{mutableIntStateOf(0)}
    var seed by remember{mutableIntStateOf(42)}
    var answer by remember(round,seed){mutableIntStateOf(-1)}
    var points by remember{mutableIntStateOf(0)}
    val current=remember(round,seed,nivel){
        val shuffled=cenarios.shuffled(Random(seed+round/24))
        shuffled[round%shuffled.size]
    }
    val correct=when(nivel) {
        "Inicial"->current.emocao
        "Intermediário"->current.frase
        else->current.apoio
    }
    val choices=remember(round,seed,nivel){
        val pool=when(nivel){
            "Inicial"->nomesEmocao.filter{it!=current.emocao}
            "Intermediário"->cenarios.map{it.frase}.filter{it!=current.frase}
            else->cenarios.map{it.apoio}.filter{it!=current.apoio}
        }
        (pool.distinct().shuffled(Random(seed+round)).take(3)+correct)
            .shuffled(Random(seed+round+99))
    }
    val ended=ready&&round>=total
    TelaJogo("Explorar emoções",onBack) {
        if(!ready) {
            GameHeadline("Conhecer e comunicar","Imagens, histórias e frases em diferentes níveis.")
            GamePanel {
                GameLevelPicker(nivel){nivel=it}
                GameCountPicker(total){total=it}
                Text("Fácil: possíveis emoções. Médio: frases de comunicação. " +
                    "Difícil: apoios e preferências.",
                    color=GameColors.muted)
                Text("Não é um teste para identificar sentimentos de outras pessoas. " +
                    "Uma figura nunca determina como alguém realmente se sente.",
                    style=MaterialTheme.typography.bodySmall,color=GameColors.muted)
            }
            GameAction("Começar",onClick={ready=true;round=0;points=0;seed=Random.nextInt()},
                modifier=Modifier.fillMaxWidth())
        } else if(ended) {
            GamePanel {
                Text("Histórias concluídas!",style=MaterialTheme.typography.headlineSmall,
                    fontWeight=FontWeight.Bold,color=GameColors.navy)
                GameProgress(points,total,"Escolhas correspondentes")
                Text("Cada pessoa pode se expressar de formas diferentes.",
                    color=GameColors.muted)
                GameAction("Outras histórias",onClick={
                    round=0;points=0;seed=Random.nextInt()
                },modifier=Modifier.fillMaxWidth())
                GameAction("Mudar o nível",onClick={ready=false},
                    modifier=Modifier.fillMaxWidth(),secondary=true)
            }
        } else {
            GamePanel {
                GameProgress(round,total,"Histórias exploradas")
                Image(painterResource(current.figura),contentDescription="Ilustração do cenário",
                    modifier=Modifier.size(145.dp).align(Alignment.CenterHorizontally))
                Text(current.historia,
                    style=MaterialTheme.typography.titleLarge,
                    fontWeight=FontWeight.Bold,color=GameColors.navy,
                    textAlign=TextAlign.Center)
                Text(when(nivel){
                    "Inicial"->"Qual emoção poderia combinar com essa situação?"
                    "Intermediário"->"Qual frase ajudaria a se expressar?"
                    else->"Que atitude respeitosa pode ajudar?"
                },color=GameColors.muted)
                choices.forEachIndexed{i,option->
                    val selected=answer==i
                    val expected=answer>=0 && option==correct
                    OutlinedButton(onClick={
                        if(answer<0){answer=i;if(option==correct)points++}
                    },modifier=Modifier.fillMaxWidth().heightIn(min=56.dp),
                        shape=RoundedCornerShape(14.dp),
                        colors=ButtonDefaults.outlinedButtonColors(
                            containerColor=when{
                                expected->GameColors.success
                                selected->Color(0xFFFFE9D9)
                                else->Color.White
                            },contentColor=GameColors.navy),
                        border=BorderStroke(1.5.dp,if(expected)GameColors.teal else GameColors.border)
                    ) {Text(option,color=GameColors.navy,
                        textAlign=TextAlign.Center,
                        style=MaterialTheme.typography.bodyLarge)}
                }
                if(answer>=0){
                    Text("Uma possibilidade: "+correct,color=GameColors.navy,
                        fontWeight=FontWeight.Bold)
                    Text(current.apoio,color=GameColors.muted)
                    Text("Pergunte à pessoa como ela se sente: imagens são apenas pistas, " +
                        "não respostas definitivas.",
                        style=MaterialTheme.typography.bodySmall,color=GameColors.muted)
                }
            }
            GameAction("Próxima história",onClick={round++},
                enabled=answer>=0,modifier=Modifier.fillMaxWidth())
            GameAction("Alterar atividade",onClick={ready=false},
                secondary=true,modifier=Modifier.fillMaxWidth())
        }
    }
}
