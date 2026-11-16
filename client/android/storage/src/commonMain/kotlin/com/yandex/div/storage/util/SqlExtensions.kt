package com.yandex.div.storage.util

import androidx.sqlite.SQLiteException
import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor
import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.database.DatabaseOpenHelper.Statement as SQLiteStatement

internal fun SQLiteStatement.bindNullableBlob(index: Int, value: ByteArray?) {
    if (value == null) bindNull(index) else bindBlob(index, value)
}

internal fun AutoCloseable.closeSilently() {
    close()
}

internal fun Database.endTransactionSilently() {
    try {
        endTransaction()
    } catch (ignored: IllegalStateException) {
    }
}

internal fun Cursor.getBlobOrNull(
    index: Int,
): ByteArray? = if (isNull(index)) null else getBlob(index)

internal fun Cursor.getColumnIndexOrThrow(
    name: String,
): Int = getColumnIndex(name).also { require(it >= 0) { "Column '$name' not found" } }

internal fun sqliteException(message: String, cause: Throwable): SQLiteException =
    SQLiteException(message).also { it.addSuppressed(cause) }
