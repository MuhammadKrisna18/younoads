package com.example.yoouutt.ui.player.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoouutt.data.model.PlaylistContext
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.feed.VideoCard
import com.example.yoouutt.ui.theme.ElectricCyan

@Composable
fun PlayerDetailContent(
    currentTitle: String,
    currentUploader: String,
    streamData: StreamData?,
    playlistContext: PlaylistContext? = null,
    isSaved: Boolean,
    isDescriptionExpanded: Boolean,
    videoUrl: String,
    onShareClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onToggleSave: () -> Unit,
    onAddToPlaylistClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onToggleDescription: () -> Unit,
    onPlaylistVideoClick: ((Int) -> Unit)? = null,
    onChannelClick: ((channelUrl: String, channelName: String) -> Unit)?,
    onRelatedVideoClick: (VideoItem) -> Unit,
    recommendedVideos: List<VideoItem> = emptyList(),
    isLoadingRecommendations: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item(key = "title_metadata") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = currentTitle,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (streamData != null) {
                        val metaLine = buildString {
                            if (streamData.viewCountText.isNotBlank()) append(streamData.viewCountText)
                            if (streamData.viewCountText.isNotBlank() && streamData.uploadDateText.isNotBlank()) append(" • ")
                            if (streamData.uploadDateText.isNotBlank()) append(streamData.uploadDateText)
                        }
                        if (metaLine.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = metaLine,
                                fontSize = 12.sp,
                                color = Color(0xFFAAAAAA)
                            )
                        }
                    }
                }
            }

            if (playlistContext != null && playlistContext.videos.isNotEmpty()) {
                item(key = "playlist_banner") {
                    PlayerPlaylistBanner(
                        playlistContext = playlistContext,
                        onVideoClick = { index -> onPlaylistVideoClick?.invoke(index) }
                    )
                }
            }

            item(key = "channel_header") {
                PlayerChannelHeader(
                    uploaderName = currentUploader,
                    streamData = streamData,
                    onChannelClick = {
                        val targetUrl = streamData?.uploaderUrl?.takeIf { it.isNotBlank() } ?: currentUploader
                        onChannelClick?.invoke(targetUrl, currentUploader)
                    }
                )
            }

            item(key = "action_pills") {
                PlayerActionPillRow(
                    videoUrl = videoUrl,
                    isSaved = isSaved,
                    onShareClick = onShareClick,
                    onCopyLinkClick = onCopyLinkClick,
                    onToggleSave = onToggleSave,
                    onAddToPlaylistClick = onAddToPlaylistClick,
                    onSettingsClick = onSettingsClick
                )
            }

            item(key = "description_card") {
                PlayerDescriptionCard(
                    streamData = streamData,
                    isExpanded = isDescriptionExpanded,
                    onToggleExpand = onToggleDescription
                )
            }

            val displayVideos = if (recommendedVideos.isNotEmpty()) {
                recommendedVideos
            } else {
                streamData?.relatedVideos.orEmpty()
            }

            if (displayVideos.isNotEmpty()) {
                item(key = "related_header") {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Rekomendasi Video",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(
                    items = displayVideos,
                    key = { it.id }
                ) { relVideo ->
                    VideoCard(
                        video = relVideo,
                        onClick = { onRelatedVideoClick(relVideo) },
                        onChannelClick = onChannelClick
                    )
                }
            } else if (isLoadingRecommendations) {
                item(key = "recommendations_loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ElectricCyan,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
