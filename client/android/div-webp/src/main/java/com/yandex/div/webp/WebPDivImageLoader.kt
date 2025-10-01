package com.yandex.div.webp

import android.content.Context
import android.graphics.ImageDecoder
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.graphics.createBitmap
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.ByteBuffer

/**
 * DivImageLoader for WebP images (API 28+).
 * Decoding via ImageDecoder and returns Drawable (or AnimatedImageDrawable for animated WebP).
 */
@RequiresApi(Build.VERSION_CODES.P)
@InternalApi
public class WebPDivImageLoader @JvmOverloads constructor(
    private val context: Context,
    lifecycleOwnerScope: CoroutineScope? = null,
) : DivImageLoader {
    private val okHttpClient = OkHttpClient.Builder().build()
    private val coroutineScope = lifecycleOwnerScope ?: MainScope()
    private val cacheBytesManager = WebpCacheManager()

    override fun hasSvgSupport(): Boolean = false

    override fun hasWebPSupport(): Boolean = true

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val call = createCallOrNull(imageUrl)
        coroutineScope.launch {
            val drawable = withContext(Dispatchers.IO) {
                val bytes = cacheBytesManager.get(imageUrl)
                    ?: run {
                        val fresh =
                            if (call == null) getImageData(imageUrl) else downloadImage(call)
                        fresh?.also { cacheBytesManager.put(imageUrl, it) }
                    }

                bytes?.let { data ->
                    runCatching {
                        val source = ImageDecoder.createSource(ByteBuffer.wrap(data))
                        ImageDecoder.decodeDrawable(source) { decoder, _, _ ->
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        }
                    }.getOrNull()
                }
            }

            drawable?.let { callback.onSuccess(drawable) } ?: callback.onError()
        }

        return LoadReference { call?.cancel() }
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val call = createCallOrNull(imageUrl)
        coroutineScope.launch {
            val drawable = withContext(Dispatchers.IO) {
                val bytes = cacheBytesManager.get(imageUrl)
                    ?: run {
                        val fresh =
                            if (call == null) getImageData(imageUrl) else downloadImage(call)
                        fresh?.also { cacheBytesManager.put(imageUrl, it) }
                    }

                bytes?.let { data ->
                    runCatching {
                        val src = ImageDecoder.createSource(ByteBuffer.wrap(data))
                        ImageDecoder.decodeDrawable(src) { decoder, _, _ ->
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        }
                    }.getOrNull()
                }
            } ?: return@launch

            imageView.setImageDrawable(drawable)
            (drawable as? Animatable)?.start()
        }

        return LoadReference { call?.cancel() }
    }

    override fun loadImageBytes(
        imageUrl: String,
        callback: DivImageDownloadCallback
    ): LoadReference {
        val call = createCallOrNull(imageUrl)
        val bitmapSource = call?.let { BitmapSource.NETWORK } ?: BitmapSource.DISK
        coroutineScope.launch {
            val (bytes, source) = withContext(Dispatchers.IO) {
                val cached = cacheBytesManager.get(imageUrl)
                cached?.let { cached to BitmapSource.MEMORY }
                    ?: run {
                        val fresh =
                            if (call == null) getImageData(imageUrl) else downloadImage(call)
                        fresh?.also { cacheBytesManager.put(imageUrl, fresh) }
                        fresh to bitmapSource
                    }
            }

            bytes?.let {
                callback.onSuccess(
                    CachedBitmap(
                        createBitmap(1, 1),
                        bytes,
                        Uri.parse(imageUrl),
                        source
                    )
                )
            } ?: run {
                callback.onError()
                return@launch
            }
        }

        return LoadReference { call?.cancel() }
    }


    private fun downloadImage(call: Call): ByteArray? = runCatching {
        call.execute().body?.bytes()
    }.getOrNull()

    private fun createCallOrNull(imageUrl: String): Call? {
        if (!(imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
            return null
        }
        val request = Request.Builder().url(imageUrl).build()
        return okHttpClient.newCall(request)
    }

    private fun getImageData(imageUrl: String): ByteArray? {
        val assetPath = imageUrl.removePrefix("file:///android_asset/")
        val stream = context.applicationContext?.assets?.open(assetPath) ?: return null
        stream.use {
            return it.readBytes()
        }
    }

}