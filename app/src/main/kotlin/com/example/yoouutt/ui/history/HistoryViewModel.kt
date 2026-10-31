package com.example.yoouutt.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoouutt.YoouuttApp
import com.example.yoouutt.data.model.WatchHistoryItem
import com.example.yoouutt.data.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: WatchHistoryRepository = YoouuttApp.instance.historyRepository
) : ViewModel() {

    val historyItems: StateFlow<List<WatchHistoryItem>> = repository.getHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteItem(videoId: String) {
        viewModelScope.launch {
            repository.deleteHistory(videoId)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }
}

