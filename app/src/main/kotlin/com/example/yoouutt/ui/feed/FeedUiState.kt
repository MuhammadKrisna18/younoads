package com.example.yoouutt.ui.feed

import com.example.yoouutt.data.model.FeedItem

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(val items: List<FeedItem>) : FeedUiState
    data class Error(val message: String) : FeedUiState
}
