package com.yandex.div.core.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.yandex.div.R
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.divs.widgets.LoadableImage
import com.yandex.div.core.view2.drawable.ScaleDrawable
import com.yandex.div.internal.widget.AspectImageView
import com.yandex.div.internal.widget.DivLayoutParams
import java.util.concurrent.Future

open class LoadableImageView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AspectImageView(context, attrs, defStyleAttr), LoadableImage, DivExtendableView {

    @Deprecated("Use imageTransform instead")
    internal var currentBitmapWithoutFilters: Bitmap? = null
    internal var loadReference: LoadReference? = null

    private var imageChangeCallback: (() -> Unit)? = null

    private var sourceDrawable: Drawable? = null

    var imageTransformer: ImageTransformer?
        get() = _imageTransformer
        set(value) {
            _imageTransformer = value ?: ImageTransformer.NOP
            sourceDrawable?.let { drawable ->
                setImageDrawable(drawable)
            }
        }

    private var _imageTransformer: ImageTransformer = ImageTransformer.NOP

    override var delegate: DivViewDelegate? = null

    @Deprecated("Use imageTransform instead")
    var externalImage: Drawable? = null
        set(value) {
            field = value?.scaleAccordingToDensity()
            invalidate()
        }
    override val isImageLoaded: Boolean
        get() = getTag(R.id.image_loaded_flag) == true

    override val isImagePreview: Boolean
        get() = getTag(R.id.image_loaded_flag) == false

    /**
     * Marks if bitmap was actually set on [LoadableImageView].
     */
    override fun imageLoaded() = setTag(R.id.image_loaded_flag, true)

    override fun previewLoaded() = setTag(R.id.image_loaded_flag, false)

    /**
     * Resetting flag when rebind before loading new image
     */
    override fun resetImageLoaded() = setTag(R.id.image_loaded_flag, null)

    override fun setPlaceholder(drawable: Drawable?) = setImageDrawable(drawable)
    override fun setPreview(drawable: Drawable?) = setImageDrawable(drawable)
    override fun setPreview(bitmap: Bitmap?) = setImageBitmap(bitmap)
    override fun setImage(bitmap: Bitmap?) = setImageBitmap(bitmap)
    override fun setImage(drawable: Drawable?) {
        setImageDrawable(drawable)
        val currentDrawable = this.drawable

        if (currentDrawable is Animatable && externalImage == null) {
            currentDrawable.start()
        }
    }

    override fun saveLoadingTask(task: Future<*>) = setTag(R.id.bitmap_load_references_tag, task)
    override fun getLoadingTask(): Future<*>? =
        getTag(R.id.bitmap_load_references_tag) as? Future<*>

    override fun cleanLoadingTask() = setTag(R.id.bitmap_load_references_tag, null)

    @CallSuper
    override fun setImageDrawable(drawable: Drawable?) {
        sourceDrawable = drawable

        if (externalImage != null) {
            if (this.drawable !== externalImage) {
                super.setImageDrawable(externalImage)
            }
            imageChangeCallback?.invoke()
            return
        }

        super.setImageDrawable(
            _imageTransformer.transform(drawable)?.scaleAccordingToDensity()
        )

        imageChangeCallback?.invoke()
    }

    @CallSuper
    override fun setImageBitmap(bm: Bitmap?) {
        if (shouldScaleAccordingToDensity()) {
            bm?.density = DisplayMetrics.DENSITY_DEFAULT
        }
        setImageDrawable(
            BitmapDrawable(context.resources, bm)
        )
    }

    override fun invalidateDrawable(dr: Drawable) {
        super.invalidateDrawable(delegate?.invalidateDrawable(dr) ?: dr)
    }

    override fun unscheduleDrawable(who: Drawable?) {
        delegate?.unscheduleDrawable(who)
        super.unscheduleDrawable(who)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (delegate?.onVisibilityChanged(changedView, visibility) == false) {
            super.onVisibilityChanged(changedView, visibility)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        delegate?.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate?.onDetachedFromWindow()
    }

    override fun buildDrawingCache(autoScale: Boolean) {
        delegate?.buildDrawingCache(autoScale)
        super.buildDrawingCache(autoScale)
    }

    /**
     * Scaling drawable only when [LoadableImageView] has (wrap_content, wrap_content) size
     * or when [imageScale] == Scale.NO_SCALE,
     * since unnecessary scaling leads to quality loss
     */
    private fun Drawable.scaleAccordingToDensity(): Drawable = when {
        !shouldScaleAccordingToDensity() -> this

        this is BitmapDrawable -> this.apply {
            bitmap?.density = DisplayMetrics.DENSITY_DEFAULT
            setTargetDensity(context.resources.displayMetrics)

        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && this is AnimatedImageDrawable -> {
            val scale = context.resources.displayMetrics.density
            ScaleDrawable(this, scale)
        }

        else -> this
    }

    private fun shouldScaleAccordingToDensity(): Boolean {
        val wrapsContent = wrapsSize(layoutParams.width) && wrapsSize(layoutParams.height)
        return wrapsContent || imageScale == Scale.NO_SCALE
    }

    private fun wrapsSize(size: Int) = when (size) {
        ViewGroup.LayoutParams.WRAP_CONTENT, DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> true
        else -> false
    }

    fun setImageChangeCallback(callback: (() -> Unit)? = null) {
        imageChangeCallback = callback
    }

    interface ImageTransformer {

        fun transform(drawable: Drawable?): Drawable?

        /**
         * Returns the input object
         */
        object NOP : ImageTransformer {
            override fun transform(drawable: Drawable?): Drawable? {
                return drawable
            }
        }
    }
}
