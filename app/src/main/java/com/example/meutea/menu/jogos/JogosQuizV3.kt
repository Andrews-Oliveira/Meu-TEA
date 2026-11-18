package com.example.meutea.menu.jogos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable internal fun JogoPerguntas(titulo:String,tipo:String,onBack:()->Unit) {
    var nivel by remember {mutableStateOf("Inicial")}
    var total by remember {mutableIntStateOf(20)}
    var operador by remember {mutableStateOf("Todos")}
    var tabuada by remember {mutableIntStateOf(0)}
    var session by remember {mutableStateOf<List<Pergunta>>(emptyList())}
    var setup by remember {mutableStateOf(true)}
    var index by remember {mutableIntStateOf(0)}
    var selected by remember {mutableIntStateOf(-1)}
    var correct by remember {mutableIntStateOf(0)}
    var end by remember {mutableStateOf(false)}
    var message by remember {mutableStateOf<String?>(null)}
    val wrong=remember{mutableStateListOf<Pergunta>()}

    fun start(questions:List<Pergunta>) {
        session=questions;index=0;selected=-1;correct=0
        wrong.clear();end=false;setup=false;message=null
    }
    fun create() {
        val randomSeed=Random.nextInt()
        val result=runCatching {
            when(tipo){
                "matematica"->gerarMatematica(nivel,total,
                    if(tabuada==0)null else tabuada,operador,randomSeed)
                "portugues"->gerarPortugues(nivel,total,randomSeed)
                else->gerarLogica(nivel,total,randomSeed)
            }
        }
        if(result.isSuccess && !result.getOrNull().isNullOrEmpty()){
            start(result.getOrThrow())
        }else {
            message="Não foi possível preparar a partida. Experimente outra configuração."
        }
    }

    TelaJogo(titulo,onBack) {
        if(setup){
            GameHeadline("Sua jornada de aprendizagem",
                "Configure uma atividade mais longa, sem limite de tempo.")
            GamePanel {
                GameLevelPicker(nivel){nivel=it}
                GameCountPicker(total){total=it}
                if(tipo=="matematica"){
                    Text("Tabuada de 1 a 9",color=GameColors.ink,fontWeight=FontWeight.Bold)
                    Text("Escolha um número ou pratique todos.",color=GameColors.muted)
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement=Arrangement.spacedBy(6.dp)){
                        (listOf(0)+(1..9).toList()).forEach{n->
                            val active=tabuada==n
                            OutlinedButton(onClick={tabuada=n},
                                shape=RoundedCornerShape(12.dp),
                                contentPadding=PaddingValues(horizontal=13.dp,vertical=6.dp),
                                colors=ButtonDefaults.outlinedButtonColors(
                                    containerColor=if(active)GameColors.navy else Color.White,
                                    contentColor=if(active)Color.White else GameColors.ink),
                                border=BorderStroke(1.dp,
                                    if(active)GameColors.navy else GameColors.border)
                            ){Text(if(n==0)"Todas" else n.toString(),
                                fontWeight=FontWeight.Bold)}
                        }
                    }
                    Text("Operações",color=GameColors.ink,fontWeight=FontWeight.Bold)
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement=Arrangement.spacedBy(6.dp)){
                        listOf("Todos","+","−","×","÷").forEach{ op->
                            val active=operador==op
                            OutlinedButton(onClick={operador=op},
                                shape=RoundedCornerShape(12.dp),
                                contentPadding=PaddingValues(horizontal=15.dp,vertical=6.dp),
                                colors=ButtonDefaults.outlinedButtonColors(
                                    containerColor=if(active)GameColors.navy else Color.White,
                                    contentColor=if(active)Color.White else GameColors.ink),
                                border=BorderStroke(1.dp,
                                    if(active)GameColors.navy else GameColors.border)
                            ){Text(op,fontWeight=FontWeight.Bold)}
                        }
                    }
                    Text("Adição (+), subtração (−), multiplicação (×) e divisão exata (÷).",
                        style=MaterialTheme.typography.bodySmall,color=GameColors.muted)
                } else {
                    Text(if(tipo=="portugues")
                        "Vocabulário, interpretação, acentuação e gramática."
                        else "Padrões, dedução, sequências, números e raciocínio.",
                        color=GameColors.muted)
                }
            }
            message?.let{GamePanel{Text(it,color=GameColors.ink)}}
            GameAction("Começar "+total+" perguntas",
                onClick={create()},modifier=Modifier.fillMaxWidth())
        } else if(end) {
            GameHeadline("Partida concluída",
                "Você terminou "+session.size+" perguntas. Continue quando quiser.")
            GamePanel {
                Text("Você acertou "+correct+" de "+session.size+"!",
                    style=MaterialTheme.typography.headlineMedium,
                    color=GameColors.navy,fontWeight=FontWeight.Bold)
                GameProgress(correct,session.size,"Acertos")
                Text("O importante é explorar e aprender. Não há punições.",
                    color=GameColors.muted)
                if(wrong.isNotEmpty()) {
                    Text("Revisar: "+wrong.size+" questão(ões)",
                        fontWeight=FontWeight.Bold,color=GameColors.ink)
                    wrong.take(8).forEach {q->
                        Text("• "+q.enunciado+"\n  "+q.explicacao,
                            color=GameColors.muted,style=MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if(wrong.isNotEmpty()){
                GameAction("Refazer apenas as que errei",
                    onClick={start(wrong.toList())},modifier=Modifier.fillMaxWidth())
            }
            GameAction("Nova partida",onClick={create()},modifier=Modifier.fillMaxWidth())
            GameAction("Alterar dificuldade ou quantidade",onClick={setup=true},
                modifier=Modifier.fillMaxWidth(),secondary=true)
        } else {
            val pergunta=session[index]
            GamePanel {
                GameProgress(index+1,session.size,"Questão atual")
                Text("Acertos: "+correct,
                    color=GameColors.blue,fontWeight=FontWeight.Bold)
                Text(pergunta.assunto.uppercase(),
                    color=GameColors.teal,style=MaterialTheme.typography.labelLarge,
                    fontWeight=FontWeight.Bold)
                Text(pergunta.enunciado,
                    color=GameColors.navy,
                    style=MaterialTheme.typography.headlineSmall,
                    fontWeight=FontWeight.Bold)
                pergunta.opcoes.forEachIndexed {i,choice->
                    val isCorrect=selected>=0&&i==pergunta.correta
                    val incorrect=selected==i&&i!=pergunta.correta
                    val surface=when{
                        isCorrect->GameColors.success
                        incorrect->Color(0xFFFFE5DA)
                        selected==i->GameColors.selected
                        else->Color.White
                    }
                    OutlinedButton(
                        onClick={
                            if(selected==-1){
                                selected=i
                                if(i==pergunta.correta)correct++ else wrong.add(pergunta)
                            }
                        },
                        modifier=Modifier.fillMaxWidth().heightIn(min=57.dp),
                        shape=RoundedCornerShape(15.dp),
                        colors=ButtonDefaults.outlinedButtonColors(
                            containerColor=surface,contentColor=GameColors.navy),
                        border=BorderStroke(if(isCorrect)2.dp else 1.dp,
                            if(isCorrect)GameColors.teal else GameColors.border)
                    ) {
                        Text(choice,style=MaterialTheme.typography.bodyLarge,
                            color=GameColors.navy,fontWeight=FontWeight.SemiBold,
                            textAlign=TextAlign.Center)
                    }
                }
                if(selected>=0){
                    HorizontalDivider(color=GameColors.border)
                    Text(if(selected==pergunta.correta)"Boa resposta!" else
                        "Veja como resolver",color=GameColors.teal,
                        fontWeight=FontWeight.Bold)
                    Text(pergunta.explicacao,color=GameColors.ink)
                } else {
                    Text("Escolha uma opção. A explicação aparece depois da resposta.",
                        color=GameColors.muted,
                        style=MaterialTheme.typography.bodySmall)
                }
            }
            GameAction(
                if(index==session.lastIndex)"Ver meu resultado" else "Próxima pergunta",
                onClick={
                    if(index==session.lastIndex)end=true
                    else {index++;selected=-1}
                },enabled=selected!=-1,modifier=Modifier.fillMaxWidth())
            GameAction("Configurar outra partida",onClick={setup=true},
                secondary=true,modifier=Modifier.fillMaxWidth())
        }
    }
}
