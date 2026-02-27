package com.yandex.divkit.benchmark.div

import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference

class DemoDivImageLoaderWrapper(private val loader: DivImageLoader) : DivImageLoader {

    private val targets = TargetList()

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

    private inner class TargetList {

        private val activeTargets = HashSet<DivImageDownloadCallback>()

        val size get() = activeTargets.size

        fun add(target: DivImageDownloadCallback) {
            synchronized(this) {
                activeTargets.add(target)
            }
        }

        fun remove(target: DivImageDownloadCallback) {
            synchronized(this) {
                activeTargets.remove(target)
            }
        }

        fun clean() {
            synchronized(this) {
                activeTargets.clear()
            }
        }
    }
}
