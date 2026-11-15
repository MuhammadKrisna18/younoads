package com.example.yoouutt.ui.player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.LocalInPipMode
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.player.components.AudioTrackSelectionDialog
import com.example.yoouutt.ui.player.components.PlayerDetailContent
import com.example.yoouutt.ui.player.components.ResolutionSelectionDialog
import com.example.yoouutt.ui.player.components.ShareBottomSheet
import com.example.yoouutt.ui.player.components.VideoPlayerSection

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
    val configuration = LocalConfiguration.current
    val isDeviceLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val currentPlayback by viewModel.currentPlayback.collectAsStateWithLifecycle()
    val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()
    val isEffectiveFullscreen = isFullscreen || isDeviceLandscape
    val isDescriptionExpanded by viewModel.isDescriptionExpanded.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val isAutoplayEnabled by viewModel.isAutoplayEnabled.collectAsStateWithLifecycle()
    val isLoopEnabled by viewModel.isLoopEnabled.collectAsStateWithLifecycle()
    val speedIndex by viewModel.speedIndex.collectAsStateWithLifecycle()
    val currentResolution by viewModel.currentResolution.collectAsStateWithLifecycle()
    val availableResolutions by viewModel.availableResolutions.collectAsStateWithLifecycle()
    val isResolutionDialogVisible by viewModel.isResolutionDialogVisible.collectAsStateWithLifecycle()
    val currentAudioTrack by viewModel.currentAudioTrack.collectAsStateWithLifecycle()
    val availableAudioTracks by viewModel.availableAudioTracks.collectAsStateWithLifecycle()
    val isAudioTrackDialogVisible by viewModel.isAudioTrackDialogVisible.collectAsStateWithLifecycle()
    val playlistContext by viewModel.playlistContext.collectAsStateWithLifecycle()
    val resolutionLabel = currentResolution?.label.orEmpty()

    val isInPipMode = LocalInPipMode.current
    var isShareSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(videoUrl) {
        viewModel.initVideo(videoUrl)
    }

    DisposableEffect(isFullscreen, isDeviceLandscape) {
        if (isFullscreen) {
            activity?.setFullscreen(true)
        } else {
            activity?.setImmersiveMode(isDeviceLandscape)
        }
        onDispose {
            activity?.setFullscreen(false)
        }
    }

    LaunchedEffect(isDeviceLandscape) {
        if (!isDeviceLandscape && !isFullscreen) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    LaunchedEffect(isInPipMode) {
        if (isInPipMode) {
            viewModel.hideControls()
        }
    }

    val handleToggleFullscreen: () -> Unit = {
        if (isEffectiveFullscreen) {
            if (isDeviceLandscape) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            viewModel.setFullscreen(false)
        } else {
            viewModel.setFullscreen(true)
        }
    }

    BackHandler(enabled = isEffectiveFullscreen) {
        handleToggleFullscreen()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isEffectiveFullscreen) Color.Black else Color.Transparent)
    ) {
        val videoBoxModifier = if (isEffectiveFullscreen) {
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
            isFullscreen = isEffectiveFullscreen,
            isInPipMode = isInPipMode,
            resolutionLabel = resolutionLabel,
            hasAudioTracks = availableAudioTracks.size > 1,
            hasPlaylist = playlistContext != null,
            hasPrevious = (playlistContext?.currentIndex ?: 0) > 0,
            hasNext = (playlistContext?.let { it.currentIndex + 1 < it.videos.size } ?: false),
            onPreviousClick = viewModel::playPreviousInPlaylist,
            onNextClick = viewModel::playNextInPlaylist,
            onBack = onBack,
            onToggleFullscreen = handleToggleFullscreen,
            onAudioTrackClick = viewModel::showAudioTrackDialog,
            modifier = videoBoxModifier
        )

        if (!isEffectiveFullscreen) {
            val currentTitle = currentPlayback?.title?.takeIf { it.isNotBlank() } ?: fallbackTitle
            val currentUploader = currentPlayback?.uploaderName?.takeIf { it.isNotBlank() } ?: fallbackUploader
            val streamData = currentPlayback?.streamData

            PlayerDetailContent(
                currentTitle = currentTitle,
                currentUploader = currentUploader,
                streamData = streamData,
                playlistContext = playlistContext,
                isSaved = isSaved,
                isDescriptionExpanded = isDescriptionExpanded,
                isAutoplayEnabled = isAutoplayEnabled,
                isLoopEnabled = isLoopEnabled,
                speedText = "${viewModel.speeds[speedIndex]}x",
                resolutionLabel = resolutionLabel,
                audioTrackText = currentAudioTrack?.displayName.orEmpty(),
                videoUrl = videoUrl,
                onShareClick = { isShareSheetVisible = true },
                onCopyLinkClick = {
                    val shortUrl = PlayerUtils.formatShortUrl(videoUrl)
                    PlayerUtils.copyToClipboard(context, shortUrl, "Tautan berhasil disalin")
                },
                onToggleSave = viewModel::toggleSave,
                onToggleAutoplay = viewModel::toggleAutoplay,
                onToggleLoop = viewModel::toggleLoop,
                onCycleSpeed = viewModel::cyclePlaybackSpeed,
                onResolutionClick = viewModel::showResolutionDialog,
                onAudioTrackClick = viewModel::showAudioTrackDialog,
                onPipClick = { activity?.enterPipMode() },
                onToggleFullscreen = handleToggleFullscreen,
                onToggleDescription = viewModel::toggleDescription,
                onPlaylistVideoClick = viewModel::playPlaylistIndex,
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
            onSelectResolution = viewModel::selectResolution,
            onDismiss = viewModel::dismissResolutionDialog
        )
    }

    if (isAudioTrackDialogVisible) {
        AudioTrackSelectionDialog(
            availableAudioTracks = availableAudioTracks,
            currentAudioTrack = currentAudioTrack,
            onSelectAudioTrack = viewModel::selectAudioTrack,
            onDismiss = viewModel::dismissAudioTrackDialog
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
