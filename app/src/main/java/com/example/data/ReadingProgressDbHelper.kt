package com.example.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class ReadingProgressRecord(
    val userId: String,
    val modelId: String,
    val isCompleted: Boolean,
    val completedAt: Long
)

class ReadingProgressDbHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        const val DATABASE_NAME = "manager101_progress.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "reading_progress"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_MODEL_ID = "model_id"
        const val COLUMN_IS_COMPLETED = "is_completed"
        const val COLUMN_COMPLETED_AT = "completed_at"

        private const val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_USER_ID TEXT NOT NULL,
                $COLUMN_MODEL_ID TEXT NOT NULL,
                $COLUMN_IS_COMPLETED INTEGER NOT NULL DEFAULT 1,
                $COLUMN_COMPLETED_AT INTEGER NOT NULL,
                PRIMARY KEY ($COLUMN_USER_ID, $COLUMN_MODEL_ID)
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun markCompleted(
        userId: String,
        modelId: String,
        completedAt: Long = System.currentTimeMillis()
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_MODEL_ID, modelId)
            put(COLUMN_IS_COMPLETED, 1)
            put(COLUMN_COMPLETED_AT, completedAt)
        }
        val result = db.insertWithOnConflict(
            TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        return result != -1L
    }

    fun getCompletedModelIds(userId: String): Set<String> {
        val db = readableDatabase
        val completedIds = mutableSetOf<String>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_MODEL_ID),
            "$COLUMN_USER_ID = ? AND $COLUMN_IS_COMPLETED = 1",
            arrayOf(userId),
            null,
            null,
            null
        )
        cursor.use {
            val modelIdIndex = it.getColumnIndex(COLUMN_MODEL_ID)
            while (it.moveToNext()) {
                if (modelIdIndex >= 0) {
                    completedIds.add(it.getString(modelIdIndex))
                }
            }
        }
        return completedIds
    }

    fun isCompleted(userId: String, modelId: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_MODEL_ID),
            "$COLUMN_USER_ID = ? AND $COLUMN_MODEL_ID = ? AND $COLUMN_IS_COMPLETED = 1",
            arrayOf(userId, modelId),
            null,
            null,
            null,
            "1"
        )
        cursor.use {
            return it.count > 0
        }
    }

    fun getAllRecords(userId: String): List<ReadingProgressRecord> {
        val db = readableDatabase
        val records = mutableListOf<ReadingProgressRecord>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_USER_ID, COLUMN_MODEL_ID, COLUMN_IS_COMPLETED, COLUMN_COMPLETED_AT),
            "$COLUMN_USER_ID = ?",
            arrayOf(userId),
            null,
            null,
            "$COLUMN_COMPLETED_AT DESC"
        )
        cursor.use {
            val userCol = it.getColumnIndex(COLUMN_USER_ID)
            val modelCol = it.getColumnIndex(COLUMN_MODEL_ID)
            val completedCol = it.getColumnIndex(COLUMN_IS_COMPLETED)
            val timeCol = it.getColumnIndex(COLUMN_COMPLETED_AT)
            while (it.moveToNext()) {
                if (userCol >= 0 && modelCol >= 0 && completedCol >= 0 && timeCol >= 0) {
                    records.add(
                        ReadingProgressRecord(
                            userId = it.getString(userCol),
                            modelId = it.getString(modelCol),
                            isCompleted = it.getInt(completedCol) == 1,
                            completedAt = it.getLong(timeCol)
                        )
                    )
                }
            }
        }
        return records
    }

    fun clearProgress(userId: String): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_USER_ID = ?", arrayOf(userId))
    }
}
