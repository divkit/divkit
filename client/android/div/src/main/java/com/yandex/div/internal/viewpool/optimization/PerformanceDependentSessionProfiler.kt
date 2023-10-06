package com.yandex.div.internal.viewpool.optimization

import androidx.annotation.AnyThread
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment
import com.yandex.div.internal.KLog
import com.yandex.div.internal.viewpool.Profiler
import javax.inject.Inject

@AnyThread
@DivScope
class PerformanceDependentSessionProfiler @Inject constructor(
    @ExperimentFlag(Experiment.VIEW_POOL_OPTIMIZATION_DEBUG)
    private val isDebuggingViewPoolOptimization: Boolean
) : Profiler() {
    @Volatile
    private var session: PerformanceDependentSession? = null

    override fun onViewObtainedWithoutBlock(
        viewName: String,
        durationNs: Long,
        viewsLeft: Int
    ) {
        session?.viewObtained(viewName, durationNs, viewsLeft, false)
    }

    override fun onViewObtainedWithBlock(viewName: String, durationNs: Long, viewsLeft: Int) {
        session?.viewObtained(viewName, durationNs, viewsLeft, true)
    }

    override fun onViewRequested(viewName: String, durationNs: Long, viewsLeft: Int) = Unit

    fun start() {
        session?.run {
            clear()
            KLog.w(TAG) { "PerformanceDependentSessionProfiler.start() was called, but session recording was already in progress, ignoring previous session" }
        } ?: run {
            session = if (isDebuggingViewPoolOptimization) {
                PerformanceDependentSession.Detailed()
            } else {
                PerformanceDependentSession.Lightweight()
            }
        }
    }

    fun end(): PerformanceDependentSession? = session?.also { session = null } ?: run {
        KLog.e(TAG) { "PerformanceDependentSessionProfiler.end() needs to be called after PerformanceDependentSessionProfiler.start()" }
        null
    }

    private companion object {
        const val TAG = "PerformanceDependentSessionProfiler"
    }
}
