package com.yandex.div.internal.viewpool

import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread

internal class FrameProfiler(
    private val reporter: ViewPoolProfiler.Reporter
) : Profiler() {
    private val session = ProfilingSession()
    private val frameWatcher = FrameWatcher()
    private val handler = Handler(Looper.getMainLooper())

    @AnyThread
    override fun onViewObtainedWithoutBlock(
        viewName: String,
        durationNs: Long,
        viewsLeft: Int
    ) {
        synchronized(session) {
            session.viewObtainedWithoutBlock(durationNs)
            frameWatcher.watch(handler)
        }
    }

    @AnyThread
    override fun onViewObtainedWithBlock(
        viewName: String,
        durationNs: Long,
        viewsLeft: Int
    ) {
        synchronized(session) {
            session.viewObtainedWithBlock(viewName, durationNs)
            frameWatcher.watch(handler)
        }
    }

    @AnyThread
    override fun onViewRequested(viewName: String, durationNs: Long, viewsLeft: Int) {
        synchronized(session) {
            session.viewRequested(durationNs)
            frameWatcher.watch(handler)
        }
    }

    private inner class FrameWatcher : Runnable {

        private var watching: Boolean = false

        override fun run() {
            onFrameReady()
            watching = false
        }

        fun watch(handler: Handler) {
            if (!watching) {
                handler.post(this)
                watching = true
            }
        }
    }

    internal fun onFrameReady() {
        synchronized(session) {
            if (session.hasLongEvents()) {
                val result = session.flush()
                reporter.reportEvent("view pool profiling", result)
            }
            session.clear()
        }
    }
}
