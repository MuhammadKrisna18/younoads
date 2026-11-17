package com.example.yoouutt.data.model

enum class DefaultVideoQuality(
    val code: String,
    val targetHeight: Int,
    val label: String
) {
    AUTO("auto", 0, "Otomatis"),
    P1080("1080p", 1080, "1080p"),
    P720("720p", 720, "720p"),
    P480("480p", 480, "480p"),
    P360("360p", 360, "360p");

    companion object {
        fun fromCode(code: String): DefaultVideoQuality =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: AUTO
    }
}
