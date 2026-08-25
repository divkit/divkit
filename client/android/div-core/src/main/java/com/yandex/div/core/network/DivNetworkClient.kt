package com.yandex.div.core.network

/** Host-provided network stack used by built-in DivKit network consumers. */
public fun interface DivNetworkClient {
    /** Executes [request]. Cancellation of the calling coroutine must cancel the underlying request. */
    public suspend fun execute(request: DivNetworkRequest): DivNetworkResponse
}
