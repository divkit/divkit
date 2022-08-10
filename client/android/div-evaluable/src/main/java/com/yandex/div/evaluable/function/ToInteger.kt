package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_CONVERT_TO_INTEGER
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed

private const val FUNCTION_NAME = "toInteger"
private const val NUMBER_MIN_INTEGER = Integer.MIN_VALUE.toDouble()
private const val NUMBER_MAX_INTEGER = Integer.MAX_VALUE.toDouble()

internal object NumberToInteger : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val numberValue = args.first() as Double
        if (numberValue < NUMBER_MIN_INTEGER || numberValue > NUMBER_MAX_INTEGER) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_INTEGER)
        }
        return numberValue.toInt()
    }
}

internal object BooleanToInteger : Function() {

    override val name = "toInteger"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.BOOLEAN))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val booleanValue = args.first() as Boolean
        return if (booleanValue) 1 else 0
    }
}

internal object StringToInteger : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val stringValue = args.first() as String
        return try {
            Integer.parseInt(stringValue)
        } catch (nfe: NumberFormatException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_INTEGER, nfe)
        }
    }
}
