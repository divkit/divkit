package com.yandex.div.core.view2.items

import android.util.DisplayMetrics
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.internal.KAssert
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

private const val OVERFLOW_CLAMP = "clamp"
private const val OVERFLOW_RING = "ring"

/**
 * Strategy to get next or previous item index based on current item.
 */
internal sealed class OverflowItemStrategy(private val itemCount: Int) {

    /**
     * Index of target item
     */
    abstract fun targetItem(step: Int): Int

    /**
     * Position in px after scroll
     */
    abstract fun positionAfterScrollBy(step: Int): Int

    protected inline fun checkItemCount(block: () -> Int): Int {
        return when {
            itemCount <= 0 -> -1
            else -> block()
        }
    }

    /**
     * Implementation of [OverflowItemStrategy] that clamps next and previous when overflowed.
     */
    internal class Clamp(
        private val nearestItem: Int,
        private val itemCount: Int,
        private val scrollRange: Int,
        private val scrollOffset: Int,
        private val metrics: DisplayMetrics
    ) : OverflowItemStrategy(itemCount) {

        override fun targetItem(step: Int): Int {
            return checkItemCount { (nearestItem + step.delta).coerceIn(0, itemCount - 1) }
        }

        override fun positionAfterScrollBy(step: Int): Int =
            min(max(0, scrollOffset + step.dpToPx(metrics)), scrollRange)
    }

    /**
     * Implementation of [OverflowItemStrategy] that cycles over items when overflowed.
     */
    internal class Ring(
        private val nearestItem: Int,
        private val itemCount: Int,
        private val scrollRange: Int,
        private val scrollOffset: Int,
        private val metrics: DisplayMetrics
    ): OverflowItemStrategy(itemCount) {

        override fun targetItem(step: Int): Int {
            return checkItemCount { (nearestItem + step.delta).mod(itemCount) }
        }

        override fun positionAfterScrollBy(step: Int): Int {
            var position = (scrollOffset + step.dpToPx(metrics)) % scrollRange
            if (position < 0) {
                position += scrollRange
            }
            return position
        }
    }

    internal companion object {
        internal fun create(
            overflow: String?,
            nearestItem: Int,
            itemCount: Int,
            scrollRange: Int,
            scrollOffset: Int,
            metrics: DisplayMetrics
        ): OverflowItemStrategy {
            return when (overflow) {
                null, OVERFLOW_CLAMP -> Clamp(nearestItem, itemCount, scrollRange, scrollOffset, metrics)
                OVERFLOW_RING -> Ring(nearestItem, itemCount, scrollRange, scrollOffset, metrics)
                else -> {
                    KAssert.fail { "Unsupported overflow $overflow" }
                    Clamp(nearestItem, itemCount, scrollRange, scrollOffset, metrics)
                }
            }
        }

        private val Int.delta get() = sign * (abs(this) - 1)
    }
}
