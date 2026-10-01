package com.yandex.div.compose.actions

import android.net.Uri
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.patch.DivPatchDownloadManager
import com.yandex.div.internal.actions.DivDownloadActionParser
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div.internal.actions.isDivAction
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionAnimatorStart
import com.yandex.div2.DivActionAnimatorStopTemplate
import com.yandex.div2.DivActionClearFocus
import com.yandex.div2.DivActionFocusElement
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivActionScrollTo
import com.yandex.div2.DivActionSetCursorPosition
import com.yandex.div2.DivActionSubmit
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivDownloadCallbacks
import com.yandex.div2.DivSightAction
import org.json.JSONObject
import javax.inject.Inject
import androidx.core.net.toUri

@DivViewScope
internal class DivActionHandler @Inject constructor(
    private val actionMenuHolder: ActionMenuHolder,
    private val externalActionHandler: DivExternalActionHandler,
    private val reporter: DivReporter,
    private val hapticActionHandler: HapticActionHandler,
    private val arrayActionsHandler: ArrayActionsHandler,
    private val copyToClipboardActionHandler: CopyToClipboardActionHandler,
    private val dictSetValueActionHandler: DictSetValueActionHandler,
    private val patchDownloadManager: DivPatchDownloadManager,
    private val setStateActionHandler: SetStateActionHandler,
    private val setStoredValueActionHandler: SetStoredValueActionHandler,
    private val setVariableActionHandler: SetVariableActionHandler,
    private val timerActionHandler: TimerActionHandler,
    private val tooltipActionHandler: TooltipActionHandler,
    private val updateStructureActionHandler: UpdateStructureActionHandler,
    private val videoActionHandler: VideoActionHandler
) {

    fun handle(
        context: DivActionHandlingContext,
        actions: List<DivAction>,
        source: DivActionSource,
    ) {
        actions.forEach {
            handle(context = context, action = it, source = source)
        }
    }

    fun handle(
        context: DivActionHandlingContext,
        action: DivAction,
        source: DivActionSource,
    ) {
        if (action.scopeId != null) {
            reporter.reportError("div-action.scope_id not supported")
        }

        handle(
            context = context,
            action = DivActionBase(
                isEnabled = action.isEnabled,
                downloadCallbacks = action.downloadCallbacks,
                logId = action.logId,
                logUrl = action.logUrl,
                payload = action.payload,
                referer = action.referer,
                source = source,
                typed = action.typed,
                url = action.url
            ),
            menuAction = action,
        )
    }

    fun handle(context: DivActionHandlingContext, action: DivSightAction) {
        if (action.scopeId != null) {
            reporter.reportError("div-action.scope_id not supported")
        }

        handle(
            context = context,
            action = DivActionBase(
                isEnabled = action.isEnabled,
                downloadCallbacks = action.downloadCallbacks,
                logId = action.logId,
                logUrl = null,
                payload = action.payload,
                referer = action.referer,
                source = if (action is DivDisappearAction) {
                    DivActionSource.DISAPPEAR
                } else {
                    DivActionSource.VISIBILITY
                },
                typed = action.typed,
                url = action.url
            )
        )
    }

    private fun handle(
        context: DivActionHandlingContext,
        action: DivActionBase,
        menuAction: DivAction? = null,
    ) {
        val expressionResolver = context.expressionResolver
        if (!action.isEnabled.evaluate(expressionResolver)) {
            return
        }

        val actionData = DivActionData(
            id = action.logId?.evaluate(expressionResolver),
            payload = action.payload,
            source = action.source,
            url = if (action.typed == null) action.url?.evaluate(expressionResolver) else null,
            logUrl = action.logUrl?.evaluate(expressionResolver),
            referer = action.referer?.evaluate(expressionResolver),
        )
        externalActionHandler.onActionTriggered(context, actionData)

        menuAction?.let { actionMenuHolder.showIfNeeded(it, expressionResolver) }

        action.typed?.let {
            handle(
                context = context,
                action = it,
                actionData = actionData,
                downloadCallbacks = action.downloadCallbacks
            )
            return
        }

        val url = actionData.url
        if (url?.isDivAction == true) {
            if (DivDownloadActionParser.matches(url)) {
                handleDownload(context, url, action.downloadCallbacks)
                return
            }
            DivUntypedAction.parse(url)?.let {
                handle(context = context, action = it)
            }
        } else {
            externalActionHandler.handle(
                context = context,
                action = actionData
            )
        }
    }

    private fun handle(
        context: DivActionHandlingContext,
        action: DivActionTyped,
        actionData: DivActionData,
        downloadCallbacks: DivDownloadCallbacks?
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

            is DivActionTyped.Haptic ->
                hapticActionHandler.handle(context, action.value)

            is DivActionTyped.ClearFocus -> notSupported(DivActionClearFocus.TYPE)

            is DivActionTyped.CopyToClipboard ->
                copyToClipboardActionHandler.handle(context, action.value)

            is DivActionTyped.Custom ->
                externalActionHandler.handleCustomAction(
                    context = context,
                    action = DivCustomActionData(
                        id = actionData.id,
                        payload = actionData.payload,
                        source = actionData.source
                    )
                )

            is DivActionTyped.DictSetValue ->
                dictSetValueActionHandler.handle(context, action.value)

            is DivActionTyped.Download -> handleDownload(
                context,
                action.value.url.evaluate(context.expressionResolver),
                action.value.onSuccessActions ?: downloadCallbacks?.onSuccessActions,
                action.value.onFailActions ?: downloadCallbacks?.onFailActions
            )
            is DivActionTyped.FocusElement -> notSupported(DivActionFocusElement.TYPE)

            is DivActionTyped.HideTooltip ->
                tooltipActionHandler.handle(context, action.value)

            is DivActionTyped.ScrollBy -> notSupported(DivActionScrollBy.TYPE)
            is DivActionTyped.ScrollTo -> notSupported(DivActionScrollTo.TYPE)
            is DivActionTyped.SetCursorPosition -> notSupported(DivActionSetCursorPosition.TYPE)

            is DivActionTyped.SetState ->
                setStateActionHandler.handle(context, action.value)

            is DivActionTyped.SetStoredValue ->
                setStoredValueActionHandler.handle(context, action.value)

            is DivActionTyped.SetVariable ->
                setVariableActionHandler.handle(context, action.value)

            is DivActionTyped.ShowTooltip ->
                tooltipActionHandler.handle(context, action.value)

            is DivActionTyped.Submit -> notSupported(DivActionSubmit.TYPE)

            is DivActionTyped.Timer ->
                timerActionHandler.handle(context, action.value)

            is DivActionTyped.UpdateStructure ->
                updateStructureActionHandler.handle(context, action.value)

            is DivActionTyped.Video ->
                videoActionHandler.handle(context, action.value)
        }
    }

    private fun handle(
        context: DivActionHandlingContext,
        action: DivUntypedAction
    ) {
        when (action) {
            is DivUntypedAction.HideTooltip ->
                tooltipActionHandler.handle(action)

            is DivUntypedAction.SetState ->
                setStateActionHandler.handle(action)

            is DivUntypedAction.SetStoredValue ->
                setStoredValueActionHandler.handle(action)

            is DivUntypedAction.SetVariable ->
                setVariableActionHandler.handle(context, action)

            is DivUntypedAction.ShowTooltip ->
                tooltipActionHandler.handle(action)

            is DivUntypedAction.Timer ->
                timerActionHandler.handle(action)

            is DivUntypedAction.Video ->
                videoActionHandler.handle(action)
        }
    }

    private fun handleDownload(
        context: DivActionHandlingContext,
        uri: Uri,
        callbacks: DivDownloadCallbacks?
    ) {
        val downloadUrl = DivDownloadActionParser.parseUrl(uri)
        if (downloadUrl == null) {
            reporter.reportError("url param is required for download action")
            return
        }
        handleDownload(
            context = context,
            url = downloadUrl.toUri(),
            onSuccess = callbacks?.onSuccessActions,
            onFail = callbacks?.onFailActions
        )
    }

    private fun handleDownload(
        context: DivActionHandlingContext,
        url: Uri,
        onSuccess: List<DivAction>?,
        onFail: List<DivAction>?
    ) {
        patchDownloadManager.download(
            url,
            onSuccess = { handle(context, onSuccess.orEmpty(), DivActionSource.PATCH) },
            onFail = { handle(context, onFail.orEmpty(), DivActionSource.PATCH) }
        )
    }

    private fun notSupported(name: String) {
        reporter.reportError("Action not supported: $name")
    }
}

private class DivActionBase(
    val isEnabled: Expression<Boolean>,
    val downloadCallbacks: DivDownloadCallbacks?,
    val logId: Expression<String>?,
    val logUrl: Expression<Uri>?,
    val payload: JSONObject?,
    val referer: Expression<Uri>?,
    val source: DivActionSource,
    val typed: DivActionTyped?,
    val url: Expression<Uri>?,
)
