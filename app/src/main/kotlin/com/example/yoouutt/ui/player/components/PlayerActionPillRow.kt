package com.example.yoouutt.ui.player.components

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
import androidx.compose.material.icons.filled.Settings
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
    onShareClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onToggleSave: () -> Unit,
    onSettingsClick: () -> Unit,
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
            icon = Icons.Default.Settings,
            label = "Pengaturan",
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onSettingsClick()
            }
        )

        ActionPill(
            icon = Icons.Default.Fullscreen,
            label = "Layar Penuh",
            onClick = onToggleFullscreen
        )
    }
}
