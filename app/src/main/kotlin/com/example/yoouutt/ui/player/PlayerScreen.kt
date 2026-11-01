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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.yoouutt.LocalInPipMode
import com.example.yoouutt.YoouuttApp
import com.example.yoouutt.playback.PlaybackManager
import com.example.yoouutt.ui.feed.VideoCard
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    videoUrl: String,
    fallbackTitle: String,
    fallbackUploader: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onChannelClick: ((channelUrl: String, channelName: String) -> Unit)? = null
) {
    val playbackManager: PlaybackManager = YoouuttApp.instance.playbackManager
    val context = LocalContext.current
    val view = LocalView.current
    val activity = context.findActivity()

    val currentPlayback by playbackManager.currentPlayback.collectAsStateWithLifecycle()
    val isLoading by playbackManager.isLoading.collectAsStateWithLifecycle()
    val errorMessage by playbackManager.errorMessage.collectAsStateWithLifecycle()
    val isPlaying by playbackManager.isPlaying.collectAsStateWithLifecycle()

    val exoPlayer = playbackManager.exoPlayer
    val isInPipMode = LocalInPipMode.current

    var isFullscreen by remember { mutableStateOf(false) }
    var resumeBannerVisible by remember { mutableStateOf(false) }
    var resumeSavedPosition by remember { mutableStateOf(0L) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    var isLiked by remember(videoUrl) { mutableStateOf(false) }
    var isSaved by remember(videoUrl) { mutableStateOf(false) }
    var isSubscribed by remember(fallbackUploader) { mutableStateOf(false) }

    var seekFeedbackText by remember { mutableStateOf<String?>(null) }
    var seekFeedbackIsForward by remember { mutableStateOf(true) }

    val speeds = remember { listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f) }
    var speedIndex by remember { mutableIntStateOf(2) }

    LaunchedEffect(seekFeedbackText) {
        if (seekFeedbackText != null) {
            delay(650)
            seekFeedbackText = null
        }
    }

    LaunchedEffect(videoUrl) {
        val active = currentPlayback
        if (active == null || active.videoUrl != videoUrl) {
            playbackManager.playVideo(videoUrl, fallbackTitle, fallbackUploader)
        }
    }

    LaunchedEffect(videoUrl) {
        val videoId = videoUrl.substringAfter("v=").substringBefore("&")
        val item = YoouuttApp.instance.historyRepository.getWatchHistory(videoId)
        val savedPos = item?.lastPlaybackPositionMs
        if (savedPos != null && savedPos > 5000L) {
            resumeSavedPosition = savedPos
            resumeBannerVisible = true
        } else {
            resumeBannerVisible = false
        }
    }

    LaunchedEffect(resumeBannerVisible) {
        if (resumeBannerVisible) {
            delay(5000)
            resumeBannerVisible = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (activity != null && isFullscreen) {
                activity.setFullscreen(false)
            }
        }
    }

    BackHandler {
        if (isFullscreen) {
            isFullscreen = false
            activity?.setFullscreen(false)
        } else {
            onBack()
        }
    }

    val toggleFullscreen: () -> Unit = {
        val nextState = !isFullscreen
        isFullscreen = nextState
        activity?.setFullscreen(nextState)
    }

    if (isInPipMode) {
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
                update = { pView ->
                    if (pView.player != exoPlayer) {
                        pView.player = exoPlayer
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val contentModifier = if (isFullscreen) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
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
                        update = { pView ->
                            if (pView.player != exoPlayer) {
                                pView.player = exoPlayer
                            }
                        },
                        modifier = Modifier.matchParentSize()
                    )
                }

                if (!isFullscreen) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Tutup",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
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
                            text = "Lanjutkan dari ${formatTime(resumeSavedPosition)}?",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Ya",
                            color = Color(0xFFE50914),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                exoPlayer.seekTo(resumeSavedPosition)
                                resumeBannerVisible = false
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Batal",
                            color = Color(0xFFAAAAAA),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.clickable { resumeBannerVisible = false }
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    val currentTitle = currentPlayback?.title?.takeIf { it.isNotBlank() } ?: fallbackTitle
                val currentUploader = currentPlayback?.uploaderName?.takeIf { it.isNotBlank() } ?: fallbackUploader
                val streamData = currentPlayback?.streamData

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = currentTitle,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (streamData != null) {
                        val metaLine = buildString {
                            if (streamData.viewCountText.isNotBlank()) append(streamData.viewCountText)
                            if (streamData.viewCountText.isNotBlank() && streamData.uploadDateText.isNotBlank()) append(" • ")
                            if (streamData.uploadDateText.isNotBlank()) append(streamData.uploadDateText)
                        }
                        if (metaLine.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = metaLine,
                                fontSize = 12.sp,
                                color = Color(0xFFAAAAAA)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val avatarUrl = streamData?.uploaderAvatarUrl.orEmpty()
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF272727))
                            .clickable {
                                val targetUrl = streamData?.uploaderUrl?.takeIf { it.isNotBlank() } ?: currentUploader
                                onChannelClick?.invoke(targetUrl, currentUploader)
                            },
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
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val targetUrl = streamData?.uploaderUrl?.takeIf { it.isNotBlank() } ?: currentUploader
                                onChannelClick?.invoke(targetUrl, currentUploader)
                            }
                    ) {
                        Text(
                            text = currentUploader,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val subText = streamData?.uploaderSubscriberCountText.orEmpty()
                        if (subText.isNotBlank()) {
                            Text(
                                text = subText,
                                fontSize = 11.5.sp,
                                color = Color(0xFFAAAAAA),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (isSubscribed) Color(0xFF272727) else Color.White)
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                isSubscribed = !isSubscribed
                            }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSubscribed) "Disubscribe" else "Subscribe",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSubscribed) Color.White else Color.Black
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionPill(
                        icon = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        label = if (isLiked) "Disukai" else "Suka",
                        isActive = isLiked,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            isLiked = !isLiked
                        }
                    )

                    ActionPill(
                        icon = Icons.Outlined.Share,
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

                    ActionPill(
                        icon = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        label = if (isSaved) "Tersimpan" else "Simpan",
                        isActive = isSaved,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            isSaved = !isSaved
                        }
                    )

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
                }

                if (streamData != null) {
                    val hasMeta = streamData.viewCountText.isNotBlank() || streamData.uploadDateText.isNotBlank()
                    val hasDesc = streamData.description.isNotBlank()

                    if (hasMeta || hasDesc) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1F1F1F))
                                .clickable {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    isDescriptionExpanded = !isDescriptionExpanded
                                }
                                .padding(12.dp)
                                .animateContentSize()
                        ) {
                            Column {
                                if (hasMeta) {
                                    val metaLine = buildString {
                                        if (streamData.viewCountText.isNotBlank()) append(streamData.viewCountText)
                                        if (streamData.viewCountText.isNotBlank() && streamData.uploadDateText.isNotBlank()) append("   ")
                                        if (streamData.uploadDateText.isNotBlank()) append(streamData.uploadDateText)
                                    }
                                    Text(
                                        text = metaLine,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    if (hasDesc) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }

                                if (hasDesc) {
                                    Text(
                                        text = streamData.description,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = Color(0xFFDDDDDD),
                                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isDescriptionExpanded) "Sembunyikan" else "...selengkapnya",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    val relatedVideos = streamData.relatedVideos
                    if (relatedVideos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Video Terkait",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

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

                    Spacer(modifier = Modifier.height(24.dp))
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
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    Row(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(if (isActive) Color.White else Color(0xFF272727))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Color.Black else Color.White,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isActive) Color.Black else Color.White
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
