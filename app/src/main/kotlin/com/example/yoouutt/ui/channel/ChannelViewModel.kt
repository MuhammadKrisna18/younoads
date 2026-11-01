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
        val currentSort: ChannelSortOption,
        val isLoadingMore: Boolean = false,
        val searchQuery: String = ""
    ) : ChannelUiState
    data class Error(val message: String) : ChannelUiState
}

class ChannelViewModel(
    private val videoRepository: VideoRepository = NewPipeVideoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChannelUiState>(ChannelUiState.Loading)
    val uiState: StateFlow<ChannelUiState> = _uiState.asStateFlow()

    private var currentChannel: ChannelData? = null
    private val allLoadedVideos = mutableListOf<VideoItem>()
    private var activeSort: ChannelSortOption = ChannelSortOption.NEWEST
    private var currentSearchQuery = ""
    private var isLoadingMore = false

    fun loadChannel(channelUrl: String) {
        viewModelScope.launch {
            _uiState.value = ChannelUiState.Loading
            allLoadedVideos.clear()
            videoRepository.getChannelDetails(channelUrl)
                .onSuccess { data ->
                    currentChannel = data
                    allLoadedVideos.addAll(data.videos)
                    applySortAndFilter()
                }
                .onFailure { error ->
                    _uiState.value = ChannelUiState.Error(error.localizedMessage ?: "Gagal memuat profil channel")
                }
        }
    }

    fun loadMore() {
        if (isLoadingMore || currentChannel == null) return
        isLoadingMore = true
        updateLoadingMoreState(true)

        viewModelScope.launch {
            videoRepository.getMoreChannelVideos()
                .onSuccess { moreVideos ->
                    if (moreVideos.isNotEmpty()) {
                        val newItems = moreVideos.filter { newVid -> allLoadedVideos.none { it.id == newVid.id } }
                        allLoadedVideos.addAll(newItems)
                    }
                    isLoadingMore = false
                    applySortAndFilter()
                }
                .onFailure {
                    isLoadingMore = false
                    updateLoadingMoreState(false)
                }
        }
    }

    fun onSortSelected(option: ChannelSortOption) {
        activeSort = option
        applySortAndFilter()
    }

    fun onSearchQueryChanged(query: String) {
        currentSearchQuery = query
        applySortAndFilter()
    }

    private fun updateLoadingMoreState(loading: Boolean) {
        val current = _uiState.value
        if (current is ChannelUiState.Success) {
            _uiState.value = current.copy(isLoadingMore = loading)
        }
    }

    private fun applySortAndFilter() {
        val channel = currentChannel ?: return
        val filtered = if (currentSearchQuery.isBlank()) {
            allLoadedVideos
        } else {
            allLoadedVideos.filter {
                it.title.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        val sorted = when (activeSort) {
            ChannelSortOption.NEWEST -> {
                filtered.sortedWith(
                    compareByDescending<VideoItem> { it.uploadDateMillis }
                        .thenByDescending { it.viewCount }
                )
            }
            ChannelSortOption.OLDEST -> {
                filtered.sortedWith(
                    compareBy<VideoItem> { it.uploadDateMillis }
                        .thenBy { it.viewCount }
                )
            }
            ChannelSortOption.POPULAR -> {
                filtered.sortedByDescending { it.viewCount }
            }
        }

        _uiState.value = ChannelUiState.Success(
            channel = channel,
            sortedVideos = sorted,
            currentSort = activeSort,
            isLoadingMore = isLoadingMore,
            searchQuery = currentSearchQuery
        )
    }
}
