package com.yandex.div.picasso

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import com.yandex.div.core.image.ASSET_PREFIX
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.internal.util.UiThreadHandler.Companion.executeOnMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.Call
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import kotlin.math.max

@Deprecated("Picasso library is deprecated. " +
    "Use CoilDivImageLoader, GlideDivImageLoader or implement your own DivImageLoader.")
class PicassoDivImageLoader(
    context: Context,
    httpClientBuilder: OkHttpClient.Builder?,
    private val limitImageBitmapSizeEnabled: Boolean,
) : DivImageLoader {

    constructor(context: Context) : this(context, null, true)

    constructor(
        context: Context,
        httpClientBuilder: OkHttpClient.Builder?
    ) : this(context, httpClientBuilder, true)

    constructor(
        context: Context,
        limitImageBitmapSizeEnabled: Boolean,
    ) : this(context, null, limitImageBitmapSizeEnabled)

    private val appContext = context.applicationContext
    private val picasso by lazy { createPicasso() }
    private val targets = TargetList()
    private val httpClient = (httpClientBuilder ?: OkHttpClient.Builder())
        .cache(Cache(context.cacheDir, DISK_CACHE_SIZE))
        .build()
    private val coroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: MainScope()

    private val maxDisplaySize = context.resources.displayMetrics.let {
        max(it.widthPixels, it.heightPixels)
    }

    @Deprecated("Was needed for internal use")
    val isIdle: Boolean
        get() = targets.size == 0

    private fun createPicasso(): Picasso {
        return Picasso.Builder(appContext)
            .downloader(OkHttp3Downloader(appContext, DISK_CACHE_SIZE))
            .build()
    }

    override fun hasSvgSupport() = false

    override fun needLimitBitmapSize() = false

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val imageUri = Uri.parse(imageUrl)
        val target = DownloadCallbackAdapter(callback)
        targets.addTarget(target)

        executeOnMainThread {
            picasso.load(imageUri)
            .limitImageBitmapSizeIfNeed()
            .into(target)
        }

        return LoadReference {
            picasso.cancelRequest(target)
            targets.removeTarget(target)
        }
    }

    @Deprecated("This method is not used in DivKit")
    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val target = ImageViewAdapter(imageView)
        targets.addTarget(target)
        picasso.load(Uri.parse(imageUrl)).into(target)
        return LoadReference {
            picasso.cancelRequest(target)
            targets.removeTarget(target)
        }
    }

    override fun loadAnimatedImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        return loadImageBytes(imageUrl, callback) { bytes, bitmapSource ->
            when {
                bytes.isEmpty() -> null
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
                    DivCachedImage.Drawable(decodeAnimatedDrawable(bytes), bitmapSource)
                else -> {
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let {
                        DivCachedImage.Bitmap(it, bitmapSource)
                    }
                }
            }
        }
    }

    private fun loadImageBytes(
        imageUrl: String,
        callback: DivImageDownloadCallback,
        decode: (ByteArray, BitmapSource) -> DivCachedImage?,
    ): LoadReference {
        targets.addTarget(targetStub)
        var loadReference: LoadReference = EMPTY_LOAD_REFERENCE
        val call = imageUrl.toHttpUrlOrNull()?.let { url ->
            val request = Request.Builder().url(url).build()
            httpClient.newCall(request).also {
                loadReference = LoadReference { it.cancel() }
            }
        }
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val (bytes, source) = call?.let { loadFromNetwork(it) ?: return@withContext null }
                    ?: loadFromDisk(imageUrl) ?: return@withContext null
                decode(bytes, source)
            }?.let {
                callback.onSuccess(it)
            } ?: callback.onError()
            targets.removeTarget(targetStub)
        }

        return loadReference
    }

    private fun loadFromNetwork(call: Call): Pair<ByteArray, BitmapSource>? {
        val response = runCatching { call.execute() }.getOrNull() ?: return null
        val bytes = response.body?.bytes() ?: return null
        val source = response.cacheResponse?.let { BitmapSource.MEMORY } ?: BitmapSource.NETWORK
        return bytes to source
    }

    private fun loadFromDisk(url: String): Pair<ByteArray, BitmapSource>? {
        val assetPath = url.removePrefix(ASSET_PREFIX)
        val stream = runCatching { appContext.assets?.open(assetPath) }.getOrNull() ?: return null
        return stream.use { it.readBytes() } to BitmapSource.DISK
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun decodeAnimatedDrawable(bytes: ByteArray): Drawable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val source = ImageDecoder.createSource(bytes)
            return decodeAnimatedDrawable(source)
        }

        // We use file here, instead of ByteBuffer, for creating source, because of android sdk issue:
        // https://issuetracker.google.com/issues/139371066?pli=1
        val tempFile = File.createTempFile(TEMP_FILE_NAME, GIF_SUFFIX, appContext.cacheDir)
        return try {
            tempFile.writeBytes(bytes)
            val source = ImageDecoder.createSource(tempFile)
            decodeAnimatedDrawable(source)
        } finally {
            tempFile.delete()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun decodeAnimatedDrawable(source: ImageDecoder.Source): Drawable {
        return ImageDecoder.decodeDrawable(source) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    }

    @Deprecated("Was needed for internal use")
    fun resetIdle() {
        //TODO(DIVKIT-290): Find out why picasso loses callbacks.
        targets.clean()
    }

    private companion object {
        private const val DISK_CACHE_SIZE = 16_777_216L
        private const val TEMP_FILE_NAME = "if_u_see_me_in_file_system_plz_report"
        private const val GIF_SUFFIX = ".gif"

        val EMPTY_LOAD_REFERENCE = LoadReference { }

        val targetStub = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) = Unit
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) = Unit
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
        }

        fun Picasso.LoadedFrom.toBitmapSource(): BitmapSource {
            return when (this) {
                Picasso.LoadedFrom.MEMORY -> BitmapSource.MEMORY
                Picasso.LoadedFrom.DISK -> BitmapSource.DISK
                Picasso.LoadedFrom.NETWORK -> BitmapSource.NETWORK
            }
        }
    }

    private inner class DownloadCallbackAdapter(private val callback: DivImageDownloadCallback) : Target {

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            callback.onSuccess(DivCachedImage.Bitmap(bitmap, from.toBitmapSource()))
            targets.removeTarget(this)
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            callback.onError(e)
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

    @Deprecated("Was needed for internal usa")
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

    private fun RequestCreator.limitImageBitmapSizeIfNeed() = apply {
        if (limitImageBitmapSizeEnabled) {
            resize(maxDisplaySize, maxDisplaySize).centerInside().onlyScaleDown()
        }
    }
}
