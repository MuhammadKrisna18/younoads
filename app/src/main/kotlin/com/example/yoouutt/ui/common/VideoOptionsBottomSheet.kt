package com.example.yoouutt.ui.common

import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.yoouutt.R
import com.example.yoouutt.YoouuttApp
import com.example.yoouutt.data.model.CustomPlaylistItem
import com.example.yoouutt.data.model.DownloadRequest
import com.example.yoouutt.data.model.SavedVideo
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.player.PlayerUtils
import com.example.yoouutt.ui.player.components.AddToPlaylistBottomSheet
import com.example.yoouutt.ui.player.components.CreatePlaylistDialog
import com.example.yoouutt.ui.player.components.DownloadSelectionDialog
import com.example.yoouutt.ui.player.components.ShareBottomSheet
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

private enum class VideoOptionsSubSheet {
    SHARE,
    PLAYLIST,
    DOWNLOAD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoOptionsBottomSheet(
    video: VideoItem,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onChannelClick: ((channelUrl: String, channelName: String) -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val container = YoouuttApp.instance.container
    val savedVideoRepository = container.savedVideoRepository
    val customPlaylistRepository = container.customPlaylistRepository
    val playbackManager = container.playbackManager
    val videoRepository = container.videoRepository
    val downloadManager = container.downloadManager

    var isSaved by remember { mutableStateOf(false) }
    LaunchedEffect(video.id) {
        val targetId = video.id.ifBlank { PlayerUtils.extractVideoId(video.url) }
        isSaved = savedVideoRepository.isSaved(targetId)
    }

    var activeSubSheet by remember { mutableStateOf<VideoOptionsSubSheet?>(null) }
    var isCreatePlaylistVisible by remember { mutableStateOf(false) }
    var streamDataForDownload by remember { mutableStateOf<StreamData?>(null) }
    val customPlaylists by customPlaylistRepository.getCustomPlaylists().collectAsStateWithLifecycle(emptyList())
    var playlistMembershipIds by remember { mutableStateOf<Set<Long>>(emptySet()) }

    if (activeSubSheet == null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = Color.Black.copy(alpha = 0.65f),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 8.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                )
            },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(96.dp)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = video.uploaderName,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 0.8.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OptionRowItem(
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    title = stringResource(R.string.options_add_to_queue),
                    subtitle = stringResource(R.string.options_add_to_queue_desc),
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        playbackManager.enqueueVideo(video)
                        Toast.makeText(context, context.getString(R.string.options_video_enqueued), Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                OptionRowItem(
                    icon = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    title = if (isSaved) stringResource(R.string.options_remove_saved) else stringResource(R.string.options_save_video),
                    subtitle = stringResource(R.string.options_save_video_desc),
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        val targetId = video.id.ifBlank { PlayerUtils.extractVideoId(video.url) }
                        scope.launch {
                            val currentlySaved = savedVideoRepository.isSaved(targetId)
                            if (currentlySaved) {
                                savedVideoRepository.removeSavedVideo(targetId)
                                isSaved = false
                                Toast.makeText(context, context.getString(R.string.options_removed_toast), Toast.LENGTH_SHORT).show()
                            } else {
                                val savedItem = SavedVideo(
                                    videoId = targetId,
                                    url = video.url,
                                    title = video.title,
                                    uploader = video.uploaderName,
                                    thumbnailUrl = video.thumbnailUrl,
                                    savedAt = System.currentTimeMillis()
                                )
                                savedVideoRepository.saveVideo(savedItem)
                                isSaved = true
                                Toast.makeText(context, context.getString(R.string.options_saved_toast), Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        }
                    }
                )

                OptionRowItem(
                    icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                    title = stringResource(R.string.options_save_to_playlist),
                    subtitle = stringResource(R.string.options_save_to_playlist_desc),
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        val targetId = video.id.ifBlank { PlayerUtils.extractVideoId(video.url) }
                        scope.launch {
                            val allPlaylists = customPlaylistRepository.getCustomPlaylists().firstOrNull().orEmpty()
                            val memberIds = allPlaylists.filter {
                                customPlaylistRepository.isVideoInPlaylist(it.id, targetId)
                            }.map { it.id }.toSet()
                            playlistMembershipIds = memberIds
                            activeSubSheet = VideoOptionsSubSheet.PLAYLIST
                        }
                    }
                )

                OptionRowItem(
                    icon = Icons.Outlined.Download,
                    title = stringResource(R.string.options_download_video),
                    subtitle = stringResource(R.string.options_download_video_desc),
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        Toast.makeText(context, context.getString(R.string.options_download_starting), Toast.LENGTH_SHORT).show()
                        scope.launch {
                            val result = videoRepository.getVideoStreams(video.url)
                            val streamData = result.getOrNull()
                            if (streamData != null) {
                                streamDataForDownload = streamData
                                activeSubSheet = VideoOptionsSubSheet.DOWNLOAD
                            } else {
                                Toast.makeText(context, "Gagal memuat format video", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        }
                    }
                )

                OptionRowItem(
                    icon = Icons.Outlined.Share,
                    title = stringResource(R.string.common_share_video),
                    subtitle = stringResource(R.string.options_share_subtitle),
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        activeSubSheet = VideoOptionsSubSheet.SHARE
                    }
                )

                if (video.uploaderName.isNotBlank() && onChannelClick != null) {
                    val targetChannelUrl = video.uploaderUrl.ifBlank { video.uploaderName }
                    OptionRowItem(
                        icon = Icons.Outlined.AccountCircle,
                        title = stringResource(R.string.common_open_channel),
                        subtitle = video.uploaderName,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onChannelClick(targetChannelUrl, video.uploaderName)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }

    when (activeSubSheet) {
        VideoOptionsSubSheet.SHARE -> {
            ShareBottomSheet(
                videoUrl = video.url,
                videoTitle = video.title,
                uploaderName = video.uploaderName,
                currentPositionMs = 0L,
                onDismiss = onDismiss
            )
        }
        VideoOptionsSubSheet.PLAYLIST -> {
            val targetId = video.id.ifBlank { PlayerUtils.extractVideoId(video.url) }
            AddToPlaylistBottomSheet(
                playlists = customPlaylists,
                selectedPlaylistIds = playlistMembershipIds,
                onTogglePlaylist = { playlistId ->
                    scope.launch {
                        val inPlaylist = customPlaylistRepository.isVideoInPlaylist(playlistId, targetId)
                        if (inPlaylist) {
                            customPlaylistRepository.removeVideoFromPlaylist(playlistId, targetId)
                            playlistMembershipIds = playlistMembershipIds - playlistId
                        } else {
                            val playlistItem = CustomPlaylistItem(
                                playlistId = playlistId,
                                videoId = targetId,
                                url = video.url,
                                title = video.title,
                                uploader = video.uploaderName,
                                thumbnailUrl = video.thumbnailUrl,
                                duration = video.durationText,
                                addedAt = System.currentTimeMillis()
                            )
                            customPlaylistRepository.addVideoToPlaylist(playlistId, playlistItem)
                            playlistMembershipIds = playlistMembershipIds + playlistId
                        }
                    }
                },
                onCreateNewPlaylistClick = {
                    isCreatePlaylistVisible = true
                },
                onDismiss = onDismiss
            )

            if (isCreatePlaylistVisible) {
                CreatePlaylistDialog(
                    onDismiss = { isCreatePlaylistVisible = false },
                    onCreate = { title, description ->
                        scope.launch {
                            val newId = customPlaylistRepository.createPlaylist(title, description)
                            val playlistItem = CustomPlaylistItem(
                                playlistId = newId,
                                videoId = targetId,
                                url = video.url,
                                title = video.title,
                                uploader = video.uploaderName,
                                thumbnailUrl = video.thumbnailUrl,
                                duration = video.durationText,
                                addedAt = System.currentTimeMillis()
                            )
                            customPlaylistRepository.addVideoToPlaylist(newId, playlistItem)
                            playlistMembershipIds = playlistMembershipIds + newId
                            isCreatePlaylistVisible = false
                        }
                    }
                )
            }
        }
        VideoOptionsSubSheet.DOWNLOAD -> {
            val streamData = streamDataForDownload
            if (streamData != null) {
                val targetId = video.id.ifBlank { PlayerUtils.extractVideoId(video.url) }
                DownloadSelectionDialog(
                    streamData = streamData,
                    onSelectDownload = { qualityLabel, downloadUrl, audioDownloadUrl, isAudioOnly ->
                        val request = DownloadRequest(
                            videoId = targetId,
                            videoUrl = video.url,
                            title = video.title,
                            uploader = video.uploaderName,
                            thumbnailUrl = video.thumbnailUrl,
                            downloadUrl = downloadUrl,
                            audioDownloadUrl = audioDownloadUrl,
                            qualityLabel = qualityLabel,
                            isAudioOnly = isAudioOnly,
                            durationMs = 0L
                        )
                        downloadManager.startDownload(request)
                        Toast.makeText(context, context.getString(R.string.player_action_downloading), Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    onDismiss = onDismiss
                )
            }
        }
        null -> {
        }
    }
}

@Composable
private fun OptionRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
