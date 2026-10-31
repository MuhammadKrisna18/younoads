package com.example.yoouutt.data.model

data class VideoItem(
    val id: String,
    val title: String,
    val uploaderName: String,
    val uploaderUrl: String = "",
    val uploaderAvatarUrl: String = "",
    val durationText: String = "",
    val thumbnailUrl: String = "",
    val url: String = "",
    val viewCount: Long = 0L,
    val viewCountText: String = "",
    val uploadDateMillis: Long = 0L,
    val uploadDateText: String = ""
)
