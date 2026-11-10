package com.example.yoouutt.data.model

sealed interface FeedItem {
    val id: String

    data class Video(val video: VideoItem) : FeedItem {
        override val id: String get() = video.id
    }

    data class Playlist(val playlist: PlaylistItem) : FeedItem {
        override val id: String get() = "playlist_${playlist.id}"
    }
}

