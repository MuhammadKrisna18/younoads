package com.example.yoouutt.ui.player.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoouutt.playback.AutoplayCountdown
import com.example.yoouutt.ui.theme.CyberpunkGradient
import com.example.yoouutt.ui.theme.ElectricCyan

@Composable
fun AutoplayCountdownBanner(
    countdown: AutoplayCountdown?,
    onPlayNext: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressAnimatable = remember { Animatable(1f) }

    LaunchedEffect(countdown?.targetVideo?.id) {
        if (countdown != null) {
            progressAnimatable.snapTo(1f)
            progressAnimatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
        } else {
            progressAnimatable.snapTo(1f)
        }
    }

    AnimatedVisibility(
        visible = countdown != null,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(250)) + scaleOut(targetScale = 0.85f, animationSpec = tween(250)),
        modifier = modifier
    ) {
        if (countdown == null) return@AnimatedVisibility

        Box(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 360.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.92f))
                .border(BorderStroke(1.2.dp, CyberpunkGradient), RoundedCornerShape(16.dp))
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan)
                    )
                    Text(
                        text = "Video berikutnya dalam",
                        color = ElectricCyan,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    AnimatedContent(
                        targetState = countdown.secondsRemaining,
                        transitionSpec = {
                            (slideInVertically { -it } + fadeIn()) togetherWith
                                    (slideOutVertically { it } + fadeOut())
                        },
                        label = "seconds_roll"
                    ) { seconds ->
                        Text(
                            text = "$seconds",
                            color = ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "detik...",
                        color = ElectricCyan,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF262633))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressAnimatable.value.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(CyberpunkGradient)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = countdown.targetVideo.title,
                    color = Color.White,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )

                if (countdown.targetVideo.uploaderName.isNotBlank()) {
                    Text(
                        text = countdown.targetVideo.uploaderName,
                        color = Color(0xFFAAAAAA),
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Batal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onPlayNext,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Putar Sekarang",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
