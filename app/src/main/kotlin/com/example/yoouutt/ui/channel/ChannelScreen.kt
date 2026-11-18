package com.example.yoouutt.ui.channel

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.channel.components.ChannelContent
import com.example.yoouutt.ui.channel.components.ChannelErrorState
import com.example.yoouutt.ui.channel.components.ChannelLoadingSkeleton
import com.example.yoouutt.ui.channel.components.ChannelTopBar
import com.example.yoouutt.ui.common.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelScreen(
    channelUrl: String,
    channelName: String,
    onVideoClick: (VideoItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onPlaylistClick: ((playlistUrl: String, playlistTitle: String) -> Unit)? = null,
    viewModel: ChannelViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSearchOpen by remember { mutableStateOf(false) }
    var inChannelQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(channelUrl) {
        viewModel.loadChannel(channelUrl)
    }

    LaunchedEffect(isSearchOpen) {
        if (isSearchOpen) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ChannelTopBar(
                channelName = channelName,
                isSearchOpen = isSearchOpen,
                searchQuery = inChannelQuery,
                focusRequester = focusRequester,
                onBackClick = onBack,
                onOpenSearch = { isSearchOpen = true },
                onCloseSearch = {
                    isSearchOpen = false
                    inChannelQuery = ""
                    viewModel.onSearchQueryChanged("")
                },
                onQueryChange = { query ->
                    inChannelQuery = query
                    viewModel.onSearchQueryChanged(query)
                },
                onClearQuery = {
                    inChannelQuery = ""
                    viewModel.onSearchQueryChanged("")
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState is ChannelUiState.Loading,
            onRefresh = { viewModel.loadChannel(channelUrl) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ChannelUiState.Loading -> ChannelLoadingSkeleton()
                is ChannelUiState.Error -> ChannelErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadChannel(channelUrl) },
                    modifier = Modifier.align(Alignment.Center)
                )
                is ChannelUiState.Success -> {
                    ChannelContent(
                        channel = state.channel,
                        videos = state.sortedVideos,
                        selectedSort = state.currentSort,
                        currentTab = state.currentTab,
                        playlists = state.playlists,
                        isPlaylistsLoading = state.isPlaylistsLoading,
                        isLoadingMore = state.isLoadingMore,
                        onTabChange = viewModel::onTabSelected,
                        onSortChange = viewModel::onSortSelected,
                        onLoadMore = viewModel::loadMore,
                        onVideoClick = onVideoClick,
                        onPlaylistClick = onPlaylistClick
                    )
                }
            }
        }
    }
}
