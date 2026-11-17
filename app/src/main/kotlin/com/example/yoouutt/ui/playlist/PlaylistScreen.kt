package com.example.yoouutt.ui.playlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.player.PlayerUtils
import com.example.yoouutt.ui.playlist.components.PlaylistContent
import com.example.yoouutt.ui.playlist.components.PlaylistErrorState
import com.example.yoouutt.ui.playlist.components.PlaylistLoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    playlistUrl: String,
    playlistTitle: String,
    onVideoClick: (video: VideoItem, index: Int, allVideos: List<VideoItem>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onChannelClick: ((channelUrl: String, channelName: String) -> Unit)? = null,
    viewModel: PlaylistViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(playlistUrl) {
        viewModel.loadPlaylist(playlistUrl)
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = playlistTitle.ifBlank { "Playlist" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        PlayerUtils.shareText(context, playlistUrl, "Bagikan Playlist")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Playlist",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState is PlaylistUiState.Loading,
            onRefresh = { viewModel.loadPlaylist(playlistUrl) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is PlaylistUiState.Loading -> PlaylistLoadingIndicator()
                is PlaylistUiState.Error -> PlaylistErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadPlaylist(playlistUrl) },
                    modifier = Modifier.align(Alignment.Center)
                )
                is PlaylistUiState.Success -> {
                    PlaylistContent(
                        playlist = state.playlist,
                        onVideoClick = onVideoClick,
                        onChannelClick = onChannelClick
                    )
                }
            }
        }
    }
}
