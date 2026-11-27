package com.yandex.div.compose.actions

/**
 * Handles application-specific actions and observes all enabled actions.
 *
 * Implement this interface to handle application-specific actions.
 *
 * @see com.yandex.div.compose.DivConfiguration
 */
public interface DivExternalActionHandler {

    /**
     * Called when an action that does not handled by DivKit is triggered.
     *
     * DivKit handles actions with `typed` parameter and actions with `url` that starts with
     * `div-action:` only.
     */
    public fun handle(context: DivActionHandlingContext, action: DivActionData): Unit = Unit

    /**
     * Called when a custom action (action with `"type": "custom"`) is triggered.
     */
    public fun handleCustomAction(
        context: DivActionHandlingContext,
        action: DivCustomActionData,
    ): Unit = Unit

    /**
     * Called for every enabled action before DivKit or the application handles it.
     */
    public fun onActionTriggered(
        context: DivActionHandlingContext,
        action: DivActionData,
    ): Unit = Unit
}
