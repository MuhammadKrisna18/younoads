package com.example.yoouutt.playback

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.yoouutt.YoouuttApp

class PlaybackService : MediaSessionService() {

    override fun onCreate() {
        super.onCreate()
        val session = YoouuttApp.instance.playbackManager.mediaSession
        addSession(session)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return YoouuttApp.instance.playbackManager.mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = YoouuttApp.instance.playbackManager.exoPlayer
        if (!player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        val session = YoouuttApp.instance.playbackManager.mediaSession
        removeSession(session)
        super.onDestroy()
    }
}

