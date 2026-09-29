package com.yandex.div.compose.actions

import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.createExpressionResolver
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

    fun init(
        externalActionHandler: DivExternalActionHandler = mock(),
        actionLogger: DivActionLogger = mock(),
        arrayActionsHandler: ArrayActionsHandler = mock(),
        dictSetValueActionHandler: DictSetValueActionHandler = mock(),
        setStateActionHandler: SetStateActionHandler = mock(),
        setStoredValueActionHandler: SetStoredValueActionHandler = mock(),
        setVariableActionHandler: SetVariableActionHandler = mock(),
        timerActionHandler: TimerActionHandler = mock(),
        tooltipActionHandler: TooltipActionHandler = mock(),
        updateStructureActionHandler: UpdateStructureActionHandler = mock(),
        videoActionHandler: VideoActionHandler = mock(),
        actionMenuHolder: ActionMenuHolder = mock()
    ) {
        actionHandler = DivActionHandler(
            actionMenuHolder = actionMenuHolder,
            actionLogger = actionLogger,
            externalActionHandler = externalActionHandler,
            reporter = reporter,
            arrayActionsHandler = arrayActionsHandler,
            dictSetValueActionHandler = dictSetValueActionHandler,
            setStateActionHandler = setStateActionHandler,
            setStoredValueActionHandler = setStoredValueActionHandler,
            setVariableActionHandler = setVariableActionHandler,
            timerActionHandler = timerActionHandler,
            tooltipActionHandler = tooltipActionHandler,
            updateStructureActionHandler = updateStructureActionHandler,
            videoActionHandler = videoActionHandler
        )
    }

    fun handle(action: DivSightAction) {
        actionHandler.handle(context = context, action = action)
    }

    fun handle(
        action: DivAction,
        source: DivActionSource = DivActionSource.EXTERNAL,
        includeLogUrl: Boolean = false,
    ) {
        actionHandler.handle(context = context, action = action, source = source, includeLogUrl = includeLogUrl)
    }
}
