package com.yandex.divkit.demo.divstorage

import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.storage.histogram.HistogramNameProvider

/**
 * Used in histogram reports.
 *
 * Report message prefix format:
 * <[divLoadTemplatesReportName]/[divDataLoadReportName]>.<[coldCallTypeSuffix]/[hotCallTypeSuffix]>
 * Sample: "StorageDivDataLoad.Cold".
 */
class DemoAppHistogramNameProvider: HistogramNameProvider {

    /**
     * Name of component under test
     */
    override val componentName: String = "com.yandex.divkit.demo"
    /**
     * Display name in report for load templates.
     */
    override val divLoadTemplatesReportName: String = "Storage.Templates.Load"
    /**
     * Display name in report for data load.
     */
    override val divDataLoadReportName: String = "Storage.Data.Load"
    /**
     * Base name for templates parsing histogram.
     */
    override val divParsingHistogramName: String = "Storage.Templates.Parsing"
    /**
     * Display suffix in report for first start.
     */
    override val coldCallTypeSuffix: String get() = HistogramCallType.CALL_TYPE_COLD
    /**
     * Display suffix in report for hot start.
     */
    override val hotCallTypeSuffix: String get() = HistogramCallType.CALL_TYPE_WARM

    //currently used only as component name
    override fun getHistogramNameFromCardId(cardId: String): String = componentName
}
