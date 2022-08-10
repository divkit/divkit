package com.yandex.divkit.demo.div.urlopen

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.nio.charset.StandardCharsets

class UrlOpener {

    private val okHttpClient by lazy { OkHttpClient.Builder().build() }

    @Throws(IOException::class)
    suspend fun open(url: String): String {
        return withContext(Dispatchers.Default) {
            try {
                val responseBody = okHttpClient.newCall(Request.Builder().url(url).build()).execute().body
                responseBody?.source()?.readString(StandardCharsets.UTF_8) ?: throw IOException("Body is missing")
            } catch (e: Exception) {
                throw IOException(e)
            }
        }
    }
}
