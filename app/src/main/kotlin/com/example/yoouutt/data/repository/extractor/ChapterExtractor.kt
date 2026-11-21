package com.example.yoouutt.data.repository.extractor

import com.example.yoouutt.data.model.VideoChapter
import org.schabi.newpipe.extractor.stream.StreamExtractor

object ChapterExtractor {
    private val timestampRegex = Regex(
        """(?:^|[\r\n])\s*(?:(?:(\d{1,2}):)?(\d{1,2}):(\d{2}))\s*[-–—:]?\s*([^\r\n]+)"""
    )

    fun extractChapters(
        streamExtractor: StreamExtractor,
        description: String
    ): List<VideoChapter> {
        val segmentChapters = try {
            streamExtractor.streamSegments.mapNotNull { segment ->
                val title = segment.title?.trim().orEmpty()
                if (title.isNotBlank()) {
                    VideoChapter(
                        title = title,
                        startTimeSeconds = segment.startTimeSeconds.toLong()
                    )
                } else null
            }.sortedBy { it.startTimeSeconds }
        } catch (_: Exception) {
            null
        }

        if (!segmentChapters.isNullOrEmpty() && segmentChapters.size >= 2) {
            return segmentChapters
        }

        return parseChaptersFromDescription(description)
    }

    fun parseChaptersFromDescription(description: String): List<VideoChapter> {
        if (description.isBlank()) return emptyList()

        val parsed = timestampRegex.findAll(description).mapNotNull { match ->
            val hours = match.groupValues[1].toIntOrNull() ?: 0
            val minutes = match.groupValues[2].toIntOrNull() ?: return@mapNotNull null
            val seconds = match.groupValues[3].toIntOrNull() ?: return@mapNotNull null
            val rawTitle = match.groupValues[4].trim()

            if (rawTitle.isBlank()) return@mapNotNull null

            val totalSeconds = hours * 3600L + minutes * 60L + seconds.toLong()
            VideoChapter(
                title = rawTitle,
                startTimeSeconds = totalSeconds
            )
        }.toList().sortedBy { it.startTimeSeconds }

        if (parsed.size >= 2 && parsed.first().startTimeSeconds <= 10L) {
            return parsed
        }

        return emptyList()
    }
}
