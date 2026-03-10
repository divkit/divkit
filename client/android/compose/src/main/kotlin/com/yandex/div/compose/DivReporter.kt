package com.yandex.div.compose

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.internal.Log

@PublicApi
open class DivReporter {

    open fun reportError(message: String) {
        Log.e(TAG, message)
    }

    open fun reportError(message: String, e: Throwable) {
        Log.e(TAG, message, e)
    }

    open fun reportWarning(message: String) {
        Log.w(TAG, message)
    }
}

private const val TAG = "DivReporter"
