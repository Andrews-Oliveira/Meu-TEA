package com.example.meutea.menu.jogos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val guides=listOf(
    "Encontre uma posição que seja confortável para você.",
    "Se desejar, observe as cores que existem ao seu redor.",
    "Você pode ajustar a luz, o som ou a temperatura, se for possível.",
    "Se quiser, sinta o contato dos pés com o chão.",
    "Observe alguma coisa bonita ou interessante perto de você.",
    "Se for confortável, perceba o ritmo natural da sua respiração.",
    "Escolha ouvir um som agradável ou permanecer em silêncio.",
    "Você pode esticar os braços devagar, se isso for confortável.",
    "Pense numa atividade de que você gosta.",
    "Beba água se tiver vontade e acesso a ela.",
    "Você decide se quer continuar ou retomar sua atividade.",
    "Obrigado por reservar um momento para você."
)
private val story=listOf(
    "Você encontrou a entrada de um pequeno jardim.",
    "Uma trilha se divide em dois caminhos tranquilos.",
    "Uma borboleta aparece perto de uma flor.",
    "Há uma fonte de água e um banco confortável.",
    "Você encontra uma árvore com folhas bem diferentes.",
    "Um passarinho canta ao longe.",
    "Uma pequena ponte cruza um riacho.",
    "O céu começa a mudar de cor.",
    "Você percebe um canteiro com várias formas de flores.",
    "O vento move as folhas suavemente.",
    "Você chega a uma clareira iluminada.",
    "Há uma mesa com papel e lápis para desenhar.",
    "Um novo caminho mostra árvores de diferentes alturas.",
    "Você escolhe um lugar para descansar.",
    "O passeio está terminando. O jardim estará aqui para outra visita."
)
private val storyChoices=listOf(
    "Olhar as flores" to "Observar a trilha",
    "Seguir o caminho azul" to "Seguir o caminho verde",
    "Observar a borboleta" to "Admirar as folhas",
    "Sentar por um instante" to "Observar a água",
    "Desenhar a árvore" to "Olhar os galhos"
)
private val palette=listOf(
    "Azul" to Color(0xFF2878C6),
    "Verde" to Color(0xFF289D68),
    "Amarelo" to Color(0xFFF2BE40),
    "Roxo" to Color(0xFF9169C6)
)

@Composable internal fun JogoPausa(onBack:()->Unit) {
    var mode by remember{mutableStateOf("")}
    var step by remember(mode){mutableIntStateOf(0)}
    var selected by remember(mode,step){mutableIntStateOf(-1)}
    val garden=remember {mutableStateListOf<Int>().apply{repeat(25){add(0)}}}
    var gardenRound by remember{mutableIntStateOf(0)}
    val targetIndex=remember(mode,step){Random(step+531).nextInt(4)}
    val targets=remember(mode,step){
        List(16){index->
            if(index==0)targetIndex else Random(step*137+index).nextInt(4)
        }.shuffled(Random(step*91+2))
    }
    val matched=remember(mode,step){mutableStateListOf<Int>()}
    TelaJogo("Meu momento",onBack) {
        if(mode.isEmpty()){
            GameHeadline("Escolha um momento para você","Sem cronômetro e sem exercícios obrigatórios.")
            GamePanel {
                listOf(
                    Triple("Jardim interativo","Cuide de 25 pequenas plantas.","jardim"),
                    Triple("Descobrir cores","Encontre cores numa grade tranquila.","cores"),
                    Triple("História de escolhas","Explore 15 cenas de um jardim.","historia"),
                    Triple("Pausa guiada","12 sugestões opcionais, no seu ritmo.","guiada")
                ).forEach { (name,description,id) ->
                    Card(onClick={mode=id},modifier=Modifier.fillMaxWidth(),
                        shape=RoundedCornerShape(17.dp),
                        colors=CardDefaults.cardColors(containerColor=GameColors.background),
                        border=androidx.compose.foundation.BorderStroke(1.dp,GameColors.border)){
                        Column(Modifier.fillMaxWidth().padding(15.dp)) {
                            Text(name,color=GameColors.navy,fontWeight=FontWeight.Bold)
                            Text(description,color=GameColors.muted)
                        }
                    }
                }
            }
            Text("As atividades são para diversão e bem-estar. " +
                "Você pode parar quando quiser.",
                color=GameColors.muted,style=MaterialTheme.typography.bodySmall)
        } else if(mode=="jardim") {
            GameHeadline("Seu jardim","Toque nos canteiros para fazer as plantas crescerem.")
            GamePanel {
                GameProgress(garden.count{it==3},25,"Flores crescidas")
                Column(verticalArrangement=Arrangement.spacedBy(6.dp)){
                    repeat(5){y->
                        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                            repeat(5){x->
                                val index=y*5+x
                                val growth=garden[index]
                                Box(
                                    Modifier.weight(1f).aspectRatio(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE3F5ED))
                                        .border(1.dp,GameColors.border,RoundedCornerShape(12.dp))
                                        .clickable{garden[index]=(growth+1).coerceAtMost(3)},
                                    contentAlignment=Alignment.Center
                                ){
                                    Canvas(Modifier.fillMaxSize().padding(5.dp)){
                                        val center=Offset(size.width*0.5f,size.height*0.65f)
                                        drawCircle(Color(0xFFD0A770),size.width*0.20f,
                                            center+Offset(0f,size.height*0.25f))
                                        if(growth>=1){
                                            drawLine(Color(0xFF248C53),center,
                                                center-Offset(0f,size.height*0.25f),
                                                strokeWidth=4f)
                                        }
                                        if(growth>=2){
                                            drawCircle(Color(0xFF3FAF70),size.width*0.12f,
                                                center-Offset(size.width*0.13f,size.height*0.04f))
                                            drawCircle(Color(0xFF3FAF70),size.width*0.12f,
                                                center+Offset(size.width*0.13f,-size.height*0.04f))
                                        }
                                        if(growth>=3){
                                            val flower=center-Offset(0f,size.height*0.3f)
                                            repeat(5){k->
                                                val angle=k*Math.PI*2/5
                                                val ox=kotlin.math.cos(angle).toFloat()*size.width*0.16f
                                                val oy=kotlin.math.sin(angle).toFloat()*size.width*0.16f
                                                drawCircle(Color(0xFFF49AC1),size.width*0.12f,
                                                    flower+Offset(ox,oy))
                                            }
                                            drawCircle(Color(0xFFFFD069),size.width*0.10f,flower)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Text(if(garden.all{it==3})"Seu jardim está cheio de flores!"
                    else "Cada toque ajuda uma planta a crescer. Não há pressa.",
                    color=GameColors.muted)
            }
            GameAction("Criar novo jardim",onClick={
                garden.indices.forEach{garden[it]=0};gardenRound++
            },modifier=Modifier.fillMaxWidth())
        } else if(mode=="cores") {
            val color=palette[targetIndex]
            GameHeadline("Descobrir cores","Procure as peças da cor solicitada.")
            GamePanel {
                GameProgress(step,20,"Rodadas")
                Text("Encontre: "+color.first,style=MaterialTheme.typography.titleLarge,
                    color=GameColors.navy,fontWeight=FontWeight.Bold)
                Box(Modifier.fillMaxWidth().height(16.dp)
                    .clip(RoundedCornerShape(8.dp)).background(color.second))
                Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    repeat(4){y->
                        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                            repeat(4){x->
                                val i=y*4+x
                                val ok=i in matched
                                Box(Modifier.weight(1f).aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if(ok)GameColors.success else palette[targets[i]].second)
                                    .border(2.dp,if(ok)GameColors.teal else Color.White,
                                        RoundedCornerShape(12.dp))
                                    .clickable {if(targets[i]==targetIndex&&!ok)matched.add(i)},
                                    contentAlignment=Alignment.Center) {
                                    if(ok)Text("✓",fontSize=25.sp,color=GameColors.navy)
                                }
                            }
                        }
                    }
                }
                val count=targets.count{it==targetIndex}
                Text("Você encontrou "+matched.size+" de "+count+" peças.",
                    color=GameColors.muted)
            }
            GameAction(if(step==19)"Começar de novo" else "Próximas cores",
                onClick={step=(step+1)%20},modifier=Modifier.fillMaxWidth(),
                enabled=matched.size==targets.count{it==targetIndex})
        } else if(mode=="historia") {
            GameHeadline("Passeio pelo jardim","Uma história interativa com escolhas sem resposta errada.")
            GamePanel {
                GameProgress(step+1,story.size,"Cena")
                Text(story[step],style=MaterialTheme.typography.headlineSmall,
                    fontWeight=FontWeight.Bold,color=GameColors.navy,
                    textAlign=TextAlign.Center,
                    modifier=Modifier.fillMaxWidth().padding(vertical=20.dp))
                val choices=storyChoices[step%storyChoices.size]
                listOf(choices.first,choices.second).forEachIndexed {i,option->
                    OutlinedButton(onClick={selected=i},modifier=Modifier.fillMaxWidth()
                        .heightIn(min=55.dp),shape=RoundedCornerShape(14.dp),
                        colors=ButtonDefaults.outlinedButtonColors(
                            containerColor=if(selected==i)GameColors.selected else Color.White,
                            contentColor=GameColors.navy),
                        border=androidx.compose.foundation.BorderStroke(1.dp,GameColors.border)
                    ){Text(option)}
                }
                if(selected>=0)Text("Boa escolha! A história continua do seu jeito.",
                    color=GameColors.teal,fontWeight=FontWeight.Bold)
            }
            GameAction(if(step==story.lastIndex)"Recomeçar história" else "Continuar",
                onClick={step=(step+1)%story.size},enabled=selected>=0,
                modifier=Modifier.fillMaxWidth())
        } else {
            GameHeadline("Pausa guiada","Escolha apenas o que for confortável para você.")
            GamePanel {
                GameProgress(step+1,guides.size,"Sugestões")
                Text("✦",fontSize=45.sp,color=GameColors.teal,
                    modifier=Modifier.align(Alignment.CenterHorizontally))
                Text(guides[step],style=MaterialTheme.typography.headlineSmall,
                    color=GameColors.navy,fontWeight=FontWeight.Bold,
                    modifier=Modifier.fillMaxWidth().padding(vertical=22.dp),
                    textAlign=TextAlign.Center)
                Text("Se essa sugestão não ajudar, pule para a próxima.",
                    color=GameColors.muted)
            }
            GameAction(if(step==guides.lastIndex)"Recomeçar" else "Próxima sugestão",
                onClick={step=(step+1)%guides.size},modifier=Modifier.fillMaxWidth())
        }
        if(mode.isNotEmpty()){
            GameAction("Voltar às atividades",onClick={mode=""},
                modifier=Modifier.fillMaxWidth(),secondary=true)
        }
    }
}
