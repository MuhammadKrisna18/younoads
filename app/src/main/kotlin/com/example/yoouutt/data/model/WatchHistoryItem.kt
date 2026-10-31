package com.example.yoouutt.data.model

data class WatchHistoryItem(
    val videoId: String,
    val videoUrl: String,
    val title: String,
    val uploaderName: String,
    val thumbnailUrl: String,
    val lastPlaybackPositionMs: Long,
    val totalDurationMs: Long,
    val lastWatchedTimestamp: Long
)

