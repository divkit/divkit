package com.yandex.divkit.demo.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun OkHttpClient.loadText(uri: String): String? = withContext(Dispatchers.IO) {
    val request = Request.Builder().url(uri).build()
    return@withContext newCall(request).execute().body?.string()
}
