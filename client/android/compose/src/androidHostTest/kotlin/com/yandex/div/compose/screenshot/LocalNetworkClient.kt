package com.yandex.div.compose.screenshot

import android.content.Context
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import com.yandex.div.test.network.LocalNetworkResponses

internal class LocalNetworkClient(context: Context) : DivNetworkClient {

    private val responses = LocalNetworkResponses(context.assets)

    override suspend fun execute(request: DivNetworkRequest): DivNetworkResponse {
        val response = responses.respond(request.method, request.url)
        return object : DivNetworkResponse {
            override val url = request.url
            override val code = response.code
            override val contentType = "application/json"
            override val body = DivNetworkResponseBody.fromBytes(response.body)

            override fun headers(name: String): List<String> = emptyList()
            override fun close() = body.close()
        }
    }
}
