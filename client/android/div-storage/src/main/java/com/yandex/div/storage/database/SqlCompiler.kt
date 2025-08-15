package com.yandex.div.storage.database

import android.database.sqlite.SQLiteStatement

/**
 * Abstraction that encapsulates sqlite database queries APIs.
 */
internal interface SqlCompiler {
    fun compileStatement(sql: String): SQLiteStatement
    fun compileQuery(sql: String, vararg selectionArgs: String): ReadState
}
