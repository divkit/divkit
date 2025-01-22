package com.yandex.divkit.demo.benchmark

import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.util.isEmpty
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div.json.withLogger
import com.yandex.div2.DivData
import com.yandex.div2.DivTemplate
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.div.createDivDataWithHistograms
import com.yandex.divkit.demo.div.parseTemplatesWithHistograms
import com.yandex.divkit.demo.div.toJSONObjectWithHistograms
import com.yandex.divkit.demo.utils.JsonAssetReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

internal class Div2StateBenchmark(
    private val divContext: Div2Context,
    private val viewController: Div2BenchmarkViewController
) {

    private val mainTemplateProvider = CachingTemplateProvider<DivTemplate>(
        InMemoryTemplateProvider(),
        TemplateProvider.empty(),
    )
    private val assetReader = JsonAssetReader(divContext)
    private val divDataTag = DivDataTag(UUID.randomUUID().toString())
    private val benchmarkMetrics = linkedMapOf<String, Long>().withDefault { 0L }

    private val job = SupervisorJob()
    private val exceptionHandler = BenchmarkCoroutineExceptionHandler(viewController)
    private val mainContext = Dispatchers.Main + job + exceptionHandler
    private val backgroundContext = newSingleThreadContext("BenchmarkThread")

    fun run(assetName: String, statePaths: List<String>) {
        CoroutineScope(mainContext).launch {
            warmUp()
            pass(assetName, statePaths)
            finish()
        }
    }

    private suspend fun warmUp() {
        viewController.showMessage("Warming up…")
        divContext.warmUp()
        Container.parsingHistogramReporter
        delay(1_000L)
    }

    private suspend fun pass(assetName: String, statePaths: List<String>) {
        val json = readJsonFile(assetName)
        val environment = parseTemplates(json.optJSONObject("templates")).withLogger { e ->
            throw RuntimeException(
                "Benchmark expects divs without errors! Please fix them first!", e
            )
        }
        val divData = parseCard(environment, json.getJSONObject("card"))
        val divView = createView(divData)
        displayView(divView)
        switchState(divView, statePaths)
        reportMetrics()
    }

    fun cancel() {
        job.cancel()
        backgroundContext.close()
        benchmarkMetrics.clear()
    }

    private suspend fun readJsonFile(assetName: String): JSONObject {
        viewController.showMessage("Reading JSON file…")
        val metrics = withContext(backgroundContext) {
            val jsonString = assetReader.readText(assetName)
            profile { jsonString.toJSONObjectWithHistograms() }
        }

        benchmarkMetrics[METRIC_PARSING_JSON] = metrics.wallTime
        return metrics.result
    }

    private suspend fun parseTemplates(templatesJson: JSONObject?): ParsingEnvironment {
        if (templatesJson == null || templatesJson.isEmpty()) {
            return DivParsingEnvironment(ParsingErrorLogger.LOG, mainTemplateProvider)
        }

        viewController.showMessage("Parsing templates…")
        val metrics = withContext(backgroundContext) {
            profile {
                DivParsingEnvironment({ e ->
                    ParsingErrorLogger.LOG.logError(e)
                    throw RuntimeException(
                        "Benchmark expects div templates without errors! Please fix them first!", e
                    )
                }, mainTemplateProvider).apply {
                    parseTemplatesWithHistograms(templatesJson)
                }
            }
        }

        benchmarkMetrics[METRIC_PARSING_TEMPLATES] = metrics.wallTime
        return metrics.result
    }

    private suspend fun parseCard(environment: ParsingEnvironment, cardJson: JSONObject): DivData {
        viewController.showMessage("Parsing card…")
        val metrics = withContext(backgroundContext) {
            profile {
                environment.createDivDataWithHistograms(cardJson)
            }
        }

        benchmarkMetrics[METRIC_PARSING_DATA] = metrics.wallTime
        return metrics.result
    }

    private fun createView(divData: DivData): Div2View {
        val divView = Div2View(divContext)
        divView.setData(divData, divDataTag)
        return divView
    }

    private suspend fun displayView(divView: Div2View) {
        viewController.showDiv(divView)
        viewController.onNextFrame { metrics ->
            benchmarkMetrics[METRIC_VIEW_MEASURE] = metrics.measure
            benchmarkMetrics[METRIC_VIEW_LAYOUT] = metrics.layout
            benchmarkMetrics[METRIC_VIEW_DRAW] = metrics.draw
        }
        delay(2_000L)
    }

    private suspend fun switchState(divView: Div2View, statePaths: List<String>) {
        val parsedPaths = statePaths.map { path -> DivStatePath.parse(path) }
        val metrics = profile {
            divView.switchToMultipleStates(parsedPaths, temporary = true, withAnimations = false)
        }
        benchmarkMetrics[METRIC_VIEW_STATE_SWITCHING] = metrics.wallTime
        viewController.onNextFrame { frameMetrics ->
            benchmarkMetrics[METRIC_VIEW_MEASURE] = frameMetrics.measure
            benchmarkMetrics[METRIC_VIEW_LAYOUT] = frameMetrics.layout
            benchmarkMetrics[METRIC_VIEW_DRAW] = frameMetrics.draw
        }
        delay(2_000L)
    }

    private fun reportMetrics() {
        reportParsingMetrics()
        reportRenderMetrics()
        benchmarkMetrics.clear()
    }

    private fun reportParsingMetrics() {
        val jsonParsingTime = benchmarkMetrics.getValue(METRIC_PARSING_JSON)
        val templatesParsingTime = benchmarkMetrics.getValue(METRIC_PARSING_TEMPLATES)
        val dataParsingTime = benchmarkMetrics.getValue(METRIC_PARSING_DATA)
        val parsingTotalTime = jsonParsingTime + templatesParsingTime + dataParsingTime

        reportTime(metricName = METRIC_PARSING_TOTAL, parsingTotalTime)
    }

    private fun reportRenderMetrics() {
        val viewBindingTime = benchmarkMetrics.getValue(METRIC_VIEW_STATE_SWITCHING)
        reportTime(metricName = METRIC_VIEW_STATE_SWITCHING, viewBindingTime)
    }

    private fun finish() {
        viewController.showMessage("Finished")
    }
}
