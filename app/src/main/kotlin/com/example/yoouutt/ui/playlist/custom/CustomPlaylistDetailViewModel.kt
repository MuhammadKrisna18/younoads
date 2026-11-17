package com.example.yoouutt.ui.playlist.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoouutt.data.model.CustomPlaylist
import com.example.yoouutt.data.model.CustomPlaylistItem
import com.example.yoouutt.data.repository.CustomPlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CustomPlaylistDetailViewModel(
    val playlistId: Long,
    private val repository: CustomPlaylistRepository
) : ViewModel() {

    private val _playlist = MutableStateFlow<CustomPlaylist?>(null)
    val playlist: StateFlow<CustomPlaylist?> = _playlist.asStateFlow()

    val items: StateFlow<List<CustomPlaylistItem>> = repository.getPlaylistItems(playlistId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadPlaylistDetails()
    }

    private fun loadPlaylistDetails() {
        viewModelScope.launch {
            _playlist.value = repository.getPlaylistById(playlistId)
        }
    }

    fun removeItem(videoId: String) {
        viewModelScope.launch {
            repository.removeVideoFromPlaylist(playlistId, videoId)
            _playlist.value = repository.getPlaylistById(playlistId)
        }
    }

    fun deletePlaylist(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
            onDeleted()
        }
    }

    fun updatePlaylist(title: String, description: String) {
        viewModelScope.launch {
            repository.updatePlaylist(playlistId, title, description)
            _playlist.value = repository.getPlaylistById(playlistId)
        }
    }
}
