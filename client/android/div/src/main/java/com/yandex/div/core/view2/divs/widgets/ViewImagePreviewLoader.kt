package com.yandex.div.core.view2.divs.widgets

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.MainThread
import com.yandex.div.core.BaseImageDownloadCallback
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.internal.util.UiThreadHandler.Companion.executeOnMainThread

internal class ViewImagePreviewLoader(
    private val imageLoader: DivImageLoader,
) {
    private var currentRequest: Request? = null

    fun load(
        url: String,
        onLoaded: (DivCachedImage, Request) -> Unit,
        onError: (Throwable?, Request) -> Unit,
    ): Request {
        val request = Request(onLoaded, onError)
        request.load(url)
        return request
    }

    fun cancel() {
        executeOnMainThread {
            currentRequest?.cancel()
        }
    }

    inner class Request(
        private val onLoaded: (DivCachedImage, Request) -> Unit,
        private val onError: (Throwable?, Request) -> Unit,
    ) : BaseImageDownloadCallback(), LoadReference {
        private var loadReference: LoadReference? = null
        private var isCancelled = false

        @MainThread
        fun isCurrent(): Boolean = currentRequest === this

        fun load(url: String) {
            executeOnMainThread {
                if (isCancelled) {
                    return@executeOnMainThread
                }
                this@ViewImagePreviewLoader.cancel()
                currentRequest = this
                val reference = imageLoader.loadImage(url, this)
                if (isCurrent()) {
                    loadReference = reference
                } else {
                    // A synchronous callback may have cancelled or replaced this request.
                    reference.cancel()
                }
            }
        }

        override fun cancel() {
            executeOnMainThread {
                isCancelled = true
                if (isCurrent()) {
                    currentRequest = null
                }
                val reference = loadReference
                loadReference = null
                reference?.cancel()
            }
        }

        override fun onSuccess(cachedImage: DivCachedImage) {
            if (isCurrent()) {
                onLoaded(cachedImage, this)
            }
        }

        override fun onSuccess(bitmap: Bitmap, source: BitmapSource) {
            onSuccess(DivCachedImage.Bitmap(bitmap, source))
        }

        override fun onSuccess(drawable: Drawable, source: BitmapSource) {
            onSuccess(DivCachedImage.Drawable(drawable, source))
        }

        override fun onError(e: Throwable?) {
            if (isCurrent()) {
                onError.invoke(e, this)
            }
        }
    }
}
