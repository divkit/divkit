package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.Visibility
import com.yandex.div.R
import com.yandex.div.core.actions.logError
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.tooltip.DivTooltipController
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.observeEdgeInsets
import com.yandex.div.core.util.observeSize
import com.yandex.div.core.util.observeTransform
import com.yandex.div.core.view.onPreDrawListener
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivAccessibilityBinder
import com.yandex.div.core.view2.animations.DivTransitionHandler.ChangeType
import com.yandex.div.core.view2.animations.allowsTransitionsOnVisibilityChange
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivBase
import com.yandex.div2.DivInput
import com.yandex.div2.DivSize
import com.yandex.div2.DivSwitch
import com.yandex.div2.DivVisibility
import javax.inject.Inject

@DivScope
internal class DivBaseBinder @Inject constructor(
    private val divBackgroundBinder: DivBackgroundBinder,
    private val tooltipController: DivTooltipController,
    private val divFocusBinder: DivFocusBinder,
    private val divAccessibilityBinder: DivAccessibilityBinder,
) {
    fun bindView(context: BindingContext, view: View, div: Div, oldDiv: Div?) {
        @Suppress("UNCHECKED_CAST")
        (view as DivHolderView<Div>).let {
            it.closeAllSubscription()
            it.div = div
            it.bindingContext = context
        }
        view.bind(context, div.value(), oldDiv?.value())
    }

    private fun View.bind(bindingContext: BindingContext, div: DivBase, oldDiv: DivBase?) {
        val resolver = bindingContext.expressionResolver
        val divView = bindingContext.divView
        val subscriber = expressionSubscriber

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultFocusHighlightEnabled = false
        }

        bindId(divView, div, oldDiv)
        bindLayoutParams(div, oldDiv, resolver, subscriber)
        bindLayoutProvider(bindingContext, div, oldDiv)
        bindAccessibility(div, oldDiv, resolver, subscriber)
        bindAlpha(div, oldDiv, resolver, subscriber)

        bindBackground(bindingContext, div, oldDiv, subscriber)
        bindBorder(bindingContext, div)
        bindPaddings(div, oldDiv, resolver, subscriber)

        bindNextFocus(divView, div, oldDiv, resolver, subscriber)
        bindFocusActions(bindingContext, div.focus?.onFocus, div.focus?.onBlur)
        bindVisibility(divView, div, oldDiv, resolver, subscriber)
        bindTransform(div, oldDiv, resolver, subscriber)

        div.tooltips?.let { tooltipController.mapTooltip(this, it) }

        applyFocusableState(div)
    }

    //region Id

    internal fun bindId(divView: Div2View, target: View, id: String?) {
        val viewId = if (id == null) View.NO_ID else divView.viewComponent.viewIdProvider.getViewId(id)
        target.applyId(id, viewId)
    }

    private fun View.bindId(divView: Div2View, newDiv: DivBase, oldDiv: DivBase?) {
        if (newDiv.id == oldDiv?.id) {
            return
        }

        val viewId = divView.viewComponent.viewIdProvider.getViewId(newDiv.id)
        applyId(newDiv.id, viewId)
    }

    //endregion

    //region Layout Params

    internal fun bindLayoutParams(
        target: View,
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        target.bindLayoutParams(newDiv, oldDiv, resolver, subscriber)
    }

    private fun View.bindLayoutParams(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (layoutParams == null) {
            KAssert.fail { "LayoutParams should be initialized before view binding" }
            layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        bindWidth(newDiv, oldDiv, resolver, subscriber)
        bindHeight(newDiv, oldDiv, resolver, subscriber)
        bindMargins(newDiv, oldDiv, resolver, subscriber)
        bindAlignment(newDiv, oldDiv, resolver, subscriber)
    }

    private fun View.bindWidth(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (newDiv.width.equalsToConstant(oldDiv?.width)) {
            return
        }

        applyWidth(newDiv, resolver)
        applyHorizontalWeightValue(newDiv.width.getWeight(resolver))
        applyMinWidth(newDiv.width.minSize, resolver)
        applyMaxWidth(newDiv.width.maxSize, resolver)

        if (newDiv.width.isConstant()) {
            return
        }

        subscriber.observeSize(newDiv.width, resolver) {
            applyWidth(newDiv, resolver)
            applyHorizontalWeightValue(newDiv.width.getWeight(resolver))
            applyMinWidth(newDiv.width.minSize, resolver)
            applyMaxWidth(newDiv.width.maxSize, resolver)
        }
    }

    private fun View.bindHeight(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (newDiv.height.equalsToConstant(oldDiv?.height)) {
            return
        }

        applyHeight(newDiv, resolver)
        applyVerticalWeightValue(newDiv.height.getWeight(resolver))
        applyMinHeight(newDiv.height.minSize, resolver)
        applyMaxHeight(newDiv.height.maxSize, resolver)

        if (newDiv.height.isConstant()) {
            return
        }

        subscriber.observeSize(newDiv.height, resolver) {
            applyHeight(newDiv, resolver)
            applyVerticalWeightValue(newDiv.height.getWeight(resolver))
            applyMinHeight(newDiv.height.minSize, resolver)
            applyMaxHeight(newDiv.height.maxSize, resolver)
        }
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

    private fun View.bindLayoutProvider(
        bindingContext: BindingContext,
        newDiv: DivBase,
        oldDiv: DivBase?,
    ) {
        val divView = bindingContext.divView
        val data = divView.divData ?: return
        val layoutProvider = newDiv.layoutProvider ?: return
        if (layoutProvider.widthVariableName.equals(oldDiv?.layoutProvider?.widthVariableName)
            && layoutProvider.heightVariableName.equals(oldDiv?.layoutProvider?.heightVariableName)) {
            return
        }
        if (oldDiv?.layoutProvider != null) {
            clearLayoutProviderVariables()
        }
        val widthVariable = layoutProvider.widthVariableName
        val heightVariable = layoutProvider.heightVariableName
        if (widthVariable.isNullOrEmpty() && heightVariable.isNullOrEmpty()) {
            divView.logError(Throwable("Neither width_variable_name nor height_variable_name found."))
            return
        }

        val variablesHolder = divView.variablesHolders[data] ?: DivLayoutProviderVariablesHolder()
            .apply { observeDivData(data, bindingContext) }
            .also { divView.variablesHolders[data] = it }

        val listener = View.OnLayoutChangeListener { _, left, top, right, bottom,
                                                     oldLeft, oldTop, oldRight, oldBottom ->
            val metrics = resources.displayMetrics
            updateSizeVariable(
                divView,
                metrics,
                widthVariable,
                variablesHolder,
                left,
                right,
                oldLeft,
                oldRight,
                bindingContext.expressionResolver,
            )
            updateSizeVariable(
                divView,
                metrics,
                heightVariable,
                variablesHolder,
                top,
                bottom,
                oldTop,
                oldBottom,
                bindingContext.expressionResolver
            )
        }
        if (width > 0 || height > 0) {
            listener.onLayoutChange(this, left, top, right, bottom, 0, 0, 0, 0)
        }
        addOnLayoutChangeListener(listener)
        setTag(R.id.div_layout_provider_listener_id, listener)
        if (divView.clearVariablesListener != null) return

        val clearVariablesListener = onPreDrawListener {
            variablesHolder.clear()
            divView.layoutSizes.forEach { (resolver, variableMap) ->
                variableMap.forEach {
                    VariableMutationHandler.setVariable(divView, it.key, it.value.toString(), resolver)
                }
            }
            divView.layoutSizes.clear()
            true
        }
        divView.clearVariablesListener = clearVariablesListener
        divView.viewTreeObserver.addOnPreDrawListener(clearVariablesListener)
    }

    private fun updateSizeVariable(
        divView: Div2View,
        metrics: DisplayMetrics,
        variableName: String?,
        variablesHolder: DivLayoutProviderVariablesHolder,
        start: Int,
        end: Int,
        oldStart: Int,
        oldEnd: Int,
        resolver: ExpressionResolver,
    ) {
        if (variableName.isNullOrEmpty()) return

        val size = end - start
        if (size == oldEnd - oldStart) return

        if (variablesHolder.contains(variableName)) {
            divView.logError(Throwable(
                "Size subscriber affects original view size. Relayout was prevented."))
            return
        }

        divView.layoutSizes.getOrPut(resolver) { mutableMapOf() }.also {
            it[variableName] = size.pxToDp(metrics)
        }
    }

    private fun View.clearLayoutProviderVariables() {
        removeOnLayoutChangeListener(getTag(R.id.div_layout_provider_listener_id) as? View.OnLayoutChangeListener)
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

    //endregion

    //region Border

    private fun View.bindBorder(
        context: BindingContext,
        newDiv: DivBase,
    ) {
        divFocusBinder.bindDivBorder(this, context, newDiv.focus?.border, newDiv.border)
    }

    //endregion

    //region Background

    internal fun bindBackground(
        context: BindingContext,
        target: View,
        newDiv: DivBase,
        oldDiv: DivBase?,
        subscriber: ExpressionSubscriber,
        additionalLayer: Drawable? = null
    ) {
        target.bindBackground(context, newDiv, oldDiv, subscriber, additionalLayer)
        target.bindPaddings(newDiv, oldDiv, context.expressionResolver, subscriber)
    }

    private fun View.bindBackground(
        context: BindingContext,
        newDiv: DivBase,
        oldDiv: DivBase?,
        subscriber: ExpressionSubscriber,
        additionalLayer: Drawable? = null
    ) {
        divBackgroundBinder.bindBackground(
            context,
            this,
            newDiv.background,
            oldDiv?.background,
            newDiv.focus?.background,
            oldDiv?.focus?.background,
            subscriber,
            additionalLayer
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accessibilityTraversalBefore = viewIdProvider.getViewId(id)
            }
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
        context: BindingContext,
        onFocus: List<DivAction>?,
        onBlur: List<DivAction>?
    ) {
        divFocusBinder.bindDivFocusActions(this, context, onFocus, onBlur)
    }

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
        if (newDiv.transform.equalsToConstant(oldDiv?.transform)) {
            return
        }

        applyTransform(newDiv.transform, resolver)

        if (newDiv.transform.isConstant()) {
            return
        }

        subscriber.observeTransform(newDiv.transform, resolver) {
            applyTransform(newDiv.transform, resolver)
        }
    }

    //endregion

    private fun View.applyFocusableState(div: DivBase) {
        if (div is DivInput || div is DivSwitch) return
        isFocusable = div.focus != null
    }

    private val DivSize.minSize get() = (this as? DivSize.WrapContent)?.value?.minSize

    private val DivSize.maxSize get() = (this as? DivSize.WrapContent)?.value?.maxSize
}
