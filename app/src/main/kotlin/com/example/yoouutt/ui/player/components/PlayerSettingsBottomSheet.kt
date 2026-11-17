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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoouutt.R
import com.example.yoouutt.ui.theme.ElectricCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettingsBottomSheet(
    resolutionLabel: String,
    speedText: String,
    seekIntervalText: String = "10 detik",
    audioTrackText: String = "",
    hasMultipleAudioTracks: Boolean = false,
    themeText: String = "Cyberpunk Neo-Tokyo",
    languageText: String = "Mengikuti Sistem",
    isLoopEnabled: Boolean,
    isAutoplayEnabled: Boolean,
    onSelectQualityClick: () -> Unit,
    onSelectSpeedClick: () -> Unit,
    onSelectSeekIntervalClick: () -> Unit = {},
    onSelectAudioTrackClick: () -> Unit = {},
    onSelectThemeClick: () -> Unit = {},
    onSelectLanguageClick: () -> Unit = {},
    onAddToPlaylistClick: () -> Unit = {},
    onToggleLoop: () -> Unit,
    onToggleAutoplay: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF161616),
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF555555))
            )
        },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            SettingsNavigationRow(
                icon = Icons.Default.HighQuality,
                title = stringResource(R.string.settings_quality),
                value = resolutionLabel.ifBlank { stringResource(R.string.settings_auto) },
                onClick = onSelectQualityClick
            )

            SettingsNavigationRow(
                icon = Icons.Default.Speed,
                title = stringResource(R.string.settings_speed),
                value = speedText,
                onClick = onSelectSpeedClick
            )

            SettingsNavigationRow(
                icon = Icons.Default.FastForward,
                title = stringResource(R.string.settings_seek_interval),
                value = seekIntervalText,
                onClick = onSelectSeekIntervalClick
            )

            if (hasMultipleAudioTracks && audioTrackText.isNotBlank()) {
                SettingsNavigationRow(
                    icon = Icons.Default.Audiotrack,
                    title = stringResource(R.string.settings_audio_track),
                    value = audioTrackText,
                    onClick = onSelectAudioTrackClick
                )
            }

            SettingsNavigationRow(
                icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                title = stringResource(R.string.settings_add_to_playlist),
                value = "",
                onClick = {
                    onDismiss()
                    onAddToPlaylistClick()
                }
            )

            SettingsNavigationRow(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.settings_theme),
                value = themeText,
                onClick = onSelectThemeClick
            )

            SettingsNavigationRow(
                icon = Icons.Default.Translate,
                title = stringResource(R.string.settings_language),
                value = languageText,
                onClick = onSelectLanguageClick
            )

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            SettingsToggleRow(
                icon = Icons.Default.Repeat,
                title = stringResource(R.string.settings_loop),
                isChecked = isLoopEnabled,
                onToggle = onToggleLoop
            )

            SettingsToggleRow(
                icon = Icons.Default.AutoMode,
                title = stringResource(R.string.settings_autoplay),
                isChecked = isAutoplayEnabled,
                onToggle = onToggleAutoplay
            )
        }
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()
            }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFAAAAAA)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF888888),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onToggle()
            }
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onToggle()
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = ElectricCyan,
                uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                uncheckedTrackColor = Color(0xFF2C2C32),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
