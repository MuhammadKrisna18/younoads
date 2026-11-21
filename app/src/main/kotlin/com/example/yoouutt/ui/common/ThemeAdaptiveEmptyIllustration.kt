package com.example.yoouutt.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.yoouutt.ui.theme.LocalYoouuttGradient
import com.example.yoouutt.ui.theme.LocalYoouuttTheme
import com.example.yoouutt.ui.theme.ThemeCategory

@Composable
fun ThemeAdaptiveEmptyIllustration(
    fallbackIcon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    animeSize: Dp = 170.dp,
    iconSize: Dp = 100.dp
) {
    val theme = LocalYoouuttTheme.current
    val themeGradient = LocalYoouuttGradient.current
    val backdropResId = theme.backdropResId

    val isAnimeTheme = theme.category == ThemeCategory.ANIME || (theme.category == ThemeCategory.TIME_ADAPTIVE && theme.hasHologramBackdrop)
    if (isAnimeTheme && backdropResId != null) {
        Box(
            modifier = modifier
                .size(animeSize)
                .clip(RoundedCornerShape(24.dp))
                .border(BorderStroke(1.5.dp, themeGradient), RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(id = backdropResId),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(iconSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(BorderStroke(1.5.dp, themeGradient), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(iconSize * 0.44f)
            )
        }
    }
}
