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

    companion object {
        const val DATABASE_NAME = "younoads_history.db"
        const val DATABASE_VERSION = 2
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
    }
}
