package com.example.yoouutt.di

import android.content.Context
import com.example.yoouutt.data.download.VideoDownloadManager
import com.example.yoouutt.data.local.SqliteCustomPlaylistRepository
import com.example.yoouutt.data.local.SqliteDownloadRepository
import com.example.yoouutt.data.local.SqliteSavedVideoRepository
import com.example.yoouutt.data.local.SqliteSearchHistoryRepository
import com.example.yoouutt.data.local.SqliteWatchHistoryRepository
import com.example.yoouutt.data.repository.CustomPlaylistRepository
import com.example.yoouutt.data.repository.DownloadRepository
import com.example.yoouutt.data.repository.NewPipeVideoRepository
import com.example.yoouutt.data.repository.SavedVideoRepository
import com.example.yoouutt.data.repository.SearchHistoryRepository
import com.example.yoouutt.data.repository.VideoRepository
import com.example.yoouutt.data.repository.WatchHistoryRepository
import com.example.yoouutt.playback.PlaybackManager

interface AppContainer {
    val videoRepository: VideoRepository
    val historyRepository: WatchHistoryRepository
    val searchHistoryRepository: SearchHistoryRepository
    val savedVideoRepository: SavedVideoRepository
    val customPlaylistRepository: CustomPlaylistRepository
    val playbackManager: PlaybackManager
    val downloadRepository: DownloadRepository
    val downloadManager: VideoDownloadManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val historyRepository: WatchHistoryRepository by lazy {
        SqliteWatchHistoryRepository(context)
    }

    override val searchHistoryRepository: SearchHistoryRepository by lazy {
        SqliteSearchHistoryRepository(context)
    }

    override val savedVideoRepository: SavedVideoRepository by lazy {
        SqliteSavedVideoRepository(context)
    }

    override val customPlaylistRepository: CustomPlaylistRepository by lazy {
        SqliteCustomPlaylistRepository(context)
    }

    override val videoRepository: VideoRepository by lazy {
        NewPipeVideoRepository()
    }

    override val playbackManager: PlaybackManager by lazy {
        PlaybackManager(context, historyRepository, videoRepository)
    }

    override val downloadRepository: DownloadRepository by lazy {
        SqliteDownloadRepository(context)
    }

    override val downloadManager: VideoDownloadManager by lazy {
        VideoDownloadManager(context, downloadRepository)
    }
}

