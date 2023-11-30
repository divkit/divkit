package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_CONVERT_TO_URL
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed
import com.yandex.div.evaluable.types.Url

private const val FUNCTION_NAME = "toUrl"

internal object StringToUrl : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))

    override val resultType = EvaluableType.URL

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val urlString = args.first() as String
        return try {
            Url.from(urlString)
        } catch (e: IllegalArgumentException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_URL, e)
        }
    }
}
