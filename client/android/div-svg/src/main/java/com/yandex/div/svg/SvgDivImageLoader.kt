package com.yandex.div.svg

import android.widget.ImageView
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

internal class SvgDivImageLoader : DivImageLoader {
    private val httpClient = OkHttpClient.Builder().build()
    private val coroutineScope = MainScope()
    private val svgDecoder = SvgDecoder()
    private val svgCacheManager = SvgCacheManager()

    override fun hasSvgSupport() = true

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val call = createCall(imageUrl)

        val pictureDrawable = svgCacheManager.get(imageUrl)
        if (pictureDrawable != null) {
            callback.onSuccess(pictureDrawable)
            return LoadReference { }
        }

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val bytes = runCatching {
                    call.execute().body?.bytes()
                }.getOrNull() ?: return@withContext null
                val drawable = svgDecoder.decode(bytes.inputStream()) ?: return@withContext null
                svgCacheManager.set(imageUrl, drawable)

                drawable
            }?.let {
                callback.onSuccess(it)
            } ?: callback.onError()
        }

        return LoadReference {
            call.cancel()
        }
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val call = createCall(imageUrl)

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val bytes = runCatching {
                    call.execute().body?.bytes()
                }.getOrNull() ?: return@withContext null
                svgDecoder.decode(bytes.inputStream())
            }?.let {
                imageView.setImageDrawable(it)
            }
        }

        return LoadReference {
            call.cancel()
        }
    }

    override fun loadImageBytes(
        imageUrl: String,
        callback: DivImageDownloadCallback
    ) = LoadReference {
        loadImage(imageUrl, callback)
    }

    private fun createCall(imageUrl: String) : Call {
        val request = Request.Builder().url(imageUrl).build()
        return httpClient.newCall(request)
    }
}
