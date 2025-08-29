package com.yandex.div.json

import com.yandex.div.internal.Assert
import com.yandex.div.internal.KLog

interface LoadingErrorLogger {
    fun logError(e: Exception)

    companion object {
        val ASSERT: LoadingErrorLogger = object : LoadingErrorLogger {
            override fun logError(e: Exception) {
                Assert.fail(e.message, e)
            }
        }
        val LOG: LoadingErrorLogger = object : LoadingErrorLogger {
            override fun logError(e: Exception) {
                KLog.e("LoadingErrorLogger", e) { "An error occurred during loading process" }
            }
        }
    }
}
