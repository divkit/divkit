package com.yandex.div.coil

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.widget.ImageView
import coil.EventListener
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference

class CoilDivImageLoader(
    private val context: Context
) : DivImageLoader {

    private val imageLoader = ImageLoader.Builder(context)
        .components {
            add(SvgDecoder.Factory())

            if (SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

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
            .listener(BitmapRequestListener(callback, imageUri))
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
            .listener(GifRequestListener(callback, imageUri))
            .build()

        val result = imageLoader.enqueue(request)

        return LoadReference {
            result.dispose()
        }
    }

    private class BitmapRequestListener(
        private val callback: DivImageDownloadCallback,
        private val imageUri: Uri,
    ): EventListener {
        override fun onSuccess(request: ImageRequest, result: SuccessResult) {
            callback.onSuccess(CachedBitmap((result.drawable as BitmapDrawable).bitmap, imageUri, result.dataSource.toBitmapSource()))
        }

        override fun onError(request: ImageRequest, result: ErrorResult) {
            callback.onError()
        }
    }

    private class GifRequestListener(
        private val callback: DivImageDownloadCallback,
        private val imageUri: Uri,
    ): EventListener {
        override fun onSuccess(request: ImageRequest, result: SuccessResult) {
            callback.onSuccess(result.drawable)
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
