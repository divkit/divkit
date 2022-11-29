package com.yandex.divkit.demo.benchmark

import com.yandex.div.internal.KLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

internal class BenchmarkCoroutineExceptionHandler(
    private val viewController: BenchmarkViewController
) : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        if (exception is CancellationException) return
        KLog.e(TAG, exception) { "An error occurred while running benchmark" }
        viewController.showMessage("Error: ${exception.message}")
    }

    private companion object {
        private const val TAG = "BenchmarkCoroutineExceptionHandler"
    }
}
