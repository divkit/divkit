package com.yandex.div.core.view2.animations

import com.yandex.div.core.view2.divs.isWrapContainer
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivData
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivPager
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText

internal object DivComparator {

    fun isDivDataReplaceable(old: DivData?, new: DivData, stateId: Long,
                             resolver: ExpressionResolver): Boolean {
        if (old == null) {
            return false
        }
        val oldState = old.states.find { it.stateId == stateId } ?: return false
        val newState = new.states.find { it.stateId == stateId } ?: return false

        return areDivsReplaceable(oldState.div, newState.div, resolver)
    }

    fun areDivsReplaceable(old: Div?, new: Div?, resolver: ExpressionResolver): Boolean {
        return areDivsReplaceable(old?.value(), new?.value(), resolver)
    }

    fun areDivsReplaceable(old: DivBase?, new: DivBase?, resolver: ExpressionResolver): Boolean {
        if (old?.javaClass != new?.javaClass) {
            return false
        }
        if (old == null || new == null || old === new) {
            return true
        }
        if (old.id != null && new.id != null && old.id != new.id) {
            return false
        }
        if (old is DivCustom && new is DivCustom && old.customType != new.customType) {
            return false
        }
        if (old is DivContainer && new is DivContainer
            && old.isWrapContainer(resolver) != new.isWrapContainer(resolver)) {
            return false
        }

        val oldChildren = extractChildren(old, resolver)
        val newChildren = extractChildren(new, resolver)
        if (oldChildren.size != newChildren.size) {
            return false
        }

        return oldChildren.zip(newChildren)
            .map { areDivsReplaceable(it.first, it.second, resolver) }
            .reduceOrNull { a, b -> a && b } ?: true
    }

    private fun extractChildren(div: DivBase, resolver: ExpressionResolver): List<Div> {
        return when (div) {
            is DivImage -> emptyList()
            is DivGifImage -> emptyList()
            is DivText -> emptyList()
            is DivSeparator -> emptyList()
            is DivContainer -> div.items
            is DivGrid -> div.items
            is DivGallery -> emptyList()
            is DivPager -> emptyList()
            is DivTabs -> emptyList()
            is DivState -> emptyList()
            is DivCustom -> emptyList()
            else -> emptyList()
        }
    }
}
