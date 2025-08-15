package com.yandex.div.storage.histogram

/**
 * Used in histogram reports.
 *
 * Report message prefix format:
 * <[divLoadTemplatesReportName]/[divDataLoadReportName]>.<[coldCallTypeSuffix]/[hotCallTypeSuffix]>
 * Sample: "StorageDivDataLoad.Cold".
 */
interface HistogramNameProvider {
    /**
     * Name of component under test
     */
    val componentName: String
    /**
     * Display name in report for load templates.
     */
    val divLoadTemplatesReportName: String
    /**
     * Display name in report for data load.
     */
    val divDataLoadReportName: String
    /**
     * Base name for templates parsing histogram.
     */
    val divParsingHistogramName: String
    /**
     * Display suffix in report for first start.
     */
    val coldCallTypeSuffix: String get() = "Cold"
    /**
     * Display suffix in report for hot start.
     */
    val hotCallTypeSuffix: String get() = "Hot"

    fun getHistogramNameFromCardId(cardId: String): String?
}
