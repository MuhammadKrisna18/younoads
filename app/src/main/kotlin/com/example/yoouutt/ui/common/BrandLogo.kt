package com.example.yoouutt.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yoouutt.ui.theme.YouTubeRed

val BrandRed = YouTubeRed

@Composable
fun BrandLogo(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(width = 28.dp, height = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(width = 28.dp, height = 20.dp)) {
                val cornerRadius = CornerRadius(5.5.dp.toPx(), 5.5.dp.toPx())
                drawRoundRect(
                    color = BrandRed,
                    cornerRadius = cornerRadius
                )
                val trianglePath = Path().apply {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val halfW = 4.5.dp.toPx()
                    val halfH = 5.dp.toPx()
                    moveTo(centerX - halfW * 0.7f, centerY - halfH)
                    lineTo(centerX + halfW * 1.3f, centerY)
                    lineTo(centerX - halfW * 0.7f, centerY + halfH)
                    close()
                }
                drawPath(trianglePath, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "You",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "noAds",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
