package com.example.yoouutt.di

import android.content.Context
import com.example.yoouutt.data.local.SqliteSearchHistoryRepository
import com.example.yoouutt.data.local.SqliteWatchHistoryRepository
import com.example.yoouutt.data.repository.NewPipeVideoRepository
import com.example.yoouutt.data.repository.SearchHistoryRepository
import com.example.yoouutt.data.repository.VideoRepository
import com.example.yoouutt.data.repository.WatchHistoryRepository
import com.example.yoouutt.playback.PlaybackManager

interface AppContainer {
    val videoRepository: VideoRepository
    val historyRepository: WatchHistoryRepository
    val searchHistoryRepository: SearchHistoryRepository
    val playbackManager: PlaybackManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val historyRepository: WatchHistoryRepository by lazy {
        SqliteWatchHistoryRepository(context)
    }

    override val searchHistoryRepository: SearchHistoryRepository by lazy {
        SqliteSearchHistoryRepository(context)
    }

    override val videoRepository: VideoRepository by lazy {
        NewPipeVideoRepository()
    }

    override val playbackManager: PlaybackManager by lazy {
        PlaybackManager(context, historyRepository, videoRepository)
    }
}

