package com.example.yoouutt.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yoouutt.update.UpdateInfo

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    downloadProgress: Float?,
    onUpdateClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDownloading = downloadProgress != null

    AlertDialog(
        onDismissRequest = {
            if (!isDownloading) onDismiss()
        },
        containerColor = Color(0xFF141414),
        titleContentColor = Color.White,
        textContentColor = Color(0xFFCCCCCC),
        shape = RoundedCornerShape(20.dp),
        title = {
            Column {
                Text(
                    text = "Pembaruan Tersedia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = updateInfo.versionName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandRed
                )
            }
        },
        text = {
            Column {
                if (updateInfo.changelog.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1F1F1F))
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = updateInfo.changelog,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFDDDDDD)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (downloadProgress != null) {
                    val progress = downloadProgress
                    val percentage = (progress * 100).toInt()
                    Text(
                        text = "Mengunduh pembaruan... $percentage%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = BrandRed,
                        trackColor = Color(0xFF333333)
                    )
                } else {
                    Text(
                        text = "Versi baru siap dipasang langsung ke HP Anda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFAAAAAA)
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
                        containerColor = BrandRed,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Perbarui Sekarang",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            if (!isDownloading) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "Nanti",
                        color = Color.White
                    )
                }
            }
        }
    )
}
