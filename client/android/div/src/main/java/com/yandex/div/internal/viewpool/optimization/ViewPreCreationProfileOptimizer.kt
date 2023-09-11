package com.yandex.div.internal.viewpool.optimization

import com.yandex.div.internal.KLog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

internal class ViewPreCreationProfileOptimizer(
    private val repository: PerformanceDependentSessionRepository,
    private val executorService: ExecutorService
) {
    private var future: Future<*>? = null

    fun optimize() {
        future?.cancel(true)

        future = executorService.submit {
            val data = repository.get()

            runCatching {
                // TODO("DIVKIT-2145")
            }.onFailure { KLog.e(TAG, it) }
        }
    }

    private companion object {
        const val TAG = "ViewPreCreationProfileOptimizer"
    }
}
