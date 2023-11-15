package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_CONVERT_TO_COLOR
import com.yandex.div.evaluable.REASON_CONVERT_TO_URL
import com.yandex.div.evaluable.VariableProvider
import com.yandex.div.evaluable.throwExceptionOnEvaluationFailed
import com.yandex.div.evaluable.toMessageFormat
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.BigInteger

internal class GetDictInteger(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictInteger"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.INTEGER
    override val isPure = false

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

internal class GetIntegerFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getIntegerFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.INTEGER
    override val isPure = false

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

internal class GetDictNumber(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictNumber"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.NUMBER
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        when (it) {
            is Int -> it.toDouble()
            is Long -> it.toDouble()
            is BigDecimal -> it.toDouble()
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal class GetNumberFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getNumberFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.NUMBER
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        when (it) {
            is Int -> it.toDouble()
            is Long -> it.toDouble()
            is BigDecimal -> it.toDouble()
            else -> throwWrongTypeException(name, args, resultType, it)
        }
    }
}

internal class GetDictString(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictString"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        it as? String ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetStringFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getStringFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any = evaluate(name, args).let {
        it as? String ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetDictColor(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictColor"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit) = evaluate(name, args).let {
        (it as? String)?.runCatching {
            Color.parse(this)
        }?.getOrElse {
            throwException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
        } ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetColorFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getColorFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit) = evaluate(name, args).let {
        (it as? String)?.runCatching {
            Color.parse(this)
        }?.getOrElse {
            throwException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
        } ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetDictUrl(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictUrl"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit) = evaluate(name, args).let {
        (it as? String)?.safeConvertToUrl() ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetUrlFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getUrlFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit) = evaluate(name, args).let {
        (it as? String)?.safeConvertToUrl() ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetDictBoolean(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictBoolean"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit) = evaluate(name, args).let {
        it as? Boolean ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetBooleanFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getBooleanFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit) = evaluate(name, args).let {
        it as? Boolean ?: throwWrongTypeException(name, args, resultType, it)
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
        is Int, is Double -> "number"
        is JSONObject -> "dict"
        is JSONArray -> "array"
        else -> actual.javaClass.simpleName.lowercase()
    }
    throwException(functionName, args,
        "Incorrect value type: expected \"${expected.typeName.lowercase()}\", got \"$actualType\".")
}

private fun throwException(functionName: String, args: List<Any>, message: String): Nothing {
    val signature = args.subList(1, args.size).joinToString(prefix = "${functionName}(<dict>, ", postfix = ")") {
        it.toMessageFormat()
    }
    throwExceptionOnEvaluationFailed(signature, message)
}

internal class GetDictOptInteger(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictOptInteger"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.INTEGER
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
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

internal class GetOptIntegerFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getOptIntegerFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.INTEGER
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
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

internal class GetDictOptNumber(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictOptNumber"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.NUMBER), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.NUMBER
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
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

internal class GetOptNumberFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getOptNumberFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.NUMBER), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.NUMBER
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
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

internal class GetDictOptString(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictOptString"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as String
        return evaluateSafe(args, fallback) as? String ?: fallback
    }
}

internal class GetOptStringFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getOptStringFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.STRING
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as String
        return evaluateSafe(args, fallback) as? String ?: fallback
    }
}

internal class GetDictOptColorWithStringFallback(
    override val variableProvider: VariableProvider
) : Function(variableProvider) {

    override val name = "getDictOptColor"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as String
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToColor()
            ?: fallback.safeConvertToColor()
            ?: throwException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal class GetDictOptColorWithColorFallback(
    override val variableProvider: VariableProvider
) : Function(variableProvider) {

    override val name = "getDictOptColor"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.COLOR), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as Color
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToColor() ?: fallback
    }
}

internal class GetOptColorFromDictWithStringFallback(
    override val variableProvider: VariableProvider
) : Function(variableProvider) {

    override val name = "getOptColorFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as String
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToColor()
            ?: fallback.safeConvertToColor()
            ?: throwException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal class GetOptColorFromDictWithColorFallback(
    override val variableProvider: VariableProvider
) : Function(variableProvider) {

    override val name = "getOptColorFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.COLOR), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.COLOR
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as Color
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToColor() ?: fallback
    }
}

internal class GetDictOptUrlWithStringFallback(
    override val variableProvider: VariableProvider) : Function(variableProvider
) {

    override val name = "getDictOptUrl"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as String
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToUrl() ?: fallback.safeConvertToUrl() ?:
        throwException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal class GetDictOptUrlWithUrlFallback(
    override val variableProvider: VariableProvider
) : Function(variableProvider) {

    override val name = "getDictOptUrl"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.URL), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as Url
        return (evaluateSafe(args, fallback) as? String)?.safeConvertToUrl() ?: fallback
    }
}

internal class GetOptUrlFromDictWithStringFallback(
    override val variableProvider: VariableProvider) : Function(variableProvider
) {

    override val name = "getOptUrlFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as String
        val result = evaluateSafe(args, fallback)
        return (result as? String).safeConvertToUrl() ?: fallback.safeConvertToUrl() ?:
        throwException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal class GetOptUrlFromDictWithUrlFallback(
    override val variableProvider: VariableProvider
) : Function(variableProvider) {

    override val name = "getOptUrlFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.URL), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.URL
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as Url
        return (evaluateSafe(args, fallback) as? String)?.safeConvertToUrl() ?: fallback
    }
}

internal class GetDictOptBoolean(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getDictOptBoolean"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.BOOLEAN), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as Boolean
        return evaluateSafe(args, fallback) as? Boolean ?: fallback
    }
}

internal class GetOptBooleanFromDict(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getOptBooleanFromDict"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.BOOLEAN), // fallback
        FunctionArgument(type = EvaluableType.DICT), // variable name
        FunctionArgument(type = EvaluableType.STRING, isVariadic = true) // property name
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = false

    override fun evaluate(args: List<Any>, onWarning: (String) -> Unit): Any {
        val fallback = args[0] as Boolean
        return evaluateSafe(args, fallback) as? Boolean ?: fallback
    }
}

private fun evaluateSafe(args: List<Any>, fallback: Any): Any? {
    var dict = args[1] as? JSONObject ?: return fallback
    for (i in 2 until args.size - 1) {
        dict = dict.optJSONObject(args[i] as String) ?: return fallback
    }
    return dict.opt(args.last() as String)
}
