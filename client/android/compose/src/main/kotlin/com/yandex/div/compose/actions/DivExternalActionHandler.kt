package com.yandex.div.compose.actions

/**
 * Handles application-specific actions and observes all enabled actions.
 *
 * Implement this interface to handle application-specific actions.
 *
 * @see com.yandex.div.compose.DivConfiguration
 */
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

    /**
     * Called for every enabled action before DivKit or the application handles it.
     */
    fun onActionTriggered(context: DivActionHandlingContext, action: DivActionData) = Unit
}
