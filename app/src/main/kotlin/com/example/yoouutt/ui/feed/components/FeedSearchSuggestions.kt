package com.example.yoouutt.ui.feed.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoouutt.ui.common.BrandRed

@Composable
fun FeedSearchSuggestions(
    searchQuery: String,
    searchHistory: List<String>,
    searchSuggestions: List<String>,
    onSelectSuggestion: (String) -> Unit,
    onQueryFill: (String) -> Unit,
    onDeleteHistoryItem: (String) -> Unit,
    onClearAllHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val focusManager = LocalFocusManager.current
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
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                onClearAllHistoryClick()
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
                        onSelectSuggestion(historyItem)
                    }
                    .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Riwayat",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = historyItem,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onQueryFill(historyItem)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NorthWest,
                        contentDescription = "Isi ke pencarian",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onDeleteHistoryItem(historyItem)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hapus riwayat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        onSelectSuggestion(suggestion)
                    }
                    .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = suggestion,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onQueryFill(suggestion)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NorthWest,
                        contentDescription = "Isi ke pencarian",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Belum ada riwayat pencarian",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pencarian Anda akan otomatis tersimpan di sini",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
