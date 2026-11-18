package com.example.yoouutt.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import com.example.yoouutt.HistoryKey
import com.example.yoouutt.Main
import com.example.yoouutt.PlayerKey
import com.example.yoouutt.SettingsKey

object GlobalNavigationTransitions {

    private fun isTopLevel(key: Any?): Boolean =
        key is Main || key is HistoryKey || key is SettingsKey

    val forwardTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
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
            isTopLevel(initialState.key) && isTopLevel(targetState.key) -> {
                (fadeIn(
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                ) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 110, easing = FastOutLinearInEasing)
                )).apply {
                    targetContentZIndex = 0f
                }
            }
            else -> {
                (slideInHorizontally(
                    initialOffsetX = { (it * 0.25f).toInt() },
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
                ) togetherWith (
                    slideOutHorizontally(
                        targetOffsetX = { -(it * 0.15f).toInt() },
                        animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing)
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 140, easing = FastOutLinearInEasing)
                    )
                )).apply {
                    targetContentZIndex = 1f
                }
            }
        }
    }

    val popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
        when {
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
            isTopLevel(initialState.key) && isTopLevel(targetState.key) -> {
                (fadeIn(
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                ) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 110, easing = FastOutLinearInEasing)
                )).apply {
                    targetContentZIndex = 0f
                }
            }
            else -> {
                (slideInHorizontally(
                    initialOffsetX = { -(it * 0.15f).toInt() },
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
                ) togetherWith (
                    slideOutHorizontally(
                        targetOffsetX = { (it * 0.25f).toInt() },
                        animationSpec = tween(durationMillis = 220, easing = FastOutLinearInEasing)
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 140, easing = FastOutLinearInEasing)
                    )
                )).apply {
                    targetContentZIndex = -1f
                }
            }
        }
    }

    val appTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = forwardTransitionSpec
}
