package com.yandex.div.internal.viewpool

import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread

class ViewPoolProfiler(private val reporter: Reporter) {

    private val session = ProfilingSession()
    private val frameWatcher = FrameWatcher()
    private val handler = Handler(Looper.getMainLooper())

    @AnyThread
    fun onViewObtainedWithoutBlock(durationNs: Long) {
        synchronized(session) {
            session.viewObtainedWithoutBlock(durationNs)
            frameWatcher.watch(handler)
        }
    }

    @AnyThread
    fun onViewObtainedWithBlock(viewName: String, durationNs: Long) {
        synchronized(session) {
            session.viewObtainedWithBlock(viewName, durationNs)
            frameWatcher.watch(handler)
        }
    }

    @AnyThread
    fun onViewRequested(durationNs: Long) {
        synchronized(session) {
            session.viewRequested(durationNs)
            frameWatcher.watch(handler)
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
