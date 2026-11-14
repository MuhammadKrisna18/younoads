package com.example.yoouutt.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.yoouutt.R
import com.example.yoouutt.ui.theme.DarkBackground

@Composable
fun CyberpunkAppBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.35f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Image(
            painter = painterResource(id = R.drawable.youno_backdrop),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            alpha = alpha,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkBackground.copy(alpha = 0.72f),
                            DarkBackground.copy(alpha = 0.08f),
                            DarkBackground.copy(alpha = 0.12f),
                            DarkBackground.copy(alpha = 0.78f)
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
                            DarkBackground.copy(alpha = 0.25f)
                        ),
                        radius = 1600f
                    )
                )
        )

        content()
    }
}
