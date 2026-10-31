package com.example.yoouutt

import android.app.Application
import com.example.yoouutt.data.local.SqliteWatchHistoryRepository
import com.example.yoouutt.data.network.OkHttpDownloader
import com.example.yoouutt.data.repository.WatchHistoryRepository
import com.example.yoouutt.playback.PlaybackManager
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import java.util.Locale

class YoouuttApp : Application() {

    lateinit var historyRepository: WatchHistoryRepository
        private set

    lateinit var playbackManager: PlaybackManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        historyRepository = SqliteWatchHistoryRepository(this)
        playbackManager = PlaybackManager(this, historyRepository)
        val defaultLocale = Locale.getDefault()
        val localization = Localization(defaultLocale.language, defaultLocale.country)
        val contentCountry = ContentCountry(defaultLocale.country)
        NewPipe.init(OkHttpDownloader(), localization, contentCountry)
    }

    companion object {
        lateinit var instance: YoouuttApp
            private set
    }
}
