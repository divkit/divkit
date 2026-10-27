package com.yandex.div.compose.actions

import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.compose.variables.DivPropertyVariableExecutor
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div2.DivAction
import com.yandex.div2.DivSightAction
import org.mockito.kotlin.mock

internal class ActionHandlerEnvironment {
    val reporter = TestReporter()
    val variableController = DivVariableController()

    val expressionResolver = createExpressionResolver(
        reporter = reporter,
        variableController = variableController
    )

    val context = DivActionHandlingContext(
        cardId = "test",
        expressionResolver = expressionResolver
    )

    lateinit var actionHandler: DivActionHandler
    lateinit var propertyVariableExecutor: DivPropertyVariableExecutor

    fun init(
        animatorActionHandler: AnimatorActionHandler = mock(),
        externalActionHandler: DivExternalActionHandler = mock(),
        arrayActionsHandler: ArrayActionsHandler = mock(),
        clearFocusActionHandler: ClearFocusActionHandler = mock(),
        copyToClipboardActionHandler: CopyToClipboardActionHandler = mock(),
        dictSetValueActionHandler: DictSetValueActionHandler = mock(),
        downloadActionHandler: DownloadActionHandler = mock(),
        focusElementActionHandler: FocusElementActionHandler = mock(),
        hapticActionHandler: HapticActionHandler = mock(),
        scrollActionHandler: ScrollActionHandler = mock(),
        setCursorPositionActionHandler: SetCursorPositionActionHandler = mock(),
        setStateActionHandler: SetStateActionHandler = mock(),
        setStoredValueActionHandler: SetStoredValueActionHandler = mock(),
        setVariableActionHandler: SetVariableActionHandler = mock(),
        submitActionHandler: SubmitActionHandler = mock(),
        timerActionHandler: TimerActionHandler = mock(),
        tooltipActionHandler: TooltipActionHandler = mock(),
        updateStructureActionHandler: UpdateStructureActionHandler = mock(),
        videoActionHandler: VideoActionHandler = mock(),
        actionMenuHolder: ActionMenuHolder = mock()
    ) {
        actionHandler = DivActionHandler(
            actionMenuHolder = actionMenuHolder,
            animatorActionHandler = animatorActionHandler,
            externalActionHandler = externalActionHandler,
            reporter = reporter,
            arrayActionsHandler = arrayActionsHandler,
            clearFocusActionHandler = clearFocusActionHandler,
            copyToClipboardActionHandler = copyToClipboardActionHandler,
            dictSetValueActionHandler = dictSetValueActionHandler,
            downloadActionHandler = downloadActionHandler,
            focusElementActionHandler = focusElementActionHandler,
            hapticActionHandler = hapticActionHandler,
            scrollActionHandler = scrollActionHandler,
            setCursorPositionActionHandler = setCursorPositionActionHandler,
            setStateActionHandler = setStateActionHandler,
            setStoredValueActionHandler = setStoredValueActionHandler,
            setVariableActionHandler = setVariableActionHandler,
            submitActionHandler = submitActionHandler,
            timerActionHandler = timerActionHandler,
            tooltipActionHandler = tooltipActionHandler,
            updateStructureActionHandler = updateStructureActionHandler,
            videoActionHandler = videoActionHandler
        )
        propertyVariableExecutor = DivPropertyVariableExecutor(
            actionHandler = { actionHandler },
            actionHandlingContext = context,
            expressionResolver = expressionResolver,
        )
    }

    fun handle(action: DivSightAction) {
        actionHandler.handle(context = context, action = action)
    }

    fun handle(
        action: DivAction,
        source: DivActionSource = DivActionSource.EXTERNAL,
    ) {
        actionHandler.handle(context = context, action = action, source = source)
    }
}
