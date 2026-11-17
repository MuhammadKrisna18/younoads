package com.example.yoouutt.data.model

import androidx.annotation.StringRes
import com.example.yoouutt.R

enum class DefaultVideoQuality(
    val code: String,
    val targetHeight: Int,
    @get:StringRes val titleRes: Int,
    @get:StringRes val descRes: Int
) {
    AUTO("auto", 0, R.string.quality_auto_title, R.string.quality_auto_desc),
    P1080("1080p", 1080, R.string.quality_1080p_title, R.string.quality_1080p_desc),
    P720("720p", 720, R.string.quality_720p_title, R.string.quality_720p_desc),
    P480("480p", 480, R.string.quality_480p_title, R.string.quality_480p_desc),
    P360("360p", 360, R.string.quality_360p_title, R.string.quality_360p_desc);

    companion object {
        fun fromCode(code: String): DefaultVideoQuality =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: AUTO
    }
}
