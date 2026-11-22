package com.example.yoouutt.playback

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.session.MediaSession
import com.example.yoouutt.MainActivity
import com.example.yoouutt.data.local.PlayerPreferences
import com.example.yoouutt.data.model.AudioTrackOption
import com.example.yoouutt.data.model.DefaultVideoQuality
import com.example.yoouutt.data.model.DownloadedVideo
import com.example.yoouutt.data.model.PlaylistContext
import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.VideoResolutionOption
import com.example.yoouutt.data.model.WatchHistoryItem
import com.example.yoouutt.data.repository.NewPipeVideoRepository
import com.example.yoouutt.data.repository.VideoRepository
import com.example.yoouutt.data.repository.WatchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ActivePlayback(
    val videoUrl: String,
    val title: String,
    val uploaderName: String,
    val videoId: String = "",
    val streamData: StreamData? = null
)

class PlaybackManager(
    private val context: Context,
    private val historyRepository: WatchHistoryRepository,
    private val videoRepository: VideoRepository = NewPipeVideoRepository(),
    private val mediaSourceBuilder: MediaSourceBuilder = MediaSourceBuilder(context),
    private val queueManager: PlaybackQueueManager = PlaybackQueueManager(),
    private val playerPreferences: PlayerPreferences = PlayerPreferences(context)
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _currentPlayback = MutableStateFlow<ActivePlayback?>(null)
    val currentPlayback: StateFlow<ActivePlayback?> = _currentPlayback.asStateFlow()

    private val _isAutoplayEnabled = MutableStateFlow(playerPreferences.isAutoplayEnabled)
    val isAutoplayEnabled: StateFlow<Boolean> = _isAutoplayEnabled.asStateFlow()

    private val _isLoopEnabled = MutableStateFlow(playerPreferences.isLoopEnabled)
    val isLoopEnabled: StateFlow<Boolean> = _isLoopEnabled.asStateFlow()

    private val _seekIntervalSeconds = MutableStateFlow(playerPreferences.seekIntervalSeconds)
    val seekIntervalSeconds: StateFlow<Int> = _seekIntervalSeconds.asStateFlow()

    private val _upcomingRecommendations = MutableStateFlow<List<VideoItem>>(emptyList())
    val upcomingRecommendations: StateFlow<List<VideoItem>> = _upcomingRecommendations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _resumePositionMs = MutableStateFlow<Long?>(null)
    val resumePositionMs: StateFlow<Long?> = _resumePositionMs.asStateFlow()

    private val _currentResolution = MutableStateFlow<VideoResolutionOption?>(null)
    val currentResolution: StateFlow<VideoResolutionOption?> = _currentResolution.asStateFlow()

    private val _availableResolutions = MutableStateFlow<List<VideoResolutionOption>>(emptyList())
    val availableResolutions: StateFlow<List<VideoResolutionOption>> = _availableResolutions.asStateFlow()

    private val _currentAudioTrack = MutableStateFlow<AudioTrackOption?>(null)
    val currentAudioTrack: StateFlow<AudioTrackOption?> = _currentAudioTrack.asStateFlow()

    private val _availableAudioTracks = MutableStateFlow<List<AudioTrackOption>>(emptyList())
    val availableAudioTracks: StateFlow<List<AudioTrackOption>> = _availableAudioTracks.asStateFlow()

    private val _defaultQuality = MutableStateFlow(DefaultVideoQuality.fromCode(playerPreferences.defaultQuality))
    val defaultQuality: StateFlow<DefaultVideoQuality> = _defaultQuality.asStateFlow()

    fun setDefaultQuality(quality: DefaultVideoQuality) {
        _defaultQuality.value = quality
        playerPreferences.defaultQuality = quality.code
    }

    private fun resolveInitialResolution(
        available: List<VideoResolutionOption>,
        defaultExtractorResolution: VideoResolutionOption?,
        preferredQuality: DefaultVideoQuality
    ): VideoResolutionOption? {
        if (available.isEmpty()) return defaultExtractorResolution
        if (preferredQuality == DefaultVideoQuality.AUTO) {
            return defaultExtractorResolution ?: available.firstOrNull()
        }
        val target = preferredQuality.targetHeight
        val exactMatch = available.find { it.height == target }
        if (exactMatch != null) return exactMatch

        val closestBelow = available.filter { it.height <= target }.maxByOrNull { it.height }
        if (closestBelow != null) return closestBelow

        val lowest = available.minByOrNull { it.height }
        return lowest ?: defaultExtractorResolution ?: available.firstOrNull()
    }

    val playlistContext: StateFlow<PlaylistContext?> = queueManager.playlistContext

    val exoPlayer: ExoPlayer = run {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15000,
                50000,
                600,
                1200
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceBuilder.defaultMediaSourceFactory)
            .setLoadControl(loadControl)
            .setSeekBackIncrementMs(5000L)
            .setSeekForwardIncrementMs(5000L)
            .build()
            .apply {
                playWhenReady = true
                repeatMode = if (playerPreferences.isLoopEnabled) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                setWakeMode(C.WAKE_MODE_NETWORK)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build()
                setAudioAttributes(audioAttributes, true)
                setHandleAudioBecomingNoisy(true)
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                        if (isPlaying) {
                            _isBuffering.value = false
                        }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        _isBuffering.value = (playbackState == Player.STATE_BUFFERING)
                        if (playbackState == Player.STATE_ENDED) {
                            if (_isLoopEnabled.value) {
                                exoPlayer.seekTo(0L)
                                exoPlayer.play()
                                return
                            }
                            if (!_isAutoplayEnabled.value) {
                                _isPlaying.value = false
                                _isBuffering.value = false
                                return
                            }
                            playNextImmediately()
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        _isBuffering.value = false
                        _isPlaying.value = false
                        _errorMessage.value = error.localizedMessage ?: "Gagal memutar media"
                    }
                })
            }
    }

    init {
        scope.launch {
            while (true) {
                delay(4000)
                if (exoPlayer.isPlaying) {
                    saveProgress()
                }
            }
        }
    }

    val mediaSession: MediaSession = run {
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        MediaSession.Builder(context, exoPlayer)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    fun playVideo(videoUrl: String, title: String, uploaderName: String, clearPlaylist: Boolean = true) {
        if (clearPlaylist) {
            queueManager.clear()
        }
        val current = _currentPlayback.value
        if (current?.videoUrl == videoUrl && exoPlayer.playbackState != Player.STATE_IDLE) {
            return
        }

        try {
            context.startService(Intent(context, PlaybackService::class.java))
        } catch (_: Exception) {
        }

        saveProgress()
        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        _currentPlayback.value = ActivePlayback(
            videoUrl = videoUrl,
            title = title,
            uploaderName = uploaderName
        )
        _isLoading.value = true
        _errorMessage.value = null
        _resumePositionMs.value = null

        scope.launch {
            videoRepository.getVideoStreams(videoUrl)
                .onSuccess { streamData ->
                    _currentPlayback.value = ActivePlayback(
                        videoUrl = videoUrl,
                        title = streamData.title.ifBlank { title },
                        uploaderName = streamData.uploaderName.ifBlank { uploaderName },
                        videoId = streamData.videoId,
                        streamData = streamData
                    )
                    _availableResolutions.value = streamData.availableResolutions
                    val initialResolution = resolveInitialResolution(
                        streamData.availableResolutions,
                        streamData.selectedResolution,
                        _defaultQuality.value
                    )
                    _currentResolution.value = initialResolution
                    _availableAudioTracks.value = streamData.availableAudioTracks
                    val initialAudioTrack = streamData.selectedAudioTrack
                        ?: streamData.availableAudioTracks.firstOrNull()
                    _currentAudioTrack.value = initialAudioTrack
                    _isLoading.value = false

                    val history = historyRepository.getWatchHistory(streamData.videoId)
                    val resumePos = if (history != null && history.lastPlaybackPositionMs > 5000L &&
                        (history.totalDurationMs <= 0L || history.lastPlaybackPositionMs < (history.totalDurationMs * 0.95))
                    ) {
                        history.lastPlaybackPositionMs
                    } else {
                        null
                    }
                    _resumePositionMs.value = resumePos

                    val metadata = buildMetadata(
                        title = streamData.title.ifBlank { title },
                        uploaderName = streamData.uploaderName.ifBlank { uploaderName },
                        videoId = streamData.videoId
                    )

                    val mediaSource = mediaSourceBuilder.buildMediaSource(initialResolution, initialAudioTrack, metadata)
                    exoPlayer.setMediaSource(mediaSource)
                    exoPlayer.prepare()
                    if (resumePos != null && resumePos > 0L) {
                        exoPlayer.seekTo(resumePos)
                    }
                    exoPlayer.play()
                }
                .onFailure { throwable ->
                    _isLoading.value = false
                    _errorMessage.value = throwable.localizedMessage ?: "Gagal memuat video"
                }
        }
    }

    fun playOfflineVideo(download: DownloadedVideo) {
        val localFile = java.io.File(download.filePath)
        if (!localFile.exists()) {
            _isLoading.value = false
            _errorMessage.value = "Berkas unduhan tidak ditemukan"
            return
        }

        try {
            context.startService(Intent(context, PlaybackService::class.java))
        } catch (_: Exception) {
        }

        saveProgress()
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        _isLoading.value = true
        _errorMessage.value = null
        queueManager.clear()
        _upcomingRecommendations.value = emptyList()

        val fileUri = Uri.fromFile(localFile)
        val metadata = buildMetadata(
            title = download.title,
            uploaderName = download.uploader,
            videoId = download.videoId
        )

        val offlineStreamData = StreamData(
            videoId = download.videoId,
            title = download.title,
            uploaderName = download.uploader,
            streamUrl = fileUri.toString(),
            isHls = false,
            availableResolutions = if (download.isAudioOnly) {
                emptyList()
            } else {
                listOf(
                    VideoResolutionOption(
                        label = download.qualityLabel,
                        height = download.qualityLabel.filter { it.isDigit() }.toIntOrNull() ?: 720,
                        videoUrl = fileUri.toString(),
                        isVideoOnly = false
                    )
                )
            },
            availableAudioTracks = if (download.isAudioOnly) {
                listOf(
                    AudioTrackOption(
                        id = "offline_audio",
                        displayName = download.qualityLabel,
                        audioUrl = fileUri.toString(),
                        isOriginal = true,
                        format = "M4A"
                    )
                )
            } else {
                emptyList()
            }
        )

        _currentPlayback.value = ActivePlayback(
            videoUrl = fileUri.toString(),
            title = download.title,
            uploaderName = download.uploader,
            videoId = download.videoId,
            streamData = offlineStreamData
        )

        _availableResolutions.value = offlineStreamData.availableResolutions
        _currentResolution.value = offlineStreamData.availableResolutions.firstOrNull()
        _availableAudioTracks.value = offlineStreamData.availableAudioTracks
        _currentAudioTrack.value = offlineStreamData.availableAudioTracks.firstOrNull()
        _isLoading.value = false

        val mediaSource = mediaSourceBuilder.buildOfflineMediaSource(fileUri, metadata)
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.play()
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
    }

    fun seekByOffset(offsetSeconds: Int) {
        val duration = exoPlayer.duration.coerceAtLeast(0L)
        val targetPosition = (exoPlayer.currentPosition + (offsetSeconds * 1000L)).coerceIn(0L, duration)
        exoPlayer.seekTo(targetPosition)
    }

    fun saveProgress() {
        val current = _currentPlayback.value ?: return
        val pos = exoPlayer.currentPosition
        val dur = exoPlayer.duration
        val videoId = current.videoId
        if (videoId.isBlank() || pos < 1000L) return

        val thumbnail = if (videoId.isNotBlank()) "https://i.ytimg.com/vi/$videoId/hqdefault.jpg" else ""
        scope.launch {
            val item = WatchHistoryItem(
                videoId = videoId,
                videoUrl = current.videoUrl,
                title = current.title,
                uploaderName = current.uploaderName,
                thumbnailUrl = thumbnail,
                lastPlaybackPositionMs = pos,
                totalDurationMs = dur,
                lastWatchedTimestamp = System.currentTimeMillis()
            )
            historyRepository.saveWatchHistory(item)
        }
    }

    fun selectResolution(option: VideoResolutionOption) {
        val current = _currentPlayback.value ?: return
        val streamData = current.streamData ?: return
        if (_currentResolution.value == option) return

        _currentResolution.value = option
        val metadata = buildMetadata(
            title = streamData.title.ifBlank { current.title },
            uploaderName = streamData.uploaderName.ifBlank { current.uploaderName },
            videoId = streamData.videoId
        )
        val source = mediaSourceBuilder.buildMediaSource(option, _currentAudioTrack.value, metadata)
        switchMediaSource(source)
    }

    fun selectAudioTrack(track: AudioTrackOption) {
        val current = _currentPlayback.value ?: return
        val streamData = current.streamData ?: return
        if (_currentAudioTrack.value == track) return

        _currentAudioTrack.value = track
        val currentRes = _currentResolution.value
        val updatedRes = currentRes?.copy(audioUrl = track.audioUrl) ?: currentRes
        _currentResolution.value = updatedRes

        val metadata = buildMetadata(
            title = streamData.title.ifBlank { current.title },
            uploaderName = streamData.uploaderName.ifBlank { current.uploaderName },
            videoId = streamData.videoId
        )
        val source = mediaSourceBuilder.buildMediaSource(updatedRes, track, metadata)
        switchMediaSource(source)
    }

    fun playPlaylistVideo(context: PlaylistContext, index: Int) {
        val targetVideo = queueManager.setPlaylist(context, index) ?: return
        recordWatchedPlaylist(context, targetVideo)
        playVideo(targetVideo.url, targetVideo.title, targetVideo.uploaderName, clearPlaylist = false)
    }

    fun playNextInPlaylist(): Boolean {
        val (updatedContext, targetVideo) = queueManager.getNextTarget() ?: return false
        recordWatchedPlaylist(updatedContext, targetVideo)
        playVideo(targetVideo.url, targetVideo.title, targetVideo.uploaderName, clearPlaylist = false)
        return true
    }

    fun playPreviousInPlaylist(): Boolean {
        val (updatedContext, targetVideo) = queueManager.getPreviousTarget() ?: return false
        recordWatchedPlaylist(updatedContext, targetVideo)
        playVideo(targetVideo.url, targetVideo.title, targetVideo.uploaderName, clearPlaylist = false)
        return true
    }

    fun clearPlaylist() {
        queueManager.clear()
    }

    fun stop() {
        queueManager.clear()
        _upcomingRecommendations.value = emptyList()
        saveProgress()
        try {
            context.stopService(Intent(context, PlaybackService::class.java))
        } catch (_: Exception) {
        }
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        _currentPlayback.value = null
        _currentResolution.value = null
        _availableResolutions.value = emptyList()
        _currentAudioTrack.value = null
        _availableAudioTracks.value = emptyList()
        _isLoading.value = false
        _errorMessage.value = null
        _resumePositionMs.value = null
    }

    fun release() {
        _upcomingRecommendations.value = emptyList()
        saveProgress()
        try {
            context.stopService(Intent(context, PlaybackService::class.java))
        } catch (_: Exception) {
        }
        try {
            mediaSession.release()
        } catch (_: Exception) {
        }
        exoPlayer.release()
    }

    fun setUpcomingRecommendations(recommendations: List<VideoItem>) {
        _upcomingRecommendations.value = recommendations
    }

    fun playNextImmediately(): Boolean {
        if (queueManager.playlistContext.value != null) {
            val playedPlaylistNext = playNextInPlaylist()
            if (playedPlaylistNext) return true
        }
        val nextVideo = queueManager.peekNextTarget()
            ?: _upcomingRecommendations.value.firstOrNull()
            ?: _currentPlayback.value?.streamData?.relatedVideos?.firstOrNull()
        if (nextVideo != null) {
            playVideo(nextVideo.url, nextVideo.title, nextVideo.uploaderName)
            return true
        }
        _isPlaying.value = false
        _isBuffering.value = false
        return false
    }

    fun toggleAutoplay() {
        val newValue = !_isAutoplayEnabled.value
        _isAutoplayEnabled.value = newValue
        playerPreferences.isAutoplayEnabled = newValue
        if (newValue && _isLoopEnabled.value) {
            _isLoopEnabled.value = false
            playerPreferences.isLoopEnabled = false
            exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    fun setAutoplay(enabled: Boolean) {
        _isAutoplayEnabled.value = enabled
        playerPreferences.isAutoplayEnabled = enabled
        if (enabled && _isLoopEnabled.value) {
            _isLoopEnabled.value = false
            playerPreferences.isLoopEnabled = false
            exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    fun toggleLoop() {
        val newValue = !_isLoopEnabled.value
        _isLoopEnabled.value = newValue
        playerPreferences.isLoopEnabled = newValue
        exoPlayer.repeatMode = if (newValue) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        if (newValue && _isAutoplayEnabled.value) {
            _isAutoplayEnabled.value = false
            playerPreferences.isAutoplayEnabled = false
        }
    }

    fun setLoop(enabled: Boolean) {
        _isLoopEnabled.value = enabled
        playerPreferences.isLoopEnabled = enabled
        exoPlayer.repeatMode = if (enabled) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        if (enabled && _isAutoplayEnabled.value) {
            _isAutoplayEnabled.value = false
            playerPreferences.isAutoplayEnabled = false
        }
    }

    fun setSeekIntervalSeconds(seconds: Int) {
        _seekIntervalSeconds.value = seconds
        playerPreferences.seekIntervalSeconds = seconds
    }

    private fun buildMetadata(title: String, uploaderName: String, videoId: String): MediaMetadata {
        val artworkUri = if (videoId.isNotBlank()) {
            Uri.parse("https://i.ytimg.com/vi/$videoId/hqdefault.jpg")
        } else {
            null
        }
        return MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(uploaderName)
            .setArtworkUri(artworkUri)
            .build()
    }

    private fun switchMediaSource(source: MediaSource) {
        val currentPos = exoPlayer.currentPosition
        val wasPlaying = exoPlayer.isPlaying
        exoPlayer.setMediaSource(source)
        exoPlayer.prepare()
        if (currentPos > 0L) {
            exoPlayer.seekTo(currentPos)
        }
        if (wasPlaying) {
            exoPlayer.play()
        }
    }

    private fun recordWatchedPlaylist(context: PlaylistContext, targetVideo: VideoItem) {
        scope.launch {
            val playlistItem = PlaylistItem(
                id = context.playlistId,
                url = context.playlistId,
                title = context.playlistTitle,
                thumbnailUrl = targetVideo.thumbnailUrl,
                uploaderName = targetVideo.uploaderName,
                streamCount = context.videos.size.toLong()
            )
            historyRepository.saveWatchedPlaylist(playlistItem)
        }
    }
}
