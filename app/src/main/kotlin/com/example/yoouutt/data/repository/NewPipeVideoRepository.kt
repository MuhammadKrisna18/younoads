package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.ChannelData
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.Page
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabs
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.util.Locale

class NewPipeVideoRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : VideoRepository {

    private val trendingTopics = listOf(
        "trending indonesia",
        "podcast indonesia terbaru",
        "musik indonesia populer",
        "video viral indonesia",
        "stand up comedy indonesia",
        "gaming indonesia",
        "berita viral terkini",
        "gadget teknologi terbaru",
        "film pendek indonesia",
        "vlog indonesia viral"
    )
    private var topicIndex = 0

    private var channelTabExtractor: ChannelTabExtractor? = null
    private var channelNextPage: Page? = null

    override suspend fun getTrendingVideos(): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching {
            val combinedList = mutableListOf<VideoItem>()

            try {
                val kioskExtractor = ServiceList.YouTube.kioskList.defaultKioskExtractor
                kioskExtractor.fetchPage()
                val page1 = kioskExtractor.initialPage
                combinedList.addAll(page1.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() })
                if (page1.hasNextPage()) {
                    try {
                        val page2 = kioskExtractor.getPage(page1.nextPage)
                        combinedList.addAll(page2.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() })
                    } catch (_: Exception) {
                    }
                }
            } catch (_: Exception) {
            }

            try {
                val currentTopic = trendingTopics[topicIndex % trendingTopics.size]
                topicIndex++
                val searchExtractor = ServiceList.YouTube.getSearchExtractor(currentTopic)
                searchExtractor.fetchPage()
                val topicItems = searchExtractor.initialPage.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
                combinedList.addAll(topicItems)
            } catch (_: Exception) {
            }

            combinedList
                .distinctBy { it.id }
                .shuffled()
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
                videoId = streamExtractor.id,
                streamUrl = streamUrl,
                title = streamExtractor.name,
                uploaderName = streamExtractor.uploaderName,
                uploaderUrl = streamExtractor.uploaderUrl,
                uploaderAvatarUrl = bestAvatar,
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
                var currentPage = tabExtractor.initialPage
                val firstBatch = currentPage.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
                videoList.addAll(firstBatch)

                var batchCount = 1
                while (currentPage.hasNextPage() && batchCount < 4) {
                    try {
                        currentPage = tabExtractor.getPage(currentPage.nextPage)
                        val nextBatch = currentPage.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
                        videoList.addAll(nextBatch)
                        batchCount++
                    } catch (_: Exception) {
                        break
                    }
                }

                channelTabExtractor = tabExtractor
                channelNextPage = if (currentPage.hasNextPage()) currentPage.nextPage else null
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

    override suspend fun getMoreChannelVideos(): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching {
            val extractor = channelTabExtractor ?: return@runCatching emptyList()
            val nextPage = channelNextPage ?: return@runCatching emptyList()
            val page = extractor.getPage(nextPage)
            channelNextPage = if (page.hasNextPage()) page.nextPage else null
            page.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
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
            String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        }
    }

    private fun formatViewCount(views: Long): String {
        if (views <= 0) return ""
        return when {
            views >= 1_000_000_000 -> String.format(Locale.getDefault(), "%.1f Miliar x", views / 1_000_000_000.0)
            views >= 1_000_000 -> String.format(Locale.getDefault(), "%.1f Juta x", views / 1_000_000.0)
            views >= 1_000 -> String.format(Locale.getDefault(), "%.1f Ribu x", views / 1_000.0)
            else -> "$views x"
        }
    }

    private fun formatSubscriberCount(subscribers: Long): String {
        if (subscribers <= 0) return ""
        return when {
            subscribers >= 1_000_000 -> String.format(Locale.getDefault(), "%.1f jt subscriber", subscribers / 1_000_000.0)
            subscribers >= 1_000 -> String.format(Locale.getDefault(), "%.1f rb subscriber", subscribers / 1_000.0)
            else -> "$subscribers subscriber"
        }
    }

    private fun parseTextualDateToMillis(text: String): Long {
        if (text.isBlank()) return 0L
        val now = System.currentTimeMillis()
        val lower = text.lowercase()

        val number = lower.filter { it.isDigit() }.toLongOrNull() ?: 1L
        val oneHour = 3_600_000L
        val oneDay = 24 * oneHour

        return when {
            lower.contains("second") || lower.contains("detik") -> now - (number * 1000L)
            lower.contains("minute") || lower.contains("menit") -> now - (number * 60_000L)
            lower.contains("hour") || lower.contains("jam") -> now - (number * oneHour)
            lower.contains("day") || lower.contains("hari") -> now - (number * oneDay)
            lower.contains("week") || lower.contains("minggu") -> now - (number * 7 * oneDay)
            lower.contains("month") || lower.contains("bulan") -> now - (number * 30 * oneDay)
            lower.contains("year") || lower.contains("tahun") -> now - (number * 365 * oneDay)
            else -> now
        }
    }
}
