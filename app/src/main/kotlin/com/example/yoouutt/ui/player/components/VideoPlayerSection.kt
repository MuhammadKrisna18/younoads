package com.example.yoouutt.ui.player.components

import android.view.HapticFeedbackConstants
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import com.example.yoouutt.ui.player.PlayerUtils
import com.example.yoouutt.ui.player.PlayerViewModel
import com.example.yoouutt.ui.theme.YouTubeRed

@Composable
fun VideoPlayerSection(
    viewModel: PlayerViewModel,
    videoUrl: String,
    fallbackTitle: String,
    fallbackUploader: String,
    isFullscreen: Boolean,
    isInPipMode: Boolean,
    resolutionLabel: String,
    hasPlaylist: Boolean = false,
    hasPrevious: Boolean = false,
    hasNext: Boolean = false,
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onBack: () -> Unit,
    onToggleFullscreen: () -> Unit,
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
    val autoplayCountdown by viewModel.autoplayCountdown.collectAsStateWithLifecycle()

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
                            onTap = { offset ->
                                val isForward = offset.x >= size.width / 2
                                if (viewModel.handleTap(isForward)) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                } else {
                                    viewModel.toggleControls()
                                }
                            },
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
                    if (isFullscreen) onToggleFullscreen() else onBack()
                },
                onPlayPauseClick = viewModel::togglePlayPause,
                onToggleFullscreen = onToggleFullscreen,
                onResolutionClick = viewModel::showResolutionDialog,
                onScrubbing = viewModel::onScrubbing,
                onSeek = viewModel::seekTo,
                hasPlaylist = hasPlaylist,
                hasPrevious = hasPrevious,
                hasNext = hasNext,
                onPreviousClick = onPreviousClick,
                onNextClick = onNextClick
            )

            seekFeedback?.let { feedback ->
                DoubleTapSeekFeedback(
                    visible = true,
                    isForward = feedback.isForward,
                    text = feedback.text
                )
            }

            AutoplayCountdownBanner(
                countdown = autoplayCountdown,
                onPlayNext = viewModel::playNextImmediately,
                onCancel = viewModel::cancelAutoplayCountdown,
                modifier = Modifier.align(Alignment.Center)
            )

            if (!controlsVisible && !isInPipMode) {
                MinimalBottomProgressBar(
                    currentPositionMs = currentPositionMs,
                    bufferedPositionMs = bufferedPositionMs,
                    durationMs = durationMs,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
