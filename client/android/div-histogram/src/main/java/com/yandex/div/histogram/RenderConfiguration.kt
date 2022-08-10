package com.yandex.div.histogram

import com.yandex.div.core.annotations.PublicApi

/**
 * Holds configuration for rendering histograms.
 */
@PublicApi
class RenderConfiguration @JvmOverloads constructor(
    /**
     * [HistogramFilter] for measure histogram.
     */
    val measureFilter: HistogramFilter = HistogramFilter.OFF,
    /**
     * [HistogramFilter] for layout histogram.
     */
    val layoutFilter: HistogramFilter = HistogramFilter.OFF,
    /**
     * [HistogramFilter] for draw histogram.
     */
    val drawFilter: HistogramFilter = HistogramFilter.OFF,
    /**
     * [HistogramFilter] for total rendering histogram.
     */
    val totalFilter: HistogramFilter = HistogramFilter.ON
)
