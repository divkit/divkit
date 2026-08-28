package com.yandex.div.svg

import android.content.Context
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoadError.Companion.toDivImageLoadError
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.readBytes
import com.yandex.div.network.OkHttpDivNetworkClient
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

@InternalApi
public class SvgDivImageLoader(
    context: Context,
    networkClient: DivNetworkClient? = null,
    private val coroutineScope: CoroutineScope = createCoroutineScope(),
) : DivImageLoader {
    private val context = context.applicationContext
    private val networkClient = networkClient ?: OkHttpDivNetworkClient(OkHttpClient.Builder().build())
    private val svgCacheManager = SvgCacheManager()

    override fun hasSvgSupport(): Boolean = true

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val cachedDrawable = svgCacheManager.get(imageUrl)
        if (cachedDrawable != null) {
            callback.onSuccess(DivCachedImage.Drawable(cachedDrawable, BitmapSource.MEMORY))
            return LoadReference { }
        }

        val job = coroutineScope.launch {
            runCatching {
                val bytes = if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    downloadImage(imageUrl)
                } else {
                    getImageData(imageUrl)
                }
                SvgDecoder.decode(bytes.inputStream())
            }.onSuccess {
                svgCacheManager.set(imageUrl, it)
                callback.onSuccess(DivCachedImage.Drawable(it, BitmapSource.NETWORK))
            }.onFailure {
                if (it is CancellationException) throw it
                callback.onError(it.toDivImageLoadError(imageUrl))
            }
        }

        return LoadReference { job.cancel() }
    }

    private suspend fun downloadImage(imageUrl: String): ByteArray {
        return networkClient.execute(DivNetworkRequest.Builder(imageUrl).build()).use { response ->
            if (!response.isSuccessful) throw IOException("Server response code ${response.code}")
            response.body?.readBytes() ?: throw IOException("No response body received")
        }
    }

    private fun getImageData(imageUrl: String): ByteArray {
        val stream = try {
            context.assets.open(imageUrl.removePrefix("file:///android_asset/"))
        } catch (e: IOException) {
            throw IOException("File not found", e)
        }
        return stream.use { it.readBytes() }
    }
}

private fun createCoroutineScope(): CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.IO)
