package com.yandex.div.storage.database

import com.yandex.div.core.annotations.InternalApi

/**
 * Abstraction layer for SQLite Open Helper.
 */
@InternalApi
public interface DatabaseOpenHelper {
    public val readableDatabase: Database
    public val writableDatabase: Database

    public interface Database : AutoCloseable {
        public fun execSQL(sql: String)
        public fun query(
            table: String,
            columns: Array<String?>?,
            selection: String?,
            selectionArgs: Array<String?>?,
            groupBy: String?,
            having: String?,
            orderBy: String?,
            limit: String?,
        ): Cursor

        public fun rawQuery(query: String, selectionArgs: Array<out String?>?): Cursor
        public fun beginTransaction()
        public fun setTransactionSuccessful()
        public fun endTransaction()
        public fun compileStatement(sql: String): Statement
    }

    public interface Cursor : AutoCloseable {
        public val isClosed: Boolean
        public fun moveToFirst(): Boolean
        public fun moveToNext(): Boolean
        public fun getColumnIndex(name: String): Int
        public fun getString(index: Int): String
        public fun getBlob(index: Int): ByteArray
        public fun isNull(index: Int): Boolean
    }

    public interface Statement : AutoCloseable {
        public fun bindString(index: Int, value: String)
        public fun bindBlob(index: Int, value: ByteArray)
        public fun bindNull(index: Int)
        public fun execute()
        public fun executeInsert(): Long
        public fun executeUpdateDelete(): Int
    }

    public fun interface CreateCallback {
        public fun onCreate(db: Database)
    }

    public fun interface UpgradeCallback {
        public fun onUpgrade(db: Database, oldVersion: Int, newVersion: Int)
    }
}
