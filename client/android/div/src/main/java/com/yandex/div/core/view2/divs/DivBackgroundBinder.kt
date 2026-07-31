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
        view: View,
        newDefaultBackgroundList: List<DivBackground>,
        oldDefaultBackgroundList: List<DivBackground>,
        newFocusedBackgroundList: List<DivBackground>,
        oldFocusedBackgroundList: List<DivBackground>,
        resolver: ExpressionResolver,
        divView: Div2View,
        subscriber: ExpressionSubscriber,
        underlay: Drawable? = null,
        overlay: Drawable? = null,
    ) {
        if (newFocusedBackgroundList.isEmpty()) {
            bindDefaultBackground(
                view,
                underlay,
                overlay,
                newDefaultBackgroundList,
                oldDefaultBackgroundList,
                resolver,
                divView,
                subscriber
            )
        } else {
            bindFocusBackground(
                view,
                underlay,
                overlay,
                newDefaultBackgroundList,
                oldDefaultBackgroundList,
                newFocusedBackgroundList,
                oldFocusedBackgroundList,
                resolver,
                divView,
                subscriber
            )
        }
    }

    private fun bindDefaultBackground(
        view: View,
        underlay: Drawable?,
        overlay: Drawable?,
        newBackground: List<DivBackground>,
        oldBackground: List<DivBackground>,
        resolver: ExpressionResolver,
        divView: Div2View,
        subscriber: ExpressionSubscriber
    ) {
        val oldUnderlay = view.boundBackgroundUnderlay
        val oldOverlay = view.boundBackgroundOverlay

        if (newBackground.compareWith(oldBackground) { left, right -> left.equalsToConstant(right) }
            && underlay == oldUnderlay
            && overlay == oldOverlay) {
            return
        }

        view.applyDefaultBackground(underlay, overlay, newBackground, resolver, divView)

        if (newBackground.all { it.isConstant() }) {
            return
        }

        val callback = { _: Any ->
            view.applyDefaultBackground(underlay, overlay, newBackground, resolver, divView)
        }
        addBackgroundSubscriptions(newBackground, resolver, subscriber, callback)
    }

    private fun View.applyDefaultBackground(
        underlay: Drawable?,
        overlay: Drawable?,
        defaultBackgroundList: List<DivBackground>,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        val metrics = resources.displayMetrics

        val newDefaultDivBackground = defaultBackgroundList.map {
            it.toBackgroundState(divView, metrics, resolver)
        }

        val oldDefaultDivBackground = this.defaultBackgroundList
        val oldUnderlay = this.boundBackgroundUnderlay
        val oldOverlay = this.boundBackgroundOverlay

        val backgroundChanged = (oldDefaultDivBackground != newDefaultDivBackground)
            || (oldUnderlay != underlay)
            || (oldOverlay != overlay)

        if (backgroundChanged) {
            updateBackground(newDefaultDivBackground.toDrawable(this, underlay, overlay, divView))

            this.defaultBackgroundList = newDefaultDivBackground
            this.focusedBackgroundList = null
            this.boundBackgroundUnderlay = underlay
            this.boundBackgroundOverlay = overlay
        }
    }

    private fun bindFocusBackground(
        view: View,
        underlay: Drawable?,
        overlay: Drawable?,
        newBackground: List<DivBackground>,
        oldBackground: List<DivBackground>,
        newFocusedBackground: List<DivBackground>,
        oldFocusedBackground: List<DivBackground>,
        resolver: ExpressionResolver,
        divView: Div2View,
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

        view.applyFocusedBackground(underlay, overlay, newBackground, newFocusedBackground, resolver, divView)

        if (newBackground.all { it.isConstant() } && newFocusedBackground.all { it.isConstant() }) {
            return
        }

        val callback = { _: Any ->
            view.applyFocusedBackground(underlay, overlay, newBackground, newFocusedBackground, resolver, divView)
        }
        addBackgroundSubscriptions(newBackground, resolver, subscriber, callback)
        addBackgroundSubscriptions(newFocusedBackground, resolver, subscriber, callback)
    }

    private fun View.applyFocusedBackground(
        underlay: Drawable?,
        overlay: Drawable?,
        defaultBackgroundList: List<DivBackground>,
        focusedBackgroundList: List<DivBackground>,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        val metrics = resources.displayMetrics

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
                newFocusedDivBackground.toDrawable(this, underlay, overlay, divView)
            )

            if (defaultBackgroundList.isNotEmpty() || underlay != null || overlay != null) {
                stateList.addState(
                    StateSet.WILD_CARD,
                    newDefaultDivBackground.toDrawable(this, underlay, overlay, divView))
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
        view: View,
        underlay: Drawable?,
        overlay: Drawable?,
        divView: Div2View,
    ): Drawable? {
        if (this == null && underlay == null && overlay == null) {
            return null
        }

        val layers = mutableListOf<Drawable>()
        underlay?.let { layers.add(it) }
        this?.let { backgroundStates ->
            layers.addAll(backgroundStates.map { it.toDrawable(view, imageLoader, divView) })
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
