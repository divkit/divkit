package com.yandex.div.internal.actions

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div2.DivActionScrollBy
import kotlin.math.sign

@InternalApi
object DivScrollActionUtils {
    fun targetItem(nearestItem: Int, itemCount: Int, step: Int, overflow: DivActionScrollBy.Overflow): Int {
        if (itemCount <= 0) return -1
        val index = nearestItem + step - step.sign
        return when (overflow) {
            DivActionScrollBy.Overflow.CLAMP -> index.coerceIn(0, itemCount - 1)
            DivActionScrollBy.Overflow.RING -> index.mod(itemCount)
        }
    }

    fun nearestItemIndexOffset(childAnchor: Int, parentAnchor: Int, direction: Int, reversed: Boolean): Int? =
        when {
            childAnchor == parentAnchor -> if ((direction >= 0) != reversed) 1 else -1
            (childAnchor > parentAnchor) == (direction >= 0) -> 0
            else -> null
        }

    fun targetOffset(scrollOffset: Int, scrollRange: Int, offset: Int, overflow: DivActionScrollBy.Overflow): Int {
        if (scrollRange <= 0) return 0
        val position = scrollOffset + offset
        return when (overflow) {
            DivActionScrollBy.Overflow.CLAMP -> position.coerceIn(0, scrollRange)
            DivActionScrollBy.Overflow.RING -> position.mod(scrollRange)
        }
    }
}
