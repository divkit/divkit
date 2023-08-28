package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_CONVERT_TO_NUMBER
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed

private const val FUNCTION_NAME = "toNumber"

internal object IntegerToNumber : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val integerValue = args.first() as Long
        return integerValue.toDouble()
    }
}

internal object StringToNumber : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val stringValue = args.first() as String
        try {
            val numberValue = java.lang.Double.parseDouble(stringValue)
            if (numberValue == Double.POSITIVE_INFINITY || numberValue == Double.NEGATIVE_INFINITY) {
                throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_NUMBER)
            }
            return numberValue
        } catch (nfe: NumberFormatException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_NUMBER, nfe)
        }
    }
}
