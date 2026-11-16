package com.yandex.div.storage.database

import com.yandex.div.storage.database.DatabaseOpenHelper.Statement as SQLiteStatement

/**
 * Abstraction that encapsulates sqlite database queries APIs.
 */
internal interface SqlCompiler {
    fun compileStatement(sql: String): SQLiteStatement
    fun compileQuery(sql: String, vararg selectionArgs: String): ReadState
}
