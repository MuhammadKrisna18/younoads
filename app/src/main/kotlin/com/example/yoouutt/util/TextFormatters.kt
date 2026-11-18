package com.example.yoouutt.util

import androidx.core.text.HtmlCompat
import com.example.yoouutt.ui.language.LanguageManager
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Locale

object TextFormatters {

    private fun isIndonesianLocale(): Boolean {
        return try {
            val locale = LanguageManager.resolveLocale(LanguageManager.currentLanguage.value)
            locale.language.equals("in", ignoreCase = true) || locale.language.equals("id", ignoreCase = true)
        } catch (_: Exception) {
            Locale.getDefault().language.equals("in", ignoreCase = true) || Locale.getDefault().language.equals("id", ignoreCase = true)
        }
    }

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
        val isIndo = isIndonesianLocale()
        return when {
            views >= 1_000_000_000 -> {
                val value = views / 1_000_000_000.0
                if (isIndo) {
                    if (value >= 10.0) String.format(Locale.getDefault(), "%.0f M x", value)
                    else String.format(Locale.getDefault(), "%.1f M x", value)
                } else {
                    if (value >= 10.0) String.format(Locale.getDefault(), "%.0fB views", value)
                    else String.format(Locale.getDefault(), "%.1fB views", value)
                }
            }
            views >= 1_000_000 -> {
                val value = views / 1_000_000.0
                if (isIndo) {
                    if (value >= 10.0) String.format(Locale.getDefault(), "%.0f jt x", value)
                    else String.format(Locale.getDefault(), "%.1f jt x", value)
                } else {
                    if (value >= 10.0) String.format(Locale.getDefault(), "%.0fM views", value)
                    else String.format(Locale.getDefault(), "%.1fM views", value)
                }
            }
            views >= 1_000 -> {
                val value = views / 1_000.0
                if (isIndo) {
                    if (value >= 10.0) String.format(Locale.getDefault(), "%.0f rb x", value)
                    else String.format(Locale.getDefault(), "%.1f rb x", value)
                } else {
                    if (value >= 10.0) String.format(Locale.getDefault(), "%.0fK views", value)
                    else String.format(Locale.getDefault(), "%.1fK views", value)
                }
            }
            else -> {
                if (isIndo) "$views x"
                else if (views == 1L) "1 view"
                else "$views views"
            }
        }
    }

    fun formatSubscriberCount(subscribers: Long): String {
        if (subscribers <= 0) return ""
        val isIndo = isIndonesianLocale()
        return when {
            subscribers >= 1_000_000 -> {
                val value = subscribers / 1_000_000.0
                if (isIndo) {
                    if (value >= 100.0) String.format(Locale.getDefault(), "%.0f jt subscriber", value)
                    else String.format(Locale.getDefault(), "%.1f jt subscriber", value)
                } else {
                    if (value >= 100.0) String.format(Locale.getDefault(), "%.0fM subscribers", value)
                    else String.format(Locale.getDefault(), "%.1fM subscribers", value)
                }
            }
            subscribers >= 1_000 -> {
                val value = subscribers / 1_000.0
                if (isIndo) {
                    if (value >= 100.0) String.format(Locale.getDefault(), "%.0f rb subscriber", value)
                    else String.format(Locale.getDefault(), "%.1f rb subscriber", value)
                } else {
                    if (value >= 100.0) String.format(Locale.getDefault(), "%.0fK subscribers", value)
                    else String.format(Locale.getDefault(), "%.1fK subscribers", value)
                }
            }
            else -> {
                if (isIndo) "$subscribers subscriber"
                else if (subscribers == 1L) "1 subscriber"
                else "$subscribers subscribers"
            }
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
        val isIndo = isIndonesianLocale()
        if (diff < 0L) return if (isIndo) "Baru saja" else "Just now"

        val seconds = diff / 1000L
        val minutes = seconds / 60L
        val hours = minutes / 60L
        val days = hours / 24L
        val weeks = days / 7L
        val months = days / 30L
        val years = days / 365L

        return if (isIndo) {
            when {
                years > 0 -> "$years tahun lalu"
                months > 0 -> "$months bulan lalu"
                weeks > 0 -> "$weeks minggu lalu"
                days > 0 -> "$days hari lalu"
                hours > 0 -> "$hours jam lalu"
                minutes > 0 -> "$minutes menit lalu"
                else -> "Baru saja"
            }
        } else {
            when {
                years > 0 -> if (years == 1L) "1 year ago" else "$years years ago"
                months > 0 -> if (months == 1L) "1 month ago" else "$months months ago"
                weeks > 0 -> if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
                days > 0 -> if (days == 1L) "1 day ago" else "$days days ago"
                hours > 0 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
                minutes > 0 -> if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
                else -> "Just now"
            }
        }
    }

    fun translateEnglishRelativeDate(text: String): String {
        var result = text
        val isIndo = isIndonesianLocale()
        if (isIndo) {
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
        } else {
            val replacements = listOf(
                "tahun lalu" to "years ago",
                "bulan lalu" to "months ago",
                "minggu lalu" to "weeks ago",
                "hari lalu" to "days ago",
                "jam lalu" to "hours ago",
                "menit lalu" to "minutes ago",
                "detik lalu" to "seconds ago",
                "Baru saja" to "Just now",
                "baru saja" to "just now",
                "Disiarkan" to "Streamed",
                "disiarkan" to "streamed"
            )
            for ((id, eng) in replacements) {
                if (result.contains(id, ignoreCase = true)) {
                    result = result.replace(Regex("(?i)" + Regex.escape(id)), eng)
                }
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
