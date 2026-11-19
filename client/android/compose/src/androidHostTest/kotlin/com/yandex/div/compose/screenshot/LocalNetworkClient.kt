package com.yandex.div.compose.screenshot

import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody

internal class LocalNetworkClient : DivNetworkClient {

    override suspend fun execute(request: DivNetworkRequest): DivNetworkResponse {
        val url = request.url
        if (url.startsWith(HTTPBIN_ROOT)) {
            return response(url, submitResponseCode(request), SUBMIT_RESPONSE)
        }

        check(request.method == "GET") {
            "Unsupported test request: ${request.method} $url"
        }

        check(url.startsWith(REMOTE_ROOT)) {
            "No local response for $url: only $REMOTE_ROOT is served locally"
        }

        val resource = "/patches/${url.removePrefix(REMOTE_ROOT)}"
        val bytes = checkNotNull(javaClass.getResourceAsStream(resource)) {
            "No local response for $url: missing resource $resource"
        }.use { it.readBytes() }

        return response(url, 200, bytes)
    }

    private fun submitResponseCode(request: DivNetworkRequest): Int {
        val url = request.url
        check(request.method == "POST") {
            "Unsupported test request: ${request.method} $url"
        }

        val path = url.removePrefix(HTTPBIN_ROOT)
        val code = when {
            path == "post" -> 200
            path.startsWith("status/") -> path.removePrefix("status/").toIntOrNull()
            else -> null
        }
        return checkNotNull(code) {
            "No local response for $url: only POST to ${HTTPBIN_ROOT}post " +
                "and ${HTTPBIN_ROOT}status/<code> is served locally"
        }
    }

    private fun response(url: String, code: Int, bytes: ByteArray) = object : DivNetworkResponse {
        override val url = url
        override val code = code
        override val contentType = "application/json"
        override val body = DivNetworkResponseBody.fromBytes(bytes)

        override fun headers(name: String): List<String> = emptyList()
        override fun close() = body.close()
    }

    private companion object {
        const val REMOTE_ROOT = "https://yastatic.net/s3/home/divkit/"
        const val HTTPBIN_ROOT = "https://httpbin.org/"
        val SUBMIT_RESPONSE = "{}".encodeToByteArray()
    }
}
