package com.yandex.div.core.util.inputfilter

import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver

internal class ExpressionInputFilter(
    private val condition: Expression<Boolean>,
    private val resolver: ExpressionResolver,
) : BaseInputFilter {

    override fun checkValue(value: String) = condition.evaluate(resolver)
}
