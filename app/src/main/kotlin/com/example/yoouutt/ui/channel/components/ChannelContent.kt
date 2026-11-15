package com.example.yoouutt.ui.channel.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yoouutt.data.model.ChannelData
import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.channel.ChannelSortOption
import com.example.yoouutt.ui.channel.ChannelTab
import com.example.yoouutt.ui.channel.PlaylistCard
import com.example.yoouutt.ui.feed.VideoCard

@Composable
fun ChannelContent(
    channel: ChannelData,
    videos: List<VideoItem>,
    selectedSort: ChannelSortOption,
    currentTab: ChannelTab,
    playlists: List<PlaylistItem>,
    isPlaylistsLoading: Boolean,
    isLoadingMore: Boolean,
    onTabChange: (ChannelTab) -> Unit,
    onSortChange: (ChannelSortOption) -> Unit,
    onLoadMore: () -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onPlaylistClick: ((playlistUrl: String, playlistTitle: String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleIndex >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 130.dp)
    ) {
        item(key = "header_section") {
            ChannelHeaderSection(channel = channel)
        }

        item(key = "nav_section") {
            ChannelTabNavigation(
                currentTab = currentTab,
                selectedSort = selectedSort,
                onTabChange = onTabChange,
                onSortChange = onSortChange
            )
        }

        if (currentTab == ChannelTab.VIDEOS) {
            if (videos.isEmpty()) {
                item(key = "empty_videos") {
                    ChannelEmptyView(message = "Tidak ada video yang ditemukan")
                }
            } else {
                items(
                    items = videos,
                    key = { it.id }
                ) { video ->
                    VideoCard(
                        video = video,
                        onClick = { onVideoClick(video) }
                    )
                }

                if (isLoadingMore) {
                    item(key = "loading_more") {
                        ChannelLoadingMoreIndicator()
                    }
                }
            }
        } else {
            if (isPlaylistsLoading) {
                item(key = "loading_playlists") {
                    ChannelPlaylistsLoadingIndicator()
                }
            } else if (playlists.isEmpty()) {
                item(key = "empty_playlists") {
                    ChannelEmptyView(message = "Channel ini belum memiliki playlist")
                }
            } else {
                items(
                    items = playlists,
                    key = { it.id }
                ) { playlist ->
                    PlaylistCard(
                        playlist = playlist,
                        onClick = { onPlaylistClick?.invoke(playlist.url, playlist.title) }
                    )
                }
            }
        }
    }
}
