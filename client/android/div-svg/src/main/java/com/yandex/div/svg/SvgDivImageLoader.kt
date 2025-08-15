package com.yandex.div.svg

import android.content.Context
import android.widget.ImageView
import com.yandex.div.core.annotations.InternalApi
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

@InternalApi
public class SvgDivImageLoader(private val context: Context) : DivImageLoader {
    private val httpClient = OkHttpClient.Builder().build()
    private val coroutineScope = MainScope()
    private val svgDecoder = SvgDecoder()
    private val svgCacheManager = SvgCacheManager()

    override fun hasSvgSupport(): Boolean = true

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val call = createCallOrNull(imageUrl)

        val pictureDrawable = svgCacheManager.get(imageUrl)
        if (pictureDrawable != null) {
            callback.onSuccess(pictureDrawable)
            return LoadReference { }
        }

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val bytes = if (call == null) {
                    getImageData(imageUrl)
                } else {
                    downloadImage(call)
                } ?: return@withContext null

                val drawable = svgDecoder.decode(bytes.inputStream()) ?: return@withContext null
                svgCacheManager.set(imageUrl, drawable)

                drawable
            }?.let {
                callback.onSuccess(it)
            } ?: callback.onError()
        }

        return LoadReference {
            call?.cancel()
        }
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val call = createCallOrNull(imageUrl)

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val bytes = if (call == null) {
                    getImageData(imageUrl)
                } else {
                    downloadImage(call)
                } ?: return@withContext null

                svgDecoder.decode(bytes.inputStream())
            }?.let {
                imageView.setImageDrawable(it)
            }
        }

        return LoadReference {
            call?.cancel()
        }
    }

    override fun loadImageBytes(
        imageUrl: String,
        callback: DivImageDownloadCallback
    ): LoadReference = LoadReference {
        loadImage(imageUrl, callback)
    }

    private fun createCallOrNull(imageUrl: String) : Call? {
        if (!(imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
            return null
        }
        val request = Request.Builder().url(imageUrl).build()
        return httpClient.newCall(request)
    }

    private fun downloadImage(call: Call): ByteArray? = runCatching {
        call.execute().body?.bytes()
    }.getOrNull()

    private fun getImageData(imageUrl: String): ByteArray? {
        val assetPath = imageUrl.removePrefix("file:///android_asset/")
        val stream = context.applicationContext?.assets?.open(assetPath) ?: return null
        stream.use {
            return it.readBytes()
        }
    }
}
