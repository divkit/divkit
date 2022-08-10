package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_CONVERT_TO_BOOLEAN
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed

private const val FUNCTION_NAME = "toBoolean"

internal object IntegerToBoolean : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))

    override val resultType = EvaluableType.BOOLEAN

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return when (args.first() as Int) {
            0 -> false
            1 -> true
            else -> throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_BOOLEAN)
        }
    }
}

internal object StringToBoolean : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))

    override val resultType = EvaluableType.BOOLEAN

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return when (args.first() as String) {
            "true" -> true
            "false" -> false
            else -> throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_BOOLEAN)
        }
    }
}
