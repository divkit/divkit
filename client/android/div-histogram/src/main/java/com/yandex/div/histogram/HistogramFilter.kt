package com.yandex.div.histogram

/**
 * Filter for reporting histogram.
 */
fun interface HistogramFilter {
    /**
     * If histogram with the given [componentName] is reported.
     */
    fun report(componentName: String?): Boolean

    companion object {
        val ON = HistogramFilter { true }
        val OFF = HistogramFilter { false }
    }
}
