package com.example.meutea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meutea.LoginCadastrar.CadastrarScreen
import com.example.meutea.LoginCadastrar.LoginScreen
import com.example.meutea.menu.CarteirinhaScreen
import com.example.meutea.menu.CarteirinhaViewScreen
import com.example.meutea.menu.MenuPrincipalScreen
import com.example.meutea.ui.theme.MeuTEATheme
import com.example.meutea.welcome.WelcomeScreen
import com.example.meutea.welcome.WelcomeScreen2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Garante que a interface preencha a tela corretamente
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
        startDestination = "welcomeScreen" // Define a tela inicial
    ) {
        // Tela de Boas-Vindas 1
        composable(route = "welcomeScreen") {
            WelcomeScreen(navController = navController)
        }

        // Tela de Boas-Vindas 2
        composable(route = "welcomeScreen2") {
            WelcomeScreen2(navController = navController)
        }

        // Tela de Login
        composable(route = "loginScreen") {
            LoginScreen(
                navController = navController,
                onBackClicked = { navController.popBackStack() }, // Volta para a tela anterior
                onContinuarClicked = { email, senha, lembrarMe ->
                    println("Login com: $email, $senha, Lembrar-me: $lembrarMe")
                },
                onCadastrarClicked = { navController.navigate("cadastrarScreen") } // Navega para Cadastro
            )
        }

        // Tela de Cadastro
        composable(route = "cadastrarScreen") {
            CadastrarScreen(
                navController = navController,
                onBackClicked = { navController.popBackStack() }, // Volta para Login
                onCadastrarClicked = { nome, email, senha ->
                    println("Usuário cadastrado: $nome, $email")
                    navController.navigate("menuPrincipalScreen") // Após cadastro, ir para o Menu Principal
                }
            )
        }

        // Tela Menu Principal
        composable(route = "menuPrincipalScreen") {
            MenuPrincipalScreen(navController = navController)
        }

        // Tela da Carteirinha (Apenas Exibição)
        composable(route = "carteirinhaViewScreen") {
            CarteirinhaViewScreen(navController = navController)
        }

        // Tela para Criar a Carteirinha
        composable(route = "carteirinhaScreen") {
            CarteirinhaScreen(navController = navController)
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
