package com.example.yoouutt.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import com.example.yoouutt.PlayerKey

object YouTubeScreenTransitions {

    val appTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
        when {
            targetState.key is PlayerKey -> {
                (slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                ) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 140, easing = FastOutLinearInEasing)
                )).apply {
                    targetContentZIndex = 2f
                }
            }
            initialState.key is PlayerKey -> {
                (fadeIn(
                    animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
                ) togetherWith slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 220, easing = FastOutLinearInEasing)
                )).apply {
                    targetContentZIndex = -1f
                }
            }
            else -> {
                (fadeIn(
                    animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
                ) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing)
                )).apply {
                    targetContentZIndex = 1f
                }
            }
        }
    }
}
