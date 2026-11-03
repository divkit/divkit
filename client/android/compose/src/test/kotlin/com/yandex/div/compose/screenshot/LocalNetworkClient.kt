package com.yandex.div.compose.screenshot

import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody

internal class LocalNetworkClient : DivNetworkClient {

    override suspend fun execute(request: DivNetworkRequest): DivNetworkResponse {
        val url = request.url
        check(request.method == "GET") {
            "Unsupported test request: ${request.method} $url"
        }

        val fileName = url.substringAfterLast('/')
        val bytes = checkNotNull(javaClass.getResourceAsStream("/patches/$fileName")) {
            "No local response for $url"
        }.use { it.readBytes() }

        return object : DivNetworkResponse {
            override val url = url
            override val code = 200
            override val contentType = "application/json"
            override val body = DivNetworkResponseBody.fromBytes(bytes)

            override fun headers(name: String): List<String> = emptyList()
            override fun close() = body.close()
        }
    }
}
