package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.VariableProvider
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
    override val variableProvider: VariableProvider,
    final override val resultType: EvaluableType,
) : Function(variableProvider) {
    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER) // index at array
    )
    override val isPure: Boolean = false
}

internal abstract class ArrayOptFunction(
    override val variableProvider: VariableProvider,
    final override val resultType: EvaluableType,
) : Function(variableProvider) {
    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = resultType)
    )
    override val isPure: Boolean = false
}

internal class GetArrayInteger(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.INTEGER) {

    override val name: String = "getArrayInteger"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        when (it) {
            is Int -> it.toLong()
            is Long -> it
            is BigInteger -> throwException(name, args, "Integer overflow.")
            is BigDecimal -> throwException(name, args, "Cannot convert value to integer.")
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal class GetIntegerFromArray(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.INTEGER) {

    override val name: String = "getIntegerFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        when (it) {
            is Int -> it.toLong()
            is Long -> it
            is BigInteger -> throwException(name, args, "Integer overflow.")
            is BigDecimal -> throwException(name, args, "Cannot convert value to integer.")
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal class GetArrayOptInteger(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.INTEGER) {

    override val name: String = "getArrayOptInteger"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
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

internal class GetOptIntegerFromArray(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.INTEGER) {

    override val name: String = "getOptIntegerFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
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

internal class GetArrayNumber(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.NUMBER) {

    override val name: String = "getArrayNumber"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        when (it) {
            is Double -> it
            is Int -> it.toDouble()
            is Long -> it.toDouble()
            is BigDecimal -> it.toDouble()
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal class GetNumberFromArray(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.NUMBER) {

    override val name: String = "getNumberFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        when (it) {
            is Double -> it
            is Int -> it.toDouble()
            is Long -> it.toDouble()
            is BigDecimal -> it.toDouble()
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal class GetArrayOptNumber(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.NUMBER) {

    override val name: String = "getArrayOptNumber"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
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

internal class GetOptNumberFromArray(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.NUMBER) {

    override val name: String = "getOptNumberFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
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

internal class GetArrayString(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.STRING) {

    override val name: String = "getArrayString"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        it as? String ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetStringFromArray(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.STRING) {

    override val name: String = "getStringFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        it as? String ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetArrayOptString(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.STRING) {

    override val name: String = "getArrayOptString"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as String
        return evaluateSafe(name, args) as? String ?: fallback
    }
}

internal class GetOptStringFromArray(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.STRING) {

    override val name: String = "getOptStringFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as String
        return evaluateSafe(name, args) as? String ?: fallback
    }
}

internal class GetArrayColor(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.COLOR) {

    override val name: String = "getArrayColor"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        (it as? Color) ?: (it as? String)?.runCatching {
            Color.parse(this)
        }?.getOrElse {
            throwException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
        } ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetColorFromArray(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.COLOR) {

    override val name: String = "getColorFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        (it as? Color) ?: (it as? String)?.runCatching {
            Color.parse(this)
        }?.getOrElse {
            throwException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
        } ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetArrayOptColorWithStringFallback(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.COLOR) {

    override val name: String = "getArrayOptColor"
    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = EvaluableType.STRING) // fallback
    )

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as String
        val result = evaluateSafe(name, args)
        return (result as? String).safeConvertToColor() ?: fallback.safeConvertToColor() ?:
        throwException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal class GetOptColorFromArrayWithStringFallback(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.COLOR) {

    override val name: String = "getOptColorFromArray"
    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = EvaluableType.STRING) // fallback
    )

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as String
        val result = evaluateSafe(name, args)
        return (result as? String).safeConvertToColor() ?: fallback.safeConvertToColor() ?:
        throwException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal fun String?.safeConvertToColor() = this?.runCatching { Color.parse(this) }?.getOrNull()

internal class GetArrayOptColorWithColorFallback(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.COLOR) {

    override val name: String = "getArrayOptColor"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as Color
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? Color) ?: (evaluateSafe as? String)?.runCatching {
            Color.parse(this)
        }?.getOrNull() ?: fallback
    }
}

internal class GetOptColorFromArrayWithColorFallback(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.COLOR) {

    override val name: String = "getOptColorFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as Color
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? Color) ?: (evaluateSafe as? String)?.runCatching {
            Color.parse(this)
        }?.getOrNull() ?: fallback
    }
}

internal class GetArrayUrl(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.URL) {

    override val name: String = "getArrayUrl"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        (it as? String)?.safeConvertToUrl() ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetUrlFromArray(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.URL) {

    override val name: String = "getUrlFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        (it as? String)?.safeConvertToUrl() ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetArrayOptUrlWithStringFallback(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.URL) {

    override val name: String = "getArrayOptUrl"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = EvaluableType.STRING) // fallback
    )

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as String
        val result = evaluateSafe(name, args)
        return (result as? String).safeConvertToUrl() ?: fallback.safeConvertToUrl()
        ?: throwException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal class GetOptUrlFromArrayWithStringFallback(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.URL) {

    override val name: String = "getOptUrlFromArray"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER), // index at array
        FunctionArgument(type = EvaluableType.STRING) // fallback
    )

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as String
        val result = evaluateSafe(name, args)
        return (result as? String).safeConvertToUrl() ?: fallback.safeConvertToUrl()
        ?: throwException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal fun String?.safeConvertToUrl() = this?.runCatching { Url.from(this) }?.getOrNull()

internal class GetArrayOptUrlWithUrlFallback(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.URL) {

    override val name: String = "getArrayOptUrl"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as Url
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? String).safeConvertToUrl() ?: fallback
    }
}

internal class GetOptUrlFromArrayWithUrlFallback(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.URL) {

    override val name: String = "getOptUrlFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as Url
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? String).safeConvertToUrl() ?: fallback
    }
}

internal class GetArrayBoolean(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.BOOLEAN) {

    override val name: String = "getArrayBoolean"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        it as? Boolean ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetBooleanFromArray(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.BOOLEAN) {

    override val name: String = "getBooleanFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        it as? Boolean ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetArrayOptBoolean(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.BOOLEAN) {

    override val name: String = "getArrayOptBoolean"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as Boolean
        return evaluateSafe(name, args) as? Boolean ?: fallback
    }
}

internal class GetOptBooleanFromArray(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.BOOLEAN) {

    override val name: String = "getOptBooleanFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[2] as Boolean
        return evaluateSafe(name, args) as? Boolean ?: fallback
    }
}

internal class GetArrayFromArray(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.ARRAY) {
    override val name: String = "getArrayFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any =
        evaluate(name, args).let {
            it as? JSONArray ?: throwWrongTypeException(name, args, resultType, it)
        }
}

internal class GetOptArrayFromArray(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.ARRAY) {
    override val name: String = "getOptArrayFromArray"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER) // index at array
    )

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any =
        evaluateSafe(name, args).let {
            it as? JSONArray ?: JSONArray()
        }
}

internal class GetDictFromArray(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.DICT) {
    override val name: String = "getDictFromArray"

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any =
        evaluate(name, args).let {
            it as? JSONObject ?: throwWrongTypeException(name, args, resultType, it)
        }
}

internal class GetOptDictFromArray(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.DICT) {
    override val name: String = "getOptDictFromArray"

    override val declaredArgs: List<FunctionArgument> = listOf(
        FunctionArgument(type = EvaluableType.ARRAY), // variable name
        FunctionArgument(type = EvaluableType.INTEGER) // index at array
    )

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any =
        evaluate(name, args).let {
            it as? JSONObject ?: JSONObject()
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
        is Int, is Double -> "number"
        is JSONObject -> "dict"
        is JSONArray -> "array"
        else -> actual.javaClass.simpleName.lowercase()
    }
    throwException(functionName, args,
        "Incorrect value type: expected \"${expected.typeName.lowercase()}\", got \"$actualType\".")
}

private fun throwException(functionName: String, args: List<Any>, message: String): Nothing {
    val signature = args.subList(1, args.size)
        .joinToString(prefix = "${functionName}(<array>, ", postfix = ")") {
            it.toMessageFormat()
        }
    throwExceptionOnEvaluationFailed(signature, message)
}