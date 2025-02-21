package com.example.meutea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meutea.LoginCadastrar.CadastrarScreen
import com.example.meutea.LoginCadastrar.LoginScreen
import com.example.meutea.menu.CarteirinhaEditScreen
import com.example.meutea.menu.CarteirinhaScreen
import com.example.meutea.menu.CarteirinhaViewScreen
import com.example.meutea.menu.MenuPrincipalScreen
import com.example.meutea.ui.theme.MeuTEATheme
import com.example.meutea.welcome.WelcomeScreen
import com.example.meutea.welcome.WelcomeScreen2

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
        startDestination = "welcomeScreen"
    ) {
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
                onContinuarClicked = { email, senha, lembrarMe ->
                    println("Login com: $email, $senha, Lembrar-me: $lembrarMe")
                },
                onCadastrarClicked = { navController.navigate("cadastrarScreen") }
            )
        }

        composable(route = "cadastrarScreen") {
            CadastrarScreen(
                navController = navController,
                onBackClicked = { navController.popBackStack() },
                onCadastrarClicked = { nome, email, senha ->
                    println("Usuário cadastrado: $nome, $email")
                    navController.navigate("menuPrincipalScreen/{usuarioId}") {
                        popUpTo("cadastrarScreen") { inclusive = true }
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
