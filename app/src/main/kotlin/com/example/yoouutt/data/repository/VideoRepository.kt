package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.ChannelData
import com.example.yoouutt.data.model.PlaylistData
import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.WatchHistoryItem

interface VideoRepository {
    suspend fun getTrendingVideos(): Result<List<VideoItem>>
    suspend fun searchVideos(query: String): Result<List<VideoItem>>
    suspend fun getVideoStreams(videoUrl: String): Result<StreamData>
    suspend fun getChannelDetails(channelUrl: String): Result<ChannelData>
    suspend fun getMoreChannelVideos(): Result<List<VideoItem>>
    suspend fun getChannelPlaylists(channelUrl: String): Result<List<PlaylistItem>>
    suspend fun getPlaylistDetails(playlistUrl: String): Result<PlaylistData>
    suspend fun getSearchSuggestions(query: String): Result<List<String>>
    suspend fun getPersonalizedFeed(watchedVideos: List<WatchHistoryItem>): Result<List<VideoItem>>
}
