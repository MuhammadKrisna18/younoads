package com.example.yoouutt.data.repository

import android.util.LruCache
import com.example.yoouutt.data.model.ChannelData
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.VideoResolutionOption
import com.example.yoouutt.data.model.WatchHistoryItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.Page
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabs
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import com.example.yoouutt.util.TextFormatters

class NewPipeVideoRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : VideoRepository {

    private data class ChannelPagination(
        val extractor: ChannelTabExtractor,
        var nextPage: Page?
    )

    private val streamCache = LruCache<String, Pair<StreamData, Long>>(25)
    private val channelCache = LruCache<String, Pair<ChannelData, Long>>(10)
    private val channelPaginationMap = mutableMapOf<String, ChannelPagination>()
    private var activeChannelUrl: String? = null

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
        val cached = streamCache.get(videoUrl)
        if (cached != null && (System.currentTimeMillis() - cached.second) < 15 * 60 * 1000L) {
            return@withContext Result.success(cached.first)
        }

        runCatching {
            val streamExtractor = ServiceList.YouTube.getStreamExtractor(videoUrl)
            streamExtractor.fetchPage()

            val hlsUrl = streamExtractor.hlsUrl
            val isLive = try {
                streamExtractor.streamType == org.schabi.newpipe.extractor.stream.StreamType.LIVE_STREAM
            } catch (_: Exception) {
                false
            }

            val audioStreams = try { streamExtractor.audioStreams } catch (_: Exception) { emptyList() }
            val bestAudioStream = audioStreams
                .filter { !it.content.isNullOrBlank() }
                .maxWithOrNull(
                    compareBy<org.schabi.newpipe.extractor.stream.AudioStream> { it.format == org.schabi.newpipe.extractor.MediaFormat.M4A }
                        .thenBy { it.averageBitrate.takeIf { b -> b > 0 } ?: it.bitrate }
                )
            val bestAudioUrl = bestAudioStream?.content

            val videoStreams = try { streamExtractor.videoStreams } catch (_: Exception) { emptyList() }
            val videoOnlyStreams = try { streamExtractor.videoOnlyStreams } catch (_: Exception) { emptyList() }

            val progressiveOptions = videoStreams
                .filter { !it.isVideoOnly() && !it.content.isNullOrBlank() }
                .map {
                    VideoResolutionOption(
                        label = it.getResolution().takeIf { res -> res.isNotBlank() } ?: "${it.height}p",
                        height = it.height,
                        videoUrl = it.content,
                        isVideoOnly = false,
                        audioUrl = null,
                        isHls = false,
                        format = it.format?.name.orEmpty()
                    )
                }

            val videoOnlyOptions = if (!bestAudioUrl.isNullOrBlank()) {
                videoOnlyStreams
                    .filter { !it.content.isNullOrBlank() }
                    .map {
                        VideoResolutionOption(
                            label = it.getResolution().takeIf { res -> res.isNotBlank() } ?: "${it.height}p",
                            height = it.height,
                            videoUrl = it.content,
                            isVideoOnly = true,
                            audioUrl = bestAudioUrl,
                            isHls = false,
                            format = it.format?.name.orEmpty()
                        )
                    }
            } else {
                emptyList()
            }

            val availableResolutions = if (isLive && !hlsUrl.isNullOrBlank()) {
                listOf(
                    VideoResolutionOption(
                        label = "Live",
                        height = 720,
                        videoUrl = hlsUrl,
                        isVideoOnly = false,
                        audioUrl = null,
                        isHls = true
                    )
                )
            } else {
                (progressiveOptions + videoOnlyOptions)
                    .groupBy { it.height }
                    .mapNotNull { (_, options) ->
                        options.maxWithOrNull(
                            compareBy<VideoResolutionOption> { !it.isVideoOnly }
                                .thenBy { it.format.equals("MPEG_4", ignoreCase = true) }
                        )
                    }
                    .sortedByDescending { it.height }
            }

            val fallbackStreamUrl = when {
                isLive && !hlsUrl.isNullOrBlank() -> hlsUrl
                progressiveOptions.isNotEmpty() -> progressiveOptions.first().videoUrl
                !hlsUrl.isNullOrBlank() -> hlsUrl
                videoStreams.isNotEmpty() -> videoStreams.first().content
                videoOnlyStreams.isNotEmpty() -> videoOnlyStreams.first().content
                else -> throw IllegalStateException("No playable stream available")
            }

            val selectedResolution = availableResolutions.firstOrNull { it.height == 720 }
                ?: availableResolutions.firstOrNull { it.height == 1080 }
                ?: availableResolutions.firstOrNull()
                ?: VideoResolutionOption(
                    label = if (isLive) "Live" else "Auto",
                    height = 720,
                    videoUrl = fallbackStreamUrl,
                    isVideoOnly = false,
                    audioUrl = null,
                    isHls = isLive && !hlsUrl.isNullOrBlank()
                )

            val streamUrl = selectedResolution.videoUrl
            val isHls = selectedResolution.isHls

            val bestAvatar = streamExtractor.uploaderAvatars.maxByOrNull { it.height }?.url.orEmpty()
            val rawDescription = try { streamExtractor.description.content.orEmpty() } catch (_: Exception) { "" }
            val cleanDesc = TextFormatters.cleanHtmlDescription(rawDescription)
            val viewCount = try { streamExtractor.viewCount } catch (_: Exception) { 0L }
            val viewText = TextFormatters.formatViewCount(viewCount)
            val uploadMillis = try {
                streamExtractor.uploadDate?.instant?.toEpochMilli()
                    ?: streamExtractor.uploadDate?.offsetDateTime()?.toInstant()?.toEpochMilli()
            } catch (_: Exception) {
                null
            }
            val textualDate = try { streamExtractor.textualUploadDate.orEmpty() } catch (_: Exception) { "" }
            val formattedDate = TextFormatters.formatUploadDate(uploadMillis, textualDate)

            val subscriberCount = try { streamExtractor.uploaderSubscriberCount } catch (_: Exception) { 0L }
            val subscriberText = if (subscriberCount > 0L) TextFormatters.formatSubscriberCount(subscriberCount) else ""

            val relatedItems = try {
                streamExtractor.relatedStreams?.items
                    ?.filterIsInstance<StreamInfoItem>()
                    ?.map { it.toVideoItem() }
                    .orEmpty()
            } catch (_: Exception) {
                emptyList()
            }

            val streamData = StreamData(
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
                relatedVideos = relatedItems,
                availableResolutions = availableResolutions,
                selectedResolution = selectedResolution
            )
            streamCache.put(videoUrl, Pair(streamData, System.currentTimeMillis()))
            streamData
        }
    }

    override suspend fun getChannelDetails(channelUrl: String): Result<ChannelData> = withContext(ioDispatcher) {
        val resolvedUrl = if (channelUrl.startsWith("http://") || channelUrl.startsWith("https://")) {
            channelUrl
        } else if (channelUrl.startsWith("@")) {
            "https://www.youtube.com/$channelUrl"
        } else {
            "https://www.youtube.com/@${channelUrl.replace(" ", "")}"
        }
        activeChannelUrl = resolvedUrl

        val cached = channelCache.get(resolvedUrl)
        if (cached != null && (System.currentTimeMillis() - cached.second) < 10 * 60 * 1000L) {
            return@withContext Result.success(cached.first)
        }

        runCatching {
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

                channelPaginationMap[resolvedUrl] = ChannelPagination(
                    extractor = tabExtractor,
                    nextPage = if (currentPage.hasNextPage()) currentPage.nextPage else null
                )
            }

            val channelData = ChannelData(
                url = resolvedUrl,
                name = name,
                avatarUrl = avatar,
                bannerUrl = banner,
                subscriberCountText = TextFormatters.formatSubscriberCount(subscribers),
                description = description,
                isVerified = isVerified,
                videos = videoList
            )
            channelCache.put(resolvedUrl, Pair(channelData, System.currentTimeMillis()))
            channelData
        }
    }

    override suspend fun getMoreChannelVideos(): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching {
            val activeUrl = activeChannelUrl ?: return@runCatching emptyList()
            val pagination = channelPaginationMap[activeUrl] ?: return@runCatching emptyList()
            val nextPage = pagination.nextPage ?: return@runCatching emptyList()
            val page = pagination.extractor.getPage(nextPage)
            pagination.nextPage = if (page.hasNextPage()) page.nextPage else null
            page.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
        }
    }

    override suspend fun getSearchSuggestions(query: String): Result<List<String>> = withContext(ioDispatcher) {
        runCatching {
            val trimmed = query.trim()
            if (trimmed.isBlank()) {
                emptyList()
            } else {
                ServiceList.YouTube.suggestionExtractor.suggestionList(trimmed).take(10)
            }
        }
    }

    override suspend fun getPersonalizedFeed(watchedVideos: List<WatchHistoryItem>): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching {
            if (watchedVideos.isEmpty()) {
                return@runCatching getTrendingVideos().getOrThrow()
            }

            coroutineScope {
                val watchedChannels = watchedVideos
                    .map { it.uploaderName }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .take(4)

                val recentVideoUrls = watchedVideos
                    .map { it.videoUrl }
                    .filter { it.isNotBlank() }
                    .take(3)

                val relatedCandidates = mutableListOf<VideoItem>()
                for (url in recentVideoUrls) {
                    val cached = streamCache.get(url)?.first?.relatedVideos
                    if (!cached.isNullOrEmpty()) {
                        relatedCandidates.addAll(cached.take(4))
                    }
                }

                val channelDeferreds = watchedChannels.map { channel ->
                    async {
                        searchVideos(channel).getOrNull().orEmpty().take(4)
                    }
                }

                val trendingDeferred = async {
                    getTrendingVideos().getOrNull().orEmpty()
                }

                val channelVideos = channelDeferreds.awaitAll().flatten()
                val trendingVideos = trendingDeferred.await()

                val seenIds = mutableSetOf<String>()
                val candidateList = (relatedCandidates + channelVideos).filter { seenIds.add(it.id) }
                val trendingList = trendingVideos.filter { seenIds.add(it.id) }

                val finalFeed = mutableListOf<VideoItem>()
                val candidateQueue = ArrayDeque(candidateList)
                val trendingQueue = ArrayDeque(trendingList)

                while (candidateQueue.isNotEmpty() || trendingQueue.isNotEmpty()) {
                    repeat(2) {
                        if (candidateQueue.isNotEmpty()) {
                            finalFeed.add(candidateQueue.removeFirst())
                        }
                    }
                    if (trendingQueue.isNotEmpty()) {
                        finalFeed.add(trendingQueue.removeFirst())
                    }
                    if (candidateQueue.isEmpty() && trendingQueue.isNotEmpty()) {
                        finalFeed.addAll(trendingQueue)
                        trendingQueue.clear()
                    }
                }

                if (finalFeed.isEmpty()) {
                    trendingVideos
                } else {
                    finalFeed
                }
            }
        }
    }

    private fun StreamInfoItem.toVideoItem(): VideoItem {
        val durationFormatted = TextFormatters.formatDuration(duration)
        val bestThumbnail = thumbnails.maxByOrNull { it.height }?.url.orEmpty()
        val bestAvatar = uploaderAvatars.maxByOrNull { it.height }?.url.orEmpty()
        val dateMillis = uploadDate?.instant?.toEpochMilli()
            ?: uploadDate?.offsetDateTime()?.toInstant()?.toEpochMilli()
            ?: TextFormatters.parseTextualDateToMillis(textualUploadDate.orEmpty())
        val formattedDate = TextFormatters.formatUploadDate(dateMillis, textualUploadDate.orEmpty())

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
            viewCountText = TextFormatters.formatViewCount(viewCount),
            uploadDateMillis = dateMillis,
            uploadDateText = formattedDate
        )
    }
}
