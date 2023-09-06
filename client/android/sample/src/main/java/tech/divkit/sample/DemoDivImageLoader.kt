package tech.divkit.sample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class DemoDivImageLoader(
    context: Context
) : DivImageLoader {

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val picasso by lazy { createPicasso() }

    private fun createPicasso(): Picasso {
        return Picasso.Builder(appContext)
            .downloader(OkHttp3Downloader(appContext, DISK_CACHE_SIZE))
            .build()
    }

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val imageUri = Uri.parse(imageUrl)
        val target = DownloadCallbackAdapter(imageUri, callback)

        // Picasso requires starting download on the main thread
        mainHandler.post { picasso.load(imageUri).into(target) }

        return LoadReference { picasso.cancelRequest(target) }
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val target = ImageViewAdapter(imageView)
        picasso.load(Uri.parse(imageUrl)).into(target)
        return LoadReference { picasso.cancelRequest(target) }
    }

    override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val request = Request.Builder().url(imageUrl).build()
        val call = OkHttpClient.Builder().build().newCall(request)
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val response = call.execute()
                val source = response.cacheResponse?.let { BitmapSource.MEMORY } ?: BitmapSource.NETWORK
                val bytes = response.body?.bytes() ?: return@withContext null
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let {
                    CachedBitmap(it, bytes, Uri.parse(imageUrl), source)
                }
            }?.let {
                callback.onSuccess(it)
            } ?: callback.onError()
        }

        return LoadReference {
            call.cancel()
        }
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
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
            callback.onError()
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
    }

    private inner class ImageViewAdapter(
        private val imageView: ImageView
    ) : Target {

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            imageView.setImageBitmap(bitmap)
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) = Unit

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
    }

}

private fun Picasso.LoadedFrom.toBitmapSource(): BitmapSource {
    return when (this) {
        Picasso.LoadedFrom.MEMORY -> BitmapSource.MEMORY
        Picasso.LoadedFrom.DISK -> BitmapSource.DISK
        Picasso.LoadedFrom.NETWORK -> BitmapSource.NETWORK
    }
}
