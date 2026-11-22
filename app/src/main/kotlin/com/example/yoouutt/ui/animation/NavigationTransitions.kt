package com.example.yoouutt.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.Alignment
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

object GlobalNavigationTransitions {

    val forwardTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
        (fadeIn(
            animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
        ) togetherWith fadeOut(
            animationSpec = tween(durationMillis = 130, easing = FastOutLinearInEasing)
        )).apply {
            targetContentZIndex = 1f
        }
    }

    val popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
        (fadeIn(
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        ) togetherWith (
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            ) + shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 180, easing = FastOutLinearInEasing)
            )
        )).apply {
            targetContentZIndex = 0f
        }
    }

    val predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform = {
        (fadeIn(
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        ) togetherWith (
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            ) + shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 180, easing = FastOutLinearInEasing)
            )
        )).apply {
            targetContentZIndex = 0f
        }
    }

    val appTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = forwardTransitionSpec
}
