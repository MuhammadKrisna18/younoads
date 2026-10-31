package com.example.yoouutt.ui.feed

import com.example.yoouutt.data.model.VideoItem

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(val videos: List<VideoItem>) : FeedUiState
    data class Error(val message: String) : FeedUiState
}

