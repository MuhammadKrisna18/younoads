package com.example.yoouutt

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Main : NavKey

@Serializable
data object HistoryKey : NavKey

@Serializable
data object SettingsKey : NavKey

@Serializable
data object SavedVideosKey : NavKey

@Serializable
data object PlaylistsKey : NavKey

@Serializable
data class CustomPlaylistDetailKey(
    val playlistId: Long,
    val playlistTitle: String
) : NavKey

@Serializable
data class PlayerKey(
    val videoUrl: String,
    val title: String,
    val uploader: String
) : NavKey

@Serializable
data class ChannelKey(
    val channelUrl: String,
    val channelName: String
) : NavKey

@Serializable
data class PlaylistKey(
    val playlistUrl: String,
    val playlistTitle: String = ""
) : NavKey
