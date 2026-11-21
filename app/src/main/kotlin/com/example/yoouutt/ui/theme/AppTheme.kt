package com.example.yoouutt.ui.theme

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.yoouutt.R

enum class ThemeCategory(
    @get:StringRes val titleRes: Int
) {
    STANDAR(R.string.theme_category_standard),
    ANIME(R.string.theme_category_anime),
    DYNAMIC(R.string.theme_category_dynamic),
    TIME_ADAPTIVE(R.string.theme_category_time_adaptive),
    SPECIAL(R.string.theme_category_special)
}

enum class AppTheme(
    val id: String,
    val category: ThemeCategory,
    @get:StringRes val titleRes: Int,
    @get:StringRes val descriptionRes: Int,
    val hasHologramBackdrop: Boolean,
    val hologramAlpha: Float,
    val isLight: Boolean = false,
    @get:DrawableRes val backdropResId: Int? = null,
    val displayName: String = "",
    val description: String = ""
) {
    CYBERPUNK(
        id = "CYBERPUNK",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_cyberpunk_name,
        descriptionRes = R.string.theme_cyberpunk_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.32f,
        backdropResId = R.drawable.youno_backdrop,
        displayName = "Cyberpunk Neo-Tokyo",
        description = "Void Black, Electric Cyan, Neon Magenta & Hologram Youno-chan"
    ),
    MATTE_BLACK(
        id = "MATTE_BLACK",
        category = ThemeCategory.STANDAR,
        titleRes = R.string.theme_matte_black_name,
        descriptionRes = R.string.theme_matte_black_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        backdropResId = null,
        displayName = "Hitam Doff (Stealth AMOLED)",
        description = "Hitam murni pekat, aksen YouTube Red klasik, tanpa hologram"
    ),
    LIGHT(
        id = "LIGHT",
        category = ThemeCategory.STANDAR,
        titleRes = R.string.theme_light_name,
        descriptionRes = R.string.theme_light_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        isLight = true,
        backdropResId = null,
        displayName = "Putih Bersih (Clean Light)",
        description = "Off-White modern, kontras teks tinggi, tanpa hologram"
    ),
    SAKURA_LOFI(
        id = "SAKURA_LOFI",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_sakura_name,
        descriptionRes = R.string.theme_sakura_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.28f,
        backdropResId = R.drawable.youno_backdrop_sakura,
        displayName = "Sakura Lo-Fi (Pastel Chill)",
        description = "Deep Muted Plum, Sakura Blossom Pink, Pastel Lavender"
    ),
    CITY_POP_SUNSET(
        id = "CITY_POP_SUNSET",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_city_pop_name,
        descriptionRes = R.string.theme_city_pop_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.28f,
        backdropResId = R.drawable.youno_backdrop_citypop,
        displayName = "City Pop Sunset (Tokyo 80s)",
        description = "Midnight Indigo, Sunset Amber Orange, Golden Yellow"
    ),
    MECHA_TACTICAL(
        id = "MECHA_TACTICAL",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_mecha_name,
        descriptionRes = R.string.theme_mecha_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.28f,
        backdropResId = R.drawable.youno_backdrop_mecha,
        displayName = "Mecha Tactical (EVA Sci-Fi)",
        description = "Gunmetal Slate, Acid Neon Green, Tactical Warning Amber"
    ),
    NATSU_UMI(
        id = "NATSU_UMI",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_natsu_umi_name,
        descriptionRes = R.string.theme_natsu_umi_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.22f,
        isLight = true,
        backdropResId = R.drawable.youno_backdrop_natsu,
        displayName = "Natsu Umi (Summer Sea)",
        description = "Sunlit cyan, white sand, and ocean seafoam"
    ),
    MOMIJI_AUTUMN(
        id = "MOMIJI_AUTUMN",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_momiji_name,
        descriptionRes = R.string.theme_momiji_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.28f,
        backdropResId = R.drawable.youno_backdrop_momiji,
        displayName = "Momiji Autumn (Maple Foliage)",
        description = "Warm autumn crimson, deep charcoal, and golden amber"
    ),
    FUYU_YUKI(
        id = "FUYU_YUKI",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_fuyu_yuki_name,
        descriptionRes = R.string.theme_fuyu_yuki_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.22f,
        isLight = true,
        backdropResId = R.drawable.youno_backdrop_fuyu,
        displayName = "Fuyu Yuki (Winter Frost)",
        description = "Crisp ice white, pale frosted blue, and silver mist"
    ),
    MATCHA_ZEN(
        id = "MATCHA_ZEN",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_matcha_name,
        descriptionRes = R.string.theme_matcha_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.22f,
        isLight = true,
        backdropResId = R.drawable.youno_backdrop_matcha,
        displayName = "Matcha Zen (Tea House)",
        description = "Earthy sage green, matcha foam cream, and warm cedar wood"
    ),
    OCEAN_ABYSS(
        id = "OCEAN_ABYSS",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_ocean_abyss_name,
        descriptionRes = R.string.theme_ocean_abyss_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.30f,
        backdropResId = R.drawable.youno_backdrop_abyss,
        displayName = "Ocean Abyss (Bioluminescent)",
        description = "Abyssal midnight navy, glowing neon aqua, and deep sea teal"
    ),
    SUMIE_INK(
        id = "SUMIE_INK",
        category = ThemeCategory.ANIME,
        titleRes = R.string.theme_sumie_name,
        descriptionRes = R.string.theme_sumie_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.30f,
        backdropResId = R.drawable.youno_backdrop_sumie,
        displayName = "Sumi-e Ink (Traditional Mono)",
        description = "Monochrome charcoal brushstrokes, rice paper, and crimson seal"
    ),
    DRACULA(
        id = "DRACULA",
        category = ThemeCategory.SPECIAL,
        titleRes = R.string.theme_dracula_name,
        descriptionRes = R.string.theme_dracula_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Dracula Developer",
        description = "Vampire purple, neon pink and cyan palette"
    ),
    NORD(
        id = "NORD",
        category = ThemeCategory.SPECIAL,
        titleRes = R.string.theme_nord_name,
        descriptionRes = R.string.theme_nord_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Nord Arctic",
        description = "Arctic blue and slate frost calm aesthetic"
    ),
    CATPPUCCIN_MOCHA(
        id = "CATPPUCCIN_MOCHA",
        category = ThemeCategory.SPECIAL,
        titleRes = R.string.theme_catppuccin_name,
        descriptionRes = R.string.theme_catppuccin_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Catppuccin Mocha",
        description = "Warm dark pastel tones, soothing and cozy"
    ),
    RETRO_PIXEL(
        id = "RETRO_PIXEL",
        category = ThemeCategory.SPECIAL,
        titleRes = R.string.theme_retro_pixel_name,
        descriptionRes = R.string.theme_retro_pixel_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Retro Pixel (Game Boy)",
        description = "Classic 4-tone monochrome greenish handheld display"
    ),
    TERMINAL_HACKER(
        id = "TERMINAL_HACKER",
        category = ThemeCategory.SPECIAL,
        titleRes = R.string.theme_terminal_name,
        descriptionRes = R.string.theme_terminal_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Terminal Hacker CRT",
        description = "Phosphor green text on deep cathode ray black"
    ),
    VAPORWAVE(
        id = "VAPORWAVE",
        category = ThemeCategory.SPECIAL,
        titleRes = R.string.theme_vaporwave_name,
        descriptionRes = R.string.theme_vaporwave_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Vaporwave Neon 90s",
        description = "Hot neon pink, cyan teal, and dreamy twilight indigo"
    ),
    SEPIA_MALAM(
        id = "SEPIA_MALAM",
        category = ThemeCategory.SPECIAL,
        titleRes = R.string.theme_sepia_malam_name,
        descriptionRes = R.string.theme_sepia_malam_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Sepia Malam (Warm Cocoa)",
        description = "Warm espresso amber, low blue light for night watching"
    ),
    KONTRAS_TINGGI(
        id = "KONTRAS_TINGGI",
        category = ThemeCategory.SPECIAL,
        titleRes = R.string.theme_kontras_tinggi_name,
        descriptionRes = R.string.theme_kontras_tinggi_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Kontras Tinggi (Accessibility)",
        description = "Maximum legibility with pitch black and vivid yellow"
    ),
    AMOLED_REDUP(
        id = "AMOLED_REDUP",
        category = ThemeCategory.SPECIAL,
        titleRes = R.string.theme_amoled_redup_name,
        descriptionRes = R.string.theme_amoled_redup_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "AMOLED Merah Redup (Night Vision)",
        description = "Deep night crimson on true black, zero blue light"
    ),
    DYNAMIC_DARK(
        id = "DYNAMIC_DARK",
        category = ThemeCategory.DYNAMIC,
        titleRes = R.string.theme_dynamic_dark_name,
        descriptionRes = R.string.theme_dynamic_dark_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Dynamic Material Dark",
        description = "Material 3 dark palette automatically adapted from device wallpaper"
    ),
    DYNAMIC_AMOLED(
        id = "DYNAMIC_AMOLED",
        category = ThemeCategory.DYNAMIC,
        titleRes = R.string.theme_dynamic_amoled_name,
        descriptionRes = R.string.theme_dynamic_amoled_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Dynamic AMOLED Black",
        description = "True pitch black background with dynamic wallpaper accents"
    ),
    DYNAMIC_LIGHT(
        id = "DYNAMIC_LIGHT",
        category = ThemeCategory.DYNAMIC,
        titleRes = R.string.theme_dynamic_light_name,
        descriptionRes = R.string.theme_dynamic_light_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        isLight = true,
        displayName = "Dynamic Material Light",
        description = "Clean light surface dynamically matching device wallpaper"
    ),
    DYNAMIC_VIBRANT(
        id = "DYNAMIC_VIBRANT",
        category = ThemeCategory.DYNAMIC,
        titleRes = R.string.theme_dynamic_vibrant_name,
        descriptionRes = R.string.theme_dynamic_vibrant_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Dynamic Vibrant Pulse",
        description = "High-saturation dynamic accents with deep contrast"
    ),
    DYNAMIC_FROSTED(
        id = "DYNAMIC_FROSTED",
        category = ThemeCategory.DYNAMIC,
        titleRes = R.string.theme_dynamic_frosted_name,
        descriptionRes = R.string.theme_dynamic_frosted_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Dynamic Frosted Glass",
        description = "Soft translucent acrylic surface with wallpaper-toned highlights"
    ),
    TIME_YOUNO_CYCLE(
        id = "TIME_YOUNO_CYCLE",
        category = ThemeCategory.TIME_ADAPTIVE,
        titleRes = R.string.theme_time_youno_name,
        descriptionRes = R.string.theme_time_youno_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.55f,
        backdropResId = R.drawable.youno_backdrop,
        displayName = "Youno-chan Anime Cycle",
        description = "Sakura morning, Matcha noon, City Pop dusk, Cyberpunk night, Abyss late night"
    ),
    TIME_CIRCADIAN(
        id = "TIME_CIRCADIAN",
        category = ThemeCategory.TIME_ADAPTIVE,
        titleRes = R.string.theme_time_circadian_name,
        descriptionRes = R.string.theme_time_circadian_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Circadian Eye Care Cycle",
        description = "Light morning, High Contrast noon, Warm Sepia dusk, Stealth Black night, Red Vision midnight"
    ),
    TIME_NATURE(
        id = "TIME_NATURE",
        category = ThemeCategory.TIME_ADAPTIVE,
        titleRes = R.string.theme_time_nature_name,
        descriptionRes = R.string.theme_time_nature_desc,
        hasHologramBackdrop = true,
        hologramAlpha = 0.5f,
        backdropResId = R.drawable.youno_backdrop_fuyu,
        displayName = "Four Seasons Nature Cycle",
        description = "Fuyu Yuki cool dawn, Matcha Zen afternoon, Momiji Autumn sunset, Sumi-e Ink night"
    ),
    TIME_RETRO(
        id = "TIME_RETRO",
        category = ThemeCategory.TIME_ADAPTIVE,
        titleRes = R.string.theme_time_retro_name,
        descriptionRes = R.string.theme_time_retro_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Digital Retro Neon Cycle",
        description = "Pixel daylight, Vaporwave 90s dusk, Dracula night, Terminal Hacker midnight"
    ),
    TIME_DYNAMIC(
        id = "TIME_DYNAMIC",
        category = ThemeCategory.TIME_ADAPTIVE,
        titleRes = R.string.theme_time_dynamic_name,
        descriptionRes = R.string.theme_time_dynamic_desc,
        hasHologramBackdrop = false,
        hologramAlpha = 0f,
        displayName = "Dynamic Monet Day-to-Night",
        description = "Dynamic Light daytime, Frosted Glass sunset, Material Dark night, AMOLED midnight"
    );

    companion object {
        fun fromId(id: String): AppTheme = entries.find { it.id.equals(id, ignoreCase = true) } ?: CYBERPUNK
    }
}
