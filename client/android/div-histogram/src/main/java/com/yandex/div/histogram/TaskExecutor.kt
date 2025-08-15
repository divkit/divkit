
package com.yandex.div.histogram

import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import com.yandex.div.core.annotations.PublicApi

/**
 * Executor for histograms reporting.
 */
@PublicApi
interface TaskExecutor {
    /**
     * Posts given [task] for execution.
     */
    @AnyThread
    fun post(task: () -> Unit)
}

/**
 * Implementation of [TaskExecutor] that delegates execution to main thread [Handler].
 */
class DefaultTaskExecutor : TaskExecutor {

    private val handler = Handler(Looper.getMainLooper())

    override fun post(task: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            task()
        } else {
            handler.post(task)
        }
    }
}
