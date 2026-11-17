package com.example.yoouutt.ui.theme

import androidx.annotation.DrawableRes
import com.example.yoouutt.R

enum class AppTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val hasHologramBackdrop: Boolean,
    val hologramAlpha: Float,
    val isLight: Boolean = false,
    @get:DrawableRes val backdropResId: Int? = null
) {
    CYBERPUNK(
        id = "CYBERPUNK",
        displayName = "Cyberpunk Neo-Tokyo",
        description = "Void Black, Electric Cyan, Neon Magenta & Hologram Youno-chan",
        hasHologramBackdrop = true,
        hologramAlpha = 0.32f,
        backdropResId = R.drawable.youno_backdrop
    ),
    MATTE_BLACK(
        id = "MATTE_BLACK",
        displayName = "Hitam Doff (Stealth AMOLED)",
        description = "Hitam murni pekat, aksen YouTube Red klasik, tanpa hologram",
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        backdropResId = null
    ),
    LIGHT(
        id = "LIGHT",
        displayName = "Putih Bersih (Clean Light)",
        description = "Off-White modern, kontras teks tinggi, tanpa hologram",
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        isLight = true,
        backdropResId = null
    ),
    SAKURA_LOFI(
        id = "SAKURA_LOFI",
        displayName = "Sakura Lo-Fi (Pastel Chill)",
        description = "Deep Muted Plum, Sakura Blossom Pink, Pastel Lavender",
        hasHologramBackdrop = true,
        hologramAlpha = 0.28f,
        backdropResId = R.drawable.youno_backdrop_sakura
    ),
    CITY_POP_SUNSET(
        id = "CITY_POP_SUNSET",
        displayName = "City Pop Sunset (Tokyo 80s)",
        description = "Midnight Indigo, Sunset Amber Orange, Golden Yellow",
        hasHologramBackdrop = true,
        hologramAlpha = 0.28f,
        backdropResId = R.drawable.youno_backdrop_citypop
    ),
    MECHA_TACTICAL(
        id = "MECHA_TACTICAL",
        displayName = "Mecha Tactical (EVA Sci-Fi)",
        description = "Gunmetal Slate, Acid Neon Green, Tactical Warning Amber",
        hasHologramBackdrop = true,
        hologramAlpha = 0.28f,
        backdropResId = R.drawable.youno_backdrop_mecha
    );

    companion object {
        fun fromId(id: String): AppTheme = entries.find { it.id.equals(id, ignoreCase = true) } ?: CYBERPUNK
    }
}
