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

internal abstract class ArrayFunction(
    final override val resultType: EvaluableType,
) : Function() {
    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER) // index at array
    )
    override val isPure: Boolean = false
}

internal abstract class ArrayOptFunction(
    final override val resultType: EvaluableType,
) : Function() {
    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = resultType)
    )
    override val isPure: Boolean = false
}

internal object GetArrayInteger : ArrayFunction(EvaluableType.INTEGER) {

    override val name: String = "getArrayInteger"

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

internal object GetIntegerFromArray : ArrayFunction(EvaluableType.INTEGER) {

    override val name: String = "getIntegerFromArray"

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

internal object GetArrayOptInteger : ArrayOptFunction(EvaluableType.INTEGER) {

    override val name: String = "getArrayOptInteger"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Long
        return evaluateSafe(name, args).let {
            when (it) {
                is Int -> it.toLong()
                is Long -> it
                else -> fallback
            }
        }
    }
}

internal object GetOptIntegerFromArray : ArrayOptFunction(EvaluableType.INTEGER) {

    override val name: String = "getOptIntegerFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Long
        return evaluateSafe(name, args).let {
            when (it) {
                is Int -> it.toLong()
                is Long -> it
                else -> fallback
            }
        }
    }
}

internal object GetArrayNumber : ArrayFunction(EvaluableType.NUMBER) {

    override val name: String = "getArrayNumber"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        when (it) {
            is Double -> it
            is Int -> it.toDouble()
            is Long -> it.toDouble()
            is BigDecimal -> it.toDouble()
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal object GetNumberFromArray : ArrayFunction(EvaluableType.NUMBER) {

    override val name: String = "getNumberFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        when (it) {
            is Double -> it
            is Int -> it.toDouble()
            is Long -> it.toDouble()
            is BigDecimal -> it.toDouble()
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal object GetArrayOptNumber : ArrayOptFunction(EvaluableType.NUMBER) {

    override val name: String = "getArrayOptNumber"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Double
        return evaluateSafe(name, args).let {
            when (it) {
                is Double -> it
                is Int -> it.toDouble()
                is Long -> it.toDouble()
                is BigDecimal -> it.toDouble()
                else -> fallback
            }
        }
    }
}

internal object GetOptNumberFromArray : ArrayOptFunction(EvaluableType.NUMBER) {

    override val name: String = "getOptNumberFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Double
        return evaluateSafe(name, args).let {
            when (it) {
                is Double -> it
                is Int -> it.toDouble()
                is Long -> it.toDouble()
                is BigDecimal -> it.toDouble()
                else -> fallback
            }
        }
    }
}

internal object GetArrayString : ArrayFunction(EvaluableType.STRING) {

    override val name: String = "getArrayString"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        it as? String ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetStringFromArray : ArrayFunction(EvaluableType.STRING) {

    override val name: String = "getStringFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        it as? String ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetArrayOptString : ArrayOptFunction(EvaluableType.STRING) {

    override val name: String = "getArrayOptString"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as String
        return evaluateSafe(name, args) as? String ?: fallback
    }
}

internal object GetOptStringFromArray : ArrayOptFunction(EvaluableType.STRING) {

    override val name: String = "getOptStringFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as String
        return evaluateSafe(name, args) as? String ?: fallback
    }
}

internal object GetArrayColor : ArrayFunction(EvaluableType.COLOR) {

    override val name: String = "getArrayColor"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        (it as? Color) ?: (it as? String)?.runCatching {
            Color.parse(this)
        }?.getOrElse {
            throwException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
        } ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetColorFromArray : ArrayFunction(EvaluableType.COLOR) {

    override val name: String = "getColorFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        (it as? Color) ?: (it as? String)?.runCatching {
            Color.parse(this)
        }?.getOrElse {
            throwException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
        } ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetArrayOptColorWithStringFallback : ArrayOptFunction(EvaluableType.COLOR) {

    override val name: String = "getArrayOptColor"
    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = EvaluableType.STRING) // fallback
    )

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as String
        val result = evaluateSafe(name, args)
        return (result as? String).safeConvertToColor() ?: fallback.safeConvertToColor() ?:
        throwException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal object GetOptColorFromArrayWithStringFallback : ArrayOptFunction(EvaluableType.COLOR) {

    override val name: String = "getOptColorFromArray"
    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = EvaluableType.STRING) // fallback
    )

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as String
        val result = evaluateSafe(name, args)
        return (result as? String).safeConvertToColor() ?: fallback.safeConvertToColor() ?:
        throwException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal fun String?.safeConvertToColor() = this?.runCatching { Color.parse(this) }?.getOrNull()

internal object GetArrayOptColorWithColorFallback : ArrayOptFunction(EvaluableType.COLOR) {

    override val name: String = "getArrayOptColor"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Color
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? Color) ?: (evaluateSafe as? String)?.runCatching {
            Color.parse(this)
        }?.getOrNull() ?: fallback
    }
}

internal object GetOptColorFromArrayWithColorFallback : ArrayOptFunction(EvaluableType.COLOR) {

    override val name: String = "getOptColorFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Color
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? Color) ?: (evaluateSafe as? String)?.runCatching {
            Color.parse(this)
        }?.getOrNull() ?: fallback
    }
}

internal object GetArrayUrl : ArrayFunction(EvaluableType.URL) {

    override val name: String = "getArrayUrl"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        (it as? String)?.safeConvertToUrl() ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetUrlFromArray : ArrayFunction(EvaluableType.URL) {

    override val name: String = "getUrlFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        (it as? String)?.safeConvertToUrl() ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetArrayOptUrlWithStringFallback : ArrayOptFunction(EvaluableType.URL) {

    override val name: String = "getArrayOptUrl"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = EvaluableType.STRING) // fallback
    )

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as String
        val result = evaluateSafe(name, args)
        return (result as? String).safeConvertToUrl() ?: fallback.safeConvertToUrl()
        ?: throwException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal object GetOptUrlFromArrayWithStringFallback : ArrayOptFunction(EvaluableType.URL) {

    override val name: String = "getOptUrlFromArray"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = EvaluableType.STRING) // fallback
    )

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as String
        val result = evaluateSafe(name, args)
        return (result as? String).safeConvertToUrl() ?: fallback.safeConvertToUrl()
        ?: throwException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal fun String?.safeConvertToUrl() = this?.runCatching { Url.from(this) }?.getOrNull()

internal object GetArrayOptUrlWithUrlFallback : ArrayOptFunction(EvaluableType.URL) {

    override val name: String = "getArrayOptUrl"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Url
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? String).safeConvertToUrl() ?: fallback
    }
}

internal object GetOptUrlFromArrayWithUrlFallback : ArrayOptFunction(EvaluableType.URL) {

    override val name: String = "getOptUrlFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Url
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? String).safeConvertToUrl() ?: fallback
    }
}

internal object GetArrayBoolean : ArrayFunction(EvaluableType.BOOLEAN) {

    override val name: String = "getArrayBoolean"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        it as? Boolean ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetBooleanFromArray : ArrayFunction(EvaluableType.BOOLEAN) {

    override val name: String = "getBooleanFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluate(name, args).let {
        it as? Boolean ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal object GetArrayOptBoolean : ArrayOptFunction(EvaluableType.BOOLEAN) {

    override val name: String = "getArrayOptBoolean"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Boolean
        return evaluateSafe(name, args) as? Boolean ?: fallback
    }
}

internal object GetOptBooleanFromArray : ArrayOptFunction(EvaluableType.BOOLEAN) {

    override val name: String = "getOptBooleanFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val fallback = args[2] as Boolean
        return evaluateSafe(name, args) as? Boolean ?: fallback
    }
}

internal object GetArrayFromArray : ArrayFunction(EvaluableType.ARRAY) {
    override val name: String = "getArrayFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any =
        evaluate(name, args).let {
            it as? JSONArray ?: throwWrongTypeException(name, args, resultType, it)
        }
}

internal object GetOptArrayFromArray : ArrayOptFunction(EvaluableType.ARRAY) {
    override val name: String = "getOptArrayFromArray"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER) // index at array
    )

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any =
        evaluateSafe(name, args).let {
            it as? JSONArray ?: JSONArray()
        }
}

internal object GetDictFromArray : ArrayFunction(EvaluableType.DICT) {
    override val name: String = "getDictFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any =
        evaluate(name, args).let {
            it as? JSONObject ?: throwWrongTypeException(name, args, resultType, it)
        }
}

internal object GetOptDictFromArray : ArrayFunction(EvaluableType.DICT) {
    override val name: String = "getOptDictFromArray"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER) // index at array
    )

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any =
        evaluate(name, args).let {
            it as? JSONObject ?: JSONObject()
        }
}

internal object GetArrayLength : Function() {

    override val name: String = "len"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY)
    )

    override val resultType: EvaluableType = EvaluableType.INTEGER

    override val isPure: Boolean = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val array = args[0] as JSONArray
        return array.length().toLong()
    }
}

private fun evaluate(functionName: String, args: List<Any>): Any {
    checkIndexOfBoundException(functionName, args)
    val array = args[0] as JSONArray
    val index = args[1] as Long
    return array.get(index.toInt())
}

private fun evaluateSafe(functionName: String, args: List<Any>): Any? {
    return runCatching {
        checkIndexOfBoundException(functionName, args)
        val array = args[0] as JSONArray
        val index = args[1] as Long
        array.get(index.toInt())
    }.getOrNull()
}

private fun checkIndexOfBoundException(functionName: String, args: List<Any>) {
    val arraySize = (args[0] as JSONArray).length()
    val index = args[1] as Long
    if (index >= arraySize) {
        throwException(
            functionName = functionName,
            args = args,
            message = "Requested index (${index}) out of bounds array size ($arraySize)."
        )
    }
}

private fun throwWrongTypeException(
    functionName: String,
    args: List<Any>,
    expected: EvaluableType,
    actual: Any
) {
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
    val signature = args.subList(1, args.size)
        .joinToString(prefix = "${functionName}(<array>, ", postfix = ")") {
            it.toMessageFormat()
        }
    throwExceptionOnEvaluationFailed(signature, message)
}