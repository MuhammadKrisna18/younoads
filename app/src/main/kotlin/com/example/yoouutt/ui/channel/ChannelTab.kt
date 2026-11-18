package com.example.yoouutt.ui.channel

import androidx.annotation.StringRes
import com.example.yoouutt.R

enum class ChannelTab(@get:StringRes val titleRes: Int) {
    VIDEOS(R.string.channel_tab_videos),
    PLAYLISTS(R.string.channel_tab_playlists)
}
