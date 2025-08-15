package com.yandex.div.core.view2.items

import android.net.Uri
import com.yandex.div.core.DivViewFacade
import com.yandex.div.internal.KAssert
import com.yandex.div.json.expressions.ExpressionResolver

private const val AUTHORITY_SET_CURRENT_ITEM = "set_current_item"
private const val AUTHORITY_NEXT_ITEM = "set_next_item"
private const val AUTHORITY_PREVIOUS_ITEM = "set_previous_item"
private const val AUTHORITY_SCROLL_FORWARD = "scroll_forward"
private const val AUTHORITY_SCROLL_BACKWARD = "scroll_backward"
private const val AUTHORITY_SCROLL_TO_POSITION = "scroll_to_position"
private const val AUTHORITY_SCROLL_TO_END = "scroll_to_end"
private const val AUTHORITY_SCROLL_TO_START = "scroll_to_start"

private const val PARAM_ANIMATED = "animated"
private const val PARAM_ITEM = "item"
private const val PARAM_ID = "id"
private const val PARAM_STEP = "step"
private const val PARAM_OVERFLOW = "overflow"

/**
 * Action handler for handling change of current item.
 */
internal object DivItemChangeActionHandler {

    @JvmStatic
    fun canHandle(authority: String?): Boolean {
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
    fun handleAction(uri: Uri, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        val id = uri.getQueryParameter(PARAM_ID)
        if (id == null) {
            KAssert.fail { "$PARAM_ID param is required to set item" }
            return false
        }
        val authority = uri.authority
        val viewController =
            DivViewWithItemsController.create(id, view, resolver, direction(authority))
                ?: return false
        val animated = uri.getQueryParameter(PARAM_ANIMATED)?.toBoolean() ?: true
        return when (authority) {
            AUTHORITY_SET_CURRENT_ITEM -> handleSetCurrentItem(uri, animated, viewController)
            AUTHORITY_NEXT_ITEM -> handleNextItem(uri, animated, viewController)
            AUTHORITY_PREVIOUS_ITEM -> handlePreviousItem(uri, animated, viewController)
            AUTHORITY_SCROLL_BACKWARD -> handleScrollBackward(uri, animated, viewController)
            AUTHORITY_SCROLL_FORWARD -> handleScrollForward(uri, animated, viewController)
            AUTHORITY_SCROLL_TO_POSITION -> handleScrollTo(uri, animated, viewController)
            AUTHORITY_SCROLL_TO_END -> handleScrollToTheEnd(animated, viewController)
            AUTHORITY_SCROLL_TO_START -> handleScrollToTheStart(animated, viewController)
            else -> false
        }
    }

    private fun handleSetCurrentItem(
        uri: Uri,
        animated: Boolean,
        viewController: DivViewWithItemsController
    ): Boolean {
        val rawItem = uri.getQueryParameter(PARAM_ITEM)
        if (rawItem == null) {
            KAssert.fail { "$PARAM_ITEM is required to set current item" }
            return false
        }
        val item = try {
            rawItem.toInt()
        } catch (e: NumberFormatException) {
            KAssert.fail { "$rawItem is not a number" }
            return false
        }
        viewController.setCurrentItem(item, animated = animated)
        return true
    }

    private fun handleNextItem(
        uri: Uri,
        animated: Boolean,
        viewController: DivViewWithItemsController
    ): Boolean {
        return withOverflowAndStep(uri) { overflow, step ->
            viewController.changeCurrentItemByStep(overflow, step, animated = animated)
        }
    }

    private fun handlePreviousItem(
        uri: Uri,
        animated: Boolean,
        viewController: DivViewWithItemsController
    ): Boolean {
        return withOverflowAndStep(uri) { overflow, step ->
            viewController.changeCurrentItemByStep(overflow, -step, animated = animated)
        }
    }

    private fun handleScrollForward(
        uri: Uri,
        animated: Boolean,
        viewController: DivViewWithItemsController
    ): Boolean {
        return withOverflowAndStep(uri) { overflow, offset ->
            viewController.scrollByOffset(overflow, offset, animated = animated)
        }
    }

    private fun handleScrollBackward(
        uri: Uri,
        animated: Boolean,
        viewController: DivViewWithItemsController
    ): Boolean {
        return withOverflowAndStep(uri) { overflow, step ->
            viewController.scrollByOffset(overflow, -step, animated = animated)
        }
    }

    private fun handleScrollTo(
        uri: Uri,
        animated: Boolean,
        viewController: DivViewWithItemsController
    ): Boolean {
        val step = uri.getStepParam()
        viewController.scrollTo(step, animated = animated)
        return true
    }

    private fun handleScrollToTheEnd(
        animated: Boolean,
        viewController: DivViewWithItemsController
    ): Boolean {
        viewController.scrollToEnd(animated = animated)
        return true
    }

    private fun handleScrollToTheStart(
        animated: Boolean,
        viewController: DivViewWithItemsController
    ): Boolean {
        viewController.scrollToStart(animated = animated)
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

    private inline fun withOverflowAndStep(
        uri: Uri,
        actionOnViewController: (String?, Int) -> Unit
    ): Boolean {
        val step = uri.getStepParam()
        val overflow = uri.getQueryParameter(PARAM_OVERFLOW)
        actionOnViewController(overflow, step)
        return true
    }

    private fun direction(authority: String?): Direction {
        return when (authority) {
            AUTHORITY_PREVIOUS_ITEM -> Direction.PREVIOUS
            AUTHORITY_NEXT_ITEM -> Direction.NEXT
            else -> Direction.NEXT
        }
    }
}
