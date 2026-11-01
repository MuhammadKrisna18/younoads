package com.example.yoouutt.ui.player

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import com.example.yoouutt.playback.PlaybackManager
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun InAppPipPlayer(
    playbackManager: PlaybackManager,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val pipWidthDp = 180.dp
    val pipHeightDp = 101.dp
    val pipWidthPx = with(density) { pipWidthDp.toPx() }
    val pipHeightPx = with(density) { pipHeightDp.toPx() }

    val initialX = screenWidthPx - pipWidthPx - with(density) { 16.dp.toPx() }
    val initialY = screenHeightPx - pipHeightPx - with(density) { 92.dp.toPx() }

    val animOffsetX = remember { Animatable(initialX) }
    val animOffsetY = remember { Animatable(initialY) }
    val coroutineScope = rememberCoroutineScope()

    val isPlaying by playbackManager.isPlaying.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .offset { IntOffset(animOffsetX.value.roundToInt(), animOffsetY.value.roundToInt()) }
            .size(pipWidthDp, pipHeightDp)
            .shadow(16.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)), RoundedCornerShape(14.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            animOffsetX.snapTo(animOffsetX.value + dragAmount.x)
                            animOffsetY.snapTo(animOffsetY.value + dragAmount.y)
                        }
                    },
                    onDragEnd = {
                        val marginPx = with(density) { 16.dp.toPx() }
                        val currentX = animOffsetX.value
                        val isDismissRight = currentX > screenWidthPx - pipWidthPx * 0.45f
                        val isDismissLeft = currentX < -pipWidthPx * 0.45f

                        if (isDismissRight || isDismissLeft) {
                            coroutineScope.launch {
                                val targetDismissX = if (isDismissRight) screenWidthPx + 60f else -pipWidthPx - 60f
                                animOffsetX.animateTo(targetDismissX, tween(200, easing = FastOutSlowInEasing))
                                playbackManager.stop()
                            }
                        } else {
                            val targetX = if (currentX + pipWidthPx / 2 < screenWidthPx / 2) {
                                marginPx
                            } else {
                                screenWidthPx - pipWidthPx - marginPx
                            }

                            val topBound = with(density) { 60.dp.toPx() }
                            val bottomBound = (screenHeightPx - pipHeightPx - with(density) { 92.dp.toPx() }).coerceAtLeast(topBound)
                            val targetY = animOffsetY.value.coerceIn(topBound, bottomBound)

                            coroutineScope.launch {
                                animOffsetX.animateTo(
                                    targetValue = targetX,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                            }
                            coroutineScope.launch {
                                animOffsetY.animateTo(
                                    targetValue = targetY,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                            }
                        }
                    }
                )
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = playbackManager.exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }
            },
            update = { view ->
                if (view.player != playbackManager.exoPlayer) {
                    view.player = playbackManager.exoPlayer
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onExpand)
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.72f))
                    .clickable { playbackManager.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.72f))
                    .clickable { playbackManager.stop() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
