package com.example.yoouutt.ui.language

import androidx.annotation.StringRes
import com.example.yoouutt.R

enum class AppLanguage(
    val code: String,
    @get:StringRes val titleRes: Int,
    @get:StringRes val descriptionRes: Int,
    val displayName: String = "",
    val description: String = ""
) {
    SYSTEM(
        code = "SYSTEM",
        titleRes = R.string.language_system_title,
        descriptionRes = R.string.language_system_desc,
        displayName = "Mengikuti Sistem",
        description = "Gunakan bahasa default perangkat"
    ),
    INDONESIAN(
        code = "in",
        titleRes = R.string.language_in_title,
        descriptionRes = R.string.language_in_desc,
        displayName = "Bahasa Indonesia",
        description = "Bahasa Indonesia baku dan natural"
    ),
    ENGLISH(
        code = "en",
        titleRes = R.string.language_en_title,
        descriptionRes = R.string.language_en_desc,
        displayName = "English",
        description = "Standard English interface"
    );

    companion object {
        fun fromCode(code: String): AppLanguage = entries.find { it.code.equals(code, ignoreCase = true) } ?: SYSTEM
    }
}
