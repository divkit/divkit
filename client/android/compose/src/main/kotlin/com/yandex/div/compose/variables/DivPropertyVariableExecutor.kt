package com.yandex.div.compose.variables

import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.core.Disposable
import com.yandex.div.internal.data.PropertyVariableExecutor
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import javax.inject.Inject

@DivLocalScope
internal class DivPropertyVariableExecutor @Inject constructor(
    private val actionHandler: DivActionHandler,
    private val actionHandlingContext: DivActionHandlingContext,
    private val expressionResolver: DivComposeExpressionResolver,
) : PropertyVariableExecutor {

    override fun evaluate(getExpression: Expression<*>) = getExpression.evaluate(expressionResolver)

    override fun observe(getExpression: Expression<*>, onChange: () -> Unit): Disposable {
        return getExpression.observe(expressionResolver) { onChange() }
    }

    override fun performSet(
        propertyName: String,
        newValueVarName: String,
        actions: List<DivAction>,
        newValue: Any,
    ) {
        actionHandler.handle(
            context = DivActionHandlingContext(
                cardId = actionHandlingContext.cardId,
                expressionResolver = expressionResolver.withPropertyNewValueVariable(
                    variableName = newValueVarName,
                    value = newValue,
                ),
            ),
            actions = actions,
            source = DivActionSource.PROPERTY,
        )
    }
}
