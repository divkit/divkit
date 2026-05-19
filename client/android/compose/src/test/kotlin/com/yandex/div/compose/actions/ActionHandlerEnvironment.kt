package com.yandex.div.compose.actions

import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div2.DivAction
import com.yandex.div2.DivSightAction
import org.mockito.kotlin.mock

internal class ActionHandlerEnvironment {
    val reporter = TestReporter()
    val variableController = DivVariableController()
    val stateStorage = DivStateStorage()

    val expressionResolver = createExpressionResolver(
        reporter = reporter,
        variableController = variableController
    )

    val context = DivActionHandlingContext(
        cardId = "test",
        expressionResolver = expressionResolver,
        stateStorage = stateStorage
    )

    private lateinit var actionHandler: DivActionHandler

    fun init(
        externalActionHandler: DivExternalActionHandler = mock(),
        arrayActionsHandler: ArrayActionsHandler = mock(),
        dictSetValueActionHandler: DictSetValueActionHandler = mock(),
        setVariableActionHandler: SetVariableActionHandler = mock(),
        updateStructureActionHandler: UpdateStructureActionHandler = mock(),
        setStateActionHandler: SetStateActionHandler = mock()
    ) {
        actionHandler = DivActionHandler(
            externalActionHandler = externalActionHandler,
            reporter = reporter,
            arrayActionsHandler = arrayActionsHandler,
            dictSetValueActionHandler = dictSetValueActionHandler,
            setVariableActionHandler = setVariableActionHandler,
            updateStructureActionHandler = updateStructureActionHandler,
            setStateActionHandler = setStateActionHandler
        )
    }

    fun handle(action: DivSightAction) {
        actionHandler.handle(context = context, action = action)
    }

    fun handle(action: DivAction, source: DivActionSource = DivActionSource.EXTERNAL) {
        actionHandler.handle(context = context, action = action, source = source)
    }
}
