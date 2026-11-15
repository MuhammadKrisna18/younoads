package com.example.yoouutt.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.yoouutt.data.model.PlaylistItem
import com.example.yoouutt.data.model.WatchHistoryItem
import com.example.yoouutt.data.repository.WatchHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SqliteWatchHistoryRepository(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dbHelper: WatchHistoryDbHelper = WatchHistoryDbHelper(context)
) : WatchHistoryRepository {

    private val historyFlow = MutableStateFlow<List<WatchHistoryItem>>(emptyList())
    private val playlistFlow = MutableStateFlow<List<PlaylistItem>>(emptyList())

    init {
        loadHistoryIntoFlow()
        loadPlaylistsIntoFlow()
    }

    override fun getHistory(): Flow<List<WatchHistoryItem>> = historyFlow.asStateFlow()

    override fun getWatchedPlaylists(): Flow<List<PlaylistItem>> = playlistFlow.asStateFlow()

    override suspend fun getWatchHistory(videoId: String): WatchHistoryItem? = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WatchHistoryDbHelper.TABLE_NAME,
            null,
            "${WatchHistoryDbHelper.COL_VIDEO_ID} = ?",
            arrayOf(videoId),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                val url = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_VIDEO_URL))
                val title = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_TITLE))
                val uploader = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_UPLOADER))
                val thumbnail = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_THUMBNAIL))
                val lastPos = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_LAST_POSITION))
                val totalDuration = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_TOTAL_DURATION))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_LAST_WATCHED))
                WatchHistoryItem(
                    videoId = videoId,
                    videoUrl = url,
                    title = title,
                    uploaderName = uploader,
                    thumbnailUrl = thumbnail,
                    lastPlaybackPositionMs = lastPos,
                    totalDurationMs = totalDuration,
                    lastWatchedTimestamp = timestamp
                )
            } else {
                null
            }
        }
    }

    override suspend fun saveWatchHistory(item: WatchHistoryItem): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_VIDEO_ID, item.videoId)
            put(WatchHistoryDbHelper.COL_VIDEO_URL, item.videoUrl)
            put(WatchHistoryDbHelper.COL_TITLE, item.title)
            put(WatchHistoryDbHelper.COL_UPLOADER, item.uploaderName)
            put(WatchHistoryDbHelper.COL_THUMBNAIL, item.thumbnailUrl)
            put(WatchHistoryDbHelper.COL_LAST_POSITION, item.lastPlaybackPositionMs)
            put(WatchHistoryDbHelper.COL_TOTAL_DURATION, item.totalDurationMs)
            put(WatchHistoryDbHelper.COL_LAST_WATCHED, item.lastWatchedTimestamp)
        }
        db.insertWithOnConflict(WatchHistoryDbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        loadHistoryIntoFlow()
    }

    override suspend fun saveWatchedPlaylist(playlist: PlaylistItem): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_PL_ID, playlist.id)
            put(WatchHistoryDbHelper.COL_PL_URL, playlist.url)
            put(WatchHistoryDbHelper.COL_PL_TITLE, playlist.title)
            put(WatchHistoryDbHelper.COL_PL_UPLOADER, playlist.uploaderName)
            put(WatchHistoryDbHelper.COL_PL_THUMBNAIL, playlist.thumbnailUrl)
            put(WatchHistoryDbHelper.COL_PL_STREAM_COUNT, playlist.streamCount)
            put(WatchHistoryDbHelper.COL_PL_LAST_WATCHED, System.currentTimeMillis())
        }
        db.insertWithOnConflict(WatchHistoryDbHelper.TABLE_PLAYLISTS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        loadPlaylistsIntoFlow()
    }

    override suspend fun deleteHistory(videoId: String): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(WatchHistoryDbHelper.TABLE_NAME, "${WatchHistoryDbHelper.COL_VIDEO_ID} = ?", arrayOf(videoId))
        loadHistoryIntoFlow()
    }

    override suspend fun deleteWatchedPlaylist(playlistId: String): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(WatchHistoryDbHelper.TABLE_PLAYLISTS, "${WatchHistoryDbHelper.COL_PL_ID} = ?", arrayOf(playlistId))
        loadPlaylistsIntoFlow()
    }

    override suspend fun clearAllHistory(): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(WatchHistoryDbHelper.TABLE_NAME, null, null)
        db.delete(WatchHistoryDbHelper.TABLE_PLAYLISTS, null, null)
        loadHistoryIntoFlow()
        loadPlaylistsIntoFlow()
    }

    private fun loadHistoryIntoFlow() {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<WatchHistoryItem>()
        val cursor = db.query(
            WatchHistoryDbHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${WatchHistoryDbHelper.COL_LAST_WATCHED} DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_VIDEO_ID))
                val url = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_VIDEO_URL))
                val title = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_TITLE))
                val uploader = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_UPLOADER))
                val thumbnail = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_THUMBNAIL))
                val lastPos = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_LAST_POSITION))
                val totalDuration = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_TOTAL_DURATION))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_LAST_WATCHED))
                list.add(
                    WatchHistoryItem(
                        videoId = id,
                        videoUrl = url,
                        title = title,
                        uploaderName = uploader,
                        thumbnailUrl = thumbnail,
                        lastPlaybackPositionMs = lastPos,
                        totalDurationMs = totalDuration,
                        lastWatchedTimestamp = timestamp
                    )
                )
            }
        }
        historyFlow.value = list
    }

    private fun loadPlaylistsIntoFlow() {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<PlaylistItem>()
        try {
            val cursor = db.query(
                WatchHistoryDbHelper.TABLE_PLAYLISTS,
                null,
                null,
                null,
                null,
                null,
                "${WatchHistoryDbHelper.COL_PL_LAST_WATCHED} DESC"
            )
            cursor.use {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_PL_ID))
                    val url = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_PL_URL))
                    val title = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_PL_TITLE))
                    val uploader = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_PL_UPLOADER))
                    val thumbnail = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_PL_THUMBNAIL))
                    val streamCount = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_PL_STREAM_COUNT))
                    list.add(
                        PlaylistItem(
                            id = id,
                            url = url,
                            title = title,
                            uploaderName = uploader,
                            thumbnailUrl = thumbnail,
                            streamCount = streamCount
                        )
                    )
                }
            }
        } catch (_: Exception) {
        }
        playlistFlow.value = list
    }
}
