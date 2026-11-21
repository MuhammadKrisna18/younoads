package com.example.yoouutt.data.model

enum class DownloadStatus(val code: Int) {
    PENDING(0),
    DOWNLOADING(1),
    COMPLETED(2),
    FAILED(3);

    companion object {
        fun fromCode(code: Int): DownloadStatus {
            return entries.firstOrNull { it.code == code } ?: FAILED
        }
    }
}

data class DownloadRequest(
    val videoId: String,
    val videoUrl: String,
    val title: String,
    val uploader: String,
    val thumbnailUrl: String,
    val downloadUrl: String,
    val qualityLabel: String,
    val isAudioOnly: Boolean = false,
    val durationMs: Long = 0L
)

data class DownloadedVideo(
    val videoId: String,
    val videoUrl: String,
    val title: String,
    val uploader: String,
    val thumbnailUrl: String,
    val filePath: String,
    val fileSizeBytes: Long,
    val qualityLabel: String,
    val isAudioOnly: Boolean = false,
    val durationMs: Long = 0L,
    val status: DownloadStatus = DownloadStatus.COMPLETED,
    val progress: Int = 100,
    val downloadedAt: Long = System.currentTimeMillis()
)
