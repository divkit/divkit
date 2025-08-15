package com.yandex.divkit.demo.perf

import com.yandex.div.internal.KAssert
import com.yandex.perftests.core.Reporter
import com.yandex.perftests.core.Units
import java.util.concurrent.TimeUnit

object PerfMetricReporter {

    fun reportCountMetric(metricName: String, value: Long) {
        Reporter.reportMetric(metricName, Units.COUNT, value)
    }

    fun reportTimeMetric(metricName: String, unit: TimeUnit, value: Long) {
        Reporter.reportMetric(metricName, unit.toPerfUnits(), value)
    }

    private fun TimeUnit.toPerfUnits(): Units {
        return when (this) {
            TimeUnit.NANOSECONDS -> Units.NANOSECONDS
            TimeUnit.MICROSECONDS -> Units.MICROSECONDS
            TimeUnit.MILLISECONDS -> Units.MILLISECONDS
            else -> {
                KAssert.fail { "Unsupported time unit: $this" }
                Units.MILLISECONDS
            }
        }
    }
}
