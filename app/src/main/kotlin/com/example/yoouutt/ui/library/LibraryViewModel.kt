package com.example.yoouutt.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoouutt.data.model.CustomPlaylist
import com.example.yoouutt.data.model.SavedVideo
import com.example.yoouutt.data.repository.CustomPlaylistRepository
import com.example.yoouutt.data.repository.SavedVideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val savedVideoRepository: SavedVideoRepository,
    private val customPlaylistRepository: CustomPlaylistRepository
) : ViewModel() {

    val savedVideos: StateFlow<List<SavedVideo>> = savedVideoRepository.getSavedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customPlaylists: StateFlow<List<CustomPlaylist>> = customPlaylistRepository.getCustomPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isCreatePlaylistDialogVisible = MutableStateFlow(false)
    val isCreatePlaylistDialogVisible: StateFlow<Boolean> = _isCreatePlaylistDialogVisible.asStateFlow()

    fun unsaveVideo(videoId: String) {
        viewModelScope.launch {
            savedVideoRepository.removeSavedVideo(videoId)
        }
    }

    fun clearAllSaved() {
        viewModelScope.launch {
            savedVideoRepository.clearAllSavedVideos()
        }
    }

    fun showCreatePlaylistDialog() {
        _isCreatePlaylistDialogVisible.value = true
    }

    fun dismissCreatePlaylistDialog() {
        _isCreatePlaylistDialogVisible.value = false
    }

    fun createPlaylist(title: String, description: String = "") {
        viewModelScope.launch {
            customPlaylistRepository.createPlaylist(title, description)
            _isCreatePlaylistDialogVisible.value = false
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            customPlaylistRepository.deletePlaylist(playlistId)
        }
    }
}
