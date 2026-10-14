package com.yandex.div.core.view2.items

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.actions.findTargetView
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivSizeUnit

internal class DivViewWithItemsController(
    private val view: DivViewWithItems,
    private val animationsEnabled: Boolean,
) {

    fun setCurrentItem(index: Int, animated: Boolean, duration: Int? = null) =
        view.setCurrentItem(index, resolveAnimated(animated, duration), duration)

    fun changeCurrentItemByStep(overflow: String?, step: Int, animated: Boolean, duration: Int? = null) {
        if (step == 0) {
            return
        }
        val strategy = createStrategy(step, overflow)
        val index = strategy.targetItem(step)
        setCurrentItem(index, animated, duration)
    }

    fun scrollByOffset(
        overflow: String?,
        offset: Int,
        animated: Boolean,
        duration: Int? = null,
        cancelPreviousScroll: Boolean = true,
    ) {
        if (offset == 0) {
            return
        }
        val strategy = createStrategy(offset, overflow)
        view.scrollTo(
            strategy.positionAfterScrollBy(offset),
            resolveAnimated(animated, duration),
            duration = duration,
            cancelPreviousScroll = cancelPreviousScroll,
        )
    }

    fun scrollTo(offset: Int, animated: Boolean, duration: Int? = null) =
        view.scrollTo(offset, resolveAnimated(animated, duration), DivSizeUnit.DP, duration)

    fun scrollToEnd(animated: Boolean, duration: Int? = null) =
        view.scrollToTheEnd(resolveAnimated(animated, duration), duration)

    fun scrollToStart(animated: Boolean, duration: Int? = null) = setCurrentItem(0, animated, duration)

    @Throws(RuntimeException::class)
    fun scrollToItemId(id: String, animated: Boolean, duration: Int? = null) {
        val indices = view.getIndicesOfItemWithId(id)

        val exception = when {
            indices.isEmpty() -> IllegalArgumentException("There are no items with id '$id'.")
            indices.size > 1 -> IllegalArgumentException("There are several items with id '$id'.")
            else -> null
        }
        exception?.let { throw RuntimeException("Failed to scroll to item with id.", it) }

        setCurrentItem(indices.first(), animated, duration)
    }

    private fun resolveAnimated(animated: Boolean, duration: Int?) =
        animated && animationsEnabled && duration != 0

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
            return DivViewWithItemsController(viewWithItems, divView.div2Component.animationsEnabledController.isEnabled())
        }
    }
}
