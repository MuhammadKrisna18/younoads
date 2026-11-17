package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.SavedVideo
import kotlinx.coroutines.flow.Flow

interface SavedVideoRepository {
    fun getSavedVideos(): Flow<List<SavedVideo>>
    suspend fun isSaved(videoId: String): Boolean
    suspend fun saveVideo(video: SavedVideo)
    suspend fun removeSavedVideo(videoId: String)
    suspend fun clearAllSavedVideos()
}
