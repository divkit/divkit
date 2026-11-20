package com.yandex.div.compose.histogram

import com.yandex.div.core.histogram.HistogramBridge
import com.yandex.div.histogram.HistogramFilter

/**
 * Configuration for [com.yandex.div.compose.DivView] performance histograms reporting.
 */
interface DivHistogramConfiguration {
    val isEnabled: Boolean
    val componentName: String
    val histogramBridge: HistogramBridge

    /**
     * [HistogramFilter] for the `DivCompose.Render.Prepare` histogram: the time
     * [com.yandex.div.compose.DivView] spends getting the card context before composition
     * (DI component, root variables and triggers). It is not part of `DivCompose.Render.Total`.
     * The context is created on the first render unless
     * [com.yandex.div.compose.DivContext.preload] or a patch has already created it; later
     * renders only look it up. Off by default.
     */
    val prepareFilter: HistogramFilter
        get() = HistogramFilter.OFF
}
