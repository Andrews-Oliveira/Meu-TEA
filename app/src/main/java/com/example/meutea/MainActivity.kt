package com.example.meutea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meutea.LoginCadastrar.CadastrarScreen
import com.example.meutea.LoginCadastrar.LoginScreen
import com.example.meutea.menu.careiradigital.CarteirinhaEditScreen
import com.example.meutea.menu.careiradigital.CarteirinhaScreen
import com.example.meutea.menu.careiradigital.CarteirinhaViewScreen
import com.example.meutea.menu.MenuPrincipalScreen
import com.example.meutea.menu.conversa.emcasa.ConversaEmCasaScreen
import com.example.meutea.menu.conversa.ConversaScreen
import com.example.meutea.menu.conversa.comidas.ComidasScreen
import com.example.meutea.menu.conversa.dor.DorScreen
import com.example.meutea.menu.conversa.emocoes.EmocoesScreen
import com.example.meutea.menu.conversa.necessidades.NecessidadesScreen
import com.example.meutea.menu.rotinas.RotinasScreen
import com.example.meutea.menu.comunicacao.ComunicacaoV2Screen
import com.example.meutea.menu.jogos.JogosV2Screen
import com.example.meutea.menu.ciptea.CipteaOrientacaoScreen
import com.example.meutea.ui.theme.MeuTEATheme
import com.example.meutea.welcome.WelcomeScreen
import com.example.meutea.welcome.WelcomeScreen2
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeuTEATheme {
                val navController = rememberNavController()
                SetupNavGraph(navController)
            }
        }
    }
}

// Função para configurar a navegação
@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "sessionGate"
    ) {
        composable(route = "sessionGate") {
            SessaoInicial(navController)
        }
        composable(route = "welcomeScreen") {
            WelcomeScreen(navController = navController)
        }

        composable(route = "welcomeScreen2") {
            WelcomeScreen2(navController = navController)
        }

        composable(route = "loginScreen") {
            LoginScreen(
                navController = navController,
                onBackClicked = { navController.popBackStack() },
                onContinuarClicked = { _, _, _ -> },
                onCadastrarClicked = { navController.navigate("cadastrarScreen") }
            )
        }

        composable(route = "cadastrarScreen") {
            CadastrarScreen(
                navController = navController,
                onBackClicked = { navController.popBackStack() },
                onCadastrarClicked = { _, _, _ ->
                    FirebaseAuth.getInstance().currentUser?.uid?.let { usuarioId ->
                        navController.navigate("menuPrincipalScreen/$usuarioId") {
                            popUpTo("cadastrarScreen") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // 🔹 Menu Principal agora recebe `usuarioId`
        composable("menuPrincipalScreen/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            MenuPrincipalScreen(navController = navController, usuarioId = usuarioId)
        }

        // 🔹 Exibir Carteirinha com `usuarioId`
        composable("carteirinhaViewScreen/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            CarteirinhaViewScreen(navController = navController, userId = usuarioId)
        }

        // 🔹 Criar Carteirinha com `usuarioId`
        composable(route = "carteirinhaScreen/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            CarteirinhaScreen(navController = navController, usuarioId = usuarioId)
        }
        composable("editarCarteirinha/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            CarteirinhaEditScreen(navController = navController, userId = usuarioId)
        }
        // Navegação v2 sem romper as rotas antigas, mantidas durante a transição.
        composable("comunicacaoV2") {
            ComunicacaoV2Screen(navController)
        }
        composable("jogosV2") {
            JogosV2Screen(navController)
        }
        composable("cipteaOrientacao") {
            CipteaOrientacaoScreen(navController)
        }
        composable(route = "conversaScreen") { // ✅ Nova rota para a tela de conversas
            ConversaScreen(navController = navController)
        }

// ✅ Rota para "Conversas em Casa"
        composable("conversaEmCasaScreen") {
            val context = LocalContext.current
            ConversaEmCasaScreen(context, navController) // ✅ Correto
        }

        composable("EmocoesScreen") {
            EmocoesScreen(navController) // ✅ Correto, agora está no mesmo nível
        }
        composable("DorScreen") {
            DorScreen(navController) // ✅ Correto, agora está no mesmo nível
        }
        composable("NecessidadesScreen") {
            NecessidadesScreen(navController) // ✅ Correto, agora está no mesmo nível
        }
        composable("ComidasScreen") {
            ComidasScreen(navController) // ✅ Correto, agora está no mesmo nível
        }
        composable("RotinasScreen") {
            RotinasScreen(navController) // ✅ Correto, agora está no mesmo nível
        }
    }
}

/**
 * FirebaseAuth no Android mantém a sessão entre inicializações.
 * Ao abrir o app, aguarda o estado autenticado e encaminha à home sem pedir senha novamente.
 * O listener é liberado quando sai deste destino; logout continua explícito.
 */
@Composable
private fun SessaoInicial(navController: NavHostController) {
    val auth = remember { FirebaseAuth.getInstance() }
    var resolvido by remember { mutableStateOf(false) }
    var usuarioId by remember { mutableStateOf<String?>(null) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebase ->
            usuarioId = firebase.currentUser?.uid
            resolvido = true
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    LaunchedEffect(resolvido, usuarioId) {
        if (resolvido) {
            val destino = usuarioId?.let { "menuPrincipalScreen/$it" } ?: "loginScreen"
            navController.navigate(destino) {
                popUpTo("sessionGate") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    MeuTEATheme {
        val navController = rememberNavController()
        SetupNavGraph(navController)
    }
}
