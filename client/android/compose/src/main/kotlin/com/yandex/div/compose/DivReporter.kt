package com.yandex.div.compose

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.internal.Log

@PublicApi
open class DivReporter {

    open fun reportError(message: String) {
        Log.e(TAG, message)
    }

    open fun reportError(message: String? = null, throwable: Throwable) {
        Log.e(TAG, message ?: "", throwable)
    }

    open fun reportWarning(message: String) {
        Log.w(TAG, message)
    }
}

private const val TAG = "DivReporter"
