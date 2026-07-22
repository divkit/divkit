package com.yandex.divkit.demo.div

import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.divkit.demo.utils.DownloadList

class DemoDivImageLoaderWrapper(private val loader: DivImageLoader) : DivImageLoader {

    private val targets = DownloadList<DivImageDownloadCallback>()

    override fun hasSvgSupport() = loader.hasSvgSupport()

    override fun needLimitBitmapSize(): Boolean = loader.needLimitBitmapSize()

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        targets.add(callback)
        val loadReference = loader.loadImage(imageUrl, CallbackWrapper(callback))
        return LoadReference {
            loadReference.cancel()
            targets.remove(callback)
        }
    }

    override fun loadAnimatedImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        targets.add(callback)
        val loadReference = loader.loadAnimatedImage(imageUrl, CallbackWrapper(callback))
        return LoadReference {
            loadReference.cancel()
            targets.remove(callback)
        }
    }

    val isIdle get() = targets.size == 0

    fun resetIdle() {
        targets.clean()
    }

    private inner class CallbackWrapper(private val callback: DivImageDownloadCallback) : DivImageDownloadCallback() {

        override fun onSuccess(cachedImage: DivCachedImage) {
            targets.remove(callback)
            callback.onSuccess(cachedImage)
        }

        override fun onError(e: Throwable?) {
            targets.remove(callback)
            callback.onError(e)
        }
    }
}
