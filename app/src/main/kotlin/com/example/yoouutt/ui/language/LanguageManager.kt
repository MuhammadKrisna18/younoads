package com.example.yoouutt.ui.language

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.example.yoouutt.data.local.PlayerPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object LanguageManager {
    private var preferences: PlayerPreferences? = null
    private val _currentLanguage = MutableStateFlow(AppLanguage.SYSTEM)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun init(context: Context) {
        val prefs = PlayerPreferences(context.applicationContext)
        preferences = prefs
        _currentLanguage.value = AppLanguage.fromCode(prefs.appLanguage)
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        preferences?.appLanguage = language.code
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
        return baseContext.createConfigurationContext(config)
    }
}
