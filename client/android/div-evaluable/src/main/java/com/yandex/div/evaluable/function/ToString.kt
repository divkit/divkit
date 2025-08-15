package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import org.json.JSONArray
import org.json.JSONObject

private const val FUNCTION_NAME = "toString"

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
        val obj = (args.first() as JSONObject)

        return obj.sort().toStringLikeJson()
    }

    private fun JSONObject.sort(): Map<String, Any> {
        val keys = mutableListOf<String>()
        keys().forEach { key -> keys.add(key) }
        keys.sort()
        val sortedObj = sortedMapOf<String, Any>()
        keys.forEach { key ->
            var value = this[key]
            if (value is JSONObject) {
                value = value.sort()
            }
            sortedObj[key] = value
        }
        return sortedObj
    }

    private fun Any.toStringLikeJson(): String {
        return when (this) {
            is Map<*, *> -> {
                val list = arrayListOf<String>()
                iterator().forEach { map -> list.add("\"" + map.key + "\":" + map.value?.toStringLikeJson()) }
                "{" + list.joinToString(",") + "}"
            }
            is String -> "\"$this\""
            else -> toString()
        }
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
        return (args.first() as JSONArray).toString()
    }
}
