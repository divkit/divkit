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
    abstract fun nextItem(step: Int = 1): Int

    /**
     * Index of previous item
     */
    abstract fun previousItem(step: Int = 1): Int

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
        override fun nextItem(step: Int) =
            checkItemCount { min(currentItem + step, itemCount - 1) }

        override fun previousItem(step: Int) =
            checkItemCount { max(0, currentItem - step) }
    }

    /**
     * Implementation of [OverflowItemStrategy] that cycles over items when overflowed.
     */
    internal class Ring(private val currentItem: Int, private val itemCount: Int) : OverflowItemStrategy(itemCount) {
        override fun nextItem(step: Int) = checkItemCount { (currentItem + step) % itemCount }

        override fun previousItem(step: Int) =
            checkItemCount { (currentItem - step).mod(itemCount) }
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
