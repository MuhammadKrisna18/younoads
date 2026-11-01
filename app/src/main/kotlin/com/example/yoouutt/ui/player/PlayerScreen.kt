package com.example.yoouutt.ui.player

import android.content.Context
import android.content.Intent
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.yoouutt.LocalInPipMode
import com.example.yoouutt.YoouuttApp
import com.example.yoouutt.playback.PlaybackManager
import com.example.yoouutt.ui.common.BrandLogo
import com.example.yoouutt.ui.common.BrandRed
import com.example.yoouutt.ui.feed.VideoCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    videoUrl: String,
    fallbackTitle: String,
    fallbackUploader: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onChannelClick: ((channelUrl: String, channelName: String) -> Unit)? = null,
    playbackManager: PlaybackManager = YoouuttApp.instance.playbackManager
) {
    val currentPlayback by playbackManager.currentPlayback.collectAsStateWithLifecycle()
    val isLoading by playbackManager.isLoading.collectAsStateWithLifecycle()
    val errorMessage by playbackManager.errorMessage.collectAsStateWithLifecycle()
    val resumePositionMs by playbackManager.resumePositionMs.collectAsStateWithLifecycle()
    val exoPlayer = playbackManager.exoPlayer
    val context = LocalContext.current
    val view = LocalView.current
    val activity = remember(context) { context.findActivity() }
    val isInPip = LocalInPipMode.current

    var isFullscreen by remember { mutableStateOf(false) }
    var seekFeedbackText by remember { mutableStateOf<String?>(null) }
    var seekFeedbackIsForward by remember { mutableStateOf(true) }

    var resumeBannerVisible by remember(videoUrl) { mutableStateOf(false) }
    var resumeBannerText by remember(videoUrl) { mutableStateOf("") }
    var hasAutoResumed by remember(videoUrl) { mutableStateOf(false) }

    val speeds = remember { listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f) }
    var speedIndex by remember { mutableIntStateOf(1) }
    var isDescriptionExpanded by remember(videoUrl) { mutableStateOf(false) }

    LaunchedEffect(videoUrl) {
        playbackManager.playVideo(videoUrl, fallbackTitle, fallbackUploader)
    }

    LaunchedEffect(resumePositionMs) {
        val savedPosition = resumePositionMs
        if (savedPosition != null && savedPosition > 0L && !hasAutoResumed) {
            resumeBannerText = "Melanjutkan dari ${formatTime(savedPosition)}"
            resumeBannerVisible = true
            hasAutoResumed = true
        }
    }

    LaunchedEffect(activity) {
        activity?.updatePipAutoEnter()
    }

    LaunchedEffect(seekFeedbackText) {
        if (seekFeedbackText != null) {
            delay(750)
            seekFeedbackText = null
        }
    }

    LaunchedEffect(resumeBannerVisible) {
        if (resumeBannerVisible) {
            delay(5000)
            resumeBannerVisible = false
        }
    }

    BackHandler(enabled = isFullscreen) {
        isFullscreen = false
        activity?.setFullscreen(false)
    }

    DisposableEffect(Unit) {
        onDispose {
            playbackManager.saveProgress()
            if (isFullscreen) {
                activity?.setFullscreen(false)
            }
        }
    }

    if (isInPip) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    }
                },
                update = { view ->
                    if (view.player != exoPlayer) {
                        view.player = exoPlayer
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    val toggleFullscreen: () -> Unit = {
        val nextState = !isFullscreen
        isFullscreen = nextState
        activity?.setFullscreen(nextState)
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (!isFullscreen) {
                TopAppBar(
                    title = { BrandLogo() },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { activity?.enterPipMode() }) {
                            Icon(
                                imageVector = Icons.Default.PictureInPictureAlt,
                                contentDescription = "PiP",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(onClick = toggleFullscreen) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    ) { innerPadding ->
        val contentModifier = if (isFullscreen) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        }

        Column(modifier = contentModifier) {
            val videoBoxModifier = if (isFullscreen) {
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            } else {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            }

            Box(modifier = videoBoxModifier) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (errorMessage != null) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { playbackManager.playVideo(videoUrl, fallbackTitle, fallbackUploader) },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Retry",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    AndroidView(
                        factory = { ctx ->
                            DoubleTapPlayerView(ctx).apply {
                                player = exoPlayer
                                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                useController = true
                                setShowFastForwardButton(false)
                                setShowRewindButton(false)
                                setFullscreenButtonClickListener {
                                    toggleFullscreen()
                                }
                                onDoubleTapSeek = { isForward ->
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    if (isForward) {
                                        val duration = if (exoPlayer.duration > 0) exoPlayer.duration else Long.MAX_VALUE
                                        val newPos = (exoPlayer.currentPosition + 10000L).coerceAtMost(duration)
                                        exoPlayer.seekTo(newPos)
                                        seekFeedbackIsForward = true
                                        seekFeedbackText = "+10 detik"
                                    } else {
                                        val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                                        exoPlayer.seekTo(newPos)
                                        seekFeedbackIsForward = false
                                        seekFeedbackText = "-10 detik"
                                    }
                                }
                            }
                        },
                        update = { view ->
                            if (view.player != exoPlayer) {
                                view.player = exoPlayer
                            }
                        },
                        modifier = Modifier.matchParentSize()
                    )
                }

                DoubleTapSeekFeedback(
                    visible = seekFeedbackText != null,
                    isForward = seekFeedbackIsForward,
                    text = seekFeedbackText.orEmpty()
                )

                if (resumeBannerVisible) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 68.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color.Black.copy(alpha = 0.88f))
                            .border(
                                BorderStroke(0.5.dp, Color.White.copy(alpha = 0.25f)),
                                RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = resumeBannerText,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Mulai dari Awal",
                            color = BrandRed,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                exoPlayer.seekTo(0L)
                                resumeBannerVisible = false
                            }
                        )
                    }
                }

                if (isFullscreen) {
                    IconButton(
                        onClick = toggleFullscreen,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White
                        )
                    }
                }
            }

            if (!isFullscreen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionPill(
                        icon = Icons.Default.Speed,
                        label = "${speeds[speedIndex]}x",
                        onClick = {
                            speedIndex = (speedIndex + 1) % speeds.size
                            exoPlayer.setPlaybackSpeed(speeds[speedIndex])
                        }
                    )
                    ActionPill(
                        icon = Icons.Default.PictureInPictureAlt,
                        label = "PiP",
                        onClick = {
                            activity?.enterPipMode()
                        }
                    )
                    ActionPill(
                        icon = Icons.Default.Fullscreen,
                        label = "Layar Penuh",
                        onClick = toggleFullscreen
                    )
                    ActionPill(
                        icon = Icons.Default.Share,
                        label = "Bagikan",
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, videoUrl)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    )
                }

                val currentTitle = currentPlayback?.title?.takeIf { it.isNotBlank() } ?: fallbackTitle
                val currentUploader = currentPlayback?.uploaderName?.takeIf { it.isNotBlank() } ?: fallbackUploader

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = currentTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val channelRowModifier = if (onChannelClick != null) {
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                val targetUrl = currentPlayback?.streamData?.uploaderUrl?.takeIf { it.isNotBlank() }
                                    ?: currentUploader
                                onChannelClick(targetUrl, currentUploader)
                            }
                            .padding(vertical = 4.dp)
                    } else {
                        Modifier.fillMaxWidth()
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = channelRowModifier
                    ) {
                        val avatarUrl = currentPlayback?.streamData?.uploaderAvatarUrl.orEmpty()
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarUrl.isNotBlank()) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = currentUploader,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                            } else {
                                val initial = currentUploader.trim().firstOrNull()?.uppercase() ?: "Y"
                                Text(
                                    text = initial,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = currentUploader,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    val streamData = currentPlayback?.streamData
                    if (streamData != null) {
                        val hasMeta = streamData.viewCountText.isNotBlank() || streamData.uploadDateText.isNotBlank()
                        val hasDesc = streamData.description.isNotBlank()

                        if (hasMeta || hasDesc) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                                    .padding(14.dp)
                                    .animateContentSize()
                            ) {
                                Column {
                                    if (hasMeta) {
                                        val metaLine = buildString {
                                            if (streamData.viewCountText.isNotBlank()) append(streamData.viewCountText)
                                            if (streamData.viewCountText.isNotBlank() && streamData.uploadDateText.isNotBlank()) append("  •  ")
                                            if (streamData.uploadDateText.isNotBlank()) append(streamData.uploadDateText)
                                        }
                                        Text(
                                            text = metaLine,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        if (hasDesc) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }

                                    if (hasDesc) {
                                        Text(
                                            text = streamData.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFFDDDDDD),
                                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (isDescriptionExpanded) "Sembunyikan" else "...selengkapnya",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        val relatedVideos = streamData.relatedVideos
                        if (relatedVideos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Video Terkait",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            relatedVideos.forEach { relVideo ->
                                VideoCard(
                                    video = relVideo,
                                    onClick = {
                                        playbackManager.playVideo(relVideo.url, relVideo.title, relVideo.uploaderName)
                                    },
                                    onChannelClick = onChannelClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@Composable
private fun ActionPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BoxScope.DoubleTapSeekFeedback(
    visible: Boolean,
    isForward: Boolean,
    text: String
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(100)) + scaleIn(tween(180, easing = FastOutSlowInEasing)),
        exit = fadeOut(tween(350, easing = FastOutSlowInEasing)),
        modifier = Modifier.align(if (isForward) Alignment.CenterEnd else Alignment.CenterStart)
    ) {
        val shape = if (isForward) {
            RoundedCornerShape(topStartPercent = 100, bottomStartPercent = 100)
        } else {
            RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
        }

        val gradient = if (isForward) {
            Brush.horizontalGradient(
                listOf(
                    Color.White.copy(alpha = 0.04f),
                    Color.White.copy(alpha = 0.22f)
                )
            )
        } else {
            Brush.horizontalGradient(
                listOf(
                    Color.White.copy(alpha = 0.22f),
                    Color.White.copy(alpha = 0.04f)
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.38f)
                .clip(shape)
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isForward) Icons.Default.FastForward else Icons.Default.FastRewind,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = text,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private class DoubleTapPlayerView(context: Context) : PlayerView(context) {
    var onDoubleTapSeek: ((Boolean) -> Unit)? = null

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean = true

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val bottomThreshold = height - (70 * resources.displayMetrics.density)
            if (isControllerFullyVisible && e.y >= bottomThreshold) {
                return false
            }
            val isForward = e.x >= width / 2
            onDoubleTapSeek?.invoke(isForward)
            post { hideController() }
            return true
        }
    })

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
}
