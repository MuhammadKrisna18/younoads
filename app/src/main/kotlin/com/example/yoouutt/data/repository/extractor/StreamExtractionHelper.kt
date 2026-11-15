package com.example.yoouutt.data.repository.extractor

import android.util.LruCache
import com.example.yoouutt.data.model.AudioTrackOption
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.VideoResolutionOption
import com.example.yoouutt.util.TextFormatters
import org.schabi.newpipe.extractor.MediaFormat
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.AudioTrackType
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType
import java.util.Locale

class StreamExtractionHelper(
    private val streamCache: LruCache<String, Pair<StreamData, Long>> = LruCache(25),
    private val fallbackExtractor: PipedFallbackExtractor = PipedFallbackExtractor()
) {
    fun getCachedRelatedVideos(videoUrl: String): List<VideoItem>? {
        return streamCache.get(videoUrl)?.first?.relatedVideos
    }

    fun extractStreams(videoUrl: String): StreamData {
        val cached = streamCache.get(videoUrl)
        if (cached != null && (System.currentTimeMillis() - cached.second) < 15 * 60 * 1000L) {
            return cached.first
        }

        try {
            val streamExtractor = ServiceList.YouTube.getStreamExtractor(videoUrl)
            streamExtractor.fetchPage()

            val hlsUrl = streamExtractor.hlsUrl
            val isLive = try {
                streamExtractor.streamType == StreamType.LIVE_STREAM
            } catch (_: Exception) {
                false
            }

            val audioStreams = try { streamExtractor.audioStreams } catch (_: Exception) { emptyList() }
            val availableAudioTracks = audioStreams
                .filter { !it.content.isNullOrBlank() }
                .groupBy { stream ->
                    stream.audioTrackId?.takeIf { it.isNotBlank() }
                        ?: "${stream.audioTrackName}_${stream.audioLocale?.toLanguageTag()}_${stream.audioTrackType?.name}"
                }
                .mapNotNull { (_, streamsInGroup) ->
                    val bestInGroup = streamsInGroup.maxWithOrNull(
                        compareBy<AudioStream> { it.format == MediaFormat.M4A }
                            .thenBy { it.averageBitrate.takeIf { b -> b > 0 } ?: it.bitrate }
                    ) ?: return@mapNotNull null

                    val isOriginal = bestInGroup.audioTrackType == AudioTrackType.ORIGINAL
                    val locale = bestInGroup.audioLocale
                    val rawName = bestInGroup.audioTrackName.orEmpty().trim()
                    val langName = when {
                        rawName.isNotBlank() -> rawName
                        locale != null -> locale.getDisplayLanguage(Locale.forLanguageTag("id")).takeIf { it.isNotBlank() } ?: locale.displayLanguage
                        else -> "Audio Utama"
                    }

                    val displayName = when {
                        isOriginal && !langName.contains("asli", ignoreCase = true) && !langName.contains("original", ignoreCase = true) -> "$langName (Asli)"
                        bestInGroup.audioTrackType == AudioTrackType.DUBBED && !langName.contains("dub", ignoreCase = true) && !langName.contains("sulih", ignoreCase = true) -> "$langName (Sulih Suara)"
                        bestInGroup.audioTrackType == AudioTrackType.DESCRIPTIVE -> "$langName (Deskriptif)"
                        else -> langName
                    }

                    AudioTrackOption(
                        id = bestInGroup.audioTrackId.orEmpty().ifBlank { "${langName}_${bestInGroup.getItag()}" },
                        displayName = displayName,
                        languageCode = locale?.language.orEmpty(),
                        audioUrl = bestInGroup.content,
                        isOriginal = isOriginal,
                        format = bestInGroup.format?.name.orEmpty(),
                        bitrate = bestInGroup.averageBitrate.takeIf { b -> b > 0 } ?: bestInGroup.bitrate
                    )
                }
                .sortedWith(
                    compareByDescending<AudioTrackOption> { it.isOriginal }
                        .thenByDescending { it.languageCode.equals(Locale.getDefault().language, ignoreCase = true) }
                        .thenByDescending { it.bitrate }
                )

            val selectedAudioTrack = availableAudioTracks.firstOrNull { it.isOriginal }
                ?: availableAudioTracks.firstOrNull { it.languageCode.equals(Locale.getDefault().language, ignoreCase = true) }
                ?: availableAudioTracks.firstOrNull()

            val bestAudioUrl = selectedAudioTrack?.audioUrl

            val videoStreams = try { streamExtractor.videoStreams } catch (_: Exception) { emptyList() }
            val videoOnlyStreams = try { streamExtractor.videoOnlyStreams } catch (_: Exception) { emptyList() }

            val progressiveOptions = videoStreams
                .filter { !it.isVideoOnly() && !it.content.isNullOrBlank() }
                .map {
                    VideoResolutionOption(
                        label = it.getResolution().takeIf { res -> res.isNotBlank() } ?: "${it.height}p",
                        height = it.height,
                        videoUrl = it.content,
                        isVideoOnly = false,
                        audioUrl = null,
                        isHls = false,
                        format = it.format?.name.orEmpty()
                    )
                }

            val videoOnlyOptions = if (!bestAudioUrl.isNullOrBlank()) {
                videoOnlyStreams
                    .filter { !it.content.isNullOrBlank() }
                    .map {
                        VideoResolutionOption(
                            label = it.getResolution().takeIf { res -> res.isNotBlank() } ?: "${it.height}p",
                            height = it.height,
                            videoUrl = it.content,
                            isVideoOnly = true,
                            audioUrl = bestAudioUrl,
                            isHls = false,
                            format = it.format?.name.orEmpty()
                        )
                    }
            } else {
                emptyList()
            }

            val availableResolutions = if (isLive && !hlsUrl.isNullOrBlank()) {
                listOf(
                    VideoResolutionOption(
                        label = "Live",
                        height = 720,
                        videoUrl = hlsUrl,
                        isVideoOnly = false,
                        audioUrl = null,
                        isHls = true
                    )
                )
            } else {
                (progressiveOptions + videoOnlyOptions)
                    .groupBy { it.height }
                    .mapNotNull { (_, options) ->
                        options.maxWithOrNull(
                            compareBy<VideoResolutionOption> { !it.isVideoOnly }
                                .thenBy { it.format.equals("MPEG_4", ignoreCase = true) }
                        )
                    }
                    .sortedByDescending { it.height }
            }

            if (availableResolutions.isEmpty() && hlsUrl.isNullOrBlank()) {
                val fallbackData = fallbackExtractor.extractStreamData(videoUrl)
                if (fallbackData != null) {
                    streamCache.put(videoUrl, Pair(fallbackData, System.currentTimeMillis()))
                    return fallbackData
                }
            }

            val fallbackStreamUrl = when {
                isLive && !hlsUrl.isNullOrBlank() -> hlsUrl
                progressiveOptions.isNotEmpty() -> progressiveOptions.first().videoUrl
                !hlsUrl.isNullOrBlank() -> hlsUrl
                videoStreams.isNotEmpty() -> videoStreams.first().content
                videoOnlyStreams.isNotEmpty() -> videoOnlyStreams.first().content
                else -> {
                    val fallbackData = fallbackExtractor.extractStreamData(videoUrl)
                    if (fallbackData != null) {
                        streamCache.put(videoUrl, Pair(fallbackData, System.currentTimeMillis()))
                        return fallbackData
                    }
                    throw IllegalStateException("No playable stream available")
                }
            }

            val selectedResolution = availableResolutions.firstOrNull { it.height == 720 }
                ?: availableResolutions.firstOrNull { it.height == 1080 }
                ?: availableResolutions.firstOrNull()
                ?: VideoResolutionOption(
                    label = if (isLive) "Live" else "Auto",
                    height = 720,
                    videoUrl = fallbackStreamUrl,
                    isVideoOnly = false,
                    audioUrl = null,
                    isHls = isLive && !hlsUrl.isNullOrBlank()
                )

            val streamUrl = selectedResolution.videoUrl
            val isHls = selectedResolution.isHls

            val bestAvatar = streamExtractor.uploaderAvatars.maxByOrNull { it.height }?.url.orEmpty()
            val rawDescription = try { streamExtractor.description.content.orEmpty() } catch (_: Exception) { "" }
            val cleanDesc = TextFormatters.cleanHtmlDescription(rawDescription)
            val viewCount = try { streamExtractor.viewCount } catch (_: Exception) { 0L }
            val viewText = TextFormatters.formatViewCount(viewCount)
            val uploadMillis = try {
                streamExtractor.uploadDate?.instant?.toEpochMilli()
                    ?: streamExtractor.uploadDate?.offsetDateTime()?.toInstant()?.toEpochMilli()
            } catch (_: Exception) {
                null
            }
            val textualDate = try { streamExtractor.textualUploadDate.orEmpty() } catch (_: Exception) { "" }
            val formattedDate = TextFormatters.formatUploadDate(uploadMillis, textualDate)

            val subscriberCount = try { streamExtractor.uploaderSubscriberCount } catch (_: Exception) { 0L }
            val subscriberText = if (subscriberCount > 0L) TextFormatters.formatSubscriberCount(subscriberCount) else ""

            val relatedItems = try {
                streamExtractor.relatedStreams?.items
                    ?.filterIsInstance<StreamInfoItem>()
                    ?.map { it.toVideoItem() }
                    .orEmpty()
            } catch (_: Exception) {
                emptyList()
            }

            val streamData = StreamData(
                videoId = streamExtractor.id,
                streamUrl = streamUrl,
                title = streamExtractor.name,
                uploaderName = streamExtractor.uploaderName,
                uploaderUrl = streamExtractor.uploaderUrl,
                uploaderAvatarUrl = bestAvatar,
                uploaderSubscriberCountText = subscriberText,
                isHls = isHls,
                description = cleanDesc,
                viewCountText = viewText,
                uploadDateText = formattedDate,
                relatedVideos = relatedItems,
                availableResolutions = availableResolutions,
                selectedResolution = selectedResolution,
                availableAudioTracks = availableAudioTracks,
                selectedAudioTrack = selectedAudioTrack
            )
            streamCache.put(videoUrl, Pair(streamData, System.currentTimeMillis()))
            return streamData
        } catch (e: Exception) {
            val fallbackData = fallbackExtractor.extractStreamData(videoUrl)
            if (fallbackData != null) {
                streamCache.put(videoUrl, Pair(fallbackData, System.currentTimeMillis()))
                return fallbackData
            }
            throw e
        }
    }
}
