package com.yandex.div.core.view2.items

import android.util.DisplayMetrics
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.actions.DivScrollActionUtils
import com.yandex.div2.DivActionScrollBy

private const val OVERFLOW_CLAMP = "clamp"
private const val OVERFLOW_RING = "ring"

/**
 * Strategy to get next or previous item index based on current item.
 */
internal sealed class OverflowItemStrategy {

    /**
     * Index of target item
     */
    abstract fun targetItem(step: Int): Int

    /**
     * Position in px after scroll
     */
    abstract fun positionAfterScrollBy(step: Int): Int

    /**
     * Implementation of [OverflowItemStrategy] that clamps next and previous when overflowed.
     */
    internal class Clamp(
        private val nearestItem: Int,
        private val itemCount: Int,
        private val scrollRange: Int,
        private val scrollOffset: Int,
        private val metrics: DisplayMetrics
    ) : OverflowItemStrategy() {

        override fun targetItem(step: Int): Int =
            DivScrollActionUtils.targetItem(nearestItem, itemCount, step, DivActionScrollBy.Overflow.CLAMP)

        override fun positionAfterScrollBy(step: Int): Int =
            DivScrollActionUtils.targetOffset(
                scrollOffset, scrollRange, step.dpToPx(metrics), DivActionScrollBy.Overflow.CLAMP,
            )
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
    ): OverflowItemStrategy() {

        override fun targetItem(step: Int): Int =
            DivScrollActionUtils.targetItem(nearestItem, itemCount, step, DivActionScrollBy.Overflow.RING)

        override fun positionAfterScrollBy(step: Int): Int =
            DivScrollActionUtils.targetOffset(
                scrollOffset, scrollRange, step.dpToPx(metrics), DivActionScrollBy.Overflow.RING,
            )
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
    }
}
