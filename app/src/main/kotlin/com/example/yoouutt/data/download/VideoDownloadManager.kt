package com.example.yoouutt.data.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import com.example.yoouutt.data.local.SqliteDownloadRepository
import com.example.yoouutt.data.model.DownloadRequest
import com.example.yoouutt.data.model.DownloadStatus
import com.example.yoouutt.data.model.DownloadedVideo
import com.example.yoouutt.data.repository.DownloadRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class VideoDownloadManager(
    private val context: Context,
    private val repository: DownloadRepository = SqliteDownloadRepository(context)
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val activeJobs = ConcurrentHashMap<String, Job>()

    private val _downloadProgressMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgressMap = _downloadProgressMap.asStateFlow()

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .build()

    init {
        createNotificationChannel()
    }

    fun getDownloads(): Flow<List<DownloadedVideo>> = repository.getDownloads()

    suspend fun isDownloaded(videoId: String): Boolean = repository.isDownloaded(videoId)

    suspend fun getDownload(videoId: String): DownloadedVideo? = repository.getDownload(videoId)

    fun startDownload(request: DownloadRequest) {
        if (activeJobs.containsKey(request.videoId)) return

        val initialEntry = DownloadedVideo(
            videoId = request.videoId,
            videoUrl = request.videoUrl,
            title = request.title,
            uploader = request.uploader,
            thumbnailUrl = request.thumbnailUrl,
            filePath = "",
            fileSizeBytes = 0L,
            qualityLabel = request.qualityLabel,
            isAudioOnly = request.isAudioOnly,
            durationMs = request.durationMs,
            status = DownloadStatus.DOWNLOADING,
            progress = 0,
            downloadedAt = System.currentTimeMillis()
        )

        val job = scope.launch {
            repository.saveDownload(initialEntry)
            executeDownload(request)
        }

        activeJobs[request.videoId] = job
        job.invokeOnCompletion {
            activeJobs.remove(request.videoId)
            val updated = _downloadProgressMap.value.toMutableMap()
            updated.remove(request.videoId)
            _downloadProgressMap.value = updated
        }
    }

    fun cancelDownload(videoId: String) {
        val job = activeJobs.remove(videoId)
        job?.cancel()

        scope.launch {
            repository.updateFailed(videoId)
            cleanTempFile(videoId)
            cancelNotification(videoId)
        }
    }

    suspend fun deleteDownload(videoId: String) {
        cancelDownload(videoId)
        val download = repository.getDownload(videoId)
        if (download != null && download.filePath.isNotBlank()) {
            try {
                val file = File(download.filePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (_: Exception) {
            }
        }
        repository.deleteDownload(videoId)
    }

    private suspend fun executeDownload(request: DownloadRequest) {
        val targetDir = getTargetDirectory(request.isAudioOnly)
        if (targetDir == null) {
            repository.updateFailed(request.videoId)
            return
        }

        val extension = if (request.isAudioOnly) "m4a" else "mp4"
        val cleanTitle = sanitizeFilename(request.title)
        val targetFile = File(targetDir, "${request.videoId}_$cleanTitle.$extension")
        val tempFile = File(targetDir, "${request.videoId}.tmp")

        showProgressNotification(request.videoId, request.title, 0)

        try {
            val httpRequest = Request.Builder()
                .url(request.downloadUrl)
                .header("User-Agent", USER_AGENT)
                .build()

            httpClient.newCall(httpRequest).execute().use { response ->
                if (!response.isSuccessful) {
                    repository.updateFailed(request.videoId)
                    showFailedNotification(request.videoId, request.title)
                    return
                }

                val body = response.body ?: run {
                    repository.updateFailed(request.videoId)
                    showFailedNotification(request.videoId, request.title)
                    return
                }

                val contentLength = body.contentLength()
                var totalBytesRead = 0L
                var lastProgressPercent = 0
                var lastNotifyTime = 0L

                body.byteStream().use { input ->
                    FileOutputStream(tempFile).use { output ->
                        val buffer = ByteArray(64 * 1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead

                            if (contentLength > 0L) {
                                val progressPercent = ((totalBytesRead * 100L) / contentLength).toInt().coerceIn(0, 100)
                                val currentTime = System.currentTimeMillis()

                                if (progressPercent != lastProgressPercent && (currentTime - lastNotifyTime >= 500L || progressPercent == 100)) {
                                    lastProgressPercent = progressPercent
                                    lastNotifyTime = currentTime

                                    val progressUpdate = _downloadProgressMap.value.toMutableMap()
                                    progressUpdate[request.videoId] = progressPercent
                                    _downloadProgressMap.value = progressUpdate

                                    repository.updateProgress(request.videoId, progressPercent, DownloadStatus.DOWNLOADING)
                                    showProgressNotification(request.videoId, request.title, progressPercent)
                                }
                            }
                        }
                        output.flush()
                    }
                }

                if (targetFile.exists()) {
                    targetFile.delete()
                }

                val moveSuccess = tempFile.renameTo(targetFile) || run {
                    tempFile.copyTo(targetFile, overwrite = true)
                    tempFile.delete()
                    true
                }

                if (moveSuccess && targetFile.exists() && targetFile.length() > 0L) {
                    val finalSize = targetFile.length()
                    repository.updateCompleted(request.videoId, targetFile.absolutePath, finalSize)
                    showCompletedNotification(request.videoId, request.title)
                } else {
                    repository.updateFailed(request.videoId)
                    showFailedNotification(request.videoId, request.title)
                }
            }
        } catch (_: Exception) {
            if (tempFile.exists()) {
                tempFile.delete()
            }
            repository.updateFailed(request.videoId)
            showFailedNotification(request.videoId, request.title)
        }
    }

    private fun getTargetDirectory(isAudioOnly: Boolean): File? {
        val type = if (isAudioOnly) Environment.DIRECTORY_MUSIC else Environment.DIRECTORY_MOVIES
        val dir = context.getExternalFilesDir(type)
        if (dir != null && !dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun cleanTempFile(videoId: String) {
        val moviesDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        File(moviesDir, "$videoId.tmp").takeIf { it.exists() }?.delete()
        File(musicDir, "$videoId.tmp").takeIf { it.exists() }?.delete()
    }

    private fun sanitizeFilename(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .take(40)
            .trim()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Unduhan YounoAds",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Proses unduhan video dan audio lokal"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showProgressNotification(videoId: String, title: String, progress: Int) {
        val notificationId = videoId.hashCode()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText("Mengunduh... $progress%")
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
        }
    }

    private fun showCompletedNotification(videoId: String, title: String) {
        val notificationId = videoId.hashCode()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText("Unduhan selesai")
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
        }
    }

    private fun showFailedNotification(videoId: String, title: String) {
        val notificationId = videoId.hashCode()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText("Unduhan gagal")
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
        }
    }

    private fun cancelNotification(videoId: String) {
        val notificationId = videoId.hashCode()
        try {
            notificationManager.cancel(notificationId)
        } catch (_: Exception) {
        }
    }

    companion object {
        private const val CHANNEL_ID = "younoads_downloads_channel"
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
}
