package com.example.yoouutt.ui.feed.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yoouutt.data.model.FeedItem
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.channel.PlaylistCard
import com.example.yoouutt.ui.feed.VideoCard

@Composable
fun FeedContentList(
    items: List<FeedItem>,
    onVideoClick: (VideoItem) -> Unit,
    onChannelClick: (channelUrl: String, channelName: String) -> Unit,
    onPlaylistClick: (playlistUrl: String, playlistTitle: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 0.dp, bottom = 130.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = items,
            key = { it.id }
        ) { feedItem ->
            when (feedItem) {
                is FeedItem.Video -> {
                    VideoCard(
                        video = feedItem.video,
                        onClick = { onVideoClick(feedItem.video) },
                        onChannelClick = onChannelClick
                    )
                }
                is FeedItem.Playlist -> {
                    PlaylistCard(
                        playlist = feedItem.playlist,
                        onClick = { onPlaylistClick(feedItem.playlist.url, feedItem.playlist.title) }
                    )
                }
            }
        }
    }
}
