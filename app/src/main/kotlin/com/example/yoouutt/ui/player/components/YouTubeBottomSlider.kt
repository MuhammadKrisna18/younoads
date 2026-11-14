package com.example.yoouutt.ui.player.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoouutt.ui.player.PlayerUtils
import com.example.yoouutt.ui.theme.ElectricCyan
import com.example.yoouutt.ui.theme.NeonMagenta
import com.example.yoouutt.ui.theme.YouTubeRed
import kotlin.math.roundToInt

@Composable
fun InteractiveSeekBar(
    currentPositionMs: Long,
    bufferedPositionMs: Long,
    durationMs: Long,
    onScrubbing: (Boolean, Long) -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val density = LocalDensity.current

    val totalDuration = durationMs.coerceAtLeast(1L)
    val progress = (currentPositionMs.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    val bufferedProgress = (bufferedPositionMs.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val activeProgress = if (isDragging) dragProgress else progress
    val animatedTrackHeight by animateDpAsState(
        targetValue = if (isDragging) 5.dp else 3.5.dp,
        label = "trackHeight"
    )
    val animatedThumbRadius by animateDpAsState(
        targetValue = if (isDragging) 8.5.dp else 6.dp,
        label = "thumbRadius"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .pointerInput(totalDuration) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val width = size.width.coerceAtLeast(1)
                    var currentX = down.position.x
                    var currentProgress = (currentX / width).coerceIn(0f, 1f)

                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    isDragging = true
                    dragProgress = currentProgress
                    onScrubbing(true, (currentProgress * totalDuration).toLong())

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (change.changedToUp()) {
                            change.consume()
                            break
                        }
                        if (change.position.x != currentX) {
                            change.consume()
                            currentX = change.position.x
                            currentProgress = (currentX / width).coerceIn(0f, 1f)
                            dragProgress = currentProgress
                            onScrubbing(true, (currentProgress * totalDuration).toLong())
                        }
                    }

                    isDragging = false
                    val finalProgress = (currentX / width).coerceIn(0f, 1f)
                    val targetMs = (finalProgress * totalDuration).toLong()
                    onSeek(targetMs)
                    onScrubbing(false, targetMs)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
        ) {
            val canvasWidth = size.width
            val centerY = size.height / 2f
            val trackStroke = animatedTrackHeight.toPx()

            drawLine(
                color = Color.White.copy(alpha = 0.25f),
                start = Offset(0f, centerY),
                end = Offset(canvasWidth, centerY),
                strokeWidth = trackStroke,
                cap = StrokeCap.Round
            )

            val bufferedX = canvasWidth * bufferedProgress
            if (bufferedX > 0f) {
                drawLine(
                    color = Color.White.copy(alpha = 0.45f),
                    start = Offset(0f, centerY),
                    end = Offset(bufferedX, centerY),
                    strokeWidth = trackStroke,
                    cap = StrokeCap.Round
                )
            }

            val playedX = canvasWidth * activeProgress
            if (playedX > 0f) {
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(ElectricCyan, NeonMagenta),
                        startX = 0f,
                        endX = playedX.coerceAtLeast(1f)
                    ),
                    start = Offset(0f, centerY),
                    end = Offset(playedX, centerY),
                    strokeWidth = trackStroke,
                    cap = StrokeCap.Round
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White, ElectricCyan, NeonMagenta),
                    center = Offset(playedX, centerY),
                    radius = animatedThumbRadius.toPx().coerceAtLeast(1f)
                ),
                radius = animatedThumbRadius.toPx(),
                center = Offset(playedX, centerY)
            )

            if (isDragging) {
                drawCircle(
                    color = Color.White,
                    radius = (animatedThumbRadius * 0.45f).toPx(),
                    center = Offset(playedX, centerY)
                )
            }
        }

        if (isDragging) {
            val scrubMs = (dragProgress * totalDuration).toLong()
            val timeText = PlayerUtils.formatTime(scrubMs)
            val badgeWidthApprox = with(density) { 56.dp.toPx() }
            val thumbX = totalWidthPx * dragProgress
            val badgeOffsetX = (thumbX - badgeWidthApprox / 2f)
                .coerceIn(0f, (totalWidthPx - badgeWidthApprox).coerceAtLeast(0f))

            Box(
                modifier = Modifier
                    .offset { IntOffset(badgeOffsetX.roundToInt(), -40) }
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = timeText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MinimalBottomProgressBar(
    currentPositionMs: Long,
    bufferedPositionMs: Long,
    durationMs: Long,
    modifier: Modifier = Modifier
) {
    val totalDuration = durationMs.coerceAtLeast(1L)
    val progress = (currentPositionMs.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    val bufferedProgress = (bufferedPositionMs.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.5.dp)
    ) {
        val canvasWidth = size.width
        val centerY = size.height / 2f
        val stroke = size.height

        drawLine(
            color = Color.White.copy(alpha = 0.2f),
            start = Offset(0f, centerY),
            end = Offset(canvasWidth, centerY),
            strokeWidth = stroke
        )

        val bufferedX = canvasWidth * bufferedProgress
        if (bufferedX > 0f) {
            drawLine(
                color = Color.White.copy(alpha = 0.4f),
                start = Offset(0f, centerY),
                end = Offset(bufferedX, centerY),
                strokeWidth = stroke
            )
        }

        val playedX = canvasWidth * progress
        if (playedX > 0f) {
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(ElectricCyan, NeonMagenta),
                    startX = 0f,
                    endX = playedX.coerceAtLeast(1f)
                ),
                start = Offset(0f, centerY),
                end = Offset(playedX, centerY),
                strokeWidth = stroke
            )
        }
    }
}

@Composable
fun YouTubeBottomSlider(
    currentPositionMs: Long,
    bufferedPositionMs: Long,
    durationMs: Long,
    isControlsVisible: Boolean,
    onScrubbing: (Boolean, Long) -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isControlsVisible) {
        InteractiveSeekBar(
            currentPositionMs = currentPositionMs,
            bufferedPositionMs = bufferedPositionMs,
            durationMs = durationMs,
            onScrubbing = onScrubbing,
            onSeek = onSeek,
            modifier = modifier
        )
    } else {
        MinimalBottomProgressBar(
            currentPositionMs = currentPositionMs,
            bufferedPositionMs = bufferedPositionMs,
            durationMs = durationMs,
            modifier = modifier
        )
    }
}
