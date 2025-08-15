package com.yandex.test.util

import android.util.Log

object Report {
    private const val TAG = "UiTestReportUtils"

    fun <T : Any?> step(description: String, block: () -> T): T {
        Log.i(TAG, "starting step: $description")
        val result = block()
        Log.i(TAG, "finishing step: $description")
        return result
    }
}
