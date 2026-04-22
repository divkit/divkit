package com.yandex.div.svg

import android.content.Context
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoadError.Companion.toDivImageLoadError
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@InternalApi
public class SvgDivImageLoader(context: Context) : DivImageLoader {
    private val context = context.applicationContext
    private val httpClient = OkHttpClient.Builder().build()
    private val coroutineScope = MainScope()
    private val svgCacheManager = SvgCacheManager()

    override fun hasSvgSupport(): Boolean = true

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val cachedDrawable = svgCacheManager.get(imageUrl)
        if (cachedDrawable != null) {
            callback.onSuccess(DivCachedImage.Drawable(cachedDrawable, BitmapSource.MEMORY))
            return LoadReference { }
        }

        val call = createCallOrNull(imageUrl)

        coroutineScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val bytes = call?.let { downloadImage(it) } ?: getImageData(imageUrl)
                    SvgDecoder.decode(bytes.inputStream())
                }
            }.onSuccess {
                svgCacheManager.set(imageUrl, it)
                val image = DivCachedImage.Drawable(it, BitmapSource.NETWORK)
                callback.onSuccess(image)
            }.onFailure {
                if (it is CancellationException) throw it
                callback.onError(it.toDivImageLoadError(imageUrl))
            }
        }

        return LoadReference {
            call?.cancel()
        }
    }

    private fun createCallOrNull(imageUrl: String) : Call? {
        if (!(imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
            return null
        }
        val request = Request.Builder().url(imageUrl).build()
        return httpClient.newCall(request)
    }

    private fun downloadImage(call: Call): ByteArray {
        val response = call.execute()
        if (!response.isSuccessful) throw IOException("Server response code ${response.code}")
        val body = response.body ?: throw IOException("No response body received")
        return body.bytes()
    }

    private fun getImageData(imageUrl: String): ByteArray {
        val stream = try {
            val assetPath = imageUrl.removePrefix("file:///android_asset/")
            context.assets.open(assetPath)
        } catch (e: IOException) {
            throw IOException("File not found", e)
        }
        stream.use {
            return it.readBytes()
        }
    }
}
