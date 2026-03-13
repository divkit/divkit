package com.yandex.div.compose.variables

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.data.Variable
import com.yandex.div.internal.data.PropertyVariableExecutor
import com.yandex.div.internal.variables.toVariable
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivVariable
import javax.inject.Inject

@Mockable
internal class DivVariableAdapter @Inject constructor(
    private val expressionResolver: ExpressionResolver,
    private val parsingErrorLogger: ParsingErrorLogger
) {

    fun convert(variable: DivVariable): Variable? {
        return variable.toVariable(
            resolver = expressionResolver,
            propertyVariableExecutor = PropertyVariableExecutor.STUB,
            logger = parsingErrorLogger
        )
    }
}
