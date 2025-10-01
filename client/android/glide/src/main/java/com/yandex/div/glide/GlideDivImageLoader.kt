package com.yandex.div.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference

class GlideDivImageLoader(
    private val context: Context
) : DivImageLoader {

    override fun hasSvgSupport() = false

    override fun hasWebPSupport() = false

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val imageUri = Uri.parse(imageUrl)
        // create target to be able to cancel loading
        val target = object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) { }
            override fun onLoadCleared(placeholder: Drawable?) { }
        }

        // load result will be handled by RequestListener to get dataSource
        Glide.with(context).asBitmap().load(imageUri)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .listener(BitmapRequestListener(callback, imageUri))
            .into(target)

        return LoadReference {
            Glide.with(context).clear(target)
        }
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val imageUri = Uri.parse(imageUrl)

        Glide.with(context).asBitmap().load(imageUri).into(imageView)

        return LoadReference {
            Glide.with(context).clear(imageView)
        }
    }

    override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val imageUri = Uri.parse(imageUrl)
        // create target to be able to cancel loading
        val target = object : CustomTarget<GifDrawable>() {
            override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) { }
            override fun onLoadCleared(placeholder: Drawable?) { }
        }

        // load result will be handled by RequestListener to get dataSource
        Glide.with(context).asGif().listener(GifImageRequestListener(callback, imageUri)).load(imageUri).into(target)

        return LoadReference {
            Glide.with(context).clear(target)
        }
    }

    private class BitmapRequestListener(
        private val callback: DivImageDownloadCallback,
        private val imageUri: Uri,
    ): RequestListener<Bitmap> {
        override fun onLoadFailed(
            e: GlideException?, model: Any?, target: Target<Bitmap>, isFirstResource: Boolean
        ): Boolean {
            callback.onError()
            return false
        }

        override fun onResourceReady(
            resource: Bitmap, model: Any, target: Target<Bitmap>?,
            dataSource: DataSource, isFirstResource: Boolean
        ): Boolean {
            callback.onSuccess(CachedBitmap(resource, imageUri, dataSource.toBitmapSource()))
            return false
        }
    }

    private class GifImageRequestListener(
        private val callback: DivImageDownloadCallback,
        private val imageUri: Uri,
    ): RequestListener<GifDrawable> {
        override fun onLoadFailed(
            e: GlideException?, model: Any?, target: Target<GifDrawable>, isFirstResource: Boolean
        ): Boolean {
            callback.onError()
            return false
        }

        override fun onResourceReady(
            resource: GifDrawable, model: Any, target: Target<GifDrawable>?,
            dataSource: DataSource, isFirstResource: Boolean
        ): Boolean {
            val imageBytes = ByteArray(resource.buffer.remaining())
            val byteArray = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val bitmap = CachedBitmap(byteArray, imageBytes, imageUri, dataSource.toBitmapSource())
            callback.onSuccess(bitmap)
            return true
        }
    }
}

private fun DataSource.toBitmapSource(): BitmapSource {
    return when (this) {
        DataSource.REMOTE -> BitmapSource.NETWORK
        DataSource.LOCAL -> BitmapSource.DISK
        else -> BitmapSource.MEMORY // here can be DataSource.DISK_CACHE or DataSource.MEMORY_CACHE
    }
}
