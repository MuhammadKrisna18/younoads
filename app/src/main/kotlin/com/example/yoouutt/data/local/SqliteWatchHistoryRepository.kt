package com.example.yoouutt.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION), WatchHistoryRepository {

    private val historyFlow = MutableStateFlow<List<WatchHistoryItem>>(emptyList())
    private val playlistFlow = MutableStateFlow<List<PlaylistItem>>(emptyList())

    init {
        loadHistoryIntoFlow()
        loadPlaylistsIntoFlow()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COL_VIDEO_ID TEXT PRIMARY KEY,
                $COL_VIDEO_URL TEXT NOT NULL,
                $COL_TITLE TEXT NOT NULL,
                $COL_UPLOADER TEXT NOT NULL,
                $COL_THUMBNAIL TEXT NOT NULL,
                $COL_LAST_POSITION INTEGER NOT NULL,
                $COL_TOTAL_DURATION INTEGER NOT NULL,
                $COL_LAST_WATCHED INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_PLAYLISTS (
                $COL_PL_ID TEXT PRIMARY KEY,
                $COL_PL_URL TEXT NOT NULL,
                $COL_PL_TITLE TEXT NOT NULL,
                $COL_PL_UPLOADER TEXT NOT NULL,
                $COL_PL_THUMBNAIL TEXT NOT NULL,
                $COL_PL_STREAM_COUNT INTEGER NOT NULL,
                $COL_PL_LAST_WATCHED INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_PLAYLISTS (
                    $COL_PL_ID TEXT PRIMARY KEY,
                    $COL_PL_URL TEXT NOT NULL,
                    $COL_PL_TITLE TEXT NOT NULL,
                    $COL_PL_UPLOADER TEXT NOT NULL,
                    $COL_PL_THUMBNAIL TEXT NOT NULL,
                    $COL_PL_STREAM_COUNT INTEGER NOT NULL,
                    $COL_PL_LAST_WATCHED INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    override fun getHistory(): Flow<List<WatchHistoryItem>> = historyFlow.asStateFlow()

    override fun getWatchedPlaylists(): Flow<List<PlaylistItem>> = playlistFlow.asStateFlow()

    override suspend fun getWatchHistory(videoId: String): WatchHistoryItem? = withContext(ioDispatcher) {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_VIDEO_ID = ?",
            arrayOf(videoId),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                val url = it.getString(it.getColumnIndexOrThrow(COL_VIDEO_URL))
                val title = it.getString(it.getColumnIndexOrThrow(COL_TITLE))
                val uploader = it.getString(it.getColumnIndexOrThrow(COL_UPLOADER))
                val thumbnail = it.getString(it.getColumnIndexOrThrow(COL_THUMBNAIL))
                val lastPos = it.getLong(it.getColumnIndexOrThrow(COL_LAST_POSITION))
                val totalDuration = it.getLong(it.getColumnIndexOrThrow(COL_TOTAL_DURATION))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(COL_LAST_WATCHED))
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
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_VIDEO_ID, item.videoId)
            put(COL_VIDEO_URL, item.videoUrl)
            put(COL_TITLE, item.title)
            put(COL_UPLOADER, item.uploaderName)
            put(COL_THUMBNAIL, item.thumbnailUrl)
            put(COL_LAST_POSITION, item.lastPlaybackPositionMs)
            put(COL_TOTAL_DURATION, item.totalDurationMs)
            put(COL_LAST_WATCHED, item.lastWatchedTimestamp)
        }
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        loadHistoryIntoFlow()
    }

    override suspend fun saveWatchedPlaylist(playlist: PlaylistItem): Unit = withContext(ioDispatcher) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PL_ID, playlist.id)
            put(COL_PL_URL, playlist.url)
            put(COL_PL_TITLE, playlist.title)
            put(COL_PL_UPLOADER, playlist.uploaderName)
            put(COL_PL_THUMBNAIL, playlist.thumbnailUrl)
            put(COL_PL_STREAM_COUNT, playlist.streamCount)
            put(COL_PL_LAST_WATCHED, System.currentTimeMillis())
        }
        db.insertWithOnConflict(TABLE_PLAYLISTS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        loadPlaylistsIntoFlow()
    }

    override suspend fun deleteHistory(videoId: String): Unit = withContext(ioDispatcher) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COL_VIDEO_ID = ?", arrayOf(videoId))
        loadHistoryIntoFlow()
    }

    override suspend fun deleteWatchedPlaylist(playlistId: String): Unit = withContext(ioDispatcher) {
        val db = writableDatabase
        db.delete(TABLE_PLAYLISTS, "$COL_PL_ID = ?", arrayOf(playlistId))
        loadPlaylistsIntoFlow()
    }

    override suspend fun clearAllHistory(): Unit = withContext(ioDispatcher) {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.delete(TABLE_PLAYLISTS, null, null)
        loadHistoryIntoFlow()
        loadPlaylistsIntoFlow()
    }

    private fun loadHistoryIntoFlow() {
        val db = readableDatabase
        val list = mutableListOf<WatchHistoryItem>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COL_LAST_WATCHED DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(COL_VIDEO_ID))
                val url = it.getString(it.getColumnIndexOrThrow(COL_VIDEO_URL))
                val title = it.getString(it.getColumnIndexOrThrow(COL_TITLE))
                val uploader = it.getString(it.getColumnIndexOrThrow(COL_UPLOADER))
                val thumbnail = it.getString(it.getColumnIndexOrThrow(COL_THUMBNAIL))
                val lastPos = it.getLong(it.getColumnIndexOrThrow(COL_LAST_POSITION))
                val totalDuration = it.getLong(it.getColumnIndexOrThrow(COL_TOTAL_DURATION))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(COL_LAST_WATCHED))
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
        val db = readableDatabase
        val list = mutableListOf<PlaylistItem>()
        try {
            val cursor = db.query(
                TABLE_PLAYLISTS,
                null,
                null,
                null,
                null,
                null,
                "$COL_PL_LAST_WATCHED DESC"
            )
            cursor.use {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndexOrThrow(COL_PL_ID))
                    val url = it.getString(it.getColumnIndexOrThrow(COL_PL_URL))
                    val title = it.getString(it.getColumnIndexOrThrow(COL_PL_TITLE))
                    val uploader = it.getString(it.getColumnIndexOrThrow(COL_PL_UPLOADER))
                    val thumbnail = it.getString(it.getColumnIndexOrThrow(COL_PL_THUMBNAIL))
                    val streamCount = it.getLong(it.getColumnIndexOrThrow(COL_PL_STREAM_COUNT))
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

    companion object {
        private const val DATABASE_NAME = "younoads_history.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "watch_history"
        private const val COL_VIDEO_ID = "video_id"
        private const val COL_VIDEO_URL = "video_url"
        private const val COL_TITLE = "title"
        private const val COL_UPLOADER = "uploader_name"
        private const val COL_THUMBNAIL = "thumbnail_url"
        private const val COL_LAST_POSITION = "last_position"
        private const val COL_TOTAL_DURATION = "total_duration"
        private const val COL_LAST_WATCHED = "last_watched"

        private const val TABLE_PLAYLISTS = "watched_playlists"
        private const val COL_PL_ID = "playlist_id"
        private const val COL_PL_URL = "playlist_url"
        private const val COL_PL_TITLE = "playlist_title"
        private const val COL_PL_UPLOADER = "uploader_name"
        private const val COL_PL_THUMBNAIL = "thumbnail_url"
        private const val COL_PL_STREAM_COUNT = "stream_count"
        private const val COL_PL_LAST_WATCHED = "last_watched"
    }
}
