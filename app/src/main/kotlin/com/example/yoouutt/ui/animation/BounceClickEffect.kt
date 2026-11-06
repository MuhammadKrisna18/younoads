package com.example.yoouutt.ui.animation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.launch

fun Modifier.bounceClick(
    enabled: Boolean = true,
    scaleDown: Float = 0.88f,
    scaleUp: Float = 1.10f,
    onClick: () -> Unit
): Modifier = composed {
    val view = LocalView.current
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    this
        .scale(scale.value)
        .clickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                coroutineScope.launch {
                    scale.animateTo(
                        targetValue = scaleDown,
                        animationSpec = tween(durationMillis = 60, easing = FastOutSlowInEasing)
                    )
                    scale.animateTo(
                        targetValue = scaleUp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    )
                }
                onClick()
            }
        )
}
