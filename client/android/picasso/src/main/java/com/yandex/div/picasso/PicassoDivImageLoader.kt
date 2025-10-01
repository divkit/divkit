package com.yandex.div.picasso

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request

class PicassoDivImageLoader(
    context: Context,
    httpClientBuilder: OkHttpClient.Builder?,
) : DivImageLoader {

    constructor(context: Context) : this(context, null)

    private val appContext = context.applicationContext
    private val picasso by lazy { createPicasso() }
    private val targets = TargetList()
    private val httpClient = (httpClientBuilder ?: OkHttpClient.Builder())
        .cache(Cache(context.cacheDir, DISK_CACHE_SIZE))
        .build()
    private val coroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: MainScope()

    val isIdle: Boolean
        get() = targets.size == 0

    private fun createPicasso(): Picasso {
        return Picasso.Builder(appContext)
            .downloader(OkHttp3Downloader(appContext, DISK_CACHE_SIZE))
            .build()
    }

    override fun hasSvgSupport() = false

    override fun hasWebPSupport() = true

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val imageUri = Uri.parse(imageUrl)
        val target = DownloadCallbackAdapter(imageUri, callback)
        targets.addTarget(target)

        picasso.load(imageUri).into(target)

        return LoadReference {
            picasso.cancelRequest(target)
            targets.removeTarget(target)
        }
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val target = ImageViewAdapter(imageView)
        targets.addTarget(target)
        picasso.load(Uri.parse(imageUrl)).into(target)
        return LoadReference {
            picasso.cancelRequest(target)
            targets.removeTarget(target)
        }
    }

    override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        var loadReference: LoadReference = EMPTY_LOAD_REFERENCE
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val response = runCatching {
                    val request = Request.Builder().url(imageUrl).build()
                    val call = httpClient.newCall(request)
                    loadReference = LoadReference {
                        call.cancel()
                    }
                    call.execute()
                }.getOrNull() ?: return@withContext null
                val source = response.cacheResponse?.let { BitmapSource.MEMORY } ?: BitmapSource.NETWORK
                val bytes = response.body?.bytes() ?: return@withContext null
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let {
                    CachedBitmap(it, bytes, Uri.parse(imageUrl), source)
                }
            }?.let {
                callback.onSuccess(it)
            } ?: callback.onError()
        }

        return loadReference
    }

    fun resetIdle() {
        //TODO(DIVKIT-290): Find out why picasso loses callbacks.
        targets.clean()
    }

    private companion object {
        const val DISK_CACHE_SIZE = 16_777_216L
        val EMPTY_LOAD_REFERENCE = LoadReference { }
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

    inner class TargetList {
        private val activeTargets = ArrayList<Target>()
        val size get() = activeTargets.size

        fun addTarget(target: Target) {
            activeTargets.add(target)
        }

        fun removeTarget(target: Target) {
            activeTargets.remove(target)
        }

        fun clean() {
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
