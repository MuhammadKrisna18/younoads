package com.example.yoouutt.data.model

data class SavedVideo(
    val videoId: String,
    val url: String,
    val title: String,
    val uploader: String,
    val thumbnailUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)
