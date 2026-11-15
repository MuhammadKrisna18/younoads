package com.example.yoouutt.data.repository.extractor

import android.util.LruCache
import com.example.yoouutt.data.model.ChannelData
import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.util.TextFormatters
import org.schabi.newpipe.extractor.Page
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabs
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class ChannelExtractionHelper(
    private val channelCache: LruCache<String, Pair<ChannelData, Long>> = LruCache(10),
    private val channelPlaylistsCache: LruCache<String, Pair<List<PlaylistItem>, Long>> = LruCache(15)
) {
    private data class ChannelPagination(
        val extractor: ChannelTabExtractor,
        var nextPage: Page?
    )

    private val channelPaginationMap = mutableMapOf<String, ChannelPagination>()
    private var activeChannelUrl: String? = null

    private fun resolveChannelUrl(channelUrl: String): String {
        return if (channelUrl.startsWith("http://") || channelUrl.startsWith("https://")) {
            channelUrl
        } else if (channelUrl.startsWith("@")) {
            "https://www.youtube.com/$channelUrl"
        } else {
            "https://www.youtube.com/@${channelUrl.replace(" ", "")}"
        }
    }

    fun getChannelDetails(channelUrl: String): ChannelData {
        val resolvedUrl = resolveChannelUrl(channelUrl)
        activeChannelUrl = resolvedUrl

        val cached = channelCache.get(resolvedUrl)
        if (cached != null && (System.currentTimeMillis() - cached.second) < 10 * 60 * 1000L) {
            return cached.first
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
        return channelData
    }

    fun getMoreChannelVideos(): List<VideoItem> {
        val activeUrl = activeChannelUrl ?: return emptyList()
        val pagination = channelPaginationMap[activeUrl] ?: return emptyList()
        val nextPage = pagination.nextPage ?: return emptyList()
        val page = pagination.extractor.getPage(nextPage)
        pagination.nextPage = if (page.hasNextPage()) page.nextPage else null
        return page.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() }
    }

    fun getChannelPlaylists(channelUrl: String): List<PlaylistItem> {
        val resolvedUrl = resolveChannelUrl(channelUrl)
        val cached = channelPlaylistsCache.get(resolvedUrl)
        if (cached != null && (System.currentTimeMillis() - cached.second) < 10 * 60 * 1000L) {
            return cached.first
        }

        val channelExtractor = ServiceList.YouTube.getChannelExtractor(resolvedUrl)
        channelExtractor.fetchPage()

        val tabs = channelExtractor.tabs
        val playlistsTab = tabs.find {
            it.contentFilters.contains(ChannelTabs.PLAYLISTS) || it.url.contains("playlists")
        } ?: return emptyList()

        val tabExtractor = ServiceList.YouTube.getChannelTabExtractor(playlistsTab)
        tabExtractor.fetchPage()

        val playlistList = mutableListOf<PlaylistItem>()
        var currentPage = tabExtractor.initialPage
        val firstBatch = currentPage.items.filterIsInstance<PlaylistInfoItem>().map { it.toPlaylistItem() }
        playlistList.addAll(firstBatch)

        var batchCount = 1
        while (currentPage.hasNextPage() && batchCount < 4) {
            try {
                currentPage = tabExtractor.getPage(currentPage.nextPage)
                val nextBatch = currentPage.items.filterIsInstance<PlaylistInfoItem>().map { it.toPlaylistItem() }
                playlistList.addAll(nextBatch)
                batchCount++
            } catch (_: Exception) {
                break
            }
        }

        val resultList = playlistList.distinctBy { it.id }
        channelPlaylistsCache.put(resolvedUrl, Pair(resultList, System.currentTimeMillis()))
        return resultList
    }
}
