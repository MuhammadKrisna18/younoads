package com.example.yoouutt.data.local

import android.content.Context
import android.content.SharedPreferences

class PlayerPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isAutoplayEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTOPLAY, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTOPLAY, value).apply()

    var isLoopEnabled: Boolean
        get() = prefs.getBoolean(KEY_LOOP, false)
        set(value) = prefs.edit().putBoolean(KEY_LOOP, value).apply()

    companion object {
        private const val PREFS_NAME = "younoads_player_prefs"
        private const val KEY_AUTOPLAY = "is_autoplay_enabled"
        private const val KEY_LOOP = "is_loop_enabled"
    }
}
