package com.yandex.android.beacon

import android.content.Context
import android.net.Uri
import com.yandex.android.net.CookieStorage
import com.yandex.div.core.annotations.PublicApi
import org.json.JSONObject

/**
 * A service similar to W3C's sendBeacon function from web-page world. It accepts requests with
 * no payloads and zero byte responses and delivers them no matter how many retries it will take.
 */
@PublicApi
class SendBeaconManager(
    context: Context,
    configuration: SendBeaconConfiguration
) {
    private val sendBeaconWorker = SendBeaconWorkerImpl(
        context,
        configuration
    )

    @JvmOverloads
    fun addUrl(url: Uri, headers: Map<String, String> = emptyMap(), payload: JSONObject? = null) {
        sendBeaconWorker.add(url, headers, payload, TRY_IMMEDIATELY)
    }

    fun addNonPersistentUrl(url: Uri, cookieStorage: CookieStorage, payload: JSONObject? = null) {
        sendBeaconWorker.addNonPersistentUrl(url, emptyMap(), cookieStorage, payload, TRY_IMMEDIATELY)
    }

    fun onStart(callback: SendBeaconWorker.Callback): Boolean {
        return sendBeaconWorker.onStart(callback)
    }

    fun onStop(): Boolean {
        return sendBeaconWorker.onStop()
    }

    private companion object {
        private const val TRY_IMMEDIATELY = true
    }
}
