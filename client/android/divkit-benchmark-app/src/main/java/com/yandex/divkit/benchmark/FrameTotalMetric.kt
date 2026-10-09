package com.yandex.divkit.benchmark

import android.os.Handler
import android.os.Looper
import android.view.FrameMetrics
import android.view.Window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

internal class FrameTotalMetric(private val window: Window) : AutoCloseable {
    private var pendingResult: CompletableDeferred<Duration>? = null
    private var drawTimeMs: Long? = null

    private val listener = Window.OnFrameMetricsAvailableListener { _, metrics, _ ->
        val result = pendingResult
        val drawTimeMs = drawTimeMs
        if (result != null && drawTimeMs != null) {
            // View.drawingTime identifies the same frame as VSYNC_TIMESTAMP, but truncates to
            // milliseconds. Reports for previously queued frames may arrive after start().
            val frameTimeMs = metrics.getMetric(FrameMetrics.VSYNC_TIMESTAMP) / 1_000_000
            if (frameTimeMs >= drawTimeMs) {
                pendingResult = null
                this.drawTimeMs = null
                val totalDuration = metrics.getMetric(FrameMetrics.TOTAL_DURATION).nanoseconds
                when {
                    frameTimeMs != drawTimeMs -> {
                        result.completeExceptionally(
                            IllegalStateException("Missing frame metrics for $drawTimeMs ms")
                        )
                    }

                    totalDuration.isNegative() -> {
                        result.completeExceptionally(
                            IllegalStateException("FrameMetrics.TOTAL_DURATION is unavailable")
                        )
                    }

                    else -> result.complete(totalDuration)
                }
            }
        }
    }

    init {
        window.addOnFrameMetricsAvailableListener(listener, Handler(Looper.getMainLooper()))
    }

    fun start(): Deferred<Duration> {
        check(pendingResult == null) { "Previous frame measurement is still pending" }
        return CompletableDeferred<Duration>().also { pendingResult = it }
    }

    fun onDraw(drawingTimeMs: Long) {
        if (pendingResult != null && drawTimeMs == null) {
            drawTimeMs = drawingTimeMs
        }
    }

    override fun close() {
        window.removeOnFrameMetricsAvailableListener(listener)
        pendingResult?.cancel()
        pendingResult = null
        drawTimeMs = null
    }
}
