package com.yandex.div.compose.images

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSource

internal object ImageHttpClientFactory {

    private val bodyCloseScope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))

    fun create(preconfigured: OkHttpClient? = null): HttpClient {
        return HttpClient(OkHttp) {
            followRedirects = false
            engine {
                this.preconfigured = preconfigured
                config {
                    followRedirects(true)
                    followSslRedirects(true)
                    addInterceptor(BackgroundBodyCloseInterceptor)
                }
            }
        }
    }

    private object BackgroundBodyCloseInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            val body = response.body ?: return response
            return response.newBuilder().body(BackgroundClosingResponseBody(body)).build()
        }
    }

    private class BackgroundClosingResponseBody(
        private val delegate: ResponseBody,
    ) : ResponseBody() {
        override fun contentType(): MediaType? = delegate.contentType()

        override fun contentLength(): Long = delegate.contentLength()

        override fun source(): BufferedSource = delegate.source()

        override fun close() {
            bodyCloseScope.launch { delegate.close() }
        }
    }
}
