package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class NewPipeVideoRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : VideoRepository {

    override suspend fun getTrendingVideos(): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching {
            val kioskExtractor = ServiceList.YouTube.kioskList.defaultKioskExtractor
            kioskExtractor.fetchPage()
            kioskExtractor.initialPage.items
                .filterIsInstance<StreamInfoItem>()
                .map { it.toVideoItem() }
        }
    }

    override suspend fun searchVideos(query: String): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching {
            val searchExtractor = ServiceList.YouTube.getSearchExtractor(query)
            searchExtractor.fetchPage()
            searchExtractor.initialPage.items
                .filterIsInstance<StreamInfoItem>()
                .map { it.toVideoItem() }
        }
    }

    override suspend fun getVideoStreams(videoUrl: String): Result<StreamData> = withContext(ioDispatcher) {
        runCatching {
            val streamExtractor = ServiceList.YouTube.getStreamExtractor(videoUrl)
            streamExtractor.fetchPage()

            val hlsUrl = streamExtractor.hlsUrl
            val progressiveStream = streamExtractor.videoStreams
                .filter { !it.isVideoOnly() }
                .maxByOrNull { it.height }
                ?.content

            val (streamUrl, isHls) = when {
                !hlsUrl.isNullOrBlank() -> hlsUrl to true
                !progressiveStream.isNullOrBlank() -> progressiveStream to false
                streamExtractor.videoStreams.isNotEmpty() -> streamExtractor.videoStreams.first().content to false
                else -> throw IllegalStateException("No playable stream available")
            }

            StreamData(
                videoId = streamExtractor.id.orEmpty(),
                title = streamExtractor.name.orEmpty(),
                uploaderName = streamExtractor.uploaderName.orEmpty(),
                streamUrl = streamUrl,
                isHls = isHls
            )
        }
    }

    private fun StreamInfoItem.toVideoItem(): VideoItem {
        val durationFormatted = formatDuration(duration)
        val bestThumbnail = thumbnails.maxByOrNull { it.height }?.url.orEmpty()
        return VideoItem(
            id = url.substringAfter("v=").substringBefore("&"),
            title = name.orEmpty(),
            uploaderName = uploaderName.orEmpty(),
            durationText = durationFormatted,
            thumbnailUrl = bestThumbnail,
            url = url
        )
    }

    private fun formatDuration(durationSeconds: Long): String {
        if (durationSeconds <= 0) return ""
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}
