package com.yandex.div.core.view2.items

import android.net.Uri
import android.util.DisplayMetrics
import android.view.View
import com.yandex.div.core.DivViewFacade
import com.yandex.div.internal.KAssert
import com.yandex.div2.DivSizeUnit

private const val AUTHORITY_SET_CURRENT_ITEM = "set_current_item"
private const val AUTHORITY_NEXT_ITEM = "set_next_item"
private const val AUTHORITY_PREVIOUS_ITEM = "set_previous_item"
private const val AUTHORITY_SCROLL_FORWARD = "scroll_forward"
private const val AUTHORITY_SCROLL_BACKWARD = "scroll_backward"
private const val AUTHORITY_SCROLL_TO_POSITION = "scroll_to_position"
private const val AUTHORITY_SCROLL_TO_END = "scroll_to_end"
private const val AUTHORITY_SCROLL_TO_START = "scroll_to_start"

private const val PARAM_ITEM = "item"
private const val PARAM_ID = "id"
private const val PARAM_STEP = "step"
private const val PARAM_OVERFLOW = "overflow"

/**
 * Action handler for handling change of current item.
 */
internal object DivItemChangeActionHandler {

    @JvmStatic
    fun canHandle(authority: String): Boolean {
        return when (authority) {
            AUTHORITY_SET_CURRENT_ITEM,
            AUTHORITY_NEXT_ITEM,
            AUTHORITY_SCROLL_BACKWARD,
            AUTHORITY_SCROLL_FORWARD,
            AUTHORITY_SCROLL_TO_START,
            AUTHORITY_SCROLL_TO_END,
            AUTHORITY_SCROLL_TO_POSITION,
            AUTHORITY_PREVIOUS_ITEM -> true
            else -> false
        }
    }

    @JvmStatic
    fun handleAction(uri: Uri, view: DivViewFacade): Boolean {
        val id = uri.getQueryParameter(PARAM_ID)
        if (id == null) {
            KAssert.fail { "$PARAM_ID param is required to set item" }
            return false
        }
        val targetView = view.view.findViewWithTag<View>(id) ?: return false
        val authority = uri.authority
        val viewWithItems = DivViewWithItems.create(targetView, view.expressionResolver) { direction(authority) } ?: return false
        return when (authority) {
            AUTHORITY_SET_CURRENT_ITEM ->
                handleSetCurrentItem(uri, viewWithItems)

            AUTHORITY_NEXT_ITEM ->
                handleNextItem(uri, viewWithItems)

            AUTHORITY_PREVIOUS_ITEM ->
                handlePreviousItem(uri, viewWithItems)

            AUTHORITY_SCROLL_BACKWARD ->
                handleScrollBackward(uri, viewWithItems)

            AUTHORITY_SCROLL_FORWARD ->
                handleScrollForward(uri, viewWithItems)

            AUTHORITY_SCROLL_TO_POSITION ->
                handleScrollTo(uri, viewWithItems)

            AUTHORITY_SCROLL_TO_END ->
                handleScrollToTheEnd(viewWithItems)

            AUTHORITY_SCROLL_TO_START ->
                handleScrollToTheStart(viewWithItems)

            else -> false
        }
    }

    private fun handleSetCurrentItem(uri: Uri, view: DivViewWithItems): Boolean {
        val item = uri.getQueryParameter(PARAM_ITEM)
        if (item == null) {
            KAssert.fail { "$PARAM_ITEM is required to set current item" }
            return false
        }
        return try {
            view.currentItem = item.toInt()
            true
        } catch (e: NumberFormatException) {
            KAssert.fail { "$item is not a number" }
            false
        }
    }

    private fun handleNextItem(uri: Uri, view: DivViewWithItems): Boolean {
        return handleItemNavigation(uri, view) { strategy, step ->
            view.currentItem = strategy.nextItem(step)
        }
    }

    private fun handlePreviousItem(uri: Uri, view: DivViewWithItems): Boolean {
        return handleItemNavigation(uri, view) { strategy, step ->
            view.currentItem = strategy.previousItem(step)
        }
    }


    private fun handleScrollForward(uri: Uri, view: DivViewWithItems): Boolean {
        return handleItemNavigation(uri, view) { strategy, step ->
            view.scrollTo(strategy.positionAfterScrollBy(step))
        }
    }

    private fun handleScrollBackward(uri: Uri, view: DivViewWithItems): Boolean {
        return handleItemNavigation(uri, view) { strategy, step ->
            view.scrollTo(strategy.positionAfterScrollBy(-step))
        }
    }

    private fun handleScrollTo(uri: Uri, view: DivViewWithItems): Boolean {
        return handleItemNavigation(uri, view) { _, step ->
            view.scrollTo(step, DivSizeUnit.DP)
        }
    }

    private fun handleScrollToTheEnd(view: DivViewWithItems): Boolean {
        view.scrollToTheEnd()
        return true
    }

    private fun handleScrollToTheStart(view: DivViewWithItems): Boolean {
        view.currentItem = 0
        return true
    }

    private inline fun handleItemNavigation(
        uri: Uri,
        view: DivViewWithItems,
        navigate: (strategy: OverflowItemStrategy, step: Int) -> Unit
    ): Boolean {
        val strategy = overflowStrategy(uri, view.currentItem, view.itemCount, view.scrollRange,
            view.scrollOffset, view.metrics)
        val step = uri.getStepParam()
        navigate(strategy, step)
        return true
    }

    private fun Uri.getStepParam(default: Int = 1): Int {
        val rawStep = getQueryParameter(PARAM_STEP) ?: return default
        return try {
            rawStep.toInt()
        } catch (e: NumberFormatException) {
            KAssert.fail { "$rawStep is not a number" }
            default
        }
    }
}

private fun overflowStrategy(uri: Uri, currentItem: Int, itemCount: Int, scrollRange: Int,
                             scrollOffset: Int, metrics: DisplayMetrics): OverflowItemStrategy {
    val overflow = uri.getQueryParameter(PARAM_OVERFLOW)
    return OverflowItemStrategy.create(overflow, currentItem, itemCount, scrollRange, scrollOffset, metrics)
}

private fun direction(authority: String?): Direction {
    return when (authority) {
        AUTHORITY_PREVIOUS_ITEM -> Direction.PREVIOUS
        AUTHORITY_NEXT_ITEM -> Direction.NEXT
        else -> Direction.NEXT
    }
}
