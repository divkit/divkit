package com.yandex.div.core.view2.animations

import com.yandex.div.core.view2.divs.isWrapContainer
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
        resolver: ExpressionResolver
    ): Boolean {
        if (old == null) {
            return false
        }
        val oldState = old.states.find { it.stateId == stateId } ?: return false
        val newState = new.states.find { it.stateId == stateId } ?: return false

        return areDivsReplaceable(oldState.div, newState.div, resolver)
    }

    fun areDivsReplaceable(
        old: DivContainer?,
        new: DivContainer?,
        resolver: ExpressionResolver
    ): Boolean {
        if (old?.javaClass != new?.javaClass) {
            return false
        }
        if (old == null || new == null || old === new) {
            return true
        }
        return areValuesReplaceable(old, new, resolver) && areChildrenReplaceable(
            old.items,
            new.items,
            resolver
        )
    }

    fun areDivsReplaceable(old: Div?, new: Div?, resolver: ExpressionResolver): Boolean {
        if (old?.javaClass != new?.javaClass) {
            return false
        }
        if (old == null || new == null || old === new) {
            return true
        }
        return areValuesReplaceable(old.value(), new.value(), resolver) && areChildrenReplaceable(
            extractChildren(old),
            extractChildren(new),
            resolver
        )
    }

    private fun areValuesReplaceable(
        old: DivBase,
        new: DivBase,
        resolver: ExpressionResolver
    ): Boolean {
        if (old.id != null && new.id != null && old.id != new.id) {
            return false
        }
        if (old is DivCustom && new is DivCustom && old.customType != new.customType) {
            return false
        }
        if (
            old is DivContainer && new is DivContainer
            && old.isWrapContainer(resolver) != new.isWrapContainer(resolver)
        ) {
            return false
        }
        return true
    }

    private fun areChildrenReplaceable(
        oldChildren: List<Div>,
        newChildren: List<Div>,
        resolver: ExpressionResolver
    ): Boolean {
        if (oldChildren.size != newChildren.size) {
            return false
        }

        return oldChildren.zip(newChildren).all {
            areDivsReplaceable(it.first, it.second, resolver)
        }
    }

    private fun extractChildren(div: Div): List<Div> {
        return when (div) {
            is Div.Container -> div.value.items
            is Div.Grid -> div.value.items
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
}
