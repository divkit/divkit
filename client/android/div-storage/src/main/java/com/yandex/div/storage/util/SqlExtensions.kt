package com.yandex.div.storage.util

import android.database.sqlite.SQLiteStatement
import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import java.io.Closeable
import java.io.IOException

internal fun SQLiteStatement.bindNullableBlob(index: Int, value: ByteArray?) {
    if (value == null) bindNull(index) else bindBlob(index, value)
}

internal fun Closeable.closeSilently() {
    try {
        close()
    } catch (ignored: IOException) {
    }
}

internal fun Database.endTransactionSilently() {
    try {
        endTransaction()
    } catch (ignored: IllegalStateException) {
    }
}
