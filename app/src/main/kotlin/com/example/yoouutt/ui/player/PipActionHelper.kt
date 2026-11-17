package com.example.yoouutt.ui.player

import android.app.Activity
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Rational
import com.example.yoouutt.R

object PipActionHelper {
    const val ACTION_PIP_CONTROL = "com.example.yoouutt.PIP_CONTROL"
    const val EXTRA_CONTROL_TYPE = "control_type"
    const val CONTROL_TYPE_PLAY_PAUSE = 1
    const val CONTROL_TYPE_REPLAY = 2
    const val CONTROL_TYPE_HEADSET = 3

    fun buildPipActions(context: Context, isPlaying: Boolean): List<RemoteAction> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return emptyList()

        val actions = ArrayList<RemoteAction>(3)

        val replayIntent = Intent(ACTION_PIP_CONTROL).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE_REPLAY)
        }
        val replayPendingIntent = PendingIntent.getBroadcast(
            context,
            CONTROL_TYPE_REPLAY,
            replayIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        actions.add(
            RemoteAction(
                Icon.createWithResource(context, R.drawable.ic_pip_replay),
                "Mundur",
                "Mundur 10 detik",
                replayPendingIntent
            )
        )

        val playPauseIntent = Intent(ACTION_PIP_CONTROL).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE_PLAY_PAUSE)
        }
        val playPausePendingIntent = PendingIntent.getBroadcast(
            context,
            CONTROL_TYPE_PLAY_PAUSE,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val iconRes = if (isPlaying) R.drawable.ic_pip_pause else R.drawable.ic_pip_play
        val title = if (isPlaying) "Jeda" else "Putar"
        actions.add(
            RemoteAction(
                Icon.createWithResource(context, iconRes),
                title,
                title,
                playPausePendingIntent
            )
        )

        val headsetIntent = Intent(ACTION_PIP_CONTROL).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE_HEADSET)
        }
        val headsetPendingIntent = PendingIntent.getBroadcast(
            context,
            CONTROL_TYPE_HEADSET,
            headsetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        actions.add(
            RemoteAction(
                Icon.createWithResource(context, R.drawable.ic_pip_headset),
                "Latar Belakang",
                "Putar di latar belakang",
                headsetPendingIntent
            )
        )

        return actions
    }

    fun buildPipParams(
        context: Context,
        isPlaying: Boolean,
        sourceRectHint: Rect? = null,
        autoEnterEnabled: Boolean = false
    ): PictureInPictureParams? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null

        val builder = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
            .setActions(buildPipActions(context, isPlaying))

        if (sourceRectHint != null && !sourceRectHint.isEmpty) {
            builder.setSourceRectHint(sourceRectHint)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setAutoEnterEnabled(autoEnterEnabled)
        }

        return builder.build()
    }
}

class PipBroadcastReceiver(
    private val onPlayPause: () -> Unit,
    private val onReplay: () -> Unit,
    private val onHeadset: () -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != PipActionHelper.ACTION_PIP_CONTROL) return

        when (intent.getIntExtra(PipActionHelper.EXTRA_CONTROL_TYPE, 0)) {
            PipActionHelper.CONTROL_TYPE_PLAY_PAUSE -> onPlayPause()
            PipActionHelper.CONTROL_TYPE_REPLAY -> onReplay()
            PipActionHelper.CONTROL_TYPE_HEADSET -> onHeadset()
        }
    }
}

fun Activity.updatePipParams(
    isPlaying: Boolean,
    sourceRectHint: Rect? = null,
    autoEnterEnabled: Boolean = false
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val params = PipActionHelper.buildPipParams(
            context = this,
            isPlaying = isPlaying,
            sourceRectHint = sourceRectHint,
            autoEnterEnabled = autoEnterEnabled
        ) ?: return
        setPictureInPictureParams(params)
    }
}
