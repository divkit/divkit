package com.yandex.div.internal.viewpool.optimization

import androidx.annotation.AnyThread
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment
import com.yandex.div.internal.KLog
import javax.inject.Inject

@AnyThread
@DivScope
class PerformanceDependentSessionProfiler @Inject constructor(
    @ExperimentFlag(Experiment.VIEW_POOL_OPTIMIZATION_DEBUG)
    private val isDebuggingViewPoolOptimization: Boolean
) {
    private var session: PerformanceDependentSession? = null

    internal inline fun onViewObtained(
        viewName: String,
        durationNs: Long,
        viewsLeft: Int,
        isObtainedWithBlock: Boolean
    ) {
        session?.viewObtained(viewName, durationNs, viewsLeft, isObtainedWithBlock)
    }

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
