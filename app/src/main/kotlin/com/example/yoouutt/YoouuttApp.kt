package com.example.yoouutt

import android.app.Application
import com.example.yoouutt.data.network.OkHttpDownloader
import com.example.yoouutt.data.repository.WatchHistoryRepository
import com.example.yoouutt.di.AppContainer
import com.example.yoouutt.di.DefaultAppContainer
import com.example.yoouutt.playback.PlaybackManager
import com.example.yoouutt.ui.icon.AppIconManager
import com.example.yoouutt.ui.language.LanguageManager
import com.example.yoouutt.ui.theme.ThemeManager
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
        ThemeManager.init(this)
        LanguageManager.init(this)
        AppIconManager.init(this)
        val locale = LanguageManager.resolveLocale(LanguageManager.currentLanguage.value)
        val localization = Localization(locale.language, locale.country)
        val contentCountry = ContentCountry(locale.country)
        NewPipe.init(OkHttpDownloader(), localization, contentCountry)
    }

    companion object {
        lateinit var instance: YoouuttApp
            private set
    }
}
