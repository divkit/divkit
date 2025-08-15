package com.yandex.android.net

import android.net.Uri

/**
 * Provides cookies for outgoing requests. Usually the implementation will wrap Android's CookieManager.
 */
interface CookieStorage {

    fun getCookies(uri: Uri): String?

    /**
     * Accepts cookies into the storage.
     */
    fun processCookies(cookieValues: List<String>?, url: String)

    object NoOp : CookieStorage {
        override fun getCookies(uri: Uri): String? = null
        override fun processCookies(cookieValues: List<String>?, url: String) = Unit
    }
}
