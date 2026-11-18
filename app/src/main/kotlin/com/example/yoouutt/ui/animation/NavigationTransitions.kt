package com.example.yoouutt.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

object YouTubeScreenTransitions {

    val appTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
        (fadeIn(
            animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
        ) togetherWith fadeOut(
            animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing)
        )).apply {
            targetContentZIndex = 1f
        }
    }
}
