package com.example.yoouutt.util

import androidx.core.text.HtmlCompat
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Locale

object TextFormatters {

    fun cleanHtmlDescription(raw: String): String {
        if (raw.isBlank()) return ""
        return try {
            HtmlCompat.fromHtml(raw, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
        } catch (_: Exception) {
            raw.replace(Regex("<[^>]*>"), "").trim()
        }
    }

    fun formatDuration(durationSeconds: Long): String {
        if (durationSeconds <= 0) return ""
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        }
    }

    fun formatTimeMs(timeMs: Long): String {
        val totalSeconds = (timeMs / 1000).coerceAtLeast(0)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    fun formatViewCount(views: Long): String {
        if (views <= 0) return ""
        return when {
            views >= 1_000_000_000 -> {
                val value = views / 1_000_000_000.0
                if (value >= 10.0) String.format(Locale.getDefault(), "%.0f M x", value)
                else String.format(Locale.getDefault(), "%.1f M x", value)
            }
            views >= 1_000_000 -> {
                val value = views / 1_000_000.0
                if (value >= 10.0) String.format(Locale.getDefault(), "%.0f jt x", value)
                else String.format(Locale.getDefault(), "%.1f jt x", value)
            }
            views >= 1_000 -> {
                val value = views / 1_000.0
                if (value >= 10.0) String.format(Locale.getDefault(), "%.0f rb x", value)
                else String.format(Locale.getDefault(), "%.1f rb x", value)
            }
            else -> "$views x"
        }
    }

    fun formatSubscriberCount(subscribers: Long): String {
        if (subscribers <= 0) return ""
        return when {
            subscribers >= 1_000_000 -> {
                val value = subscribers / 1_000_000.0
                if (value >= 100.0) String.format(Locale.getDefault(), "%.0f jt subscriber", value)
                else String.format(Locale.getDefault(), "%.1f jt subscriber", value)
            }
            subscribers >= 1_000 -> {
                val value = subscribers / 1_000.0
                if (value >= 100.0) String.format(Locale.getDefault(), "%.0f rb subscriber", value)
                else String.format(Locale.getDefault(), "%.1f rb subscriber", value)
            }
            else -> "$subscribers subscriber"
        }
    }

    fun formatUploadDate(uploadDateMillis: Long?, textualDate: String?): String {
        if (uploadDateMillis != null && uploadDateMillis > 0L) {
            val relative = formatRelativeTime(uploadDateMillis)
            if (relative.isNotBlank()) return relative
        }

        val rawText = textualDate.orEmpty().trim()
        if (rawText.isBlank()) return ""

        try {
            val offsetDateTime = OffsetDateTime.parse(rawText)
            return formatRelativeTime(offsetDateTime.toInstant().toEpochMilli())
        } catch (_: Exception) {
        }

        try {
            val instant = Instant.parse(rawText)
            return formatRelativeTime(instant.toEpochMilli())
        } catch (_: Exception) {
        }

        return translateEnglishRelativeDate(rawText)
    }

    fun formatRelativeTime(millis: Long): String {
        if (millis <= 0L) return ""
        val diff = System.currentTimeMillis() - millis
        if (diff < 0L) return "Baru saja"

        val seconds = diff / 1000L
        val minutes = seconds / 60L
        val hours = minutes / 60L
        val days = hours / 24L
        val weeks = days / 7L
        val months = days / 30L
        val years = days / 365L

        return when {
            years > 0 -> "$years tahun lalu"
            months > 0 -> "$months bulan lalu"
            weeks > 0 -> "$weeks minggu lalu"
            days > 0 -> "$days hari lalu"
            hours > 0 -> "$hours jam lalu"
            minutes > 0 -> "$minutes menit lalu"
            else -> "Baru saja"
        }
    }

    fun translateEnglishRelativeDate(text: String): String {
        var result = text
        val replacements = listOf(
            "years ago" to "tahun lalu",
            "year ago" to "tahun lalu",
            "months ago" to "bulan lalu",
            "month ago" to "bulan lalu",
            "weeks ago" to "minggu lalu",
            "week ago" to "minggu lalu",
            "days ago" to "hari lalu",
            "day ago" to "hari lalu",
            "hours ago" to "jam lalu",
            "hour ago" to "jam lalu",
            "minutes ago" to "menit lalu",
            "minute ago" to "menit lalu",
            "seconds ago" to "detik lalu",
            "second ago" to "detik lalu",
            "Streamed" to "Disiarkan",
            "streamed" to "disiarkan"
        )
        for ((eng, id) in replacements) {
            if (result.contains(eng, ignoreCase = true)) {
                result = result.replace(Regex("(?i)" + Regex.escape(eng)), id)
            }
        }
        return result.trim()
    }

    fun parseTextualDateToMillis(text: String): Long {
        if (text.isBlank()) return 0L
        val now = System.currentTimeMillis()
        val lower = text.lowercase()

        val number = lower.filter { it.isDigit() }.toLongOrNull() ?: 1L
        val oneHour = 3_600_000L
        val oneDay = 24 * oneHour

        return when {
            lower.contains("second") || lower.contains("detik") -> now - (number * 1000L)
            lower.contains("minute") || lower.contains("menit") -> now - (number * 60_000L)
            lower.contains("hour") || lower.contains("jam") -> now - (number * oneHour)
            lower.contains("day") || lower.contains("hari") -> now - (number * oneDay)
            lower.contains("week") || lower.contains("minggu") -> now - (number * 7 * oneDay)
            lower.contains("month") || lower.contains("bulan") -> now - (number * 30 * oneDay)
            lower.contains("year") || lower.contains("tahun") -> now - (number * 365 * oneDay)
            else -> now
        }
    }
}
