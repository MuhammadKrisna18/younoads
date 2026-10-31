package com.example.yoouutt.ui.player

import com.example.yoouutt.data.model.StreamData

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Ready(val streamData: StreamData) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}

