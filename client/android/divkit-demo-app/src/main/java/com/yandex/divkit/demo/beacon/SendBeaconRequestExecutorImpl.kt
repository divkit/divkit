package com.yandex.divkit.demo.beacon

import com.yandex.android.beacon.SendBeaconRequest
import com.yandex.android.beacon.SendBeaconRequestExecutor
import com.yandex.android.beacon.SendBeaconResponse
import com.yandex.div.core.util.KLog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

internal class SendBeaconRequestExecutorImpl(
    private val httpClient: OkHttpClient
) : SendBeaconRequestExecutor {

    override fun execute(request: SendBeaconRequest): SendBeaconResponse {
        val url = request.url.toString()

        val requestBuilder: Request.Builder = Request.Builder().url(url)
        request.headers.forEach { (name, value) ->
            requestBuilder.addHeader(name, value)
        }
        request.payload?.let {
            requestBuilder.post(RequestBody.create("application/json".toMediaTypeOrNull(), it.toString()))
        }

        KLog.i(TAG) { "Performing sendBeacon request to $url" }
        val call = httpClient.newCall(requestBuilder.build())
        val response = call.execute()
        return SendBeaconResponseImpl(response.code)
    }

    private class SendBeaconResponseImpl(override val responseCode: Int) : SendBeaconResponse {

        override fun isValid() = true
    }

    private companion object {

        private const val TAG = "SendBeaconRequestExecutorImpl"
    }
}
