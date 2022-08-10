package com.yandex.div.histogram

import java.util.concurrent.ConcurrentHashMap

abstract class HistogramCallTypeChecker {

    private val reportedHistograms by lazy { ConcurrentHashMap<String, Unit>() }

    fun addReported(histogramName: String): Boolean {
        if (reportedHistograms.containsKey(histogramName)) {
            return false
        }
        return reportedHistograms.putIfAbsent(histogramName, Unit) == null
    }
}
