package com.yandex.div.compose.histogram

import com.yandex.div.histogram.HistogramBridge

/**
 * Configuration for [com.yandex.div.compose.DivView] performance histograms reporting.
 */
interface DivHistogramConfiguration {
    val isEnabled: Boolean
    val componentName: String
    val histogramBridge: HistogramBridge
}
