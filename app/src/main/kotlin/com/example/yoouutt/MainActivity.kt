package com.example.yoouutt

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.yoouutt.ui.language.LanguageManager
import com.example.yoouutt.ui.player.PipActionHelper
import com.example.yoouutt.ui.player.PipBroadcastReceiver
import com.example.yoouutt.ui.player.enterPipMode
import com.example.yoouutt.ui.player.updatePipAutoEnter
import com.example.yoouutt.ui.player.updatePipParams
import com.example.yoouutt.ui.theme.ThemeManager
import com.example.yoouutt.ui.theme.YoouuttTheme
import kotlinx.coroutines.launch

val LocalInPipMode = staticCompositionLocalOf { false }
val LocalActivity = staticCompositionLocalOf<ComponentActivity?> { null }

class MainActivity : ComponentActivity() {
    private val inPipModeState = mutableStateOf(false)
    private var pipReceiver: PipBroadcastReceiver? = null
    private var isDismissedToBackgroundAudio = false
    private var isPlayerActive = false
    private var currentSourceRectHint: Rect? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        registerPipReceiver()
        observePlaybackStateForPip()

        setContent {
            val currentTheme by ThemeManager.currentTheme.collectAsState()
            val currentLanguage by LanguageManager.currentLanguage.collectAsState()
            val baseContext = LocalContext.current
            val localizedContext = remember(currentLanguage) {
                LanguageManager.createLocalizedContext(baseContext, currentLanguage)
            }
            val localizedConfig = remember(currentLanguage) {
                localizedContext.resources.configuration
            }

            CompositionLocalProvider(
                LocalInPipMode provides inPipModeState.value,
                LocalActivity provides this,
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfig
            ) {
                YoouuttTheme(appTheme = currentTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainNavigation()
                    }
                }
            }
        }
    }

    fun setPlayerActive(active: Boolean, sourceRect: Rect? = null) {
        isPlayerActive = active
        currentSourceRectHint = sourceRect
        val isPlaying = YoouuttApp.instance.playbackManager.isPlaying.value
        updatePipAutoEnter(isPlaying = active && isPlaying, sourceRectHint = sourceRect)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (isPlayerActive && YoouuttApp.instance.playbackManager.isPlaying.value) {
            enterPipMode(isPlaying = true, sourceRectHint = currentSourceRectHint)
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        inPipModeState.value = isInPictureInPictureMode

        if (!isInPictureInPictureMode) {
            if (isFinishing && !isDismissedToBackgroundAudio) {
                YoouuttApp.instance.playbackManager.stop()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (inPipModeState.value && isFinishing && !isDismissedToBackgroundAudio) {
            YoouuttApp.instance.playbackManager.stop()
        }
    }

    override fun onDestroy() {
        if (inPipModeState.value && !isDismissedToBackgroundAudio) {
            YoouuttApp.instance.playbackManager.stop()
        }
        pipReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (_: Exception) {
            }
        }
        pipReceiver = null
        super.onDestroy()
    }

    private fun registerPipReceiver() {
        val receiver = PipBroadcastReceiver(
            onPlayPause = {
                val playbackManager = YoouuttApp.instance.playbackManager
                playbackManager.togglePlayPause()
                updatePipParams(
                    isPlaying = playbackManager.exoPlayer.isPlaying,
                    sourceRectHint = currentSourceRectHint,
                    autoEnterEnabled = isPlayerActive && playbackManager.exoPlayer.isPlaying
                )
            },
            onReplay = {
                YoouuttApp.instance.playbackManager.seekByOffset(-10)
            },
            onHeadset = {
                isDismissedToBackgroundAudio = true
                finish()
            }
        )
        pipReceiver = receiver
        val filter = IntentFilter(PipActionHelper.ACTION_PIP_CONTROL)
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    private fun observePlaybackStateForPip() {
        lifecycleScope.launch {
            YoouuttApp.instance.playbackManager.isPlaying.collect { isPlaying ->
                if (inPipModeState.value) {
                    updatePipParams(
                        isPlaying = isPlaying,
                        sourceRectHint = currentSourceRectHint,
                        autoEnterEnabled = isPlayerActive && isPlaying
                    )
                } else if (isPlayerActive) {
                    updatePipAutoEnter(
                        isPlaying = isPlaying,
                        sourceRectHint = currentSourceRectHint
                    )
                }
            }
        }
    }
}
