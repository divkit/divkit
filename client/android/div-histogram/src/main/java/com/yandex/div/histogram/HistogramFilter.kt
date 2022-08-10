package com.yandex.div.histogram

import com.yandex.div.core.annotations.PublicApi

/**
 * Filter for reporting histogram.
 */
@PublicApi
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
