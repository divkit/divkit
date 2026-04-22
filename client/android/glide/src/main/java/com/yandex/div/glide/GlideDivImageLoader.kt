package com.yandex.div.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Option
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoadError.Companion.toDivImageLoadError
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.svg.SvgDecoder
import java.io.InputStream
import kotlin.math.max

class GlideDivImageLoader @JvmOverloads constructor(
    private val context: Context,
    private val limitImageBitmapSizeEnabled: Boolean = true,
) : DivImageLoader {

    private val maxDisplaySize = context.resources.displayMetrics.let {
        max(it.widthPixels, it.heightPixels)
    }

    init {
        Glide.get(context).registry
            .append(InputStream::class.java, PictureDrawable::class.java, svgDecoder)
    }

    @Deprecated("Is unused in DivKit, will be removed in future")
    override fun hasSvgSupport() = true

    override fun needLimitBitmapSize() = false

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback) =
        loadImage(imageUrl, callback, canLimitSize = true)

    override fun loadAnimatedImage(imageUrl: String, callback: DivImageDownloadCallback) =
        loadImage(imageUrl, callback, canLimitSize = false)

    private fun loadImage(imageUrl: String, callback: DivImageDownloadCallback, canLimitSize: Boolean): LoadReference {
        val imageUri = Uri.parse(imageUrl)
        // create target to be able to cancel loading
        val target = createTarget<Drawable>()

        // load result will be handled by RequestListener to get dataSource
        Glide.with(context).load(imageUri)
            .set(Option.memory(KEY_SVG), SvgDecoder.isSvg(imageUrl))
            .limitImageBitmapSizeIfNeed(canLimitSize)
            .listener(ImageRequestListener(imageUrl, callback))
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

    private class ImageRequestListener<T: Drawable>(
        private val imageUrl: String,
        private val callback: DivImageDownloadCallback,
    ): RequestListener<T> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>,
            isFirstResource: Boolean
        ): Boolean {
            callback.onError(e.toDivImageLoadError(imageUrl))
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

    private fun <T : BaseRequestOptions<T>> T.limitImageBitmapSizeIfNeed(canLimitSize: Boolean): T {
        if (!limitImageBitmapSizeEnabled || !canLimitSize) return this
        return override(maxDisplaySize, maxDisplaySize).optionalCenterInside()
    }

    private companion object {

        private const val KEY_SVG = "is_svg"

        fun <T: Drawable> createTarget() = object : CustomTarget<T>() {
            override fun onResourceReady(resource: T, transition: Transition<in T>?) = Unit
            override fun onLoadCleared(placeholder: Drawable?) = Unit
        }

        private val svgDecoder = object : ResourceDecoder<InputStream, PictureDrawable> {

            override fun handles(source: InputStream, options: Options) =
                options.get(Option.memory<Boolean>(KEY_SVG)) == true

            override fun decode(source: InputStream, width: Int, height: Int, options: Options) =
                SimpleResource(SvgDecoder.decode(source))
        }

        private fun DataSource.toBitmapSource(): BitmapSource {
            return when (this) {
                DataSource.REMOTE -> BitmapSource.NETWORK
                DataSource.LOCAL -> BitmapSource.DISK
                else -> BitmapSource.MEMORY // here can be DataSource.DISK_CACHE or DataSource.MEMORY_CACHE
            }
        }
    }
}
