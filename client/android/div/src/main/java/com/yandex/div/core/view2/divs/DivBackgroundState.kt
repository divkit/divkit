package com.yandex.div.core.view2.divs

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.View
import androidx.annotation.UiThread
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.util.toCachedBitmap
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.drawable.LinearGradientDrawable
import com.yandex.div.internal.drawable.NinePatchDrawable
import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.drawable.ScalingDrawable
import com.yandex.div.internal.graphics.Colormap
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivFilter
import com.yandex.div2.DivImageScale

internal sealed class DivBackgroundState {

    data class LinearGradient(
        val angle: Int,
        val colormap: Colormap,
    ): DivBackgroundState()

    data class RadialGradient(
        val centerX: RadialGradientDrawable.Center,
        val centerY: RadialGradientDrawable.Center,
        val colormap: Colormap,
        val radius: RadialGradientDrawable.Radius,
    ) : DivBackgroundState()

    data class Image(
        val alpha: Double,
        val contentAlignmentHorizontal: DivAlignmentHorizontal,
        val contentAlignmentVertical: DivAlignmentVertical,
        val imageUrl: Uri,
        val preloadRequired: Boolean,
        val scale: DivImageScale,
        val filters: List<Filter>?,
        val isVectorCompatible: Boolean,
    ): DivBackgroundState() {

        sealed class Filter {

            data class Blur(val radius: Int, val div: DivFilter.Blur) : Filter()
            data class RtlMirror(val div: DivFilter.RtlMirror) : Filter()

            fun toDiv(): DivFilter = when (this) {
                is Blur -> div
                is RtlMirror -> div
            }
        }

        fun getDivImageBackground(
            context: BindingContext,
            target: View,
            imageLoader: DivImageLoader,
        ): Drawable {
            val scaleDrawable = ScalingDrawable()
            scaleDrawable.alpha = (alpha * 255).toInt()
            scaleDrawable.customScaleType = scale.toScaleType()
            scaleDrawable.alignmentHorizontal =
                contentAlignmentHorizontal.toHorizontalAlignment(target.isLayoutRtl())
            scaleDrawable.alignmentVertical = contentAlignmentVertical.toVerticalAlignment()

            val url = imageUrl.toString()
            val loadReference = imageLoader.loadImage(
                url,
                object : DivIdLoggingImageDownloadCallback(context.divView) {
                    @UiThread
                    override fun onSuccess(cachedBitmap: CachedBitmap) {
                        target.applyBitmapFilters(
                            context,
                            cachedBitmap.bitmap,
                            filters?.map { it.toDiv() },
                        ) { scaleDrawable.setBitmap(it) }
                    }

                    @UiThread
                    override fun onSuccess(pictureDrawable: PictureDrawable) {
                        if (!isVectorCompatible) {
                            onSuccess(pictureDrawable.toCachedBitmap(imageUrl))
                            return
                        }
                        scaleDrawable.setPicture(pictureDrawable.picture)
                    }
                }
            )
            context.divView.addLoadReference(loadReference, target)

            return scaleDrawable
        }

        private fun DivImageScale.toScaleType(): ScalingDrawable.ScaleType {
            return when(this) {
                DivImageScale.FILL -> ScalingDrawable.ScaleType.FILL
                DivImageScale.FIT -> ScalingDrawable.ScaleType.FIT
                DivImageScale.STRETCH -> ScalingDrawable.ScaleType.STRETCH
                else -> ScalingDrawable.ScaleType.NO_SCALE
            }
        }

        private fun DivAlignmentHorizontal.toHorizontalAlignment(isRtl: Boolean): ScalingDrawable.AlignmentHorizontal {
            return when(this) {
                DivAlignmentHorizontal.LEFT -> ScalingDrawable.AlignmentHorizontal.LEFT
                DivAlignmentHorizontal.CENTER -> ScalingDrawable.AlignmentHorizontal.CENTER
                DivAlignmentHorizontal.RIGHT -> ScalingDrawable.AlignmentHorizontal.RIGHT
                DivAlignmentHorizontal.START ->
                    if (isRtl) ScalingDrawable.AlignmentHorizontal.RIGHT else ScalingDrawable.AlignmentHorizontal.LEFT
                DivAlignmentHorizontal.END ->
                    if (isRtl) ScalingDrawable.AlignmentHorizontal.LEFT else ScalingDrawable.AlignmentHorizontal.RIGHT
            }
        }

        private fun DivAlignmentVertical.toVerticalAlignment(): ScalingDrawable.AlignmentVertical {
            return when(this) {
                DivAlignmentVertical.CENTER -> ScalingDrawable.AlignmentVertical.CENTER
                DivAlignmentVertical.BOTTOM -> ScalingDrawable.AlignmentVertical.BOTTOM
                else -> ScalingDrawable.AlignmentVertical.TOP
            }
        }
    }

    data class Solid(
        val color: Int
    ): DivBackgroundState()

    data class NinePatch(
        val imageUrl: Uri,
        val insets: Rect
    ): DivBackgroundState() {

        fun getNinePatchDrawable(
            divView: Div2View,
            target: View,
            imageLoader: DivImageLoader
        ): Drawable {
            val ninePatchDrawable = NinePatchDrawable()

            val url = imageUrl.toString()
            val loadReference = imageLoader.loadImage(url, object : DivIdLoggingImageDownloadCallback(divView) {
                @UiThread
                override fun onSuccess(cachedBitmap: CachedBitmap) {
                    ninePatchDrawable.apply {
                        bottom = insets.bottom
                        left = insets.left
                        right = insets.right
                        top = insets.top
                        bitmap = cachedBitmap.bitmap
                    }
                }
            })
            divView.addLoadReference(loadReference, target)

            return ninePatchDrawable
        }
    }

    fun toDrawable(
        context: BindingContext,
        target: View,
        imageLoader: DivImageLoader,
    ): Drawable = when (this) {
        is Image -> getDivImageBackground(context, target, imageLoader)
        is NinePatch -> getNinePatchDrawable(context.divView, target, imageLoader)
        is Solid -> ColorDrawable(color)
        is LinearGradient -> LinearGradientDrawable(angle.toFloat(), colormap)
        is RadialGradient -> RadialGradientDrawable(radius, centerX, centerY, colormap.colors, colormap.positions)
    }
}
