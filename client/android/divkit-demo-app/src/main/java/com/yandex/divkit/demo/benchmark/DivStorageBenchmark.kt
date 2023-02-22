package com.yandex.divkit.demo.benchmark

import android.app.Activity
import com.yandex.div.core.Div2Context
import com.yandex.div.internal.util.forEach
import com.yandex.div.storage.DivDataRepository
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.RawDataAndMetadata
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.divstorage.DemoAppHistogramNameProvider
import com.yandex.divkit.demo.utils.JsonAssetReader
import com.yandex.divkit.demo.utils.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val CARD_ID = "card_id"

internal class DivStorageBenchmark(
    divContext: Div2Context,
    val activity: Activity,
    private val viewController: DivStorageBenchmarkViewController
) {

    private val assetReader = JsonAssetReader(divContext)
    private val benchmarkMetrics = linkedMapOf<String, LongArray>()

    private val exceptionHandler = BenchmarkCoroutineExceptionHandler(viewController)
    private val mainContext = Dispatchers.Main + exceptionHandler
    private val backgroundContext = newSingleThreadContext("BenchmarkThread")

    fun run(assetNames: Array<String>) {
        activity.coroutineScope.launch(mainContext) {
            val jsonFiles = readJsonFiles(assetNames)
            putAssetsToDivStorage(jsonFiles)
            loadAssetFromDivStorage("${CARD_ID}0")
            benchmarkMetrics.clear()
            finish()
        }
    }

    private val createStorageComponent get() = DivStorageComponent.create(
        context = activity,
        errorLogger = { throw(it) },
        histogramReporter = Container.histogramReporterDelegate,
        histogramNameProvider = DemoAppHistogramNameProvider(),
    )

    private suspend fun putAssetsToDivStorage(jsonFiles: List<JSONObject>) {
        viewController.showMessage("Put cards to DivStorage…")
        val repository = createStorageComponent.repository
        return withContext(backgroundContext) {
            var assertCounter = 0
            jsonFiles.forEach {
                val card = it.getJSONObject("card")
                val templates = it.getJSONObject("templates")
                repository.put(DivDataRepository.Payload(divs = listOf(
                    RawDataAndMetadata(CARD_ID + assertCounter, card)), templatesFromData(templates)))
                assertCounter++
            }
        }
    }

    private suspend fun loadAssetFromDivStorage(cardId: String) {
        viewController.showMessage("Load card from DivStorage…")
        val repository = createStorageComponent.repository
        return withContext(backgroundContext) {
            repository.get(listOf(cardId))
        }
    }

    private fun templatesFromData(templates: JSONObject): Map<String, JSONObject> = run {
        val results = mutableMapOf<String, JSONObject>()
        templates.forEach { templateId, json: JSONObject ->
            results[templateId] = json
        }
        results
    }

    private suspend fun readJsonFiles(assetNames: Array<String>): List<JSONObject> {
        viewController.showMessage("Reading JSON files…")
        return withContext(backgroundContext) {
            assetNames.map { name ->
                assetReader.readJson(name)
            }
        }
    }

    private fun finish() {
        viewController.showMessage("Finished")
    }
}