package com.example.yoouutt.ui.theme

enum class AppTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val hasHologramBackdrop: Boolean,
    val hologramAlpha: Float,
    val isLight: Boolean = false
) {
    CYBERPUNK(
        id = "CYBERPUNK",
        displayName = "Cyberpunk Neo-Tokyo",
        description = "Void Black, Electric Cyan, Neon Magenta & Hologram Youno-chan",
        hasHologramBackdrop = true,
        hologramAlpha = 0.35f
    ),
    MATTE_BLACK(
        id = "MATTE_BLACK",
        displayName = "Hitam Doff (Stealth AMOLED)",
        description = "Hitam murni pekat, aksen YouTube Red klasik, tanpa hologram",
        hasHologramBackdrop = false,
        hologramAlpha = 0f
    ),
    LIGHT(
        id = "LIGHT",
        displayName = "Putih Bersih (Clean Light)",
        description = "Off-White modern, kontras teks tinggi, tanpa hologram",
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        isLight = true
    ),
    SAKURA_LOFI(
        id = "SAKURA_LOFI",
        displayName = "Sakura Lo-Fi (Pastel Chill)",
        description = "Deep Muted Plum, Sakura Blossom Pink, Pastel Lavender",
        hasHologramBackdrop = true,
        hologramAlpha = 0.22f
    ),
    CITY_POP_SUNSET(
        id = "CITY_POP_SUNSET",
        displayName = "City Pop Sunset (Tokyo 80s)",
        description = "Midnight Indigo, Sunset Amber Orange, Golden Yellow",
        hasHologramBackdrop = true,
        hologramAlpha = 0.22f
    ),
    MECHA_TACTICAL(
        id = "MECHA_TACTICAL",
        displayName = "Mecha Tactical (EVA Sci-Fi)",
        description = "Gunmetal Slate, Acid Neon Green, Tactical Warning Amber",
        hasHologramBackdrop = true,
        hologramAlpha = 0.22f
    );

    companion object {
        fun fromId(id: String): AppTheme = entries.find { it.id.equals(id, ignoreCase = true) } ?: CYBERPUNK
    }
}
