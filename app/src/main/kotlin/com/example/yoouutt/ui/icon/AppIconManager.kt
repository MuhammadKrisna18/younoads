package com.example.yoouutt.ui.icon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.example.yoouutt.data.local.PlayerPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppIconManager {
    private const val DEFAULT_ACTIVITY_NAME = "com.example.yoouutt.MainActivity"

    private var appContext: Context? = null
    private var preferences: PlayerPreferences? = null

    private val _currentIcon = MutableStateFlow(AppIcon.DEFAULT)
    val currentIcon: StateFlow<AppIcon> = _currentIcon.asStateFlow()

    fun init(context: Context) {
        val app = context.applicationContext
        appContext = app
        val prefs = PlayerPreferences(app)
        preferences = prefs
        _currentIcon.value = AppIcon.fromId(prefs.appIcon)
    }

    fun setIcon(icon: AppIcon) {
        val context = appContext ?: return
        val packageManager = context.packageManager
        val packageName = context.packageName

        val targetComponent = if (icon.aliasName != null) {
            ComponentName(packageName, icon.aliasName)
        } else {
            ComponentName(packageName, DEFAULT_ACTIVITY_NAME)
        }

        try {
            packageManager.setComponentEnabledSetting(
                targetComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            AppIcon.entries.forEach { entry ->
                if (entry != icon) {
                    val otherComponent = if (entry.aliasName != null) {
                        ComponentName(packageName, entry.aliasName)
                    } else {
                        ComponentName(packageName, DEFAULT_ACTIVITY_NAME)
                    }
                    packageManager.setComponentEnabledSetting(
                        otherComponent,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }

            _currentIcon.value = icon
            preferences?.appIcon = icon.id
        } catch (_: Exception) {
        }
    }
}
