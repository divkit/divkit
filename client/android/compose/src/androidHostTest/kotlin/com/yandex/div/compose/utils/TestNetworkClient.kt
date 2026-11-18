package com.yandex.div.compose.utils

import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import kotlinx.coroutines.CompletableDeferred
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class TestNetworkClient : DivNetworkClient {
    private val _requests = mutableListOf<DivNetworkRequest>()
    val requests: List<DivNetworkRequest> = _requests
    val response = CompletableDeferred<DivNetworkResponse>()

    override suspend fun execute(request: DivNetworkRequest): DivNetworkResponse {
        _requests.add(request)
        return response.await()
    }

    fun completeResponse(
        isSuccessful: Boolean = true,
        body: String? = null,
    ) {
        val responseBody = body?.let {
            DivNetworkResponseBody.fromBytes(it.toByteArray())
        }
        response.complete(
            mock<DivNetworkResponse> {
                on { this.isSuccessful } doReturn isSuccessful
                on { this.body } doReturn responseBody
            }
        )
    }
}
