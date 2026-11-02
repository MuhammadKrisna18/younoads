package com.example.yoouutt.ui.player.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoouutt.data.model.StreamData

@Composable
fun PlayerDescriptionCard(
    streamData: StreamData?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (streamData == null) return

    val hasMeta = streamData.viewCountText.isNotBlank() || streamData.uploadDateText.isNotBlank()
    val hasDesc = streamData.description.isNotBlank()

    if (!hasMeta && !hasDesc) return

    val view = LocalView.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E1E1E))
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onToggleExpand()
            }
            .padding(12.dp)
            .animateContentSize()
    ) {
        Column {
            if (hasMeta) {
                val metaLine = buildString {
                    if (streamData.viewCountText.isNotBlank()) append(streamData.viewCountText)
                    if (streamData.viewCountText.isNotBlank() && streamData.uploadDateText.isNotBlank()) append("   ")
                    if (streamData.uploadDateText.isNotBlank()) append(streamData.uploadDateText)
                }
                Text(
                    text = metaLine,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                if (hasDesc) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            if (hasDesc) {
                Text(
                    text = streamData.description,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = Color(0xFFDDDDDD),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isExpanded) "Sembunyikan" else "...selengkapnya",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

