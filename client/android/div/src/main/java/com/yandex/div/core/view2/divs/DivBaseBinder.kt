package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.Visibility
import com.yandex.div.core.Disposable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.tooltip.DivTooltipController
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivAccessibilityBinder
import com.yandex.div.core.view2.DivAccessibilityVisitor
import com.yandex.div.core.view2.animations.DivTransitionHandler.ChangeType
import com.yandex.div.core.view2.animations.allowsTransitionsOnVisibilityChange
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivBackground
import com.yandex.div2.DivBase
import com.yandex.div2.DivBorder
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivFocus
import com.yandex.div2.DivSize
import com.yandex.div2.DivVisibility
import javax.inject.Inject

@DivScope
internal class DivBaseBinder @Inject constructor(
    private val divBackgroundBinder: DivBackgroundBinder,
    private val tooltipController: DivTooltipController,
    private val extensionController: DivExtensionController,
    private val divFocusBinder: DivFocusBinder,
    private val divAccessibilityBinder: DivAccessibilityBinder,
) {
    fun bindView(view: View, div: DivBase, oldDiv: DivBase?, divView: Div2View) {
        val resolver = divView.expressionResolver
        val subscriber = view.expressionSubscriber

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.defaultFocusHighlightEnabled = false
        }

        div.id.applyIfNotEquals(oldDiv?.id) {
            bindId(view, divView, div.id)
        }

        bindLayoutParams(view, div, oldDiv, resolver)
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

        view.observeVisibility(div, resolver, subscriber, divView)
        view.observeTransform(div, resolver, subscriber)
    }

    fun bindId(view: View, divView: Div2View, id: String?) {
        val viewId = divView.viewComponent.viewIdProvider.getViewId(id)
        view.applyId(id, viewId)
    }

    fun bindLayoutParams(view: View, div: DivBase, oldDiv: DivBase?, resolver: ExpressionResolver) {
        if (view.layoutParams == null) {
            KAssert.fail { "LayoutParams should be initialized before view binding" }
            view.layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val subscriber = view.expressionSubscriber

        view.observeWidth(div, resolver, subscriber)
        view.observeHeight(div, resolver, subscriber)
        view.observeAlignment(div, oldDiv, resolver, subscriber)

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
        callback: (Long) -> Unit) {
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
        val width = div.width
        applyHorizontalWeightValue(width.getWeight(resolver))

        applyMinWidth(width.minSize, resolver)
        applyMaxWidth(width.maxSize, resolver)

        when (width) {
            is DivSize.Fixed -> {
                subscriber.addSubscription(width.value.value.observe(resolver) { applyWidth(div, resolver) })
                subscriber.addSubscription(width.value.unit.observe(resolver) { applyWidth(div, resolver) })
            }
            is DivSize.MatchParent -> {
                width.value.weight?.observe(resolver) {
                    applyHorizontalWeightValue(it.toFloat())
                }?.let { subscriber.addSubscription(it) }
            }
            is DivSize.WrapContent -> {
                subscriber.addSubscription(width.minSize?.value?.observe(resolver) {
                    applyMinWidth(width.minSize, resolver)
                } ?: Disposable.NULL)
                subscriber.addSubscription(width.minSize?.unit?.observe(resolver) {
                    applyMinWidth(width.minSize, resolver)
                } ?: Disposable.NULL)

                subscriber.addSubscription(width.maxSize?.value?.observe(resolver) {
                    applyMaxWidth(width.maxSize, resolver)
                } ?: Disposable.NULL)
                subscriber.addSubscription(width.maxSize?.unit?.observe(resolver) {
                    applyMaxWidth(width.maxSize, resolver)
                } ?: Disposable.NULL)
            }
            else -> Unit
        }
    }

    private fun View.observeHeight(div: DivBase, resolver: ExpressionResolver, subscriber: ExpressionSubscriber) {
        applyHeight(div, resolver)
        val height = div.height
        applyVerticalWeightValue(height.getWeight(resolver))

        applyMinHeight(height.minSize, resolver)
        applyMaxHeight(height.maxSize, resolver)

        when (height) {
            is DivSize.Fixed -> {
                subscriber.addSubscription(height.value.value.observe(resolver) { applyHeight(div, resolver) })
                subscriber.addSubscription(height.value.unit.observe(resolver) { applyHeight(div, resolver) })
            }
            is DivSize.MatchParent -> {
                height.value.weight?.observe(resolver) {
                    applyVerticalWeightValue(it.toFloat())
                }?.let { subscriber.addSubscription(it) }
            }
            is DivSize.WrapContent -> {
                subscriber.addSubscription(height.minSize?.value?.observe(resolver) {
                    applyMinHeight(height.minSize, resolver)
                } ?: Disposable.NULL)
                subscriber.addSubscription(height.minSize?.unit?.observe(resolver) {
                    applyMinHeight(height.minSize, resolver)
                } ?: Disposable.NULL)

                subscriber.addSubscription(height.maxSize?.value?.observe(resolver) {
                    applyMaxHeight(height.maxSize, resolver)
                } ?: Disposable.NULL)
                subscriber.addSubscription(height.maxSize?.unit?.observe(resolver) {
                    applyMaxHeight(height.maxSize, resolver)
                } ?: Disposable.NULL)
            }
            else -> Unit
        }
    }

    private fun View.observeAlignment(
        div: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val horizontalAlignment = div.alignmentHorizontal
        val verticalAlignment = div.alignmentVertical

        listOf(horizontalAlignment, verticalAlignment)
            .applyIfNotEquals(listOf(oldDiv?.alignmentHorizontal, oldDiv?.alignmentVertical)) {
                applyAlignment(horizontalAlignment?.evaluate(resolver), verticalAlignment?.evaluate(resolver))
            }

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

        applyDescriptionAndHint(accessibility.description?.evaluate(resolver), accessibility.hint?.evaluate(resolver))
        subscriber.addSubscription(
            accessibility.description?.observe(resolver) { description ->
                applyDescriptionAndHint(description, accessibility.hint?.evaluate(resolver))
            } ?: Disposable.NULL
        )
        subscriber.addSubscription(
                accessibility.hint?.observe(resolver) { hint ->
                    applyDescriptionAndHint(accessibility.description?.evaluate(resolver), hint)
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
        val accessibilityVisitor = DivAccessibilityVisitor(divAccessibilityBinder, divView, resolver)
        subscriber.addSubscription(
                accessibility.mode.observe(resolver) {
                    accessibilityVisitor.visitViewTree(this)
                })

        accessibility.type?.let { type ->
            divAccessibilityBinder.bindType(this, type)
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
    ) = divBackgroundBinder.bindBackground(this, divView, defaultBackgroundList, focusedBackgroundList, resolver,
        subscriber, additionalLayer)

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

    private fun View.observeVisibility(
        div: DivBase,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        divView: Div2View,
    ) {
        subscriber.addSubscription(div.visibility.observeAndGet(resolver) { visibility ->
            if (visibility != DivVisibility.GONE) {
                applyTransform(div, resolver)
            }
            applyVisibility(div, visibility, divView, resolver)
        })
    }

    private fun View.applyVisibility(
        div: DivBase,
        divVisibility: DivVisibility,
        divView: Div2View,
        resolver: ExpressionResolver
    ) {
        val divTransitionHandler = divView.divTransitionHandler

        val newVisibility = when (divVisibility) {
            DivVisibility.VISIBLE -> View.VISIBLE
            DivVisibility.INVISIBLE -> View.INVISIBLE
            DivVisibility.GONE -> View.GONE
        }

        if (divVisibility != DivVisibility.VISIBLE) {
            clearAnimation()
        }

        var transition: Transition? = null

        var visibility = visibility

        if (div.transitionTriggers?.allowsTransitionsOnVisibilityChange() != false) {
            val currentChange = divTransitionHandler
                .getLastChange(this)

            currentChange?.let { visibility = it.new }

            val transitionBuilder = divView.viewComponent.transitionBuilder

            transition = when {
                (visibility == View.INVISIBLE || visibility == View.GONE)
                    && newVisibility == View.VISIBLE -> {
                    transitionBuilder.createAndroidTransition(
                        div.transitionIn,
                        Visibility.MODE_IN,
                        resolver
                    )
                }
                (newVisibility == View.INVISIBLE || newVisibility == View.GONE)
                    && visibility == View.VISIBLE -> {
                    transitionBuilder.createAndroidTransition(
                        div.transitionOut,
                        Visibility.MODE_OUT,
                        resolver
                    )
                }
                else -> {
                    if (currentChange != null) TransitionManager.endTransitions(divView)
                    null
                }
            }

            transition?.addTarget(this)
        }

        if (transition != null) {
            divTransitionHandler.putTransition(
                transition,
                this,
                ChangeType.Visibility(newVisibility)
            )
        } else {
            this.visibility = newVisibility
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

    private fun View.bindFocusActions(
        divView: Div2View,
        resolver: ExpressionResolver,
        onFocus: List<DivAction>?,
        onBlur: List<DivAction>?
    ) {
        divFocusBinder.bindDivFocusActions(this, divView, resolver, onFocus, onBlur)
    }

    private val DivSize.minSize get() = (this as? DivSize.WrapContent)?.value?.minSize

    private val DivSize.maxSize get() = (this as? DivSize.WrapContent)?.value?.maxSize
}
