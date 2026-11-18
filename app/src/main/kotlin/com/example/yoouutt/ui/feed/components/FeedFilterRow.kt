package com.example.yoouutt.ui.feed.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoouutt.R
import com.example.yoouutt.ui.theme.LocalYoouuttGradient

@Composable
fun FeedFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            val chipShape = RoundedCornerShape(50.dp)
            val themeGradient = LocalYoouuttGradient.current
            val primaryColor = MaterialTheme.colorScheme.primary

            val displayCategory = when (category.lowercase()) {
                "semua", "all" -> stringResource(R.string.category_all)
                "trending" -> stringResource(R.string.category_trending)
                "musik", "music" -> stringResource(R.string.category_music)
                "gaming" -> stringResource(R.string.category_gaming)
                "berita", "news" -> stringResource(R.string.category_news)
                "podcast" -> stringResource(R.string.category_podcast)
                "teknologi", "technology", "tech" -> stringResource(R.string.category_tech)
                "film", "movies", "movies & animation" -> stringResource(R.string.category_movies)
                "video", "videos" -> stringResource(R.string.search_filter_videos)
                "playlist", "playlists" -> stringResource(R.string.search_filter_playlists)
                else -> category
            }

            Box(
                modifier = Modifier
                    .clip(chipShape)
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(themeGradient)
                                .border(BorderStroke(1.dp, primaryColor.copy(alpha = 0.6f)), chipShape)
                        } else {
                            Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .border(BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)), chipShape)
                        }
                    )
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onCategorySelected(category)
                    }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayCategory,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                )
            }
        }
    }
}
