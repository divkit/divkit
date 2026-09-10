package com.yandex.div.compose.variables

import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.data.Variable
import com.yandex.div.internal.variables.toVariable
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivVariable
import javax.inject.Inject

@DivLocalScope
internal class DivVariableAdapter @Inject constructor(
    private val expressionResolver: ExpressionResolver,
    private val parsingErrorLogger: ParsingErrorLogger,
    private val propertyVariableExecutor: DivPropertyVariableExecutor
) {

    fun convert(variable: DivVariable): Variable? {
        return variable.toVariable(
            resolver = expressionResolver,
            propertyVariableExecutor = propertyVariableExecutor,
            logger = parsingErrorLogger
        )
    }
}
