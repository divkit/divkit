package com.yandex.div.histogram

/**
 * Holds configuration for rendering histograms.
 */
class RenderConfiguration(
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
    val totalFilter: HistogramFilter = HistogramFilter.ON,
    /**
     * [HistogramFilter] for preparation histogram.
     */
    val prepareFilter: HistogramFilter,
) {

    /**
     * Creates a configuration with the preparation histogram off.
     */
    @JvmOverloads
    constructor(
        measureFilter: HistogramFilter = HistogramFilter.OFF,
        layoutFilter: HistogramFilter = HistogramFilter.OFF,
        drawFilter: HistogramFilter = HistogramFilter.OFF,
        totalFilter: HistogramFilter = HistogramFilter.ON,
    ) : this(measureFilter, layoutFilter, drawFilter, totalFilter, HistogramFilter.OFF)
}
