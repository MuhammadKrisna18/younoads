package com.example.yoouutt.ui.player.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Sd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.yoouutt.R
import com.example.yoouutt.data.model.StreamData

data class DownloadQualityOption(
    val title: String,
    val subtitle: String,
    val downloadUrl: String,
    val audioDownloadUrl: String? = null,
    val qualityLabel: String,
    val isAudioOnly: Boolean,
    val icon: ImageVector
)

@Composable
fun DownloadSelectionDialog(
    streamData: StreamData?,
    onSelectDownload: (qualityLabel: String, downloadUrl: String, audioDownloadUrl: String?, isAudioOnly: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    val primaryColor = MaterialTheme.colorScheme.primary

    val options = buildList {
        if (streamData != null) {
            val bestAudioUrl = streamData.availableAudioTracks
                .firstOrNull { it.format.equals("M4A", ignoreCase = true) }?.audioUrl
                ?: streamData.availableAudioTracks.firstOrNull()?.audioUrl

            val distinctResolutions = streamData.availableResolutions
                .filter { it.videoUrl.isNotBlank() }
                .distinctBy { it.height }
                .sortedByDescending { it.height }

            for (res in distinctResolutions) {
                val title = when (res.height) {
                    2160 -> "4K (2160p)"
                    1440 -> "2K (1440p)"
                    1080 -> "1080p (Full HD)"
                    720 -> "720p (HD)"
                    480 -> "480p (Standard)"
                    360 -> "360p (Data Saver)"
                    240 -> "240p (Low)"
                    144 -> "144p (Lowest)"
                    else -> "${res.height}p"
                }

                val audioUrlToUse = if (res.isVideoOnly) {
                    res.audioUrl ?: bestAudioUrl
                } else {
                    null
                }

                add(
                    DownloadQualityOption(
                        title = title,
                        subtitle = "Video MP4",
                        downloadUrl = res.videoUrl,
                        audioDownloadUrl = audioUrlToUse,
                        qualityLabel = "${res.height}p",
                        isAudioOnly = false,
                        icon = if (res.height >= 720) Icons.Default.Hd else Icons.Default.Sd
                    )
                )
            }

            if (bestAudioUrl != null && bestAudioUrl.isNotBlank()) {
                add(
                    DownloadQualityOption(
                        title = stringResource(R.string.download_option_audio),
                        subtitle = "Audio M4A",
                        downloadUrl = bestAudioUrl,
                        audioDownloadUrl = null,
                        qualityLabel = "Audio M4A",
                        isAudioOnly = true,
                        icon = Icons.Default.Audiotrack
                    )
                )
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .heightIn(max = 520.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(primaryColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.download_dialog_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.download_dialog_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.common_close),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(options) { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                .clickable {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    onSelectDownload(
                                        option.qualityLabel,
                                        option.downloadUrl,
                                        option.audioDownloadUrl,
                                        option.isAudioOnly
                                    )
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(primaryColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = option.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
