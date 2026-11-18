package com.example.yoouutt.ui.history

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.R
import com.example.yoouutt.data.model.WatchHistoryItem
import com.example.yoouutt.ui.common.AppConfirmDialog
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.history.components.HistoryEmptyState
import com.example.yoouutt.ui.history.components.HistoryItemRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onVideoClick: (String, String, String) -> Unit,
    onBack: (() -> Unit)? = null,
    onExploreClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val historyItems by viewModel.historyItems.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<WatchHistoryItem?>(null) }
    val view = LocalView.current

    if (showClearDialog) {
        AppConfirmDialog(
            title = stringResource(R.string.library_clear_history_title),
            message = stringResource(R.string.library_clear_history_msg),
            onConfirm = {
                viewModel.clearAll()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }

    val currentItemToDelete = itemToDelete
    if (currentItemToDelete != null) {
        AppConfirmDialog(
            title = stringResource(R.string.history_delete_single_title),
            message = stringResource(R.string.history_delete_single_msg, currentItemToDelete.title),
            onConfirm = {
                viewModel.deleteItem(currentItemToDelete.videoId)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_history),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                actions = {
                    if (historyItems.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.library_clear_history_title),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (historyItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                HistoryEmptyState(onExploreClick = { onExploreClick?.invoke() ?: onBack?.invoke() })
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 130.dp)
            ) {
                items(
                    items = historyItems,
                    key = { it.videoId }
                ) { item ->
                    HistoryItemRow(
                        item = item,
                        onClick = {
                            onVideoClick(item.videoUrl, item.title, item.uploaderName)
                        },
                        onDelete = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            itemToDelete = item
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
