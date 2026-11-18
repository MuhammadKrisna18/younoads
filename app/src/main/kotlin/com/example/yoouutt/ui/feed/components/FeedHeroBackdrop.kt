package com.example.yoouutt.ui.feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.yoouutt.ui.theme.LocalYoouuttTheme

@Composable
fun FeedHeroBackdrop(
    modifier: Modifier = Modifier
) {
    val theme = LocalYoouuttTheme.current
    val backdropRes = theme.backdropResId
    if (!theme.hasHologramBackdrop || backdropRes == null) {
        return
    }

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Image(
            painter = painterResource(id = backdropRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            alpha = theme.hologramAlpha,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = if (theme.isLight) 0.65f else 0.45f),
                            backgroundColor.copy(alpha = if (theme.isLight) 0.30f else 0.15f),
                            backgroundColor.copy(alpha = if (theme.isLight) 0.85f else 0.75f),
                            backgroundColor
                        )
                    )
                )
        )
    }
}
