package com.example.yoouutt.ui.feed

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.R
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.common.AppConfirmDialog
import com.example.yoouutt.ui.common.AppThemeBackdrop
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.common.UpdateDialog
import com.example.yoouutt.ui.feed.components.FeedContentList
import com.example.yoouutt.ui.feed.components.FeedEmptyState
import com.example.yoouutt.ui.feed.components.FeedErrorState
import com.example.yoouutt.ui.feed.components.FeedSearchSuggestions
import com.example.yoouutt.ui.feed.components.FeedSkeletonList
import com.example.yoouutt.ui.feed.components.FeedTopBar
import com.example.yoouutt.update.AppUpdateManager
import com.example.yoouutt.update.UpdateInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onVideoClick: (VideoItem) -> Unit,
    onChannelClick: (channelUrl: String, channelName: String) -> Unit,
    onPlaylistClick: (playlistUrl: String, playlistTitle: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val searchSuggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var isSearchExpanded by remember { mutableStateOf(false) }
    var isSearchOverlayVisible by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val updateManager = remember { AppUpdateManager() }
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var downloadProgress by remember { mutableStateOf<Float?>(null) }

    fun refreshCurrentFeed() {
        when (selectedCategory) {
            "Semua" -> viewModel.loadHomeFeed()
            "Trending" -> viewModel.loadTrending()
            else -> viewModel.search()
        }
    }

    BackHandler(enabled = isSearchExpanded) {
        if (isSearchOverlayVisible && searchQuery.isNotBlank()) {
            focusManager.clearFocus()
            isSearchOverlayVisible = false
        } else {
            isSearchExpanded = false
            isSearchOverlayVisible = false
            viewModel.clearSuggestions()
            if (searchQuery.isNotEmpty()) {
                viewModel.onQueryChanged("")
                viewModel.loadHomeFeed()
            }
        }
    }

    LaunchedEffect(isSearchExpanded) {
        if (isSearchExpanded) {
            isSearchOverlayVisible = true
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        val currentVersionName = try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0"
        } catch (_: Exception) {
            "1.0"
        }
        val update = updateManager.checkForUpdate(currentVersionName)
        if (update != null) {
            updateInfo = update
        }
    }

    val activeUpdate = updateInfo
    if (activeUpdate != null) {
        UpdateDialog(
            updateInfo = activeUpdate,
            downloadProgress = downloadProgress,
            onUpdateClick = {
                coroutineScope.launch {
                    downloadProgress = 0f
                    val file = updateManager.downloadApk(activeUpdate.downloadUrl, context) { progress ->
                        downloadProgress = progress
                    }
                    downloadProgress = null
                    if (file != null) {
                        updateInfo = null
                        updateManager.installApk(file, context)
                    }
                }
            },
            onDismiss = { updateInfo = null }
        )
    }

    AppThemeBackdrop(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
            FeedTopBar(
                isSearchExpanded = isSearchExpanded,
                searchQuery = searchQuery,
                categories = categories,
                selectedCategory = selectedCategory,
                focusRequester = focusRequester,
                onSearchClick = {
                    isSearchExpanded = true
                    isSearchOverlayVisible = true
                },
                onBackClick = {
                    if (isSearchOverlayVisible && searchQuery.isNotBlank()) {
                        focusManager.clearFocus()
                        isSearchOverlayVisible = false
                    } else {
                        isSearchExpanded = false
                        isSearchOverlayVisible = false
                        viewModel.clearSuggestions()
                        if (searchQuery.isNotEmpty()) {
                            viewModel.onQueryChanged("")
                            viewModel.loadHomeFeed()
                        }
                    }
                },
                onQueryChange = { query ->
                    viewModel.onQueryChanged(query)
                    isSearchOverlayVisible = true
                },
                onClearQuery = {
                    viewModel.onQueryChanged("")
                    isSearchOverlayVisible = true
                },
                onSearchSubmit = {
                    focusManager.clearFocus()
                    isSearchOverlayVisible = false
                    viewModel.search()
                },
                onCategorySelected = { category ->
                    viewModel.selectCategory(category)
                },
                onFocusGain = {
                    isSearchOverlayVisible = true
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PullToRefreshBox(
                isRefreshing = uiState is FeedUiState.Loading && searchQuery.isEmpty(),
                onRefresh = ::refreshCurrentFeed,
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = uiState,
                    contentAlignment = Alignment.Center,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(120)) togetherWith fadeOut(animationSpec = tween(100))
                    },
                    label = "feed_content_transition",
                    modifier = Modifier.fillMaxSize()
                ) { state ->
                    when (state) {
                        is FeedUiState.Loading -> FeedSkeletonList()
                        is FeedUiState.Error -> FeedErrorState(
                            message = state.message,
                            onRetry = ::refreshCurrentFeed
                        )
                        is FeedUiState.Success -> {
                            if (state.items.isEmpty()) {
                                FeedEmptyState()
                            } else {
                                FeedContentList(
                                    items = state.items,
                                    onVideoClick = onVideoClick,
                                    onChannelClick = onChannelClick,
                                    onPlaylistClick = onPlaylistClick
                                )
                            }
                        }
                    }
                }
            }

            if (isSearchExpanded && isSearchOverlayVisible) {
                FeedSearchSuggestions(
                    searchQuery = searchQuery,
                    searchHistory = searchHistory,
                    searchSuggestions = searchSuggestions,
                    onSelectSuggestion = { suggestion ->
                        focusManager.clearFocus()
                        isSearchOverlayVisible = false
                        viewModel.selectSuggestion(suggestion)
                    },
                    onQueryFill = { item ->
                        viewModel.onQueryChanged(item)
                    },
                    onDeleteHistoryItem = { item ->
                        viewModel.deleteSearchHistory(item)
                    },
                    onClearAllHistoryClick = {
                        showClearHistoryDialog = true
                    }
                )
            }
        }
    }
}

    if (showClearHistoryDialog) {
        AppConfirmDialog(
            title = stringResource(R.string.search_clear_history_title),
            message = stringResource(R.string.search_clear_history_msg),
            onConfirm = {
                viewModel.clearAllSearchHistory()
                showClearHistoryDialog = false
            },
            onDismiss = { showClearHistoryDialog = false }
        )
    }
}
