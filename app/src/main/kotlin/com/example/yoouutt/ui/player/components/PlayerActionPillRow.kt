package com.example.yoouutt.ui.player.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.yoouutt.R

@Composable
fun PlayerActionPillRow(
    isSaved: Boolean,
    isDownloaded: Boolean = false,
    downloadProgress: Int? = null,
    onShareClick: () -> Unit,
    onToggleSave: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onDownloadClick: () -> Unit = {},
    onSettingsClick: () -> Unit,
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
            label = stringResource(R.string.player_action_share),
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onShareClick()
            }
        )

        ActionPill(
            icon = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            label = if (isSaved) stringResource(R.string.player_action_saved) else stringResource(R.string.player_action_save),
            isActive = isSaved,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onToggleSave()
            }
        )

        ActionPill(
            icon = when {
                isDownloaded -> Icons.Default.DownloadDone
                downloadProgress != null -> Icons.Default.Downloading
                else -> Icons.Outlined.Download
            },
            label = when {
                isDownloaded -> stringResource(R.string.player_action_downloaded)
                downloadProgress != null -> "$downloadProgress%"
                else -> stringResource(R.string.player_action_download)
            },
            isActive = isDownloaded || downloadProgress != null,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onDownloadClick()
            }
        )

        ActionPill(
            icon = Icons.AutoMirrored.Filled.PlaylistAdd,
            label = stringResource(R.string.player_action_playlist),
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onAddToPlaylistClick()
            }
        )

        ActionPill(
            icon = Icons.Default.Settings,
            label = stringResource(R.string.player_action_settings),
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onSettingsClick()
            }
        )
    }
}
