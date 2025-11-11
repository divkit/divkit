package com.yandex.div.network

import com.yandex.div.core.downloader.DivDownloader
import com.yandex.div.core.downloader.DivPatchDownloadCallback
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.Div2View
import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.json.LoadingErrorLogger
import com.yandex.div.json.ParsingErrorLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * The [downloadJson] function defines how raw JSON is fetched from a URL,
 * allowing integration with different HTTP clients.
 */
class DefaultDivDownloader(
    private val downloadJson: suspend (url: String) -> Result<String>,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val histogramReporter: DivParsingHistogramReporter = DivParsingHistogramReporter.DEFAULT,
    private val parsingErrorLogger: ParsingErrorLogger = ParsingErrorLogger.LOG,
    private val loadingErrorLogger: LoadingErrorLogger = LoadingErrorLogger.LOG,
) : DivDownloader {

    @JvmOverloads
    constructor(
        client: OkHttpClient,
        scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        histogramReporter: DivParsingHistogramReporter = DivParsingHistogramReporter.DEFAULT,
        parsingErrorLogger: ParsingErrorLogger = ParsingErrorLogger.LOG,
        loadingErrorLogger: LoadingErrorLogger = LoadingErrorLogger.LOG
    ) : this(
        downloadJson = { url -> client.downloadJson(url) },
        scope = scope,
        histogramReporter = histogramReporter,
        parsingErrorLogger = parsingErrorLogger,
        loadingErrorLogger = loadingErrorLogger,
    )

    override fun downloadPatch(
        divView: Div2View,
        downloadUrl: String,
        callback: DivPatchDownloadCallback
    ): LoadReference {
        val job = scope.launch {
            val json: String = try {
                val result = downloadJson(downloadUrl)
                result.getOrElse { t ->
                    logFailAndNotify(
                        exception = RuntimeException(
                            "Failed to download patch JSON from $downloadUrl", t
                        ),
                        callback = callback,
                        divView = divView,
                    )
                    return@launch
                }
            } catch (e: Exception) {
                logFailAndNotify(
                    exception = RuntimeException("Failed to apply patch JSON from $downloadUrl", e),
                    callback = callback, divView = divView
                )
                return@launch
            }


            val errorsInsidePatch = mutableListOf<Throwable>()
            val patch = try {
                JSONObject(json).asDivPatchWithTemplates(histogramReporter) {
                    errorsInsidePatch.add(RuntimeException(
                        "Patch parsing non-critical error #${errorsInsidePatch.size + 1}", it))
                    parsingErrorLogger.logError(it)
                }
            } catch (e: JSONException) {
                val error = RuntimeException("Failed to parse patch JSON from $downloadUrl", e)
                divView.logError(error)
                parsingErrorLogger.logError(error)

                notifyMain {
                    callback.onFail()
                    errorsInsidePatch.forEach {
                        divView.logError(it)
                    }
                }
                return@launch
            }

            notifyMain {
                callback.onSuccess(patch)
                errorsInsidePatch.forEach {
                    divView.logError(it)
                }
            }
        }

        return LoadReference { job.cancel("cancel all downloads") }
    }

    private suspend fun notifyMain(action: suspend () -> Unit) {
        withContext(Dispatchers.Main) { action() }
    }

    private suspend fun logFailAndNotify(
        exception: Exception,
        callback: DivPatchDownloadCallback,
        divView: Div2View,
    ) {
        loadingErrorLogger.logError(exception)
        notifyMain {
            callback.onFail()
            divView.logError(exception)
        }
    }
}

private suspend fun OkHttpClient.downloadJson(uri: String): Result<String> {
    return withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(uri)
            .get()
            .build()

        newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) {
                return@use Result.failure(
                    IOException("HTTP ${resp.code} ${resp.message} for $uri")
                )
            }
            val body = resp.body?.string()
                ?: return@use Result.failure(
                    IllegalStateException("Empty response body for $uri")
                )
            Result.success(body)
        }
    }
}
