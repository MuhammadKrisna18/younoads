package com.example.yoouutt.data.repository.extractor

import com.example.yoouutt.data.model.FeedItem
import com.example.yoouutt.data.model.VideoItem
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class SearchExtractionHelper {
    fun searchVideos(query: String): List<VideoItem> {
        val searchExtractor = ServiceList.YouTube.getSearchExtractor(query)
        searchExtractor.fetchPage()
        val initialPage = searchExtractor.initialPage
        return initialPage.items
            .filterIsInstance<StreamInfoItem>()
            .map { it.toVideoItem() }
            .distinctBy { it.id }
    }

    fun search(query: String, filter: String?): List<FeedItem> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()

        val searchExtractor = if (!filter.isNullOrBlank()) {
            ServiceList.YouTube.getSearchExtractor(trimmed, listOf(filter), null)
        } else {
            ServiceList.YouTube.getSearchExtractor(trimmed)
        }
        searchExtractor.fetchPage()
        val initialPage = searchExtractor.initialPage
        val list = mutableListOf<FeedItem>()
        for (item in initialPage.items) {
            when (item) {
                is StreamInfoItem -> list.add(FeedItem.Video(item.toVideoItem()))
                is PlaylistInfoItem -> list.add(FeedItem.Playlist(item.toPlaylistItem()))
            }
        }
        return list.distinctBy { it.id }
    }

    fun getSearchSuggestions(query: String): List<String> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()
        return ServiceList.YouTube.suggestionExtractor.suggestionList(trimmed).take(10)
    }
}
