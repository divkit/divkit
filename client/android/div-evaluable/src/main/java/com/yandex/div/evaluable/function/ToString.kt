package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument

private const val FUNCTION_NAME = "toString"

internal object IntegerToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val integerValue = args.first() as Int
        return integerValue.toString()
    }
}

internal object NumberToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val numberValue = args.first() as Double
        return numberValue.toString()
    }
}

internal object BooleanToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.BOOLEAN))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val booleanValue = args.first() as Boolean
        return if (booleanValue) "true" else "false"
    }
}
