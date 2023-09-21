package com.yandex.div.internal.viewpool.optimization

import androidx.annotation.AnyThread
import com.yandex.div.internal.KLog
import com.yandex.div.internal.viewpool.Profiler
import java.util.concurrent.ExecutorService

class PerformanceDependentSessionProfiler internal constructor(
    private val repository: PerformanceDependentSessionRepository,
    private val optimizer: ViewPreCreationProfileOptimizer,
    private val executorService: ExecutorService
) : Profiler() {
    private var session: PerformanceDependentSession? = null

    @AnyThread
    override fun onViewObtainedWithoutBlock(
        viewName: String,
        durationNs: Long,
        viewsLeft: Int
    ) {
        session?.viewObtained(viewName, durationNs, viewsLeft, false)
    }

    @AnyThread
    override fun onViewObtainedWithBlock(viewName: String, durationNs: Long, viewsLeft: Int) {
        session?.viewObtained(viewName, durationNs, viewsLeft, true)
    }

    @AnyThread
    override fun onViewRequested(viewName: String, durationNs: Long, viewsLeft: Int) = Unit

    fun start() {
        session?.run {
            clear()
            KLog.w(TAG) { "PerformanceDependentSessionProfiler.start() was called, but session recording was already in progress, ignoring previous session" }
        } ?: run { session = PerformanceDependentSession() }
    }

    fun end(): PerformanceDependentSession? = session?.also {
        session = null
        executorService.submit {
            if (repository.save(it)) {
                optimizer.optimize()
            }
        }
    } ?: run {
        KLog.e(TAG) { "PerformanceDependentSessionProfiler.end() needs to be called after PerformanceDependentSessionProfiler.start()" }
        null
    }

    private companion object {
        const val TAG = "PerformanceDependentSessionProfiler"
    }
}
