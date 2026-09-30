package com.yandex.divkit.demo.div

import android.net.Uri
import com.yandex.div.compose.patch.DivPatchDownloader
import com.yandex.div2.DivPatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ComposePatchDownloader(private val client: OkHttpClient) : DivPatchDownloader {
    override suspend fun downloadPatch(url: Uri): DivPatch = withContext(Dispatchers.IO) {
        JSONObject(downloadJson(url)).asDivPatchWithTemplates()
    }

    private suspend fun downloadJson(url: Uri): String = suspendCancellableCoroutine { continuation ->
        val call = client.newCall(Request.Builder().url(url.toString()).build())
        continuation.invokeOnCancellation { call.cancel() }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWith(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                continuation.resumeWith(runCatching {
                    response.use {
                        if (!it.isSuccessful) throw IOException("Patch request failed: HTTP ${it.code}")
                        it.body?.string() ?: throw IOException("Empty patch response")
                    }
                })
            }
        })
    }
}
