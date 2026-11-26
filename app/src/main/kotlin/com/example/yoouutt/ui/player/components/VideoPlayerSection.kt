package com.example.yoouutt.ui.player.components

import android.view.HapticFeedbackConstants
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import com.example.yoouutt.R
import com.example.yoouutt.ui.player.PlayerUtils
import com.example.yoouutt.ui.player.PlayerViewModel

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
    val isFastForwarding by viewModel.isFastForwarding.collectAsStateWithLifecycle()
    val currentPlayback by viewModel.currentPlayback.collectAsStateWithLifecycle()
    val chapters = currentPlayback?.streamData?.chapters.orEmpty()
    var isHoldingFastForward by remember { mutableStateOf(false) }

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
                        text = stringResource(R.string.common_retry),
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
                            onPress = {
                                isHoldingFastForward = false
                                try {
                                    tryAwaitRelease()
                                } finally {
                                    if (isHoldingFastForward) {
                                        viewModel.stopFastForward()
                                        isHoldingFastForward = false
                                    }
                                }
                            },
                            onLongPress = {
                                if (viewModel.isPlaying.value) {
                                    isHoldingFastForward = true
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    viewModel.startFastForward()
                                }
                            },
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
                onNextClick = onNextClick,
                chapters = chapters
            )

            AnimatedVisibility(
                visible = isFastForwarding,
                enter = fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.85f),
                exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.85f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (isFullscreen) 28.dp else 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2X",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            seekFeedback?.let { feedback ->
                DoubleTapSeekFeedback(
                    visible = true,
                    isForward = feedback.isForward,
                    text = stringResource(
                        if (feedback.isForward) R.string.seek_forward_feedback else R.string.seek_backward_feedback,
                        feedback.seconds
                    )
                )
            }

            if (!controlsVisible && !isInPipMode) {
                MinimalBottomProgressBar(
                    currentPositionMs = currentPositionMs,
                    bufferedPositionMs = bufferedPositionMs,
                    durationMs = durationMs,
                    chapters = chapters,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
