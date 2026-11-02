package com.example.yoouutt

import android.app.Application
import com.example.yoouutt.data.network.OkHttpDownloader
import com.example.yoouutt.data.repository.WatchHistoryRepository
import com.example.yoouutt.di.AppContainer
import com.example.yoouutt.di.DefaultAppContainer
import com.example.yoouutt.playback.PlaybackManager
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import java.util.Locale

class YoouuttApp : Application() {

    lateinit var container: AppContainer
        private set

    val historyRepository: WatchHistoryRepository
        get() = container.historyRepository

    val playbackManager: PlaybackManager
        get() = container.playbackManager

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = DefaultAppContainer(this)
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
