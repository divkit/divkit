package com.yandex.divkit.demo.div

import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference

class DemoDivImageLoaderWrapper(private val loader: DivImageLoader) : DivImageLoader {

    private val targets = TargetList()

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        targets.add(callback)
        val loadReference = loader.loadImage(imageUrl, CallbackWrapper(callback))
        return LoadReference {
            loadReference.cancel()
            targets.remove(callback)
        }
    }

    override fun loadImage(imageUrl: String, imageView: ImageView) = loader.loadImage(imageUrl, imageView)

    override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        targets.add(callback)
        val loadReference = loader.loadImageBytes(imageUrl, CallbackWrapper(callback))
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

        override fun onSuccess(cachedBitmap: CachedBitmap) {
            targets.remove(callback)
            callback.onSuccess(cachedBitmap)
        }

        override fun onSuccess(pictureDrawable: PictureDrawable) {
            targets.remove(callback)
            callback.onSuccess(pictureDrawable)
        }

        override fun onSuccess(drawable: Drawable) {
            targets.remove(callback)
            callback.onSuccess(drawable)
        }

        override fun onError() {
            targets.remove(callback)
            callback.onError()
        }

        override fun onCancel() {
            targets.remove(callback)
            callback.onCancel()
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
