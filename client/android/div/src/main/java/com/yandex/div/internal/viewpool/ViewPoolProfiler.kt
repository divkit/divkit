package com.yandex.div.internal.viewpool

import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread

class ViewPoolProfiler(private val reporter: Reporter) {
    private val session = ProfilingSession()
    private val frameWatcher = FrameWatcher()
    private val handler = Handler(Looper.getMainLooper())

    @AnyThread
    internal fun onViewObtainedWithoutBlock(durationNs: Long) {
        synchronized(session) {
            session.viewObtainedWithoutBlock(durationNs)
            frameWatcher.watch(handler)
        }
    }

    @AnyThread
    internal fun onViewObtainedWithBlock(viewName: String, durationNs: Long) {
        synchronized(session) {
            session.viewObtainedWithBlock(viewName, durationNs)
            frameWatcher.watch(handler)
        }
    }

    @AnyThread
    internal fun onViewRequested(durationNs: Long) {
        session.viewRequested(durationNs)
        frameWatcher.watch(handler)
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
