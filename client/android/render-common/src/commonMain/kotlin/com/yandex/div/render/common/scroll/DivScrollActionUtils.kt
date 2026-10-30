package com.yandex.div.render.common.scroll

import com.yandex.div.core.annotations.InternalApi
import kotlin.math.sign

@InternalApi
public object DivScrollActionUtils {
    public enum class Overflow {
        CLAMP,
        RING,
    }

    public fun targetItem(
        nearestItem: Int,
        itemCount: Int,
        step: Int,
        overflow: Overflow,
    ): Int {
        if (itemCount <= 0) return -1
        val index = nearestItem + step - step.sign
        return when (overflow) {
            Overflow.CLAMP -> index.coerceIn(0, itemCount - 1)
            Overflow.RING -> index.mod(itemCount)
        }
    }

    public fun nearestItemIndexOffset(
        childAnchor: Int,
        parentAnchor: Int,
        direction: Int,
        reversed: Boolean,
    ): Int? =
        when {
            childAnchor == parentAnchor -> if ((direction >= 0) != reversed) 1 else -1
            (childAnchor > parentAnchor) == (direction >= 0) -> 0
            else -> null
        }

    public fun targetOffset(
        scrollOffset: Int,
        scrollRange: Int,
        offset: Int,
        overflow: Overflow,
    ): Int {
        if (scrollRange <= 0) return 0
        val position = scrollOffset + offset
        return when (overflow) {
            Overflow.CLAMP -> position.coerceIn(0, scrollRange)
            Overflow.RING -> position.mod(scrollRange)
        }
    }
}
