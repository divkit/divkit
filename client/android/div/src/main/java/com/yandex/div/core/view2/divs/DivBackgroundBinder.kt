package com.yandex.div.core.view2.divs

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.PictureDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.util.DisplayMetrics
import android.util.StateSet
import android.view.View
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import com.yandex.div.R
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.observeBackground
import com.yandex.div.core.util.toCachedBitmap
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.drawable.LinearGradientDrawable
import com.yandex.div.internal.drawable.NinePatchDrawable
import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.drawable.ScalingDrawable
import com.yandex.div.internal.util.compareWith
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBackground
import com.yandex.div2.DivFilter
import com.yandex.div2.DivImageBackground
import com.yandex.div2.DivImageScale
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientRadius
import com.yandex.div2.DivRadialGradientRelativeRadius
import javax.inject.Inject

@DivScope
@Mockable
internal class DivBackgroundBinder @Inject constructor(
    private val imageLoader: DivImageLoader
) {

    fun bindBackground(
        context: BindingContext,
        view: View,
        newDefaultBackgroundList: List<DivBackground>?,
        oldDefaultBackgroundList: List<DivBackground>? = null,
        newFocusedBackgroundList: List<DivBackground>?,
        oldFocusedBackgroundList: List<DivBackground>? = null,
        subscriber: ExpressionSubscriber,
        additionalLayer: Drawable? = null
    ) {
        if (newFocusedBackgroundList == null) {
            bindDefaultBackground(
                context,
                view,
                additionalLayer,
                newDefaultBackgroundList,
                oldDefaultBackgroundList,
                subscriber
            )
        } else {
            bindFocusBackground(
                context,
                view,
                additionalLayer,
                newDefaultBackgroundList,
                oldDefaultBackgroundList,
                newFocusedBackgroundList,
                oldFocusedBackgroundList,
                subscriber
            )
        }
    }

    private fun bindDefaultBackground(
        context: BindingContext,
        view: View,
        newAdditionalLayer: Drawable?,
        newDefaultBackgroundList: List<DivBackground>?,
        oldDefaultBackgroundList: List<DivBackground>?,
        subscriber: ExpressionSubscriber
    ) {
        val newBackground = newDefaultBackgroundList ?: emptyList()
        val oldBackground = oldDefaultBackgroundList ?: emptyList()
        val oldAdditionalLayer = view.additionalLayer

        if (newBackground.compareWith(oldBackground) { left, right -> left.equalsToConstant(right) }
            && newAdditionalLayer == oldAdditionalLayer) {
            return
        }

        view.applyDefaultBackground(
            context,
            newAdditionalLayer,
            newDefaultBackgroundList
        )

        if (newBackground.all { it.isConstant() }) {
            return
        }

        val callback = { _: Any ->
            view.applyDefaultBackground(
                context,
                newAdditionalLayer,
                newDefaultBackgroundList
            )
        }
        addBackgroundSubscriptions(newDefaultBackgroundList, context.expressionResolver, subscriber, callback)
    }

    private fun View.applyDefaultBackground(
        context: BindingContext,
        additionalLayer: Drawable?,
        defaultBackgroundList: List<DivBackground>?
    ) {
        val metrics = resources.displayMetrics
        val resolver = context.expressionResolver

        val newDefaultDivBackground =
            defaultBackgroundList?.map { it.toBackgroundState(metrics, resolver) }
                ?: emptyList()

        val oldDefaultDivBackground = this.defaultBackgroundList
        val oldAdditionalLayer = this.additionalLayer

        val backgroundChanged = (oldDefaultDivBackground != newDefaultDivBackground) ||
            (oldAdditionalLayer != additionalLayer)

        if (backgroundChanged) {
            updateBackground(newDefaultDivBackground.toDrawable(context, this, additionalLayer))

            this.defaultBackgroundList = newDefaultDivBackground
            this.focusedBackgroundList = null
            this.additionalLayer = additionalLayer
        }
    }

    private fun bindFocusBackground(
        context: BindingContext,
        view: View,
        newAdditionalLayer: Drawable?,
        newDefaultBackgroundList: List<DivBackground>?,
        oldDefaultBackgroundList: List<DivBackground>?,
        newFocusedBackgroundList: List<DivBackground>,
        oldFocusedBackgroundList: List<DivBackground>?,
        subscriber: ExpressionSubscriber
    ) {
        val newBackground = newDefaultBackgroundList ?: emptyList()
        val oldBackground = oldDefaultBackgroundList ?: emptyList()
        val newFocusedBackground = newFocusedBackgroundList
        val oldFocusedBackground = oldFocusedBackgroundList ?: emptyList()
        val oldAdditionalLayer = view.additionalLayer

        if (newBackground.compareWith(oldBackground) { left, right -> left.equalsToConstant(right) }
            && newFocusedBackground.compareWith(oldFocusedBackground) { left, right -> left.equalsToConstant(right) }
            && newAdditionalLayer == oldAdditionalLayer) {
            return
        }

        view.applyFocusedBackground(
            context,
            newAdditionalLayer,
            newDefaultBackgroundList,
            newFocusedBackgroundList
        )

        if (newBackground.all { it.isConstant() } && newFocusedBackground.all { it.isConstant() }) {
            return
        }

        val callback = { _: Any ->
            view.applyFocusedBackground(
                context,
                newAdditionalLayer,
                newDefaultBackgroundList,
                newFocusedBackgroundList
            )
        }
        val resolver = context.expressionResolver
        addBackgroundSubscriptions(newDefaultBackgroundList, resolver, subscriber, callback)
        addBackgroundSubscriptions(newFocusedBackgroundList, resolver, subscriber, callback)
    }

    private fun View.applyFocusedBackground(
        context: BindingContext,
        additionalLayer: Drawable?,
        defaultBackgroundList: List<DivBackground>?,
        focusedBackgroundList: List<DivBackground>
    ) {
        val metrics = resources.displayMetrics
        val resolver = context.expressionResolver

        val newDefaultDivBackground =
            defaultBackgroundList?.map { it.toBackgroundState(metrics, resolver) } ?: emptyList()
        val newFocusedDivBackground = focusedBackgroundList.map { it.toBackgroundState(metrics, resolver) }

        val oldDefaultDivBackground = this.defaultBackgroundList
        val oldFocusedDivBackground = this.focusedBackgroundList
        val oldAdditionalLayer = this.additionalLayer

        val backgroundChanged = (oldDefaultDivBackground != newDefaultDivBackground) ||
            (oldFocusedDivBackground != newFocusedDivBackground) ||
            (oldAdditionalLayer != additionalLayer)

        if (backgroundChanged) {
            val stateList = StateListDrawable()

            stateList.addState(
                intArrayOf(android.R.attr.state_focused),
                newFocusedDivBackground.toDrawable(context, this, additionalLayer)
            )

            if (defaultBackgroundList != null || additionalLayer != null) {
                stateList.addState(
                    StateSet.WILD_CARD,
                    newDefaultDivBackground.toDrawable(context, this, additionalLayer))
            }

            updateBackground(stateList)

            this.defaultBackgroundList = newDefaultDivBackground
            this.focusedBackgroundList = newFocusedDivBackground
            this.additionalLayer = additionalLayer
        }
    }

    private fun View.updateBackground(drawable: Drawable?) {
        val drawables = mutableListOf<Drawable>()

        drawable?.let { drawables.add(it) }

        var hasNativeAnimation = false
        val layerBackground = background as? LayerDrawable
        if (layerBackground?.findDrawableByLayerId(R.drawable.native_animation_background) != null) {
            val animation = ContextCompat
                .getDrawable(context, R.drawable.native_animation_background)
            animation?.let { drawables.add(it) }
            hasNativeAnimation = true
        }

        background = LayerDrawable(drawables.toTypedArray())
        if (hasNativeAnimation) {
            //mark background has animation
            (background as LayerDrawable).setId((background as LayerDrawable).numberOfLayers - 1,
                R.drawable.native_animation_background)
        }
    }

    private fun addBackgroundSubscriptions(
        backgroundList: List<DivBackground>?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        callback: (Any) -> Unit
    ) {
        backgroundList?.forEach { background ->
            subscriber.observeBackground(background, resolver, callback)
        }
    }

    private fun List<DivBackgroundState>?.toDrawable(
        context: BindingContext,
        view: View,
        additionalLayer: Drawable?
    ): Drawable? {
        additionalLayer?.mutate()

        this ?: return additionalLayer?.let { LayerDrawable(arrayOf(it)) }

        val listDrawable = mapNotNull {
            it.toDrawable(context, view, imageLoader).mutate()
        }.toMutableList()
        additionalLayer?.let { listDrawable.add(it) }

        return if (listDrawable.isNotEmpty()) LayerDrawable(listDrawable.toTypedArray()) else null
    }

    private fun DivBackground.toBackgroundState(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver
    ): DivBackgroundState = when (this) {
        is DivBackground.LinearGradient -> DivBackgroundState.LinearGradient(
            angle = value.angle.evaluate(resolver).toIntSafely(),
            colors = value.colors.evaluate(resolver),
        )
        is DivBackground.RadialGradient -> DivBackgroundState.RadialGradient(
            centerX = value.centerX.toBackgroundState(metrics, resolver),
            centerY = value.centerY.toBackgroundState(metrics, resolver),
            colors = value.colors.evaluate(resolver),
            radius = value.radius.toBackgroundState(metrics, resolver)
        )
        is DivBackground.Image -> DivBackgroundState.Image(
            alpha = value.alpha.evaluate(resolver),
            contentAlignmentHorizontal = value.contentAlignmentHorizontal.evaluate(resolver),
            contentAlignmentVertical = value.contentAlignmentVertical.evaluate(resolver),
            imageUrl = value.imageUrl.evaluate(resolver),
            preloadRequired = value.preloadRequired.evaluate(resolver),
            scale = value.scale.evaluate(resolver),
            filters = value.filters?.map { it.toBackgroundState(resolver) },
            isVectorCompatible = value.isVectorCompatible(resolver)
        )
        is DivBackground.Solid -> DivBackgroundState.Solid(
            color = value.color.evaluate(resolver)
        )
        is DivBackground.NinePatch -> DivBackgroundState.NinePatch(
            imageUrl = value.imageUrl.evaluate(resolver),
            insets= Rect(
                    value.insets.left.evaluate(resolver).toIntSafely(),
                    value.insets.top.evaluate(resolver).toIntSafely(),
                    value.insets.right.evaluate(resolver).toIntSafely(),
                    value.insets.bottom.evaluate(resolver).toIntSafely()
            )
        )
    }

    /**
     * Vector format ImageBackground doesn't support alpha and filters.
     * If alpha is not 1.0 or filters are specified for ImageBackground, it should be rasterized.
     */
    private fun DivImageBackground.isVectorCompatible(resolver: ExpressionResolver) : Boolean {
        return alpha.evaluate(resolver) == 1.0 && filters.isNullOrEmpty()
    }

    private fun DivRadialGradientCenter.toBackgroundState(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver
    ) = when (this) {
        is DivRadialGradientCenter.Fixed -> DivBackgroundState.RadialGradient.Center.Fixed(
            value.toPxF(metrics, resolver)
        )
        is DivRadialGradientCenter.Relative -> DivBackgroundState.RadialGradient.Center.Relative(
            value.value.evaluate(resolver).toFloat()
        )
    }

    private fun DivRadialGradientRadius.toBackgroundState(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver
    ) = when (this) {
        is DivRadialGradientRadius.FixedSize -> DivBackgroundState.RadialGradient.Radius.Fixed(
            value.toPxF(metrics, resolver)
        )
        is DivRadialGradientRadius.Relative -> DivBackgroundState.RadialGradient.Radius.Relative(
            value.value.evaluate(resolver)
        )
    }

    private fun DivFilter.toBackgroundState(resolver: ExpressionResolver) = when (this) {
        is DivFilter.Blur -> DivBackgroundState.Image.Filter.Blur(value.radius.evaluate(resolver).toIntSafely(), this)
        is DivFilter.RtlMirror -> DivBackgroundState.Image.Filter.RtlMirror(this)
    }

    @Suppress("UNCHECKED_CAST")
    private var View.defaultBackgroundList: List<DivBackgroundState>?
        get() = getTag(R.id.div_default_background_list_tag) as? List<DivBackgroundState>?
        set(value) {
            setTag(R.id.div_default_background_list_tag, value)
        }

    @Suppress("UNCHECKED_CAST")
    private var View.focusedBackgroundList: List<DivBackgroundState>?
        get() = getTag(R.id.div_focused_background_list_tag) as? List<DivBackgroundState>?
        set(value) {
            setTag(R.id.div_focused_background_list_tag, value)
        }

    private var View.additionalLayer: Drawable?
        get() = getTag(R.id.div_additional_background_layer_tag) as? Drawable?
        set(value) {
            setTag(R.id.div_additional_background_layer_tag, value)
        }

    private sealed class DivBackgroundState {
        data class LinearGradient(
            val angle: Int,
            val colors: List<Int>
        ): DivBackgroundState()

        data class RadialGradient(
            val centerX: Center,
            val centerY: Center,
            val colors: List<Int>,
            val radius: Radius
        ) : DivBackgroundState() {
            sealed class Center {
                data class Relative(val value: Float) : Center()
                data class Fixed(val valuePx: Float) : Center()

                fun toRadialGradientDrawableCenter() = when (this) {
                    is Fixed -> RadialGradientDrawable.Center.Fixed(valuePx)
                    is Relative -> RadialGradientDrawable.Center.Relative(value)
                }
            }

            sealed class Radius {
                data class Relative(val value: DivRadialGradientRelativeRadius.Value) : Radius()
                data class Fixed(val valuePx: Float) : Radius()

                fun toRadialGradientDrawableRadius() = when (this) {
                    is Fixed -> RadialGradientDrawable.Radius.Fixed(valuePx)
                    is Relative -> RadialGradientDrawable.Radius.Relative(
                        when (value) {
                            DivRadialGradientRelativeRadius.Value.FARTHEST_CORNER ->
                                RadialGradientDrawable.Radius.Relative.Type.FARTHEST_CORNER
                            DivRadialGradientRelativeRadius.Value.NEAREST_CORNER ->
                                RadialGradientDrawable.Radius.Relative.Type.NEAREST_CORNER
                            DivRadialGradientRelativeRadius.Value.FARTHEST_SIDE ->
                                RadialGradientDrawable.Radius.Relative.Type.FARTHEST_SIDE
                            DivRadialGradientRelativeRadius.Value.NEAREST_SIDE ->
                                RadialGradientDrawable.Radius.Relative.Type.NEAREST_SIDE
                        }
                    )
                }
            }
        }

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
                scaleDrawable.alignmentHorizontal = contentAlignmentHorizontal.toHorizontalAlignment()
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
            is LinearGradient -> LinearGradientDrawable(
                angle = angle.toFloat(),
                colors = colors.toIntArray()
            )
            is RadialGradient -> RadialGradientDrawable(
                radius = radius.toRadialGradientDrawableRadius(),
                centerX = centerX.toRadialGradientDrawableCenter(),
                centerY = centerY.toRadialGradientDrawableCenter(),
                colors = colors.toIntArray()
            )
        }
    }
}
