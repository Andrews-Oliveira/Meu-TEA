package com.example.meutea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meutea.LoginCadastrar.CadastrarScreen
import com.example.meutea.LoginCadastrar.LoginScreen
import com.example.meutea.home.WelcomeScreen
import com.example.meutea.home.WelcomeScreen2
import com.example.meutea.ui.theme.MeuTEATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Garante que a interface preencha a tela corretamente
        setContent {
            MeuTEATheme {
                val navController = rememberNavController()
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
                            onBackClicked = {
                                navController.popBackStack() // Volta para a tela anterior
                            },
                            onContinuarClicked = { email, senha, lembrarMe ->
                                println("Login com: $email, $senha, Lembrar-me: $lembrarMe")
                            },
                            onCadastrarClicked = {
                                navController.navigate("cadastrarScreen") // Navega para a tela de cadastro
                            }
                        )
                    }

                    // Tela de Cadastro
                    composable(route = "cadastrarScreen") {
                        CadastrarScreen(
                            navController = navController,
                            onBackClicked = {
                                navController.popBackStack() // Voltar para a tela anterior
                            },
                            onCadastrarClicked = { nome, email, senha ->
                                println("Usuário cadastrado: $nome, $email")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    MeuTEATheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "welcomeScreen"
        ) {
            composable("welcomeScreen") {
                WelcomeScreen(navController = navController)
            }
        }
    }
}
