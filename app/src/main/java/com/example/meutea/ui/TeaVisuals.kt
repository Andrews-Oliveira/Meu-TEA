package com.example.meutea.ui

import android.widget.ImageView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.meutea.R

// Elementos reutilizáveis: os botões continuam nativos, os fundos aceitam XML e WebP.
// Não colocar nomes, botões nem textos dentro do arquivo da imagem de fundo.
object TeaPalette {
    val navy = Color(0xFF0C2855)
    val ocean = Color(0xFF0066B3)
    val sky = Color(0xFF6ED6F0)
    val teal = Color(0xFF00A6A7)
    val mint = Color(0xFFB5F6E9)
    val lilac = Color(0xFF8577EE)
    val sunflower = Color(0xFFFFCB56)
    val white = Color(0xFFFFFFFF)
    val ink = Color(0xFF132E58)
}

/** O mesmo fundo gráfico acompanha a pessoa entre início, comunicação e rotina. */
@Composable
fun TeaWallpaper(
    temaVisual: String,
    modifier: Modifier = Modifier,
    tint: Float = 0f
) {
    val picture = if (temaVisual == "feminino") {
        R.drawable.tea_background_feminino
    } else {
        R.drawable.tea_background_masculino
    }
    Box(modifier) {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    importantForAccessibility = android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO
                    setImageResource(picture)
                }
            },
            update = { imageView -> imageView.setImageResource(picture) },
            modifier = Modifier.fillMaxSize()
        )
        if (tint > 0f) {
            Box(
                Modifier.fillMaxSize().background(TeaPalette.navy.copy(alpha = tint))
            )
        }
    }
}

@Composable
fun TeaGlassPanel(
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    val surface = if (dark) {
        Brush.linearGradient(listOf(Color(0xEA005C9A), Color(0xED007B96)))
    } else {
        Brush.linearGradient(listOf(Color(0xF5F8FDFF), Color(0xE6CAEAFD)))
    }
    Column(
        modifier = modifier
            .shadow(10.dp, shape, ambientColor = Color(0x55003F78))
            .clip(shape)
            .background(surface)
            .border(1.5.dp, Color.White.copy(alpha = 0.90f), shape)
            .padding(18.dp),
        content = content
    )
}

@Composable
fun TeaPill(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (selected) Brush.horizontalGradient(
                    listOf(TeaPalette.ocean, TeaPalette.teal)
                ) else Brush.horizontalGradient(
                    listOf(Color(0xFFF2FAFF), Color(0xFFD9F1FE))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.9f), shape)
            .padding(horizontal = 15.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (selected) Color.White else TeaPalette.ink,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TeaSectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.ExtraBold
    )
}
