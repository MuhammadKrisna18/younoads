package com.example.yoouutt.ui.player.components

import android.content.Context
import android.content.Intent
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

@Composable
fun PlayerActionPillRow(
    videoUrl: String,
    isLiked: Boolean,
    isSaved: Boolean,
    speedText: String,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    onCycleSpeed: () -> Unit,
    onPipClick: () -> Unit,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionPill(
            icon = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
            label = if (isLiked) "Disukai" else "Suka",
            isActive = isLiked,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onToggleLike()
            }
        )

        ActionPill(
            icon = Icons.Outlined.Share,
            label = "Bagikan",
            onClick = {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, videoUrl)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }
        )

        ActionPill(
            icon = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            label = if (isSaved) "Tersimpan" else "Simpan",
            isActive = isSaved,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onToggleSave()
            }
        )

        ActionPill(
            icon = Icons.Default.Speed,
            label = speedText,
            onClick = onCycleSpeed
        )

        ActionPill(
            icon = Icons.Default.PictureInPictureAlt,
            label = "PiP",
            onClick = onPipClick
        )

        ActionPill(
            icon = Icons.Default.Fullscreen,
            label = "Layar Penuh",
            onClick = onToggleFullscreen
        )
    }
}

