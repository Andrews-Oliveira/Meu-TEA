package com.example.meutea

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.core.content.ContextCompat
import com.example.meutea.LoginCadastrar.LoginScreen
import com.example.meutea.LoginCadastrar.CadastrarScreen
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Teste de regressão para o crash do painterResource em XML <shape>.
 * Rodar no emulador: ./gradlew :app:connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class FundosDeTemaInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fundosDosTresTemasSaoDrawableValidos() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        listOf(
            R.drawable.tea_background_login,
            R.drawable.tea_background_masculino,
            R.drawable.tea_background_feminino
        ).forEach { id ->
            assertNotNull(ContextCompat.getDrawable(context, id))
        }
    }

    @Test
    fun cadastroAbreSemFalharComBackgroundXml() {
        composeTestRule.setContent {
            val controller = rememberNavController()
            CadastrarScreen(
                navController = controller,
                onBackClicked = {},
                onCadastrarClicked = { _, _, _ -> }
            )
        }
        composeTestRule.onNodeWithText("Boas vindas!").assertExists()
    }

    @Test
    fun loginExibeFormularioSemFalharComBackgroundXml() {
        composeTestRule.setContent {
            val controller = rememberNavController()
            LoginScreen(
                navController = controller,
                onBackClicked = {},
                onContinuarClicked = { _, _, _ -> },
                onCadastrarClicked = {}
            )
        }
        composeTestRule.onNodeWithText("Meu TEA").assertIsDisplayed()
        composeTestRule.onNodeWithText("Seu e-mail").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sua senha").assertIsDisplayed()
    }
}
