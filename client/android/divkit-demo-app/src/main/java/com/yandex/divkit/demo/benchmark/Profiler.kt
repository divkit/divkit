package com.yandex.divkit.demo.benchmark

import android.os.SystemClock
import com.yandex.divkit.demo.perf.PerfMetricReporter
import java.util.concurrent.TimeUnit

internal class BlockMetrics<T>(
    val result: T,
    val wallTime: Long,
    val threadTime: Long
)

internal inline fun <T> profile(block: () -> T): BlockMetrics<T> {
    val wallStart = SystemClock.uptimeMillis()
    val threadStart = SystemClock.currentThreadTimeMillis()

    val result = block()

    val threadTime = SystemClock.currentThreadTimeMillis() - threadStart
    val wallTime = SystemClock.uptimeMillis() - wallStart

    return BlockMetrics(result, wallTime, threadTime)
}

internal fun reportTime(metricName: String, millis: Long) {
    if (millis <= 0) return
    PerfMetricReporter.reportTimeMetric(metricName, TimeUnit.MILLISECONDS, millis)
}
