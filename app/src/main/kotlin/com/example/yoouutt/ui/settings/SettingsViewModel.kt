package com.example.yoouutt.ui.settings

import androidx.lifecycle.ViewModel
import com.example.yoouutt.data.model.DefaultVideoQuality
import com.example.yoouutt.playback.PlaybackManager
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val playbackManager: PlaybackManager
) : ViewModel() {

    val defaultQuality: StateFlow<DefaultVideoQuality> = playbackManager.defaultQuality

    fun setDefaultQuality(quality: DefaultVideoQuality) {
        playbackManager.setDefaultQuality(quality)
    }
}
