package com.yandex.divkit.demo.utils

import com.yandex.div.network.ktor.KtorNetworkClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom
import io.ktor.utils.io.InternalAPI
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient

private val REDIRECT_CODES = setOf(300, 301, 302, 303, 307, 308)
private val METHOD_PRESERVING_CODES = setOf(307, 308)

private val OkHttpRedirects = createClientPlugin("OkHttpRedirects") {
    on(Send) { request ->
        var call = proceed(request)
        var redirect = call.redirectRequest(request)
        while (redirect != null) {
            call = proceed(redirect)
            redirect = call.redirectRequest(redirect)
        }
        call
    }
}

internal fun createKtorNetworkClient(okHttpClient: OkHttpClient): KtorNetworkClient {
    return KtorNetworkClient(OkHttp) {
        followRedirects = false
        engine { preconfigured = okHttpClient }
        install(OkHttpRedirects)
    }
}

@OptIn(InternalAPI::class)
private fun HttpClientCall.redirectRequest(previous: HttpRequestBuilder): HttpRequestBuilder? {
    val status = response.status.value
    val location = response.headers[HttpHeaders.Location]
    if (status !in REDIRECT_CODES || location == null) return null
    val source = previous.url.buildString().toHttpUrlOrNull() ?: return null
    val target = source.resolve(location) ?: return null
    return HttpRequestBuilder().apply {
        takeFromWithExecutionContext(previous)
        url.parameters.clear()
        url.takeFrom(target.toString())
        val keepsMethod = status in METHOD_PRESERVING_CODES ||
            previous.method == HttpMethod.Get ||
            previous.method == HttpMethod.Head
        if (!keepsMethod) {
            method = HttpMethod.Get
            setBody(EmptyContent)
            headers.remove(HttpHeaders.ContentType)
        }
        if (!target.hasSameOrigin(source)) {
            headers.remove(HttpHeaders.Authorization)
        }
    }
}

private fun HttpUrl.hasSameOrigin(other: HttpUrl): Boolean {
    return scheme == other.scheme && host == other.host && port == other.port
}
