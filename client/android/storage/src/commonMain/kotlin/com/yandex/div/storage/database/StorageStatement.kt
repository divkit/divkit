package com.yandex.div.storage.database

import androidx.sqlite.SQLiteException as SQLException
import com.yandex.div.storage.database.DatabaseOpenHelper.Statement as SQLiteStatement

/**
 * Used to execute multiple [SQLiteStatement]s that represent
 * single logical operation in terms of storage.
 */
internal fun interface StorageStatement {
    @Throws(SQLException::class)
    fun execute(compiler: SqlCompiler)
}
