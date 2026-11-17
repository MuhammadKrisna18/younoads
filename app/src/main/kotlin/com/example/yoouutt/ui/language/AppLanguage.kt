package com.example.yoouutt.ui.language

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val description: String
) {
    SYSTEM(
        code = "SYSTEM",
        displayName = "Mengikuti Sistem",
        description = "Gunakan bahasa default perangkat"
    ),
    INDONESIAN(
        code = "in",
        displayName = "Bahasa Indonesia",
        description = "Bahasa Indonesia baku dan natural"
    ),
    ENGLISH(
        code = "en",
        displayName = "English",
        description = "Standard English interface"
    );

    companion object {
        fun fromCode(code: String): AppLanguage = entries.find { it.code.equals(code, ignoreCase = true) } ?: SYSTEM
    }
}
