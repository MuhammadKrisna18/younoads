package com.example.yoouutt.data.model

data class VideoResolutionOption(
    val label: String,
    val height: Int,
    val videoUrl: String,
    val isVideoOnly: Boolean = false,
    val audioUrl: String? = null,
    val isHls: Boolean = false,
    val format: String = ""
)

data class StreamData(
    val videoId: String,
    val title: String,
    val uploaderName: String,
    val uploaderUrl: String = "",
    val uploaderAvatarUrl: String = "",
    val uploaderSubscriberCountText: String = "",
    val streamUrl: String,
    val isHls: Boolean,
    val description: String = "",
    val viewCountText: String = "",
    val uploadDateText: String = "",
    val relatedVideos: List<VideoItem> = emptyList(),
    val availableResolutions: List<VideoResolutionOption> = emptyList(),
    val selectedResolution: VideoResolutionOption? = null
)
