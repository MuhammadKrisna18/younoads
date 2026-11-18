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
import com.example.yoouutt.R
import com.example.yoouutt.util.TextFormatters

import android.graphics.Rect

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return currentContext as? Activity
}

fun Activity.enterPipMode(
    isPlaying: Boolean = true,
    sourceRectHint: Rect? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val params = PipActionHelper.buildPipParams(
            context = this,
            isPlaying = isPlaying,
            sourceRectHint = sourceRectHint,
            autoEnterEnabled = false
        ) ?: return
        enterPictureInPictureMode(params)
    }
}

fun Activity.updatePipAutoEnter(
    isPlaying: Boolean,
    sourceRectHint: Rect? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val params = PipActionHelper.buildPipParams(
            context = this,
            isPlaying = isPlaying,
            sourceRectHint = sourceRectHint,
            autoEnterEnabled = isPlaying
        ) ?: return
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

fun Activity.setImmersiveMode(enabled: Boolean) {
    val window = this.window
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    if (enabled) {
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    } else {
        insetsController.show(WindowInsetsCompat.Type.systemBars())
    }
}

object PlayerUtils {
    fun formatTime(timeMs: Long): String = TextFormatters.formatTimeMs(timeMs)

    fun extractVideoId(urlOrId: String): String {
        val trimmed = urlOrId.trim()
        if (trimmed.isEmpty()) return ""

        if (trimmed.length == 11 && trimmed.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
            return trimmed
        }

        val vMatch = Regex("[?&]v=([a-zA-Z0-9_-]{11})").find(trimmed)
        if (vMatch != null) {
            return vMatch.groupValues[1]
        }

        val shortMatch = Regex("youtu\\.be/([a-zA-Z0-9_-]{11})").find(trimmed)
        if (shortMatch != null) {
            return shortMatch.groupValues[1]
        }

        val shortsMatch = Regex("shorts/([a-zA-Z0-9_-]{11})").find(trimmed)
        if (shortsMatch != null) {
            return shortsMatch.groupValues[1]
        }

        val embedMatch = Regex("embed/([a-zA-Z0-9_-]{11})").find(trimmed)
        if (embedMatch != null) {
            return embedMatch.groupValues[1]
        }

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

    fun copyToClipboard(context: Context, text: String, message: String? = null) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText("YounoAds", text)
            clipboard?.setPrimaryClip(clip)
            val toastMsg = message ?: context.getString(R.string.common_link_copied)
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, context.getString(R.string.common_error), Toast.LENGTH_SHORT).show()
        }
    }

    fun shareText(context: Context, text: String, title: String? = null) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val chooserTitle = title ?: context.getString(R.string.common_share_video)
            val shareIntent = Intent.createChooser(sendIntent, chooserTitle)
            context.startActivity(shareIntent)
        } catch (_: Exception) {
        }
    }
}
