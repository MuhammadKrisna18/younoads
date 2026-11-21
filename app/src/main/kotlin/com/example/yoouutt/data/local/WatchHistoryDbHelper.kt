package com.example.yoouutt.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WatchHistoryDbHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
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
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_SAVED_VIDEOS (
                $COL_SAVED_VIDEO_ID TEXT PRIMARY KEY,
                $COL_SAVED_URL TEXT NOT NULL,
                $COL_SAVED_TITLE TEXT NOT NULL,
                $COL_SAVED_UPLOADER TEXT NOT NULL,
                $COL_SAVED_THUMBNAIL TEXT NOT NULL,
                $COL_SAVED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_CUSTOM_PLAYLISTS (
                $COL_CP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CP_TITLE TEXT NOT NULL,
                $COL_CP_DESCRIPTION TEXT NOT NULL DEFAULT '',
                $COL_CP_CREATED_AT INTEGER NOT NULL,
                $COL_CP_UPDATED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_CUSTOM_PLAYLIST_ITEMS (
                $COL_CPI_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CPI_PLAYLIST_ID INTEGER NOT NULL,
                $COL_CPI_VIDEO_ID TEXT NOT NULL,
                $COL_CPI_URL TEXT NOT NULL,
                $COL_CPI_TITLE TEXT NOT NULL,
                $COL_CPI_UPLOADER TEXT NOT NULL,
                $COL_CPI_THUMBNAIL TEXT NOT NULL,
                $COL_CPI_DURATION TEXT NOT NULL DEFAULT '',
                $COL_CPI_ADDED_AT INTEGER NOT NULL,
                $COL_CPI_SORT_ORDER INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY($COL_CPI_PLAYLIST_ID) REFERENCES $TABLE_CUSTOM_PLAYLISTS($COL_CP_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_DOWNLOADS (
                $COL_DL_VIDEO_ID TEXT PRIMARY KEY,
                $COL_DL_URL TEXT NOT NULL,
                $COL_DL_TITLE TEXT NOT NULL,
                $COL_DL_UPLOADER TEXT NOT NULL,
                $COL_DL_THUMBNAIL TEXT NOT NULL,
                $COL_DL_FILE_PATH TEXT NOT NULL,
                $COL_DL_FILE_SIZE INTEGER NOT NULL DEFAULT 0,
                $COL_DL_QUALITY TEXT NOT NULL,
                $COL_DL_IS_AUDIO INTEGER NOT NULL DEFAULT 0,
                $COL_DL_DURATION INTEGER NOT NULL DEFAULT 0,
                $COL_DL_STATUS INTEGER NOT NULL DEFAULT 0,
                $COL_DL_PROGRESS INTEGER NOT NULL DEFAULT 0,
                $COL_DL_DOWNLOADED_AT INTEGER NOT NULL
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
        if (oldVersion < 3) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_SAVED_VIDEOS (
                    $COL_SAVED_VIDEO_ID TEXT PRIMARY KEY,
                    $COL_SAVED_URL TEXT NOT NULL,
                    $COL_SAVED_TITLE TEXT NOT NULL,
                    $COL_SAVED_UPLOADER TEXT NOT NULL,
                    $COL_SAVED_THUMBNAIL TEXT NOT NULL,
                    $COL_SAVED_AT INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_CUSTOM_PLAYLISTS (
                    $COL_CP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_CP_TITLE TEXT NOT NULL,
                    $COL_CP_DESCRIPTION TEXT NOT NULL DEFAULT '',
                    $COL_CP_CREATED_AT INTEGER NOT NULL,
                    $COL_CP_UPDATED_AT INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_CUSTOM_PLAYLIST_ITEMS (
                    $COL_CPI_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_CPI_PLAYLIST_ID INTEGER NOT NULL,
                    $COL_CPI_VIDEO_ID TEXT NOT NULL,
                    $COL_CPI_URL TEXT NOT NULL,
                    $COL_CPI_TITLE TEXT NOT NULL,
                    $COL_CPI_UPLOADER TEXT NOT NULL,
                    $COL_CPI_THUMBNAIL TEXT NOT NULL,
                    $COL_CPI_DURATION TEXT NOT NULL DEFAULT '',
                    $COL_CPI_ADDED_AT INTEGER NOT NULL,
                    $COL_CPI_SORT_ORDER INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY($COL_CPI_PLAYLIST_ID) REFERENCES $TABLE_CUSTOM_PLAYLISTS($COL_CP_ID) ON DELETE CASCADE
                )
                """.trimIndent()
            )
        }
        if (oldVersion < 4) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_DOWNLOADS (
                    $COL_DL_VIDEO_ID TEXT PRIMARY KEY,
                    $COL_DL_URL TEXT NOT NULL,
                    $COL_DL_TITLE TEXT NOT NULL,
                    $COL_DL_UPLOADER TEXT NOT NULL,
                    $COL_DL_THUMBNAIL TEXT NOT NULL,
                    $COL_DL_FILE_PATH TEXT NOT NULL,
                    $COL_DL_FILE_SIZE INTEGER NOT NULL DEFAULT 0,
                    $COL_DL_QUALITY TEXT NOT NULL,
                    $COL_DL_IS_AUDIO INTEGER NOT NULL DEFAULT 0,
                    $COL_DL_DURATION INTEGER NOT NULL DEFAULT 0,
                    $COL_DL_STATUS INTEGER NOT NULL DEFAULT 0,
                    $COL_DL_PROGRESS INTEGER NOT NULL DEFAULT 0,
                    $COL_DL_DOWNLOADED_AT INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    companion object {
        const val DATABASE_NAME = "younoads_history.db"
        const val DATABASE_VERSION = 4
        const val TABLE_NAME = "watch_history"
        const val COL_VIDEO_ID = "video_id"
        const val COL_VIDEO_URL = "video_url"
        const val COL_TITLE = "title"
        const val COL_UPLOADER = "uploader_name"
        const val COL_THUMBNAIL = "thumbnail_url"
        const val COL_LAST_POSITION = "last_position"
        const val COL_TOTAL_DURATION = "total_duration"
        const val COL_LAST_WATCHED = "last_watched"

        const val TABLE_PLAYLISTS = "watched_playlists"
        const val COL_PL_ID = "playlist_id"
        const val COL_PL_URL = "playlist_url"
        const val COL_PL_TITLE = "playlist_title"
        const val COL_PL_UPLOADER = "uploader_name"
        const val COL_PL_THUMBNAIL = "thumbnail_url"
        const val COL_PL_STREAM_COUNT = "stream_count"
        const val COL_PL_LAST_WATCHED = "last_watched"

        const val TABLE_SAVED_VIDEOS = "saved_videos"
        const val COL_SAVED_VIDEO_ID = "video_id"
        const val COL_SAVED_URL = "video_url"
        const val COL_SAVED_TITLE = "title"
        const val COL_SAVED_UPLOADER = "uploader_name"
        const val COL_SAVED_THUMBNAIL = "thumbnail_url"
        const val COL_SAVED_AT = "saved_at"

        const val TABLE_CUSTOM_PLAYLISTS = "custom_playlists"
        const val COL_CP_ID = "playlist_id"
        const val COL_CP_TITLE = "title"
        const val COL_CP_DESCRIPTION = "description"
        const val COL_CP_CREATED_AT = "created_at"
        const val COL_CP_UPDATED_AT = "updated_at"

        const val TABLE_CUSTOM_PLAYLIST_ITEMS = "custom_playlist_items"
        const val COL_CPI_ID = "item_id"
        const val COL_CPI_PLAYLIST_ID = "playlist_id"
        const val COL_CPI_VIDEO_ID = "video_id"
        const val COL_CPI_URL = "video_url"
        const val COL_CPI_TITLE = "title"
        const val COL_CPI_UPLOADER = "uploader_name"
        const val COL_CPI_THUMBNAIL = "thumbnail_url"
        const val COL_CPI_DURATION = "duration"
        const val COL_CPI_ADDED_AT = "added_at"
        const val COL_CPI_SORT_ORDER = "sort_order"

        const val TABLE_DOWNLOADS = "downloaded_videos"
        const val COL_DL_VIDEO_ID = "video_id"
        const val COL_DL_URL = "video_url"
        const val COL_DL_TITLE = "title"
        const val COL_DL_UPLOADER = "uploader_name"
        const val COL_DL_THUMBNAIL = "thumbnail_url"
        const val COL_DL_FILE_PATH = "file_path"
        const val COL_DL_FILE_SIZE = "file_size_bytes"
        const val COL_DL_QUALITY = "quality_label"
        const val COL_DL_IS_AUDIO = "is_audio_only"
        const val COL_DL_DURATION = "duration_ms"
        const val COL_DL_STATUS = "download_status"
        const val COL_DL_PROGRESS = "download_progress"
        const val COL_DL_DOWNLOADED_AT = "downloaded_at"
    }
}
