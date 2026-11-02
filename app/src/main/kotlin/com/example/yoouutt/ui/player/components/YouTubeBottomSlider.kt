package com.example.yoouutt.ui.player.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

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
    val totalDuration = durationMs.coerceAtLeast(1L)
    val progress = (currentPositionMs.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    val bufferedProgress = (bufferedPositionMs.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val activeProgress = if (isDragging) dragProgress else progress
    val trackHeight = if (isControlsVisible || isDragging) 3.5.dp else 2.dp
    val thumbRadius = if (isDragging) 7.dp else 5.5.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isControlsVisible) 32.dp else 4.dp)
            .pointerInput(totalDuration, isControlsVisible) {
                if (!isControlsVisible) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val width = size.width.coerceAtLeast(1)
                    var currentX = down.position.x
                    val initialProgress = (currentX / width).coerceIn(0f, 1f)

                    isDragging = true
                    dragProgress = initialProgress
                    onScrubbing(true, (initialProgress * totalDuration).toLong())

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
                            val newProgress = (currentX / width).coerceIn(0f, 1f)
                            dragProgress = newProgress
                            onScrubbing(true, (newProgress * totalDuration).toLong())
                        }
                    }

                    isDragging = false
                    val finalProgress = (currentX / width).coerceIn(0f, 1f)
                    val targetMs = (finalProgress * totalDuration).toLong()
                    onScrubbing(false, targetMs)
                    onSeek(targetMs)
                }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = Offset(0f, canvasHeight / 2),
                end = Offset(canvasWidth, canvasHeight / 2),
                strokeWidth = canvasHeight
            )

            val bufferedX = canvasWidth * bufferedProgress
            if (bufferedX > 0f) {
                drawLine(
                    color = Color.White.copy(alpha = 0.4f),
                    start = Offset(0f, canvasHeight / 2),
                    end = Offset(bufferedX, canvasHeight / 2),
                    strokeWidth = canvasHeight
                )
            }

            val playedX = canvasWidth * activeProgress
            if (playedX > 0f) {
                drawLine(
                    color = Color(0xFFE50914),
                    start = Offset(0f, canvasHeight / 2),
                    end = Offset(playedX, canvasHeight / 2),
                    strokeWidth = canvasHeight
                )
            }

            if (isControlsVisible || isDragging) {
                drawCircle(
                    color = Color(0xFFE50914),
                    radius = thumbRadius.toPx(),
                    center = Offset(playedX, canvasHeight / 2)
                )
            }
        }
    }
}

