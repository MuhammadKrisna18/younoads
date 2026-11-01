package com.example.yoouutt.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.repository.NewPipeVideoRepository
import com.example.yoouutt.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: VideoRepository = NewPipeVideoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        loadTrending()
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.isBlank()) {
            _selectedCategory.value = "Semua"
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        if (category == "Semua") {
            _searchQuery.value = ""
            loadTrending()
        } else {
            _searchQuery.value = category
            search()
        }
    }

    fun loadTrending() {
        _selectedCategory.value = "Semua"
        _uiState.value = FeedUiState.Loading
        viewModelScope.launch {
            repository.getTrendingVideos()
                .onSuccess { videos ->
                    _uiState.value = FeedUiState.Success(videos)
                }
                .onFailure { throwable ->
                    _uiState.value = FeedUiState.Error(throwable.localizedMessage ?: "Gagal memuat video")
                }
        }
    }

    fun search() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) {
            loadTrending()
            return
        }

        _uiState.value = FeedUiState.Loading
        viewModelScope.launch {
            repository.searchVideos(query)
                .onSuccess { videos ->
                    _uiState.value = FeedUiState.Success(videos)
                }
                .onFailure { throwable ->
                    _uiState.value = FeedUiState.Error(throwable.localizedMessage ?: "Pencarian gagal")
                }
        }
    }
}
