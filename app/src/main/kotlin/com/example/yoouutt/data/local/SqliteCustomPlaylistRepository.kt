package com.example.yoouutt.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.yoouutt.data.model.CustomPlaylist
import com.example.yoouutt.data.model.CustomPlaylistItem
import com.example.yoouutt.data.repository.CustomPlaylistRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class SqliteCustomPlaylistRepository(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dbHelper: WatchHistoryDbHelper = WatchHistoryDbHelper(context)
) : CustomPlaylistRepository {

    private val playlistsFlow = MutableStateFlow<List<CustomPlaylist>>(emptyList())
    private val itemsFlows = ConcurrentHashMap<Long, MutableStateFlow<List<CustomPlaylistItem>>>()

    init {
        loadPlaylistsIntoFlow()
    }

    override fun getCustomPlaylists(): Flow<List<CustomPlaylist>> = playlistsFlow.asStateFlow()

    override fun getPlaylistItems(playlistId: Long): Flow<List<CustomPlaylistItem>> {
        val flow = itemsFlows.getOrPut(playlistId) {
            MutableStateFlow(emptyList())
        }
        loadPlaylistItemsIntoFlow(playlistId)
        return flow.asStateFlow()
    }

    override suspend fun createPlaylist(title: String, description: String): Long = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_CP_TITLE, title.trim())
            put(WatchHistoryDbHelper.COL_CP_DESCRIPTION, description.trim())
            put(WatchHistoryDbHelper.COL_CP_CREATED_AT, now)
            put(WatchHistoryDbHelper.COL_CP_UPDATED_AT, now)
        }
        val id = db.insert(WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLISTS, null, values)
        loadPlaylistsIntoFlow()
        id
    }

    override suspend fun updatePlaylist(playlistId: Long, title: String, description: String): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_CP_TITLE, title.trim())
            put(WatchHistoryDbHelper.COL_CP_DESCRIPTION, description.trim())
            put(WatchHistoryDbHelper.COL_CP_UPDATED_AT, System.currentTimeMillis())
        }
        db.update(
            WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLISTS,
            values,
            "${WatchHistoryDbHelper.COL_CP_ID} = ?",
            arrayOf(playlistId.toString())
        )
        loadPlaylistsIntoFlow()
    }

    override suspend fun deletePlaylist(playlistId: Long): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(
            WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLIST_ITEMS,
            "${WatchHistoryDbHelper.COL_CPI_PLAYLIST_ID} = ?",
            arrayOf(playlistId.toString())
        )
        db.delete(
            WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLISTS,
            "${WatchHistoryDbHelper.COL_CP_ID} = ?",
            arrayOf(playlistId.toString())
        )
        itemsFlows.remove(playlistId)
        loadPlaylistsIntoFlow()
    }

    override suspend fun addVideoToPlaylist(playlistId: Long, item: CustomPlaylistItem): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_CPI_PLAYLIST_ID, playlistId)
            put(WatchHistoryDbHelper.COL_CPI_VIDEO_ID, item.videoId)
            put(WatchHistoryDbHelper.COL_CPI_URL, item.url)
            put(WatchHistoryDbHelper.COL_CPI_TITLE, item.title)
            put(WatchHistoryDbHelper.COL_CPI_UPLOADER, item.uploader)
            put(WatchHistoryDbHelper.COL_CPI_THUMBNAIL, item.thumbnailUrl)
            put(WatchHistoryDbHelper.COL_CPI_DURATION, item.duration)
            put(WatchHistoryDbHelper.COL_CPI_ADDED_AT, now)
            put(WatchHistoryDbHelper.COL_CPI_SORT_ORDER, item.sortOrder)
        }
        db.insertWithOnConflict(
            WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLIST_ITEMS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        val updateValues = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_CP_UPDATED_AT, now)
        }
        db.update(
            WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLISTS,
            updateValues,
            "${WatchHistoryDbHelper.COL_CP_ID} = ?",
            arrayOf(playlistId.toString())
        )
        loadPlaylistItemsIntoFlow(playlistId)
        loadPlaylistsIntoFlow()
    }

    override suspend fun removeVideoFromPlaylist(playlistId: Long, videoId: String): Unit = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(
            WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLIST_ITEMS,
            "${WatchHistoryDbHelper.COL_CPI_PLAYLIST_ID} = ? AND ${WatchHistoryDbHelper.COL_CPI_VIDEO_ID} = ?",
            arrayOf(playlistId.toString(), videoId)
        )
        val updateValues = ContentValues().apply {
            put(WatchHistoryDbHelper.COL_CP_UPDATED_AT, System.currentTimeMillis())
        }
        db.update(
            WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLISTS,
            updateValues,
            "${WatchHistoryDbHelper.COL_CP_ID} = ?",
            arrayOf(playlistId.toString())
        )
        loadPlaylistItemsIntoFlow(playlistId)
        loadPlaylistsIntoFlow()
    }

    override suspend fun isVideoInPlaylist(playlistId: Long, videoId: String): Boolean = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLIST_ITEMS,
            arrayOf(WatchHistoryDbHelper.COL_CPI_ID),
            "${WatchHistoryDbHelper.COL_CPI_PLAYLIST_ID} = ? AND ${WatchHistoryDbHelper.COL_CPI_VIDEO_ID} = ?",
            arrayOf(playlistId.toString(), videoId),
            null,
            null,
            null
        )
        cursor.use { it.moveToFirst() }
    }

    override suspend fun getPlaylistById(playlistId: Long): CustomPlaylist? = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT p.${WatchHistoryDbHelper.COL_CP_ID},
                   p.${WatchHistoryDbHelper.COL_CP_TITLE},
                   p.${WatchHistoryDbHelper.COL_CP_DESCRIPTION},
                   p.${WatchHistoryDbHelper.COL_CP_CREATED_AT},
                   p.${WatchHistoryDbHelper.COL_CP_UPDATED_AT},
                   COUNT(i.${WatchHistoryDbHelper.COL_CPI_ID}) as item_count,
                   MIN(i.${WatchHistoryDbHelper.COL_CPI_THUMBNAIL}) as first_thumb
            FROM ${WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLISTS} p
            LEFT JOIN ${WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLIST_ITEMS} i
                   ON p.${WatchHistoryDbHelper.COL_CP_ID} = i.${WatchHistoryDbHelper.COL_CPI_PLAYLIST_ID}
            WHERE p.${WatchHistoryDbHelper.COL_CP_ID} = ?
            GROUP BY p.${WatchHistoryDbHelper.COL_CP_ID}
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(playlistId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_ID))
                val title = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_TITLE))
                val desc = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_DESCRIPTION))
                val createdAt = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_CREATED_AT))
                val updatedAt = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_UPDATED_AT))
                val count = it.getInt(it.getColumnIndexOrThrow("item_count"))
                val thumb = it.getString(it.getColumnIndexOrThrow("first_thumb")).orEmpty()
                CustomPlaylist(
                    id = id,
                    title = title,
                    description = desc,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    itemCount = count,
                    thumbnailFallbackUrl = thumb
                )
            } else {
                null
            }
        }
    }

    private fun loadPlaylistsIntoFlow() {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT p.${WatchHistoryDbHelper.COL_CP_ID},
                   p.${WatchHistoryDbHelper.COL_CP_TITLE},
                   p.${WatchHistoryDbHelper.COL_CP_DESCRIPTION},
                   p.${WatchHistoryDbHelper.COL_CP_CREATED_AT},
                   p.${WatchHistoryDbHelper.COL_CP_UPDATED_AT},
                   COUNT(i.${WatchHistoryDbHelper.COL_CPI_ID}) as item_count,
                   MIN(i.${WatchHistoryDbHelper.COL_CPI_THUMBNAIL}) as first_thumb
            FROM ${WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLISTS} p
            LEFT JOIN ${WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLIST_ITEMS} i
                   ON p.${WatchHistoryDbHelper.COL_CP_ID} = i.${WatchHistoryDbHelper.COL_CPI_PLAYLIST_ID}
            GROUP BY p.${WatchHistoryDbHelper.COL_CP_ID}
            ORDER BY p.${WatchHistoryDbHelper.COL_CP_UPDATED_AT} DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        val list = mutableListOf<CustomPlaylist>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_ID))
                val title = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_TITLE))
                val desc = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_DESCRIPTION))
                val createdAt = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_CREATED_AT))
                val updatedAt = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CP_UPDATED_AT))
                val count = it.getInt(it.getColumnIndexOrThrow("item_count"))
                val thumb = it.getString(it.getColumnIndexOrThrow("first_thumb")).orEmpty()
                list.add(
                    CustomPlaylist(
                        id = id,
                        title = title,
                        description = desc,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        itemCount = count,
                        thumbnailFallbackUrl = thumb
                    )
                )
            }
        }
        playlistsFlow.value = list
    }

    private fun loadPlaylistItemsIntoFlow(playlistId: Long) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WatchHistoryDbHelper.TABLE_CUSTOM_PLAYLIST_ITEMS,
            null,
            "${WatchHistoryDbHelper.COL_CPI_PLAYLIST_ID} = ?",
            arrayOf(playlistId.toString()),
            null,
            null,
            "${WatchHistoryDbHelper.COL_CPI_SORT_ORDER} ASC, ${WatchHistoryDbHelper.COL_CPI_ADDED_AT} DESC"
        )
        val list = mutableListOf<CustomPlaylistItem>()
        cursor.use {
            while (it.moveToNext()) {
                val itemId = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_ID))
                val pId = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_PLAYLIST_ID))
                val vId = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_VIDEO_ID))
                val url = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_URL))
                val title = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_TITLE))
                val uploader = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_UPLOADER))
                val thumb = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_THUMBNAIL))
                val dur = it.getString(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_DURATION))
                val addedAt = it.getLong(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_ADDED_AT))
                val sortOrder = it.getInt(it.getColumnIndexOrThrow(WatchHistoryDbHelper.COL_CPI_SORT_ORDER))
                list.add(
                    CustomPlaylistItem(
                        id = itemId,
                        playlistId = pId,
                        videoId = vId,
                        url = url,
                        title = title,
                        uploader = uploader,
                        thumbnailUrl = thumb,
                        duration = dur,
                        addedAt = addedAt,
                        sortOrder = sortOrder
                    )
                )
            }
        }
        val flow = itemsFlows.getOrPut(playlistId) { MutableStateFlow(emptyList()) }
        flow.value = list
    }
}
