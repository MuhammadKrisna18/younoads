package com.example.yoouutt.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.yoouutt.data.model.SavedVideo
import com.example.yoouutt.data.repository.SavedVideoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SqliteSavedVideoRepository(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dbHelper: WatchHistoryDbHelper = WatchHistoryDbHelper(context)
) : SavedVideoRepository {

    private val savedFlow = MutableStateFlow<List<SavedVideo>>(emptyList())

    init {
        loadSavedVideosIntoFlow()
    }

    override fun getSavedVideos(): Flow<List<SavedVideo>> = savedFlow.asStateFlow()

    override suspend fun isSaved(videoId: String): Boolean = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WatchHistoryDbHelper.TABLE_SAVED_VIDEOS,
            arrayOf(WatchHistoryDbHelper.COL_SAVED_VIDEO_ID),
            "${WatchHistoryDbHelper.COL_SAVED_VIDEO_ID} = ?",
            arrayOf(videoId),
            null,
            null,
            null
        )
        cursor.use { it.moveToFirst() }
    }

    override suspend fun saveVideo(video: SavedVideo): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_SAVED_VIDEO_ID, video.videoId)
            put(WatchHistoryDbHelper.COL_SAVED_URL, video.url)
            put(WatchHistoryDbHelper.COL_SAVED_TITLE, video.title)
            put(WatchHistoryDbHelper.COL_SAVED_UPLOADER, video.uploader)
            put(WatchHistoryDbHelper.COL_SAVED_THUMBNAIL, video.thumbnailUrl)
            put(WatchHistoryDbHelper.COL_SAVED_AT, video.savedAt)
        }
        db.insertWithOnConflict(
            WatchHistoryDbHelper.TABLE_SAVED_VIDEOS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        loadSavedVideosIntoFlow()
    }

    override suspend fun removeSavedVideo(videoId: String): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(
            WatchHistoryDbHelper.TABLE_SAVED_VIDEOS,
            "${WatchHistoryDbHelper.COL_SAVED_VIDEO_ID} = ?",
            arrayOf(videoId)
        )
        loadSavedVideosIntoFlow()
    }

    override suspend fun clearAllSavedVideos(): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(WatchHistoryDbHelper.TABLE_SAVED_VIDEOS, null, null)
        loadSavedVideosIntoFlow()
    }

    private fun loadSavedVideosIntoFlow() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WatchHistoryDbHelper.TABLE_SAVED_VIDEOS,
            null,
            null,
            null,
            null,
            null,
            "${WatchHistoryDbHelper.COL_SAVED_AT} DESC"
        )
        val list = mutableListOf<SavedVideo>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_SAVED_VIDEO_ID))
                val url = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_SAVED_URL))
                val title = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_SAVED_TITLE))
                val uploader = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_SAVED_UPLOADER))
                val thumbnail = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_SAVED_THUMBNAIL))
                val savedAt = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_SAVED_AT))
                list.add(
                    SavedVideo(
                        videoId = id,
                        url = url,
                        title = title,
                        uploader = uploader,
                        thumbnailUrl = thumbnail,
                        savedAt = savedAt
                    )
                )
            }
        }
        savedFlow.value = list
    }
}
