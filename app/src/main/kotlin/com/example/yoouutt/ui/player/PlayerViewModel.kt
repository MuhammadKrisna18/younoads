package com.example.yoouutt.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoResolutionOption
import com.example.yoouutt.data.repository.WatchHistoryRepository
import com.example.yoouutt.playback.ActivePlayback
import com.example.yoouutt.playback.PlaybackManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SeekFeedback(
    val text: String,
    val isForward: Boolean
)

class PlayerViewModel(
    private val playbackManager: PlaybackManager,
    private val historyRepository: WatchHistoryRepository
) : ViewModel() {

    val currentPlayback: StateFlow<ActivePlayback?> = playbackManager.currentPlayback
    val isLoading: StateFlow<Boolean> = playbackManager.isLoading
    val errorMessage: StateFlow<String?> = playbackManager.errorMessage
    val isPlaying: StateFlow<Boolean> = playbackManager.isPlaying
    val isBuffering: StateFlow<Boolean> = playbackManager.isBuffering
    val exoPlayer: ExoPlayer get() = playbackManager.exoPlayer
    val currentResolution: StateFlow<VideoResolutionOption?> = playbackManager.currentResolution
    val availableResolutions: StateFlow<List<VideoResolutionOption>> = playbackManager.availableResolutions

    private val _isResolutionDialogVisible = MutableStateFlow(false)
    val isResolutionDialogVisible: StateFlow<Boolean> = _isResolutionDialogVisible.asStateFlow()

    private val _isAmbientModeEnabled = MutableStateFlow(true)
    val isAmbientModeEnabled: StateFlow<Boolean> = _isAmbientModeEnabled.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _bufferedPositionMs = MutableStateFlow(0L)
    val bufferedPositionMs: StateFlow<Long> = _bufferedPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _isScrubbing = MutableStateFlow(false)
    val isScrubbing: StateFlow<Boolean> = _isScrubbing.asStateFlow()

    private val _scrubPositionMs = MutableStateFlow(0L)
    val scrubPositionMs: StateFlow<Long> = _scrubPositionMs.asStateFlow()

    private val _controlsVisible = MutableStateFlow(false)
    val controlsVisible: StateFlow<Boolean> = _controlsVisible.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    private val _resumeBannerVisible = MutableStateFlow(false)
    val resumeBannerVisible: StateFlow<Boolean> = _resumeBannerVisible.asStateFlow()

    private val _resumeSavedPosition = MutableStateFlow(0L)
    val resumeSavedPosition: StateFlow<Long> = _resumeSavedPosition.asStateFlow()

    private val _isDescriptionExpanded = MutableStateFlow(false)
    val isDescriptionExpanded: StateFlow<Boolean> = _isDescriptionExpanded.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _isSubscribed = MutableStateFlow(false)
    val isSubscribed: StateFlow<Boolean> = _isSubscribed.asStateFlow()

    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    private val _speedIndex = MutableStateFlow(2)
    val speedIndex: StateFlow<Int> = _speedIndex.asStateFlow()

    private val _seekFeedback = MutableStateFlow<SeekFeedback?>(null)
    val seekFeedback: StateFlow<SeekFeedback?> = _seekFeedback.asStateFlow()

    private var controlsTimeoutJob: Job? = null
    private var seekFeedbackJob: Job? = null
    private var progressTrackingJob: Job? = null

    init {
        startProgressTracking()
    }

    fun initVideo(videoUrl: String) {
        val videoId = videoUrl.substringAfter("v=").substringBefore("&")
        viewModelScope.launch {
            val item = historyRepository.getWatchHistory(videoId)
            val pos = item?.lastPlaybackPositionMs ?: 0L
            val dur = item?.totalDurationMs ?: 0L
            if (pos > 5000L && (dur <= 0L || pos < dur - 10000L)) {
                _resumeSavedPosition.value = pos
                _resumeBannerVisible.value = true
            }
        }
    }

    private fun startProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = viewModelScope.launch {
            while (true) {
                if (!_isScrubbing.value) {
                    val player = playbackManager.exoPlayer
                    _currentPositionMs.value = player.currentPosition.coerceAtLeast(0L)
                    _bufferedPositionMs.value = player.bufferedPosition.coerceAtLeast(0L)
                    _durationMs.value = if (player.duration > 0L) player.duration else 0L
                }
                delay(200)
            }
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_IDLE) {
                exoPlayer.prepare()
            } else if (exoPlayer.playbackState == Player.STATE_ENDED) {
                exoPlayer.seekTo(0L)
            }
            exoPlayer.play()
        }
        resetControlsTimeout()
    }

    fun seekTo(seekMs: Long) {
        if (exoPlayer.playbackState == Player.STATE_IDLE) {
            exoPlayer.prepare()
        }
        exoPlayer.seekTo(seekMs)
        exoPlayer.play()
        _currentPositionMs.value = seekMs
        resetControlsTimeout()
    }

    fun doubleTapSeek(isForward: Boolean) {
        val player = playbackManager.exoPlayer
        val current = player.currentPosition
        val duration = if (player.duration > 0L) player.duration else Long.MAX_VALUE
        val target = if (isForward) {
            (current + 10000L).coerceAtMost(duration)
        } else {
            (current - 10000L).coerceAtLeast(0L)
        }

        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }
        player.seekTo(target)
        player.play()
        _currentPositionMs.value = target

        val feedback = SeekFeedback(
            text = if (isForward) "+10 detik" else "-10 detik",
            isForward = isForward
        )
        _seekFeedback.value = feedback
        seekFeedbackJob?.cancel()
        seekFeedbackJob = viewModelScope.launch {
            delay(750)
            _seekFeedback.value = null
        }
    }

    fun onScrubbing(scrubbing: Boolean, targetMs: Long) {
        _isScrubbing.value = scrubbing
        _scrubPositionMs.value = targetMs
        if (scrubbing) {
            controlsTimeoutJob?.cancel()
        } else {
            resetControlsTimeout()
        }
    }

    fun showResolutionDialog() {
        _isResolutionDialogVisible.value = true
    }

    fun dismissResolutionDialog() {
        _isResolutionDialogVisible.value = false
    }

    fun selectResolution(option: VideoResolutionOption) {
        playbackManager.selectResolution(option)
    }

    fun toggleAmbientMode() {
        _isAmbientModeEnabled.value = !_isAmbientModeEnabled.value
    }

    fun toggleControls() {
        if (_controlsVisible.value) {
            _controlsVisible.value = false
            controlsTimeoutJob?.cancel()
        } else {
            _controlsVisible.value = true
            resetControlsTimeout()
        }
    }

    fun showControls() {
        _controlsVisible.value = true
        resetControlsTimeout()
    }

    fun hideControls() {
        _controlsVisible.value = false
        controlsTimeoutJob?.cancel()
    }

    fun resetControlsTimeout() {
        controlsTimeoutJob?.cancel()
        controlsTimeoutJob = viewModelScope.launch {
            delay(3500)
            _controlsVisible.value = false
        }
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun setFullscreen(value: Boolean) {
        _isFullscreen.value = value
    }

    fun cyclePlaybackSpeed() {
        val nextIndex = (_speedIndex.value + 1) % speeds.size
        _speedIndex.value = nextIndex
        exoPlayer.setPlaybackSpeed(speeds[nextIndex])
    }

    fun toggleDescription() {
        _isDescriptionExpanded.value = !_isDescriptionExpanded.value
    }

    fun toggleLike() {
        _isLiked.value = !_isLiked.value
    }

    fun toggleSave() {
        _isSaved.value = !_isSaved.value
    }

    fun toggleSubscribe() {
        _isSubscribed.value = !_isSubscribed.value
    }

    fun applyResume() {
        val pos = _resumeSavedPosition.value
        seekTo(pos)
        _resumeBannerVisible.value = false
    }

    fun dismissResume() {
        _resumeBannerVisible.value = false
    }

    fun playRelatedVideo(url: String, title: String, uploader: String) {
        playbackManager.playVideo(url, title, uploader)
    }

    override fun onCleared() {
        super.onCleared()
        progressTrackingJob?.cancel()
        controlsTimeoutJob?.cancel()
        seekFeedbackJob?.cancel()
    }
}

