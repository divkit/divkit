package com.yandex.div.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import kotlin.math.max

class GlideDivImageLoader @JvmOverloads constructor(
    private val context: Context,
    private val limitImageBitmapSizeEnabled: Boolean = true,
) : DivImageLoader {

    private val maxDisplaySize = context.resources.displayMetrics.let {
        max(it.widthPixels, it.heightPixels)
    }

    override fun hasSvgSupport() = false

    override fun needLimitBitmapSize() = false

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val imageUri = Uri.parse(imageUrl)
        // create target to be able to cancel loading
        val target = createTarget<Drawable>()

        // load result will be handled by RequestListener to get dataSource
        Glide.with(context).load(imageUri)
            .limitImageBitmapSizeIfNeed()
            .listener(ImageRequestListener(callback))
            .into(target)

        return LoadReference {
            Glide.with(context).clear(target)
        }
    }

    @Deprecated("This method is not used in DivKit")
    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val imageUri = Uri.parse(imageUrl)

        Glide.with(context).asBitmap().load(imageUri).into(imageView)

        return LoadReference {
            Glide.with(context).clear(imageView)
        }
    }

    override fun loadAnimatedImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val imageUri = Uri.parse(imageUrl)
        // create target to be able to cancel loading
        val target = createTarget<GifDrawable>()

        // load result will be handled by RequestListener to get dataSource
        Glide.with(context).asGif()
            .limitImageBitmapSizeIfNeed()
            .listener(ImageRequestListener(callback))
            .load(imageUri)
            .into(target)

        return LoadReference {
            Glide.with(context).clear(target)
        }
    }

    private class ImageRequestListener<T: Drawable>(
        private val callback: DivImageDownloadCallback,
    ): RequestListener<T> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>,
            isFirstResource: Boolean
        ): Boolean {
            callback.onError(e)
            return false
        }

        override fun onResourceReady(
            resource: T,
            model: Any,
            target: Target<T>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            callback.onSuccess(DivCachedImage.Drawable(resource, dataSource.toBitmapSource()))
            return true
        }
    }

    private fun <T : BaseRequestOptions<T>> BaseRequestOptions<T>.limitImageBitmapSizeIfNeed(): T =
        if (limitImageBitmapSizeEnabled) {
            override(maxDisplaySize, maxDisplaySize).centerInside()
        } else {
            override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
        }

    private companion object {

        fun <T: Drawable> createTarget() = object : CustomTarget<T>() {
            override fun onResourceReady(resource: T, transition: Transition<in T>?) = Unit
            override fun onLoadCleared(placeholder: Drawable?) = Unit
        }

        fun DataSource.toBitmapSource(): BitmapSource {
            return when (this) {
                DataSource.REMOTE -> BitmapSource.NETWORK
                DataSource.LOCAL -> BitmapSource.DISK
                else -> BitmapSource.MEMORY // here can be DataSource.DISK_CACHE or DataSource.MEMORY_CACHE
            }
        }
    }
}
