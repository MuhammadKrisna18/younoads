package com.example.yoouutt.ui.library

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.R
import com.example.yoouutt.ui.common.AppConfirmDialog
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.library.components.CustomPlaylistCard
import com.example.yoouutt.ui.player.components.CreatePlaylistDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    onCustomPlaylistClick: (playlistId: Long, playlistTitle: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val customPlaylists by viewModel.customPlaylists.collectAsStateWithLifecycle()
    val isCreatePlaylistDialogVisible by viewModel.isCreatePlaylistDialogVisible.collectAsStateWithLifecycle()
    var playlistToDeleteId by remember { mutableStateOf<Long?>(null) }
    val view = LocalView.current

    if (playlistToDeleteId != null) {
        AppConfirmDialog(
            title = stringResource(R.string.library_delete_playlist_title),
            message = stringResource(R.string.library_delete_playlist_msg),
            onConfirm = {
                playlistToDeleteId?.let { viewModel.deletePlaylist(it) }
                playlistToDeleteId = null
            },
            onDismiss = { playlistToDeleteId = null }
        )
    }

    if (isCreatePlaylistDialogVisible) {
        CreatePlaylistDialog(
            onDismiss = viewModel::dismissCreatePlaylistDialog,
            onCreate = viewModel::createPlaylist
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_playlists_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            viewModel.showCreatePlaylistDialog()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.library_btn_create_playlist),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "create_playlist_button") {
                Button(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        viewModel.showCreatePlaylistDialog()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.library_btn_create_playlist),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            items(customPlaylists, key = { it.id }) { playlist ->
                CustomPlaylistCard(
                    playlist = playlist,
                    onClick = { onCustomPlaylistClick(playlist.id, playlist.title) },
                    onDelete = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        playlistToDeleteId = playlist.id
                    }
                )
            }
        }
    }
}
