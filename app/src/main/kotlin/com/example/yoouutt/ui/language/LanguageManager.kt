package com.example.yoouutt.ui.language

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import com.example.yoouutt.data.local.PlayerPreferences
import com.example.yoouutt.data.network.OkHttpDownloader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import java.util.Locale

object LanguageManager {
    private var preferences: PlayerPreferences? = null
    private val _currentLanguage = MutableStateFlow(AppLanguage.SYSTEM)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun init(context: Context) {
        val prefs = PlayerPreferences(context.applicationContext)
        preferences = prefs
        _currentLanguage.value = AppLanguage.fromCode(prefs.appLanguage)
        updateNewPipeLocalization(_currentLanguage.value)
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        preferences?.appLanguage = language.code
        updateNewPipeLocalization(language)
    }

    fun updateNewPipeLocalization(language: AppLanguage) {
        try {
            val locale = resolveLocale(language)
            val localization = Localization(locale.language, locale.country)
            val contentCountry = ContentCountry(locale.country)
            NewPipe.init(OkHttpDownloader(), localization, contentCountry)
        } catch (_: Exception) {
        }
    }

    fun resolveLocale(language: AppLanguage): Locale {
        return when (language) {
            AppLanguage.SYSTEM -> {
                val systemLocales = Resources.getSystem().configuration.locales
                if (!systemLocales.isEmpty) systemLocales[0] else Locale.getDefault()
            }
            AppLanguage.INDONESIAN -> Locale.Builder().setLanguage("in").setRegion("ID").build()
            AppLanguage.ENGLISH -> Locale.ENGLISH
        }
    }

    fun createLocalizedContext(baseContext: Context, language: AppLanguage): Context {
        val locale = resolveLocale(language)
        Locale.setDefault(locale)
        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        val localizedConfigContext = baseContext.createConfigurationContext(config)
        return object : ContextWrapper(baseContext) {
            override fun getResources(): Resources = localizedConfigContext.resources
            override fun getAssets(): AssetManager = localizedConfigContext.assets
        }
    }
}
