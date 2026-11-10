package com.example.yoouutt.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yoouutt.YoouuttApp
import com.example.yoouutt.ui.channel.ChannelViewModel
import com.example.yoouutt.ui.feed.FeedViewModel
import com.example.yoouutt.ui.history.HistoryViewModel
import com.example.yoouutt.ui.player.PlayerViewModel
import com.example.yoouutt.ui.playlist.PlaylistViewModel

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
                    PlayerViewModel(container.playbackManager, container.historyRepository) as T
                modelClass.isAssignableFrom(PlaylistViewModel::class.java) ->
                    PlaylistViewModel(container.videoRepository) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}

