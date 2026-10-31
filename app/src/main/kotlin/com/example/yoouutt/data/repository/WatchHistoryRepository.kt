package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.WatchHistoryItem
import kotlinx.coroutines.flow.Flow

interface WatchHistoryRepository {
    fun getHistory(): Flow<List<WatchHistoryItem>>
    suspend fun getWatchHistory(videoId: String): WatchHistoryItem?
    suspend fun saveWatchHistory(item: WatchHistoryItem)
    suspend fun deleteHistory(videoId: String)
    suspend fun clearAllHistory()
}

