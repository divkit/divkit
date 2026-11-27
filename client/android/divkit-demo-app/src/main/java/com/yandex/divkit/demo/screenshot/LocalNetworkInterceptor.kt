package com.yandex.divkit.demo.screenshot

import android.content.res.AssetManager
import com.yandex.div.test.network.LocalNetworkResponses
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

internal object LocalNetworkInterceptor : Interceptor {

    private var responses: LocalNetworkResponses? = null

    fun start(assets: AssetManager) {
        responses = LocalNetworkResponses(assets)
    }

    fun stop() {
        val finished = responses
        responses = null
        finished?.checkAllAnswered()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val local = responses?.respond(request.method, request.url.toString())
            ?: return chain.proceed(request)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(local.code)
            .message("")
            .body(local.body.toResponseBody())
            .build()
    }
}
