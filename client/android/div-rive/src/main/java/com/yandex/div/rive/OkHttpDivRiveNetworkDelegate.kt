package com.yandex.div.rive

import okhttp3.Call
import okhttp3.Request
import java.io.IOException

class OkHttpDivRiveNetworkDelegate(private val httpCallFactory: Call.Factory) : DivRiveNetworkDelegate {
    override fun load(url: String): ByteArray {
        return Request.Builder().url(url).build()
            .let { httpCallFactory.newCall(it).execute() }
            .let { response ->
                if (response.isSuccessful) {
                    response.body?.bytes() ?: throw IOException("Unexpected body $response")
                } else {
                    throw IOException("Unexpected code $response")
                }
            }
    }
}
