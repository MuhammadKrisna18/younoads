package com.example.yoouutt.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoouutt.data.model.PlaylistData
import com.example.yoouutt.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PlaylistUiState {
    data object Loading : PlaylistUiState
    data class Success(val playlist: PlaylistData) : PlaylistUiState
    data class Error(val message: String) : PlaylistUiState
}

class PlaylistViewModel(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistUiState>(PlaylistUiState.Loading)
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    private var currentPlaylistUrl: String = ""

    fun loadPlaylist(playlistUrl: String) {
        if (currentPlaylistUrl == playlistUrl && _uiState.value is PlaylistUiState.Success) {
            return
        }
        currentPlaylistUrl = playlistUrl
        viewModelScope.launch {
            _uiState.value = PlaylistUiState.Loading
            videoRepository.getPlaylistDetails(playlistUrl)
                .onSuccess { data ->
                    _uiState.value = PlaylistUiState.Success(data)
                }
                .onFailure { error ->
                    _uiState.value = PlaylistUiState.Error(error.localizedMessage ?: "Gagal memuat playlist")
                }
        }
    }
}

