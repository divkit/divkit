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
import com.yandex.div.core.util.toFilters
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
import javax.inject.Inject

@DivScope
@Mockable
internal class DivBackgroundBinder @Inject constructor(
    private val imageLoader: DivImageLoader
) {

    fun bindBackground(
        context: BindingContext,
        view: View,
        newDefaultBackgroundList: List<DivBackground>,
        oldDefaultBackgroundList: List<DivBackground>,
        newFocusedBackgroundList: List<DivBackground>,
        oldFocusedBackgroundList: List<DivBackground>,
        subscriber: ExpressionSubscriber,
        underlay: Drawable? = null,
        overlay: Drawable? = null,
    ) {
        if (newFocusedBackgroundList.isEmpty()) {
            bindDefaultBackground(
                context,
                view,
                underlay,
                overlay,
                newDefaultBackgroundList,
                oldDefaultBackgroundList,
                subscriber
            )
        } else {
            bindFocusBackground(
                context,
                view,
                underlay,
                overlay,
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
        underlay: Drawable?,
        overlay: Drawable?,
        newBackground: List<DivBackground>,
        oldBackground: List<DivBackground>,
        subscriber: ExpressionSubscriber
    ) {
        val oldUnderlay = view.boundBackgroundUnderlay
        val oldOverlay = view.boundBackgroundOverlay

        if (newBackground.compareWith(oldBackground) { left, right -> left.equalsToConstant(right) }
            && underlay == oldUnderlay
            && overlay == oldOverlay) {
            return
        }

        view.applyDefaultBackground(context, underlay, overlay, newBackground)

        if (newBackground.all { it.isConstant() }) {
            return
        }

        val callback = { _: Any ->
            view.applyDefaultBackground(context, underlay, overlay, newBackground)
        }
        addBackgroundSubscriptions(newBackground, context.expressionResolver, subscriber, callback)
    }

    private fun View.applyDefaultBackground(
        context: BindingContext,
        underlay: Drawable?,
        overlay: Drawable?,
        defaultBackgroundList: List<DivBackground>
    ) {
        val metrics = resources.displayMetrics
        val resolver = context.expressionResolver

        val newDefaultDivBackground = defaultBackgroundList.map {
            it.toBackgroundState(context.divView, metrics, resolver)
        }

        val oldDefaultDivBackground = this.defaultBackgroundList
        val oldUnderlay = this.boundBackgroundUnderlay
        val oldOverlay = this.boundBackgroundOverlay

        val backgroundChanged = (oldDefaultDivBackground != newDefaultDivBackground)
            || (oldUnderlay != underlay)
            || (oldOverlay != overlay)

        if (backgroundChanged) {
            updateBackground(newDefaultDivBackground.toDrawable(context, this, underlay, overlay))

            this.defaultBackgroundList = newDefaultDivBackground
            this.focusedBackgroundList = null
            this.boundBackgroundUnderlay = underlay
            this.boundBackgroundOverlay = overlay
        }
    }

    private fun bindFocusBackground(
        context: BindingContext,
        view: View,
        underlay: Drawable?,
        overlay: Drawable?,
        newBackground: List<DivBackground>,
        oldBackground: List<DivBackground>,
        newFocusedBackground: List<DivBackground>,
        oldFocusedBackground: List<DivBackground>,
        subscriber: ExpressionSubscriber
    ) {
        val oldUnderlay = view.boundBackgroundUnderlay
        val oldOverlay = view.boundBackgroundOverlay

        if (newBackground.compareWith(oldBackground) { left, right -> left.equalsToConstant(right) }
            && newFocusedBackground.compareWith(oldFocusedBackground) { left, right -> left.equalsToConstant(right) }
            && underlay == oldUnderlay
            && overlay == oldOverlay) {
            return
        }

        view.applyFocusedBackground(context, underlay, overlay, newBackground, newFocusedBackground)

        if (newBackground.all { it.isConstant() } && newFocusedBackground.all { it.isConstant() }) {
            return
        }

        val callback = { _: Any ->
            view.applyFocusedBackground(context, underlay, overlay, newBackground, newFocusedBackground)
        }
        val resolver = context.expressionResolver
        addBackgroundSubscriptions(newBackground, resolver, subscriber, callback)
        addBackgroundSubscriptions(newFocusedBackground, resolver, subscriber, callback)
    }

    private fun View.applyFocusedBackground(
        context: BindingContext,
        underlay: Drawable?,
        overlay: Drawable?,
        defaultBackgroundList: List<DivBackground>,
        focusedBackgroundList: List<DivBackground>
    ) {
        val metrics = resources.displayMetrics
        val divView = context.divView
        val resolver = context.expressionResolver

        val newDefaultDivBackground = defaultBackgroundList.map { it.toBackgroundState(divView, metrics, resolver) }
        val newFocusedDivBackground = focusedBackgroundList.map { it.toBackgroundState(divView, metrics, resolver) }

        val oldDefaultDivBackground = this.defaultBackgroundList
        val oldFocusedDivBackground = this.focusedBackgroundList
        val oldUnderlay = this.boundBackgroundUnderlay
        val oldOverlay = this.boundBackgroundOverlay

        val backgroundChanged = (oldDefaultDivBackground != newDefaultDivBackground)
            || (oldFocusedDivBackground != newFocusedDivBackground)
            || (oldUnderlay != underlay)
            || (oldOverlay != overlay)

        if (backgroundChanged) {
            val stateList = StateListDrawable()

            stateList.addState(
                intArrayOf(android.R.attr.state_focused),
                newFocusedDivBackground.toDrawable(context, this, underlay, overlay)
            )

            if (defaultBackgroundList.isNotEmpty() || underlay != null || overlay != null) {
                stateList.addState(
                    StateSet.WILD_CARD,
                    newDefaultDivBackground.toDrawable(context, this, underlay, overlay))
            }

            updateBackground(stateList)

            this.defaultBackgroundList = newDefaultDivBackground
            this.focusedBackgroundList = newFocusedDivBackground
            this.boundBackgroundUnderlay = underlay
            this.boundBackgroundOverlay = overlay
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
        underlay: Drawable?,
        overlay: Drawable?,
    ): Drawable? {
        if (this == null && underlay == null && overlay == null) {
            return null
        }

        val layers = mutableListOf<Drawable>()
        underlay?.let { layers.add(it) }
        this?.let { backgroundStates ->
            layers.addAll(backgroundStates.map { it.toDrawable(context, view, imageLoader) })
        }
        overlay?.let { layers.add(it) }

        layers.forEach { layer ->
            layer.mutate()
        }

        return LayerDrawable(layers.toTypedArray())
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
            filters = value.filters?.toFilters(resolver),
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
}
