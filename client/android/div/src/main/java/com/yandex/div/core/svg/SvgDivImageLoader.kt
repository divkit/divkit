package com.yandex.div.core.svg

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
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

class SvgDivImageLoader : DivImageLoader {
    private val httpClient = OkHttpClient.Builder().build()
    private val coroutineScope = MainScope()
    private val svgDecoder = SvgDecoder()
    private val svgCacheManager = SvgCacheManager()
    override fun doesSupportSvg(): Boolean {
        return true
    }

    private fun createCall(imageUrl: String) : Call {
        val request = Request.Builder().url(imageUrl).build()
        return httpClient.newCall(request)
    }

    override fun loadImage(
        imageUrl: String,
        shouldBeRasterized: Boolean,
        callback: DivImageDownloadCallback
    ): LoadReference {
        val call = createCall(imageUrl)

        val pictureDrawable = svgCacheManager.get(imageUrl)
        if (pictureDrawable != null) {
            // If it's not possible to handle the image as a vector, convert it to bitmap
            if (shouldBeRasterized) {
                val bitmap = pictureDrawable!!.toCachedBitmap(imageUrl, BitmapSource.MEMORY)
                callback.onSuccess(bitmap)
            } else {
                callback.onSuccess(pictureDrawable!!)
            }
            return LoadReference { }
        }

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val response = runCatching {
                    call.execute()
                }.getOrNull() ?: return@withContext null
                val source = response.cacheResponse?.let { BitmapSource.MEMORY } ?: BitmapSource.NETWORK
                val bytes = response.body?.bytes() ?: return@withContext null
                val pictureDrawable = svgDecoder.decode(bytes.inputStream()) ?: return@withContext null
                svgCacheManager.set(imageUrl, pictureDrawable)

                if (shouldBeRasterized) pictureDrawable.toCachedBitmap(imageUrl, source, bytes) else pictureDrawable
            }?.let {
                if (it is PictureDrawable) callback.onSuccess(it) else callback.onSuccess(it as CachedBitmap)
            } ?: callback.onError()
        }

        return LoadReference {
            call.cancel()
        }
    }

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        // By default, we assume svg images can be represented as vectors.
        return loadImage(imageUrl, false, callback)
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val call = createCall(imageUrl)

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val response = runCatching {
                    call.execute()
                }.getOrNull() ?: return@withContext null
                val bytes = response.body?.bytes() ?: return@withContext null
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
    ): LoadReference {
        return LoadReference {
            loadImage(imageUrl, callback)
        }
    }

    private fun PictureDrawable.toCachedBitmap(imageUrl: String, source: BitmapSource, bytes: ByteArray? = null) : CachedBitmap {
        val bitmap = toBitmap(intrinsicWidth, intrinsicHeight)
        return CachedBitmap(bitmap, bytes, Uri.parse(imageUrl), source)
    }
}