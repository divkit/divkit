package com.yandex.div.compose.utils

import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import kotlinx.coroutines.CompletableDeferred

class TestNetworkClient : DivNetworkClient {
    private val _requests = mutableListOf<DivNetworkRequest>()
    val requests: List<DivNetworkRequest> = _requests
    val response = CompletableDeferred<DivNetworkResponse>()

    override suspend fun execute(request: DivNetworkRequest): DivNetworkResponse {
        _requests.add(request)
        return response.await()
    }
}
