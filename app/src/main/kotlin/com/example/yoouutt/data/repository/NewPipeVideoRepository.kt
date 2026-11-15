package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.ChannelData
import com.example.yoouutt.data.model.FeedItem
import com.example.yoouutt.data.model.PlaylistData
import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.WatchHistoryItem
import com.example.yoouutt.data.repository.extractor.ChannelExtractionHelper
import com.example.yoouutt.data.repository.extractor.FeedExtractionHelper
import com.example.yoouutt.data.repository.extractor.PlaylistExtractionHelper
import com.example.yoouutt.data.repository.extractor.SearchExtractionHelper
import com.example.yoouutt.data.repository.extractor.StreamExtractionHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewPipeVideoRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val streamHelper: StreamExtractionHelper = StreamExtractionHelper(),
    private val searchHelper: SearchExtractionHelper = SearchExtractionHelper(),
    private val channelHelper: ChannelExtractionHelper = ChannelExtractionHelper(),
    private val playlistHelper: PlaylistExtractionHelper = PlaylistExtractionHelper(),
    private val feedHelper: FeedExtractionHelper = FeedExtractionHelper(searchHelper, channelHelper, streamHelper)
) : VideoRepository {

    override suspend fun getTrendingVideos(): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching { feedHelper.getTrendingVideos() }
    }

    override suspend fun searchVideos(query: String): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching { searchHelper.searchVideos(query) }
    }

    override suspend fun search(query: String, filter: String?): Result<List<FeedItem>> = withContext(ioDispatcher) {
        runCatching { searchHelper.search(query, filter) }
    }

    override suspend fun getVideoStreams(videoUrl: String): Result<StreamData> = withContext(ioDispatcher) {
        runCatching { streamHelper.extractStreams(videoUrl) }
    }

    override suspend fun getChannelDetails(channelUrl: String): Result<ChannelData> = withContext(ioDispatcher) {
        runCatching { channelHelper.getChannelDetails(channelUrl) }
    }

    override suspend fun getMoreChannelVideos(): Result<List<VideoItem>> = withContext(ioDispatcher) {
        runCatching { channelHelper.getMoreChannelVideos() }
    }

    override suspend fun getChannelPlaylists(channelUrl: String): Result<List<PlaylistItem>> = withContext(ioDispatcher) {
        runCatching { channelHelper.getChannelPlaylists(channelUrl) }
    }

    override suspend fun getPlaylistDetails(playlistUrl: String): Result<PlaylistData> = withContext(ioDispatcher) {
        runCatching { playlistHelper.getPlaylistDetails(playlistUrl) }
    }

    override suspend fun getSearchSuggestions(query: String): Result<List<String>> = withContext(ioDispatcher) {
        runCatching { searchHelper.getSearchSuggestions(query) }
    }

    override suspend fun getPersonalizedFeed(
        watchedVideos: List<WatchHistoryItem>,
        watchedPlaylists: List<PlaylistItem>
    ): Result<List<FeedItem>> = withContext(ioDispatcher) {
        runCatching { feedHelper.getPersonalizedFeed(watchedVideos, watchedPlaylists) }
    }
}
