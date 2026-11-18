package com.example.meutea.menu.jogos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meutea.R
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

private const val SIDE=11
internal fun direcaoPermitida(atual:String,nova:String):Boolean {
    val opposite=when(atual){
        "cima"->"baixo";"baixo"->"cima";"esquerda"->"direita";else->"esquerda"
    }
    return nova!=opposite
}

private fun snakeNext(index:Int,direction:String,wrap:Boolean):Int {
    val x=index%SIDE;val y=index/SIDE
    val (dx,dy)=when(direction){
        "cima"->0 to -1;"baixo"->0 to 1;"esquerda"->-1 to 0;else->1 to 0
    }
    var nx=x+dx;var ny=y+dy
    if(wrap){ nx=(nx+SIDE)%SIDE;ny=(ny+SIDE)%SIDE }
    else if(nx !in 0 until SIDE||ny !in 0 until SIDE)return -1
    return ny*SIDE+nx
}
private fun foodFor(body:List<Int>):Int {
    return (0 until SIDE*SIDE).filterNot{it in body}.randomOrNull()?:-1
}

@Composable internal fun JogoCobrinha(onBack:()->Unit) {
    var modo by remember{mutableStateOf("Tranquilo")}
    var velocidade by remember{mutableStateOf("Calmo")}
    var body by remember{mutableStateOf(listOf(60,59,58))}
    var direction by remember{mutableStateOf("direita")}
    var food by remember{mutableIntStateOf(22)}
    var score by remember{mutableIntStateOf(0)}
    var best by remember{mutableIntStateOf(0)}
    var playing by remember{mutableStateOf(false)}
    var ended by remember{mutableStateOf(false)}
    var rodada by remember{mutableIntStateOf(0)}

    fun reset(){
        body=listOf(60,59,58);direction="direita";food=22;score=0
        playing=false;ended=false;rodada++
    }
    fun turn(to:String){
        if(direcaoPermitida(direction,to))direction=to
    }
    val delayMs=when(velocidade){"Calmo"->760L;"Normal"->530L;else->340L}
    LaunchedEffect(playing,velocidade,modo,rodada) {
        while(playing&&!ended){
            delay(delayMs)
            val next=snakeNext(body.first(),direction,modo=="Tranquilo")
            val eating=next==food
            if(next<0||(next in if(eating)body else body.dropLast(1))){
                if(modo=="Clássico") {
                    ended=true;playing=false
                }
                // No modo tranquilo, é possível escolher outra direção sem perder.
                continue
            }
            val moved=listOf(next)+(if(eating)body else body.dropLast(1))
            body=moved
            if(eating){
                score++
                if(score>best)best=score
                food=foodFor(body)
                if(food<0){ended=true;playing=false}
            }
        }
    }
    TelaJogo("Cobrinha",onBack) {
        GameHeadline("Caça às frutas","Escolha seu ritmo. Use as setas ou deslize na grade.")
        GamePanel {
            Text("Modo de jogo",fontWeight=FontWeight.Bold,color=GameColors.ink)
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                listOf("Tranquilo","Clássico").forEach{item->
                    OutlinedButton(onClick={reset();modo=item},
                        modifier=Modifier.weight(1f),shape=RoundedCornerShape(12.dp),
                        colors=ButtonDefaults.outlinedButtonColors(
                            containerColor=if(item==modo)GameColors.navy else Color.White,
                            contentColor=if(item==modo)Color.White else GameColors.ink)
                    ){Text(item)}
                }
            }
            Text(if(modo=="Tranquilo")
                "Sem derrota: atravesse as bordas e mude a direção quando precisar."
                else "Desafio: bater nas bordas ou no corpo encerra a partida.",
                color=GameColors.muted,fontSize=13.sp)
            Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                listOf("Calmo","Normal","Rápido").forEach{item->
                    OutlinedButton(onClick={velocidade=item},
                        modifier=Modifier.weight(1f),
                        contentPadding=PaddingValues(horizontal=2.dp),
                        shape=RoundedCornerShape(11.dp),
                        colors=ButtonDefaults.outlinedButtonColors(
                            containerColor=if(item==velocidade)GameColors.selected else Color.White,
                            contentColor=GameColors.ink)
                    ){Text(item,maxLines=1)}
                }
            }
        }
        GamePanel {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Text("🍎  Frutas: "+score,color=GameColors.ink,fontWeight=FontWeight.Bold)
                Text("Recorde: "+best,color=GameColors.blue,fontWeight=FontWeight.Bold)
            }
            var gesture by remember{mutableStateOf(Offset.Zero)}
            Canvas(
                Modifier.fillMaxWidth().aspectRatio(1f)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFE3F0F5))
                    .pointerInput(modo) {
                        detectDragGestures(
                            onDragStart={gesture=Offset.Zero},
                            onDragEnd={
                                if(abs(gesture.x)>abs(gesture.y)&&abs(gesture.x)>18)
                                    turn(if(gesture.x>0)"direita" else "esquerda")
                                else if(abs(gesture.y)>18)
                                    turn(if(gesture.y>0)"baixo" else "cima")
                            },
                            onDrag={change,drag->gesture+=drag;change.consume()}
                        )
                    }
            ) {
                val cell=size.width/SIDE
                repeat(SIDE){y->repeat(SIDE){x->
                    drawRoundRect(
                        color=if((x+y)%2==0)Color(0xFFDAEEF0) else Color(0xFFCCE5E9),
                        topLeft=Offset(x*cell,y*cell),
                        size=Size(cell-1.5f,cell-1.5f),
                        cornerRadius=CornerRadius(3f))
                }}
                if(food>=0){
                    val center=Offset((food%SIDE+0.5f)*cell,(food/SIDE+0.5f)*cell)
                    drawCircle(Color(0xFFE65454),radius=cell*0.33f,center=center)
                    drawCircle(Color(0xFF7DCE83),radius=cell*0.12f,
                        center=center+Offset(cell*0.08f,-cell*0.29f))
                }
                body.forEachIndexed{index,p->
                    drawRoundRect(
                        color=if(index==0)GameColors.navy else Color(0xFF168B71),
                        topLeft=Offset((p%SIDE)*cell+1.5f,(p/SIDE)*cell+1.5f),
                        size=Size(cell-3f,cell-3f),
                        cornerRadius=CornerRadius(cell*0.25f)
                    )
                    if(index==0){
                        drawCircle(Color.White,cell*0.065f,
                            Offset((p%SIDE+0.32f)*cell,(p/SIDE+0.34f)*cell))
                        drawCircle(Color.White,cell*0.065f,
                            Offset((p%SIDE+0.68f)*cell,(p/SIDE+0.34f)*cell))
                    }
                }
            }
            if(ended) Text("Partida concluída. Você pode começar outra!",
                color=GameColors.navy,fontWeight=FontWeight.Bold)
            else Text(if(playing)"Em movimento • use as setas para mudar a direção"
                else "Pausado • toque em Jogar para começar",
                color=GameColors.muted)
        }
        Row(horizontalArrangement=Arrangement.spacedBy(9.dp)) {
            GameAction(if(playing)"Pausar" else "Jogar",onClick={playing=!playing&&!ended},
                enabled=!ended,modifier=Modifier.weight(1f))
            GameAction("Reiniciar",onClick={reset()},secondary=true,modifier=Modifier.weight(1f))
        }
        GamePanel {
            Text("Direção",fontWeight=FontWeight.Bold,color=GameColors.ink)
            Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,
                verticalArrangement=Arrangement.spacedBy(6.dp)) {
                GameAction("↑",onClick={turn("cima")},modifier=Modifier.width(86.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    GameAction("←",onClick={turn("esquerda")},modifier=Modifier.width(86.dp))
                    GameAction("↓",onClick={turn("baixo")},modifier=Modifier.width(86.dp))
                    GameAction("→",onClick={turn("direita")},modifier=Modifier.width(86.dp))
                }
            }
        }
    }
}

private data class FinderItem(val title:String,val res:Int)
private val objects=listOf(
    FinderItem("Água",R.drawable.tea_pic_agua),
    FinderItem("Comida",R.drawable.tea_pic_comer),
    FinderItem("Banheiro",R.drawable.tea_pic_banheiro),
    FinderItem("Ajuda",R.drawable.tea_pic_ajuda),
    FinderItem("Alegria",R.drawable.tea_pic_feliz),
    FinderItem("Tristeza",R.drawable.tea_pic_triste),
    FinderItem("Cansaço",R.drawable.tea_pic_cansaco),
    FinderItem("Conforto",R.drawable.tea_pic_dor),
    FinderItem("Silêncio",R.drawable.tea_pic_silencio),
    FinderItem("Pausa",R.drawable.tea_pic_pausa),
    FinderItem("Sim",R.drawable.tea_pic_sim),
    FinderItem("Não",R.drawable.tea_pic_nao),
    FinderItem("Escolher",R.drawable.tea_pic_escolher),
    FinderItem("Parar",R.drawable.tea_pic_parar),
    FinderItem("Olá",R.drawable.tea_pic_oi),
    FinderItem("Obrigado",R.drawable.tea_pic_obrigado),
    FinderItem("Dúvida",R.drawable.tea_pic_desculpa),
    FinderItem("Repetir",R.drawable.tea_pic_repetir),
    FinderItem("Conversa",R.drawable.tea_action_chat),
    FinderItem("Calendário",R.drawable.tea_action_calendar),
    FinderItem("Jogos",R.drawable.tea_action_game),
    FinderItem("Documento",R.drawable.tea_action_cert),
    FinderItem("Identidade",R.drawable.tea_action_id),
    FinderItem("Livros",R.drawable.tea_game_books),
    FinderItem("Quebra-cabeça",R.drawable.tea_game_puzzle)
)

@Composable internal fun JogoProcureObjeto(onBack:()->Unit) {
    var nivel by remember{mutableStateOf("Inicial")}
    var total by remember{mutableIntStateOf(20)}
    var round by remember{mutableIntStateOf(0)}
    var found by remember(round,nivel){mutableStateOf(false)}
    var tips by remember(round,nivel){mutableStateOf(false)}
    var attempts by remember(round,nivel){mutableIntStateOf(0)}
    var finished by remember{mutableStateOf(false)}
    val n=when(nivel){"Inicial"->3;"Intermediário"->4;else->5}
    val items=remember(round,nivel) {
        val rnd=Random(round*147+objects.size)
        objects.shuffled(rnd).take(n*n).shuffled(rnd)
    }
    val target=remember(round,nivel){items[Random(round*37+5).nextInt(items.size)]}
    fun next() {
        if(round+1>=total){finished=true}
        else round++
    }
    TelaJogo("Encontre a figura",onBack) {
        GameHeadline("Olhar atento","Procure uma figura na grade. Sem cronômetro e sem pressa.")
        if(finished) {
            GamePanel {
                Text("Você explorou "+total+" desafios!",
                    style=MaterialTheme.typography.headlineSmall,
                    fontWeight=FontWeight.Bold,color=GameColors.navy)
                Text("Pode continuar com outras figuras ou escolher outra atividade.",
                    color=GameColors.muted)
                GameAction("Jogar novamente",onClick={round=0;finished=false},
                    modifier=Modifier.fillMaxWidth())
            }
        } else {
            GamePanel {
                GameLevelPicker(nivel){nivel=it;round=0;finished=false}
                GameCountPicker(total){total=it;round=0;finished=false}
            }
            GamePanel {
                GameProgress(round+if(found)1 else 0,total,"Desafios concluídos")
                Text("Encontre: "+target.title,color=GameColors.navy,
                    fontWeight=FontWeight.ExtraBold,
                    style=MaterialTheme.typography.titleLarge)
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Image(painterResource(target.res),
                        contentDescription=target.title,modifier=Modifier.size(75.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Toque na imagem igual.",color=GameColors.ink)
                        if(tips)Text("Dica: observe as cores e os detalhes do desenho.",
                            color=GameColors.muted)
                    }
                }
                Column(verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    repeat(n){y->
                        Row(horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                            repeat(n){x->
                                val item=items[y*n+x]
                                Box(
                                    Modifier.weight(1f).aspectRatio(1f)
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(if(found&&item==target)
                                            GameColors.success else GameColors.background)
                                        .border(if(found&&item==target)2.dp else 1.dp,
                                            if(found&&item==target)GameColors.teal else GameColors.border,
                                            RoundedCornerShape(13.dp))
                                        .clickable {
                                            if(!found) {
                                                attempts++
                                                found=item==target
                                            }
                                        },
                                    contentAlignment=Alignment.Center
                                ) {
                                    Image(painterResource(item.res),
                                        contentDescription=item.title,
                                        modifier=Modifier.fillMaxSize().padding(5.dp))
                                }
                            }
                        }
                    }
                }
                Text(if(found)"Encontrou! Muito bem. Vamos para outra figura?"
                    else if(attempts>0)"Essa ainda não é a figura. Observe e tente novamente."
                    else "As figuras têm nomes acessíveis para leitores de tela.",
                    color=GameColors.ink)
            }
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                GameAction("Dica",onClick={tips=true},
                    secondary=true,modifier=Modifier.weight(1f))
                GameAction("Próximo",onClick={next()},enabled=found,
                    modifier=Modifier.weight(1f))
            }
        }
    }
}
