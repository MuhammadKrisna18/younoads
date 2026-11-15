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
            return getTrendingVideos().map { FeedItem.Video(it) }
        }

        return coroutineScope {
            val watchedChannels = watchedVideos
                .groupingBy { it.uploaderName }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .map { it.key }
                .filter { it.isNotBlank() }
                .take(4)

            val recentVideoUrls = watchedVideos
                .map { it.videoUrl }
                .filter { it.isNotBlank() }
                .take(3)

            val relatedCandidates = mutableListOf<VideoItem>()
            for (url in recentVideoUrls) {
                val cached = streamHelper.getCachedRelatedVideos(url)
                if (!cached.isNullOrEmpty()) {
                    relatedCandidates.addAll(cached.take(4))
                }
            }

            val channelDeferreds = watchedChannels.map { channel ->
                async {
                    runCatching { searchHelper.searchVideos(channel).take(4) }.getOrNull().orEmpty()
                }
            }

            val playlistDeferreds = watchedChannels.take(2).map { channel ->
                async {
                    runCatching { channelHelper.getChannelPlaylists(channel).take(2) }.getOrNull().orEmpty()
                }
            }

            val trendingDeferred = async {
                runCatching { getTrendingVideos() }.getOrNull().orEmpty()
            }

            val channelVideos = channelDeferreds.awaitAll().flatten()
            val channelPlaylists = playlistDeferreds.awaitAll().flatten()
            val trendingVideos = trendingDeferred.await()

            val seenVideoIds = mutableSetOf<String>()
            val candidateList = (relatedCandidates + channelVideos).filter { seenVideoIds.add(it.id) }
            val trendingList = trendingVideos.filter { seenVideoIds.add(it.id) }

            val relevantPlaylists = (watchedPlaylists + channelPlaylists).distinctBy { it.id }
            val playlistQueue = ArrayDeque(relevantPlaylists)

            val candidateQueue = ArrayDeque(candidateList)
            val trendingQueue = ArrayDeque(trendingList)
            val finalFeed = mutableListOf<FeedItem>()

            var videoCounter = 0
            while (candidateQueue.isNotEmpty() || trendingQueue.isNotEmpty()) {
                repeat(2) {
                    if (candidateQueue.isNotEmpty()) {
                        finalFeed.add(FeedItem.Video(candidateQueue.removeFirst()))
                        videoCounter++
                    }
                }
                if (videoCounter in 1..2 && playlistQueue.isNotEmpty()) {
                    finalFeed.add(FeedItem.Playlist(playlistQueue.removeFirst()))
                }
                if (trendingQueue.isNotEmpty()) {
                    finalFeed.add(FeedItem.Video(trendingQueue.removeFirst()))
                    videoCounter++
                }
                if (videoCounter >= 4 && playlistQueue.isNotEmpty()) {
                    finalFeed.add(FeedItem.Playlist(playlistQueue.removeFirst()))
                    videoCounter = 0
                }
                if (candidateQueue.isEmpty() && trendingQueue.isNotEmpty()) {
                    while (trendingQueue.isNotEmpty()) {
                        finalFeed.add(FeedItem.Video(trendingQueue.removeFirst()))
                        videoCounter++
                        if (videoCounter >= 4 && playlistQueue.isNotEmpty()) {
                            finalFeed.add(FeedItem.Playlist(playlistQueue.removeFirst()))
                            videoCounter = 0
                        }
                    }
                }
            }

            while (playlistQueue.isNotEmpty()) {
                finalFeed.add(FeedItem.Playlist(playlistQueue.removeFirst()))
            }

            if (finalFeed.isEmpty()) {
                trendingVideos.map { FeedItem.Video(it) }
            } else {
                finalFeed
            }
        }
    }
}
