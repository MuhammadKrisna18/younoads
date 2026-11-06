package com.example.yoouutt.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.yoouutt.data.repository.SearchHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SqliteSearchHistoryRepository(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION), SearchHistoryRepository {

    private val historyFlow = MutableStateFlow<List<String>>(emptyList())

    init {
        loadHistoryIntoFlow()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                $COL_QUERY TEXT PRIMARY KEY,
                $COL_TIMESTAMP INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun getSearchHistory(): Flow<List<String>> = historyFlow.asStateFlow()

    override suspend fun saveSearchQuery(query: String): Unit = withContext(ioDispatcher) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return@withContext

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_QUERY, cleanQuery)
            put(COL_TIMESTAMP, System.currentTimeMillis())
        }
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        loadHistoryIntoFlow()
    }

    override suspend fun deleteSearchQuery(query: String): Unit = withContext(ioDispatcher) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return@withContext

        val db = writableDatabase
        db.delete(TABLE_NAME, "$COL_QUERY = ?", arrayOf(cleanQuery))
        loadHistoryIntoFlow()
    }

    override suspend fun clearAllSearchHistory(): Unit = withContext(ioDispatcher) {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
        loadHistoryIntoFlow()
    }

    private fun loadHistoryIntoFlow() {
        val db = readableDatabase
        val list = mutableListOf<String>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COL_QUERY),
            null,
            null,
            null,
            null,
            "$COL_TIMESTAMP DESC",
            "50"
        )
        cursor.use {
            while (it.moveToNext()) {
                val item = it.getString(it.getColumnIndexOrThrow(COL_QUERY))
                if (item.isNotBlank()) {
                    list.add(item)
                }
            }
        }
        historyFlow.value = list
    }

    companion object {
        private const val DATABASE_NAME = "younoads_search_history.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "search_history"
        private const val COL_QUERY = "query"
        private const val COL_TIMESTAMP = "timestamp"
    }
}

