package com.yandex.div.histogram

/**
 * Filter for reporting histogram.
 */
public fun interface HistogramFilter {
    /**
     * If histogram with the given [componentName] is reported.
     */
    public fun report(componentName: String?): Boolean

    public companion object {
        public val ON: HistogramFilter = HistogramFilter { true }
        public val OFF: HistogramFilter = HistogramFilter { false }
    }
}
