package com.yandex.divkit.benchmark

import com.yandex.div.histogram.util.HistogramClock
import com.yandex.divkit.benchmark.perf.PerfMetricReporter

internal class BlockMetrics<T>(
    val result: T,
    val wallTime: Long,
)

internal inline fun <T> profile(block: () -> T): BlockMetrics<T> {
    val wallStart = HistogramClock.uptime()

    val result = block()

    val wallTime = HistogramClock.uptime() - wallStart

    return BlockMetrics(result, wallTime)
}

internal fun reportTime(metricName: String, duration: Long) {
    if (duration <= 0) return
    PerfMetricReporter.reportTimeMetric(metricName, HistogramClock.durationUnit, duration)
}
