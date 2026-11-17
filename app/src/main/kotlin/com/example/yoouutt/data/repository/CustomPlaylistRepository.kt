package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.CustomPlaylist
import com.example.yoouutt.data.model.CustomPlaylistItem
import kotlinx.coroutines.flow.Flow

interface CustomPlaylistRepository {
    fun getCustomPlaylists(): Flow<List<CustomPlaylist>>
    fun getPlaylistItems(playlistId: Long): Flow<List<CustomPlaylistItem>>
    suspend fun createPlaylist(title: String, description: String = ""): Long
    suspend fun updatePlaylist(playlistId: Long, title: String, description: String)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addVideoToPlaylist(playlistId: Long, item: CustomPlaylistItem)
    suspend fun removeVideoFromPlaylist(playlistId: Long, videoId: String)
    suspend fun isVideoInPlaylist(playlistId: Long, videoId: String): Boolean
    suspend fun getPlaylistById(playlistId: Long): CustomPlaylist?
}
