package com.yandex.div.storage.database

import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor

internal class ReadState(
    private val onCloseState: () -> Unit = { },
        private val cursorProvider: () -> Cursor,
) : AutoCloseable {
    private var _cursor: Cursor? = null
    val cursor: Cursor
        get() {
            if (_cursor != null) {
                throw RuntimeException("Cursor should be called only once")
            }
            val c = cursorProvider()
            _cursor = c
            return c
        }

    override fun close() {
        AutoCloseable(onCloseState).use {
            _cursor?.takeUnless { it.isClosed }?.close()
        }
    }
}
