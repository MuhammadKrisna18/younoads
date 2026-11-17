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
import androidx.compose.ui.unit.sp
import com.example.yoouutt.ui.theme.ElectricCyan
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
        containerColor = Color(0xFF16161C),
        titleContentColor = Color.White,
        textContentColor = Color(0xFFCCCCCC),
        shape = RoundedCornerShape(22.dp),
        title = {
            Column {
                Text(
                    text = "Pembaruan Tersedia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Versi Terbaru:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFAAAAAA)
                    )
                    Text(
                        text = updateInfo.versionName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Apa yang baru di ${updateInfo.versionName}:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))

                val changelogText = updateInfo.changelog.ifBlank {
                    "• Transisi otomatis Picture-in-Picture (Auto-PiP) saat keluar aplikasi\n• Tombol Headset di jendela PiP untuk beralih ke audio latar belakang\n• Kontrol aksi PiP interaktif (Mundur 10 detik, Putar/Jeda dinamis)\n• Penghapusan tombol PiP manual dan perampingan baris aksi pemutar"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF202028))
                        .border(BorderStroke(1.dp, Color(0xFF2E2E38)), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = changelogText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFDDDDDD),
                        lineHeight = 19.sp
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

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
                        color = ElectricCyan,
                        trackColor = Color(0xFF33333D)
                    )
                } else {
                    Text(
                        text = "Versi baru siap diunduh dan dipasang langsung ke HP Anda.",
                        style = MaterialTheme.typography.bodySmall,
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
                        containerColor = ElectricCyan,
                        contentColor = Color.Black
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
                    shape = RoundedCornerShape(50.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
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
