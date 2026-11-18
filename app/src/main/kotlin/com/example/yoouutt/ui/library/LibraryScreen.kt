package com.example.yoouutt.ui.library

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.example.yoouutt.data.model.SavedVideo
import com.example.yoouutt.data.model.WatchHistoryItem
import com.example.yoouutt.ui.common.AppConfirmDialog
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.history.components.HistoryItemRow
import com.example.yoouutt.ui.library.components.CustomPlaylistCard
import com.example.yoouutt.ui.library.components.LibraryEmptyTabState
import com.example.yoouutt.ui.library.components.SavedVideoItemRow
import com.example.yoouutt.ui.player.components.CreatePlaylistDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    initialTab: Int = 0,
    onVideoClick: (String, String, String) -> Unit,
    onPlayAllSaved: (List<SavedVideo>) -> Unit,
    onCustomPlaylistClick: (playlistId: Long, playlistTitle: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val savedVideos by viewModel.savedVideos.collectAsStateWithLifecycle()
    val customPlaylists by viewModel.customPlaylists.collectAsStateWithLifecycle()
    val historyItems by viewModel.historyItems.collectAsStateWithLifecycle()
    val isCreatePlaylistDialogVisible by viewModel.isCreatePlaylistDialogVisible.collectAsStateWithLifecycle()

    var showClearSavedDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var playlistToDeleteId by remember { mutableStateOf<Long?>(null) }
    var historyItemToDelete by remember { mutableStateOf<WatchHistoryItem?>(null) }

    LaunchedEffect(initialTab) {
        viewModel.selectTab(initialTab)
    }

    val view = LocalView.current
    val tabs = listOf(
        stringResource(R.string.library_tab_saved, savedVideos.size),
        stringResource(R.string.library_tab_playlists, customPlaylists.size),
        stringResource(R.string.library_tab_history, historyItems.size)
    )

    if (showClearSavedDialog) {
        AppConfirmDialog(
            title = stringResource(R.string.library_clear_saved_title),
            message = stringResource(R.string.library_clear_saved_msg),
            onConfirm = {
                viewModel.clearAllSaved()
                showClearSavedDialog = false
            },
            onDismiss = { showClearSavedDialog = false }
        )
    }

    if (showClearHistoryDialog) {
        AppConfirmDialog(
            title = stringResource(R.string.library_clear_history_title),
            message = stringResource(R.string.library_clear_history_msg),
            onConfirm = {
                viewModel.clearAllHistory()
                showClearHistoryDialog = false
            },
            onDismiss = { showClearHistoryDialog = false }
        )
    }

    val currentHistoryToDelete = historyItemToDelete
    if (currentHistoryToDelete != null) {
        AppConfirmDialog(
            title = stringResource(R.string.history_delete_single_title),
            message = stringResource(R.string.history_delete_single_msg, currentHistoryToDelete.title),
            onConfirm = {
                viewModel.deleteHistoryItem(currentHistoryToDelete.videoId)
                historyItemToDelete = null
            },
            onDismiss = { historyItemToDelete = null }
        )
    }

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
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.library_title),
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
                    if (selectedTab == 0 && savedVideos.isNotEmpty()) {
                        IconButton(onClick = { showClearSavedDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.library_clear_saved_action),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else if (selectedTab == 2 && historyItems.isNotEmpty()) {
                        IconButton(onClick = { showClearHistoryDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.library_clear_history_action),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            viewModel.selectTab(index)
                        },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(tween(220)) togetherWith fadeOut(tween(160))
                },
                label = "library_tab_transition",
                modifier = Modifier.fillMaxSize()
            ) { tab ->
                when (tab) {
                    0 -> {
                        if (savedVideos.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LibraryEmptyTabState(
                                    title = stringResource(R.string.library_empty_saved_title),
                                    message = stringResource(R.string.library_empty_saved_desc),
                                    actionButtonText = stringResource(R.string.library_btn_explore),
                                    onActionClick = onBack
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 130.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item(key = "saved_header") {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(R.string.library_saved_count, savedVideos.size),
                                            color = Color(0xFFAAAAAA),
                                            fontSize = 12.5.sp
                                        )

                                        Button(
                                            onClick = { onPlayAllSaved(savedVideos) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(50.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = stringResource(R.string.library_btn_play_all),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                        }
                                    }
                                }

                                items(savedVideos, key = { it.videoId }) { item ->
                                    SavedVideoItemRow(
                                        item = item,
                                        onClick = { onVideoClick(item.url, item.title, item.uploader) },
                                        onDelete = {
                                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                            viewModel.unsaveVideo(item.videoId)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
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
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text(
                                        text = stringResource(R.string.library_btn_create_playlist),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            if (customPlaylists.isEmpty()) {
                                item(key = "empty_playlists") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LibraryEmptyTabState(
                                            title = stringResource(R.string.library_empty_playlists_title),
                                            message = stringResource(R.string.library_empty_playlists_desc)
                                        )
                                    }
                                }
                            } else {
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

                    2 -> {
                        if (historyItems.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LibraryEmptyTabState(
                                    title = stringResource(R.string.library_empty_history_title),
                                    message = stringResource(R.string.library_empty_history_desc),
                                    actionButtonText = stringResource(R.string.library_btn_explore),
                                    onActionClick = onBack
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 130.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(historyItems, key = { it.videoId }) { item ->
                                    HistoryItemRow(
                                        item = item,
                                        onClick = { onVideoClick(item.videoUrl, item.title, item.uploaderName) },
                                        onDelete = {
                                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                            historyItemToDelete = item
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
