package com.yandex.div.core.view2.divs

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.ExpressionSubscriber
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.tooltip.DivTooltipController
import com.yandex.div.core.util.KAssert
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivAccessibilityBinder
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.drawables.LinearGradientDrawable
import com.yandex.div.drawables.ScalingDrawable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBackground
import com.yandex.div2.DivBase
import com.yandex.div2.DivBorder
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivFocus
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivImageBackground
import com.yandex.div2.DivSize
import com.yandex.div2.DivSolidBackground
import com.yandex.div2.DivVisibility
import javax.inject.Inject


@DivScope
internal class DivBaseBinder @Inject constructor(
    private val imageLoader: DivImageLoader,
    private val tooltipController: DivTooltipController,
    private val extensionController: DivExtensionController,
    private val divFocusBinder: DivFocusBinder,
    private val divAccessibilityBinder: DivAccessibilityBinder,
) {
    fun bindView(view: View, div: DivBase, oldDiv: DivBase?, divView: Div2View) {
        val resolver = divView.expressionResolver
        val subscriber = view.expressionSubscriber

        div.id.applyIfNotEquals(oldDiv?.id) {
            val viewId = divView.viewComponent.viewIdProvider.getViewId(div.id)
            view.applyId(div.id, viewId)
        }

        bindLayoutParams(view, div, resolver)
        view.observePadding(div.paddings, resolver, subscriber)

        view.observeAccessibility(divView, div, resolver, subscriber)
        view.observeAlpha(div.alpha, resolver, subscriber)

        view.observeBackground(divView, div.background, div.focus?.background, resolver, subscriber)

        listOf(div.border, div.focus?.border)
            .applyIfNotEquals(listOf(oldDiv?.border, oldDiv?.focus?.border)) {
                view.bindBorder(divView, div.border, div.focus?.border, resolver)
            }

        div.focus?.nextFocusIds.applyIfNotEquals(oldDiv?.focus?.nextFocusIds) {
            view.observeNextViewId(divView, div.focus?.nextFocusIds, resolver, subscriber)
        }

        div.tooltips?.let {
            tooltipController.mapTooltip(view, it)
        }

        listOf(div.focus?.onFocus, div.focus?.onBlur)
            .applyIfNotEquals(listOf(oldDiv?.focus?.onFocus, oldDiv?.focus?.onBlur)) {
                view.bindFocusActions(divView, resolver, div.focus?.onFocus, div.focus?.onBlur)
            }

        // DivAccessibilityBinder is responsible for focus setup, so changing isFocusable only if binder is disabled
        if(!divAccessibilityBinder.enabled) {
            view.applyFocusableState(div)
        }
        view.observeVisibility(div, resolver, subscriber, divView)
        view.observeTransform(div, resolver, subscriber)
    }

    fun bindLayoutParams(view: View, div: DivBase, resolver: ExpressionResolver) {
        if (view.layoutParams == null) {
            KAssert.fail { "LayoutParams should be initialized before view binding" }
            view.layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val subscriber = view.expressionSubscriber

        view.observeWidth(div, resolver, subscriber)
        view.observeHeight(div, resolver, subscriber)
        view.observeAlignment(div.alignmentHorizontal, div.alignmentVertical, resolver, subscriber)

        view.observeMargins(div.margins, resolver, subscriber)
    }

    fun bindBackground(
        view: View,
        div: DivBase,
        divView: Div2View,
        resolver: ExpressionResolver,
        additionalLayer: Drawable?
    ) {
        view.observeBackground(
            divView,
            div.background,
            div.focus?.background,
            resolver,
            view.expressionSubscriber,
            additionalLayer
        )
        view.applyPaddings(div.paddings, resolver)
    }

    fun unbindExtensions(view: View, oldDiv: DivBase, divView: Div2View) {
        extensionController.unbindView(divView, view, oldDiv)
    }

    fun observeWidthAndHeightSubscription(
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        div: DivBase,
        callback: (Int) -> Unit) {
        if (div.width is DivSize.Fixed) {
            subscriber.addSubscription(
                (div.width.value() as DivFixedSize).value.observe(resolver, callback))
        }
        if (div.height is DivSize.Fixed) {
            subscriber.addSubscription(
                (div.height.value() as DivFixedSize).value.observe(resolver, callback))
        }
    }

    private fun View.observeWidth(div: DivBase, resolver: ExpressionResolver, subscriber: ExpressionSubscriber) {
        applyWidth(div, resolver)

        when (val width = div.width) {
            is DivSize.Fixed -> {
                subscriber.addSubscription(width.value.value.observe(resolver) { applyWidth(div, resolver) })
                subscriber.addSubscription(width.value.unit.observe(resolver) { applyWidth(div, resolver) })
            }
            is DivSize.MatchParent -> Unit
            else -> Unit
        }
    }

    private fun View.observeHeight(div: DivBase, resolver: ExpressionResolver, subscriber: ExpressionSubscriber) {
        applyHeight(div, resolver)

        when (val height = div.height) {
            is DivSize.Fixed -> {
                subscriber.addSubscription(height.value.value.observe(resolver) { applyHeight(div, resolver) })
                subscriber.addSubscription(height.value.unit.observe(resolver) { applyHeight(div, resolver) })
            }
            is DivSize.MatchParent -> Unit
            else -> Unit
        }
    }

    private fun View.observeAlignment(
        horizontalAlignment: Expression<DivAlignmentHorizontal>?,
        verticalAlignment: Expression<DivAlignmentVertical>?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        applyAlignment(horizontalAlignment?.evaluate(resolver), verticalAlignment?.evaluate(resolver))

        val callback = { _: Any ->
            applyAlignment(horizontalAlignment?.evaluate(resolver), verticalAlignment?.evaluate(resolver))
        }
        subscriber.addSubscription(horizontalAlignment?.observe(resolver, callback) ?: Disposable.NULL)
        subscriber.addSubscription(verticalAlignment?.observe(resolver, callback) ?: Disposable.NULL)
    }

    private fun View.observePadding(
        paddings: DivEdgeInsets,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val padding = if (this is DivPagerView) DivEdgeInsets() else paddings
        applyPaddings(padding, resolver)

        val callback = { _: Any -> applyPaddings(padding, resolver) }
        subscriber.addSubscription(padding.left.observe(resolver, callback))
        subscriber.addSubscription(padding.top.observe(resolver, callback))
        subscriber.addSubscription(padding.right.observe(resolver, callback))
        subscriber.addSubscription(padding.bottom.observe(resolver, callback))
    }

    private fun View.observeMargins(
        margins: DivEdgeInsets?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        applyMargins(margins, resolver)

        if (margins == null) return

        val callback = { _: Any -> applyMargins(margins, resolver) }
        subscriber.addSubscription(margins.left.observe(resolver, callback))
        subscriber.addSubscription(margins.top.observe(resolver, callback))
        subscriber.addSubscription(margins.right.observe(resolver, callback))
        subscriber.addSubscription(margins.bottom.observe(resolver, callback))
    }

    private fun View.observeAccessibility(
        divView: Div2View,
        div: DivBase,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val accessibility = div.accessibility
        val hint = accessibility.hint?.evaluate(resolver)

        applyDescriptionAndHint(accessibility.description?.evaluate(resolver), hint)
        subscriber.addSubscription(
            accessibility.description?.observe(resolver) { description ->
                applyDescriptionAndHint(description, hint)
            } ?: Disposable.NULL
        )

        applyAccessibilityStateDescription(accessibility.stateDescription?.evaluate(resolver))
        subscriber.addSubscription(
            accessibility.stateDescription?.observe(resolver) { description ->
                applyAccessibilityStateDescription(description)
            } ?: Disposable.NULL
        )

        divAccessibilityBinder.bindAccessibilityMode(
            this, divView, accessibility.mode.evaluate(resolver)
        )

        accessibility.type?.let { type ->
            divAccessibilityBinder.bindType(this, type.evaluate(resolver))
        } ?: divAccessibilityBinder.bindTypeAutomatically(this, div)
    }

    private fun View.observeAlpha(
        alphaExpr: Expression<Double>,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        subscriber.addSubscription(alphaExpr.observeAndGet(resolver) { alpha -> applyAlpha(alpha) })
    }

    private fun View.bindBorder(
        divView: Div2View,
        border: DivBorder,
        focusedBorder: DivBorder?,
        resolver: ExpressionResolver
    ) {
        divFocusBinder.bindDivBorder(this, divView, resolver, focusedBorder, border)
    }

    private fun View.observeBackground(
        divView: Div2View,
        defaultBackgroundList: List<DivBackground>?,
        focusedBackgroundList: List<DivBackground>?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        additionalLayer: Drawable? = null
    ) {
        val updateBackground = { drawable: Drawable? ->
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

        if (focusedBackgroundList == null) {
            val callback = { _: Any ->
                updateBackground(defaultBackgroundList.toDrawable(this, divView, resolver,
                    additionalLayer))
            }

            callback(Unit)

            addBackgroundSubscriptions(defaultBackgroundList, resolver, subscriber, callback)
        } else {
            val callback = { _: Any ->
                val stateList = StateListDrawable()

                stateList.addState(
                    intArrayOf(android.R.attr.state_focused),
                    focusedBackgroundList.toDrawable(this, divView, resolver, additionalLayer)
                )

                if (defaultBackgroundList != null || additionalLayer != null) {
                    stateList.addState(
                        StateSet.WILD_CARD,
                        defaultBackgroundList.toDrawable(this, divView, resolver, additionalLayer)
                    )
                }

                updateBackground(stateList)
            }

            callback(Unit)

            addBackgroundSubscriptions(focusedBackgroundList, resolver, subscriber, callback)
            addBackgroundSubscriptions(defaultBackgroundList, resolver, subscriber, callback)
        }
    }

    private fun View.observeNextViewId(
        divView: Div2View,
        nextFocusIds: DivFocus.NextFocusIds?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val viewIdProvider = divView.viewComponent.viewIdProvider
        if (nextFocusIds != null) {
            nextFocusIds.forward.let { forwardId ->
                if (forwardId != null) {
                    subscriber.addSubscription(
                        forwardId.observeAndGet(resolver) { id -> nextFocusForwardId = viewIdProvider.getViewId(id) }
                    )
                } else {
                    nextFocusForwardId = View.NO_ID
                }
            }

            nextFocusIds.up.let { upId ->
                if (upId != null) {
                    subscriber.addSubscription(
                        upId.observeAndGet(resolver) { id -> nextFocusUpId = viewIdProvider.getViewId(id) }
                    )
                } else {
                    nextFocusUpId = View.NO_ID
                }
            }

            nextFocusIds.right.let { rightId ->
                if (rightId != null) {
                    subscriber.addSubscription(
                        rightId.observeAndGet(resolver) { id -> nextFocusRightId = viewIdProvider.getViewId(id) }
                    )
                } else {
                    nextFocusRightId = View.NO_ID
                }
            }

            nextFocusIds.down.let { downId ->
                if (downId != null) {
                    subscriber.addSubscription(downId.observeAndGet(
                        resolver) { id -> nextFocusDownId = viewIdProvider.getViewId(id) }
                    )
                } else {
                    nextFocusDownId = View.NO_ID
                }
            }

            nextFocusIds.left.let { leftId ->
                if (leftId != null) {
                    subscriber.addSubscription(
                        leftId.observeAndGet(resolver) { id -> nextFocusLeftId = viewIdProvider.getViewId(id) }
                    )
                } else {
                    nextFocusLeftId = View.NO_ID
                }
            }
        } else {
            nextFocusForwardId = View.NO_ID
            nextFocusUpId = View.NO_ID
            nextFocusRightId = View.NO_ID
            nextFocusDownId = View.NO_ID
            nextFocusLeftId = View.NO_ID
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
                is DivSolidBackground -> {
                    subscriber.addSubscription(divBackground.color.observe(resolver, callback))
                }

                is DivLinearGradient -> {
                    subscriber.addSubscription(divBackground.angle.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.colors.observe(resolver, callback))
                }

                is DivImageBackground -> {
                    subscriber.addSubscription(divBackground.alpha.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.imageUrl.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.contentAlignmentHorizontal.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.contentAlignmentVertical.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.preloadRequired.observe(resolver, callback))
                    subscriber.addSubscription(divBackground.scale.observe(resolver, callback))
                }
            }
        }
    }

    private fun View.observeVisibility(
        div: DivBase,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        divView: Div2View,
    ) {
        applyVisibility(div.visibility.evaluate(resolver), divView)
        subscriber.addSubscription(div.visibility.observeAndGet(resolver) { visibility ->
            if (visibility != DivVisibility.GONE) {
                applyTransform(div, resolver)
            }
            applyVisibility(visibility, divView)
        })
    }

    private fun View.applyVisibility(
        divVisibility: DivVisibility,
        divView: Div2View,
    ) {
        visibility = when(divVisibility) {
            DivVisibility.VISIBLE -> View.VISIBLE
            DivVisibility.GONE -> View.GONE
            DivVisibility.INVISIBLE -> View.INVISIBLE
        }
        divView.trackChildrenVisibility()
    }

    private fun View.observeTransform(
        div: DivBase,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
    ) {
        div.transform.rotation?.observe(resolver) {
            applyTransform(div, resolver)
        }?.let { subscriber.addSubscription(it) }
    }

    private fun List<DivBackground>?.toDrawable(
        view: View,
        divView: Div2View,
        resolver: ExpressionResolver,
        additionalLayer: Drawable?
    ): Drawable? {
        additionalLayer?.mutate()

        this ?: return additionalLayer?.let { LayerDrawable(arrayOf(it)) }

        val listDrawable = mapNotNull {
            divBackgroundToDrawable(it, divView, view, resolver)?.mutate()
        }.toMutableList()
        additionalLayer?.let { listDrawable.add(it) }

        return if (listDrawable.isNotEmpty()) LayerDrawable(listDrawable.toTypedArray()) else null
    }

    private fun divBackgroundToDrawable(
        background: DivBackground,
        divView: Div2View,
        target: View,
        resolver: ExpressionResolver
    ): Drawable? {
        return when (val divBackground = background.value()) {
            is DivImageBackground -> getDivImageBackground(divBackground, divView, target, resolver)
            is DivLinearGradient -> {
                LinearGradientDrawable(
                    divBackground.angle.evaluate(resolver).toFloat(),
                    divBackground.colors.evaluate(resolver).toIntArray()
                )
            }
            is DivSolidBackground -> ColorDrawable(divBackground.color.evaluate(resolver))
            else -> null
        }
    }

    private fun getDivImageBackground(
        background: DivImageBackground,
        divView: Div2View,
        target: View,
        resolver: ExpressionResolver
    ): Drawable {
        val scaleDrawable = ScalingDrawable()

        val url = background.imageUrl.evaluate(resolver).toString()
        val loadReference = imageLoader.loadImage(url, object : DivIdLoggingImageDownloadCallback(divView) {
            @UiThread
            override fun onSuccess(cachedBitmap: CachedBitmap) {
                scaleDrawable.setBitmap(cachedBitmap.bitmap)
                scaleDrawable.alpha = (background.alpha.evaluate(resolver) * 255).toInt()
                scaleDrawable.customScaleType = background.scale.evaluate(resolver).toScaleType()
                scaleDrawable.alignmentHorizontal = background.contentAlignmentHorizontal.evaluate(resolver).toHorizontalAlignment()
                scaleDrawable.alignmentVertical = background.contentAlignmentVertical.evaluate(resolver).toVerticalAlignment()
            }
        })
        divView.addLoadReference(loadReference, target)

        return scaleDrawable
    }

    private fun View.bindFocusActions(
        divView: Div2View,
        resolver: ExpressionResolver,
        onFocus: List<DivAction>?,
        onBlur: List<DivAction>?
    ) {
        divFocusBinder.bindDivFocusActions(this, divView, resolver, onFocus, onBlur)
    }

    private fun View.applyFocusableState(div: DivBase) {
        isFocusable = div.focus != null
    }
}
