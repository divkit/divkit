package com.yandex.div.compose

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.internal.Log

/**
 * Handles error and warning reports from the DivKit pipeline.
 *
 * Override the methods to provide custom logging.
 *
 * @see DivComposeConfiguration
 */
@PublicApi
open class DivReporter {

    open fun reportError(message: String) {
        Log.e(TAG, message)
    }

    open fun reportError(e: Exception) {
        Log.e(TAG, "", e)
    }

    open fun reportWarning(message: String) {
        Log.w(TAG, message)
    }
}

private const val TAG = "DivReporter"
