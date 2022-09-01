package com.yandex.divkit.demo.perf

import android.util.Log
import java.util.concurrent.TimeUnit

object PerfMetricReporter {

    fun reportCountMetric(metricName: String, value: Long) {
        Log.d("PerfReporter", "$metricName: $value")
    }

    fun reportTimeMetric(metricName: String, unit: TimeUnit, value: Long) {
        Log.d("PerfReporter", "$metricName: $value ${unit.symbol()}")
    }

    private fun TimeUnit.symbol(): String {
        return when (this) {
            TimeUnit.NANOSECONDS -> "ns"
            TimeUnit.MICROSECONDS -> "us"
            TimeUnit.MILLISECONDS -> "ms"
            TimeUnit.SECONDS -> "s"
            TimeUnit.MINUTES -> "m"
            TimeUnit.HOURS -> "h"
            TimeUnit.DAYS -> "d"
        }
    }
}