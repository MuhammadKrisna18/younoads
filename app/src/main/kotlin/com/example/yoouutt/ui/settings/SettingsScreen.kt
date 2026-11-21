package com.example.yoouutt.ui.settings

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.BuildConfig
import com.example.yoouutt.R
import com.example.yoouutt.ui.common.AppThemeBackdrop
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.icon.AppIconManager
import com.example.yoouutt.ui.icon.components.AppIconSelectionDialog
import com.example.yoouutt.ui.language.LanguageManager
import com.example.yoouutt.ui.language.components.LanguageSelectionDialog
import com.example.yoouutt.ui.player.components.DefaultQualitySelectionDialog
import com.example.yoouutt.ui.theme.ThemeManager
import com.example.yoouutt.ui.theme.components.ThemeSelectionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToPlaylists: () -> Unit,
    onNavigateToSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val defaultQuality by viewModel.defaultQuality.collectAsStateWithLifecycle()
    val currentTheme by ThemeManager.currentTheme.collectAsStateWithLifecycle()
    val currentLanguage by LanguageManager.currentLanguage.collectAsStateWithLifecycle()
    val currentIcon by AppIconManager.currentIcon.collectAsStateWithLifecycle()

    var showQualityDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showIconDialog by remember { mutableStateOf(false) }

    if (showQualityDialog) {
        DefaultQualitySelectionDialog(
            currentQuality = defaultQuality,
            onQualitySelected = viewModel::setDefaultQuality,
            onDismiss = { showQualityDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            onThemeSelected = ThemeManager::setTheme,
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = LanguageManager::setLanguage,
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showIconDialog) {
        AppIconSelectionDialog(
            currentIcon = currentIcon,
            onIconSelected = AppIconManager::setIcon,
            onDismiss = { showIconDialog = false }
        )
    }

    AppThemeBackdrop(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_category_preferences))
            }

            item {
                SettingsActionCard(
                    icon = Icons.Default.HighQuality,
                    title = stringResource(R.string.settings_default_quality),
                    subtitle = stringResource(R.string.default_quality_dialog_subtitle),
                    value = stringResource(defaultQuality.titleRes),
                    onClick = { showQualityDialog = true }
                )
            }

            item {
                SettingsActionCard(
                    icon = Icons.Default.Palette,
                    title = stringResource(R.string.settings_theme),
                    subtitle = stringResource(currentTheme.descriptionRes),
                    value = stringResource(currentTheme.titleRes),
                    onClick = { showThemeDialog = true }
                )
            }

            item {
                SettingsActionCard(
                    icon = Icons.Default.AppShortcut,
                    title = stringResource(R.string.settings_app_icon),
                    subtitle = stringResource(currentIcon.descriptionRes),
                    value = stringResource(currentIcon.titleRes),
                    onClick = { showIconDialog = true }
                )
            }

            item {
                SettingsActionCard(
                    icon = Icons.Default.Translate,
                    title = stringResource(R.string.settings_language),
                    subtitle = stringResource(currentLanguage.descriptionRes),
                    value = stringResource(currentLanguage.titleRes),
                    onClick = { showLanguageDialog = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                SettingsSectionHeader(title = stringResource(R.string.settings_category_content))
            }

            item {
                SettingsActionCard(
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    title = stringResource(R.string.settings_playlists_title),
                    subtitle = stringResource(R.string.settings_playlists_desc),
                    onClick = onNavigateToPlaylists
                )
            }

            item {
                SettingsActionCard(
                    icon = Icons.Default.Bookmark,
                    title = stringResource(R.string.settings_saved_title),
                    subtitle = stringResource(R.string.settings_saved_desc),
                    onClick = onNavigateToSaved
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                SettingsSectionHeader(title = stringResource(R.string.settings_category_about))
            }

            item {
                SettingsActionCard(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.settings_app_version),
                    subtitle = stringResource(R.string.settings_app_description),
                    value = "v${BuildConfig.VERSION_NAME}",
                    onClick = {}
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String? = null,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                shape
            )
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }

        if (value != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                color = primaryColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}
