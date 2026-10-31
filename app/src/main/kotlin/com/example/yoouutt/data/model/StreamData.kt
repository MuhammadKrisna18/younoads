package com.example.yoouutt.data.model

data class StreamData(
    val videoId: String,
    val title: String,
    val uploaderName: String,
    val uploaderUrl: String = "",
    val uploaderAvatarUrl: String = "",
    val streamUrl: String,
    val isHls: Boolean
)
