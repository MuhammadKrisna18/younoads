package com.example.yoouutt.ui.player

import android.view.HapticFeedbackConstants
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView
import com.example.yoouutt.LocalInPipMode
import com.example.yoouutt.data.model.StreamData
import com.example.yoouutt.data.model.VideoItem
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.feed.VideoCard
import com.example.yoouutt.ui.player.components.AmbientGlow
import com.example.yoouutt.ui.player.components.DoubleTapSeekFeedback
import com.example.yoouutt.ui.player.components.MinimalBottomProgressBar
import com.example.yoouutt.ui.player.components.PlayerActionPillRow
import com.example.yoouutt.ui.player.components.PlayerChannelHeader
import com.example.yoouutt.ui.player.components.PlayerControlsOverlay
import com.example.yoouutt.ui.player.components.PlayerDescriptionCard
import com.example.yoouutt.ui.player.components.ResolutionSelectionDialog
import com.example.yoouutt.ui.player.components.ShareBottomSheet

@Composable
fun PlayerScreen(
    videoUrl: String,
    fallbackTitle: String,
    fallbackUploader: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onChannelClick: ((channelUrl: String, channelName: String) -> Unit)? = null,
    viewModel: PlayerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val currentPlayback by viewModel.currentPlayback.collectAsStateWithLifecycle()
    val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()
    val isDescriptionExpanded by viewModel.isDescriptionExpanded.collectAsStateWithLifecycle()
    val isLiked by viewModel.isLiked.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val isSubscribed by viewModel.isSubscribed.collectAsStateWithLifecycle()
    val speedIndex by viewModel.speedIndex.collectAsStateWithLifecycle()
    val currentResolution by viewModel.currentResolution.collectAsStateWithLifecycle()
    val availableResolutions by viewModel.availableResolutions.collectAsStateWithLifecycle()
    val isResolutionDialogVisible by viewModel.isResolutionDialogVisible.collectAsStateWithLifecycle()
    val isAmbientModeEnabled by viewModel.isAmbientModeEnabled.collectAsStateWithLifecycle()
    val resolutionLabel = currentResolution?.label.orEmpty()

    val isInPipMode = LocalInPipMode.current
    var isShareSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(videoUrl) {
        viewModel.initVideo(videoUrl)
    }

    DisposableEffect(isFullscreen) {
        activity?.setFullscreen(isFullscreen)
        onDispose {
            activity?.setFullscreen(false)
        }
    }

    LaunchedEffect(isInPipMode) {
        if (isInPipMode) {
            viewModel.hideControls()
        }
    }

    BackHandler(enabled = isFullscreen) {
        viewModel.setFullscreen(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val videoBoxModifier = if (isFullscreen) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        }

        VideoPlayerSection(
            viewModel = viewModel,
            videoUrl = videoUrl,
            fallbackTitle = fallbackTitle,
            fallbackUploader = fallbackUploader,
            isFullscreen = isFullscreen,
            isInPipMode = isInPipMode,
            resolutionLabel = resolutionLabel,
            onBack = onBack,
            modifier = videoBoxModifier
        )

        if (!isFullscreen) {
            val currentTitle = currentPlayback?.title?.takeIf { it.isNotBlank() } ?: fallbackTitle
            val currentUploader = currentPlayback?.uploaderName?.takeIf { it.isNotBlank() } ?: fallbackUploader
            val streamData = currentPlayback?.streamData

            PlayerDetailContent(
                currentTitle = currentTitle,
                currentUploader = currentUploader,
                streamData = streamData,
                isLiked = isLiked,
                isSaved = isSaved,
                isSubscribed = isSubscribed,
                isAmbientModeEnabled = isAmbientModeEnabled,
                isDescriptionExpanded = isDescriptionExpanded,
                speedText = "${viewModel.speeds[speedIndex]}x",
                resolutionLabel = resolutionLabel,
                videoUrl = videoUrl,
                onToggleLike = viewModel::toggleLike,
                onShareClick = { isShareSheetVisible = true },
                onCopyLinkClick = {
                    val shortUrl = PlayerUtils.formatShortUrl(videoUrl)
                    PlayerUtils.copyToClipboard(context, shortUrl, "Tautan berhasil disalin")
                },
                onToggleSave = viewModel::toggleSave,
                onCycleSpeed = viewModel::cyclePlaybackSpeed,
                onResolutionClick = viewModel::showResolutionDialog,
                onToggleAmbient = viewModel::toggleAmbientMode,
                onPipClick = { activity?.enterPipMode() },
                onToggleFullscreen = viewModel::toggleFullscreen,
                onToggleSubscribe = viewModel::toggleSubscribe,
                onToggleDescription = viewModel::toggleDescription,
                onChannelClick = onChannelClick,
                onRelatedVideoClick = { relVideo ->
                    viewModel.playRelatedVideo(relVideo.url, relVideo.title, relVideo.uploaderName)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }

    if (isResolutionDialogVisible) {
        ResolutionSelectionDialog(
            availableResolutions = availableResolutions,
            currentResolution = currentResolution,
            isAmbientModeEnabled = isAmbientModeEnabled,
            onSelectResolution = viewModel::selectResolution,
            onToggleAmbientMode = viewModel::toggleAmbientMode,
            onDismiss = viewModel::dismissResolutionDialog
        )
    }

    if (isShareSheetVisible) {
        val currentTitle = currentPlayback?.title?.takeIf { it.isNotBlank() } ?: fallbackTitle
        val currentUploader = currentPlayback?.uploaderName?.takeIf { it.isNotBlank() } ?: fallbackUploader
        ShareBottomSheet(
            videoUrl = videoUrl,
            videoTitle = currentTitle,
            uploaderName = currentUploader,
            currentPositionMs = viewModel.currentPositionMs.value,
            onDismiss = { isShareSheetVisible = false }
        )
    }
}

@Composable
private fun VideoPlayerSection(
    viewModel: PlayerViewModel,
    videoUrl: String,
    fallbackTitle: String,
    fallbackUploader: String,
    isFullscreen: Boolean,
    isInPipMode: Boolean,
    resolutionLabel: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isBuffering.collectAsStateWithLifecycle()
    val currentPositionMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
    val bufferedPositionMs by viewModel.bufferedPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val isScrubbing by viewModel.isScrubbing.collectAsStateWithLifecycle()
    val scrubPositionMs by viewModel.scrubPositionMs.collectAsStateWithLifecycle()
    val controlsVisible by viewModel.controlsVisible.collectAsStateWithLifecycle()
    val seekFeedback by viewModel.seekFeedback.collectAsStateWithLifecycle()
    val resumeBannerVisible by viewModel.resumeBannerVisible.collectAsStateWithLifecycle()
    val resumeSavedPosition by viewModel.resumeSavedPosition.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (errorMessage != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.playRelatedVideo(videoUrl, fallbackTitle, fallbackUploader) },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Retry",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = viewModel.exoPlayer
                        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        useController = false
                    }
                },
                update = { pView ->
                    if (pView.player != viewModel.exoPlayer) {
                        pView.player = viewModel.exoPlayer
                    }
                },
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { viewModel.toggleControls() },
                            onDoubleTap = { offset ->
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                val isForward = offset.x >= size.width / 2
                                viewModel.doubleTapSeek(isForward)
                            }
                        )
                    }
            )

            PlayerControlsOverlay(
                visible = controlsVisible,
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                isFullscreen = isFullscreen,
                currentPositionMs = if (isScrubbing) scrubPositionMs else currentPositionMs,
                bufferedPositionMs = bufferedPositionMs,
                durationMs = durationMs,
                currentResolutionText = resolutionLabel,
                onBackClick = {
                    if (isFullscreen) viewModel.toggleFullscreen() else onBack()
                },
                onPlayPauseClick = viewModel::togglePlayPause,
                onToggleFullscreen = viewModel::toggleFullscreen,
                onResolutionClick = viewModel::showResolutionDialog,
                onScrubbing = viewModel::onScrubbing,
                onSeek = viewModel::seekTo
            )

            seekFeedback?.let { feedback ->
                DoubleTapSeekFeedback(
                    visible = true,
                    isForward = feedback.isForward,
                    text = feedback.text
                )
            }

            if (!controlsVisible && !isInPipMode) {
                MinimalBottomProgressBar(
                    currentPositionMs = currentPositionMs,
                    bufferedPositionMs = bufferedPositionMs,
                    durationMs = durationMs,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            if (resumeBannerVisible) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 54.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.Black.copy(alpha = 0.88f))
                        .border(
                            BorderStroke(0.5.dp, Color.White.copy(alpha = 0.25f)),
                            RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lanjutkan dari ${PlayerUtils.formatTime(resumeSavedPosition)}?",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ya",
                        color = Color(0xFFE50914),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.applyResume() }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Batal",
                        color = Color(0xFFAAAAAA),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable { viewModel.dismissResume() }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerDetailContent(
    currentTitle: String,
    currentUploader: String,
    streamData: StreamData?,
    isLiked: Boolean,
    isSaved: Boolean,
    isSubscribed: Boolean,
    isAmbientModeEnabled: Boolean,
    isDescriptionExpanded: Boolean,
    speedText: String,
    resolutionLabel: String,
    videoUrl: String,
    onToggleLike: () -> Unit,
    onShareClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onToggleSave: () -> Unit,
    onCycleSpeed: () -> Unit,
    onResolutionClick: () -> Unit,
    onToggleAmbient: () -> Unit,
    onPipClick: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleSubscribe: () -> Unit,
    onToggleDescription: () -> Unit,
    onChannelClick: ((channelUrl: String, channelName: String) -> Unit)?,
    onRelatedVideoClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (isAmbientModeEnabled) {
            AmbientGlow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item(key = "title_metadata") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = currentTitle,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (streamData != null) {
                        val metaLine = buildString {
                            if (streamData.viewCountText.isNotBlank()) append(streamData.viewCountText)
                            if (streamData.viewCountText.isNotBlank() && streamData.uploadDateText.isNotBlank()) append(" • ")
                            if (streamData.uploadDateText.isNotBlank()) append(streamData.uploadDateText)
                        }
                        if (metaLine.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = metaLine,
                                fontSize = 12.sp,
                                color = Color(0xFFAAAAAA)
                            )
                        }
                    }
                }
            }

            item(key = "channel_header") {
                PlayerChannelHeader(
                    uploaderName = currentUploader,
                    streamData = streamData,
                    isSubscribed = isSubscribed,
                    onToggleSubscribe = onToggleSubscribe,
                    onChannelClick = {
                        val targetUrl = streamData?.uploaderUrl?.takeIf { it.isNotBlank() } ?: currentUploader
                        onChannelClick?.invoke(targetUrl, currentUploader)
                    }
                )
            }

            item(key = "action_pills") {
                PlayerActionPillRow(
                    videoUrl = videoUrl,
                    isLiked = isLiked,
                    isSaved = isSaved,
                    speedText = speedText,
                    resolutionText = resolutionLabel.ifBlank { "Kualitas" },
                    isAmbientEnabled = isAmbientModeEnabled,
                    onToggleLike = onToggleLike,
                    onShareClick = onShareClick,
                    onCopyLinkClick = onCopyLinkClick,
                    onToggleSave = onToggleSave,
                    onCycleSpeed = onCycleSpeed,
                    onResolutionClick = onResolutionClick,
                    onToggleAmbient = onToggleAmbient,
                    onPipClick = onPipClick,
                    onToggleFullscreen = onToggleFullscreen
                )
            }

            item(key = "description_card") {
                PlayerDescriptionCard(
                    streamData = streamData,
                    isExpanded = isDescriptionExpanded,
                    onToggleExpand = onToggleDescription
                )
            }

            val relatedVideos = streamData?.relatedVideos.orEmpty()
            if (relatedVideos.isNotEmpty()) {
                item(key = "related_header") {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Video Terkait",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(
                    items = relatedVideos,
                    key = { it.id }
                ) { relVideo ->
                    VideoCard(
                        video = relVideo,
                        onClick = { onRelatedVideoClick(relVideo) },
                        onChannelClick = onChannelClick
                    )
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
