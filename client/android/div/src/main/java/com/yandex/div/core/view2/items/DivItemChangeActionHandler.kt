package com.yandex.div.core.view2.items

import android.net.Uri
import android.view.View
import com.yandex.div.core.DivViewFacade
import com.yandex.div.internal.KAssert

private const val AUTHORITY_SET_CURRENT_ITEM = "set_current_item"
private const val AUTHORITY_NEXT_ITEM = "set_next_item"
private const val AUTHORITY_PREVIOUS_ITEM = "set_previous_item"

private const val PARAM_ITEM = "item"
private const val PARAM_ID = "id"
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
        val viewWithItems = DivViewWithItems.create(targetView) { direction(authority) } ?: return false
        // TODO(MORDAANDROID-663): Enable navigation for PagingGallery
        if (!isItemNavigationSupported(viewWithItems)) {
            return false
        }
        return when (authority) {
            AUTHORITY_SET_CURRENT_ITEM ->
                handleSetCurrentItem(uri, viewWithItems)

            AUTHORITY_NEXT_ITEM ->
                handleNextItem(uri, viewWithItems)

            AUTHORITY_PREVIOUS_ITEM ->
                handlePreviousItem(uri, viewWithItems)

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
        return handleItemNavigation(uri, view) { strategy -> view.currentItem = strategy.nextItem }
    }

    private fun handlePreviousItem(uri: Uri, view: DivViewWithItems): Boolean {
        return handleItemNavigation(uri, view) { strategy -> view.currentItem = strategy.previousItem }
    }

    private inline fun handleItemNavigation(
        uri: Uri,
        view: DivViewWithItems,
        navigate: (strategy: OverflowItemStrategy) -> Unit
    ): Boolean {
        val strategy = overflowStrategy(uri, view.currentItem, view.itemCount)
        navigate(strategy)
        return true
    }

    private fun isItemNavigationSupported(viewWithItems: DivViewWithItems): Boolean {
        return when (viewWithItems) {
            is DivViewWithItems.PagingGallery -> false
            else -> true
        }
    }
}

private fun overflowStrategy(uri: Uri, currentItem: Int, itemCount: Int): OverflowItemStrategy {
    val overflow = uri.getQueryParameter(PARAM_OVERFLOW)
    return OverflowItemStrategy.create(overflow, currentItem, itemCount)
}

private fun direction(authority: String?): Direction {
    return when (authority) {
        AUTHORITY_PREVIOUS_ITEM -> Direction.PREVIOUS
        AUTHORITY_NEXT_ITEM -> Direction.NEXT
        else -> Direction.NEXT
    }
}
