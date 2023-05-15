package com.yandex.div.storage.database

import android.database.Cursor
import com.yandex.div.internal.util.IOUtils
import java.io.Closeable
import javax.inject.Provider

internal class ReadState constructor(
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
        IOUtils.closeCursorSilently(_cursor)
        onCloseState()
    }
}
