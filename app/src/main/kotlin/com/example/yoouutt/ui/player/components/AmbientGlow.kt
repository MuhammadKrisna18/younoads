package com.example.yoouutt.ui.player.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.yoouutt.ui.theme.CyberViolet
import com.example.yoouutt.ui.theme.ElectricCyan
import com.example.yoouutt.ui.theme.NeonMagenta

@Composable
fun AmbientGlow(
    modifier: Modifier = Modifier,
    glowHeight: Int = 260
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    val infiniteTransition = rememberInfiniteTransition(label = "ambientPulse")
    val pulseFactor by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseFactor"
    )

    val cyanGlow = ElectricCyan
    val magentaGlow = NeonMagenta
    val violetGlow = CyberViolet

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(glowHeight.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(glowHeight.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.24f * pulseFactor),
                            cyanGlow.copy(alpha = 0.32f * pulseFactor),
                            magentaGlow.copy(alpha = 0.34f * pulseFactor),
                            violetGlow.copy(alpha = 0.16f * pulseFactor),
                            Color.Transparent
                        ),
                        center = Offset(screenWidthPx / 2f, 0f),
                        radius = screenWidthPx * 0.92f
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((glowHeight * 0.70f).dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            magentaGlow.copy(alpha = 0.24f * pulseFactor),
                            violetGlow.copy(alpha = 0.12f * pulseFactor),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((glowHeight * 0.42f).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.20f),
                            Color.Transparent
                        ),
                        center = Offset(screenWidthPx / 2f, 0f),
                        radius = screenWidthPx * 0.55f
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(glowHeight.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF08080C).copy(alpha = 0.55f),
                            Color(0xFF08080C)
                        )
                    )
                )
        )
    }
}
