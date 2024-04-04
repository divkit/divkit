package com.yandex.div.core.view2.animations

import com.yandex.div.core.view2.divs.isWrapContainer
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
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
    ): Boolean {
        if (old == null) {
            return false
        }
        val oldState = old.states.find { it.stateId == stateId } ?: return false
        val newState = new.states.find { it.stateId == stateId } ?: return false

        return areDivsReplaceable(oldState.div, newState.div, oldResolver, newResolver)
    }

    fun areDivsReplaceable(
        old: Div?,
        new: Div?,
        oldResolver: ExpressionResolver,
        newResolver: ExpressionResolver,
    ): Boolean {
        if (old?.javaClass != new?.javaClass) {
            return false
        }
        if (old == null || new == null || old === new) {
            return true
        }
        return areValuesReplaceable(old.value(), new.value(), oldResolver, newResolver) && areChildrenReplaceable(
            extractChildren(old, oldResolver),
            extractChildren(new, newResolver),
            oldResolver,
            newResolver
        )
    }

    fun areValuesReplaceable(
        old: DivBase,
        new: DivBase,
        oldResolver: ExpressionResolver,
        newResolver: ExpressionResolver,
    ): Boolean {
        if (old.id != null && new.id != null && old.id != new.id && (old.hasTransitions() || new.hasTransitions())) {
            return false
        }
        if (old is DivCustom && new is DivCustom && old.customType != new.customType) {
            return false
        }
        if (old is DivContainer && new is DivContainer) {
            if (old.isOverlap(oldResolver) != new.isOverlap(newResolver)) {
                return false
            }
            if (old.isWrapContainer(oldResolver) != new.isWrapContainer(newResolver)) {
                return false
            }
        }
        return true
    }

    fun areChildrenReplaceable(
        oldChildren: List<Div>,
        newChildren: List<Div>,
        oldResolver: ExpressionResolver,
        newResolver: ExpressionResolver,
    ): Boolean {
        if (oldChildren.size != newChildren.size) {
            return false
        }

        return oldChildren.zip(newChildren).all {
            areDivsReplaceable(it.first, it.second, oldResolver, newResolver)
        }
    }

    private fun extractChildren(div: Div, resolver: ExpressionResolver): List<Div> {
        return when (div) {
            is Div.Container -> div.value.buildItems(resolver)
            is Div.Grid -> div.value.nonNullItems
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
        }
    }

    private fun DivBase.hasTransitions(): Boolean {
        return transitionIn != null || transitionOut != null || transitionChange != null
    }

    private fun DivContainer.isOverlap(resolver: ExpressionResolver) =
        orientation.evaluate(resolver) == DivContainer.Orientation.OVERLAP
}
