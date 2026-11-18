package com.example.meutea.menu.jogos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.meutea.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

/** Um design previsível e legível, sem o fundo ilustrado competindo com os controles. */
internal object GameColors {
    val navy = Color(0xFF102D49)
    val blue = Color(0xFF1067A1)
    val teal = Color(0xFF087F86)
    val ink = Color(0xFF183B54)
    val muted = Color(0xFF4A6176)
    val border = Color(0xFFB9D2E5)
    val background = Color(0xFFF0F6FC)
    val success = Color(0xFFDBF7E6)
    val selected = Color(0xFFB7EDF0)
    val accent = Color(0xFFFFC96D)
}

private data class GameEntry(val id:String,val name:String,val info:String,val drawable:Int, val tint:Color)
private val entries = listOf(
    GameEntry("quebra","Quebra-cabeça","9, 16, 25 ou 36 peças",R.drawable.tea_game_puzzle,Color(0xFF2B82C2)),
    GameEntry("palavras","Caça-palavras","Temas e níveis progressivos",R.drawable.tea_game_words,Color(0xFF169E8D)),
    GameEntry("cobra","Cobrinha","Modos tranquilo e clássico",R.drawable.tea_game_snake,Color(0xFF479E63)),
    GameEntry("objetos","Encontre o objeto","Mais figuras e desafios",R.drawable.tea_game_search,Color(0xFFCD8A28)),
    GameEntry("matematica","Matemática","Tabuada 1–9 e 10–100 questões",R.drawable.tea_game_math,Color(0xFF7564C6)),
    GameEntry("portugues","Português","Leitura, escrita e vocabulário",R.drawable.tea_game_books,Color(0xFFCC6888)),
    GameEntry("logica","Lógica","Sequências e padrões",R.drawable.tea_game_logic,Color(0xFF2773B8)),
    GameEntry("emocoes","Emoções","Histórias e expressões",R.drawable.tea_game_smile,Color(0xFFD37C53)),
    GameEntry("pausa","Meu momento","Atividades sensoriais opcionais",R.drawable.tea_game_leaf,Color(0xFF168E81))
)

@Composable fun JogosV2Screen(navController: NavHostController) {
    androidx.compose.runtime.key(navController) {
        var active by rememberSaveable {
            androidx.compose.runtime.mutableStateOf("")
        }
        when(active) {
            "quebra" -> JogoQuebraCabeca { active = "" }
            "palavras" -> JogoCacaPalavras { active = "" }
            "cobra" -> JogoCobrinha { active = "" }
            "objetos" -> JogoProcureObjeto { active = "" }
            "matematica" -> JogoPerguntas("Matemática","matematica") { active = "" }
            "portugues" -> JogoPerguntas("Português","portugues") { active = "" }
            "logica" -> JogoPerguntas("Lógica","logica") { active = "" }
            "emocoes" -> JogoEmocoes { active = "" }
            "pausa" -> JogoPausa { active = "" }
            else -> GameLibrary(onBack={navController.popBackStack()},onChoose={active=it})
        }
    }
}

@Composable internal fun GamePanel(modifier:Modifier=Modifier, content:@Composable ColumnScope.()->Unit) {
    Card(
        modifier=modifier,
        colors=CardDefaults.cardColors(containerColor=Color.White),
        border=BorderStroke(1.dp,GameColors.border),
        shape=RoundedCornerShape(22.dp),
        elevation=CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement=Arrangement.spacedBy(10.dp),
            content=content)
    }
}

@Composable internal fun GameHeadline(title:String,subtitle:String) {
    Column(Modifier.fillMaxWidth().padding(bottom=4.dp)) {
        Text(title,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,
            color=GameColors.navy)
        Text(subtitle,style=MaterialTheme.typography.bodyMedium,color=GameColors.muted)
    }
}

@Composable internal fun GameAction(
    label:String,onClick:()->Unit,modifier:Modifier=Modifier,
    enabled:Boolean=true, secondary:Boolean=false
) {
    if(secondary) OutlinedButton(
        onClick=onClick,enabled=enabled,modifier=modifier.heightIn(min=52.dp),
        shape=RoundedCornerShape(15.dp),
        border=BorderStroke(1.5.dp,GameColors.blue),
        colors=ButtonDefaults.outlinedButtonColors(
            containerColor=Color.White,contentColor=GameColors.blue,
            disabledContainerColor=Color(0xFFE1E8ED),disabledContentColor=GameColors.muted
        )
    ){ Text(label,fontWeight=FontWeight.SemiBold,maxLines=2,textAlign=TextAlign.Center) }
    else Button(
        onClick=onClick,enabled=enabled,modifier=modifier.heightIn(min=52.dp),
        shape=RoundedCornerShape(15.dp),
        colors=ButtonDefaults.buttonColors(
            containerColor=GameColors.teal, contentColor=Color.White,
            disabledContainerColor=Color(0xFFD5E0E7),disabledContentColor=GameColors.ink
        )
    ){ Text(label,fontWeight=FontWeight.Bold,maxLines=2,textAlign=TextAlign.Center) }
}

@Composable internal fun GameLevelPicker(value:String,onSelect:(String)->Unit) {
    Text("Dificuldade",fontWeight=FontWeight.Bold,color=GameColors.ink)
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)) {
        listOf("Inicial" to "Fácil","Intermediário" to "Médio","Avançado" to "Difícil").forEach { (key,label) ->
            val active=value==key
            OutlinedButton(
                onClick={onSelect(key)},modifier=Modifier.weight(1f).heightIn(min=48.dp),
                contentPadding=PaddingValues(horizontal=3.dp,vertical=2.dp),
                shape=RoundedCornerShape(13.dp),
                colors=ButtonDefaults.outlinedButtonColors(
                    containerColor=if(active)GameColors.navy else Color.White,
                    contentColor=if(active)Color.White else GameColors.ink),
                border=BorderStroke(1.dp,if(active)GameColors.navy else GameColors.border)
            ){Text(label,maxLines=1,softWrap=false,fontSize=13.sp,fontWeight=FontWeight.Bold)}
        }
    }
}

@Composable internal fun GameCountPicker(value:Int,onSelect:(Int)->Unit) {
    Text("Duração da partida",color=GameColors.ink,fontWeight=FontWeight.Bold)
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement=Arrangement.spacedBy(7.dp)) {
        (10..100 step 10).forEach {n->
            val active=n==value
            OutlinedButton(
                onClick={onSelect(n)},shape=RoundedCornerShape(12.dp),
                colors=ButtonDefaults.outlinedButtonColors(
                    containerColor=if(active)GameColors.navy else Color.White,
                    contentColor=if(active)Color.White else GameColors.ink
                ),border=BorderStroke(1.dp,if(active)GameColors.navy else GameColors.border),
                contentPadding=PaddingValues(horizontal=12.dp,vertical=5.dp)
            ){Text("$n",fontWeight=FontWeight.Bold)}
        }
    }
    Text("$value perguntas • sem limite de tempo",style=MaterialTheme.typography.bodySmall,
        color=GameColors.muted)
}

@Composable internal fun GameProgress(current:Int,total:Int,label:String="Progresso") {
    Column(verticalArrangement=Arrangement.spacedBy(7.dp)) {
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            Text(label,fontWeight=FontWeight.Bold,color=GameColors.ink)
            Text("$current / $total",color=GameColors.blue,fontWeight=FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress={if(total==0)0f else (current.toFloat()/total).coerceIn(0f,1f)},
            modifier=Modifier.fillMaxWidth().height(9.dp).clip(RoundedCornerShape(12.dp)),
            color=GameColors.teal,trackColor=Color(0xFFD9E6F0)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun TelaJogo(
    titulo:String,onBack:()->Unit,
    content:@Composable ColumnScope.()->Unit
) {
    Scaffold(containerColor=GameColors.background,topBar={
        TopAppBar(
            title={ Text(titulo,maxLines=1,overflow=TextOverflow.Ellipsis,
                fontWeight=FontWeight.Bold,color=Color.White) },
            navigationIcon={
                TextButton(onClick=onBack) {Text("Voltar",color=Color.White,fontWeight=FontWeight.Bold)}
            },
            colors=TopAppBarDefaults.topAppBarColors(containerColor=GameColors.navy)
        )
    }) { p ->
        Box(Modifier.fillMaxSize().padding(p),contentAlignment=Alignment.TopCenter) {
            Column(
                modifier=Modifier.fillMaxWidth().widthIn(max=650.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal=14.dp,vertical=18.dp)
                    .padding(bottom=24.dp),
                verticalArrangement=Arrangement.spacedBy(14.dp),content=content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun GameLibrary(onBack:()->Unit,onChoose:(String)->Unit) {
    Scaffold(containerColor=GameColors.background,topBar={
        TopAppBar(
            title={Text("Jogos e descobertas",fontWeight=FontWeight.Bold,color=Color.White)},
            navigationIcon={TextButton(onClick=onBack){Text("Voltar",color=Color.White)}},
            colors=TopAppBarDefaults.topAppBarColors(containerColor=GameColors.navy)
        )
    }) { p ->
        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns=androidx.compose.foundation.lazy.grid.GridCells.Adaptive(156.dp),
            modifier=Modifier.fillMaxSize().padding(p).widthIn(max=800.dp),
            contentPadding=PaddingValues(14.dp),
            horizontalArrangement=Arrangement.spacedBy(12.dp),
            verticalArrangement=Arrangement.spacedBy(12.dp)
        ) {
            item(span={androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan)}) {
                GamePanel {
                    Text("Escolha sua aventura",style=MaterialTheme.typography.headlineSmall,
                        color=GameColors.navy,fontWeight=FontWeight.Bold)
                    Text("Partidas maiores, atividades variadas e controles claros. " +
                        "Você decide o ritmo e pode sair quando quiser.",
                        color=GameColors.muted)
                }
            }
            items(entries.size) {i->
                val it=entries[i]
                Card(onClick={onChoose(it.id)},shape=RoundedCornerShape(22.dp),
                    colors=CardDefaults.cardColors(containerColor=Color.White),
                    border=BorderStroke(1.dp,GameColors.border),
                    elevation=CardDefaults.cardElevation(3.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment=Alignment.CenterHorizontally) {
                        Box(Modifier.fillMaxWidth().height(94.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(
                                listOf(it.tint,it.tint.copy(alpha=0.78f)))),
                            contentAlignment=Alignment.Center) {
                            Image(painterResource(it.drawable),contentDescription=null,
                                modifier=Modifier.size(62.dp))
                        }
                        Spacer(Modifier.height(11.dp))
                        Text(it.name,color=GameColors.navy,fontWeight=FontWeight.Bold,
                            maxLines=2,textAlign=TextAlign.Center)
                        Text(it.info,color=GameColors.muted,
                            style=MaterialTheme.typography.bodySmall,textAlign=TextAlign.Center,
                            modifier=Modifier.padding(top=5.dp),maxLines=3)
                    }
                }
            }
        }
    }
}
