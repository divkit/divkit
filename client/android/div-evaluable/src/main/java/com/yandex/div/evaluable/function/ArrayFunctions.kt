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
import kotlin.math.roundToLong

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

internal abstract class ArrayInteger : ArrayFunction(EvaluableType.INTEGER) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return when (val result = evaluate(name, args)) {
            is Int -> result.toLong()
            is Long -> result
            is BigInteger -> throwArrayException(name, args, "Integer overflow.")
            is BigDecimal -> throwArrayException(name, args, "Cannot convert value to integer.")
            is Double -> {
                if (result < Long.MIN_VALUE || result > Long.MAX_VALUE) {
                    throwArrayException(name, args, "Integer overflow.")
                }
                val longResult = result.roundToLong()
                if (result - longResult == 0.0) return longResult
                throwArrayException(name, args, "Cannot convert value to integer.")
            }
            else -> throwWrongTypeException(name, args, resultType, result)
        }
    }
}

internal object GetArrayInteger : ArrayInteger() {
    override val name: String = "getArrayInteger"
}

internal object GetIntegerFromArray : ArrayInteger() {
    override val name: String = "getIntegerFromArray"
}

internal abstract class ArrayOptInteger : ArrayOptFunction(EvaluableType.INTEGER) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return when (val result = evaluateSafe(name, args)) {
            is Int -> result.toLong()
            is Long -> result
            else -> args[2] // fallback
        }
    }
}

internal object GetArrayOptInteger : ArrayOptInteger() {
    override val name: String = "getArrayOptInteger"
}

internal object GetOptIntegerFromArray : ArrayOptInteger() {
    override val name: String = "getOptIntegerFromArray"
}

internal abstract class ArrayNumber : ArrayFunction(EvaluableType.NUMBER) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluate(name, args)
        return (result as? Number)?.toDouble() ?: throwWrongTypeException(name, args, resultType, result)
    }
}

internal object GetArrayNumber : ArrayNumber() {
    override val name: String = "getArrayNumber"
}

internal object GetNumberFromArray : ArrayNumber() {
    override val name: String = "getNumberFromArray"
}

internal abstract class ArrayOptNumber : ArrayOptFunction(EvaluableType.NUMBER) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluateSafe(name, args) as? Number
        return result?.toDouble() ?: args[2]
    }
}

internal object GetArrayOptNumber : ArrayOptNumber() {
    override val name: String = "getArrayOptNumber"
}

internal object GetOptNumberFromArray : ArrayOptNumber() {
    override val name: String = "getOptNumberFromArray"
}

internal abstract class ArrayString : ArrayFunction(EvaluableType.STRING) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluate(name, args)
        return result as? String ?: throwWrongTypeException(name, args, resultType, result)
    }
}

internal object GetArrayString : ArrayString() {
    override val name: String = "getArrayString"
}

internal object GetStringFromArray : ArrayString() {
    override val name: String = "getStringFromArray"
}

internal abstract class ArrayOptString : ArrayOptFunction(EvaluableType.STRING) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluateSafe(name, args) as? String ?: args[2] // fallback
}

internal object GetArrayOptString : ArrayOptString() {
    override val name: String = "getArrayOptString"
}

internal object GetOptStringFromArray : ArrayOptString() {
    override val name: String = "getOptStringFromArray"
}

internal abstract class ArrayColor : ArrayFunction(EvaluableType.COLOR) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return when (val result = evaluate(name, args)) {
            is Color -> result
            !is String -> throwWrongTypeException(name, args, resultType, result)
            else -> {
                runCatching {
                    Color.parse(result)
                }.getOrElse {
                    throwArrayException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
                }
            }
        }
    }
}

internal object GetArrayColor : ArrayColor() {
    override val name: String = "getArrayColor"
}

internal object GetColorFromArray : ArrayColor() {
    override val name: String = "getColorFromArray"
}

internal abstract class ArrayOptColorWithStringFallback : ArrayOptFunction(EvaluableType.COLOR) {

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
        return (evaluateSafe(name, args) as? String).safeConvertToColor()
            ?: (args[2] as String).safeConvertToColor() // fallback
            ?: throwArrayException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal fun String?.safeConvertToColor() = this?.runCatching { Color.parse(this) }?.getOrNull()

internal object GetArrayOptColorWithStringFallback : ArrayOptColorWithStringFallback() {
    override val name: String = "getArrayOptColor"
}

internal object GetOptColorFromArrayWithStringFallback : ArrayOptColorWithStringFallback() {
    override val name: String = "getOptColorFromArray"
}

internal abstract class ArrayOptColorWithColorFallback : ArrayOptFunction(EvaluableType.COLOR) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? Color) ?: (evaluateSafe as? String)?.runCatching {
            Color.parse(this)
        }?.getOrNull() ?: args[2] // fallback
    }
}

internal object GetArrayOptColorWithColorFallback : ArrayOptColorWithColorFallback() {
    override val name: String = "getArrayOptColor"
}

internal object GetOptColorFromArrayWithColorFallback : ArrayOptColorWithColorFallback() {
    override val name: String = "getOptColorFromArray"
}

internal abstract class ArrayUrl : ArrayFunction(EvaluableType.URL) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluate(name, args)
        return (result as? String)?.safeConvertToUrl() ?: throwWrongTypeException(name, args, resultType, result)
    }
}

internal object GetArrayUrl : ArrayUrl() {
    override val name: String = "getArrayUrl"
}

internal object GetUrlFromArray : ArrayUrl() {
    override val name: String = "getUrlFromArray"
}

internal abstract class ArrayOptUrlWithStringFallback : ArrayOptFunction(EvaluableType.URL) {

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
        return (evaluateSafe(name, args) as? String).safeConvertToUrl()
            ?: (args[2] as String).safeConvertToUrl() // fallback
            ?: throwArrayException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal fun String?.safeConvertToUrl() = this?.runCatching { Url.from(this) }?.getOrNull()

internal object GetArrayOptUrlWithStringFallback : ArrayOptUrlWithStringFallback() {
    override val name: String = "getArrayOptUrl"
}

internal object GetOptUrlFromArrayWithStringFallback : ArrayOptUrlWithStringFallback() {
    override val name: String = "getOptUrlFromArray"
}

internal abstract class ArrayOptUrlWithUrlFallback : ArrayOptFunction(EvaluableType.URL) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = (evaluateSafe(name, args) as? String).safeConvertToUrl() ?: args[2] // fallback
}

internal object GetArrayOptUrlWithUrlFallback : ArrayOptUrlWithUrlFallback() {
    override val name: String = "getArrayOptUrl"
}

internal object GetOptUrlFromArrayWithUrlFallback : ArrayOptUrlWithUrlFallback() {
    override val name: String = "getOptUrlFromArray"
}

internal abstract class ArrayBoolean : ArrayFunction(EvaluableType.BOOLEAN) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluate(name, args)
        return result as? Boolean ?: throwWrongTypeException(name, args, resultType, result)
    }
}

internal object GetArrayBoolean : ArrayBoolean() {
    override val name: String = "getArrayBoolean"
}

internal object GetBooleanFromArray : ArrayBoolean() {
    override val name: String = "getBooleanFromArray"
}

internal abstract class ArrayOptBoolean : ArrayOptFunction(EvaluableType.BOOLEAN) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any = evaluateSafe(name, args) as? Boolean ?: args[2] // fallback
}

internal object GetArrayOptBoolean : ArrayOptBoolean() {
    override val name: String = "getArrayOptBoolean"
}

internal object GetOptBooleanFromArray : ArrayOptBoolean() {
    override val name: String = "getOptBooleanFromArray"
}

internal object GetArrayFromArray : ArrayFunction(EvaluableType.ARRAY) {

    override val name: String = "getArrayFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluate(name, args)
        return result as? JSONArray ?: throwWrongTypeException(name, args, resultType, result)
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
    ): Any = evaluateSafe(name, args) as? JSONArray ?: JSONArray()
}

internal object GetDictFromArray : ArrayFunction(EvaluableType.DICT) {

    override val name: String = "getDictFromArray"

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluate(name, args)
        return result as? JSONObject ?: throwWrongTypeException(name, args, resultType, result)
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
    ): Any = evaluate(name, args) as? JSONObject ?: JSONObject()
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

internal fun evaluate(functionName: String, args: List<Any>): Any {
    checkIndexOfBoundException(functionName, args)
    val array = args[0] as JSONArray
    val index = args[1] as Long
    return array.get(index.toInt())
}

internal fun evaluateSafe(functionName: String, args: List<Any>): Any? {
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
        throwArrayException(
            functionName = functionName,
            args = args,
            message = "Requested index (${index}) out of bounds array size ($arraySize)."
        )
    }
}

internal fun throwWrongTypeException(
    functionName: String,
    args: List<Any>,
    expected: EvaluableType,
    actual: Any
) {
    val actualType = when (actual) {
        JSONObject.NULL -> "Null"
        is Number -> "Number"
        is JSONObject -> "Dict"
        is JSONArray -> "Array"
        else -> actual.javaClass.simpleName
    }
    throwArrayException(functionName, args,
        "Incorrect value type: expected ${expected.typeName}, got $actualType.")
}

internal fun throwArrayException(functionName: String, args: List<Any>, message: String): Nothing =
    throwException("array", functionName, args, message)

internal fun throwException(type: String, functionName: String, args: List<Any>, message: String): Nothing {
    val signature = args.subList(1, args.size)
        .joinToString(prefix = "${functionName}(<$type>, ", postfix = ")") {
            it.toMessageFormat()
        }
    throwExceptionOnEvaluationFailed(signature, message)
}
