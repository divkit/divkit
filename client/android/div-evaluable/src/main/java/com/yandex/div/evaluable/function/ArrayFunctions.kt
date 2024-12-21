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

    open val isMethod: Boolean = false
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
        return when (val result = evaluateArray(name, args, isMethod)) {
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
            else -> throwArrayWrongTypeException(name, args, resultType, result, isMethod)
        }
    }
}

internal object GetArrayInteger : ArrayInteger() {
    override val name: String = "getArrayInteger"
}

internal object GetIntegerFromArray : ArrayInteger() {
    override val name: String = "getIntegerFromArray"
}

internal object ArrayGetInteger : ArrayInteger() {
    override val name: String = "getInteger"
    override val isMethod: Boolean = true
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
        val result = evaluateArray(name, args, isMethod)
        return (result as? Number)?.toDouble() ?: throwArrayWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetArrayNumber : ArrayNumber() {
    override val name: String = "getArrayNumber"
}

internal object GetNumberFromArray : ArrayNumber() {
    override val name: String = "getNumberFromArray"
}

internal object ArrayGetNumber : ArrayNumber() {
    override val name: String = "getNumber"
    override val isMethod: Boolean = true
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
        val result = evaluateArray(name, args, isMethod)
        return result as? String ?: throwArrayWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetArrayString : ArrayString() {
    override val name: String = "getArrayString"
}

internal object GetStringFromArray : ArrayString() {
    override val name: String = "getStringFromArray"
}

internal object ArrayGetString : ArrayString() {
    override val name: String = "getString"
    override val isMethod: Boolean = true
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
        return when (val result = evaluateArray(name, args, isMethod)) {
            is Color -> result
            !is String -> throwArrayWrongTypeException(name, args, resultType, result, isMethod)
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

internal object ArrayGetColor : ArrayColor() {
    override val name: String = "getColor"
    override val isMethod: Boolean = true
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
        val result = evaluateArray(name, args, isMethod)
        return (result as? String)?.safeConvertToUrl() ?: throwArrayWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetArrayUrl : ArrayUrl() {
    override val name: String = "getArrayUrl"
}

internal object GetUrlFromArray : ArrayUrl() {
    override val name: String = "getUrlFromArray"
}

internal object ArrayGetUrl : ArrayUrl() {
    override val name: String = "getUrl"
    override val isMethod: Boolean = true
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
        val result = evaluateArray(name, args, isMethod)
        return result as? Boolean ?: throwArrayWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetArrayBoolean : ArrayBoolean() {
    override val name: String = "getArrayBoolean"
}

internal object GetBooleanFromArray : ArrayBoolean() {
    override val name: String = "getBooleanFromArray"
}

internal object ArrayGetBoolean : ArrayBoolean() {
    override val name: String = "getBoolean"
    override val isMethod: Boolean = true
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

internal abstract class ArrayFromArray : ArrayFunction(EvaluableType.ARRAY) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluateArray(name, args, isMethod)
        return result as? JSONArray ?: throwArrayWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetArrayFromArray : ArrayFromArray() {
    override val name: String = "getArrayFromArray"
}

internal object ArrayGetArray :  ArrayFromArray() {
    override val name: String = "getArray"
    override val isMethod: Boolean = true
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

internal abstract class ArrayDict : ArrayFunction(EvaluableType.DICT) {
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluateArray(name, args, isMethod)
        return result as? JSONObject ?: throwArrayWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetDictFromArray : ArrayDict() {
    override val name: String = "getDictFromArray"
}

internal object ArrayGetDict : ArrayDict() {
    override val name: String = "getDict"
    override val isMethod: Boolean = true
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
    ): Any = evaluateArray(name, args) as? JSONObject ?: JSONObject()
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

internal object ArrayIsEmpty : Function() {
    override val name: String = "isEmpty"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY)
    )

    override val resultType: EvaluableType = EvaluableType.BOOLEAN

    override val isPure: Boolean = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val array = args[0] as JSONArray
        return array.length() == 0
    }
}

internal object ArrayAvg : Function() {
    override val name: String = "avg"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY)
    )

    override val resultType: EvaluableType = EvaluableType.BOOLEAN

    override val isPure: Boolean = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Number {
        if (!args[0] is Array<Integer> && !args[0] is Array<Number>) {
            throwArrayWrongTypeException(name, args, resultType, result, isMethod)
        }
        val array = args[0] as Array<Number>
        return array.length == 0 ? 0 : array.sum() / array.length
    }
}

internal fun evaluateArray(functionName: String, args: List<Any>, isMethod: Boolean = false): Any {
    checkIndexOfBoundException(functionName, args, isMethod)
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

private fun checkIndexOfBoundException(functionName: String, args: List<Any>, isMethod: Boolean = false) {
    val arraySize = (args[0] as JSONArray).length()
    val index = args[1] as Long
    if (index >= arraySize) {
        throwArrayException(
            functionName = functionName,
            args = args,
            message = "Requested index (${index}) out of bounds array size ($arraySize).",
            isMethod = isMethod
        )
    }
}

internal fun throwArrayWrongTypeException(
    functionName: String,
    args: List<Any>,
    expected: EvaluableType,
    actual: Any,
    isMethod: Boolean = false
) {
    val actualType = when (actual) {
        JSONObject.NULL -> "Null"
        is Number -> "Number"
        is JSONObject -> "Dict"
        is JSONArray -> "Array"
        else -> actual.javaClass.simpleName
    }
    throwArrayException(functionName, args,
        "Incorrect value type: expected ${expected.typeName}, got $actualType.", isMethod)
}

internal fun throwArrayException(functionName: String, args: List<Any>, message: String, isMethod: Boolean = false): Nothing =
    throwException("array", functionName, args, message, isMethod)

internal fun throwException(type: String, functionName: String, args: List<Any>, message: String, isMethod: Boolean = false): Nothing {
    val prefix = if (isMethod) "" else "<$type>, "
    val signature = args.subList(1, args.size)
        .joinToString(prefix = "${functionName}($prefix", postfix = ")") {
            it.toMessageFormat()
        }
    throwExceptionOnEvaluationFailed(signature, message)
}
