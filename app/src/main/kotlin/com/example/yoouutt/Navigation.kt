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
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.yoouutt.ui.channel.ChannelScreen
import com.example.yoouutt.ui.common.BottomNavigationBar
import com.example.yoouutt.ui.feed.FeedScreen
import com.example.yoouutt.ui.history.HistoryScreen
import com.example.yoouutt.ui.player.InAppPipPlayer
import com.example.yoouutt.ui.player.PlayerScreen

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
                            backStack.add(HistoryKey)
                        },
                        onChannelClick = { url, name ->
                            backStack.add(ChannelKey(url, name))
                        }
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
                    isHistorySelected = currentKey is HistoryKey,
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
                    }
                )
            }
        }
    }
}
