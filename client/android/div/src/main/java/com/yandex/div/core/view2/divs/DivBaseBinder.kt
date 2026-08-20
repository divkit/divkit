package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.Visibility
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.tooltip.DivTooltipController
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.observeEdgeInsets
import com.yandex.div.core.util.observeTransform
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivAccessibilityBinder
import com.yandex.div.core.view2.animations.DivTransitionHandler.ChangeType
import com.yandex.div.core.view2.animations.allowsTransitionsOnVisibilityChange
import com.yandex.div.core.view2.animations.suppressOverlayVisibilityRestore
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.util.compareWith
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivAction
import com.yandex.div2.DivBase
import com.yandex.div2.DivInput
import com.yandex.div2.DivSwitch
import com.yandex.div2.DivVisibility
import javax.inject.Inject

@DivScope
internal class DivBaseBinder @Inject constructor(
    private val divBackgroundBinder: DivBackgroundBinder,
    private val tooltipController: DivTooltipController,
    private val divFocusBinder: DivFocusBinder,
    private val divAccessibilityBinder: DivAccessibilityBinder,
    private val layoutParamsBinder: DivLayoutParamsBinder,
) {

    fun bindView(view: View, divBlock: DivBlock, oldDivBlock: DivBlock?, divView: Div2View) {
        @Suppress("UNCHECKED_CAST")
        (view as DivHolderView<DivBlock>).let {
            it.closeAllSubscription()
            it.divBlock = divBlock
        }
        view.bind(divBlock, oldDivBlock, divView)
    }

    private fun View.bind(divBlock: DivBlock, oldDivBlock: DivBlock?, divView: Div2View) {
        val subscriber = expressionSubscriber

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultFocusHighlightEnabled = false
        }

        val div = divBlock.div.value()
        val oldDiv = oldDivBlock?.div?.value()
        val resolver = divBlock.expressionResolver

        bindId(divView, div, oldDiv)
        bindLayoutParams(div, oldDiv, resolver, divView, subscriber)
        bindMargins(div, oldDiv, resolver, subscriber)
        bindAlignment(div, oldDiv, resolver, subscriber)
        divView.dataComponent.layoutProviderBinder
            .bind(this, div.layoutProvider, oldDiv?.layoutProvider, resolver, divView)
        bindAccessibility(div, oldDiv, resolver, subscriber)
        bindAlpha(div, oldDiv, resolver, subscriber)

        bindBackground(div, oldDiv, resolver, divView, subscriber, true, backgroundUnderlay, null)
        bindBorder(div, resolver, divView)
        bindPaddings(div, oldDiv, resolver, subscriber)

        bindNextFocus(divView, div, oldDiv, resolver, subscriber)
        bindFocusActions(div.focus?.onFocus, div.focus?.onBlur, resolver, divView)
        bindVisibility(divView, div, oldDiv, resolver, subscriber)
        bindTransform(div, oldDiv, resolver, subscriber)

        div.tooltips?.let { tooltipController.mapTooltip(this, it) }

        applyFocusableState(div)
    }

    //region Id

    internal fun bindId(divView: Div2View, target: View, id: String?) {
        val viewId = divView.viewComponent.viewIdProvider.getViewId(id)
        target.applyId(id, viewId)
    }

    private fun View.bindId(divView: Div2View, newDiv: DivBase, oldDiv: DivBase?) {
        if (newDiv.id == oldDiv?.id) {
            return
        }

        val viewId = divView.viewComponent.viewIdProvider.getViewId(newDiv.id)
        applyId(newDiv.id, viewId)
    }

    private fun View.applyId(divId: String?, viewId: Int = View.NO_ID) {
        tag = divId
        id = viewId
    }

    //endregion

    //region Layout Params

    private fun View.bindLayoutParams(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        divView: Div2View,
        subscriber: ExpressionSubscriber
    ) {
        layoutParamsBinder.bindLayoutParams(this, newDiv, oldDiv, resolver, divView, subscriber)
    }

    private fun View.bindMargins(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (newDiv.margins.equalsToConstant(oldDiv?.margins)) {
            return
        }

        applyMargins(newDiv.margins, resolver)

        if (newDiv.margins.isConstant()) {
            return
        }

        subscriber.observeEdgeInsets(newDiv.margins, resolver)  {
            applyMargins(newDiv.margins, resolver)
        }
    }

    private fun View.bindAlignment(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (newDiv.alignmentHorizontal.equalsToConstant(oldDiv?.alignmentHorizontal)
            && newDiv.alignmentVertical.equalsToConstant(oldDiv?.alignmentVertical)) {
            return
        }

        applyAlignment(
            newDiv.alignmentHorizontal?.evaluate(resolver),
            newDiv.alignmentVertical?.evaluate(resolver)
        )

        if (newDiv.alignmentHorizontal.isConstantOrNull() && newDiv.alignmentVertical.isConstantOrNull()) {
            return
        }

        val callback = { _: Any ->
            applyAlignment(
                newDiv.alignmentHorizontal?.evaluate(resolver),
                newDiv.alignmentVertical?.evaluate(resolver)
            )
        }
        subscriber.addSubscription(newDiv.alignmentHorizontal?.observe(resolver, callback))
        subscriber.addSubscription(newDiv.alignmentVertical?.observe(resolver, callback))
    }

    //endregion

    //region Paddings

    private fun View.bindPaddings(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (this is DivPagerView || newDiv.paddings.equalsToConstant(oldDiv?.paddings)) {
            return
        }

        applyPaddings(newDiv.paddings, resolver)

        if (newDiv.paddings.isConstant()) {
            return
        }

        subscriber.observeEdgeInsets(newDiv.paddings, resolver) {
            applyPaddings(newDiv.paddings, resolver)
        }
    }

    //endregion

    //region Accessibility

    private fun View.bindAccessibility(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) = divAccessibilityBinder.bind(this, newDiv, oldDiv, resolver, subscriber)

    //endregion

    //region Alpha

    private fun View.bindAlpha(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (newDiv.alpha.equalsToConstant(oldDiv?.alpha)) {
            return
        }

        applyAlpha(newDiv.alpha.evaluate(resolver))

        if (newDiv.alpha.isConstant()) {
            return
        }

        subscriber.addSubscription(
            newDiv.alpha.observe(resolver) { alpha -> applyAlpha(alpha) }
        )
    }

    private fun View.applyAlpha(alpha: Double) {
        this.alpha = alpha.toFloat()
        (this as? DivBorderSupports)?.invalidateBorder()
    }

    //endregion

    //region Border

    private fun View.bindBorder(
        newDiv: DivBase,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        divFocusBinder.bindDivBorder(
            this,
            newDiv.focus?.border,
            newDiv.border,
            resolver,
            divView
        )
    }

    //endregion

    //region Background

    internal fun bindBackground(
        target: View,
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        divView: Div2View,
        subscriber: ExpressionSubscriber,
        underlay: Drawable?,
        overlay: Drawable?,
    ) {
        target.bindBackground(newDiv, oldDiv, resolver, divView, subscriber, false, underlay, overlay)
        target.bindPaddings(newDiv, oldDiv, resolver, subscriber)
    }

    private fun View.bindBackground(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        divView: Div2View,
        subscriber: ExpressionSubscriber,
        checkEquality: Boolean = true,
        underlay: Drawable? = null,
        overlay: Drawable? = null,
    ) {
        val newBackground = newDiv.background ?: emptyList()
        val oldBackground = oldDiv?.background ?: emptyList()
        val newFocusBackground = newDiv.focus?.background ?: emptyList()
        val oldFocusBackground = oldDiv?.focus?.background ?: emptyList()

        if (checkEquality &&
            underlay == boundBackgroundUnderlay &&
            newBackground.compareWith(oldBackground) { left, right -> left.equalsToConstant(right) } &&
            newFocusBackground.compareWith(oldFocusBackground) { left, right -> left.equalsToConstant(right) }) {
            return
        }

        divBackgroundBinder.bindBackground(
            this,
            newBackground,
            oldBackground,
            newFocusBackground,
            oldFocusBackground,
            resolver,
            divView,
            subscriber,
            underlay,
            overlay,
        )
    }

    //endregion

    //region Focus

    private fun View.bindNextFocus(
        divView: Div2View,
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val viewIdProvider = divView.viewComponent.viewIdProvider
        bindNextFocusId(newDiv.focus?.nextFocusIds?.forward, oldDiv?.focus?.nextFocusIds?.forward, resolver, subscriber) { id ->
            nextFocusForwardId = viewIdProvider.getViewId(id)
            accessibilityTraversalBefore = viewIdProvider.getViewId(id)
        }
        bindNextFocusId(newDiv.focus?.nextFocusIds?.left, oldDiv?.focus?.nextFocusIds?.left, resolver, subscriber) { id ->
            nextFocusLeftId = viewIdProvider.getViewId(id)
        }
        bindNextFocusId(newDiv.focus?.nextFocusIds?.right, oldDiv?.focus?.nextFocusIds?.right, resolver, subscriber) { id ->
            nextFocusRightId = viewIdProvider.getViewId(id)
        }
        bindNextFocusId(newDiv.focus?.nextFocusIds?.up, oldDiv?.focus?.nextFocusIds?.up, resolver, subscriber) { id ->
            nextFocusUpId = viewIdProvider.getViewId(id)
        }
        bindNextFocusId(newDiv.focus?.nextFocusIds?.down, oldDiv?.focus?.nextFocusIds?.down, resolver, subscriber) { id ->
            nextFocusDownId = viewIdProvider.getViewId(id)
        }
    }

    private inline fun bindNextFocusId(
        newFocusId: Expression<String>?,
        oldFocusId: Expression<String>?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        crossinline applyNextFocusId: (String?) -> Unit
    ) {
        if (newFocusId.equalsToConstant(oldFocusId)) {
            return
        }

        applyNextFocusId(newFocusId?.evaluate(resolver))

        if (newFocusId.isConstantOrNull()) {
            return
        }

        subscriber.addSubscription(
            newFocusId?.observe(resolver) { id -> applyNextFocusId(id) }
        )
    }

    private fun View.bindFocusActions(
        onFocus: List<DivAction>?,
        onBlur: List<DivAction>?,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) = divFocusBinder.bindDivFocusActions(this, onFocus, onBlur, resolver, divView)

    //endregion

    //region Visibility

    private fun View.bindVisibility(
        divView: Div2View,
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (newDiv.visibility.equalsToConstant(oldDiv?.visibility)) {
            return
        }

        applyVisibility(divView, newDiv, resolver, oldDiv == null)

        if (newDiv.visibility.isConstant()) {
            return
        }

        subscriber.addSubscription(
            newDiv.visibility.observe(resolver) { applyVisibility(divView, newDiv, resolver, false) }
        )
    }

    private fun View.applyVisibility(
        divView: Div2View,
        newDiv: DivBase,
        resolver: ExpressionResolver,
        firstApply: Boolean
    ) {
        val divTransitionHandler = divView.divTransitionHandler

        val newVisibility = when (newDiv.visibility.evaluate(resolver)) {
            DivVisibility.VISIBLE -> View.VISIBLE
            DivVisibility.INVISIBLE -> View.INVISIBLE
            DivVisibility.GONE -> View.GONE
        }

        if (newVisibility != View.VISIBLE) {
            clearAnimation()
        }

        var transition: Transition? = null

        var visibility = visibility

        if (newDiv.transitionTriggers?.allowsTransitionsOnVisibilityChange() != false) {
            val currentChange = divTransitionHandler
                .getLastChange(this)

            currentChange?.let { visibility = it.new }

            val transitionBuilder = divView.viewComponent.transitionBuilder

            transition = when {
                (visibility == View.INVISIBLE || visibility == View.GONE)
                    && newVisibility == View.VISIBLE -> {
                    transitionBuilder.createAndroidTransition(
                        newDiv.transitionIn,
                        Visibility.MODE_IN,
                        resolver
                    )
                }
                (newVisibility == View.INVISIBLE || newVisibility == View.GONE)
                    && visibility == View.VISIBLE  && !firstApply -> {
                    transitionBuilder.createAndroidTransition(
                        newDiv.transitionOut,
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
            if (newVisibility != View.VISIBLE) {
                suppressOverlayVisibilityRestore()
            }
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

    //endregion

    //region Transform

    private fun View.bindTransform(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val transform = newDiv.transform

        // Always track size changes to keep pivot correct after layout changes (including rebind).
        if (transform != null) {
            subscriber.addSubscription(observeTransformPivot(transform, resolver))
        }

        if (newDiv.transform.equalsToConstant(oldDiv?.transform)) {
            return
        }

        applyTransform(newDiv, resolver)

        if (transform == null || newDiv.transform.isConstant()) {
            return
        }

        subscriber.observeTransform(newDiv.transform, resolver) {
            applyTransform(newDiv, resolver)
        }
    }

    //endregion

    private fun View.applyFocusableState(div: DivBase) {
        if (div is DivInput || div is DivSwitch) return
        isFocusable = div.focus != null
        isFocusableInTouchMode = div.focus != null
    }
}
