package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_CONVERT_TO_COLOR
import com.yandex.div.evaluable.REASON_CONVERT_TO_URL
import com.yandex.div.evaluable.throwExceptionOnEvaluationFailed
import com.yandex.div.evaluable.toMessageFormat
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.BigInteger

internal object GetDictInteger : Function() {

    override val name = "getDictInteger"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.INTEGER
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        when (it) {
            is Int -> it.toLong()
            is Long -> it
            is BigInteger -> throwException(name, args, "Integer overflow.")
            is BigDecimal -> throwException(name, args, "Cannot convert value to integer.")
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal object GetIntegerFromDict : Function() {

    override val name = "getIntegerFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.INTEGER
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        when (it) {
            is Int -> it.toLong()
            is Long -> it
            is BigInteger -> throwException(name, args, "Integer overflow.")
            is BigDecimal -> throwException(name, args, "Cannot convert value to integer.")
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal object GetDictNumber : Function() {

    override val name = "getDictNumber"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.NUMBER
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        when (it) {
            is Int -> it.toDouble()
            is Long -> it.toDouble()
            is BigDecimal -> it.toDouble()
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal object GetNumberFromDict : Function() {

    override val name = "getNumberFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.NUMBER
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        when (it) {
            is Int -> it.toDouble()
            is Long -> it.toDouble()
            is BigDecimal -> it.toDouble()
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal object GetDictString : Function() {

    override val name = "getDictString"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        it as? String ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetStringFromDict : Function() {

    override val name = "getStringFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        it as? String ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetDictColor : Function() {

    override val name = "getDictColor"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = evaluate(name, args).let {
        (it as? String)?.runCatching {
            Color.parse(this)
        }?.getOrElse {
            throwException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
        } ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetColorFromDict : Function() {

    override val name = "getColorFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = evaluate(name, args).let {
        (it as? String)?.runCatching {
            Color.parse(this)
        }?.getOrElse {
            throwException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
        } ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetDictUrl : Function() {

    override val name = "getDictUrl"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = evaluate(name, args).let {
        (it as? String)?.safeConvertToUrl() ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetUrlFromDict : Function() {

    override val name = "getUrlFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = evaluate(name, args).let {
        (it as? String)?.safeConvertToUrl() ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetDictBoolean : Function() {

    override val name = "getDictBoolean"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = evaluate(name, args).let {
        it as? Boolean ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetBooleanFromDict : Function() {

    override val name = "getBooleanFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = evaluate(name, args).let {
        it as? Boolean ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetDictFromDict : Function() {

    override val name = "getDictFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.DICT
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = evaluate(name, args).let {
        it as? JSONObject ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetArrayFromDict : Function() {

    override val name = "getArrayFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.ARRAY
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = evaluate(name, args).let {
        it as? JSONArray ?: throwWrongTypeException(name, args, resultType, it)
    }
}

private fun evaluate(functionName: String, args: List<Any>): Any {
    var dict = args.first() as JSONObject?
    var propName: String
    for (i in 1 until args.size - 1) {
        propName = args[i] as String
        runCatching { dict = dict!!.opt(propName) as? JSONObject }.getOrElse {
            throwMissingPropertyException(functionName, args, propName)
        }
    }
    propName = args.last() as String
    return runCatching { dict!!.get(propName) }.getOrElse {
        throwMissingPropertyException(functionName, args, propName)
    }
}

private fun throwMissingPropertyException(functionName: String, args: List<Any>, propName: String): Nothing =
    throwException(functionName, args, "Missing property \"$propName\" in the dict.")

private fun throwWrongTypeException(functionName: String, args: List<Any>, expected: EvaluableType, actual: Any): Nothing {
    val actualType = when (actual) {
        is Int, is Double, is BigDecimal -> "Number"
        is JSONObject -> "Dict"
        is JSONArray -> "Array"
        else -> actual.javaClass.simpleName
    }
    throwException(functionName, args,
        "Incorrect value type: expected ${expected.typeName}, got $actualType.")
}

private fun throwException(functionName: String, args: List<Any>, message: String): Nothing {
    val signature = args.subList(1, args.size).joinToString(prefix = "${functionName}(<dict>, ", postfix = ")") {
        it.toMessageFormat()
    }
    throwExceptionOnEvaluationFailed(signature, message)
}

internal object GetDictOptInteger : Function() {

    override val name = "getDictOptInteger"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.INTEGER
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Long
        return evaluateSafe(args, fallback).let {
            when (it) {
                is Int -> it.toLong()
                is Long -> it
                else -> fallback
            }
        }
    }
}

internal object GetOptIntegerFromDict : Function() {

    override val name = "getOptIntegerFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.INTEGER
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Long
        return evaluateSafe(args, fallback).let {
            when (it) {
                is Int -> it.toLong()
                is Long -> it
                else -> fallback
            }
        }
    }
}

internal object GetDictOptNumber : Function() {

    override val name = "getDictOptNumber"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.NUMBER), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.NUMBER
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Double
        return evaluateSafe(args, fallback).let {
            when (it) {
                is Int -> it.toDouble()
                is Long -> it.toDouble()
                is BigDecimal -> it.toDouble()
                else -> fallback
            }
        }
    }
}

internal object GetOptNumberFromDict : Function() {

    override val name = "getOptNumberFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.NUMBER), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.NUMBER
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Double
        return evaluateSafe(args, fallback).let {
            when (it) {
                is Int -> it.toDouble()
                is Long -> it.toDouble()
                is BigDecimal -> it.toDouble()
                else -> fallback
            }
        }
    }
}

internal object GetDictOptString : Function() {

    override val name = "getDictOptString"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as String
        return evaluateSafe(args, fallback) as? String ?: fallback
    }
}

internal object GetOptStringFromDict : Function() {

    override val name = "getOptStringFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as String
        return evaluateSafe(args, fallback) as? String ?: fallback
    }
}

internal object GetDictOptColorWithStringFallback : Function() {

    override val name = "getDictOptColor"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as String
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToColor()
            ?: fallback.safeConvertToColor()
            ?: throwException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal object GetDictOptColorWithColorFallback : Function() {

    override val name = "getDictOptColor"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.COLOR), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Color
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToColor() ?: fallback
    }
}

internal object GetOptColorFromDictWithStringFallback : Function() {

    override val name = "getOptColorFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as String
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToColor()
            ?: fallback.safeConvertToColor()
            ?: throwException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal object GetOptColorFromDictWithColorFallback : Function() {

    override val name = "getOptColorFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.COLOR), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Color
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToColor() ?: fallback
    }
}

internal object GetDictOptUrlWithStringFallback : Function() {

    override val name = "getDictOptUrl"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as String
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToUrl() ?: fallback.safeConvertToUrl() ?:
        throwException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal object GetDictOptUrlWithUrlFallback : Function() {

    override val name = "getDictOptUrl"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.URL), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Url
        return (evaluateSafe(args, fallback) as? String)?.safeConvertToUrl() ?: fallback
    }
}

internal object GetOptUrlFromDictWithStringFallback : Function() {

    override val name = "getOptUrlFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as String
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToUrl() ?: fallback.safeConvertToUrl() ?:
        throwException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal object GetOptUrlFromDictWithUrlFallback : Function() {

    override val name = "getOptUrlFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.URL), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Url
        return (evaluateSafe(args, fallback) as? String)?.safeConvertToUrl() ?: fallback
    }
}

internal object GetDictOptBoolean : Function() {

    override val name = "getDictOptBoolean"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.BOOLEAN), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Boolean
        return evaluateSafe(args, fallback) as? Boolean ?: fallback
    }
}

internal object GetOptBooleanFromDict : Function() {

    override val name = "getOptBooleanFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.BOOLEAN), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[0] as Boolean
        return evaluateSafe(args, fallback) as? Boolean ?: fallback
    }
}

internal object GetOptDictFromDict : Function() {

    override val name = "getOptDictFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.DICT
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = JSONObject()
        return evaluateSafe(args, fallback, defaultFallback = true) as? JSONObject ?: fallback
    }
}

internal object GetOptArrayFromDict : Function() {

    override val name = "getOptArrayFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.ARRAY
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = JSONArray()
        return evaluateSafe(args, fallback, defaultFallback = true) as? JSONArray ?: fallback
    }
}

private fun evaluateSafe(args: List<Any>, fallback: Any, defaultFallback: Boolean = false): Any? {
    val dictIndex = if (defaultFallback) 0 else 1
    var dict = args[dictIndex] as? JSONObject ?: return fallback
    for (i in dictIndex + 1 until args.size - 1) {
        dict = dict.optJSONObject(args[i] as String) ?: return fallback
    }
    return dict.opt(args.last() as String)
}
