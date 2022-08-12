package com.yandex.div.core.expression

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.json.expressions.ExpressionResolver

internal class ExpressionsRuntime(
    val expressionResolver: ExpressionResolver,
    val variableController: VariableController,
    val triggersController: TriggersController,
) {
    var activeBinding: DivViewFacade? = null
        set(value) {
            if (field == value) {
                return
            }
            triggersController.setupBinding(value)
            field = value
        }
}
