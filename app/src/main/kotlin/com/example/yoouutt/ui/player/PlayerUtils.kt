package com.example.yoouutt.ui.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Rational
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

fun Activity.enterPipMode() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
            .build()
        enterPictureInPictureMode(params)
    }
}

fun Activity.updatePipAutoEnter() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val params = PictureInPictureParams.Builder()
            .setAutoEnterEnabled(true)
            .setAspectRatio(Rational(16, 9))
            .build()
        setPictureInPictureParams(params)
    }
}

fun Activity.setFullscreen(enabled: Boolean) {
    val window = this.window
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    if (enabled) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    } else {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        insetsController.show(WindowInsetsCompat.Type.systemBars())
    }
}

object PlayerUtils {
    fun formatTime(timeMs: Long): String {
        val totalSeconds = (timeMs / 1000).coerceAtLeast(0)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun extractVideoId(urlOrId: String): String {
        val trimmed = urlOrId.trim()
        if (trimmed.isEmpty()) return ""

        // Already 11-char ID
        if (trimmed.length == 11 && trimmed.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
            return trimmed
        }

        // Standard watch URL: v=([a-zA-Z0-9_-]{11})
        val vMatch = Regex("[?&]v=([a-zA-Z0-9_-]{11})").find(trimmed)
        if (vMatch != null) {
            return vMatch.groupValues[1]
        }

        // youtu.be/([a-zA-Z0-9_-]{11})
        val shortMatch = Regex("youtu\\.be/([a-zA-Z0-9_-]{11})").find(trimmed)
        if (shortMatch != null) {
            return shortMatch.groupValues[1]
        }

        // shorts/([a-zA-Z0-9_-]{11})
        val shortsMatch = Regex("shorts/([a-zA-Z0-9_-]{11})").find(trimmed)
        if (shortsMatch != null) {
            return shortsMatch.groupValues[1]
        }

        // embed/([a-zA-Z0-9_-]{11})
        val embedMatch = Regex("embed/([a-zA-Z0-9_-]{11})").find(trimmed)
        if (embedMatch != null) {
            return embedMatch.groupValues[1]
        }

        // Fallback: match first 11-character alphanumeric token
        val genericMatch = Regex("([a-zA-Z0-9_-]{11})").find(trimmed)
        return genericMatch?.groupValues?.get(1) ?: trimmed
    }

    fun formatShortUrl(urlOrId: String, timestampSeconds: Long? = null): String {
        val videoId = extractVideoId(urlOrId)
        val baseUrl = if (videoId.isNotBlank() && videoId.length == 11) {
            "https://youtu.be/$videoId"
        } else if (urlOrId.startsWith("http://") || urlOrId.startsWith("https://")) {
            urlOrId
        } else {
            "https://youtu.be/$urlOrId"
        }

        return if (timestampSeconds != null && timestampSeconds > 0) {
            if (baseUrl.contains("?")) "$baseUrl&t=${timestampSeconds}s" else "$baseUrl?t=${timestampSeconds}s"
        } else {
            baseUrl
        }
    }

    fun copyToClipboard(context: Context, text: String, message: String = "Tautan berhasil disalin") {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText("YouTube Link", text)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, "Gagal menyalin tautan", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareText(context: Context, text: String, title: String = "Bagikan Video") {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, title)
            context.startActivity(shareIntent)
        } catch (_: Exception) {
        }
    }
}
