package com.example.yoouutt.ui.icon

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.yoouutt.R

enum class AppIcon(
    val id: String,
    val aliasName: String?,
    @get:StringRes val titleRes: Int,
    @get:StringRes val descriptionRes: Int,
    @get:DrawableRes val previewResId: Int
) {
    DEFAULT(
        id = "DEFAULT",
        aliasName = null,
        titleRes = R.string.app_icon_default_name,
        descriptionRes = R.string.app_icon_default_desc,
        previewResId = R.drawable.ic_app_icon_preview_default
    ),
    CYBERPUNK(
        id = "CYBERPUNK",
        aliasName = "com.example.yoouutt.MainActivityCyberpunk",
        titleRes = R.string.app_icon_cyberpunk_name,
        descriptionRes = R.string.app_icon_cyberpunk_desc,
        previewResId = R.drawable.ic_app_icon_preview_cyberpunk
    ),
    SAKURA(
        id = "SAKURA",
        aliasName = "com.example.yoouutt.MainActivitySakura",
        titleRes = R.string.app_icon_sakura_name,
        descriptionRes = R.string.app_icon_sakura_desc,
        previewResId = R.drawable.ic_app_icon_preview_sakura
    ),
    STEALTH(
        id = "STEALTH",
        aliasName = "com.example.yoouutt.MainActivityStealth",
        titleRes = R.string.app_icon_stealth_name,
        descriptionRes = R.string.app_icon_stealth_desc,
        previewResId = R.drawable.ic_app_icon_preview_stealth
    ),
    LIGHT(
        id = "LIGHT",
        aliasName = "com.example.yoouutt.MainActivityLight",
        titleRes = R.string.app_icon_light_name,
        descriptionRes = R.string.app_icon_light_desc,
        previewResId = R.drawable.ic_app_icon_preview_light
    ),
    YOUNO(
        id = "YOUNO",
        aliasName = "com.example.yoouutt.MainActivityYouno",
        titleRes = R.string.app_icon_youno_name,
        descriptionRes = R.string.app_icon_youno_desc,
        previewResId = R.drawable.ic_app_icon_preview_youno
    ),
    RETRO(
        id = "RETRO",
        aliasName = "com.example.yoouutt.MainActivityRetro",
        titleRes = R.string.app_icon_retro_name,
        descriptionRes = R.string.app_icon_retro_desc,
        previewResId = R.drawable.ic_app_icon_preview_retro
    ),
    CITY_POP(
        id = "CITY_POP",
        aliasName = "com.example.yoouutt.MainActivityCityPop",
        titleRes = R.string.app_icon_citypop_name,
        descriptionRes = R.string.app_icon_citypop_desc,
        previewResId = R.drawable.ic_app_icon_preview_citypop
    ),
    MECHA(
        id = "MECHA",
        aliasName = "com.example.yoouutt.MainActivityMecha",
        titleRes = R.string.app_icon_mecha_name,
        descriptionRes = R.string.app_icon_mecha_desc,
        previewResId = R.drawable.ic_app_icon_preview_mecha
    ),
    TERMINAL(
        id = "TERMINAL",
        aliasName = "com.example.yoouutt.MainActivityTerminal",
        titleRes = R.string.app_icon_terminal_name,
        descriptionRes = R.string.app_icon_terminal_desc,
        previewResId = R.drawable.ic_app_icon_preview_terminal
    );

    companion object {
        fun fromId(id: String): AppIcon {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: DEFAULT
        }
    }
}
