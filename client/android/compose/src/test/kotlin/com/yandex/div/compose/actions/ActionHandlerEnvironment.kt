package com.yandex.div.compose.actions

import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.core.expression.variables.DivVariableController
import org.mockito.kotlin.mock

internal class ActionHandlerEnvironment {
    val reporter = TestReporter()
    val variableController = DivVariableController()

    val expressionResolver = createExpressionResolver(
        reporter = reporter,
        variableController = variableController
    )

    val context = DivActionHandlingContext(
        expressionResolver = expressionResolver
    )

    fun createActionHandler(
        externalActionHandler: DivExternalActionHandler = mock(),
        arrayActionsHandler: ArrayActionsHandler = mock(),
        dictSetValueActionHandler: DictSetValueActionHandler = mock(),
        setVariableActionHandler: SetVariableActionHandler = mock(),
        updateStructureActionHandler: UpdateStructureActionHandler = mock()
    ): DivActionHandler {
        return DivActionHandler(
            externalActionHandler = externalActionHandler,
            reporter = reporter,
            arrayActionsHandler = arrayActionsHandler,
            dictSetValueActionHandler = dictSetValueActionHandler,
            setVariableActionHandler = setVariableActionHandler,
            updateStructureActionHandler = updateStructureActionHandler
        )
    }
}
