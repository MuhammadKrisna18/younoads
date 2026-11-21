package com.example.yoouutt.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoouutt.data.download.VideoDownloadManager
import com.example.yoouutt.data.model.DownloadedVideo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DownloadsViewModel(
    private val downloadManager: VideoDownloadManager
) : ViewModel() {

    val downloads: StateFlow<List<DownloadedVideo>> = downloadManager
        .getDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadProgressMap: StateFlow<Map<String, Int>> = downloadManager.downloadProgressMap

    fun deleteDownload(videoId: String) {
        viewModelScope.launch {
            downloadManager.deleteDownload(videoId)
        }
    }

    fun cancelDownload(videoId: String) {
        downloadManager.cancelDownload(videoId)
    }
}
