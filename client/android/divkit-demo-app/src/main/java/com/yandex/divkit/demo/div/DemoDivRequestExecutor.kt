package com.yandex.divkit.demo.div

import com.yandex.div.core.DivRequestExecutor
import com.yandex.div.core.images.LoadReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class DemoDivRequestExecutor(private val okHttpClient: OkHttpClient) : DivRequestExecutor {

    private val scope = MainScope()

    override fun execute(request: DivRequestExecutor.Request, callback: DivRequestExecutor.Callback?): LoadReference {
        val job = scope.launch(Dispatchers.Main) {
            runCatching {
                withContext(Dispatchers.IO) {
                    okHttpClient.newCall(request.toHttpRequest()).execute()
                }
            }.getOrNull() ?: run {
                callback?.onFail()
                return@launch
            }
            callback?.onSuccess()
        }
        return LoadReference { job.cancel("Cancel submit action") }
    }

    private fun DivRequestExecutor.Request.toHttpRequest(): Request {
        return Request.Builder()
            .url(url.toString())
            .method(method, body.toRequestBody())
            .apply {
                headers?.forEach { addHeader(it.name, it.value) }
            }
            .build()
    }
}
