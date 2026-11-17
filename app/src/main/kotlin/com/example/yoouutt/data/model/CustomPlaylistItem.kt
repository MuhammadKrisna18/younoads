package com.example.yoouutt.data.model

data class CustomPlaylistItem(
    val id: Long = 0,
    val playlistId: Long,
    val videoId: String,
    val url: String,
    val title: String,
    val uploader: String,
    val thumbnailUrl: String,
    val duration: String = "",
    val addedAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
