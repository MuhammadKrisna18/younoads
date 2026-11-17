package com.example.yoouutt.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yoouutt.YoouuttApp
import com.example.yoouutt.ui.channel.ChannelViewModel
import com.example.yoouutt.ui.feed.FeedViewModel
import com.example.yoouutt.ui.history.HistoryViewModel
import com.example.yoouutt.ui.library.LibraryViewModel
import com.example.yoouutt.ui.player.PlayerViewModel
import com.example.yoouutt.ui.playlist.PlaylistViewModel
import com.example.yoouutt.ui.playlist.custom.CustomPlaylistDetailViewModel
import com.example.yoouutt.ui.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = YoouuttApp.instance.container
            return when {
                modelClass.isAssignableFrom(FeedViewModel::class.java) ->
                    FeedViewModel(
                        container.videoRepository,
                        container.historyRepository,
                        container.searchHistoryRepository
                    ) as T
                modelClass.isAssignableFrom(ChannelViewModel::class.java) ->
                    ChannelViewModel(container.videoRepository) as T
                modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                    HistoryViewModel(container.historyRepository) as T
                modelClass.isAssignableFrom(PlayerViewModel::class.java) ->
                    PlayerViewModel(
                        container.playbackManager,
                        container.historyRepository,
                        container.videoRepository,
                        container.savedVideoRepository,
                        container.customPlaylistRepository
                    ) as T
                modelClass.isAssignableFrom(PlaylistViewModel::class.java) ->
                    PlaylistViewModel(container.videoRepository) as T
                modelClass.isAssignableFrom(LibraryViewModel::class.java) ->
                    LibraryViewModel(
                        container.historyRepository,
                        container.savedVideoRepository,
                        container.customPlaylistRepository,
                        container.playbackManager
                    ) as T
                modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                    SettingsViewModel(container.playbackManager) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    fun customPlaylistDetailFactory(playlistId: Long): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = YoouuttApp.instance.container
            return CustomPlaylistDetailViewModel(
                playlistId = playlistId,
                repository = container.customPlaylistRepository
            ) as T
        }
    }
}

