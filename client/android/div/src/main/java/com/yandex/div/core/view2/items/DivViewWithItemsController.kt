package com.yandex.div.core.view2.items

import android.view.View
import com.yandex.div.core.DivViewFacade
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivSizeUnit

internal class DivViewWithItemsController private constructor(private val view: DivViewWithItems) {
    fun setCurrentItem(index: Int, animated: Boolean = true) {
        if (animated) {
            view.currentItem = index
        } else {
            view.setCurrentItemNoAnimation(index)
        }
    }

    fun changeCurrentItemByStep(overflow: String?, step: Int = 1, animated: Boolean = true) {
        val strategy = createStrategy(overflow)
        val index = when {
            step > 0 -> strategy.nextItem(step)
            step < 0 -> strategy.previousItem(-step)
            else -> return
        }
        setCurrentItem(index, animated)
    }

    fun scrollByOffset(overflow: String? = null, offset: Int, animated: Boolean = true) {
        if (offset == 0) return
        val strategy = createStrategy(overflow)
        view.scrollTo(strategy.positionAfterScrollBy(offset), animated = animated)
    }

    fun scrollTo(offset: Int, animated: Boolean = true) {
        view.scrollTo(offset, DivSizeUnit.DP, animated)
    }

    fun scrollToEnd(animated: Boolean = false) {
        view.scrollToTheEnd(animated)
    }

    fun scrollToStart(animated: Boolean = false) {
        setCurrentItem(0, animated)
    }

    @Throws(RuntimeException::class)
    fun scrollToItemId(id: String, animated: Boolean = false) {
        val indices = view.getIndicesOfItemWithId(id)

        val exception = when {
            indices.isEmpty() -> IllegalArgumentException("There are no items with id '$id'.")
            indices.size > 1 -> IllegalArgumentException("There are several items with id '$id'.")
            else -> null
        }
        exception?.let { throw RuntimeException("Failed to scroll to item with id.", it) }

        setCurrentItem(indices.first(), animated)
    }

    private fun createStrategy(overflow: String? = null): OverflowItemStrategy {
        return OverflowItemStrategy.create(
            overflow,
            view.currentItem,
            view.itemCount,
            view.scrollRange,
            view.scrollOffset,
            view.metrics
        )
    }

    companion object {

        fun create(
            id: String,
            view: DivViewFacade,
            resolver: ExpressionResolver,
            direction: Direction = Direction.NEXT
        ): DivViewWithItemsController? {
            val targetView = view.view.findViewWithTag<View>(id) ?: return null
            val viewWithItems = DivViewWithItems.create(targetView, resolver) { direction } ?: return null
            return DivViewWithItemsController(viewWithItems)
        }

        const val TAG = "DivViewWithItems"
    }
}
