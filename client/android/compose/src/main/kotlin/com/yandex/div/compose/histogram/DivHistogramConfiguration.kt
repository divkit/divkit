package com.yandex.div.compose.histogram

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.histogram.HistogramBridge

/**
 * Configuration for [com.yandex.div.compose.DivView] performance histograms reporting.
 */
@ExperimentalApi
interface DivHistogramConfiguration {
    val isEnabled: Boolean
    val componentName: String
    val histogramBridge: HistogramBridge
}
