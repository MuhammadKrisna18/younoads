package com.example.yoouutt.ui.theme

import android.content.Context
import com.example.yoouutt.data.local.PlayerPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    private var preferences: PlayerPreferences? = null
    private val _currentTheme = MutableStateFlow(AppTheme.MATTE_BLACK)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    fun init(context: Context) {
        val prefs = PlayerPreferences(context.applicationContext)
        preferences = prefs
        _currentTheme.value = AppTheme.fromId(prefs.appTheme)
    }

    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
        preferences?.appTheme = theme.id
    }
}
