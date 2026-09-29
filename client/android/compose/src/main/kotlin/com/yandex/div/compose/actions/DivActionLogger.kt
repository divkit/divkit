package com.yandex.div.compose.actions

/**
 * Observes every enabled action before DivKit or the application handles it.
 *
 * This includes typed actions, `div-action://` URLs, custom actions and visibility actions.
 * Configure the logger with [com.yandex.div.compose.DivConfiguration.Builder.actionLogger].
 */
fun interface DivActionLogger {
    fun logAction(context: DivActionHandlingContext, event: DivActionEvent)
}
