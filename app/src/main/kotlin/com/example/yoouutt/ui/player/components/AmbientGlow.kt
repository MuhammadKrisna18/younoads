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
import com.example.yoouutt.ui.theme.YouTubeRed

@Composable
fun AmbientGlow(
    modifier: Modifier = Modifier,
    glowHeight: Int = 260
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    // Subtle breathing pulse for an alive stage-light atmosphere
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

    val pureRed = YouTubeRed
    val deepRed = Color(0xFFCC0000)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(glowHeight.dp)
    ) {
        // Layer 1: Core Radial Bloom with White center radiating into pure YouTube Red
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(glowHeight.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f * pulseFactor),
                            pureRed.copy(alpha = 0.38f * pulseFactor),
                            deepRed.copy(alpha = 0.18f * pulseFactor),
                            Color.Transparent
                        ),
                        center = Offset(screenWidthPx / 2f, 0f),
                        radius = screenWidthPx * 0.92f
                    )
                )
        )

        // Layer 2: Vertical Red Bleed from top edge under the video player
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((glowHeight * 0.70f).dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            pureRed.copy(alpha = 0.28f * pulseFactor),
                            deepRed.copy(alpha = 0.12f * pulseFactor),
                            Color.Transparent
                        )
                    )
                )
        )

        // Layer 3: Central Soft White spotlight accent directly below the video
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((glowHeight * 0.42f).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        center = Offset(screenWidthPx / 2f, 0f),
                        radius = screenWidthPx * 0.55f
                    )
                )
        )

        // Layer 4: Smooth fade into pure black at the bottom for crystal-clear readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(glowHeight.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black
                        )
                    )
                )
        )
    }
}
