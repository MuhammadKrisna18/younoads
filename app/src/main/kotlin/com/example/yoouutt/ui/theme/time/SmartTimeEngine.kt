package com.example.yoouutt.ui.theme.time

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.yoouutt.ui.theme.AppTheme
import com.example.yoouutt.ui.theme.ThemeCategory
import java.util.Calendar

enum class TimePhase {
    MORNING,
    AFTERNOON,
    SUNSET,
    NIGHT,
    MIDNIGHT
}

object SmartTimeEngine {

    fun getCurrentPhase(calendar: Calendar = Calendar.getInstance()): TimePhase {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val totalMinutes = hour * 60 + minute
        return when {
            totalMinutes in 300 until 660 -> TimePhase.MORNING
            totalMinutes in 660 until 960 -> TimePhase.AFTERNOON
            totalMinutes in 960 until 1110 -> TimePhase.SUNSET
            totalMinutes in 1110 until 1380 -> TimePhase.NIGHT
            else -> TimePhase.MIDNIGHT
        }
    }

    fun resolveTheme(theme: AppTheme, phase: TimePhase = getCurrentPhase()): AppTheme {
        return when (theme) {
            AppTheme.TIME_YOUNO_CYCLE -> when (phase) {
                TimePhase.MORNING -> AppTheme.SAKURA_LOFI
                TimePhase.AFTERNOON -> AppTheme.MATCHA_ZEN
                TimePhase.SUNSET -> AppTheme.CITY_POP_SUNSET
                TimePhase.NIGHT -> AppTheme.CYBERPUNK
                TimePhase.MIDNIGHT -> AppTheme.OCEAN_ABYSS
            }
            AppTheme.TIME_CIRCADIAN -> when (phase) {
                TimePhase.MORNING -> AppTheme.LIGHT
                TimePhase.AFTERNOON -> AppTheme.KONTRAS_TINGGI
                TimePhase.SUNSET -> AppTheme.SEPIA_MALAM
                TimePhase.NIGHT -> AppTheme.MATTE_BLACK
                TimePhase.MIDNIGHT -> AppTheme.AMOLED_REDUP
            }
            AppTheme.TIME_NATURE -> when (phase) {
                TimePhase.MORNING -> AppTheme.FUYU_YUKI
                TimePhase.AFTERNOON -> AppTheme.MATCHA_ZEN
                TimePhase.SUNSET -> AppTheme.MOMIJI_AUTUMN
                TimePhase.NIGHT -> AppTheme.SUMIE_INK
                TimePhase.MIDNIGHT -> AppTheme.OCEAN_ABYSS
            }
            AppTheme.TIME_RETRO -> when (phase) {
                TimePhase.MORNING -> AppTheme.RETRO_PIXEL
                TimePhase.AFTERNOON -> AppTheme.RETRO_PIXEL
                TimePhase.SUNSET -> AppTheme.VAPORWAVE
                TimePhase.NIGHT -> AppTheme.DRACULA
                TimePhase.MIDNIGHT -> AppTheme.TERMINAL_HACKER
            }
            AppTheme.TIME_DYNAMIC -> when (phase) {
                TimePhase.MORNING -> AppTheme.DYNAMIC_LIGHT
                TimePhase.AFTERNOON -> AppTheme.DYNAMIC_LIGHT
                TimePhase.SUNSET -> AppTheme.DYNAMIC_FROSTED
                TimePhase.NIGHT -> AppTheme.DYNAMIC_DARK
                TimePhase.MIDNIGHT -> AppTheme.DYNAMIC_AMOLED
            }
            else -> theme
        }
    }

    @Composable
    fun rememberEffectiveTheme(appTheme: AppTheme): AppTheme {
        if (appTheme.category != ThemeCategory.TIME_ADAPTIVE) {
            return appTheme
        }
        val context = LocalContext.current
        var phase by remember { mutableStateOf(getCurrentPhase()) }
        DisposableEffect(context) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context?, intent: Intent?) {
                    phase = getCurrentPhase()
                }
            }
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }
            context.registerReceiver(receiver, filter)
            onDispose {
                runCatching { context.unregisterReceiver(receiver) }
            }
        }
        return remember(appTheme, phase) {
            resolveTheme(appTheme, phase)
        }
    }
}
