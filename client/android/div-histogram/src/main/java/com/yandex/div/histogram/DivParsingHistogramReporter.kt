package com.yandex.div.histogram

import android.os.SystemClock
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.histogram.util.HistogramUtils
import org.json.JSONObject
import java.util.concurrent.Executor

/**
 * Histogram reporter to measure and report Div related parsing.
 */
@PublicApi
interface DivParsingHistogramReporter {

    /**
     * Measure DivData parsing, i.e. taken time to invoke DivData.fromJson.
     *
     * @param parse function calling DivData.fromJson.
     */
    fun <D> measureDataParsing(json: JSONObject, componentName: String?, parse: () -> D): D

    /**
     * Measure Div templates parsing, i.e. taken time to invoke ParsingEnvironment.parseTemplates.
     *
     * @param parse function calling ParsingEnvironment.parseTemplates
     */
    fun <T> measureTemplatesParsing(json: JSONObject, componentName: String?, parse: () -> T): T

    /**
     * Measure Div input to JSON parsing, i.e. taken time to fulfill JSONObject from given input.
     *
     * @param parse function that creates JSONObject from input.
     */
    fun measureJsonParsing(componentName: String?, parse: () -> JSONObject): JSONObject

    companion object {
        val DEFAULT: DivParsingHistogramReporter by lazy(::DefaultDivParsingHistogramReporter)
    }
}

private class DefaultDivParsingHistogramReporter : DivParsingHistogramReporter {
    override fun <D> measureDataParsing(
        json: JSONObject,
        componentName: String?,
        parse: () -> D
    ): D = parse()

    override fun <T> measureTemplatesParsing(
        json: JSONObject,
        componentName: String?,
        parse: () -> T
    ): T = parse()

    override fun measureJsonParsing(
        componentName: String?,
        parse: () -> JSONObject
    ): JSONObject = parse()
}

class DivParsingHistogramReporterImpl(
    private val histogramReporter: () -> HistogramReporter,
    private val calculateSizeExecutor: () -> Executor
) : DivParsingHistogramReporter {

    private val currentUptime: Long
        get() = SystemClock.uptimeMillis()

    override fun <D> measureDataParsing(
        json: JSONObject,
        componentName: String?,
        parse: () -> D
    ): D = doMeasure(DIV_PARSING_DATA, json, componentName, parse)

    override fun <T> measureTemplatesParsing(
        json: JSONObject,
        componentName: String?,
        parse: () -> T
    ): T = doMeasure(DIV_PARSING_TEMPLATES, json, componentName, parse)

    override fun measureJsonParsing(
        componentName: String?,
        parse: () -> JSONObject
    ): JSONObject = doMeasure(DIV_PARSING_JSON, null, componentName, parse)

    private inline fun <D> doMeasure(
        histogramName: String,
        json: JSONObject?,
        componentName: String?,
        parse: () -> D
    ): D {
        val startTime = currentUptime
        return try {
            parse()
        } finally {
            val duration = currentUptime - startTime
            reportHistogram(histogramName, duration, componentName, json)
        }
    }

    private fun reportHistogram(
        histogramName: String,
        duration: Long,
        componentName: String?,
        json: JSONObject?
    ) {
        histogramReporter().reportDuration(histogramName, duration, componentName)
        if (json == null) {
            return
        }
        calculateSizeExecutor().execute {
            val size = HistogramUtils.calculateUtf8JsonByteSize(json)
            histogramReporter().reportSize(histogramName, size, componentName)
        }
    }
}
