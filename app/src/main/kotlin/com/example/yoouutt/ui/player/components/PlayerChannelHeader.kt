package com.example.yoouutt.ui.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun PlayerChannelHeader(
    uploaderName: String,
    streamData: StreamData?,
    onChannelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarUrl = streamData?.uploaderAvatarUrl.orEmpty()
    val subText = streamData?.uploaderSubscriberCountText.orEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable(onClick = onChannelClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF272727)),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUrl.isNotBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = uploaderName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(38.dp)
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

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = uploaderName,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subText.isNotBlank()) {
                Text(
                    text = subText,
                    fontSize = 12.sp,
                    color = Color(0xFFAAAAAA),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
