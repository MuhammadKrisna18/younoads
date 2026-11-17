package com.example.yoouutt.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.yoouutt.ui.theme.LocalYoouuttTheme

@Composable
fun CyberpunkAppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val theme = LocalYoouuttTheme.current
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        val backdropRes = theme.backdropResId
        if (theme.hasHologramBackdrop && backdropRes != null) {
            Image(
                painter = painterResource(id = backdropRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                alpha = theme.hologramAlpha,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                backgroundColor.copy(alpha = 0.72f),
                                backgroundColor.copy(alpha = 0.08f),
                                backgroundColor.copy(alpha = 0.12f),
                                backgroundColor.copy(alpha = 0.78f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                backgroundColor.copy(alpha = 0.25f)
                            ),
                            radius = 1600f
                        )
                    )
            )
        }

        content()
    }
}
