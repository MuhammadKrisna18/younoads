package com.example.yoouutt.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.yoouutt.YoouuttApp
import com.example.yoouutt.data.download.VideoDownloadManager
import com.example.yoouutt.data.model.CustomPlaylist
import com.example.yoouutt.data.model.CustomPlaylistItem
import com.example.yoouutt.data.model.DownloadRequest
import com.example.yoouutt.data.model.DownloadStatus
import com.example.yoouutt.data.model.FeedItem
import com.example.yoouutt.data.model.PlaylistContext
import com.example.yoouutt.data.model.SavedVideo
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.VideoResolutionOption
import com.example.yoouutt.data.repository.CustomPlaylistRepository
import com.example.yoouutt.data.repository.SavedVideoRepository
import com.example.yoouutt.data.repository.VideoRepository
import com.example.yoouutt.data.repository.WatchHistoryRepository
import com.example.yoouutt.playback.ActivePlayback
import com.example.yoouutt.playback.PlaybackManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SeekFeedback(
    val seconds: Int,
    val isForward: Boolean
)

class PlayerViewModel(
    private val playbackManager: PlaybackManager,
    private val historyRepository: WatchHistoryRepository,
    private val videoRepository: VideoRepository,
    private val savedVideoRepository: SavedVideoRepository,
    private val customPlaylistRepository: CustomPlaylistRepository,
    private val downloadManager: VideoDownloadManager = YoouuttApp.instance.container.downloadManager
) : ViewModel() {

    val currentPlayback: StateFlow<ActivePlayback?> = playbackManager.currentPlayback
    val isLoading: StateFlow<Boolean> = playbackManager.isLoading
    val errorMessage: StateFlow<String?> = playbackManager.errorMessage
    val isPlaying: StateFlow<Boolean> = playbackManager.isPlaying
    val isBuffering: StateFlow<Boolean> = playbackManager.isBuffering
    val exoPlayer: ExoPlayer get() = playbackManager.exoPlayer
    val currentResolution: StateFlow<VideoResolutionOption?> = playbackManager.currentResolution
    val availableResolutions: StateFlow<List<VideoResolutionOption>> = playbackManager.availableResolutions
    val playlistContext: StateFlow<PlaylistContext?> = playbackManager.playlistContext
    val isAutoplayEnabled: StateFlow<Boolean> = playbackManager.isAutoplayEnabled
    val isLoopEnabled: StateFlow<Boolean> = playbackManager.isLoopEnabled
    val seekIntervalSeconds: StateFlow<Int> = playbackManager.seekIntervalSeconds
    val availableSeekIntervals = listOf(5, 10, 15, 20)

    val customPlaylists: StateFlow<List<CustomPlaylist>> = customPlaylistRepository
        .getCustomPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAddToPlaylistSheetVisible = MutableStateFlow(false)
    val isAddToPlaylistSheetVisible: StateFlow<Boolean> = _isAddToPlaylistSheetVisible.asStateFlow()

    private val _isCreatePlaylistDialogVisible = MutableStateFlow(false)
    val isCreatePlaylistDialogVisible: StateFlow<Boolean> = _isCreatePlaylistDialogVisible.asStateFlow()

    private val _currentVideoPlaylistIds = MutableStateFlow<Set<Long>>(emptySet())
    val currentVideoPlaylistIds: StateFlow<Set<Long>> = _currentVideoPlaylistIds.asStateFlow()

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

    private val _isDownloaded = MutableStateFlow(false)
    val isDownloaded: StateFlow<Boolean> = _isDownloaded.asStateFlow()

    private val _isDownloadDialogVisible = MutableStateFlow(false)
    val isDownloadDialogVisible: StateFlow<Boolean> = _isDownloadDialogVisible.asStateFlow()

    val downloadProgressMap: StateFlow<Map<String, Int>> = downloadManager.downloadProgressMap

    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    private val _speedIndex = MutableStateFlow(2)
    val speedIndex: StateFlow<Int> = _speedIndex.asStateFlow()

    private val _isFastForwarding = MutableStateFlow(false)
    val isFastForwarding: StateFlow<Boolean> = _isFastForwarding.asStateFlow()

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
        observeDownloads()
    }

    private fun observeDownloads() {
        viewModelScope.launch {
            downloadManager.getDownloads().collect { downloads ->
                val currentId = currentPlayback.value?.videoId.orEmpty()
                if (currentId.isNotBlank()) {
                    _isDownloaded.value = downloads.any { it.videoId == currentId && it.status == DownloadStatus.COMPLETED }
                }
            }
        }
    }

    private fun checkDownloadedStatus(videoId: String) {
        if (videoId.isBlank()) {
            _isDownloaded.value = false
            return
        }
        viewModelScope.launch {
            _isDownloaded.value = downloadManager.isDownloaded(videoId)
        }
    }

    fun showDownloadDialog() {
        _isDownloadDialogVisible.value = true
    }

    fun dismissDownloadDialog() {
        _isDownloadDialogVisible.value = false
    }

    fun startDownload(
        qualityLabel: String,
        downloadUrl: String,
        audioDownloadUrl: String? = null,
        isAudioOnly: Boolean
    ) {
        val playback = currentPlayback.value ?: return
        val id = playback.videoId.ifBlank { PlayerUtils.extractVideoId(playback.videoUrl) }
        val thumbUrl = if (id.isNotBlank()) "https://i.ytimg.com/vi/$id/hqdefault.jpg" else ""
        val request = DownloadRequest(
            videoId = id,
            videoUrl = playback.videoUrl,
            title = playback.title,
            uploader = playback.uploaderName,
            thumbnailUrl = thumbUrl,
            downloadUrl = downloadUrl,
            audioDownloadUrl = audioDownloadUrl,
            qualityLabel = qualityLabel,
            isAudioOnly = isAudioOnly,
            durationMs = _durationMs.value
        )
        downloadManager.startDownload(request)
        _isDownloadDialogVisible.value = false
    }

    fun initVideo(videoUrl: String) {
    }

    private fun observeCurrentPlaybackForRecommendations() {
        viewModelScope.launch {
            playbackManager.currentPlayback.collect { playback ->
                if (playback != null) {
                    val videoKey = playback.videoId.ifBlank { playback.videoUrl }
                    checkSavedStatus(videoKey)
                    checkDownloadedStatus(playback.videoId)
                    if (playback.videoUrl.startsWith("file:")) {
                        _isLoadingRecommendations.value = false
                        _recommendedVideos.value = emptyList()
                    } else if (playback.streamData != null) {
                        if (currentLoadedRecommendationVideoId != videoKey) {
                            currentLoadedRecommendationVideoId = videoKey
                            loadRecommendations(playback)
                        }
                    } else {
                        _isLoadingRecommendations.value = true
                    }
                } else {
                    _isSaved.value = false
                    _isDownloaded.value = false
                    currentLoadedRecommendationVideoId = null
                    recommendationJob?.cancel()
                    _recommendedVideos.value = emptyList()
                    playbackManager.setUpcomingRecommendations(emptyList())
                    _isLoadingRecommendations.value = false
                }
            }
        }
    }

    private fun extractTopicQuery(title: String, uploader: String): String {
        val stopWords = setOf(
            "dan", "yang", "di", "ke", "dari", "ini", "itu", "untuk", "pada", "dengan", "adalah",
            "buat", "full", "eps", "episode", "part", "official", "video", "the", "a", "an", "in",
            "on", "at", "to", "for", "of", "with", "by"
        )
        val cleanedWords = title.replace(Regex("[^a-zA-Z0-9\\s]"), " ")
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() && it.length > 2 && it.lowercase() !in stopWords }
        val topicKeywords = cleanedWords.take(4).joinToString(" ")
        return if (topicKeywords.isNotBlank()) {
            topicKeywords
        } else if (uploader.isNotBlank()) {
            uploader
        } else {
            title.take(30)
        }
    }

    private fun loadRecommendations(playback: ActivePlayback) {
        recommendationJob?.cancel()
        recommendationJob = viewModelScope.launch {
            _isLoadingRecommendations.value = true
            val streamData = playback.streamData
            val currentId = playback.videoId
            val currentUrl = playback.videoUrl

            val existingRelated = streamData?.relatedVideos.orEmpty().filter {
                it.id != currentId && it.url != currentUrl
            }

            val targetChannel = streamData?.uploaderUrl?.takeIf { it.isNotBlank() } ?: playback.uploaderName
            val topicQuery = extractTopicQuery(playback.title, playback.uploaderName)

            coroutineScope {
                val channelDeferred = async {
                    if (targetChannel.isNotBlank()) {
                        videoRepository.getChannelDetails(targetChannel)
                            .getOrNull()
                            ?.videos
                            .orEmpty()
                            .filter { it.id != currentId && it.url != currentUrl }
                            .take(3)
                    } else {
                        emptyList()
                    }
                }

                val similarDeferred = async {
                    if (topicQuery.isNotBlank()) {
                        videoRepository.searchVideos(topicQuery)
                            .getOrNull()
                            .orEmpty()
                            .filter { it.id != currentId && it.url != currentUrl }
                            .take(8)
                    } else {
                        emptyList()
                    }
                }

                val personalDeferred = async {
                    val watchedHistory = historyRepository.getHistory().firstOrNull().orEmpty()
                    if (watchedHistory.isNotEmpty()) {
                        videoRepository.getPersonalizedFeed(watchedHistory)
                            .getOrNull()
                            .orEmpty()
                            .filterIsInstance<FeedItem.Video>()
                            .map { it.video }
                            .filter { it.id != currentId && it.url != currentUrl }
                            .take(6)
                    } else {
                        emptyList()
                    }
                }

                val channelVideos = channelDeferred.await()
                val similarVideos = similarDeferred.await()
                val personalVideos = personalDeferred.await()

                val pool = mutableListOf<VideoItem>()
                pool.addAll(existingRelated.take(4))
                pool.addAll(similarVideos)
                pool.addAll(personalVideos)
                pool.addAll(channelVideos)

                val distinctCandidates = pool.distinctBy { it.id }
                val finalRecommendations = if (distinctCandidates.isNotEmpty()) {
                    distinctCandidates.shuffled()
                } else {
                    existingRelated
                }

                _recommendedVideos.value = finalRecommendations
                playbackManager.setUpcomingRecommendations(finalRecommendations)
                _isLoadingRecommendations.value = false
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
            seconds = accumulatedSeekSeconds,
            isForward = isForward
        )
        _seekFeedback.value = feedback
        seekFeedbackJob?.cancel()
        seekFeedbackJob = viewModelScope.launch {
            delay(1200)
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

    fun startFastForward() {
        if (!_isFastForwarding.value && exoPlayer.isPlaying) {
            _isFastForwarding.value = true
            exoPlayer.setPlaybackSpeed(2.0f)
        }
    }

    fun stopFastForward() {
        if (_isFastForwarding.value) {
            _isFastForwarding.value = false
            val normalSpeed = speeds.getOrElse(_speedIndex.value) { 1.0f }
            exoPlayer.setPlaybackSpeed(normalSpeed)
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

    private fun checkSavedStatus(videoId: String) {
        viewModelScope.launch {
            _isSaved.value = savedVideoRepository.isSaved(videoId)
        }
    }

    fun toggleSave() {
        val playback = playbackManager.currentPlayback.value ?: return
        val id = playback.videoId.ifBlank { PlayerUtils.extractVideoId(playback.videoUrl) }
        val thumbUrl = if (id.isNotBlank()) "https://i.ytimg.com/vi/$id/hqdefault.jpg" else ""
        viewModelScope.launch {
            if (_isSaved.value) {
                savedVideoRepository.removeSavedVideo(id)
                _isSaved.value = false
            } else {
                val video = SavedVideo(
                    videoId = id,
                    url = playback.videoUrl,
                    title = playback.title,
                    uploader = playback.uploaderName,
                    thumbnailUrl = thumbUrl,
                    savedAt = System.currentTimeMillis()
                )
                savedVideoRepository.saveVideo(video)
                _isSaved.value = true
            }
        }
    }

    fun showAddToPlaylistSheet() {
        val playback = playbackManager.currentPlayback.value ?: return
        val id = playback.videoId.ifBlank { PlayerUtils.extractVideoId(playback.videoUrl) }
        viewModelScope.launch {
            updateCurrentVideoPlaylists(id)
            _isAddToPlaylistSheetVisible.value = true
        }
    }

    fun dismissAddToPlaylistSheet() {
        _isAddToPlaylistSheetVisible.value = false
    }

    fun showCreatePlaylistDialog() {
        _isCreatePlaylistDialogVisible.value = true
    }

    fun dismissCreatePlaylistDialog() {
        _isCreatePlaylistDialogVisible.value = false
    }

    fun createPlaylistAndAddCurrentVideo(title: String, description: String = "") {
        val playback = playbackManager.currentPlayback.value ?: return
        val id = playback.videoId.ifBlank { PlayerUtils.extractVideoId(playback.videoUrl) }
        val thumbUrl = if (id.isNotBlank()) "https://i.ytimg.com/vi/$id/hqdefault.jpg" else ""
        val durFormatted = if (exoPlayer.duration > 0) PlayerUtils.formatTime(exoPlayer.duration) else ""
        viewModelScope.launch {
            val newPlaylistId = customPlaylistRepository.createPlaylist(title, description)
            val item = CustomPlaylistItem(
                playlistId = newPlaylistId,
                videoId = id,
                url = playback.videoUrl,
                title = playback.title,
                uploader = playback.uploaderName,
                thumbnailUrl = thumbUrl,
                duration = durFormatted
            )
            customPlaylistRepository.addVideoToPlaylist(newPlaylistId, item)
            updateCurrentVideoPlaylists(id)
            _isCreatePlaylistDialogVisible.value = false
        }
    }

    fun toggleVideoInPlaylist(playlistId: Long) {
        val playback = playbackManager.currentPlayback.value ?: return
        val id = playback.videoId.ifBlank { PlayerUtils.extractVideoId(playback.videoUrl) }
        val thumbUrl = if (id.isNotBlank()) "https://i.ytimg.com/vi/$id/hqdefault.jpg" else ""
        val durFormatted = if (exoPlayer.duration > 0) PlayerUtils.formatTime(exoPlayer.duration) else ""
        viewModelScope.launch {
            val isInPlaylist = customPlaylistRepository.isVideoInPlaylist(playlistId, id)
            if (isInPlaylist) {
                customPlaylistRepository.removeVideoFromPlaylist(playlistId, id)
            } else {
                val item = CustomPlaylistItem(
                    playlistId = playlistId,
                    videoId = id,
                    url = playback.videoUrl,
                    title = playback.title,
                    uploader = playback.uploaderName,
                    thumbnailUrl = thumbUrl,
                    duration = durFormatted
                )
                customPlaylistRepository.addVideoToPlaylist(playlistId, item)
            }
            updateCurrentVideoPlaylists(id)
        }
    }

    private suspend fun updateCurrentVideoPlaylists(videoId: String) {
        val playlists = customPlaylistRepository.getCustomPlaylists().firstOrNull().orEmpty()
        val containingIds = mutableSetOf<Long>()
        for (p in playlists) {
            if (customPlaylistRepository.isVideoInPlaylist(p.id, videoId)) {
                containingIds.add(p.id)
            }
        }
        _currentVideoPlaylistIds.value = containingIds
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
        playbackManager.setUpcomingRecommendations(emptyList())
        accumulatedSeekSeconds = 0
        currentSeekDirection = null
    }
}

