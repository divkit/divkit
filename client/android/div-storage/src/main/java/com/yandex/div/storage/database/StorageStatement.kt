package com.yandex.div.storage.database

import android.database.SQLException
import android.database.sqlite.SQLiteStatement
import androidx.annotation.WorkerThread

/**
 * Used to execute multiple [SQLiteStatement]s that represent
 * single logical operation in terms of storage.
 */
internal fun interface StorageStatement {
    @WorkerThread
    @Throws(SQLException::class)
    fun execute(compiler: SqlCompiler)
}
