package com.yandex.div.evaluable.function

import com.yandex.div.core.type.DataArray
import com.yandex.div.core.type.DataObject
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url

private const val FUNCTION_NAME = "toString"
private const val HEX_RADIX = 16
private const val UNICODE_ESCAPE_LENGTH = 4

internal object IntegerToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val integerValue = args.first() as Long
        return integerValue.toString()
    }
}

internal object NumberToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val numberValue = args.first() as Double
        return numberValue.toString()
    }
}

internal object BooleanToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.BOOLEAN))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val booleanValue = args.first() as Boolean
        return if (booleanValue) "true" else "false"
    }
}

internal object ColorToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.COLOR))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return (args.first() as Color).toString()
    }
}

internal object UrlToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.URL))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return (args.first() as Url).toString()
    }
}

internal object StringToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return (args.first() as String)
    }
}

internal object DictToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val obj = (args.first() as DataObject)
        return obj.toJsonString()
    }
}

internal object ArrayToString : Function() {

    override val name = FUNCTION_NAME

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val array = (args.first() as DataArray)
        return array.toJsonString()
    }
}

private fun Any?.toJsonString(): String = when (this) {
    is DataObject -> keys.sorted().joinToString(separator = ",", prefix = "{", postfix = "}") { key ->
        "${key.quotedJsonString()}:${opt(key).toJsonString()}"
    }

    is DataArray -> joinToString(separator = ",", prefix = "[", postfix = "]") { element ->
        element.toJsonString()
    }

    is String -> quotedJsonString()
    is Float -> toJsonString()
    is Double -> toJsonString()
    else -> toString()
}

private fun Float.toJsonString(): String {
    val integerPart = toLong()
    return if (this == integerPart.toFloat()) integerPart.toString() else toString()
}

private fun Double.toJsonString(): String {
    val integerPart = toLong()
    return if (this == integerPart.toDouble()) integerPart.toString() else toString()
}

private fun String.quotedJsonString(): String = buildString {
    append('"')
    for (char in this@quotedJsonString) {
        when (char) {
            '"' -> append("\\\"")
            '\\' -> append("\\\\")
            '\b' -> append("\\b")
            '\u000C' -> append("\\f")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> if (char < ' ') {
                append("\\u")
                append(char.code.toString(HEX_RADIX).padStart(UNICODE_ESCAPE_LENGTH, '0'))
            } else {
                append(char)
            }
        }
    }
    append('"')
}
