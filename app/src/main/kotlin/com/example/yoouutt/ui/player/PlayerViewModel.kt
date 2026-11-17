package com.example.yoouutt.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.yoouutt.data.model.AudioTrackOption
import com.example.yoouutt.data.model.FeedItem
import com.example.yoouutt.data.model.PlaylistContext
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.VideoResolutionOption
import com.example.yoouutt.data.repository.VideoRepository
import com.example.yoouutt.data.repository.WatchHistoryRepository
import com.example.yoouutt.playback.ActivePlayback
import com.example.yoouutt.playback.AutoplayCountdown
import com.example.yoouutt.playback.PlaybackManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class SeekFeedback(
    val text: String,
    val isForward: Boolean
)

class PlayerViewModel(
    private val playbackManager: PlaybackManager,
    private val historyRepository: WatchHistoryRepository,
    private val videoRepository: VideoRepository
) : ViewModel() {

    val currentPlayback: StateFlow<ActivePlayback?> = playbackManager.currentPlayback
    val isLoading: StateFlow<Boolean> = playbackManager.isLoading
    val errorMessage: StateFlow<String?> = playbackManager.errorMessage
    val isPlaying: StateFlow<Boolean> = playbackManager.isPlaying
    val isBuffering: StateFlow<Boolean> = playbackManager.isBuffering
    val exoPlayer: ExoPlayer get() = playbackManager.exoPlayer
    val currentResolution: StateFlow<VideoResolutionOption?> = playbackManager.currentResolution
    val availableResolutions: StateFlow<List<VideoResolutionOption>> = playbackManager.availableResolutions
    val currentAudioTrack: StateFlow<AudioTrackOption?> = playbackManager.currentAudioTrack
    val availableAudioTracks: StateFlow<List<AudioTrackOption>> = playbackManager.availableAudioTracks
    val playlistContext: StateFlow<PlaylistContext?> = playbackManager.playlistContext
    val isAutoplayEnabled: StateFlow<Boolean> = playbackManager.isAutoplayEnabled
    val isLoopEnabled: StateFlow<Boolean> = playbackManager.isLoopEnabled
    val autoplayCountdown: StateFlow<AutoplayCountdown?> = playbackManager.autoplayCountdown
    val seekIntervalSeconds: StateFlow<Int> = playbackManager.seekIntervalSeconds
    val availableSeekIntervals = listOf(5, 10, 15, 20)

    fun playNextInPlaylist() {
        playbackManager.playNextInPlaylist()
    }

    fun playPreviousInPlaylist() {
        playbackManager.playPreviousInPlaylist()
    }

    fun playPlaylistIndex(index: Int) {
        val context = playlistContext.value ?: return
        playbackManager.playPlaylistVideo(context, index)
    }

    private val _isResolutionDialogVisible = MutableStateFlow(false)
    val isResolutionDialogVisible: StateFlow<Boolean> = _isResolutionDialogVisible.asStateFlow()

    private val _isAudioTrackDialogVisible = MutableStateFlow(false)
    val isAudioTrackDialogVisible: StateFlow<Boolean> = _isAudioTrackDialogVisible.asStateFlow()


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

    private val _isDescriptionExpanded = MutableStateFlow(false)
    val isDescriptionExpanded: StateFlow<Boolean> = _isDescriptionExpanded.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    private val _speedIndex = MutableStateFlow(2)
    val speedIndex: StateFlow<Int> = _speedIndex.asStateFlow()

    private val _isSettingsSheetVisible = MutableStateFlow(false)
    val isSettingsSheetVisible: StateFlow<Boolean> = _isSettingsSheetVisible.asStateFlow()

    private val _isSpeedDialogVisible = MutableStateFlow(false)
    val isSpeedDialogVisible: StateFlow<Boolean> = _isSpeedDialogVisible.asStateFlow()

    private val _isSeekIntervalDialogVisible = MutableStateFlow(false)
    val isSeekIntervalDialogVisible: StateFlow<Boolean> = _isSeekIntervalDialogVisible.asStateFlow()

    private val _seekFeedback = MutableStateFlow<SeekFeedback?>(null)
    val seekFeedback: StateFlow<SeekFeedback?> = _seekFeedback.asStateFlow()

    private val _recommendedVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    val recommendedVideos: StateFlow<List<VideoItem>> = _recommendedVideos.asStateFlow()

    private val _isLoadingRecommendations = MutableStateFlow(false)
    val isLoadingRecommendations: StateFlow<Boolean> = _isLoadingRecommendations.asStateFlow()

    private var recommendationJob: Job? = null
    private var currentLoadedRecommendationVideoId: String? = null
    private var controlsTimeoutJob: Job? = null
    private var seekFeedbackJob: Job? = null
    private var progressTrackingJob: Job? = null
    private var accumulatedSeekSeconds = 0
    private var currentSeekDirection: Boolean? = null

    init {
        startProgressTracking()
        observeCurrentPlaybackForRecommendations()
    }

    fun initVideo(videoUrl: String) {
    }

    private fun observeCurrentPlaybackForRecommendations() {
        viewModelScope.launch {
            playbackManager.currentPlayback.collect { playback ->
                if (playback != null) {
                    if (playback.streamData != null) {
                        val videoKey = playback.videoId.ifBlank { playback.videoUrl }
                        if (currentLoadedRecommendationVideoId != videoKey) {
                            currentLoadedRecommendationVideoId = videoKey
                            loadRecommendations(playback)
                        }
                    } else {
                        _isLoadingRecommendations.value = true
                    }
                } else {
                    currentLoadedRecommendationVideoId = null
                    recommendationJob?.cancel()
                    _recommendedVideos.value = emptyList()
                    _isLoadingRecommendations.value = false
                }
            }
        }
    }

    private fun loadRecommendations(playback: ActivePlayback) {
        recommendationJob?.cancel()
        recommendationJob = viewModelScope.launch {
            val streamData = playback.streamData
            val existingRelated = streamData?.relatedVideos.orEmpty().filter {
                it.id != playback.videoId && it.url != playback.videoUrl
            }

            if (existingRelated.size >= 4) {
                _recommendedVideos.value = existingRelated
                _isLoadingRecommendations.value = false
                return@launch
            }

            _isLoadingRecommendations.value = true
            val collected = existingRelated.toMutableList()

            val targetChannel = streamData?.uploaderUrl?.takeIf { it.isNotBlank() } ?: playback.uploaderName
            if (targetChannel.isNotBlank()) {
                val channelResult = videoRepository.getChannelDetails(targetChannel)
                channelResult.onSuccess { channelData ->
                    val channelVideos = channelData.videos.filter {
                        it.id != playback.videoId && it.url != playback.videoUrl && collected.none { c -> c.id == it.id }
                    }
                    collected.addAll(channelVideos)
                }
            }

            if (collected.size < 6) {
                val query = if (playback.uploaderName.isNotBlank()) {
                    playback.uploaderName
                } else {
                    playback.title.split(" ").filter { it.length > 2 }.take(3).joinToString(" ")
                }
                if (query.isNotBlank()) {
                    val searchResult = videoRepository.searchVideos(query)
                    searchResult.onSuccess { searchVideos ->
                        val similarVideos = searchVideos.filter {
                            it.id != playback.videoId && it.url != playback.videoUrl && collected.none { c -> c.id == it.id }
                        }
                        collected.addAll(similarVideos)
                    }
                }
            }

            if (collected.size < 6) {
                val watchedHistory = historyRepository.getHistory().firstOrNull().orEmpty()
                if (watchedHistory.isNotEmpty()) {
                    val feedResult = videoRepository.getPersonalizedFeed(watchedHistory)
                    feedResult.onSuccess { feedItems ->
                        val feedVideos = feedItems.filterIsInstance<FeedItem.Video>().map { it.video }.filter {
                            it.id != playback.videoId && it.url != playback.videoUrl && collected.none { c -> c.id == it.id }
                        }
                        collected.addAll(feedVideos)
                    }
                }
            }

            _recommendedVideos.value = collected.distinctBy { it.id }
            _isLoadingRecommendations.value = false
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
        cancelAutoplayCountdown()
        accumulatedSeekSeconds = 0
        currentSeekDirection = null
        seekFeedbackJob?.cancel()
        _seekFeedback.value = null
        if (exoPlayer.playbackState == Player.STATE_IDLE) {
            exoPlayer.prepare()
        }
        exoPlayer.seekTo(seekMs)
        exoPlayer.play()
        _currentPositionMs.value = seekMs
        resetControlsTimeout()
    }

    fun doubleTapSeek(isForward: Boolean) {
        cancelAutoplayCountdown()
        val interval = seekIntervalSeconds.value
        if (currentSeekDirection == isForward && seekFeedbackJob?.isActive == true) {
            accumulatedSeekSeconds += interval
        } else {
            accumulatedSeekSeconds = interval
            currentSeekDirection = isForward
        }

        val stepMs = interval * 1000L
        val player = playbackManager.exoPlayer
        val current = player.currentPosition
        val duration = if (player.duration > 0L) player.duration else Long.MAX_VALUE
        val target = if (isForward) {
            (current + stepMs).coerceAtMost(duration)
        } else {
            (current - stepMs).coerceAtLeast(0L)
        }

        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }
        player.seekTo(target)
        player.play()
        _currentPositionMs.value = target

        val feedback = SeekFeedback(
            text = if (isForward) "+$accumulatedSeekSeconds detik" else "-$accumulatedSeekSeconds detik",
            isForward = isForward
        )
        _seekFeedback.value = feedback
        seekFeedbackJob?.cancel()
        seekFeedbackJob = viewModelScope.launch {
            delay(800)
            _seekFeedback.value = null
            accumulatedSeekSeconds = 0
            currentSeekDirection = null
        }
    }

    fun handleTap(isForward: Boolean): Boolean {
        if (currentSeekDirection == isForward && seekFeedbackJob?.isActive == true) {
            doubleTapSeek(isForward)
            return true
        }
        return false
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

    fun showAudioTrackDialog() {
        _isAudioTrackDialogVisible.value = true
    }

    fun dismissAudioTrackDialog() {
        _isAudioTrackDialogVisible.value = false
    }

    fun selectAudioTrack(track: AudioTrackOption) {
        playbackManager.selectAudioTrack(track)
        _isAudioTrackDialogVisible.value = false
    }

    fun toggleAutoplay() {
        playbackManager.toggleAutoplay()
    }

    fun setAutoplay(enabled: Boolean) {
        playbackManager.setAutoplay(enabled)
    }

    fun toggleLoop() {
        playbackManager.toggleLoop()
    }

    fun setLoop(enabled: Boolean) {
        playbackManager.setLoop(enabled)
    }

    fun cancelAutoplayCountdown() {
        playbackManager.cancelAutoplayCountdown()
    }

    fun playNextImmediately() {
        playbackManager.playNextImmediately()
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

    fun selectSpeed(index: Int) {
        if (index in speeds.indices) {
            _speedIndex.value = index
            exoPlayer.setPlaybackSpeed(speeds[index])
        }
    }

    fun showSettingsSheet() {
        _isSettingsSheetVisible.value = true
    }

    fun dismissSettingsSheet() {
        _isSettingsSheetVisible.value = false
    }

    fun showSpeedDialog() {
        _isSpeedDialogVisible.value = true
    }

    fun dismissSpeedDialog() {
        _isSpeedDialogVisible.value = false
    }

    fun showSeekIntervalDialog() {
        _isSeekIntervalDialogVisible.value = true
    }

    fun dismissSeekIntervalDialog() {
        _isSeekIntervalDialogVisible.value = false
    }

    fun selectSeekInterval(seconds: Int) {
        playbackManager.setSeekIntervalSeconds(seconds)
        dismissSeekIntervalDialog()
    }

    fun toggleDescription() {
        _isDescriptionExpanded.value = !_isDescriptionExpanded.value
    }

    fun toggleSave() {
        _isSaved.value = !_isSaved.value
    }

    fun playRelatedVideo(url: String, title: String, uploader: String) {
        playbackManager.playVideo(url, title, uploader)
    }

    override fun onCleared() {
        super.onCleared()
        progressTrackingJob?.cancel()
        controlsTimeoutJob?.cancel()
        seekFeedbackJob?.cancel()
        recommendationJob?.cancel()
        currentLoadedRecommendationVideoId = null
        accumulatedSeekSeconds = 0
        currentSeekDirection = null
    }
}

