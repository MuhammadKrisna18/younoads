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

            val topAlpha = if (theme.isLight) 0.85f else 0.72f
            val midUpperAlpha = if (theme.isLight) 0.18f else 0.08f
            val midLowerAlpha = if (theme.isLight) 0.24f else 0.12f
            val bottomAlpha = if (theme.isLight) 0.88f else 0.78f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                backgroundColor.copy(alpha = topAlpha),
                                backgroundColor.copy(alpha = midUpperAlpha),
                                backgroundColor.copy(alpha = midLowerAlpha),
                                backgroundColor.copy(alpha = bottomAlpha)
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
