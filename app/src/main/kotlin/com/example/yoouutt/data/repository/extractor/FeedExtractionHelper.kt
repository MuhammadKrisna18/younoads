package com.example.yoouutt.data.repository.extractor

import com.example.yoouutt.data.model.FeedItem
import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.WatchHistoryItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class FeedExtractionHelper(
    private val searchHelper: SearchExtractionHelper,
    private val channelHelper: ChannelExtractionHelper,
    private val streamHelper: StreamExtractionHelper
) {
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

    fun getTrendingVideos(): List<VideoItem> {
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

        return combinedList.distinctBy { it.id }
    }

    suspend fun getPersonalizedFeed(
        watchedVideos: List<WatchHistoryItem>,
        watchedPlaylists: List<PlaylistItem>
    ): List<FeedItem> {
        if (watchedVideos.isEmpty() && watchedPlaylists.isEmpty()) {
            return getTrendingVideos().shuffled().map { FeedItem.Video(it) }
        }

        return coroutineScope {
            val watchedVideoIds = watchedVideos.mapNotNull { it.videoId.takeIf { id -> id.isNotBlank() } }.toSet()
            val watchedVideoUrls = watchedVideos.mapNotNull { it.videoUrl.takeIf { url -> url.isNotBlank() } }.toSet()

            val allWatchedChannels = watchedVideos
                .map { it.uploaderName.trim() }
                .filter { it.isNotBlank() }
                .distinct()

            val frequentChannels = watchedVideos
                .groupingBy { it.uploaderName.trim() }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .map { it.key }
                .filter { it.isNotBlank() }
                .take(3)

            val otherChannels = (allWatchedChannels - frequentChannels.toSet()).shuffled().take(4)
            val selectedChannels = (frequentChannels + otherChannels).distinct()

            val stopWords = setOf(
                "dan", "yang", "di", "ke", "dari", "ini", "itu", "untuk", "pada", "dengan", "adalah",
                "buat", "full", "eps", "episode", "part", "official", "video", "the", "a", "an", "in",
                "on", "at", "to", "for", "of", "with", "by", "live", "stream"
            )
            val topicQueries = watchedVideos.take(5).mapNotNull { item ->
                val words = item.title.replace(Regex("[^a-zA-Z0-9\\s]"), " ")
                    .split("\\s+".toRegex())
                    .filter { it.isNotBlank() && it.length > 2 && it.lowercase() !in stopWords }
                words.take(3).joinToString(" ").takeIf { it.isNotBlank() }
            }.distinct().take(2)

            val channelDeferreds = selectedChannels.map { channel ->
                async {
                    runCatching { searchHelper.searchVideos(channel).take(4) }.getOrNull().orEmpty()
                }
            }

            val topicDeferreds = topicQueries.map { topic ->
                async {
                    runCatching { searchHelper.searchVideos(topic).take(4) }.getOrNull().orEmpty()
                }
            }

            val playlistDeferreds = selectedChannels.take(2).map { channel ->
                async {
                    runCatching { channelHelper.getChannelPlaylists(channel).take(2) }.getOrNull().orEmpty()
                }
            }

            val trendingDeferred = async {
                runCatching { getTrendingVideos() }.getOrNull().orEmpty()
            }

            val channelVideos = channelDeferreds.awaitAll().flatten()
            val topicVideos = topicDeferreds.awaitAll().flatten()
            val channelPlaylists = playlistDeferreds.awaitAll().flatten()
            val trendingVideos = trendingDeferred.await()

            val rawVideos = (channelVideos + topicVideos).distinctBy { it.id }
            val unwatchedVideos = rawVideos.filter {
                (it.id.isBlank() || it.id !in watchedVideoIds) &&
                (it.url.isBlank() || it.url !in watchedVideoUrls)
            }
            val baseCandidates = if (unwatchedVideos.size >= 8) unwatchedVideos else rawVideos

            val cappedVideos = mutableListOf<VideoItem>()
            val creatorCounts = mutableMapOf<String, Int>()
            for (video in baseCandidates.shuffled()) {
                val creatorKey = video.uploaderName.trim().lowercase()
                val count = creatorCounts.getOrDefault(creatorKey, 0)
                if (count < 2) {
                    cappedVideos.add(video)
                    creatorCounts[creatorKey] = count + 1
                }
            }

            val trendingFiltered = trendingVideos
                .filter { (it.id.isBlank() || it.id !in watchedVideoIds) && cappedVideos.none { c -> c.id == it.id } }
                .shuffled()
                .take(4)

            val finalVideoPool = (cappedVideos + trendingFiltered).distinctBy { it.id }.shuffled()
            val relevantPlaylists = (watchedPlaylists + channelPlaylists).distinctBy { it.id }.shuffled()
            val playlistQueue = ArrayDeque(relevantPlaylists)

            val finalFeed = mutableListOf<FeedItem>()
            var countSinceLastPlaylist = 0
            for (video in finalVideoPool) {
                finalFeed.add(FeedItem.Video(video))
                countSinceLastPlaylist++
                if (countSinceLastPlaylist >= 4 && playlistQueue.isNotEmpty()) {
                    finalFeed.add(FeedItem.Playlist(playlistQueue.removeFirst()))
                    countSinceLastPlaylist = 0
                }
            }

            while (playlistQueue.isNotEmpty()) {
                finalFeed.add(FeedItem.Playlist(playlistQueue.removeFirst()))
            }

            if (finalFeed.isEmpty()) {
                trendingVideos.shuffled().map { FeedItem.Video(it) }
            } else {
                finalFeed
            }
        }
    }
}
