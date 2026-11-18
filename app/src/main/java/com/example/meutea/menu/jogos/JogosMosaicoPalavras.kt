package com.example.meutea.menu.jogos

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import com.example.meutea.R

private val figuras = listOf(
    "Alegria" to R.drawable.tea_pic_feliz,
    "Água" to R.drawable.tea_pic_agua,
    "Natureza" to R.drawable.tea_pic_oi,
    "Conforto" to R.drawable.tea_pic_cansaco,
    "Pausa" to R.drawable.tea_pic_pausa,
    "Alimentação" to R.drawable.tea_pic_comer,
    "Amizade" to R.drawable.tea_pic_obrigado,
    "Escolhas" to R.drawable.tea_pic_escolher
)

@Composable internal fun JogoQuebraCabeca(onBack:()->Unit) {
    var lado by remember { mutableIntStateOf(3) }
    var escolhida by remember { mutableIntStateOf(0) }
    var rodada by remember { mutableIntStateOf(0) }
    var movimentos by remember { mutableIntStateOf(0) }
    var numeros by remember { mutableStateOf(true) }
    val pecas=remember(lado,escolhida,rodada) {
        mutableStateListOf<Int>().apply { addAll(gerarPuzzle(lado)) }
    }
    val pronto=pecas.withIndex().all {
        it.value==(if(it.index==lado*lado-1)0 else it.index+1)
    }
    val context=LocalContext.current
    val res=figuras[escolhida].second
    val figura=remember(context,res) {
        val bitmap=Bitmap.createBitmap(720,720,Bitmap.Config.ARGB_8888)
        ContextCompat.getDrawable(context,res)?.apply {
            setBounds(0,0,720,720);draw(AndroidCanvas(bitmap))
        }
        bitmap.asImageBitmap()
    }
    TelaJogo("Quebra-cabeça",onBack) {
        GameHeadline("Monte uma figura","Escolha a arte e o tamanho. Sem cronômetro.")
        GamePanel {
            Text("Peças",color=GameColors.ink,fontWeight=FontWeight.Bold)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                listOf(3,4,5,6).forEach { n ->
                    val selected=n==lado
                    OutlinedButton(
                        onClick={lado=n;movimentos=0},
                        modifier=Modifier.weight(1f).heightIn(min=48.dp),
                        contentPadding=PaddingValues(0.dp),
                        colors=ButtonDefaults.outlinedButtonColors(
                            containerColor=if(selected)GameColors.navy else Color.White,
                            contentColor=if(selected)Color.White else GameColors.ink),
                        border=BorderStroke(1.dp,if(selected)GameColors.navy else GameColors.border),
                        shape=RoundedCornerShape(12.dp)
                    ) { Text(n.toString()+"×"+n,fontWeight=FontWeight.Bold) }
                }
            }
            Text("Figura",color=GameColors.ink,fontWeight=FontWeight.Bold)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement=Arrangement.spacedBy(9.dp)) {
                figuras.forEachIndexed { index,pair ->
                    Card(
                        onClick={escolhida=index;movimentos=0},
                        colors=CardDefaults.cardColors(
                            containerColor=if(index==escolhida)GameColors.selected else Color.White),
                        border=BorderStroke(if(index==escolhida)2.dp else 1.dp,
                            if(index==escolhida)GameColors.teal else GameColors.border),
                        shape=RoundedCornerShape(14.dp)
                    ) {
                        Column(Modifier.padding(6.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                            Image(painterResource(pair.second),contentDescription=pair.first,
                                modifier=Modifier.size(59.dp))
                            Text(pair.first,color=GameColors.ink,fontSize=12.sp)
                        }
                    }
                }
            }
        }
        GamePanel {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Image(bitmap=figura,contentDescription="Imagem completa de referência",
                    modifier=Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(if(pronto)"Você completou a imagem!" else "Figura de referência",
                        fontWeight=FontWeight.Bold,color=GameColors.navy)
                    Text(movimentos.toString()+" movimentos • "+(lado*lado-1)+" peças",
                        color=GameColors.muted,fontSize=13.sp)
                }
            }
            Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                repeat(lado){row->
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(3.dp)) {
                        repeat(lado){col->
                            val at=row*lado+col
                            val tile=pecas[at]
                            val movable=tile!=0&&at in vizinhosDe(pecas.indexOf(0),lado)
                            Box(Modifier.weight(1f).aspectRatio(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if(tile==0)Color(0xFFD7E6F0) else Color.White)
                                .border(if(movable)2.dp else 1.dp,
                                    if(movable)GameColors.teal else GameColors.border,
                                    RoundedCornerShape(6.dp))
                                .clickable(enabled=movable&&!pronto) {
                                    val empty=pecas.indexOf(0)
                                    pecas[empty]=tile;pecas[at]=0;movimentos++
                                },
                                contentAlignment=Alignment.Center) {
                                if(tile!=0){
                                    val index=tile-1
                                    Canvas(Modifier.fillMaxSize()) {
                                        val w=figura.width/lado;val h=figura.height/lado
                                        drawImage(figura,
                                            srcOffset=IntOffset((index%lado)*w,(index/lado)*h),
                                            srcSize=IntSize(w,h),
                                            dstSize=IntSize(size.width.toInt(),size.height.toInt()),
                                            filterQuality=FilterQuality.High)
                                    }
                                    if(numeros) {
                                        Text(tile.toString(),fontSize=if(lado>=5)12.sp else 15.sp,
                                            fontWeight=FontWeight.Bold,color=Color.White,
                                            modifier=Modifier.align(Alignment.TopStart)
                                                .background(Color(0xC5102D49),RoundedCornerShape(5.dp))
                                                .padding(horizontal=5.dp,vertical=2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Text(if(pronto)"Parabéns! Escolha outra arte ou dificuldade."
                else "Mova uma peça com borda verde para o espaço vazio.",
                color=GameColors.muted)
        }
        Row(horizontalArrangement=Arrangement.spacedBy(9.dp)) {
            GameAction("Embaralhar",onClick={rodada++;movimentos=0},
                modifier=Modifier.weight(1f))
            GameAction(if(numeros)"Ocultar números" else "Mostrar números",
                onClick={numeros=!numeros},
                modifier=Modifier.weight(1f),secondary=true)
        }
    }
}

@Composable internal fun JogoCacaPalavras(onBack:()->Unit) {
    var nivel by remember{ mutableStateOf("Inicial") }
    var tema by remember{ mutableStateOf(temasPalavras.first()) }
    var rodada by remember{ mutableIntStateOf(1) }
    val jogo=remember(nivel,tema,rodada) {
        gerarCacaPalavras(nivel,tema,rodada*137+tema.hashCode())
    }
    val selected=remember(nivel,tema,rodada){mutableStateListOf<Int>()}
    val found=remember(nivel,tema,rodada){mutableStateMapOf<String,List<Int>>()}
    var feedback by remember(nivel,tema,rodada){
        mutableStateOf("Selecione letras adjacentes até formar uma palavra.")
    }
    fun selecionar(cell:Int) {
        if(!caminhoValido(selected,cell,jogo.tamanho))selected.clear()
        if(caminhoValido(selected,cell,jogo.tamanho))selected.add(cell)
        val palavra=descobrirPalavra(selected,jogo.respostas)
        if(palavra!=null&&!found.containsKey(palavra)) {
            found[palavra]=selected.toList()
            feedback="Encontrou "+palavra+"!"
            selected.clear()
        } else if(selected.size>=jogo.tamanho){
            selected.clear()
            feedback="Experimente outra sequência."
        }
    }
    TelaJogo("Caça-palavras",onBack) {
        GameHeadline("Descubra as palavras","Níveis e temas diferentes em cada rodada.")
        GamePanel {
            GameLevelPicker(nivel){nivel=it;rodada++}
            Text("Temas",fontWeight=FontWeight.Bold,color=GameColors.ink)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement=Arrangement.spacedBy(7.dp)){
                temasPalavras.forEach { item ->
                    OutlinedButton(onClick={tema=item;rodada++},
                        shape=RoundedCornerShape(11.dp),
                        colors=ButtonDefaults.outlinedButtonColors(
                            containerColor=if(tema==item)GameColors.navy else Color.White,
                            contentColor=if(tema==item)Color.White else GameColors.ink)
                    ){Text(item)}
                }
            }
        }
        GamePanel {
            GameProgress(found.size,jogo.respostas.size,"Palavras encontradas")
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement=Arrangement.spacedBy(6.dp)){
                jogo.respostas.keys.forEach { word ->
                    val ok=found.containsKey(word)
                    Box(Modifier.clip(RoundedCornerShape(10.dp))
                        .background(if(ok)GameColors.success else GameColors.background)
                        .border(1.dp,if(ok)GameColors.teal else GameColors.border,
                            RoundedCornerShape(10.dp))
                        .padding(horizontal=10.dp,vertical=8.dp)){
                        Text((if(ok)"✓ " else "")+word,
                            color=GameColors.ink,fontWeight=FontWeight.Bold,fontSize=13.sp)
                    }
                }
            }
            Column(verticalArrangement=Arrangement.spacedBy(2.dp)) {
                repeat(jogo.tamanho){r->
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(2.dp)){
                        repeat(jogo.tamanho){c->
                            val pos=r*jogo.tamanho+c
                            val ok=found.values.any{pos in it}
                            val chosen=pos in selected
                            Box(Modifier.weight(1f).aspectRatio(1f)
                                .clip(RoundedCornerShape(5.dp))
                                .background(when {
                                    ok->GameColors.success
                                    chosen->GameColors.accent
                                    else->GameColors.background
                                })
                                .border(1.dp,if(chosen)GameColors.navy else
                                    if(ok)GameColors.teal else GameColors.border,
                                    RoundedCornerShape(5.dp))
                                .clickable{selecionar(pos)},contentAlignment=Alignment.Center){
                                Text(jogo.letras[pos].toString(),
                                    fontSize=when(jogo.tamanho){6->18.sp;8->16.sp;else->13.sp},
                                    color=GameColors.ink,fontWeight=FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
            Text(feedback,color=GameColors.ink,fontWeight=FontWeight.Medium)
            if(found.size==jogo.respostas.size){
                Text("Todas as palavras encontradas!",color=GameColors.teal,
                    fontWeight=FontWeight.Bold)
            }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){
            GameAction("Limpar seleção",onClick={selected.clear()},
                modifier=Modifier.weight(1f),secondary=true)
            GameAction(if(found.size==jogo.respostas.size)"Próxima rodada" else "Nova grade",
                onClick={rodada++},modifier=Modifier.weight(1f))
        }
    }
}
