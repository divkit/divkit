package com.yandex.div.core.view2.items

import com.yandex.div.internal.KAssert
import kotlin.math.max
import kotlin.math.min

private const val OVERFLOW_CLAMP = "clamp"
private const val OVERFLOW_RING = "ring"

/**
 * Strategy to get next or previous item index based on current item.
 */
internal sealed class OverflowItemStrategy(private val itemCount: Int) {

    /**
     * Index of next item
     */
    abstract val nextItem: Int

    /**
     * Index of previous item
     */
    abstract val previousItem: Int

    protected inline fun checkItemCount(block: () -> Int): Int {
        return when {
            itemCount <= 0 -> -1
            else -> block()
        }
    }

    /**
     * Implementation of [OverflowItemStrategy] that clamps next and previous when overflowed.
     */
    internal class Clamp(private val currentItem: Int, private val itemCount: Int) : OverflowItemStrategy(itemCount) {
        override val nextItem: Int
            get() = checkItemCount { min(currentItem + 1, itemCount - 1) }

        override val previousItem: Int
            get() = checkItemCount { max(0, currentItem - 1) }
    }

    /**
     * Implementation of [OverflowItemStrategy] that cycles over items when overflowed.
     */
    internal class Ring(private val currentItem: Int, private val itemCount: Int) : OverflowItemStrategy(itemCount) {
        override val nextItem: Int
            get() = checkItemCount { (currentItem + 1) % itemCount }

        override val previousItem: Int
            get() = checkItemCount { (itemCount + (currentItem - 1)) % itemCount }
    }

    internal companion object {
        internal fun create(overflow: String?, currentItem: Int, itemCount: Int): OverflowItemStrategy {
            return when (overflow) {
                null, OVERFLOW_CLAMP -> Clamp(currentItem, itemCount)
                OVERFLOW_RING -> Ring(currentItem, itemCount)
                else -> {
                    KAssert.fail { "Unsupported overflow $overflow" }
                    Clamp(currentItem, itemCount)
                }
            }
        }
    }
}
