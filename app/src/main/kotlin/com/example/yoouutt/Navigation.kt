package com.example.yoouutt

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.yoouutt.data.model.PlaylistContext
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.animation.YouTubeScreenTransitions
import com.example.yoouutt.ui.channel.ChannelScreen
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.common.BottomNavigationBar
import com.example.yoouutt.ui.feed.FeedScreen
import com.example.yoouutt.ui.history.HistoryScreen
import com.example.yoouutt.ui.library.LibraryScreen
import com.example.yoouutt.ui.player.InAppPipPlayer
import com.example.yoouutt.ui.player.PlayerScreen
import com.example.yoouutt.ui.playlist.PlaylistScreen
import com.example.yoouutt.ui.playlist.custom.CustomPlaylistDetailScreen
import com.example.yoouutt.ui.playlist.custom.CustomPlaylistDetailViewModel

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Main)
    val playbackManager = YoouuttApp.instance.playbackManager
    val currentPlayback by playbackManager.currentPlayback.collectAsStateWithLifecycle()
    val currentKey = backStack.lastOrNull()
    val isPlayerScreenVisible = currentKey is PlayerKey

    BackHandler(enabled = !isPlayerScreenVisible && currentPlayback != null) {
        playbackManager.stop()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = YouTubeScreenTransitions.appTransitionSpec,
            entryProvider = entryProvider {
                entry<Main> {
                    FeedScreen(
                        onVideoClick = { video ->
                            playbackManager.playVideo(video.url, video.title, video.uploaderName)
                            backStack.add(
                                PlayerKey(
                                    videoUrl = video.url,
                                    title = video.title,
                                    uploader = video.uploaderName
                                )
                            )
                        },
                        onHistoryClick = {
                            backStack.add(LibraryKey)
                        },
                        onChannelClick = { url, name ->
                            backStack.add(ChannelKey(url, name))
                        },
                        onPlaylistClick = { playlistUrl, playlistTitle ->
                            backStack.add(PlaylistKey(playlistUrl, playlistTitle))
                        }
                    )
                }
                entry<LibraryKey> {
                    LibraryScreen(
                        onVideoClick = { url, title, uploader ->
                            playbackManager.playVideo(url, title, uploader)
                            backStack.add(
                                PlayerKey(
                                    videoUrl = url,
                                    title = title,
                                    uploader = uploader
                                )
                            )
                        },
                        onPlayAllSaved = { savedList ->
                            if (savedList.isNotEmpty()) {
                                val videoItems = savedList.map { saved ->
                                    VideoItem(
                                        id = saved.videoId,
                                        title = saved.title,
                                        uploaderName = saved.uploader,
                                        thumbnailUrl = saved.thumbnailUrl,
                                        url = saved.url
                                    )
                                }
                                val context = PlaylistContext(
                                    playlistId = "saved_videos",
                                    playlistTitle = "Video Tersimpan",
                                    videos = videoItems,
                                    currentIndex = 0
                                )
                                playbackManager.playPlaylistVideo(context, 0)
                                val first = videoItems.first()
                                backStack.add(
                                    PlayerKey(
                                        videoUrl = first.url,
                                        title = first.title,
                                        uploader = first.uploaderName
                                    )
                                )
                            }
                        },
                        onCustomPlaylistClick = { playlistId, playlistTitle ->
                            backStack.add(CustomPlaylistDetailKey(playlistId, playlistTitle))
                        },
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                entry<CustomPlaylistDetailKey> { key ->
                    val customPlaylistDetailViewModel: CustomPlaylistDetailViewModel = viewModel(
                        key = "custom_playlist_${key.playlistId}",
                        factory = AppViewModelProvider.customPlaylistDetailFactory(key.playlistId)
                    )
                    CustomPlaylistDetailScreen(
                        playlistId = key.playlistId,
                        playlistTitle = key.playlistTitle,
                        onBack = { backStack.removeLastOrNull() },
                        onPlayPlaylist = { videoItems, startIndex ->
                            if (videoItems.isNotEmpty()) {
                                val context = PlaylistContext(
                                    playlistId = "custom_${key.playlistId}",
                                    playlistTitle = key.playlistTitle,
                                    videos = videoItems,
                                    currentIndex = startIndex
                                )
                                playbackManager.playPlaylistVideo(context, startIndex)
                                val target = videoItems[startIndex]
                                backStack.add(
                                    PlayerKey(
                                        videoUrl = target.url,
                                        title = target.title,
                                        uploader = target.uploaderName
                                    )
                                )
                            }
                        },
                        viewModel = customPlaylistDetailViewModel
                    )
                }
                entry<HistoryKey> {
                    HistoryScreen(
                        onVideoClick = { url, title, uploader ->
                            playbackManager.playVideo(url, title, uploader)
                            backStack.add(
                                PlayerKey(
                                    videoUrl = url,
                                    title = title,
                                    uploader = uploader
                                )
                            )
                        },
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                entry<PlayerKey> { key ->
                    PlayerScreen(
                        videoUrl = key.videoUrl,
                        fallbackTitle = key.title,
                        fallbackUploader = key.uploader,
                        onBack = { backStack.removeLastOrNull() },
                        onChannelClick = { url, name ->
                            backStack.add(ChannelKey(url, name))
                        }
                    )
                }
                entry<ChannelKey> { key ->
                    ChannelScreen(
                        channelUrl = key.channelUrl,
                        channelName = key.channelName,
                        onVideoClick = { video ->
                            playbackManager.playVideo(video.url, video.title, video.uploaderName)
                            backStack.add(
                                PlayerKey(
                                    videoUrl = video.url,
                                    title = video.title,
                                    uploader = video.uploaderName
                                )
                            )
                        },
                        onPlaylistClick = { playlistUrl, playlistTitle ->
                            backStack.add(PlaylistKey(playlistUrl, playlistTitle))
                        },
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                entry<PlaylistKey> { key ->
                    PlaylistScreen(
                        playlistUrl = key.playlistUrl,
                        playlistTitle = key.playlistTitle,
                        onVideoClick = { video, index, allVideos ->
                            val playlistContext = PlaylistContext(
                                playlistId = key.playlistUrl,
                                playlistTitle = key.playlistTitle,
                                videos = allVideos,
                                currentIndex = index
                            )
                            playbackManager.playPlaylistVideo(playlistContext, index)
                            backStack.add(
                                PlayerKey(
                                    videoUrl = video.url,
                                    title = video.title,
                                    uploader = video.uploaderName
                                )
                            )
                        },
                        onChannelClick = { url, name ->
                            backStack.add(ChannelKey(url, name))
                        },
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
            }
        )

        if (!isPlayerScreenVisible) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                val playback = currentPlayback
                if (playback != null) {
                    InAppPipPlayer(
                        playback = playback,
                        playbackManager = playbackManager,
                        onExpand = {
                            backStack.add(
                                PlayerKey(
                                    videoUrl = playback.videoUrl,
                                    title = playback.title,
                                    uploader = playback.uploaderName
                                )
                            )
                        }
                    )
                }

                BottomNavigationBar(
                    isHomeSelected = currentKey is Main,
                    isHistorySelected = currentKey is LibraryKey || currentKey is HistoryKey || currentKey is CustomPlaylistDetailKey,
                    onHomeClick = {
                        while (backStack.size > 1 && backStack.lastOrNull() !is Main) {
                            backStack.removeLastOrNull()
                        }
                        if (backStack.lastOrNull() !is Main) {
                            backStack.add(Main)
                        }
                    },
                    onHistoryClick = {
                        if (backStack.lastOrNull() !is LibraryKey) {
                            if (backStack.any { it is LibraryKey }) {
                                while (backStack.lastOrNull() !is LibraryKey && backStack.size > 1) {
                                    backStack.removeLastOrNull()
                                }
                            } else {
                                backStack.add(LibraryKey)
                            }
                        }
                    }
                )
            }
        }
    }
}
