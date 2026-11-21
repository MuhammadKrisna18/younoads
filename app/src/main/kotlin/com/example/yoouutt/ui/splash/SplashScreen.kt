package com.example.yoouutt.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yoouutt.BuildConfig
import com.example.yoouutt.R
import com.example.yoouutt.ui.common.AppThemeBackdrop
import com.example.yoouutt.ui.icon.AppIconManager
import com.example.yoouutt.ui.theme.LocalYoouuttTheme
import com.example.yoouutt.ui.theme.gradientColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val theme = LocalYoouuttTheme.current
    val currentIcon by AppIconManager.currentIcon.collectAsStateWithLifecycle()
    val gradientColors = remember(theme, context) { theme.gradientColors(context) }

    val scaleAnim = remember { Animatable(0.82f) }
    val alphaAnim = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            alphaAnim.animateTo(1f, tween(500, easing = LinearEasing))
        }
        launch {
            scaleAnim.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
        }
        launch {
            progressAnim.animateTo(1f, tween(2500, easing = LinearEasing))
        }
        delay(2500L)
        onSplashFinished()
    }

    AppThemeBackdrop(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.92f))
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(alphaAnim.value)
                    .scale(scaleAnim.value),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        gradientColors.first().copy(alpha = 0.45f),
                                        gradientColors.last().copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    Image(
                        painter = painterResource(id = currentIcon.previewResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(18.dp))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.splash_tagline),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressAnim.value)
                            .height(3.dp)
                            .background(
                                Brush.horizontalGradient(gradientColors)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
