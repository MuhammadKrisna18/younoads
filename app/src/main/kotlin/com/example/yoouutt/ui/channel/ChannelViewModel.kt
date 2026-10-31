package com.example.yoouutt.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoouutt.data.model.ChannelData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.repository.NewPipeVideoRepository
import com.example.yoouutt.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ChannelUiState {
    data object Loading : ChannelUiState
    data class Success(
        val channel: ChannelData,
        val sortedVideos: List<VideoItem>,
        val currentSort: ChannelSortOption
    ) : ChannelUiState
    data class Error(val message: String) : ChannelUiState
}

class ChannelViewModel(
    private val videoRepository: VideoRepository = NewPipeVideoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChannelUiState>(ChannelUiState.Loading)
    val uiState: StateFlow<ChannelUiState> = _uiState.asStateFlow()

    private var currentChannel: ChannelData? = null
    private var activeSort: ChannelSortOption = ChannelSortOption.NEWEST

    fun loadChannel(channelUrl: String) {
        viewModelScope.launch {
            _uiState.value = ChannelUiState.Loading
            videoRepository.getChannelDetails(channelUrl)
                .onSuccess { data ->
                    currentChannel = data
                    applySort(activeSort)
                }
                .onFailure { error ->
                    _uiState.value = ChannelUiState.Error(error.localizedMessage ?: "Gagal memuat profil channel")
                }
        }
    }

    fun onSortSelected(option: ChannelSortOption) {
        activeSort = option
        applySort(option)
    }

    private fun applySort(option: ChannelSortOption) {
        val channel = currentChannel ?: return
        val sorted = when (option) {
            ChannelSortOption.NEWEST -> {
                channel.videos.sortedWith(
                    compareByDescending<VideoItem> { it.uploadDateMillis }
                        .thenByDescending { it.viewCount }
                )
            }
            ChannelSortOption.OLDEST -> {
                channel.videos.sortedWith(
                    compareBy<VideoItem> { it.uploadDateMillis }
                        .thenBy { it.viewCount }
                )
            }
            ChannelSortOption.POPULAR -> {
                channel.videos.sortedByDescending { it.viewCount }
            }
        }
        _uiState.value = ChannelUiState.Success(
            channel = channel,
            sortedVideos = sorted,
            currentSort = option
        )
    }
}

