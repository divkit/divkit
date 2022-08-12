package com.yandex.android.beacon

import android.net.Uri
import androidx.annotation.WorkerThread
import java.io.IOException

internal class TestSendBeaconRequestExecutor : SendBeaconRequestExecutor {

    val badDomains = mutableSetOf<String>()
    val sentOut = mutableListOf<Uri>()

    var ioException: IOException? = null
    var postSendHook: Runnable? = null

    @WorkerThread
    @Throws(IOException::class)
    override fun execute(request: SendBeaconRequest): SendBeaconResponse {
        ioException?.let { throw it }

        if (badDomains.contains(request.url.host)) {
            throw IOException("Bad domain")
        }

        sentOut.add(request.url)
        if (postSendHook != null) {
            val hook = postSendHook
            postSendHook = null
            hook?.run()
        }

        return OkSendBeaconResponse()
    }
}
