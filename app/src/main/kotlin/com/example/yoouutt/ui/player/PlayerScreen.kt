package com.example.yoouutt.ui.player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
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
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoouutt.LocalInPipMode
import com.example.yoouutt.MainActivity
import com.example.yoouutt.ui.common.AppViewModelProvider
import com.example.yoouutt.ui.player.components.AddToPlaylistBottomSheet
import com.example.yoouutt.ui.player.components.AudioTrackSelectionDialog
import com.example.yoouutt.ui.player.components.CreatePlaylistDialog
import com.example.yoouutt.ui.player.components.PlaybackSpeedSelectionDialog
import com.example.yoouutt.ui.player.components.PlayerDetailContent
import com.example.yoouutt.ui.player.components.PlayerSettingsBottomSheet
import com.example.yoouutt.ui.player.components.ResolutionSelectionDialog
import com.example.yoouutt.ui.player.components.SeekIntervalSelectionDialog
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
    val isSettingsSheetVisible by viewModel.isSettingsSheetVisible.collectAsStateWithLifecycle()
    val isSpeedDialogVisible by viewModel.isSpeedDialogVisible.collectAsStateWithLifecycle()
    val seekIntervalSeconds by viewModel.seekIntervalSeconds.collectAsStateWithLifecycle()
    val isSeekIntervalDialogVisible by viewModel.isSeekIntervalDialogVisible.collectAsStateWithLifecycle()
    val currentAudioTrack by viewModel.currentAudioTrack.collectAsStateWithLifecycle()
    val availableAudioTracks by viewModel.availableAudioTracks.collectAsStateWithLifecycle()
    val isAudioTrackDialogVisible by viewModel.isAudioTrackDialogVisible.collectAsStateWithLifecycle()
    val playlistContext by viewModel.playlistContext.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val recommendedVideos by viewModel.recommendedVideos.collectAsStateWithLifecycle()
    val isLoadingRecommendations by viewModel.isLoadingRecommendations.collectAsStateWithLifecycle()
    val customPlaylists by viewModel.customPlaylists.collectAsStateWithLifecycle()
    val isAddToPlaylistSheetVisible by viewModel.isAddToPlaylistSheetVisible.collectAsStateWithLifecycle()
    val isCreatePlaylistDialogVisible by viewModel.isCreatePlaylistDialogVisible.collectAsStateWithLifecycle()
    val currentVideoPlaylistIds by viewModel.currentVideoPlaylistIds.collectAsStateWithLifecycle()
    val resolutionLabel = currentResolution?.label.orEmpty()

    val isInPipMode = LocalInPipMode.current
    var videoBounds by remember { mutableStateOf<Rect?>(null) }
    var isShareSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying, videoBounds) {
        (activity as? MainActivity)?.setPlayerActive(true, videoBounds)
    }

    DisposableEffect(Unit) {
        onDispose {
            (activity as? MainActivity)?.setPlayerActive(false, null)
        }
    }

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
        val baseModifier = if (isEffectiveFullscreen) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        }
        val videoBoxModifier = baseModifier.onGloballyPositioned { coordinates ->
            val bounds = coordinates.boundsInWindow()
            videoBounds = Rect(
                bounds.left.toInt(),
                bounds.top.toInt(),
                bounds.right.toInt(),
                bounds.bottom.toInt()
            )
        }

        VideoPlayerSection(
            viewModel = viewModel,
            videoUrl = videoUrl,
            fallbackTitle = fallbackTitle,
            fallbackUploader = fallbackUploader,
            isFullscreen = isEffectiveFullscreen,
            isInPipMode = isInPipMode,
            resolutionLabel = resolutionLabel,
            hasPlaylist = playlistContext != null,
            hasPrevious = (playlistContext?.currentIndex ?: 0) > 0,
            hasNext = (playlistContext?.let { it.currentIndex + 1 < it.videos.size } ?: false),
            onPreviousClick = viewModel::playPreviousInPlaylist,
            onNextClick = viewModel::playNextInPlaylist,
            onBack = onBack,
            onToggleFullscreen = handleToggleFullscreen,
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
                videoUrl = videoUrl,
                onShareClick = { isShareSheetVisible = true },
                onCopyLinkClick = {
                    val shortUrl = PlayerUtils.formatShortUrl(videoUrl)
                    PlayerUtils.copyToClipboard(context, shortUrl, "Tautan berhasil disalin")
                },
                onToggleSave = viewModel::toggleSave,
                onAddToPlaylistClick = viewModel::showAddToPlaylistSheet,
                onSettingsClick = viewModel::showSettingsSheet,
                onToggleDescription = viewModel::toggleDescription,
                onPlaylistVideoClick = viewModel::playPlaylistIndex,
                onChannelClick = onChannelClick,
                onRelatedVideoClick = { relVideo ->
                    viewModel.playRelatedVideo(relVideo.url, relVideo.title, relVideo.uploaderName)
                },
                recommendedVideos = recommendedVideos,
                isLoadingRecommendations = isLoadingRecommendations,
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

    if (isSettingsSheetVisible) {
        val currentSpeed = viewModel.speeds[speedIndex]
        val formattedSpeedText = if (currentSpeed == 1.0f) "Normal (1.0x)" else "${currentSpeed}x"

        PlayerSettingsBottomSheet(
            resolutionLabel = resolutionLabel,
            speedText = formattedSpeedText,
            seekIntervalText = "$seekIntervalSeconds detik",
            audioTrackText = currentAudioTrack?.displayName.orEmpty(),
            hasMultipleAudioTracks = availableAudioTracks.size > 1,
            isLoopEnabled = isLoopEnabled,
            isAutoplayEnabled = isAutoplayEnabled,
            onSelectQualityClick = {
                viewModel.dismissSettingsSheet()
                viewModel.showResolutionDialog()
            },
            onSelectSpeedClick = {
                viewModel.dismissSettingsSheet()
                viewModel.showSpeedDialog()
            },
            onSelectSeekIntervalClick = {
                viewModel.dismissSettingsSheet()
                viewModel.showSeekIntervalDialog()
            },
            onSelectAudioTrackClick = {
                viewModel.dismissSettingsSheet()
                viewModel.showAudioTrackDialog()
            },
            onAddToPlaylistClick = viewModel::showAddToPlaylistSheet,
            onToggleLoop = viewModel::toggleLoop,
            onToggleAutoplay = viewModel::toggleAutoplay,
            onDismiss = viewModel::dismissSettingsSheet
        )
    }

    if (isSeekIntervalDialogVisible) {
        SeekIntervalSelectionDialog(
            availableIntervals = viewModel.availableSeekIntervals,
            currentIntervalSeconds = seekIntervalSeconds,
            onSelectInterval = viewModel::selectSeekInterval,
            onDismiss = viewModel::dismissSeekIntervalDialog
        )
    }

    if (isSpeedDialogVisible) {
        PlaybackSpeedSelectionDialog(
            availableSpeeds = viewModel.speeds,
            currentSpeedIndex = speedIndex,
            onSelectSpeed = { index ->
                viewModel.selectSpeed(index)
                viewModel.dismissSpeedDialog()
            },
            onDismiss = viewModel::dismissSpeedDialog
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

    if (isAddToPlaylistSheetVisible) {
        AddToPlaylistBottomSheet(
            playlists = customPlaylists,
            selectedPlaylistIds = currentVideoPlaylistIds,
            onTogglePlaylist = viewModel::toggleVideoInPlaylist,
            onCreateNewPlaylistClick = viewModel::showCreatePlaylistDialog,
            onDismiss = viewModel::dismissAddToPlaylistSheet
        )
    }

    if (isCreatePlaylistDialogVisible) {
        CreatePlaylistDialog(
            onDismiss = viewModel::dismissCreatePlaylistDialog,
            onCreate = viewModel::createPlaylistAndAddCurrentVideo
        )
    }
}
