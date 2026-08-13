package com.yandex.div.storage.database

import android.database.Cursor
import java.io.Closeable
import javax.inject.Provider

internal class ReadState(
    private val onCloseState: () -> Unit = { },
        private val cursorProvider: Provider<Cursor>,
) : Closeable {
    private var _cursor: Cursor? = null
    val cursor: Cursor
        get() {
            if (_cursor != null) {
                throw RuntimeException("Cursor should be called only once")
            }
            val c = cursorProvider.get()
            _cursor = c
            return c
        }

    override fun close() {
        val cursor = _cursor
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        onCloseState()
    }
}
