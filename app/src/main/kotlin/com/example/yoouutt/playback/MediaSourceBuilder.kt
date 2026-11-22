package com.example.yoouutt.playback

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.yoouutt.data.model.AudioTrackOption
import com.example.yoouutt.data.model.VideoResolutionOption

@OptIn(UnstableApi::class)
class MediaSourceBuilder(
    context: Context
) {
    private val httpDataSourceFactory = ResilientHttpDataSourceFactory()
    private val defaultDataSourceFactory: DataSource.Factory =
        DefaultDataSource.Factory(context, httpDataSourceFactory)
    private val cachedDataSourceFactory: DataSource.Factory =
        MediaCacheManager.createCacheDataSourceFactory(context, defaultDataSourceFactory)

    val defaultMediaSourceFactory: DefaultMediaSourceFactory =
        DefaultMediaSourceFactory(context).setDataSourceFactory(cachedDataSourceFactory)

    fun buildOfflineMediaSource(
        fileUri: Uri,
        mediaMetadata: MediaMetadata
    ): MediaSource {
        val mediaItem = MediaItem.Builder()
            .setUri(fileUri)
            .setMediaMetadata(mediaMetadata)
            .build()
        val fileDataSourceFactory = FileDataSource.Factory()
        return ProgressiveMediaSource.Factory(fileDataSourceFactory)
            .createMediaSource(mediaItem)
    }

    fun buildMediaSource(
        resolution: VideoResolutionOption?,
        audioTrack: AudioTrackOption?,
        mediaMetadata: MediaMetadata
    ): MediaSource {
        if (resolution == null) {
            val fallbackItem = MediaItem.Builder().setMediaMetadata(mediaMetadata).build()
            return defaultMediaSourceFactory.createMediaSource(fallbackItem)
        }

        val activeAudioUrl = audioTrack?.audioUrl ?: resolution.audioUrl

        return if (resolution.isHls) {
            val mediaItem = MediaItem.Builder()
                .setUri(resolution.videoUrl)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .setMediaMetadata(mediaMetadata)
                .build()
            defaultMediaSourceFactory.createMediaSource(mediaItem)
        } else if (!activeAudioUrl.isNullOrBlank() && (resolution.isVideoOnly || audioTrack != null)) {
            val videoItem = MediaItem.Builder()
                .setUri(resolution.videoUrl)
                .setMediaMetadata(mediaMetadata)
                .build()
            val audioItem = MediaItem.Builder()
                .setUri(activeAudioUrl)
                .build()
            val videoSource = defaultMediaSourceFactory.createMediaSource(videoItem)
            val audioSource = defaultMediaSourceFactory.createMediaSource(audioItem)
            MergingMediaSource(true, true, videoSource, audioSource)
        } else {
            val mediaItem = MediaItem.Builder()
                .setUri(resolution.videoUrl)
                .setMediaMetadata(mediaMetadata)
                .build()
            defaultMediaSourceFactory.createMediaSource(mediaItem)
        }
    }
}
