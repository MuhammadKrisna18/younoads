package com.example.yoouutt.data.model

data class CustomPlaylist(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val itemCount: Int = 0,
    val thumbnailFallbackUrl: String = ""
)
