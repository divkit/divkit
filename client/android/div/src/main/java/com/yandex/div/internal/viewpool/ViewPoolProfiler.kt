package com.yandex.div.internal.viewpool

import androidx.annotation.AnyThread

class ViewPoolProfiler(private val profilers: List<Profiler>) : Profiler() {
    @AnyThread
    override fun onViewObtainedWithoutBlock(viewName: String, durationNs: Long, viewsLeft: Int) {
        profilers.forEach { it.onViewObtainedWithoutBlock(viewName, durationNs, viewsLeft) }
    }

    @AnyThread
    override fun onViewObtainedWithBlock(viewName: String, durationNs: Long, viewsLeft: Int) {
        profilers.forEach { it.onViewObtainedWithBlock(viewName, durationNs, viewsLeft) }
    }

    @AnyThread
    override fun onViewRequested(viewName: String, durationNs: Long, viewsLeft: Int) {
        profilers.forEach { it.onViewRequested(viewName, durationNs, viewsLeft) }
    }

    interface Reporter {
        fun reportEvent(message: String, result: Map<String, Any>)

        companion object {

            @JvmField
            val NO_OP = object : Reporter {
                override fun reportEvent(message: String, result: Map<String, Any>) = Unit
            }
        }
    }
}

abstract class Profiler {
    @AnyThread
    internal abstract fun onViewObtainedWithoutBlock(
        viewName: String,
        durationNs: Long,
        viewsLeft: Int
    )

    @AnyThread
    internal abstract fun onViewObtainedWithBlock(
        viewName: String,
        durationNs: Long,
        viewsLeft: Int
    )

    @AnyThread
    internal abstract fun onViewRequested(viewName: String, durationNs: Long, viewsLeft: Int)
}
