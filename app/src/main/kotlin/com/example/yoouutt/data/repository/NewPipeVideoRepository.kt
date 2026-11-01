package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.ChannelData
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabs
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.util.Locale

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

            val bestAvatar = streamExtractor.uploaderAvatars.maxByOrNull { it.height }?.url.orEmpty()
            val description = try { streamExtractor.description.content.orEmpty() } catch (_: Exception) { "" }
            val viewCount = try { streamExtractor.viewCount } catch (_: Exception) { 0L }
            val viewText = formatViewCount(viewCount)
            val dateText = try { streamExtractor.textualUploadDate.orEmpty() } catch (_: Exception) { "" }
            val relatedItems = try {
                streamExtractor.relatedStreams?.items
                    ?.filterIsInstance<StreamInfoItem>()
                    ?.map { it.toVideoItem() }
                    .orEmpty()
            } catch (_: Exception) {
                emptyList()
            }

            StreamData(
                videoId = streamExtractor.id.orEmpty(),
                title = streamExtractor.name.orEmpty(),
                uploaderName = streamExtractor.uploaderName.orEmpty(),
                uploaderUrl = streamExtractor.uploaderUrl.orEmpty(),
                uploaderAvatarUrl = bestAvatar,
                streamUrl = streamUrl,
                isHls = isHls,
                description = description,
                viewCountText = viewText,
                uploadDateText = dateText,
                relatedVideos = relatedItems
            )
        }
    }

    override suspend fun getChannelDetails(channelUrl: String): Result<ChannelData> = withContext(ioDispatcher) {
        runCatching {
            val resolvedUrl = if (channelUrl.startsWith("http://") || channelUrl.startsWith("https://")) {
                channelUrl
            } else if (channelUrl.startsWith("@")) {
                "https://www.youtube.com/$channelUrl"
            } else {
                "https://www.youtube.com/@${channelUrl.replace(" ", "")}"
            }

            val channelExtractor = ServiceList.YouTube.getChannelExtractor(resolvedUrl)
            channelExtractor.fetchPage()

            val name = channelExtractor.name.orEmpty()
            val avatar = channelExtractor.avatars.maxByOrNull { it.height }?.url.orEmpty()
            val banner = channelExtractor.banners.maxByOrNull { it.height }?.url.orEmpty()
            val subscribers = channelExtractor.subscriberCount
            val description = channelExtractor.description.orEmpty()
            val isVerified = try { channelExtractor.isVerified } catch (_: Exception) { false }

            val tabs = channelExtractor.tabs
            val videosTab = tabs.find {
                it.contentFilters.contains(ChannelTabs.VIDEOS) || it.url.contains("videos")
            } ?: tabs.firstOrNull()

            val videoList = mutableListOf<VideoItem>()
            if (videosTab != null) {
                val tabExtractor = ServiceList.YouTube.getChannelTabExtractor(videosTab)
                tabExtractor.fetchPage()
                val initialPage = tabExtractor.initialPage
                val pageItems = initialPage.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
                videoList.addAll(pageItems)

                if (initialPage.hasNextPage() && videoList.size < 60) {
                    try {
                        val nextPage = tabExtractor.getPage(initialPage.nextPage)
                        val moreItems = nextPage.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
                        videoList.addAll(moreItems)
                    } catch (_: Exception) {
                    }
                }
            }

            ChannelData(
                url = resolvedUrl,
                name = name,
                avatarUrl = avatar,
                bannerUrl = banner,
                subscriberCountText = formatSubscriberCount(subscribers),
                description = description,
                isVerified = isVerified,
                videos = videoList
            )
        }
    }

    private fun StreamInfoItem.toVideoItem(): VideoItem {
        val durationFormatted = formatDuration(duration)
        val bestThumbnail = thumbnails.maxByOrNull { it.height }?.url.orEmpty()
        val bestAvatar = uploaderAvatars.maxByOrNull { it.height }?.url.orEmpty()
        val dateMillis = uploadDate?.instant?.toEpochMilli()
            ?: uploadDate?.offsetDateTime()?.toInstant()?.toEpochMilli()
            ?: parseTextualDateToMillis(textualUploadDate.orEmpty())

        return VideoItem(
            id = url.substringAfter("v=").substringBefore("&"),
            title = name.orEmpty(),
            uploaderName = uploaderName.orEmpty(),
            uploaderUrl = uploaderUrl.orEmpty(),
            uploaderAvatarUrl = bestAvatar,
            durationText = durationFormatted,
            thumbnailUrl = bestThumbnail,
            url = url,
            viewCount = viewCount,
            viewCountText = formatViewCount(viewCount),
            uploadDateMillis = dateMillis,
            uploadDateText = textualUploadDate.orEmpty()
        )
    }

    private fun formatDuration(durationSeconds: Long): String {
        if (durationSeconds <= 0) return ""
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    private fun formatViewCount(views: Long): String {
        if (views <= 0) return ""
        return when {
            views >= 1_000_000_000 -> String.format(Locale.getDefault(), "%.1f Miliar", views / 1_000_000_000.0)
            views >= 1_000_000 -> String.format(Locale.getDefault(), "%.1f Juta", views / 1_000_000.0)
            views >= 1_000 -> String.format(Locale.getDefault(), "%.1f Rb", views / 1_000.0)
            else -> "$views"
        } + " x ditonton"
    }

    private fun formatSubscriberCount(count: Long): String {
        if (count <= 0) return ""
        return when {
            count >= 1_000_000_000 -> String.format(Locale.getDefault(), "%.2f M", count / 1_000_000_000.0)
            count >= 1_000_000 -> String.format(Locale.getDefault(), "%.1f jt", count / 1_000_000.0)
            count >= 1_000 -> String.format(Locale.getDefault(), "%.1f rb", count / 1_000.0)
            else -> "$count"
        } + " subscriber"
    }

    private fun parseTextualDateToMillis(text: String): Long {
        if (text.isBlank()) return 0L
        val lower = text.lowercase()
        val num = Regex("\\d+").find(lower)?.value?.toLongOrNull() ?: 1L
        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L
        return when {
            "year" in lower || "tahun" in lower -> now - (num * 365 * dayMillis)
            "month" in lower || "bulan" in lower -> now - (num * 30 * dayMillis)
            "week" in lower || "minggu" in lower -> now - (num * 7 * dayMillis)
            "day" in lower || "hari" in lower -> now - (num * dayMillis)
            "hour" in lower || "jam" in lower -> now - (num * 3600_000L)
            "minute" in lower || "menit" in lower -> now - (num * 60_000L)
            else -> now
        }
    }
}
