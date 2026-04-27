package com.yandex.div.compose.actions

import android.net.Uri
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div.internal.actions.isDivAction
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionAnimatorStart
import com.yandex.div2.DivActionAnimatorStopTemplate
import com.yandex.div2.DivActionClearFocus
import com.yandex.div2.DivActionCopyToClipboard
import com.yandex.div2.DivActionDownload
import com.yandex.div2.DivActionFocusElement
import com.yandex.div2.DivActionHideTooltip
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivActionScrollTo
import com.yandex.div2.DivActionSetCursorPosition
import com.yandex.div2.DivActionSetState
import com.yandex.div2.DivActionSetStoredValue
import com.yandex.div2.DivActionShowTooltip
import com.yandex.div2.DivActionSubmit
import com.yandex.div2.DivActionTimer
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivActionVideo
import com.yandex.div2.DivSightAction
import org.json.JSONObject
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

    fun handle(
        context: DivActionHandlingContext,
        actions: List<DivAction>,
        source: DivActionSource
    ) {
        actions.forEach { handle(context = context, action = it, source = source) }
    }

    fun handle(context: DivActionHandlingContext, action: DivAction, source: DivActionSource) {
        handle(
            context = context,
            action = DivActionBase(
                id = action.logId,
                isEnabled = action.isEnabled,
                payload = action.payload,
                source = source,
                typed = action.typed,
                url = action.url
            )
        )
    }

    fun handle(context: DivActionHandlingContext, action: DivSightAction) {
        handle(
            context = context,
            action = DivActionBase(
                id = action.logId,
                isEnabled = action.isEnabled,
                payload = action.payload,
                source = DivActionSource.VISIBILITY,
                typed = action.typed,
                url = action.url
            )
        )
    }

    private fun handle(context: DivActionHandlingContext, action: DivActionBase) {
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
                    id = action.id.evaluate(expressionResolver),
                    payload = action.payload,
                    source = action.source,
                    url = action.url.evaluate(expressionResolver)
                )
            )
        }
    }

    private fun handle(
        context: DivActionHandlingContext,
        action: DivActionTyped,
        baseAction: DivActionBase
    ) {
        when (action) {
            is DivActionTyped.AnimatorStart -> notSupported(DivActionAnimatorStart.TYPE)
            is DivActionTyped.AnimatorStop -> notSupported(DivActionAnimatorStopTemplate.TYPE)
            is DivActionTyped.ArrayInsertValue ->
                arrayActionsHandler.handle(context, action.value)

            is DivActionTyped.ArrayRemoveValue ->
                arrayActionsHandler.handle(context, action.value)

            is DivActionTyped.ArraySetValue ->
                arrayActionsHandler.handle(context, action.value)

            is DivActionTyped.ClearFocus -> notSupported(DivActionClearFocus.TYPE)
            is DivActionTyped.CopyToClipboard -> notSupported(DivActionCopyToClipboard.TYPE)
            is DivActionTyped.Custom ->
                externalActionHandler.handleCustomAction(
                    context = context,
                    action = DivCustomActionData(
                        id = baseAction.id.evaluate(context.expressionResolver),
                        payload = baseAction.payload,
                        source = baseAction.source
                    )
                )

            is DivActionTyped.DictSetValue ->
                dictSetValueActionHandler.handle(context, action.value)

            is DivActionTyped.Download -> notSupported(DivActionDownload.TYPE)
            is DivActionTyped.FocusElement -> notSupported(DivActionFocusElement.TYPE)
            is DivActionTyped.HideTooltip -> notSupported(DivActionHideTooltip.TYPE)
            is DivActionTyped.ScrollBy -> notSupported(DivActionScrollBy.TYPE)
            is DivActionTyped.ScrollTo -> notSupported(DivActionScrollTo.TYPE)
            is DivActionTyped.SetCursorPosition -> notSupported(DivActionSetCursorPosition.TYPE)
            is DivActionTyped.SetState -> notSupported(DivActionSetState.TYPE)
            is DivActionTyped.SetStoredValue -> notSupported(DivActionSetStoredValue.TYPE)
            is DivActionTyped.SetVariable ->
                setVariableActionHandler.handle(context, action.value)

            is DivActionTyped.ShowTooltip -> notSupported(DivActionShowTooltip.TYPE)
            is DivActionTyped.Submit -> notSupported(DivActionSubmit.TYPE)
            is DivActionTyped.Timer -> notSupported(DivActionTimer.TYPE)
            is DivActionTyped.UpdateStructure ->
                updateStructureActionHandler.handle(context, action.value)

            is DivActionTyped.Video -> notSupported(DivActionVideo.TYPE)
        }
    }

    private fun handle(
        context: DivActionHandlingContext,
        action: DivUntypedAction
    ) {
        when (action) {
            is DivUntypedAction.HideTooltip -> notSupported("hide_tooltip")
            is DivUntypedAction.SetState -> notSupported("set_state")
            is DivUntypedAction.SetStoredValue -> notSupported("set_stored_value")
            is DivUntypedAction.SetVariable ->
                setVariableActionHandler.handle(context, action)

            is DivUntypedAction.ShowTooltip -> notSupported("show_tooltip")
            is DivUntypedAction.Timer -> notSupported("timer")
            is DivUntypedAction.Video -> notSupported("video")
        }
    }

    private fun notSupported(name: String) {
        reporter.reportError("Action not supported: $name")
    }
}

private class DivActionBase(
    val id: Expression<String>,
    val isEnabled: Expression<Boolean>,
    val payload: JSONObject?,
    val source: DivActionSource,
    val typed: DivActionTyped?,
    val url: Expression<Uri>?,
)
