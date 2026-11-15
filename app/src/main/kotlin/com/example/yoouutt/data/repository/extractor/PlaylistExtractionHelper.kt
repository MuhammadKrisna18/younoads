package com.example.yoouutt.data.repository.extractor

import android.util.LruCache
import com.example.yoouutt.data.model.PlaylistData
import com.example.yoouutt.data.model.VideoItem
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class PlaylistExtractionHelper(
    private val playlistCache: LruCache<String, Pair<PlaylistData, Long>> = LruCache(15)
) {
    fun getPlaylistDetails(playlistUrl: String): PlaylistData {
        val resolvedUrl = if (playlistUrl.startsWith("http://") || playlistUrl.startsWith("https://")) {
            playlistUrl
        } else {
            "https://www.youtube.com/playlist?list=$playlistUrl"
        }

        val cached = playlistCache.get(resolvedUrl)
        if (cached != null && (System.currentTimeMillis() - cached.second) < 10 * 60 * 1000L) {
            return cached.first
        }

        val playlistExtractor = ServiceList.YouTube.getPlaylistExtractor(resolvedUrl)
        playlistExtractor.fetchPage()

        val id = playlistExtractor.id.orEmpty().ifEmpty {
            resolvedUrl.substringAfter("list=").substringBefore("&")
        }
        val title = playlistExtractor.name.orEmpty()
        val uploaderName = playlistExtractor.uploaderName.orEmpty()
        val uploaderAvatarUrl = try {
            playlistExtractor.uploaderAvatars.maxByOrNull { it.height }?.url.orEmpty()
        } catch (_: Exception) {
            ""
        }
        val thumbnailUrl = try {
            playlistExtractor.thumbnails.maxByOrNull { it.height }?.url.orEmpty()
        } catch (_: Exception) {
            ""
        }
        val streamCount = try {
            playlistExtractor.streamCount
        } catch (_: Exception) {
            0L
        }

        val videoList = mutableListOf<VideoItem>()
        var currentPage = playlistExtractor.initialPage
        videoList.addAll(currentPage.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() })

        var batchCount = 1
        while (currentPage.hasNextPage() && batchCount < 5) {
            try {
                currentPage = playlistExtractor.getPage(currentPage.nextPage)
                videoList.addAll(currentPage.items.filterIsInstance<StreamInfoItem>().map { it.toVideoItem() })
                batchCount++
            } catch (_: Exception) {
                break
            }
        }

        val playlistData = PlaylistData(
            id = id,
            url = resolvedUrl,
            title = title,
            uploaderName = uploaderName,
            uploaderAvatarUrl = uploaderAvatarUrl,
            thumbnailUrl = thumbnailUrl,
            streamCount = if (streamCount > 0L) streamCount else videoList.size.toLong(),
            videos = videoList.distinctBy { it.id }
        )
        playlistCache.put(resolvedUrl, Pair(playlistData, System.currentTimeMillis()))
        return playlistData
    }
}
