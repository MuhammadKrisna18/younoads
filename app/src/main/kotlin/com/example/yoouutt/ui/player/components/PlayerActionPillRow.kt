package com.example.yoouutt.ui.player.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

@Composable
fun PlayerActionPillRow(
    videoUrl: String,
    isSaved: Boolean,
    isAutoplayEnabled: Boolean = true,
    isLoopEnabled: Boolean = false,
    speedText: String,
    resolutionText: String = "",
    audioTrackText: String = "",
    onShareClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onToggleSave: () -> Unit,
    onToggleAutoplay: () -> Unit = {},
    onToggleLoop: () -> Unit = {},
    onCycleSpeed: () -> Unit,
    onResolutionClick: () -> Unit = {},
    onAudioTrackClick: () -> Unit = {},
    onPipClick: () -> Unit,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            icon = Icons.Outlined.Share,
            label = "Bagikan",
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onShareClick()
            }
        )

        ActionPill(
            icon = Icons.Outlined.ContentCopy,
            label = "Salin",
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onCopyLinkClick()
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
            icon = Icons.Default.AutoMode,
            label = if (isAutoplayEnabled) "Autoplay: Aktif" else "Autoplay: Nonaktif",
            isActive = isAutoplayEnabled,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onToggleAutoplay()
            }
        )

        ActionPill(
            icon = Icons.Default.Repeat,
            label = if (isLoopEnabled) "Loop: Aktif" else "Loop: Nonaktif",
            isActive = isLoopEnabled,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onToggleLoop()
            }
        )

        if (resolutionText.isNotBlank()) {
            ActionPill(
                icon = Icons.Default.HighQuality,
                label = resolutionText,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onResolutionClick()
                }
            )
        }

        if (audioTrackText.isNotBlank()) {
            ActionPill(
                icon = Icons.Default.Audiotrack,
                label = audioTrackText,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onAudioTrackClick()
                }
            )
        }

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
