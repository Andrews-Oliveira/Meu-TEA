package com.example.meutea.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Contraste estável: não mudar cores por fabricante do aparelho.
private val LightColors = lightColorScheme(
    primary = Color(0xFF146B70),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8F1EE),
    onPrimaryContainer = Color(0xFF104447),
    secondary = Color(0xFF66559A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE5FC),
    onSecondaryContainer = Color(0xFF403268),
    tertiary = Color(0xFF8A5940),
    onTertiary = Color.White,
    background = Color(0xFFF8FAF9),
    onBackground = Color(0xFF203136),
    surface = Color.White,
    onSurface = Color(0xFF203136),
    surfaceVariant = Color(0xFFEDF3F1),
    onSurfaceVariant = Color(0xFF3B5152),
    error = Color(0xFFAD3B35)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9DE2DC),
    onPrimary = Color(0xFF003B3D),
    primaryContainer = Color(0xFF18575A),
    onPrimaryContainer = Color(0xFFCBF4EF),
    secondary = Color(0xFFCEC1F5),
    onSecondary = Color(0xFF332553),
    secondaryContainer = Color(0xFF4C3C72),
    onSecondaryContainer = Color(0xFFECE6FF),
    tertiary = Color(0xFFF0C3A9),
    onTertiary = Color(0xFF4E2B19),
    background = Color(0xFF141D21),
    onBackground = Color(0xFFE3ECEA),
    surface = Color(0xFF1D292C),
    onSurface = Color(0xFFE3ECEA),
    surfaceVariant = Color(0xFF324245),
    onSurfaceVariant = Color(0xFFD0DCD9),
    error = Color(0xFFFFB4AC)
)

@Composable
fun MeuTEATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
