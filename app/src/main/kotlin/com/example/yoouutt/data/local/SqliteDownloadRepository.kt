package com.example.yoouutt.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.yoouutt.data.model.DownloadStatus
import com.example.yoouutt.data.model.DownloadedVideo
import com.example.yoouutt.data.repository.DownloadRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SqliteDownloadRepository(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dbHelper: WatchHistoryDbHelper = WatchHistoryDbHelper(context)
) : DownloadRepository {

    private val downloadsFlow = MutableStateFlow<List<DownloadedVideo>>(emptyList())

    init {
        loadDownloadsIntoFlow()
    }

    override fun getDownloads(): Flow<List<DownloadedVideo>> = downloadsFlow.asStateFlow()

    override suspend fun getDownload(videoId: String): DownloadedVideo? = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WatchHistoryDbHelper.TABLE_DOWNLOADS,
            null,
            "${WatchHistoryDbHelper.COL_DL_VIDEO_ID} = ?",
            arrayOf(videoId),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) parseDownloadedVideo(it) else null
        }
    }

    override suspend fun isDownloaded(videoId: String): Boolean = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WatchHistoryDbHelper.TABLE_DOWNLOADS,
            arrayOf(WatchHistoryDbHelper.COL_DL_VIDEO_ID),
            "${WatchHistoryDbHelper.COL_DL_VIDEO_ID} = ? AND ${WatchHistoryDbHelper.COL_DL_STATUS} = ?",
            arrayOf(videoId, DownloadStatus.COMPLETED.code.toString()),
            null,
            null,
            null
        )
        cursor.use { it.moveToFirst() }
    }

    override suspend fun saveDownload(download: DownloadedVideo): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_DL_VIDEO_ID, download.videoId)
            put(WatchHistoryDbHelper.COL_DL_URL, download.videoUrl)
            put(WatchHistoryDbHelper.COL_DL_TITLE, download.title)
            put(WatchHistoryDbHelper.COL_DL_UPLOADER, download.uploader)
            put(WatchHistoryDbHelper.COL_DL_THUMBNAIL, download.thumbnailUrl)
            put(WatchHistoryDbHelper.COL_DL_FILE_PATH, download.filePath)
            put(WatchHistoryDbHelper.COL_DL_FILE_SIZE, download.fileSizeBytes)
            put(WatchHistoryDbHelper.COL_DL_QUALITY, download.qualityLabel)
            put(WatchHistoryDbHelper.COL_DL_IS_AUDIO, if (download.isAudioOnly) 1 else 0)
            put(WatchHistoryDbHelper.COL_DL_DURATION, download.durationMs)
            put(WatchHistoryDbHelper.COL_DL_STATUS, download.status.code)
            put(WatchHistoryDbHelper.COL_DL_PROGRESS, download.progress)
            put(WatchHistoryDbHelper.COL_DL_DOWNLOADED_AT, download.downloadedAt)
        }
        db.insertWithOnConflict(
            WatchHistoryDbHelper.TABLE_DOWNLOADS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        loadDownloadsIntoFlow()
    }

    override suspend fun updateProgress(videoId: String, progress: Int, status: DownloadStatus): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_DL_PROGRESS, progress)
            put(WatchHistoryDbHelper.COL_DL_STATUS, status.code)
        }
        db.update(
            WatchHistoryDbHelper.TABLE_DOWNLOADS,
            values,
            "${WatchHistoryDbHelper.COL_DL_VIDEO_ID} = ?",
            arrayOf(videoId)
        )
        loadDownloadsIntoFlow()
    }

    override suspend fun updateCompleted(videoId: String, filePath: String, fileSizeBytes: Long): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_DL_FILE_PATH, filePath)
            put(WatchHistoryDbHelper.COL_DL_FILE_SIZE, fileSizeBytes)
            put(WatchHistoryDbHelper.COL_DL_PROGRESS, 100)
            put(WatchHistoryDbHelper.COL_DL_STATUS, DownloadStatus.COMPLETED.code)
            put(WatchHistoryDbHelper.COL_DL_DOWNLOADED_AT, System.currentTimeMillis())
        }
        db.update(
            WatchHistoryDbHelper.TABLE_DOWNLOADS,
            values,
            "${WatchHistoryDbHelper.COL_DL_VIDEO_ID} = ?",
            arrayOf(videoId)
        )
        loadDownloadsIntoFlow()
    }

    override suspend fun updateFailed(videoId: String): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_DL_STATUS, DownloadStatus.FAILED.code)
        }
        db.update(
            WatchHistoryDbHelper.TABLE_DOWNLOADS,
            values,
            "${WatchHistoryDbHelper.COL_DL_VIDEO_ID} = ?",
            arrayOf(videoId)
        )
        loadDownloadsIntoFlow()
    }

    override suspend fun deleteDownload(videoId: String): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(
            WatchHistoryDbHelper.TABLE_DOWNLOADS,
            "${WatchHistoryDbHelper.COL_DL_VIDEO_ID} = ?",
            arrayOf(videoId)
        )
        loadDownloadsIntoFlow()
    }

    override suspend fun clearAllDownloads(): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(WatchHistoryDbHelper.TABLE_DOWNLOADS, null, null)
        loadDownloadsIntoFlow()
    }

    private fun loadDownloadsIntoFlow() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WatchHistoryDbHelper.TABLE_DOWNLOADS,
            null,
            null,
            null,
            null,
            null,
            "${WatchHistoryDbHelper.COL_DL_DOWNLOADED_AT} DESC"
        )
        val items = mutableListOf<DownloadedVideo>()
        cursor.use {
            while (it.moveToNext()) {
                items.add(parseDownloadedVideo(it))
            }
        }
        downloadsFlow.value = items
    }

    private fun parseDownloadedVideo(cursor: Cursor): DownloadedVideo {
        return DownloadedVideo(
            videoId = cursor.getString(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_VIDEO_ID)),
            videoUrl = cursor.getString(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_URL)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_TITLE)),
            uploader = cursor.getString(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_UPLOADER)),
            thumbnailUrl = cursor.getString(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_THUMBNAIL)),
            filePath = cursor.getString(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_FILE_PATH)),
            fileSizeBytes = cursor.getLong(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_FILE_SIZE)),
            qualityLabel = cursor.getString(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_QUALITY)),
            isAudioOnly = cursor.getInt(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_IS_AUDIO)) == 1,
            durationMs = cursor.getLong(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_DURATION)),
            status = DownloadStatus.fromCode(cursor.getInt(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_STATUS))),
            progress = cursor.getInt(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_PROGRESS)),
            downloadedAt = cursor.getLong(cursor.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_DL_DOWNLOADED_AT))
        )
    }
}
