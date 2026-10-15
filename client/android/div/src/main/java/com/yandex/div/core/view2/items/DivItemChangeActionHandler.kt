package com.yandex.div.core.view2.items

import android.net.Uri
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.actions.DivActionTypedScrollHandler
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.actions.DivScrollActionParser
import com.yandex.div.json.expressions.ExpressionResolver

private const val AUTHORITY_NEXT_ITEM = "set_next_item"
private const val AUTHORITY_PREVIOUS_ITEM = "set_previous_item"
private const val AUTHORITY_SCROLL_FORWARD = "scroll_forward"
private const val AUTHORITY_SCROLL_BACKWARD = "scroll_backward"
private const val AUTHORITY_SCROLL_TO_POSITION = "scroll_to_position"

private const val PARAM_ANIMATED = "animated"
private const val PARAM_ID = "id"
private const val PARAM_STEP = "step"
private const val PARAM_OVERFLOW = "overflow"

/**
 * Action handler for handling change of current item.
 */
internal object DivItemChangeActionHandler {

    @JvmStatic
    fun canHandle(authority: String?): Boolean {
        return DivScrollActionParser.matches(authority) || when (authority) {
            AUTHORITY_NEXT_ITEM,
            AUTHORITY_SCROLL_BACKWARD,
            AUTHORITY_SCROLL_FORWARD,
            AUTHORITY_SCROLL_TO_POSITION,
            AUTHORITY_PREVIOUS_ITEM -> true

            else -> false
        }
    }

    @JvmStatic
    fun handleAction(uri: Uri, scopeId: String?, view: DivViewFacade): Boolean {
        if (DivScrollActionParser.matches(uri.authority)) {
            val action = DivScrollActionParser.parse(uri) { message -> KAssert.fail { message } } ?: return false
            return DivActionTypedScrollHandler.handleScrollTo(action, scopeId, view, ExpressionResolver.EMPTY)
        }
        val id = uri.getQueryParameter(PARAM_ID)
        if (id == null) {
            KAssert.fail { "$PARAM_ID param is required to set item" }
            return false
        }
        val authority = uri.authority ?: return false
        val viewController = DivViewWithItemsController.create(id, scopeId, view, authority) ?: return false
        val animated = uri.getQueryParameter(PARAM_ANIMATED)?.toBoolean() ?: true
        return when (authority) {
            AUTHORITY_NEXT_ITEM -> handleNextItem(uri, animated, viewController)
            AUTHORITY_PREVIOUS_ITEM -> handlePreviousItem(uri, animated, viewController)
            AUTHORITY_SCROLL_BACKWARD -> handleScrollBackward(uri, animated, viewController)
            AUTHORITY_SCROLL_FORWARD -> handleScrollForward(uri, animated, viewController)
            AUTHORITY_SCROLL_TO_POSITION -> handleScrollTo(uri, animated, viewController)
            else -> false
        }
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
        viewController.scrollTo(step, animated)
        return true
    }

    private fun Uri.getStepParam(default: Int = 1): Int {
        val rawStep = getQueryParameter(PARAM_STEP) ?: return default
        return try {
            rawStep.toInt()
        } catch (_: NumberFormatException) {
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
}
