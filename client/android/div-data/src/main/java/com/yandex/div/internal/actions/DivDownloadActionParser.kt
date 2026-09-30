package com.yandex.div.internal.actions

import android.net.Uri
import com.yandex.div.core.annotations.InternalApi

@InternalApi
object DivDownloadActionParser {
    fun matches(uri: Uri): Boolean = uri.authority == "download"

    fun parseUrl(uri: Uri): String? = uri.getQueryParameter("url")
}
