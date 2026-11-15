package com.example.yoouutt.data.repository.extractor

import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.util.TextFormatters
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.stream.StreamInfoItem

fun StreamInfoItem.toVideoItem(): VideoItem {
    val durationFormatted = TextFormatters.formatDuration(duration)
    val bestThumbnail = thumbnails.maxByOrNull { it.height }?.url.orEmpty()
    val bestAvatar = uploaderAvatars.maxByOrNull { it.height }?.url.orEmpty()
    val dateMillis = uploadDate?.instant?.toEpochMilli()
        ?: uploadDate?.offsetDateTime()?.toInstant()?.toEpochMilli()
        ?: TextFormatters.parseTextualDateToMillis(textualUploadDate.orEmpty())
    val formattedDate = TextFormatters.formatUploadDate(dateMillis, textualUploadDate.orEmpty())

    return VideoItem(
        id = url.substringAfter("v=").substringBefore("&"),
        title = name.orEmpty(),
        uploaderName = uploaderName.orEmpty(),
        uploaderUrl = uploaderUrl.orEmpty(),
        uploaderAvatarUrl = bestAvatar,
        durationText = durationFormatted,
        thumbnailUrl = bestThumbnail,
        url = url,
        viewCount = viewCount,
        viewCountText = TextFormatters.formatViewCount(viewCount),
        uploadDateMillis = dateMillis,
        uploadDateText = formattedDate
    )
}

fun PlaylistInfoItem.toPlaylistItem(): PlaylistItem {
    val bestThumbnail = try {
        thumbnails.maxByOrNull { it.height }?.url.orEmpty()
    } catch (_: Exception) {
        ""
    }
    val cleanId = url.substringAfter("list=").substringBefore("&")
    return PlaylistItem(
        id = cleanId.ifEmpty { url },
        url = url,
        title = name.orEmpty(),
        thumbnailUrl = bestThumbnail,
        uploaderName = uploaderName.orEmpty(),
        streamCount = streamCount
    )
}
