package com.example.yoouutt.ui.playlist.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.yoouutt.data.model.PlaylistData
import com.example.yoouutt.data.model.VideoItem

@Composable
fun PlaylistContent(
    playlist: PlaylistData,
    onVideoClick: (video: VideoItem, index: Int, allVideos: List<VideoItem>) -> Unit,
    onChannelClick: ((channelUrl: String, channelName: String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 130.dp)
    ) {
        item(key = "header") {
            PlaylistHeaderSection(
                playlist = playlist,
                onVideoClick = onVideoClick,
                onChannelClick = onChannelClick
            )
        }

        if (playlist.videos.isEmpty()) {
            item(key = "empty") {
                PlaylistEmptyView()
            }
        } else {
            itemsIndexed(
                items = playlist.videos,
                key = { _, item -> item.id }
            ) { index, video ->
                PlaylistItemRow(
                    index = index + 1,
                    video = video,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onVideoClick(video, index, playlist.videos)
                    }
                )
            }
        }
    }
}
