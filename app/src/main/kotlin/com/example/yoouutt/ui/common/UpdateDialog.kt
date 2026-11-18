package com.example.yoouutt.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoouutt.R
import com.example.yoouutt.update.UpdateInfo

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    downloadProgress: Float?,
    onUpdateClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDownloading = downloadProgress != null
    val primaryColor = MaterialTheme.colorScheme.primary

    AlertDialog(
        onDismissRequest = {
            if (!isDownloading) onDismiss()
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(22.dp),
        title = {
            Column {
                Text(
                    text = stringResource(R.string.update_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.update_dialog_latest_version),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = updateInfo.versionName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.update_dialog_whats_new, updateInfo.versionName),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                val changelogText = updateInfo.changelog.ifBlank {
                    stringResource(R.string.update_dialog_default_changelog)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = changelogText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                if (downloadProgress != null) {
                    val progress = downloadProgress
                    val percentage = (progress * 100).toInt()
                    Text(
                        text = stringResource(R.string.update_dialog_downloading, percentage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = primaryColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                } else {
                    Text(
                        text = stringResource(R.string.update_dialog_ready_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            if (!isDownloading) {
                Button(
                    onClick = onUpdateClick,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.update_dialog_btn_update),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            if (!isDownloading) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(50.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = stringResource(R.string.update_dialog_btn_later),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
}
