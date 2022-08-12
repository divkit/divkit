package com.yandex.divkit.demo.div

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import java.util.concurrent.atomic.AtomicInteger

class DemoDivImageLoader(
    context: Context
) : DivImageLoader {

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val picasso by lazy { createPicasso() }
    private val targets = TargetList()

    val isIdle: Boolean
        get() = targets.size == 0

    private fun createPicasso(): Picasso {
        return Picasso.Builder(appContext)
            .downloader(OkHttp3Downloader(appContext, DISK_CACHE_SIZE))
            .build()
    }

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val imageUri = Uri.parse(imageUrl)
        val target = DownloadCallbackAdapter(imageUri, callback)
        targets.addTarget(target)

        // Picasso requires starting download on the main thread
        mainHandler.post { picasso.load(imageUri).into(target) }

        return LoadReference { picasso.cancelRequest(target) }
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val target = ImageViewAdapter(imageView)
        targets.addTarget(target)
        picasso.load(Uri.parse(imageUrl)).into(target)
        return LoadReference { picasso.cancelRequest(target) }
    }

    override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        // TODO: load raw bytes instead of Bitmap
        return loadImage(imageUrl, callback)
    }

    fun resetIdle() {
        //TODO(DIVKIT-290): Find out why picasso loses callbacks.
        targets.clean()
    }

    private companion object {
        const val DISK_CACHE_SIZE = 16_777_216L
    }

    private inner class DownloadCallbackAdapter(
        private val imageUri: Uri,
        private val callback: DivImageDownloadCallback
    ) : Target {

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            callback.onSuccess(CachedBitmap(bitmap, imageUri, from.toBitmapSource()))
            targets.removeTarget(this)
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
            callback.onError()
            targets.removeTarget(this)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
    }

    private inner class ImageViewAdapter(
        private val imageView: ImageView
    ) : Target {

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            imageView.setImageBitmap(bitmap)
            targets.removeTarget(this)
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
            targets.removeTarget(this)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
    }

    inner class TargetList() {
        private val activeTargets = ArrayList<Target>()
        val size get() = activeTargets.size
        private val requestCount = AtomicInteger(0)

        fun addTarget(target: Target) {
            requestCount.incrementAndGet()
            activeTargets.add(target)
        }

        fun removeTarget(target: Target) {
            requestCount.decrementAndGet()
            activeTargets.remove(target)
        }

        fun clean() {
            requestCount.set(0)
            activeTargets.clear()
        }
    }
}

private fun Picasso.LoadedFrom.toBitmapSource(): BitmapSource {
    return when (this) {
        Picasso.LoadedFrom.MEMORY -> BitmapSource.MEMORY
        Picasso.LoadedFrom.DISK -> BitmapSource.DISK
        Picasso.LoadedFrom.NETWORK -> BitmapSource.NETWORK
    }
}
