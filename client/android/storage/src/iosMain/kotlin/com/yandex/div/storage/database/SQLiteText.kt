package com.yandex.div.storage.database

import androidx.sqlite.SQLiteStatement

/**
 * Reads the full text value from the UTF-8 SQLite database.
 *
 * The AndroidX Native implementation of [SQLiteStatement.getText] stops at the first NUL
 * (`U+0000`). For example, a stored ID `a\u0000b` would be read as `a`, so it would no longer
 * match the original ID when selecting or deleting the record.
 * [SQLiteStatement.getBlob] returns all bytes of the value, including bytes after the NUL.
 * Decoding those bytes as UTF-8 preserves the original ID. Android and JVM use `getText` directly.
 */
internal actual fun SQLiteStatement.readText(index: Int): String = getBlob(index).decodeToString()
