package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.DownloadStatus
import com.example.yoouutt.data.model.DownloadedVideo
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun getDownloads(): Flow<List<DownloadedVideo>>
    suspend fun getDownload(videoId: String): DownloadedVideo?
    suspend fun isDownloaded(videoId: String): Boolean
    suspend fun saveDownload(download: DownloadedVideo)
    suspend fun updateProgress(videoId: String, progress: Int, status: DownloadStatus)
    suspend fun updateCompleted(videoId: String, filePath: String, fileSizeBytes: Long)
    suspend fun updateFailed(videoId: String)
    suspend fun deleteDownload(videoId: String)
    suspend fun clearAllDownloads()
}
