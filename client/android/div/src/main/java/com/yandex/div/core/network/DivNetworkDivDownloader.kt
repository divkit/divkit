package com.yandex.div.core.network

import com.yandex.div.core.downloader.DivDownloader
import com.yandex.div.core.downloader.DivPatchDownloadCallback
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivPatch
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/** Downloads patches through an explicitly provided [DivNetworkClient]. */
internal class DivNetworkDivDownloader @JvmOverloads internal constructor(
    private val networkClient: DivNetworkClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val histogramReporter: DivParsingHistogramReporter = DivParsingHistogramReporter.DEFAULT,
) : DivDownloader {
    override fun downloadPatch(
        divView: Div2View,
        downloadUrl: String,
        callback: DivPatchDownloadCallback,
    ): LoadReference {
        val client = networkClient
        // DivDownloadActionHandler attaches the returned reference to Div2View, so cancelling
        // the view's loads also cancels this request without coupling the downloader to a view.
        val job = CoroutineScope(dispatcher).launch {
            runCatching {
                client.execute(DivNetworkRequest.Builder(downloadUrl).build()).use { response ->
                    if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
                    val json = JSONObject(response.body?.readBytes()?.decodeToString()
                        ?: throw IOException("Empty response body"))
                    val environment = DivParsingEnvironment(
                        ParsingErrorLogger { error -> divView.logError(error) }
                    )
                    json.optJSONObject("templates")?.let { templates ->
                        histogramReporter.measureTemplatesParsing(templates, null) {
                            environment.parseTemplates(templates)
                        }
                    }
                    DivPatch(environment, json.getJSONObject("patch"))
                }
            }.onSuccess {
                withContext(Dispatchers.Main) { callback.onSuccess(it) }
            }.onFailure {
                if (it is CancellationException) throw it
                withContext(Dispatchers.Main) {
                    divView.logError(it)
                    callback.onFail(it)
                }
            }
        }
        return LoadReference {
            job.cancel("Cancel patch download")
        }
    }
}
