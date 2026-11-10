package com.example.yoouutt.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoouutt.data.model.FeedItem
import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.WatchHistoryItem
import com.example.yoouutt.data.repository.SearchHistoryRepository
import com.example.yoouutt.data.repository.VideoRepository
import com.example.yoouutt.data.repository.WatchHistoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory

class FeedViewModel(
    private val repository: VideoRepository,
    private val historyRepository: WatchHistoryRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val defaultCategories = listOf("Musik", "Gaming", "Berita", "Podcast", "Teknologi", "Film", "Anime")
    private val searchFilterCategories = listOf("Semua", "Video", "Playlist")

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

    val searchHistory: StateFlow<List<String>> = searchHistoryRepository.getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var currentHistory: List<WatchHistoryItem> = emptyList()
    private var currentPlaylists: List<PlaylistItem> = emptyList()
    private var suggestionJob: Job? = null

    init {
        viewModelScope.launch {
            historyRepository.getWatchedPlaylists().collect { playlists ->
                currentPlaylists = playlists
            }
        }
        viewModelScope.launch {
            historyRepository.getHistory().collect { history ->
                val wasEmpty = currentHistory.isEmpty()
                currentHistory = history
                updateHomeCategories()
                if (wasEmpty && history.isNotEmpty() && _selectedCategory.value == "Semua" && _searchQuery.value.isEmpty()) {
                    loadHomeFeed()
                }
            }
        }
        loadHomeFeed()
    }

    private fun updateHomeCategories() {
        if (_searchQuery.value.isNotEmpty()) {
            _categories.value = searchFilterCategories
            return
        }
        val watchedChannels = currentHistory
            .map { it.uploaderName }
            .filter { it.isNotBlank() }
            .distinct()
            .take(6)
        _categories.value = (listOf("Semua", "Trending") + watchedChannels + defaultCategories).distinct()
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        suggestionJob?.cancel()
        val trimmed = newQuery.trim()
        if (trimmed.isBlank()) {
            _searchSuggestions.value = emptyList()
            _selectedCategory.value = "Semua"
            updateHomeCategories()
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
        search(suggestion)
    }

    fun clearSuggestions() {
        suggestionJob?.cancel()
        _searchSuggestions.value = emptyList()
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        if (_searchQuery.value.isNotBlank()) {
            val filter = when (category) {
                "Video" -> YoutubeSearchQueryHandlerFactory.VIDEOS
                "Playlist" -> YoutubeSearchQueryHandlerFactory.PLAYLISTS
                else -> null
            }
            search(_searchQuery.value, filter)
            return
        }
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
                search(category)
            }
        }
    }

    fun loadHomeFeed() {
        suggestionJob?.cancel()
        _searchSuggestions.value = emptyList()
        _selectedCategory.value = "Semua"
        _searchQuery.value = ""
        updateHomeCategories()
        _uiState.value = FeedUiState.Loading
        viewModelScope.launch {
            repository.getPersonalizedFeed(currentHistory, currentPlaylists)
                .onSuccess { items ->
                    _uiState.value = FeedUiState.Success(items)
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
        _searchQuery.value = ""
        updateHomeCategories()
        _uiState.value = FeedUiState.Loading
        viewModelScope.launch {
            repository.getTrendingVideos()
                .onSuccess { videos ->
                    _uiState.value = FeedUiState.Success(videos.map { FeedItem.Video(it) })
                }
                .onFailure { throwable ->
                    _uiState.value = FeedUiState.Error(throwable.localizedMessage ?: "Gagal memuat trending")
                }
        }
    }

    fun search(targetQuery: String? = null, filter: String? = null) {
        suggestionJob?.cancel()
        _searchSuggestions.value = emptyList()
        val query = (targetQuery ?: _searchQuery.value).trim()
        if (targetQuery != null) {
            _searchQuery.value = query
        }
        if (query.isBlank()) {
            loadHomeFeed()
            return
        }

        if (filter == null) {
            _selectedCategory.value = "Semua"
        }
        _categories.value = searchFilterCategories

        viewModelScope.launch {
            searchHistoryRepository.saveSearchQuery(query)
        }

        _uiState.value = FeedUiState.Loading
        viewModelScope.launch {
            repository.search(query, filter)
                .onSuccess { items ->
                    _uiState.value = FeedUiState.Success(items)
                }
                .onFailure { throwable ->
                    _uiState.value = FeedUiState.Error(throwable.localizedMessage ?: "Pencarian gagal")
                }
        }
    }

    fun deleteSearchHistory(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearchQuery(query)
        }
    }

    fun clearAllSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearAllSearchHistory()
        }
    }
}
