package com.yandex.div.core.view2.items

import androidx.annotation.VisibleForTesting
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.actions.findTargetView
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivSizeUnit

internal class DivViewWithItemsController @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) constructor(
    private val view: DivViewWithItems
) {

    fun setCurrentItem(index: Int, animated: Boolean) = view.setCurrentItem(index, animated)

    fun changeCurrentItemByStep(overflow: String?, step: Int, animated: Boolean) {
        if (step == 0) return
        val strategy = createStrategy(step, overflow)
        val index = strategy.targetItem(step)
        setCurrentItem(index, animated)
    }

    fun scrollByOffset(overflow: String?, offset: Int, animated: Boolean) {
        if (offset == 0) return
        val strategy = createStrategy(offset, overflow)
        view.scrollTo(strategy.positionAfterScrollBy(offset), animated)
    }

    fun scrollTo(offset: Int, animated: Boolean) = view.scrollTo(offset, animated, DivSizeUnit.DP)

    fun scrollToEnd(animated: Boolean) = view.scrollToTheEnd(animated)

    fun scrollToStart(animated: Boolean) = setCurrentItem(0, animated)

    @Throws(RuntimeException::class)
    fun scrollToItemId(id: String, animated: Boolean) {
        val indices = view.getIndicesOfItemWithId(id)

        val exception = when {
            indices.isEmpty() -> IllegalArgumentException("There are no items with id '$id'.")
            indices.size > 1 -> IllegalArgumentException("There are several items with id '$id'.")
            else -> null
        }
        exception?.let { throw RuntimeException("Failed to scroll to item with id.", it) }

        setCurrentItem(indices.first(), animated)
    }

    private fun createStrategy(step: Int, overflow: String?): OverflowItemStrategy {
        return OverflowItemStrategy.create(
            overflow,
            view.getNearestItem(ScrollDirection.from(step)),
            view.itemCount,
            view.scrollRange,
            view.scrollOffset,
            view.metrics
        )
    }

    companion object {

        fun create(
            id: String,
            scopeId: String?,
            view: DivViewFacade,
            actionType: String,
        ): DivViewWithItemsController? {
            val divView = view as? Div2View ?: return null
            val targetView = divView.findTargetView<DivScrollActionHolder>(id, actionType, scopeId) ?: return null
            val viewWithItems = DivViewWithItems.create(targetView) ?: return null
            return DivViewWithItemsController(viewWithItems)
        }
    }
}
