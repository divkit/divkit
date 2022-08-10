package com.yandex.android.beacon

import android.net.Uri
import com.yandex.android.net.CookieStorage
import org.json.JSONObject

abstract class BeaconItem internal constructor(
    val url: Uri,
    val headers: Map<String, String>,
    val payload: JSONObject?,
    val addTimestamp: Long
) {

    abstract val cookieStorage: CookieStorage?

    abstract fun asPersistent(): Persistent?

    override fun toString(): String {
        return "BeaconItem{url=$url, headers=$headers, addTimestamp=$addTimestamp"
    }

    class NonPersistent(
        url: Uri,
        headers: Map<String, String>,
        payload: JSONObject?,
        addTimestamp: Long,
        override val cookieStorage: CookieStorage
    ) : BeaconItem(url, headers, payload, addTimestamp) {

        override fun asPersistent(): Persistent? = null
    }

    class Persistent(
        url: Uri,
        headers: Map<String, String>,
        payload: JSONObject?,
        addTimestamp: Long,
        val rowId: Long
    ) : BeaconItem(url, headers, payload, addTimestamp) {

        override val cookieStorage: CookieStorage? = null

        override fun asPersistent(): Persistent = this
    }
}
