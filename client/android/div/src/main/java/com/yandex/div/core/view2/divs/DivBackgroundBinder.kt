package com.yandex.div.core.view2.divs

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
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
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.applyFilters
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.drawable.LinearGradientDrawable
import com.yandex.div.internal.drawable.NinePatchDrawable
import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.drawable.ScalingDrawable
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBackground
import com.yandex.div2.DivFilter
import com.yandex.div2.DivImageBackground
import com.yandex.div2.DivImageScale
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientRadius
import com.yandex.div2.DivRadialGradientRelativeRadius
import com.yandex.div2.DivSolidBackground
import javax.inject.Inject

@DivScope
@Mockable
internal class DivBackgroundBinder @Inject constructor(
    private val imageLoader: DivImageLoader
) {

    @Suppress("UNCHECKED_CAST")
    fun bindBackground(
        view: View,
        divView: Div2View,
        defaultBackgroundList: List<DivBackground>?,
        focusedBackgroundList: List<DivBackground>?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        additionalLayer: Drawable? = null
    ): Unit = view.run {
        val metrics = resources.displayMetrics

        if (focusedBackgroundList == null) {
            val callback = { _: Any ->
                val newDefaultDivBackground =
                    defaultBackgroundList?.map { it.toBackgroundState(metrics, resolver) }
                        ?: emptyList()

                val currentDefaultDivBackground =
                    getTag(R.id.div_default_background_list_tag) as? List<DivBackgroundState>
                val currentAdditionalLayer = getTag(R.id.div_additional_background_layer_tag) as? Drawable?

                val backgroundChanged = (currentDefaultDivBackground != newDefaultDivBackground) ||
                    (currentAdditionalLayer != additionalLayer)

                if (backgroundChanged) {
                    updateBackground(newDefaultDivBackground.toDrawable(this, divView, additionalLayer, resolver))

                    setTag(R.id.div_default_background_list_tag, newDefaultDivBackground)
                    setTag(R.id.div_focused_background_list_tag, null)
                    setTag(R.id.div_additional_background_layer_tag, additionalLayer)
                }
            }

            callback(Unit)

            addBackgroundSubscriptions(defaultBackgroundList, resolver, subscriber, callback)
        } else {
            val callback = { _: Any ->
                val newDefaultDivBackground =
                    defaultBackgroundList?.map { it.toBackgroundState(metrics, resolver) } ?: emptyList()
                val newFocusedDivBackground = focusedBackgroundList.map { it.toBackgroundState(metrics, resolver) }

                val currentDefaultDivBackground =
                    getTag(R.id.div_default_background_list_tag) as? List<DivBackgroundState>
                val currentFocusedDivBackground =
                    getTag(R.id.div_focused_background_list_tag) as? List<DivBackgroundState>
                val currentAdditionalLayer = getTag(R.id.div_additional_background_layer_tag) as? Drawable?

                val backgroundChanged = (currentDefaultDivBackground != newDefaultDivBackground) ||
                    (currentFocusedDivBackground != newFocusedDivBackground) ||
                    (currentAdditionalLayer != additionalLayer)

                if (backgroundChanged) {
                    val stateList = StateListDrawable()

                    stateList.addState(
                        intArrayOf(android.R.attr.state_focused),
                        newFocusedDivBackground.toDrawable(this, divView, additionalLayer, resolver)
                    )

                    if (defaultBackgroundList != null || additionalLayer != null) {
                        stateList.addState(
                            StateSet.WILD_CARD,
                            newDefaultDivBackground.toDrawable(this, divView, additionalLayer, resolver))
                    }

                    updateBackground(stateList)

                    setTag(R.id.div_default_background_list_tag, newDefaultDivBackground)
                    setTag(R.id.div_focused_background_list_tag, newFocusedDivBackground)
                    setTag(R.id.div_additional_background_layer_tag, additionalLayer)
                }
            }

            callback(Unit)

            addBackgroundSubscriptions(focusedBackgroundList, resolver, subscriber, callback)
            addBackgroundSubscriptions(defaultBackgroundList, resolver, subscriber, callback)
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
            when (val divBackground = background.value()) {
                is DivSolidBackground -> subscriber.addSubscription(divBackground.color.observe(resolver, callback))

                is DivLinearGradient -> {
                    subscriber.addSubscription(divBackground.angle.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.colors.observe(resolver, callback))
                }

                is DivRadialGradient -> {
                    divBackground.centerX.observe(resolver, subscriber, callback)
                    divBackground.centerY.observe(resolver, subscriber, callback)
                    divBackground.radius.observe(resolver, subscriber, callback)
                    subscriber.addSubscription(divBackground.colors.observe(resolver, callback))
                }

                is DivImageBackground -> {
                    subscriber.addSubscription(divBackground.alpha.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.imageUrl.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.contentAlignmentHorizontal.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.contentAlignmentVertical.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.preloadRequired.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.scale.observe(resolver, callback))

                    for (filter in divBackground.filters ?: emptyList()) {
                        when (filter) {
                            is DivFilter.Blur -> {
                                subscriber.addSubscription(filter.value.radius.observe(resolver, callback))
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun List<DivBackgroundState>?.toDrawable(
        view: View,
        divView: Div2View,
        additionalLayer: Drawable?,
        resolver: ExpressionResolver,
    ): Drawable? {
        additionalLayer?.mutate()

        this ?: return additionalLayer?.let { LayerDrawable(arrayOf(it)) }

        val listDrawable = mapNotNull {
            it.toDrawable(divView, view, imageLoader, resolver).mutate()
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
            filters = value.filters?.map { it.toBackgroundState(resolver) }
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
            val filters: List<Filter>?
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
                divView: Div2View,
                target: View,
                imageLoader: DivImageLoader,
                resolver: ExpressionResolver
            ): Drawable {
                val scaleDrawable = ScalingDrawable()

                val url = imageUrl.toString()
                val loadReference = imageLoader.loadImage(url, object : DivIdLoggingImageDownloadCallback(divView) {
                    @UiThread
                    override fun onSuccess(cachedBitmap: CachedBitmap) {
                        cachedBitmap.bitmap.applyFilters(
                            target,
                            filters?.map { it.toDiv() },
                            divView.div2Component,
                            resolver
                        ) { scaleDrawable.setBitmap(it) }
                        scaleDrawable.alpha = (alpha * 255).toInt()
                        scaleDrawable.customScaleType = scale.toScaleType()
                        scaleDrawable.alignmentHorizontal = contentAlignmentHorizontal.toHorizontalAlignment()
                        scaleDrawable.alignmentVertical = contentAlignmentVertical.toVerticalAlignment()
                    }
                })
                divView.addLoadReference(loadReference, target)

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
            divView: Div2View,
            target: View,
            imageLoader: DivImageLoader,
            resolver: ExpressionResolver,
        ): Drawable = when (this) {
            is Image -> getDivImageBackground(divView, target, imageLoader, resolver)
            is NinePatch -> getNinePatchDrawable(divView, target, imageLoader)
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
