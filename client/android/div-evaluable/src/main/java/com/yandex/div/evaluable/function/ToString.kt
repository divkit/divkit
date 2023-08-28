package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.types.Color

private const val FUNCTION_NAME = "toString"

internal object IntegerToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val integerValue = args.first() as Long
        return integerValue.toString()
    }
}

internal object NumberToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val numberValue = args.first() as Double
        return numberValue.toString()
    }
}

internal object BooleanToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.BOOLEAN))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val booleanValue = args.first() as Boolean
        return if (booleanValue) "true" else "false"
    }
}

internal object ColorToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.COLOR))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        return (args.first() as Color).toString()
    }
}
