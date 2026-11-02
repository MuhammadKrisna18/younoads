package com.example.yoouutt.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.data.model.WatchHistoryItem
import com.example.yoouutt.data.repository.VideoRepository
import com.example.yoouutt.data.repository.WatchHistoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: VideoRepository,
    private val historyRepository: WatchHistoryRepository
) : ViewModel() {

    private val defaultCategories = listOf("Musik", "Gaming", "Berita", "Podcast", "Teknologi", "Film", "Anime")

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(listOf("Semua", "Trending") + defaultCategories)
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    private var currentHistory: List<WatchHistoryItem> = emptyList()
    private var suggestionJob: Job? = null

    init {
        viewModelScope.launch {
            historyRepository.getHistory().collect { history ->
                val wasEmpty = currentHistory.isEmpty()
                currentHistory = history
                val watchedChannels = history
                    .map { it.uploaderName }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .take(6)
                _categories.value = (listOf("Semua", "Trending") + watchedChannels + defaultCategories).distinct()
                if (wasEmpty && history.isNotEmpty() && _selectedCategory.value == "Semua" && _searchQuery.value.isEmpty()) {
                    loadHomeFeed()
                }
            }
        }
        loadHomeFeed()
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        suggestionJob?.cancel()
        val trimmed = newQuery.trim()
        if (trimmed.isBlank()) {
            _searchSuggestions.value = emptyList()
            _selectedCategory.value = "Semua"
        } else {
            suggestionJob = viewModelScope.launch {
                delay(250)
                repository.getSearchSuggestions(trimmed)
                    .onSuccess { suggestions ->
                        _searchSuggestions.value = suggestions
                    }
                    .onFailure {
                        _searchSuggestions.value = emptyList()
                    }
            }
        }
    }

    fun selectSuggestion(suggestion: String) {
        suggestionJob?.cancel()
        _searchQuery.value = suggestion
        _searchSuggestions.value = emptyList()
        search()
    }

    fun clearSuggestions() {
        suggestionJob?.cancel()
        _searchSuggestions.value = emptyList()
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        when (category) {
            "Semua" -> {
                _searchQuery.value = ""
                loadHomeFeed()
            }
            "Trending" -> {
                _searchQuery.value = ""
                loadTrending()
            }
            else -> {
                _searchQuery.value = category
                search()
            }
        }
    }

    fun loadHomeFeed() {
        suggestionJob?.cancel()
        _searchSuggestions.value = emptyList()
        _selectedCategory.value = "Semua"
        _uiState.value = FeedUiState.Loading
        viewModelScope.launch {
            repository.getPersonalizedFeed(currentHistory)
                .onSuccess { videos ->
                    _uiState.value = FeedUiState.Success(videos)
                }
                .onFailure { throwable ->
                    _uiState.value = FeedUiState.Error(throwable.localizedMessage ?: "Gagal memuat video")
                }
        }
    }

    fun loadTrending() {
        suggestionJob?.cancel()
        _searchSuggestions.value = emptyList()
        _selectedCategory.value = "Trending"
        _uiState.value = FeedUiState.Loading
        viewModelScope.launch {
            repository.getTrendingVideos()
                .onSuccess { videos ->
                    _uiState.value = FeedUiState.Success(videos)
                }
                .onFailure { throwable ->
                    _uiState.value = FeedUiState.Error(throwable.localizedMessage ?: "Gagal memuat trending")
                }
        }
    }

    fun search() {
        suggestionJob?.cancel()
        _searchSuggestions.value = emptyList()
        val query = _searchQuery.value.trim()
        if (query.isBlank()) {
            loadHomeFeed()
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
