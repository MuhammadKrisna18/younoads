package com.example.yoouutt.data.repository.extractor

import com.example.yoouutt.data.model.AudioTrackOption
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.VideoResolutionOption
import com.example.yoouutt.util.TextFormatters
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PipedFallbackExtractor(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()
) {
    private enum class ApiType {
        PIPED,
        INVIDIOUS
    }

    private data class FallbackEndpoint(
        val type: ApiType,
        val baseUrl: String
    )

    private val endpoints = listOf(
        FallbackEndpoint(ApiType.PIPED, "https://api.piped.private.coffee"),
        FallbackEndpoint(ApiType.PIPED, "https://pipedapi.ducks.party"),
        FallbackEndpoint(ApiType.INVIDIOUS, "https://yt.chocolatemoo53.com"),
        FallbackEndpoint(ApiType.PIPED, "https://pipedapi.kavin.rocks")
    )

    fun extractStreamData(videoUrl: String): StreamData? {
        val videoId = extractVideoId(videoUrl)
        if (videoId.isBlank()) return null

        for (endpoint in endpoints) {
            try {
                val targetUrl = when (endpoint.type) {
                    ApiType.PIPED -> "${endpoint.baseUrl}/streams/$videoId"
                    ApiType.INVIDIOUS -> "${endpoint.baseUrl}/api/v1/videos/$videoId"
                }

                val request = Request.Builder()
                    .url(targetUrl)
                    .header("User-Agent", "Mozilla/5.0 (Android; Mobile; rv:130.0) Gecko/130.0 Firefox/130.0")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use
                    val bodyString = response.body?.string().orEmpty()
                    if (bodyString.isBlank()) return@use

                    val json = JSONObject(bodyString)
                    val result = when (endpoint.type) {
                        ApiType.PIPED -> parsePipedResponse(videoId, json)
                        ApiType.INVIDIOUS -> parseInvidiousResponse(videoId, json)
                    }
                    if (result != null) {
                        return result
                    }
                }
            } catch (_: Exception) {
            }
        }
        return null
    }

    private fun extractVideoId(url: String): String {
        return when {
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
            url.contains("embed/") -> url.substringAfter("embed/").substringBefore("?").substringBefore("&")
            url.contains("shorts/") -> url.substringAfter("shorts/").substringBefore("?").substringBefore("&")
            else -> url.trim().removePrefix("https://").removePrefix("http://")
        }
    }

    private fun parsePipedResponse(videoId: String, json: JSONObject): StreamData? {
        val title = json.optString("title").ifBlank { "Video" }
        val uploaderName = json.optString("uploader").ifBlank { "Saluran" }
        val uploaderUrl = json.optString("uploaderUrl")
        val uploaderAvatar = json.optString("uploaderAvatar")
        val uploaderSubCount = json.optLong("uploaderSubscriberCount", 0L)
        val subscriberText = if (uploaderSubCount > 0L) TextFormatters.formatSubscriberCount(uploaderSubCount) else ""
        val description = TextFormatters.cleanHtmlDescription(json.optString("description"))
        val views = json.optLong("views", 0L)
        val viewText = if (views > 0L) TextFormatters.formatViewCount(views) else ""
        val uploadDateText = json.optString("uploadDate")
        val isLive = json.optBoolean("liveStream", false) || json.optBoolean("isLive", false)

        val hlsUrl = json.optString("hls").takeIf { it.isNotBlank() }
        val videoStreamsArray = json.optJSONArray("videoStreams")
        val audioStreamsArray = json.optJSONArray("audioStreams")

        val audioOptions = mutableListOf<AudioTrackOption>()
        if (audioStreamsArray != null) {
            for (i in 0 until audioStreamsArray.length()) {
                val audioObj = audioStreamsArray.optJSONObject(i) ?: continue
                val audioUrl = audioObj.optString("url")
                if (audioUrl.isBlank()) continue
                val quality = audioObj.optString("quality").ifBlank { "Audio" }
                val format = audioObj.optString("format").ifBlank { "M4A" }
                val bitrate = audioObj.optInt("bitrate", 0)
                audioOptions.add(
                    AudioTrackOption(
                        id = "audio_$i",
                        displayName = "$quality ($format)",
                        audioUrl = audioUrl,
                        isOriginal = (i == 0),
                        format = format,
                        bitrate = bitrate
                    )
                )
            }
        }
        val bestAudioUrl = audioOptions.firstOrNull()?.audioUrl

        val resolutionOptions = mutableListOf<VideoResolutionOption>()
        if (videoStreamsArray != null) {
            for (i in 0 until videoStreamsArray.length()) {
                val vObj = videoStreamsArray.optJSONObject(i) ?: continue
                val vUrl = vObj.optString("url")
                if (vUrl.isBlank()) continue
                val quality = vObj.optString("quality").ifBlank { "720p" }
                val format = vObj.optString("format")
                val isVideoOnly = vObj.optBoolean("videoOnly", false)
                val height = vObj.optInt("height", quality.filter { it.isDigit() }.toIntOrNull() ?: 720)
                val isHlsFormat = format.equals("HLS", ignoreCase = true)
                if (isHlsFormat && !isLive) continue

                resolutionOptions.add(
                    VideoResolutionOption(
                        label = quality,
                        height = height,
                        videoUrl = vUrl,
                        isVideoOnly = isVideoOnly,
                        audioUrl = if (isVideoOnly) bestAudioUrl else null,
                        isHls = isHlsFormat,
                        format = format
                    )
                )
            }
        }

        if (isLive && !hlsUrl.isNullOrBlank() && resolutionOptions.none { it.isHls }) {
            resolutionOptions.add(
                0,
                VideoResolutionOption(
                    label = "Live",
                    height = 720,
                    videoUrl = hlsUrl,
                    isVideoOnly = false,
                    audioUrl = null,
                    isHls = true,
                    format = "HLS"
                )
            )
        }

        if (resolutionOptions.isEmpty() && (!isLive || hlsUrl.isNullOrBlank())) {
            return null
        }

        val selectedResolution = resolutionOptions.firstOrNull { it.height == 720 }
            ?: resolutionOptions.firstOrNull { it.height == 1080 }
            ?: resolutionOptions.firstOrNull()
            ?: VideoResolutionOption(
                label = if (isLive) "Live" else "Auto",
                height = 720,
                videoUrl = if (isLive) hlsUrl.orEmpty() else (resolutionOptions.firstOrNull()?.videoUrl ?: ""),
                isVideoOnly = false,
                audioUrl = null,
                isHls = isLive && !hlsUrl.isNullOrBlank()
            )

        val relatedVideos = mutableListOf<VideoItem>()
        val relatedArray = json.optJSONArray("relatedStreams")
        if (relatedArray != null) {
            for (i in 0 until relatedArray.length()) {
                val relObj = relatedArray.optJSONObject(i) ?: continue
                val relUrl = relObj.optString("url")
                val relId = when {
                    relUrl.contains("v=") -> relUrl.substringAfter("v=").substringBefore("&")
                    relUrl.startsWith("/watch?v=") -> relUrl.removePrefix("/watch?v=").substringBefore("&")
                    else -> relUrl.removePrefix("/")
                }
                if (relId.isBlank()) continue
                val relTitle = relObj.optString("title")
                val relUploader = relObj.optString("uploaderName")
                val relThumb = relObj.optString("thumbnail")
                val relDuration = relObj.optLong("duration", 0L)
                val relViews = relObj.optLong("views", 0L)
                val relDate = relObj.optString("uploadedDate")

                relatedVideos.add(
                    VideoItem(
                        id = relId,
                        title = relTitle,
                        uploaderName = relUploader,
                        uploaderUrl = relObj.optString("uploaderUrl"),
                        uploaderAvatarUrl = relObj.optString("uploaderAvatar"),
                        durationText = TextFormatters.formatDuration(relDuration),
                        thumbnailUrl = relThumb,
                        url = "https://www.youtube.com/watch?v=$relId",
                        viewCount = relViews,
                        viewCountText = TextFormatters.formatViewCount(relViews),
                        uploadDateMillis = 0L,
                        uploadDateText = relDate
                    )
                )
            }
        }

        return StreamData(
            videoId = videoId,
            title = title,
            uploaderName = uploaderName,
            uploaderUrl = uploaderUrl,
            uploaderAvatarUrl = uploaderAvatar,
            uploaderSubscriberCountText = subscriberText,
            streamUrl = selectedResolution.videoUrl,
            isHls = selectedResolution.isHls,
            description = description,
            viewCountText = viewText,
            uploadDateText = uploadDateText,
            relatedVideos = relatedVideos,
            availableResolutions = resolutionOptions,
            selectedResolution = selectedResolution,
            availableAudioTracks = audioOptions,
            selectedAudioTrack = audioOptions.firstOrNull()
        )
    }

    private fun parseInvidiousResponse(videoId: String, json: JSONObject): StreamData? {
        val title = json.optString("title").ifBlank { "Video" }
        val author = json.optString("author").ifBlank { "Saluran" }
        val authorUrl = json.optString("authorUrl")
        val authorThumbnails = json.optJSONArray("authorThumbnails")
        val bestAvatar = if (authorThumbnails != null && authorThumbnails.length() > 0) {
            authorThumbnails.optJSONObject(authorThumbnails.length() - 1)?.optString("url").orEmpty()
        } else {
            ""
        }
        val subCountText = json.optString("subCountText")
        val description = TextFormatters.cleanHtmlDescription(json.optString("description"))
        val viewCount = json.optLong("viewCount", 0L)
        val viewText = if (viewCount > 0L) TextFormatters.formatViewCount(viewCount) else ""
        val publishedText = json.optString("publishedText")
        val isLive = json.optBoolean("liveStream", false) || json.optBoolean("isLive", false)

        val hlsUrl = json.optString("hlsUrl").takeIf { it.isNotBlank() }
        val formatStreamsArray = json.optJSONArray("formatStreams")
        val adaptiveFormatsArray = json.optJSONArray("adaptiveFormats")

        val resolutionOptions = mutableListOf<VideoResolutionOption>()

        if (isLive && !hlsUrl.isNullOrBlank()) {
            resolutionOptions.add(
                VideoResolutionOption(
                    label = "Live",
                    height = 720,
                    videoUrl = hlsUrl,
                    isVideoOnly = false,
                    audioUrl = null,
                    isHls = true,
                    format = "HLS"
                )
            )
        }

        if (formatStreamsArray != null) {
            for (i in 0 until formatStreamsArray.length()) {
                val fObj = formatStreamsArray.optJSONObject(i) ?: continue
                val fUrl = fObj.optString("url")
                if (fUrl.isBlank()) continue
                val qualityLabel = fObj.optString("qualityLabel").ifBlank { fObj.optString("resolution", "720p") }
                val height = qualityLabel.filter { it.isDigit() }.toIntOrNull() ?: 720
                val container = fObj.optString("container", "mp4")
                if ((container.equals("m3u8", ignoreCase = true) || container.equals("hls", ignoreCase = true)) && !isLive) {
                    continue
                }

                resolutionOptions.add(
                    VideoResolutionOption(
                        label = qualityLabel,
                        height = height,
                        videoUrl = fUrl,
                        isVideoOnly = false,
                        audioUrl = null,
                        isHls = false,
                        format = container
                    )
                )
            }
        }

        val audioOptions = mutableListOf<AudioTrackOption>()
        if (adaptiveFormatsArray != null) {
            for (i in 0 until adaptiveFormatsArray.length()) {
                val aObj = adaptiveFormatsArray.optJSONObject(i) ?: continue
                val type = aObj.optString("type")
                if (!type.startsWith("audio/")) continue
                val aUrl = aObj.optString("url")
                if (aUrl.isBlank()) continue
                val audioQuality = aObj.optString("audioQuality").ifBlank { "Medium" }
                val container = aObj.optString("container", "m4a")
                val bitrate = aObj.optInt("bitrate", 0)

                audioOptions.add(
                    AudioTrackOption(
                        id = "audio_$i",
                        displayName = "$audioQuality ($container)",
                        audioUrl = aUrl,
                        isOriginal = (i == 0),
                        format = container,
                        bitrate = bitrate
                    )
                )
            }
        }

        if (resolutionOptions.isEmpty() && (!isLive || hlsUrl.isNullOrBlank())) {
            return null
        }

        val selectedResolution = resolutionOptions.firstOrNull { it.height == 720 }
            ?: resolutionOptions.firstOrNull { it.height == 1080 }
            ?: resolutionOptions.firstOrNull()
            ?: VideoResolutionOption(
                label = if (isLive) "Live" else "Auto",
                height = 720,
                videoUrl = if (isLive) hlsUrl.orEmpty() else (resolutionOptions.firstOrNull()?.videoUrl ?: ""),
                isVideoOnly = false,
                audioUrl = null,
                isHls = isLive && !hlsUrl.isNullOrBlank()
            )

        val relatedVideos = mutableListOf<VideoItem>()
        val recommendedArray = json.optJSONArray("recommendedVideos")
        if (recommendedArray != null) {
            for (i in 0 until recommendedArray.length()) {
                val recObj = recommendedArray.optJSONObject(i) ?: continue
                val recId = recObj.optString("videoId")
                if (recId.isBlank()) continue
                val recTitle = recObj.optString("title")
                val recAuthor = recObj.optString("author")
                val recThumbnails = recObj.optJSONArray("videoThumbnails")
                val recThumb = if (recThumbnails != null && recThumbnails.length() > 0) {
                    recThumbnails.optJSONObject(0)?.optString("url").orEmpty()
                } else {
                    "https://i.ytimg.com/vi/$recId/hqdefault.jpg"
                }
                val recDuration = recObj.optLong("lengthSeconds", 0L)
                val recViews = recObj.optLong("viewCount", 0L)

                relatedVideos.add(
                    VideoItem(
                        id = recId,
                        title = recTitle,
                        uploaderName = recAuthor,
                        uploaderUrl = recObj.optString("authorUrl"),
                        uploaderAvatarUrl = "",
                        durationText = TextFormatters.formatDuration(recDuration),
                        thumbnailUrl = recThumb,
                        url = "https://www.youtube.com/watch?v=$recId",
                        viewCount = recViews,
                        viewCountText = TextFormatters.formatViewCount(recViews),
                        uploadDateMillis = 0L,
                        uploadDateText = ""
                    )
                )
            }
        }

        return StreamData(
            videoId = videoId,
            title = title,
            uploaderName = author,
            uploaderUrl = authorUrl,
            uploaderAvatarUrl = bestAvatar,
            uploaderSubscriberCountText = subCountText,
            streamUrl = selectedResolution.videoUrl,
            isHls = selectedResolution.isHls,
            description = description,
            viewCountText = viewText,
            uploadDateText = publishedText,
            relatedVideos = relatedVideos,
            availableResolutions = resolutionOptions,
            selectedResolution = selectedResolution,
            availableAudioTracks = audioOptions,
            selectedAudioTrack = audioOptions.firstOrNull()
        )
    }
}
