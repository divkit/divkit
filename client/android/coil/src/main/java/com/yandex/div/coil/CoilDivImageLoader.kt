package com.yandex.div.coil

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.widget.ImageView
import coil3.EventListener
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.asDrawable
import coil3.decode.DataSource
import coil3.decode.Decoder
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.load
import coil3.network.cachecontrol.CacheControlCacheStrategy
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.svg.SvgDecoder
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import okhttp3.OkHttpClient

class CoilDivImageLoader private constructor(
    private val context: Context,
    private val okHttpClientFactory: () -> OkHttpClient
) : DivImageLoader {

    constructor(context: Context) : this(context, ::OkHttpClient)

    @Suppress("unused")
    constructor(
        context: Context,
        okHttpClient: OkHttpClient
    ) : this(context, { okHttpClient.newBuilder().build() })

    constructor(
        context: Context,
        okHttpClientBuilder: OkHttpClient.Builder
    ) : this(context, { okHttpClientBuilder.build() })

    @OptIn(ExperimentalCoilApi::class)
    private val imageLoader = ImageLoader.Builder(context)
        .components {
            add(
                OkHttpNetworkFetcherFactory(
                    callFactory = okHttpClientFactory,
                    cacheStrategy = { CacheControlCacheStrategy() }
                )
            )
            add(SvgDecoder.Factory())
            add(gifDecoder())
        }
        .build()

    private fun gifDecoder(): Decoder.Factory {
        return if (SDK_INT >= Build.VERSION_CODES.P) {
            AnimatedImageDecoder.Factory()
        } else {
            GifDecoder.Factory()
        }
    }

    @Deprecated("Is unused in DivKit, will be removed in future")
    override fun hasSvgSupport() = true

    @Deprecated("Is unused in DivKit, will be removed in future")
    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val imageUri = Uri.parse(imageUrl)

        val result = imageView.load(imageUri, imageLoader)

        return LoadReference {
            result.dispose()
        }
    }

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val imageUri = Uri.parse(imageUrl)

        val request = ImageRequest.Builder(context)
            .data(imageUri)
            .allowHardware(false)
            .listener(BitmapRequestListener(context, callback, imageUri))
            .build()

        val result = imageLoader.enqueue(request)

        return LoadReference {
            result.dispose()
        }
    }

    override fun loadImageBytes(
        imageUrl: String,
        callback: DivImageDownloadCallback
    ): LoadReference {
        val imageUri = Uri.parse(imageUrl)

        val request = ImageRequest.Builder(context)
            .data(imageUri)
            .allowHardware(false)
            .listener(GifRequestListener(context, callback))
            .build()

        val result = imageLoader.enqueue(request)

        return LoadReference {
            result.dispose()
        }
    }

    private class BitmapRequestListener(
        private val context: Context,
        private val callback: DivImageDownloadCallback,
        private val imageUri: Uri,
    ): EventListener() {
        override fun onSuccess(request: ImageRequest, result: SuccessResult) {
            val bitmapDrawable = result.image.asDrawable(context.resources) as BitmapDrawable
            callback.onSuccess(
                CachedBitmap(
                    bitmapDrawable.bitmap,
                    imageUri,
                    result.dataSource.toBitmapSource()
                )
            )
        }

        override fun onError(request: ImageRequest, result: ErrorResult) {
            callback.onError()
        }
    }

    private class GifRequestListener(
        private val context: Context,
        private val callback: DivImageDownloadCallback,
    ): EventListener() {
        override fun onSuccess(request: ImageRequest, result: SuccessResult) {
            callback.onSuccess(result.image.asDrawable(context.resources))
        }

        override fun onError(request: ImageRequest, result: ErrorResult) {
            callback.onError()
        }
    }
}

private fun DataSource.toBitmapSource(): BitmapSource {
    return when (this) {
        DataSource.MEMORY -> BitmapSource.MEMORY
        DataSource.DISK -> BitmapSource.DISK
        DataSource.NETWORK -> BitmapSource.NETWORK
        DataSource.MEMORY_CACHE -> BitmapSource.MEMORY
    }
}
