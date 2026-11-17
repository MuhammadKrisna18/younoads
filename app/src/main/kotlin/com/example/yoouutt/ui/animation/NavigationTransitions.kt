package com.example.yoouutt.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.CubicBezierEasing
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
import com.example.yoouutt.CustomPlaylistDetailKey
import com.example.yoouutt.HistoryKey
import com.example.yoouutt.LibraryKey
import com.example.yoouutt.PlayerKey
import com.example.yoouutt.PlaylistKey

object YouTubeScreenTransitions {

    private val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    private val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    val appTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
        val targetKey = targetState.key
        val initialKey = initialState.key

        when {
            targetKey is PlayerKey -> {
                (slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 360, easing = EmphasizedDecelerate)
                ) + fadeIn(tween(220))) togetherWith fadeOut(tween(160))
            }

            initialKey is PlayerKey -> {
                fadeIn(tween(160)) togetherWith (slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 320, easing = EmphasizedAccelerate)
                ) + fadeOut(tween(200)))
            }

            targetKey is ChannelKey || targetKey is HistoryKey || targetKey is LibraryKey || targetKey is CustomPlaylistDetailKey || targetKey is PlaylistKey -> {
                (slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 320, easing = EmphasizedDecelerate)
                ) + fadeIn(tween(200))) togetherWith (slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(durationMillis = 320, easing = EmphasizedAccelerate)
                ) + fadeOut(tween(150)))
            }

            initialKey is ChannelKey || initialKey is HistoryKey || initialKey is LibraryKey || initialKey is CustomPlaylistDetailKey || initialKey is PlaylistKey -> {
                (slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(durationMillis = 280, easing = EmphasizedDecelerate)
                ) + fadeIn(tween(160))) togetherWith (slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 280, easing = EmphasizedAccelerate)
                ) + fadeOut(tween(200)))
            }

            else -> {
                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
            }
        }
    }
}
