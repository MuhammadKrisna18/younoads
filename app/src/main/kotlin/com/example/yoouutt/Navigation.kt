package com.example.yoouutt

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.example.yoouutt.ui.settings.SettingsScreen

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
                            backStack.add(SettingsKey)
                        },
                        onChannelClick = { url, name ->
                            backStack.add(ChannelKey(url, name))
                        },
                        onPlaylistClick = { playlistUrl, playlistTitle ->
                            backStack.add(PlaylistKey(playlistUrl, playlistTitle))
                        }
                    )
                }
                entry<SettingsKey> {
                    SettingsScreen(
                        onNavigateToPlaylists = {
                            backStack.add(LibraryKey(initialTab = 1))
                        },
                        onNavigateToSaved = {
                            backStack.add(LibraryKey(initialTab = 0))
                        }
                    )
                }
                entry<LibraryKey> { key ->
                    LibraryScreen(
                        initialTab = key.initialTab,
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
                        onBack = null,
                        onExploreClick = {
                            while (backStack.size > 1 && backStack.lastOrNull() !is Main) {
                                backStack.removeLastOrNull()
                            }
                            if (backStack.lastOrNull() !is Main) {
                                backStack.add(Main)
                            }
                        }
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

        AnimatedVisibility(
            visible = !isPlayerScreenVisible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            ) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 180, easing = FastOutLinearInEasing)
            ) + slideOutVertically(
                targetOffsetY = { it / 3 },
                animationSpec = tween(durationMillis = 180, easing = FastOutLinearInEasing)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
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
                    isHistorySelected = currentKey is HistoryKey,
                    isSettingsSelected = currentKey is SettingsKey || currentKey is LibraryKey || currentKey is CustomPlaylistDetailKey,
                    onHomeClick = {
                        while (backStack.size > 1 && backStack.lastOrNull() !is Main) {
                            backStack.removeLastOrNull()
                        }
                        if (backStack.lastOrNull() !is Main) {
                            backStack.add(Main)
                        }
                    },
                    onHistoryClick = {
                        if (backStack.lastOrNull() !is HistoryKey) {
                            if (backStack.any { it is HistoryKey }) {
                                while (backStack.lastOrNull() !is HistoryKey && backStack.size > 1) {
                                    backStack.removeLastOrNull()
                                }
                            } else {
                                backStack.add(HistoryKey)
                            }
                        }
                    },
                    onSettingsClick = {
                        if (backStack.lastOrNull() !is SettingsKey) {
                            if (backStack.any { it is SettingsKey }) {
                                while (backStack.lastOrNull() !is SettingsKey && backStack.size > 1) {
                                    backStack.removeLastOrNull()
                                }
                            } else {
                                backStack.add(SettingsKey)
                            }
                        }
                    }
                )
            }
        }
    }
}
