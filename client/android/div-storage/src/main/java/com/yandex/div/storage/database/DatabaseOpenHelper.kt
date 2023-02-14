package com.yandex.div.storage.database

import android.database.Cursor
import android.database.sqlite.SQLiteStatement
import java.io.Closeable

/**
 * Abstraction layer for SQLite Open Helper.
 */
internal interface DatabaseOpenHelper {
    val readableDatabase: Database
    val writableDatabase: Database

    interface Database : Closeable {
        fun execSQL(sql: String)
        fun query(
            table: String,
            columns: Array<String?>?,
            selection: String?,
            selectionArgs: Array<String?>?,
            groupBy: String?,
            having: String?,
            orderBy: String?,
            limit: String?
        ): Cursor

        fun rawQuery(query: String, selectionArgs: Array<out String?>?): Cursor
        fun beginTransaction()
        fun setTransactionSuccessful()
        fun endTransaction()
        fun compileStatement(sql: String): SQLiteStatement
    }

    fun interface CreateCallback {
        fun onCreate(db: Database)
    }

    fun interface UpgradeCallback {
        fun onUpgrade(db: Database, oldVersion: Int, newVersion: Int)
    }
}
