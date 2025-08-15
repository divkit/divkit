package com.yandex.divkit.demo.benchmark

import com.yandex.div.core.Div2Context
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div2.DivData
import com.yandex.div2.DivTemplate
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.div.createDivDataWithHistograms
import com.yandex.divkit.demo.div.parseTemplatesWithHistograms
import com.yandex.divkit.demo.utils.JsonAssetReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import org.json.JSONObject

internal class Div2FeedBenchmark(
    private val divContext: Div2Context,
    private val viewController: Div2FeedBenchmarkViewController
) {

    private val mainTemplateProvider = CachingTemplateProvider<DivTemplate>(
            InMemoryTemplateProvider(),
            TemplateProvider.empty(),
    )
    private val assetReader = JsonAssetReader(divContext)
    private val benchmarkMetrics = linkedMapOf<String, LongArray>()

    private val job = SupervisorJob()
    private val exceptionHandler = BenchmarkCoroutineExceptionHandler(viewController)
    private val mainContext = Dispatchers.Main + job + exceptionHandler
    private val backgroundContext = newSingleThreadContext("BenchmarkThread")

    fun run(assetNames: Array<String>) {
        GlobalScope.launch(mainContext) {
            warmUp()
            val suite = readJsonFiles(assetNames)
            val environment = parseTemplates(suite)
            val dataSet = parseCards(environment, suite)
            val feedAdapter = createFeedAdapter(dataSet)
            displayFeed(feedAdapter)
            scrollFeed()
            benchmarkMetrics.clear()
            finish()
        }
    }

    fun cancel() {
        job.cancel()
        backgroundContext.close()
        benchmarkMetrics.clear()
    }

    private suspend fun warmUp() {
        viewController.showMessage("Warming up…")
        divContext.warmUp()
        Container.parsingHistogramReporter
        delay(1_000L)
    }

    private suspend fun readJsonFiles(assetNames: Array<String>): List<JSONObject> {
        viewController.showMessage("Reading JSON files…")
        return withContext(backgroundContext) {
            assetNames.map { name ->
                assetReader.readJson(name)
            }
        }
    }

    private suspend fun parseTemplates(suite: List<JSONObject>): ParsingEnvironment {
        viewController.showMessage("Parsing templates…")
        val environment = DivParsingEnvironment(ParsingErrorLogger.LOG, mainTemplateProvider)
        withContext(backgroundContext) {
            suite.forEach { json ->
                json.optJSONObject("templates")?.let {
                    environment.parseTemplatesWithHistograms(it)
                }
            }
        }
        return environment
    }

    private suspend fun parseCards(environment: ParsingEnvironment, suite: List<JSONObject>): List<DivData> {
        viewController.showMessage("Parsing cards…")
        return withContext(backgroundContext) {
            List(FEED_LENGTH) { index ->
                val json = suite[index % suite.size]
                val card = json.getJSONObject("card")
                environment.createDivDataWithHistograms(card)
            }
        }
    }

    private fun createFeedAdapter(dataSet: List<DivData>): Div2FeedAdapter {
        return Div2FeedAdapter(divContext).apply {
            setFeed(dataSet)
            metricsObserver = Div2FeedAdapter.BindMetricsObserver { index, bindingTime, measureTime, layoutTime, drawTime ->
                benchmarkMetrics.getOrPut(METRIC_VIEW_BINDING, emptyMetrics())[index] = bindingTime
                benchmarkMetrics.getOrPut(METRIC_VIEW_MEASURE, emptyMetrics())[index] = measureTime
                benchmarkMetrics.getOrPut(METRIC_VIEW_LAYOUT, emptyMetrics())[index] = layoutTime
                benchmarkMetrics.getOrPut(METRIC_VIEW_DRAW, emptyMetrics())[index] = drawTime
            }
        }
    }

    private suspend fun displayFeed(feedAdapter: Div2FeedAdapter) {
        viewController.showFeed(feedAdapter)
        awaitFrame()
        delay(1_000L)
    }

    private suspend fun scrollFeed() {
        while (viewController.canScrollDown()) {
            viewController.scrollFeed(SCROLL_SPEED_DP)
            awaitFrame()
        }
        delay(1_000L)
    }

    private fun finish() {
        viewController.showMessage("Finished")
    }

    private companion object {

        private const val FEED_LENGTH = 16
        private const val SCROLL_SPEED_DP = 24

        private fun emptyMetrics(): () -> LongArray = { LongArray(FEED_LENGTH) }
    }
}
