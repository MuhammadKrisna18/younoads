package com.example.yoouutt.playback

import com.example.yoouutt.data.model.PlaylistContext
import com.example.yoouutt.data.model.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaybackQueueManager {
    private val _playlistContext = MutableStateFlow<PlaylistContext?>(null)
    val playlistContext: StateFlow<PlaylistContext?> = _playlistContext.asStateFlow()

    fun setPlaylist(context: PlaylistContext, index: Int): VideoItem? {
        if (index !in context.videos.indices) return null
        val updatedContext = context.copy(currentIndex = index)
        _playlistContext.value = updatedContext
        return updatedContext.videos[index]
    }

    fun getNextTarget(): Pair<PlaylistContext, VideoItem>? {
        val current = _playlistContext.value ?: return null
        val nextIndex = current.currentIndex + 1
        if (nextIndex !in current.videos.indices) return null
        val updatedContext = current.copy(currentIndex = nextIndex)
        _playlistContext.value = updatedContext
        return Pair(updatedContext, updatedContext.videos[nextIndex])
    }

    fun getPreviousTarget(): Pair<PlaylistContext, VideoItem>? {
        val current = _playlistContext.value ?: return null
        val prevIndex = current.currentIndex - 1
        if (prevIndex !in current.videos.indices) return null
        val updatedContext = current.copy(currentIndex = prevIndex)
        _playlistContext.value = updatedContext
        return Pair(updatedContext, updatedContext.videos[prevIndex])
    }

    fun clear() {
        _playlistContext.value = null
    }
}
