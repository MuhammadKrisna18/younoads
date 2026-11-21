package com.example.yoouutt.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.yoouutt.data.model.DefaultVideoQuality

class PlayerPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isAutoplayEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTOPLAY, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTOPLAY, value).apply()

    var isLoopEnabled: Boolean
        get() = prefs.getBoolean(KEY_LOOP, false)
        set(value) = prefs.edit().putBoolean(KEY_LOOP, value).apply()

    var seekIntervalSeconds: Int
        get() = prefs.getInt(KEY_SEEK_INTERVAL, 10)
        set(value) = prefs.edit().putInt(KEY_SEEK_INTERVAL, value).apply()

    var appTheme: String
        get() = prefs.getString(KEY_APP_THEME, "CYBERPUNK") ?: "CYBERPUNK"
        set(value) = prefs.edit().putString(KEY_APP_THEME, value).apply()

    var appLanguage: String
        get() = prefs.getString(KEY_APP_LANGUAGE, "SYSTEM") ?: "SYSTEM"
        set(value) = prefs.edit().putString(KEY_APP_LANGUAGE, value).apply()

    var appIcon: String
        get() = prefs.getString(KEY_APP_ICON, "DEFAULT") ?: "DEFAULT"
        set(value) = prefs.edit().putString(KEY_APP_ICON, value).apply()

    var defaultQuality: String
        get() = prefs.getString(KEY_DEFAULT_QUALITY, DefaultVideoQuality.AUTO.code) ?: DefaultVideoQuality.AUTO.code
        set(value) = prefs.edit().putString(KEY_DEFAULT_QUALITY, value).apply()

    companion object {
        private const val PREFS_NAME = "younoads_player_prefs"
        private const val KEY_AUTOPLAY = "is_autoplay_enabled"
        private const val KEY_LOOP = "is_loop_enabled"
        private const val KEY_SEEK_INTERVAL = "seek_interval_seconds"
        private const val KEY_APP_THEME = "app_theme"
        private const val KEY_APP_LANGUAGE = "app_language"
        private const val KEY_APP_ICON = "app_icon"
        private const val KEY_DEFAULT_QUALITY = "default_video_quality"
    }
}
