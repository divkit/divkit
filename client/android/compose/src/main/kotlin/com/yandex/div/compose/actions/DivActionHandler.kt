package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div.internal.actions.isDivAction
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTyped
import javax.inject.Inject

@DivContextScope
internal class DivActionHandler @Inject constructor(
    private val externalActionHandler: DivExternalActionHandler,
    private val reporter: DivReporter,
    private val arrayActionsHandler: ArrayActionsHandler,
    private val dictSetValueActionHandler: DictSetValueActionHandler,
    private val setVariableActionHandler: SetVariableActionHandler,
    private val updateStructureActionHandler: UpdateStructureActionHandler
) {

    fun handle(context: DivActionHandlingContext, action: DivAction) {
        val expressionResolver = context.expressionResolver
        if (!action.isEnabled.evaluate(expressionResolver)) {
            return
        }

        action.typed?.let {
            handle(context = context, action = it, baseAction = action)
            return
        }

        val url = action.url?.evaluate(expressionResolver) ?: return
        if (url.isDivAction) {
            DivUntypedAction.parse(url)?.let {
                handle(context = context, action = it)
            }
        } else {
            externalActionHandler.handle(
                context = context,
                action = DivActionData(
                    id = action.logId.evaluate(expressionResolver),
                    payload = action.payload,
                    url = action.url?.evaluate(expressionResolver)
                )
            )
        }
    }

    private fun handle(
        context: DivActionHandlingContext,
        action: DivActionTyped,
        baseAction: DivAction
    ) {
        when (action) {
            is DivActionTyped.AnimatorStart -> notSupported()
            is DivActionTyped.AnimatorStop -> notSupported()
            is DivActionTyped.ArrayInsertValue ->
                arrayActionsHandler.handle(context, action.value)

            is DivActionTyped.ArrayRemoveValue ->
                arrayActionsHandler.handle(context, action.value)

            is DivActionTyped.ArraySetValue ->
                arrayActionsHandler.handle(context, action.value)

            is DivActionTyped.ClearFocus -> notSupported()
            is DivActionTyped.CopyToClipboard -> notSupported()
            is DivActionTyped.Custom ->
                externalActionHandler.handleCustomAction(
                    context = context,
                    action = DivCustomActionData(
                        id = baseAction.logId.evaluate(context.expressionResolver),
                        payload = baseAction.payload
                    )
                )

            is DivActionTyped.DictSetValue ->
                dictSetValueActionHandler.handle(context, action.value)

            is DivActionTyped.Download -> notSupported()
            is DivActionTyped.FocusElement -> notSupported()
            is DivActionTyped.HideTooltip -> notSupported()
            is DivActionTyped.ScrollBy -> notSupported()
            is DivActionTyped.ScrollTo -> notSupported()
            is DivActionTyped.SetState -> notSupported()
            is DivActionTyped.SetStoredValue -> notSupported()
            is DivActionTyped.SetVariable ->
                setVariableActionHandler.handle(context, action.value)

            is DivActionTyped.ShowTooltip -> notSupported()
            is DivActionTyped.Submit -> notSupported()
            is DivActionTyped.Timer -> notSupported()
            is DivActionTyped.UpdateStructure ->
                updateStructureActionHandler.handle(context, action.value)

            is DivActionTyped.Video -> notSupported()
        }
    }

    private fun handle(
        context: DivActionHandlingContext,
        action: DivUntypedAction
    ) {
        when (action) {
            is DivUntypedAction.HideTooltip -> notSupported()
            is DivUntypedAction.SetState -> notSupported()
            is DivUntypedAction.SetStoredValue -> notSupported()
            is DivUntypedAction.SetVariable ->
                setVariableActionHandler.handle(context, action)

            is DivUntypedAction.ShowTooltip -> notSupported()
            is DivUntypedAction.Timer -> notSupported()
            is DivUntypedAction.Video -> notSupported()
        }
    }

    private fun notSupported() {
        reporter.reportError("Action not supported")
    }
}
