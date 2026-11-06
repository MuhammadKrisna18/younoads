package com.example.yoouutt.ui.player.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoouutt.ui.animation.bounceClick

@Composable
fun ActionPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (isActive) Color(0xFFFF0000) else Color(0xFF272727),
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "pillBgColor"
    )

    Row(
        modifier = modifier
            .bounceClick(onClick = onClick)
            .height(34.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(animatedBgColor)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
            color = Color.White
        )
    }
}
