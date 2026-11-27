package com.yandex.div.compose

import com.yandex.div.core.log.Log

/**
 * Handles error and warning reports from the DivKit pipeline.
 *
 * Override the methods to provide custom logging.
 *
 * @see DivConfiguration
 */
public open class DivReporter {

    public open fun reportError(message: String) {
        Log.e(TAG, message)
    }

    public open fun reportError(e: Exception) {
        Log.e(TAG, "", e)
    }

    public open fun reportWarning(message: String) {
        Log.w(TAG, message)
    }
}

private const val TAG = "DivReporter"
