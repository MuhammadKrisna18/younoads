package com.example.yoouutt.playback

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.session.MediaSession
import com.example.yoouutt.MainActivity
import com.example.yoouutt.data.model.PlaylistContext
import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoResolutionOption
import com.example.yoouutt.data.model.WatchHistoryItem
import com.example.yoouutt.data.repository.NewPipeVideoRepository
import com.example.yoouutt.data.repository.VideoRepository
import com.example.yoouutt.data.repository.WatchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val videoRepository: VideoRepository = NewPipeVideoRepository()
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _currentPlayback = MutableStateFlow<ActivePlayback?>(null)
    val currentPlayback: StateFlow<ActivePlayback?> = _currentPlayback.asStateFlow()

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

    private val _playlistContext = MutableStateFlow<PlaylistContext?>(null)
    val playlistContext: StateFlow<PlaylistContext?> = _playlistContext.asStateFlow()

    val exoPlayer: ExoPlayer = run {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(20000)
            .setReadTimeoutMs(20000)

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15000,
                50000,
                1500,
                2500
            )
            .build()

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setSeekBackIncrementMs(5000L)
            .setSeekForwardIncrementMs(5000L)
            .build()
            .apply {
                playWhenReady = true
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
                            if (_playlistContext.value != null && playNextInPlaylist()) {
                                return
                            }
                            _isPlaying.value = false
                            _isBuffering.value = false
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        _isBuffering.value = false
                        _isPlaying.value = false
                        prepare()
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
            _playlistContext.value = null
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
                    val initialResolution = streamData.selectedResolution
                        ?: streamData.availableResolutions.firstOrNull()
                    _currentResolution.value = initialResolution
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

                    val resolvedTitle = streamData.title.ifBlank { title }
                    val resolvedUploader = streamData.uploaderName.ifBlank { uploaderName }
                    val artworkUri = if (streamData.videoId.isNotBlank()) {
                        Uri.parse("https://i.ytimg.com/vi/${streamData.videoId}/hqdefault.jpg")
                    } else {
                        null
                    }

                    val mediaMetadata = MediaMetadata.Builder()
                        .setTitle(resolvedTitle)
                        .setArtist(resolvedUploader)
                        .setArtworkUri(artworkUri)
                        .build()

                    val mediaSource = createMediaSource(initialResolution, mediaMetadata)
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

    private fun createMediaSource(
        resolution: VideoResolutionOption?,
        mediaMetadata: MediaMetadata
    ): MediaSource {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(20000)
            .setReadTimeoutMs(20000)

        val defaultFactory = DefaultMediaSourceFactory(context).setDataSourceFactory(httpDataSourceFactory)

        if (resolution == null) {
            val fallbackItem = MediaItem.Builder().setMediaMetadata(mediaMetadata).build()
            return defaultFactory.createMediaSource(fallbackItem)
        }

        return if (resolution.isHls) {
            val mediaItem = MediaItem.Builder()
                .setUri(resolution.videoUrl)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .setMediaMetadata(mediaMetadata)
                .build()
            defaultFactory.createMediaSource(mediaItem)
        } else if (resolution.isVideoOnly && !resolution.audioUrl.isNullOrBlank()) {
            val videoItem = MediaItem.Builder()
                .setUri(resolution.videoUrl)
                .setMediaMetadata(mediaMetadata)
                .build()
            val audioItem = MediaItem.Builder()
                .setUri(resolution.audioUrl)
                .build()
            val videoSource = defaultFactory.createMediaSource(videoItem)
            val audioSource = defaultFactory.createMediaSource(audioItem)
            MergingMediaSource(true, true, videoSource, audioSource)
        } else {
            val mediaItem = MediaItem.Builder()
                .setUri(resolution.videoUrl)
                .setMediaMetadata(mediaMetadata)
                .build()
            defaultFactory.createMediaSource(mediaItem)
        }
    }

    fun selectResolution(option: VideoResolutionOption) {
        val current = _currentPlayback.value ?: return
        val streamData = current.streamData ?: return
        if (_currentResolution.value == option) return

        val currentPos = exoPlayer.currentPosition
        val wasPlaying = exoPlayer.isPlaying
        _currentResolution.value = option

        val resolvedTitle = streamData.title.ifBlank { current.title }
        val resolvedUploader = streamData.uploaderName.ifBlank { current.uploaderName }
        val artworkUri = if (streamData.videoId.isNotBlank()) {
            Uri.parse("https://i.ytimg.com/vi/${streamData.videoId}/hqdefault.jpg")
        } else {
            null
        }

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(resolvedTitle)
            .setArtist(resolvedUploader)
            .setArtworkUri(artworkUri)
            .build()

        val source = createMediaSource(option, mediaMetadata)
        exoPlayer.setMediaSource(source)
        exoPlayer.prepare()
        if (currentPos > 0L) {
            exoPlayer.seekTo(currentPos)
        }
        if (wasPlaying) {
            exoPlayer.play()
        }
    }

    fun playPlaylistVideo(context: PlaylistContext, index: Int) {
        if (index !in context.videos.indices) return
        _playlistContext.value = context.copy(currentIndex = index)
        val targetVideo = context.videos[index]
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
        playVideo(targetVideo.url, targetVideo.title, targetVideo.uploaderName, clearPlaylist = false)
    }

    fun playNextInPlaylist(): Boolean {
        val currentContext = _playlistContext.value ?: return false
        val nextIndex = currentContext.currentIndex + 1
        if (nextIndex in currentContext.videos.indices) {
            playPlaylistVideo(currentContext, nextIndex)
            return true
        }
        return false
    }

    fun playPreviousInPlaylist(): Boolean {
        val currentContext = _playlistContext.value ?: return false
        val prevIndex = currentContext.currentIndex - 1
        if (prevIndex in currentContext.videos.indices) {
            playPlaylistVideo(currentContext, prevIndex)
            return true
        }
        return false
    }

    fun clearPlaylist() {
        _playlistContext.value = null
    }

    fun stop() {
        _playlistContext.value = null
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
        _isLoading.value = false
        _errorMessage.value = null
        _resumePositionMs.value = null
    }

    fun release() {
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
}

