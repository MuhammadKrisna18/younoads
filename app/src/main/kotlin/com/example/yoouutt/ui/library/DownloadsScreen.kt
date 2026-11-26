package com.example.yoouutt.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.R
import com.example.yoouutt.data.model.DownloadStatus
import com.example.yoouutt.data.model.DownloadedVideo
import com.example.yoouutt.ui.common.AppConfirmDialog
import com.example.yoouutt.ui.common.AppThemeBackdrop
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.library.components.DownloadedVideoItemRow
import com.example.yoouutt.ui.library.components.LibraryEmptyTabState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onVideoClick: (DownloadedVideo) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DownloadsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val progressMap by viewModel.downloadProgressMap.collectAsStateWithLifecycle()
    var videoToDelete by remember { mutableStateOf<DownloadedVideo?>(null) }

    videoToDelete?.let { video ->
        AppConfirmDialog(
            title = stringResource(R.string.download_delete_confirm_title),
            message = stringResource(R.string.download_delete_confirm_msg),
            onConfirm = {
                viewModel.deleteDownload(video.videoId)
                videoToDelete = null
            },
            onDismiss = { videoToDelete = null }
        )
    }

    AppThemeBackdrop(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.downloads_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            if (downloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    LibraryEmptyTabState(
                        title = stringResource(R.string.downloads_empty_title),
                        message = stringResource(R.string.downloads_empty_desc),
                        actionButtonText = stringResource(R.string.library_btn_explore),
                        onActionClick = onBack
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 130.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item(key = "downloads_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${downloads.size} Media",
                                color = Color(0xFFAAAAAA),
                                fontSize = 12.5.sp
                            )
                        }
                    }

                    items(
                        items = downloads,
                        key = { it.videoId }
                    ) { item ->
                        DownloadedVideoItemRow(
                            item = item,
                            progress = progressMap[item.videoId],
                            onClick = {
                                if (item.status == DownloadStatus.COMPLETED && item.filePath.isNotBlank()) {
                                    onVideoClick(item)
                                }
                            },
                            onDelete = { videoToDelete = item }
                        )
                    }
                }
            }
        }
    }
}
