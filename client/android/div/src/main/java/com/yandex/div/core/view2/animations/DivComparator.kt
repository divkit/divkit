package com.yandex.div.core.view2.animations

import com.yandex.div.core.util.isWrapContainer
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.itemsToDivItemBuilderResult
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivData

internal object DivComparator {

    fun isDivDataReplaceable(
        old: DivData?,
        new: DivData,
        stateId: Long,
        oldResolver: ExpressionResolver,
        newResolver: ExpressionResolver,
        reporter: DivComparatorReporter? = null,
    ): Boolean {
        if (old == null) {
            reporter?.onComparisonNoOldData()
            return false
        }
        val oldState = old.states.find { it.stateId == stateId }
        val newState = new.states.find { it.stateId == stateId }

        if (oldState == null || newState == null) {
            reporter?.onComparisonNoState()
            return false
        }

        return areDivsReplaceable(oldState.div, newState.div, oldResolver, newResolver, reporter).also {
            if (it) reporter?.onComparisonSuccess()
        }
    }

    fun areDivsReplaceable(
        old: Div?,
        new: Div?,
        oldResolver: ExpressionResolver,
        newResolver: ExpressionResolver,
        reporter: DivComparatorReporter? = null,
    ): Boolean {
        if (old?.javaClass != new?.javaClass) {
            reporter?.onComparisonDifferentClasses()
            return false
        }
        if (old == null || new == null || old === new) {
            return true
        }
        return areValuesReplaceable(old.value(), new.value(), oldResolver, newResolver, reporter) &&
            areChildrenReplaceable(extractChildren(old, oldResolver), extractChildren(new, newResolver), reporter)
    }

    fun areValuesReplaceable(
        old: DivBase,
        new: DivBase,
        oldResolver: ExpressionResolver,
        newResolver: ExpressionResolver,
        reporter: DivComparatorReporter? = null,
    ): Boolean {
        if (old.id != null && new.id != null && old.id != new.id && (old.hasTransitions() || new.hasTransitions())) {
            reporter?.onComparisonDifferentIdsWithTransition()
            return false
        }
        if (old is DivCustom && new is DivCustom && old.customType != new.customType) {
            reporter?.onComparisonDifferentCustomTypes()
            return false
        }
        if (old is DivContainer && new is DivContainer) {
            if (old.isOverlap(oldResolver) != new.isOverlap(newResolver)) {
                reporter?.onComparisonDifferentOverlap()
                return false
            }
            if (old.isWrapContainer(oldResolver) != new.isWrapContainer(newResolver)) {
                reporter?.onComparisonDifferentWrap()
                return false
            }
        }
        return true
    }

    fun areChildrenReplaceable(
        oldChildren: List<DivItemBuilderResult>,
        newChildren: List<DivItemBuilderResult>,
        reporter: DivComparatorReporter? = null,
    ): Boolean {
        if (oldChildren.size != newChildren.size) {
            reporter?.onComparisonDifferentChildCount()
            return false
        }

        return oldChildren.zip(newChildren).all {
            areDivsReplaceable(
                it.first.div,
                it.second.div,
                it.first.expressionResolver,
                it.second.expressionResolver,
                reporter
            )
        }
    }

    private fun extractChildren(div: Div, resolver: ExpressionResolver): List<DivItemBuilderResult> {
        return when (div) {
            is Div.Container -> div.value.buildItems(resolver)
            is Div.Grid -> div.value.itemsToDivItemBuilderResult(resolver)
            is Div.Image -> emptyList()
            is Div.GifImage -> emptyList()
            is Div.Text -> emptyList()
            is Div.Separator -> emptyList()
            is Div.Gallery -> emptyList()
            is Div.Pager -> emptyList()
            is Div.Tabs -> emptyList()
            is Div.State -> emptyList()
            is Div.Custom -> emptyList()
            is Div.Input -> emptyList()
            is Div.Select -> emptyList()
            is Div.Indicator -> emptyList()
            is Div.Slider -> emptyList()
            is Div.Video -> emptyList()
            is Div.Switch -> emptyList()
        }
    }

    private fun DivBase.hasTransitions(): Boolean {
        return transitionIn != null || transitionOut != null || transitionChange != null
    }

    private fun DivContainer.isOverlap(resolver: ExpressionResolver) =
        orientation.evaluate(resolver) == DivContainer.Orientation.OVERLAP
}
