package com.yandex.div.network

import com.yandex.div.core.DivRequestExecutor
import com.yandex.div.core.images.LoadReference
import com.yandex.div.json.LoadingErrorLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * The [executeRequest] function — network request handler, pluggable for different HTTP clients.
 */
class DefaultDivRequestExecutor(
    private val executeRequest: suspend (DivRequestExecutor.Request) -> Result<Unit>,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    private val loadingErrorLogger: LoadingErrorLogger = LoadingErrorLogger.LOG,
) : DivRequestExecutor {

    @JvmOverloads
    constructor(
        client: OkHttpClient,
        scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        loadingErrorLogger: LoadingErrorLogger = LoadingErrorLogger.LOG,
    ) : this(
        executeRequest = { request -> client.executeRequest(request) },
        scope = scope,
        loadingErrorLogger = loadingErrorLogger,
    )

    override fun execute(
        request: DivRequestExecutor.Request,
        callback: DivRequestExecutor.Callback?
    ): LoadReference {
        val job = scope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) {
                    executeRequest(request)
                }
                result
                    .onSuccess { callback?.onSuccess() }
                    .onFailure { err ->
                        logFailAndNotify(request, err, callback)
                    }
            } catch (e: Exception) {
                logFailAndNotify(request, e, callback)
            }
        }

        return LoadReference { job.cancel("Cancel submit action") }
    }

    private fun logFailAndNotify(
        request: DivRequestExecutor.Request,
        cause: Throwable,
        callback: DivRequestExecutor.Callback?
    ) {
        loadingErrorLogger.logError(
            IllegalStateException(
                "Error while executing request [${request.method} ${request.url}]", cause
            )
        )
        callback?.onFail()
    }
}

private suspend fun OkHttpClient.executeRequest(
    request: DivRequestExecutor.Request
): Result<Unit> {
    val httpRequest = Request.Builder()
        .url(request.url.toString())
        .method(request.method, request.body.toRequestBody())
        .apply { request.headers?.forEach { addHeader(it.name, it.value) } }
        .build()

    newCall(httpRequest).execute().use { resp ->
        return if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(
                IOException("HTTP request failed ${resp.code} ${resp.message} [${request.method} ${request.url}]")
            )
        }
    }
}
