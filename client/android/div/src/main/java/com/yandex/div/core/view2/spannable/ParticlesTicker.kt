package com.yandex.div.core.view2.spannable

import android.view.Choreographer
import android.widget.TextView

internal class ParticlesTicker(private val view: TextView) : Choreographer.FrameCallback {

    private val choreographer = Choreographer.getInstance()
    private val spans = LinkedHashSet<MaskSpan>()
    private var running = false
    private var lastFrameNs = 0L

    fun track(span: MaskSpan) {
        spans += span
        resumeIfNeeded()
    }

    fun untrack(span: MaskSpan) {
        spans.remove(span)
        if (spans.isEmpty()) stop()
    }

    fun resumeIfNeeded() {
        if (running || spans.isEmpty() || !view.isShown) return
        running = true
        lastFrameNs = 0L
        choreographer.postFrameCallback(this)
    }

    fun stop() {
        if (!running) return
        running = false
        choreographer.removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!running) return
        if (spans.isEmpty() || !view.isShown) {
            stop()
            return
        }
        val dt = when (lastFrameNs) {
            0L -> 0f
            else -> ((frameTimeNanos - lastFrameNs) * 1e-9f).coerceIn(0f, 0.05f)
        }
        lastFrameNs = frameTimeNanos

        var anyActive = false
        val it = spans.iterator()
        while (it.hasNext()) {
            val s = it.next()
            if (!s.isAlive()) {
                it.remove()
                continue
            }
            anyActive = s.onFrame(dt) || anyActive
        }
        if (spans.isEmpty() || !anyActive) {
            stop()
            return
        }

        view.postInvalidateOnAnimation()
        choreographer.postFrameCallback(this)
    }
}
