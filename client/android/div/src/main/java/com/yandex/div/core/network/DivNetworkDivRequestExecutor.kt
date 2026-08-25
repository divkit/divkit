package com.yandex.div.core.network

import com.yandex.div.core.DivRequestExecutor
import com.yandex.div.core.images.LoadReference
import com.yandex.div.json.LoadingErrorLogger
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class DivNetworkDivRequestExecutor(
    private val networkClient: DivNetworkClient,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    private val loadingErrorLogger: LoadingErrorLogger = LoadingErrorLogger.LOG,
) : DivRequestExecutor {
    override fun execute(
        request: DivRequestExecutor.Request,
        callback: DivRequestExecutor.Callback?,
    ): LoadReference {
        val job = scope.launch {
            runCatching {
                val networkRequest = DivNetworkRequest.Builder(request.url.toString())
                    .method(request.method)
                    .body(request.body.takeUnless {
                        request.method.equals("GET", true) || request.method.equals("HEAD", true)
                    }?.toByteArray())
                    .apply { request.headers?.forEach { addHeader(it.name, it.value) } }
                    .build()
                networkClient.execute(networkRequest).use {
                    if (!it.isSuccessful) throw IOException("HTTP ${it.code}")
                }
            }.onSuccess {
                withContext(Dispatchers.Main) { callback?.onSuccess() }
            }.onFailure { cause ->
                if (cause is CancellationException) throw cause
                val error = IllegalStateException(
                    "Error while executing request [${request.method} ${request.url}]",
                    cause,
                )
                loadingErrorLogger.logError(error)
                withContext(Dispatchers.Main) { callback?.onFail() }
            }
        }
        return LoadReference {
            job.cancel("Cancel submit action")
        }
    }
}

