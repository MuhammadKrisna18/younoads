package com.example.yoouutt.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
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
import com.example.yoouutt.ChannelKey
import com.example.yoouutt.HistoryKey
import com.example.yoouutt.PlayerKey

object YouTubeScreenTransitions {

    val appTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
        val targetKey = targetState.key
        val initialKey = initialState.key

        when {
            targetKey is PlayerKey -> {
                (slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(200))) togetherWith fadeOut(tween(150))
            }

            initialKey is PlayerKey -> {
                fadeIn(tween(150)) togetherWith (slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(200)))
            }

            targetKey is ChannelKey || targetKey is HistoryKey -> {
                (slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(200))) togetherWith (slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(150)))
            }

            initialKey is ChannelKey || initialKey is HistoryKey -> {
                (slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(150))) togetherWith (slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(200)))
            }

            else -> {
                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
            }
        }
    }
}
