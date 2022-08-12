package com.yandex.divkit.demo.div.editor

import com.yandex.div.core.util.Log
import java.lang.IllegalStateException

private const val DIV_EDITOR = "DIV_EDITOR"

class DivEditorLogger(
    private val onErrorCallback: (String) -> Unit
) {

    fun logError(message: String, throwable: Throwable? = null) {
        Log.e(DIV_EDITOR, message, throwable ?: IllegalStateException(message))
        onErrorCallback(message)
    }

    fun log(message: String) {
        Log.d(DIV_EDITOR, message)
    }
}
