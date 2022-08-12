package com.yandex.android.beacon

import android.net.Uri
import com.yandex.android.net.CookieStorage
import org.json.JSONObject

/**
 * Send beacon network request primitive.
 */
data class SendBeaconRequest(
        val url: Uri,
        val headers: Map<String, String>,
        val payload: JSONObject?,
        val cookieStorage: CookieStorage?
) {

    companion object {

        @JvmStatic
        fun from(beaconItem: BeaconItem): SendBeaconRequest {
            return SendBeaconRequest(
                beaconItem.url,
                beaconItem.headers,
                beaconItem.payload,
                beaconItem.cookieStorage
            )
        }
    }
}
