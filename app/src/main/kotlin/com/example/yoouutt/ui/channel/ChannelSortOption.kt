package com.example.yoouutt.ui.channel

import androidx.annotation.StringRes
import com.example.yoouutt.R

enum class ChannelSortOption(@get:StringRes val titleRes: Int) {
    NEWEST(R.string.channel_sort_newest),
    POPULAR(R.string.channel_sort_popular),
    OLDEST(R.string.channel_sort_oldest)
}
