package com.yandex.div.compose.actions

import com.yandex.div.core.annotations.PublicApi

/**
 * Handler for custom DivKit actions (actions with `"type": "custom"`).
 *
 * Implement this interface to handle application-specific actions.
 *
 * @see com.yandex.div.compose.DivComposeConfiguration
 */
@PublicApi
interface DivCustomActionHandler {

    /**
     * Called when a custom action is triggered.
     */
    fun handle(context: DivActionHandlingContext, action: DivActionData)
}
