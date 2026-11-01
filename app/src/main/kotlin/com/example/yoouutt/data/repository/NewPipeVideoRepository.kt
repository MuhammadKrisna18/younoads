package com.example.yoouutt.data.repository

import androidx.core.text.HtmlCompat
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
import java.time.Instant
import java.time.OffsetDateTime
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
                val searchPage = searchExtractor.initialPage
                val searchItems = searchPage.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
                combinedList.addAll(searchItems)
            } catch (_: Exception) {
            }

            if (combinedList.isEmpty()) {
                val searchExtractor = ServiceList.YouTube.getSearchExtractor("indonesia")
                searchExtractor.fetchPage()
                val fallbackItems = searchExtractor.initialPage.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
                combinedList.addAll(fallbackItems)
            }

            combinedList.distinctBy { it.id }
        }
    }

    override suspend fun searchVideos(query: String): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching {
            val searchExtractor = ServiceList.YouTube.getSearchExtractor(query)
            searchExtractor.fetchPage()
            val initialPage = searchExtractor.initialPage
            initialPage.items
                .filterIsInstance<StreamInfoItem>()
                .map { it.toVideoItem() }
                .distinctBy { it.id }
        }
    }

    override suspend fun getVideoStreams(videoUrl: String): Result<StreamData> = withContext(ioDispatcher) {
        runCatching {
            val streamExtractor = ServiceList.YouTube.getStreamExtractor(videoUrl)
            streamExtractor.fetchPage()

            val hlsUrl = streamExtractor.hlsUrl
            val isLive = try {
                streamExtractor.streamType == org.schabi.newpipe.extractor.stream.StreamType.LIVE_STREAM
            } catch (_: Exception) {
                false
            }

            val progressiveStream = streamExtractor.videoStreams
                .filter { !it.isVideoOnly() }
                .maxByOrNull { it.height }
                ?.content

            val (streamUrl, isHls) = when {
                isLive && !hlsUrl.isNullOrBlank() -> hlsUrl to true
                !progressiveStream.isNullOrBlank() -> progressiveStream to false
                !hlsUrl.isNullOrBlank() -> hlsUrl to true
                streamExtractor.videoStreams.isNotEmpty() -> streamExtractor.videoStreams.first().content to false
                else -> throw IllegalStateException("No playable stream available")
            }

            val bestAvatar = streamExtractor.uploaderAvatars.maxByOrNull { it.height }?.url.orEmpty()
            val rawDescription = try { streamExtractor.description.content.orEmpty() } catch (_: Exception) { "" }
            val cleanDesc = cleanHtmlDescription(rawDescription)
            val viewCount = try { streamExtractor.viewCount } catch (_: Exception) { 0L }
            val viewText = formatViewCount(viewCount)
            val uploadMillis = try {
                streamExtractor.uploadDate?.instant?.toEpochMilli()
                    ?: streamExtractor.uploadDate?.offsetDateTime()?.toInstant()?.toEpochMilli()
            } catch (_: Exception) {
                null
            }
            val textualDate = try { streamExtractor.textualUploadDate.orEmpty() } catch (_: Exception) { "" }
            val formattedDate = formatUploadDate(uploadMillis, textualDate)

            val subscriberCount = try { streamExtractor.uploaderSubscriberCount } catch (_: Exception) { 0L }
            val subscriberText = if (subscriberCount > 0L) formatSubscriberCount(subscriberCount) else ""

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
                uploaderSubscriberCountText = subscriberText,
                isHls = isHls,
                description = cleanDesc,
                viewCountText = viewText,
                uploadDateText = formattedDate,
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
        val formattedDate = formatUploadDate(dateMillis, textualUploadDate.orEmpty())

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
            uploadDateText = formattedDate
        )
    }

    private fun cleanHtmlDescription(raw: String): String {
        if (raw.isBlank()) return ""
        return try {
            HtmlCompat.fromHtml(raw, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
        } catch (_: Exception) {
            raw.replace(Regex("<[^>]*>"), "").trim()
        }
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
            views >= 1_000_000_000 -> {
                val value = views / 1_000_000_000.0
                if (value >= 10.0) String.format(Locale.getDefault(), "%.0f M x", value)
                else String.format(Locale.getDefault(), "%.1f M x", value)
            }
            views >= 1_000_000 -> {
                val value = views / 1_000_000.0
                if (value >= 10.0) String.format(Locale.getDefault(), "%.0f jt x", value)
                else String.format(Locale.getDefault(), "%.1f jt x", value)
            }
            views >= 1_000 -> {
                val value = views / 1_000.0
                if (value >= 10.0) String.format(Locale.getDefault(), "%.0f rb x", value)
                else String.format(Locale.getDefault(), "%.1f rb x", value)
            }
            else -> "$views x"
        }
    }

    private fun formatSubscriberCount(subscribers: Long): String {
        if (subscribers <= 0) return ""
        return when {
            subscribers >= 1_000_000 -> {
                val value = subscribers / 1_000_000.0
                if (value >= 100.0) String.format(Locale.getDefault(), "%.0f jt subscriber", value)
                else String.format(Locale.getDefault(), "%.1f jt subscriber", value)
            }
            subscribers >= 1_000 -> {
                val value = subscribers / 1_000.0
                if (value >= 100.0) String.format(Locale.getDefault(), "%.0f rb subscriber", value)
                else String.format(Locale.getDefault(), "%.1f rb subscriber", value)
            }
            else -> "$subscribers subscriber"
        }
    }

    private fun formatUploadDate(uploadDateMillis: Long?, textualDate: String?): String {
        if (uploadDateMillis != null && uploadDateMillis > 0L) {
            val relative = formatRelativeTime(uploadDateMillis)
            if (relative.isNotBlank()) return relative
        }

        val rawText = textualDate.orEmpty().trim()
        if (rawText.isBlank()) return ""

        try {
            val offsetDateTime = OffsetDateTime.parse(rawText)
            return formatRelativeTime(offsetDateTime.toInstant().toEpochMilli())
        } catch (_: Exception) {
        }

        try {
            val instant = Instant.parse(rawText)
            return formatRelativeTime(instant.toEpochMilli())
        } catch (_: Exception) {
        }

        return translateEnglishRelativeDate(rawText)
    }

    private fun formatRelativeTime(millis: Long): String {
        if (millis <= 0L) return ""
        val diff = System.currentTimeMillis() - millis
        if (diff < 0L) return "Baru saja"

        val seconds = diff / 1000L
        val minutes = seconds / 60L
        val hours = minutes / 60L
        val days = hours / 24L
        val weeks = days / 7L
        val months = days / 30L
        val years = days / 365L

        return when {
            years > 0 -> "$years tahun lalu"
            months > 0 -> "$months bulan lalu"
            weeks > 0 -> "$weeks minggu lalu"
            days > 0 -> "$days hari lalu"
            hours > 0 -> "$hours jam lalu"
            minutes > 0 -> "$minutes menit lalu"
            else -> "Baru saja"
        }
    }

    private fun translateEnglishRelativeDate(text: String): String {
        var result = text
        val replacements = listOf(
            "years ago" to "tahun lalu",
            "year ago" to "tahun lalu",
            "months ago" to "bulan lalu",
            "month ago" to "bulan lalu",
            "weeks ago" to "minggu lalu",
            "week ago" to "minggu lalu",
            "days ago" to "hari lalu",
            "day ago" to "hari lalu",
            "hours ago" to "jam lalu",
            "hour ago" to "jam lalu",
            "minutes ago" to "menit lalu",
            "minute ago" to "menit lalu",
            "seconds ago" to "detik lalu",
            "second ago" to "detik lalu",
            "Streamed" to "Disiarkan",
            "streamed" to "disiarkan"
        )
        for ((eng, id) in replacements) {
            if (result.contains(eng, ignoreCase = true)) {
                result = result.replace(Regex("(?i)" + Regex.escape(eng)), id)
            }
        }
        return result.trim()
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
