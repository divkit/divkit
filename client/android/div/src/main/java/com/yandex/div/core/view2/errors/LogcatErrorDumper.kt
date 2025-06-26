package com.yandex.div.core.view2.errors

import com.yandex.div.DivDataTag
import com.yandex.div.internal.KLog
import com.yandex.div.json.ParsingException

private const val TAG = "ErrorVisualMonitor"

/**
 * Handles logging of errors and warnings to logcat with de-duplication support.
 * Prevents the same errors from being logged multiple times.
 */
internal class LogcatErrorDumper {
    private val loggedErrors = mutableSetOf<Throwable>()
    private val loggedWarnings = mutableSetOf<Throwable>()

    fun logErrors(errors: List<Throwable>, warnings: List<Throwable>, dataTag: DivDataTag?) {
        if (errors.isEmpty() && warnings.isEmpty()) {
            return
        }

        val newErrors = errors.filter { error ->
            val isNew = loggedErrors.add(error)
            isNew
        }

        val newWarnings = warnings.filter { warning ->
            val isNew = loggedWarnings.add(warning)
            isNew
        }

        if (newErrors.isNotEmpty()) {
            KLog.e(TAG) { "=== DIV VISUAL ERROR MONITOR - NEW ERRORS DETECTED ===" }
            KLog.e(TAG) { errorsToDetails(newErrors) }
        }

        if (newWarnings.isNotEmpty()) {
            KLog.w(TAG) { "=== DIV VISUAL ERROR MONITOR - NEW WARNINGS DETECTED ===" }
            KLog.w(TAG) { errorsToDetails(newWarnings) }
        }

        if (newErrors.isNotEmpty() || newWarnings.isNotEmpty()) {
            KLog.i(TAG) {
                "DataTag '$dataTag'. New errors: ${newErrors.size}, New warnings: ${newWarnings.size}. " +
                        "Total errors: ${errors.size}, Total warnings: ${warnings.size}"
            }
        }
    }

    private fun errorsToDetails(errors: List<Throwable>) = errors.joinToString(separator = "\n") {
        if (it is ParsingException) {
            " - ${it.reason}: ${it.stackTraceToString()}"
        } else {
            " - ${it.stackTraceToString()}"
        }
    }
}
