package com.example.yoouutt.data.model

data class PlaylistItem(
    val id: String,
    val url: String,
    val title: String,
    val thumbnailUrl: String,
    val uploaderName: String,
    val streamCount: Long = 0L
)

data class PlaylistData(
    val id: String,
    val url: String,
    val title: String,
    val uploaderName: String,
    val uploaderAvatarUrl: String = "",
    val thumbnailUrl: String = "",
    val streamCount: Long = 0L,
    val videos: List<VideoItem> = emptyList()
)

data class PlaylistContext(
    val playlistId: String,
    val playlistTitle: String,
    val videos: List<VideoItem>,
    val currentIndex: Int
)

