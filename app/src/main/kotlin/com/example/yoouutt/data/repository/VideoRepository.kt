package com.example.yoouutt.data.repository

import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem

interface VideoRepository {
    suspend fun getTrendingVideos(): Result<List<VideoItem>>
    suspend fun searchVideos(query: String): Result<List<VideoItem>>
    suspend fun getVideoStreams(videoUrl: String): Result<StreamData>
}

