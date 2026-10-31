package com.example.yoouutt.data.model

data class ChannelData(
    val url: String,
    val name: String,
    val avatarUrl: String,
    val bannerUrl: String,
    val subscriberCountText: String,
    val description: String,
    val isVerified: Boolean,
    val videos: List<VideoItem>
)

