package com.yandex.div.core.view2.divs

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.util.DisplayMetrics
import android.util.StateSet
import android.view.View
import androidx.core.content.ContextCompat
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.observeBackground
import com.yandex.div.core.util.toColormap
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.util.toRadialGradientDrawableCenter
import com.yandex.div.core.util.toRadialGradientDrawableRadius
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.graphics.checkIsNotEmpty
import com.yandex.div.internal.util.compareWith
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBackground
import com.yandex.div2.DivFilter
import com.yandex.div2.DivImageBackground
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
            defaultBackgroundList?.map { it.toBackgroundState(context.divView, metrics, resolver) }
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
        val divView = context.divView
        val resolver = context.expressionResolver

        val newDefaultDivBackground =
            defaultBackgroundList?.map { it.toBackgroundState(divView, metrics, resolver) } ?: emptyList()
        val newFocusedDivBackground = focusedBackgroundList.map { it.toBackgroundState(divView, metrics, resolver) }

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
        divView: Div2View,
        metrics: DisplayMetrics,
        resolver: ExpressionResolver
    ): DivBackgroundState = when (this) {
        is DivBackground.LinearGradient -> DivBackgroundState.LinearGradient(
            angle = value.angle.evaluate(resolver).toIntSafely(),
            colormap = value.toColormap(resolver).checkIsNotEmpty(divView)
        )
        is DivBackground.RadialGradient -> DivBackgroundState.RadialGradient(
            centerX = value.centerX.toRadialGradientDrawableCenter(metrics, resolver),
            centerY = value.centerY.toRadialGradientDrawableCenter(metrics, resolver),
            colormap = value.toColormap(resolver).checkIsNotEmpty(divView),
            radius = value.radius.toRadialGradientDrawableRadius(metrics, resolver)
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
}
