package com.example.yoouutt.ui.feed

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.data.model.FeedItem
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.channel.PlaylistCard
import com.example.yoouutt.ui.common.AppConfirmDialog
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.common.BrandLogo
import com.example.yoouutt.ui.common.BrandRed
import com.example.yoouutt.ui.theme.YouTubeRed
import com.example.yoouutt.ui.common.UpdateDialog
import com.example.yoouutt.update.AppUpdateManager
import com.example.yoouutt.update.UpdateInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onVideoClick: (VideoItem) -> Unit,
    onHistoryClick: () -> Unit,
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
    val view = LocalView.current

    var isSearchExpanded by remember { mutableStateOf(false) }
    var isSearchOverlayVisible by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val updateManager = remember { AppUpdateManager() }
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var downloadProgress by remember { mutableStateOf<Float?>(null) }

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
            onDismiss = {
                updateInfo = null
            }
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Black,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .statusBarsPadding()
            ) {
                AnimatedContent(
                    targetState = isSearchExpanded,
                    transitionSpec = {
                        fadeIn(tween(180, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(tween(120, easing = FastOutSlowInEasing))
                    },
                    label = "searchBarTransition"
                ) { expanded ->
                    if (!expanded) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BrandLogo()
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                        isSearchExpanded = true
                                        isSearchOverlayVisible = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { category ->
                                    val isSelected = selectedCategory == category
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(if (isSelected) YouTubeRed else Color(0xFF222222))
                                            .clickable {
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                viewModel.selectCategory(category)
                                            }
                                            .padding(horizontal = 14.dp, vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = category,
                                            color = Color.White,
                                            fontSize = 12.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
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
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = Color.White
                                )
                            }

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { query ->
                                    viewModel.onQueryChanged(query)
                                    isSearchOverlayVisible = true
                                },
                                placeholder = {
                                    Text(
                                        text = "Search YouTube...",
                                        fontSize = 14.sp,
                                        color = Color(0xFF8E8E8E)
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                viewModel.onQueryChanged("")
                                                isSearchOverlayVisible = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Hapus",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(50.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF1F1F1F),
                                    unfocusedContainerColor = Color(0xFF1F1F1F),
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        focusManager.clearFocus()
                                        isSearchOverlayVisible = false
                                        viewModel.search()
                                    }
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { focusState ->
                                        if (focusState.isFocused) {
                                            isSearchOverlayVisible = true
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PullToRefreshBox(
                isRefreshing = uiState is FeedUiState.Loading && searchQuery.isEmpty(),
                onRefresh = {
                    when (selectedCategory) {
                        "Semua" -> viewModel.loadHomeFeed()
                        "Trending" -> viewModel.loadTrending()
                        else -> viewModel.search()
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when (val state = uiState) {
                    is FeedUiState.Loading -> {
                        LazyColumn(
                            contentPadding = PaddingValues(top = 0.dp, bottom = 130.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(4) {
                                VideoCardSkeleton()
                            }
                        }
                    }

                    is FeedUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    when (selectedCategory) {
                                        "Semua" -> viewModel.loadHomeFeed()
                                        "Trending" -> viewModel.loadTrending()
                                        else -> viewModel.search()
                                    }
                                },
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    text = "Retry",
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    is FeedUiState.Success -> {
                        if (state.items.isEmpty()) {
                            Text(
                                text = "Tidak ada hasil ditemukan",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(top = 0.dp, bottom = 130.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = state.items,
                                    key = { it.id }
                                ) { feedItem ->
                                    when (feedItem) {
                                        is FeedItem.Video -> {
                                            VideoCard(
                                                video = feedItem.video,
                                                onClick = { onVideoClick(feedItem.video) },
                                                onChannelClick = onChannelClick
                                            )
                                        }
                                        is FeedItem.Playlist -> {
                                            PlaylistCard(
                                                playlist = feedItem.playlist,
                                                onClick = { onPlaylistClick(feedItem.playlist.url, feedItem.playlist.title) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isSearchExpanded && isSearchOverlayVisible) {
                val trimmedQuery = searchQuery.trim()
                val matchingHistory = remember(trimmedQuery, searchHistory) {
                    if (trimmedQuery.isBlank()) {
                        searchHistory.distinct()
                    } else {
                        searchHistory.filter { it.contains(trimmedQuery, ignoreCase = true) }.distinct()
                    }
                }
                val nonHistorySuggestions = remember(searchSuggestions, matchingHistory) {
                    searchSuggestions
                        .filter { s -> matchingHistory.none { it.equals(s, ignoreCase = true) } }
                        .distinct()
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .imePadding(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 130.dp)
                ) {
                    if (trimmedQuery.isBlank() && matchingHistory.isNotEmpty()) {
                        item(key = "header_history") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Riwayat Pencarian",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Hapus Semua",
                                    color = BrandRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable {
                                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                            showClearHistoryDialog = true
                                        }
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    items(
                        items = matchingHistory,
                        key = { "hist_$it" }
                    ) { historyItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    focusManager.clearFocus()
                                    isSearchOverlayVisible = false
                                    viewModel.selectSuggestion(historyItem)
                                }
                                .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Riwayat",
                                tint = Color(0xFFAAAAAA),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = historyItem,
                                color = Color.White,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.onQueryChanged(historyItem)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NorthWest,
                                    contentDescription = "Isi ke pencarian",
                                    tint = Color(0xFF8E8E8E),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.deleteSearchHistory(historyItem)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Hapus riwayat",
                                    tint = Color(0xFF8E8E8E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    items(
                        items = nonHistorySuggestions,
                        key = { "sug_$it" }
                    ) { suggestion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    focusManager.clearFocus()
                                    isSearchOverlayVisible = false
                                    viewModel.selectSuggestion(suggestion)
                                }
                                .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF8E8E8E),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = suggestion,
                                color = Color.White,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.onQueryChanged(suggestion)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NorthWest,
                                    contentDescription = "Isi ke pencarian",
                                    tint = Color(0xFF8E8E8E),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (trimmedQuery.isBlank() && matchingHistory.isEmpty()) {
                        item(key = "empty_history") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 90.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = Color(0xFF444444),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Belum ada riwayat pencarian",
                                    color = Color(0xFF888888),
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pencarian Anda akan otomatis tersimpan di sini",
                                    color = Color(0xFF555555),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearHistoryDialog) {
        AppConfirmDialog(
            title = "Hapus riwayat pencarian?",
            message = "Semua riwayat pencarian Anda akan dihapus secara permanen.",
            onConfirm = {
                viewModel.clearAllSearchHistory()
                showClearHistoryDialog = false
            },
            onDismiss = { showClearHistoryDialog = false }
        )
    }
}
