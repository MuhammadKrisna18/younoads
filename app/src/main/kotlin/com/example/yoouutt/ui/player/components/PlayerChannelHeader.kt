package com.example.yoouutt.ui.player.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.ui.animation.bounceClick

@Composable
fun PlayerChannelHeader(
    uploaderName: String,
    streamData: StreamData?,
    isSubscribed: Boolean,
    onToggleSubscribe: () -> Unit,
    onChannelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarUrl = streamData?.uploaderAvatarUrl.orEmpty()
    val subText = streamData?.uploaderSubscriberCountText.orEmpty()

    val animatedSubColor by animateColorAsState(
        targetValue = if (isSubscribed) Color(0xFF272727) else Color(0xFFFF0000),
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "subBgColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF272727))
                .clickable(onClick = onChannelClick),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUrl.isNotBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = uploaderName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                val initial = uploaderName.trim().firstOrNull()?.uppercase() ?: "Y"
                Text(
                    text = initial,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onChannelClick)
        ) {
            Text(
                text = uploaderName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subText.isNotBlank()) {
                Text(
                    text = subText,
                    fontSize = 11.5.sp,
                    color = Color(0xFFAAAAAA),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .bounceClick(onClick = onToggleSubscribe)
                .height(34.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(animatedSubColor)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isSubscribed) "Disubscribe" else "Subscribe",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
