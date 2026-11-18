package com.example.meutea.menu.jogos

import kotlin.math.abs
import kotlin.random.Random

internal fun vizinhosDe(pos:Int,lado:Int):List<Int> {
    val x=pos%lado
    val y=pos/lado
    return listOfNotNull(
        if(x>0)pos-1 else null,
        if(x<lado-1)pos+1 else null,
        if(y>0)pos-lado else null,
        if(y<lado-1)pos+lado else null
    )
}

/** Embaralhar por movimentos legais garante configuração sempre solúvel. */
internal fun gerarPuzzle(lado:Int,random:Random=Random.Default):List<Int> {
    require(lado in 3..6)
    val a=(1 until lado*lado).toMutableList().apply{add(0)}
    var anterior=-1
    repeat(30*lado*lado) {
        val zero=a.indexOf(0)
        val moves=vizinhosDe(zero,lado).filter{it!=anterior}
        val from=(moves.ifEmpty{vizinhosDe(zero,lado)}).random(random)
        a[zero]=a[from];a[from]=0;anterior=zero
    }
    if(a.withIndex().all {it.value == if(it.index == a.lastIndex)0 else it.index+1}) {
        val from=vizinhosDe(a.indexOf(0),lado).first()
        a[a.lastIndex]=a[from];a[from]=0
    }
    return a
}
internal fun novoQuebraCabeca(random:Random=Random.Default)=gerarPuzzle(3,random)

internal data class PalavraGrade(
    val tamanho:Int,val letras:List<Char>,val respostas:Map<String,List<Int>>,val tema:String
)

internal fun caminhoValido(selected:List<Int>,next:Int,size:Int=8):Boolean {
    if(next !in 0 until size*size || next in selected)return false
    if(selected.isEmpty())return true
    val from=selected.last()
    val dx=next%size-from%size
    val dy=next/size-from/size
    if(maxOf(abs(dx),abs(dy))!=1)return false
    if(selected.size<=1)return true
    return dx==selected[1]%size-selected[0]%size &&
        dy==selected[1]/size-selected[0]/size
}

private val bancosPalavras=linkedMapOf(
    "Natureza" to listOf("SOL","LUA","MAR","RIO","FLOR","NUVEM","VENTO","TERRA","CHUVA","FOLHA","ARVORE","JARDIM","PLANETA","PRAIA"),
    "Animais" to listOf("GATO","PATO","LEAO","URSO","PEIXE","COBRA","TIGRE","COELHO","ABELHA","GIRAFA","MACACO","CAVALO","TARTARUGA"),
    "Objetos" to listOf("MESA","BOLA","CASA","LIVRO","PORTA","CANETA","CADEIRA","CADERNO","LAPIS","CELULAR","JANELA","MOCHILA"),
    "Alimentos" to listOf("UVA","PAO","OVO","SUCO","BOLO","BANANA","MELAO","MANGA","CENOURA","LARANJA","ABACATE","MORANGO"),
    "Escola" to listOf("AULA","NOTA","MAPA","GIZ","TURMA","LETRA","PAPEL","LIVRO","ESCOLA","PROVA","ALUNO","APRENDER"),
    "Cidade" to listOf("RUA","LOJA","CARRO","PONTE","PARQUE","ONIBUS","BAIRRO","PESSOA","PRACA","AVENIDA","MERCADO"),
    "Esportes" to listOf("GOL","REDE","BOLA","TIME","CORRER","NADAR","PULAR","FUTEBOL","TENIS","VOLEI","ATLETA","QUADRA"),
    "Sentimentos" to listOf("PAZ","AMOR","CALMA","MEDO","RAIVA","FELIZ","TRISTE","ORGULHO","CARINHO","ALEGRIA","SAUDADE","RESPEITO"),
    "Tecnologia" to listOf("TELA","MOUSE","SOM","REDE","DADOS","VIDEO","TECLADO","JOGO","CELULAR","CAMERA","SENSOR","CODIGO")
)
internal val temasPalavras get()=bancosPalavras.keys.toList()

/** Toda palavra apresentada é realmente colocada na grade e tem caminho verificável. */
internal fun gerarCacaPalavras(
    nivel:String, tema:String,seed:Int
):PalavraGrade {
    val n=when(nivel){"Inicial"->6;"Intermediário"->8;else->10}
    val random=Random(seed)
    val dictionary=bancosPalavras[tema].orEmpty()
        .filter{it.length in 3..n}.distinct().shuffled(random)
    val target=when(n){6->4;8->6;else->8}
    val directions=if(n==6)listOf(1 to 0,0 to 1)
        else if(n==8)listOf(1 to 0,0 to 1,1 to 1)
        else listOf(1 to 0,0 to 1,1 to 1,-1 to 1)
    var best:PalavraGrade?=null
    repeat(40) {
        val grid=MutableList(n*n){' '}
        val paths=linkedMapOf<String,List<Int>>()
        for(word in dictionary) {
            if(paths.size>=target)break
            var found:List<Int>?=null
            repeat(250) {
                if(found!=null)return@repeat
                val (dx,dy)=directions.random(random)
                val x=random.nextInt(n);val y=random.nextInt(n)
                val endX=x+(word.length-1)*dx
                val endY=y+(word.length-1)*dy
                if(endX !in 0 until n || endY !in 0 until n)return@repeat
                val p=(word.indices).map{idx->(y+idx*dy)*n+x+idx*dx}
                if(p.indices.all{idx->grid[p[idx]]==' '||grid[p[idx]]==word[idx]})found=p
            }
            if(found!=null){
                found!!.forEachIndexed{idx,p->grid[p]=word[idx]}
                paths[word]=found!!
            }
        }
        val letters=grid.map{if(it==' ')('A'+random.nextInt(26)) else it}
        val result=PalavraGrade(n,letters,paths,tema)
        if(best==null || result.respostas.size>best!!.respostas.size) best=result
        if(result.respostas.size==target)return result
    }
    return requireNotNull(best)
}
internal fun descobrirPalavra(selected:List<Int>,respostas:Map<String,List<Int>>):String? =
    respostas.entries.firstOrNull{(_,path)->selected==path||selected==path.reversed()}?.key

internal data class Pergunta(
    val enunciado:String,val opcoes:List<String>,val correta:Int,val explicacao:String,val assunto:String
)
internal fun resposta(numero:Int, enunciado:String, explicacao:String, assunto:String,
    random:Random):Pergunta {
    val options=linkedSetOf(numero)
    val desvios=listOf(1,2,3,4,5,6,7,9,10,12,15,20,25)
    while(options.size<4){
        val dif=desvios.random(random)
        val candidate=numero+if(random.nextBoolean())dif else -dif
        if(candidate>=0)options.add(candidate)
    }
    val choices=options.map{it.toString()}.shuffled(random)
    return Pergunta(enunciado,choices,choices.indexOf(numero.toString()),explicacao,assunto)
}

/** Tabuada selecionável 1–9 e operações +, −, ×, ÷; até 100 exercícios diferentes. */
internal fun gerarMatematica(
    nivel:String,total:Int,tabuada:Int?,operador:String,seed:Int
):List<Pergunta> {
    require(total in 10..100 && total%10==0)
    require(tabuada==null || tabuada in 1..9)
    val rand=Random(seed)
    val operations=if(operador=="Todos")listOf("+","−","×","÷") else listOf(operador)
    require(operations.isNotEmpty()&&operations.all{it in listOf("+","−","×","÷")})
    val result=mutableListOf<Pergunta>()
    val seen=mutableSetOf<String>()
    var trials=0
    while(result.size<total && trials++<10000){
        val operation=operations.random(rand)
        val bound=when(nivel){"Inicial"->30;"Intermediário"->90;else->250}
        val a=tabuada?:rand.nextInt(1,bound+1)
        val b=rand.nextInt(1,if(tabuada!=null)101 else if(operation=="×"||operation=="÷") 16 else bound+1)
        val key="$operation:$a:$b"
        if(!seen.add(key))continue
        val question=when(operation){
            "+"->resposta(a+b,"Quanto é $a + $b?",
                "Somando $a e $b, obtemos ${a+b}.","Adição",rand)
            "−"->{
                val hi=maxOf(a,b);val lo=minOf(a,b)
                resposta(hi-lo,"Quanto é $hi − $lo?",
                    "A diferença entre $hi e $lo é ${hi-lo}.","Subtração",rand)
            }
            "×"->resposta(a*b,"Quanto é $a × $b?",
                "$a grupos de $b resultam em ${a*b}.","Multiplicação",rand)
            else->resposta(a,"Quanto é ${a*b} ÷ $b?",
                "$b multiplicado por $a dá ${a*b}.","Divisão exata",rand)
        }
        result.add(question)
    }
    require(result.size==total){"Não foi possível gerar todas as questões"}
    return result
}

private fun textoQ(statement:String,correct:String,other1:String,other2:String,
    why:String,theme:String,rand:Random):Pergunta {
    val alternatives=listOf(correct,other1,other2).distinct()
    require(alternatives.size==3)
    val mixed=alternatives.shuffled(rand)
    return Pergunta(statement,mixed,mixed.indexOf(correct),why,theme)
}
private val sinonimos=listOf(
    "alegre" to "feliz","rápido" to "veloz","bonito" to "belo",
    "iniciar" to "começar","encerrar" to "terminar","tranquilo" to "calmo",
    "ajudar" to "auxiliar","casa" to "lar","fácil" to "simples",
    "difícil" to "complicado","grande" to "enorme","pequeno" to "miúdo",
    "esperto" to "inteligente","caminhar" to "andar","valente" to "corajoso",
    "contente" to "satisfeito","escutar" to "ouvir","enxergar" to "ver",
    "trabalho" to "ocupação","relevante" to "importante","essencial" to "fundamental",
    "gentil" to "amável","concluir" to "finalizar","reparar" to "observar"
)
private val antonimos=listOf(
    "claro" to "escuro","alegre" to "triste","quente" to "frio",
    "alto" to "baixo","longo" to "curto","rápido" to "lento",
    "cheio" to "vazio","fácil" to "difícil","cedo" to "tarde",
    "longe" to "perto","novo" to "velho","aberto" to "fechado",
    "forte" to "fraco","limpo" to "sujo","subir" to "descer",
    "ganhar" to "perder","leve" to "pesado","seco" to "molhado",
    "silêncio" to "barulho","verdade" to "mentira","completo" to "incompleto",
    "corajoso" to "medroso","ordenado" to "bagunçado","começo" to "fim"
)
private val plural=listOf(
    "animal" to "animais","papel" to "papéis","farol" to "faróis",
    "pão" to "pães","flor" to "flores","lápis" to "lápis",
    "cidadão" to "cidadãos","avião" to "aviões","mão" to "mãos",
    "coração" to "corações","irmão" to "irmãos","alemão" to "alemães",
    "funil" to "funis","azul" to "azuis","hotel" to "hotéis",
    "jardim" to "jardins","homem" to "homens","mulher" to "mulheres",
    "ônibus" to "ônibus","leão" to "leões","limão" to "limões",
    "cão" to "cães","sol" to "sóis","anzol" to "anzóis"
)

internal fun gerarPortugues(nivel:String,total:Int,seed:Int):List<Pergunta> {
    val random=Random(seed)
    val bank=mutableListOf<Pergunta>()
    if(nivel!="Avançado"){
        sinonimos.forEach{(a,b)->
            bank+=textoQ("Qual palavra tem sentido parecido com “$a”?",b,"janela","quadrado",
                "“$b” pode ter sentido semelhante a “$a” em determinados contextos.",
                "Vocabulário",random)
        }
        antonimos.forEach{(a,b)->
            bank+=textoQ("Qual é o contrário de “$a”?",b,a,"janela",
                "“$b” expressa ideia oposta a “$a”.","Antônimos",random)
        }
    }
    plural.forEach{(a,b)->
        val erros=listOf(a,a+"s",a+"es",a+"is",a+"ões").distinct().filter{it!=b}
        bank+=textoQ("Qual é o plural de “$a”?",b,erros[0],erros[1],
            "A forma plural de “$a” é “$b”.","Gramática",random)
    }
    val acentos=listOf("você","também","café","lápis","ônibus","fácil","médico",
        "árvore","pássaro","língua","história","último","página","rápido","música",
        "água","saúde","país","baú","público","júri","bênção","tênis","família")
    acentos.forEach{w->
        val without=java.text.Normalizer.normalize(w,java.text.Normalizer.Form.NFD)
            .replace(Regex("[\\u0300-\\u036f]"),"")
        if(without!=w)bank+=textoQ("Qual opção apresenta a acentuação correta?",w,without,
            without.uppercase(),"A grafia correta é “$w”.","Ortografia",random)
    }
    val classes=listOf(
        Triple("O cachorro corre.","cachorro","Substantivo"),
        Triple("A menina estudou.","estudou","Verbo"),
        Triple("O dia está lindo.","lindo","Adjetivo"),
        Triple("Ela chegou cedo.","cedo","Advérbio"),
        Triple("Nós vamos viajar.","nós","Pronome"),
        Triple("O livro é azul.","azul","Adjetivo"),
        Triple("Eles cantam bem.","cantam","Verbo"),
        Triple("Um carro passou.","carro","Substantivo"),
        Triple("A chuva caiu forte.","forte","Advérbio"),
        Triple("Meu amigo sorriu.","amigo","Substantivo"),
        Triple("Ela falou baixinho.","baixinho","Advérbio"),
        Triple("As crianças brincam.","brincam","Verbo")
    )
    classes.forEach{(sentence,word,part)->
        val errados=listOf("Substantivo","Verbo","Adjetivo","Advérbio","Pronome")
            .filter{it!=part}.shuffled(random)
        bank+=textoQ("Na frase “$sentence”, a palavra “$word” é:",part,
            errados[0],errados[1],
            "Em “$sentence”, “$word” exerce a função de $part.","Classes de palavras",random)
    }
    val contextos=listOf(
        Triple("Ana levou guarda-chuva porque o céu estava escuro.",
            "Por que Ana levou guarda-chuva?","Porque o céu estava escuro."),
        Triple("O ônibus saiu às sete horas; Pedro chegou às sete e cinco.",
            "Pedro chegou antes ou depois do ônibus?","Depois."),
        Triple("O jardim tinha flores azuis e amarelas.",
            "Quais cores aparecem no jardim?","Azul e amarelo."),
        Triple("Maria comprou pão e leite na padaria.",
            "Onde Maria fez suas compras?","Na padaria."),
        Triple("Lia alimentou o gato antes de ir para a escola.",
            "O que Lia fez primeiro?","Alimentou o gato."),
        Triple("João guardou seus livros para não perdê-los.",
            "Por que João guardou os livros?","Para não perdê-los."),
        Triple("A turma leu um conto e depois desenhou os personagens.",
            "O que a turma fez depois da leitura?","Desenhou os personagens."),
        Triple("O museu abre às nove e fecha às dezessete.",
            "A que horas o museu fecha?","Às dezessete."),
        Triple("No sábado, Rafa visitou a avó e preparou um bolo.",
            "Em qual dia Rafa visitou a avó?","No sábado."),
        Triple("A biblioteca estava silenciosa e cheia de livros.",
            "Como estava a biblioteca?","Silenciosa."),
        Triple("A aula começou após o recreio.",
            "Quando a aula começou?","Depois do recreio."),
        Triple("O cachorro descansou porque estava cansado.",
            "Por que ele descansou?","Porque estava cansado.")
    )
    contextos.forEach{(story,ask,correct)->
        bank+=textoQ(story+"\n\n"+ask,correct,"Não há informação no texto.",
            "O texto diz o contrário.", "A resposta está explicitamente no pequeno texto.",
            "Interpretação de texto",random)
    }
    val phraseBank=listOf(
        Triple("Fui ___ escola.","à","á"),
        Triple("Ela gosta ___ estudar.","de","da"),
        Triple("Nós ___ amanhã.","viajaremos","viajaram"),
        Triple("Eles ___ cedo todos os dias.","chegam","chega"),
        Triple("Você ___ preparado?","está","estar"),
        Triple("Ontem nós ___ juntos.","saímos","sairão"),
        Triple("Se eu ___ tempo, estudarei.","tiver","ter"),
        Triple("As crianças ___ no parque.","brincaram","brincou"),
        Triple("A professora ___ os exercícios.","corrigiu","corrigi"),
        Triple("Os livros ___ na mesa.","estão","está"),
        Triple("Nós precisamos ___ ajuda.","de","em"),
        Triple("Ela escreveu ___ carta.","uma","um")
    )
    phraseBank.forEach{(phrase,correct,wrong)->
        bank+=textoQ("Complete a frase corretamente: “$phrase”",correct,wrong,"nunca",
            "A expressão correta é “"+phrase.replace("___",correct)+"”.",
            "Construção de frases",random)
    }
    // Variações de leitura ampliam a oferta para sessões de 10 a 100 itens.
    if(bank.size<total) {
        val extra=bank.toList()
        var rodada=1
        while(bank.size<total) {
            extra.shuffled(random).forEach{q->
                if(bank.size<total)bank+=q.copy(
                    enunciado="Revisão "+rodada+": "+q.enunciado
                )
            }
            rodada++
        }
    }
    return bank.shuffled(random).take(total)
}

internal fun gerarLogica(nivel:String,total:Int,seed:Int):List<Pergunta> {
    val random=Random(seed)
    val questions=mutableListOf<Pergunta>()
    val seen=mutableSetOf<String>()
    var n=0
    while(questions.size<total&&n++<5000){
        val kind=random.nextInt(5)
        val start=random.nextInt(1,if(nivel=="Inicial")20 else 60)
        val step=random.nextInt(2,if(nivel=="Inicial")7 else if(nivel=="Intermediário")13 else 22)
        val question=when(kind){
            0->{
                val vals=(0..3).map{start+it*step}
                resposta(start+4*step,"Continue a sequência: "+vals.joinToString(", ") + ", ...",
                    "Cada termo aumenta $step unidades.","Sequências",random)
            }
            1->{
                val vals=(0..3).map{start+it*it*step}
                resposta(start+16*step,"Qual número vem depois? "+vals.joinToString(", ")+", ...",
                    "Os acréscimos seguem quadrados: 0, 1, 4, 9, 16 vezes $step.",
                    "Padrões",random)
            }
            2->{
                val a=random.nextInt(2,7)
                val vals=(0..3).map{a*(1 shl it)}
                resposta(a*16,"Descubra o próximo: "+vals.joinToString(" → ")+" → ?",
                    "Os valores dobram a cada passo.","Duplicação",random)
            }
            3->{
                val group=step+2
                val marked=group/2
                resposta(group-marked,"Há $group objetos. $marked estão marcados. Quantos restam sem marca?",
                    "Basta tirar $marked de $group.","Contagem",random)
            }
            else->{
                val day=(start%7)
                val days=listOf("segunda","terça","quarta","quinta","sexta","sábado","domingo")
                val next=(day+step)%7
                val correct=days[next]
                val options=days.shuffled(random).take(2).filter{it!=correct}+
                    listOf(correct)
                val mixed=options.distinct().let {
                    (it + days.filterNot{d->d in it}).take(3).shuffled(random)
                }
                Pergunta("Se hoje é "+days[day]+", que dia será daqui a $step dias?",
                    mixed,mixed.indexOf(correct),
                    "A semana tem sete dias; contamos $step dias a partir de "+days[day]+".",
                    "Orientação temporal")
            }
        }
        if(seen.add(question.enunciado)&&question.correta>=0)questions+=question
    }
    return questions.take(total)
}
