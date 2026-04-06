package com.yandex.div.compose.actions

import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Handler for actions that DivKit does not handle internally.
 *
 * Implement this interface to handle application-specific actions.
 *
 * @see com.yandex.div.compose.DivComposeConfiguration
 */
@ExperimentalApi
interface DivExternalActionHandler {

    /**
     * Called when an action that does not handled by DivKit is triggered.
     *
     * DivKit handles actions with `typed` parameter and actions with `url` that starts with
     * `div-action:` only.
     */
    fun handle(context: DivActionHandlingContext, action: DivActionData) = Unit

    /**
     * Called when a custom action (action with `"type": "custom"`) is triggered.
     */
    fun handleCustomAction(context: DivActionHandlingContext, action: DivCustomActionData) = Unit
}
