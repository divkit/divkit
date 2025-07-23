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

internal abstract class DictInteger : Function() {

    open val isMethod = false

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
    ): Any {
        return when (val result = evaluate(name, args, isMethod)) {
            is Int -> result.toLong()
            is Long -> result
            is BigInteger -> throwException(name, args, "Integer overflow.", isMethod)
            is BigDecimal -> throwException(name, args, "Cannot convert value to integer.", isMethod)
            is Double -> {
                if (result < Long.MIN_VALUE || result > Long.MAX_VALUE) {
                    throwException(name, args, "Integer overflow.", isMethod)
                }
                val longResult = result.roundToLong()
                if (result - longResult == 0.0) return longResult
                throwException(name, args, "Cannot convert value to integer.", isMethod)
            }
            else -> throwWrongTypeException(name, args, resultType, result, isMethod)
        }
    }
}

internal object GetDictInteger : DictInteger() {
    override val name = "getDictInteger"
}

internal object GetIntegerFromDict : DictInteger() {
    override val name = "getIntegerFromDict"
}

internal object GetInteger : DictInteger() {
    override val name = "getInteger"
    override val isMethod = true
}

internal abstract class DictNumber : Function() {

    open val isMethod = false

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
    ): Any {
        val result = evaluate(name, args, isMethod)
        return (result as? Number)?.toDouble() ?: throwWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetDictNumber : DictNumber() {
    override val name = "getDictNumber"
}

internal object GetNumberFromDict : DictNumber() {
    override val name = "getNumberFromDict"
}

internal object GetNumber : DictNumber() {
    override val name = "getNumber"
    override val isMethod = true
}

internal abstract class DictString : Function() {

    open val isMethod = false

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
    ): Any {
        val result = evaluate(name, args, isMethod)
        return result as? String ?: throwWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetDictString : DictString() {
    override val name = "getDictString"
}

internal object GetStringFromDict : DictString() {
    override val name = "getStringFromDict"
}

internal object GetString : DictString() {
    override val name = "getString"
    override val isMethod = true
}

internal abstract class ColorFromDict : Function() {

    open val isMethod = false

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
    ): Any {
        val result = evaluate(name, args, isMethod)
        if (result !is String) throwWrongTypeException(name, args, resultType, result, isMethod)
        return runCatching {
            Color.parse(result)
        }.getOrElse {
            throwDictException(name, args, "Unable to convert value to Color, expected format #AARRGGBB.")
        }
    }
}

internal object GetColorFromDict : ColorFromDict() {
    override val name = "getColorFromDict"
}

internal object GetColor : ColorFromDict() {
    override val name = "getColor"
    override val isMethod = true
}

internal object GetDictColor : ColorFromDict() {
    override val name = "getDictColor"
}

internal abstract class UrlFromDict : Function() {

    open val isMethod = false

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
    ): Any {
        val result = evaluate(name, args, isMethod)
        return (result as? String)?.safeConvertToUrl()
            ?: throwWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetUrlFromDict : UrlFromDict() {
    override val name = "getUrlFromDict"
}

internal object GetDictUrl : UrlFromDict() {
    override val name = "getDictUrl"
}

internal object GetUrl : UrlFromDict() {
    override val name = "getUrl"
    override val isMethod = true
}

internal abstract class BooleanFromDict : Function() {

    open val isMethod = false

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
    ): Any {
        val result = evaluate(name, args, isMethod)
        return result as? Boolean ?: throwWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetBooleanFromDict : BooleanFromDict() {
    override val name = "getBooleanFromDict"
}

internal object GetDictBoolean : BooleanFromDict() {
    override val name = "getDictBoolean"
}

internal object GetBoolean : BooleanFromDict() {
    override val name = "getBoolean"
    override val isMethod = true
}

internal abstract class DictFromDict : Function() {

    open val isMethod = false

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
        val result = evaluate(name, args, isMethod)
        return result as? JSONObject ?: throwWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetDictFromDict: DictFromDict() {
    override val name = "getDictFromDict"
}

internal object GetDict : DictFromDict() {
    override val name = "getDict"
    override val isMethod = true
}

internal abstract class ArrayFromDict : Function() {

    open val isMethod = false

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
        val result = evaluate(name, args, isMethod)
        return result as? JSONArray ?: throwWrongTypeException(name, args, resultType, result, isMethod)
    }
}

internal object GetArrayFromDict: ArrayFromDict() {
    override val name = "getArrayFromDict"
}

internal object GetArray : ArrayFromDict() {
    override val name = "getArray"
    override val isMethod = true
}

internal fun evaluate(functionName: String, args: List<Any>, isMethod: Boolean = false): Any {
    var dict = args.first() as JSONObject?
    var propName: String
    for (i in 1 until args.size - 1) {
        propName = args[i] as String
        runCatching { dict = dict!!.opt(propName) as? JSONObject }.getOrElse {
            throwMissingPropertyException(functionName, args, propName, isMethod)
        }
    }
    propName = args.last() as String
    return runCatching { dict!!.get(propName) }.getOrElse {
        throwMissingPropertyException(functionName, args, propName, isMethod)
    }
}

private fun throwMissingPropertyException(functionName: String, args: List<Any>, propName: String, isMethod: Boolean = false): Nothing =
    throwException(functionName, args, "Missing property \"$propName\" in the dict.", isMethod)

internal fun throwWrongTypeException(functionName: String, args: List<Any>, expected: EvaluableType, actual: Any, isMethod: Boolean = false): Nothing {
    val actualType = when (actual) {
        JSONObject.NULL -> "Null"
        is Number -> "Number"
        is JSONObject -> "Dict"
        is JSONArray -> "Array"
        else -> actual.javaClass.simpleName
    }
    throwException(functionName, args,
        "Incorrect value type: expected ${expected.typeName}, got $actualType.", isMethod)
}

internal fun throwException(functionName: String, args: List<Any>, message: String, isMethod: Boolean = false): Nothing {
    val dictPrefix = if (isMethod) "" else "<dict>, "
    val signature = args.subList(1, args.size).joinToString(prefix = "${functionName}($dictPrefix", postfix = ")") {
        it.toMessageFormat()
    }
    throwExceptionOnEvaluationFailed(signature, message)
}

internal abstract class DictOptInteger : Function() {

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
        return when (val result = evaluateSafe(args, fallback)) {
            is Int -> result.toLong()
            is Long -> result
            else -> fallback
        }
    }
}

internal object GetDictOptInteger : DictOptInteger() {
    override val name = "getDictOptInteger"
}

internal object GetOptIntegerFromDict : DictOptInteger() {
    override val name = "getOptIntegerFromDict"
}

internal abstract class DictOptNumber : Function() {

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
        return (evaluateSafe(args, fallback) as? Number)?.toDouble() ?: fallback
    }
}

internal object GetDictOptNumber : DictOptNumber() {
    override val name = "getDictOptNumber"
}

internal object GetOptNumberFromDict : DictOptNumber() {
    override val name = "getOptNumberFromDict"
}

internal abstract class DictOptString : Function() {

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

internal object GetDictOptString : DictOptString() {
    override val name = "getDictOptString"
}

internal object GetOptStringFromDict : DictOptString() {
    override val name = "getOptStringFromDict"
}

internal abstract class DictOptColorWithStringFallback : Function() {

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
            ?: throwDictException(name, args, REASON_CONVERT_TO_COLOR)
    }
}

internal object GetDictOptColorWithStringFallback : DictOptColorWithStringFallback() {
    override val name = "getDictOptColor"
}

internal object GetOptColorFromDictWithStringFallback : DictOptColorWithStringFallback() {
    override val name = "getOptColorFromDict"
}

internal abstract class DictOptColorWithColorFallback : Function() {

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

internal object GetDictOptColorWithColorFallback : DictOptColorWithColorFallback() {
    override val name = "getDictOptColor"
}

internal object GetOptColorFromDictWithColorFallback : DictOptColorWithColorFallback() {
    override val name = "getOptColorFromDict"
}

internal abstract class DictOptUrlWithStringFallback : Function() {

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
        return (result as? String).safeConvertToUrl()
            ?: fallback.safeConvertToUrl()
            ?: throwDictException(name, args, REASON_CONVERT_TO_URL)
    }
}

internal object GetDictOptUrlWithStringFallback : DictOptUrlWithStringFallback() {
    override val name = "getDictOptUrl"
}

internal object GetOptUrlFromDictWithStringFallback : DictOptUrlWithStringFallback() {
    override val name = "getOptUrlFromDict"
}

internal abstract class DictOptUrlWithUrlFallback : Function() {

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

internal object GetDictOptUrlWithUrlFallback : DictOptUrlWithUrlFallback() {
    override val name = "getDictOptUrl"
}

internal object GetOptUrlFromDictWithUrlFallback : DictOptUrlWithUrlFallback() {
    override val name = "getOptUrlFromDict"
}

internal abstract class DictOptBoolean : Function() {

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

internal object GetDictOptBoolean : DictOptBoolean() {
    override val name = "getDictOptBoolean"
}

internal object GetOptBooleanFromDict : DictOptBoolean() {
    override val name = "getOptBooleanFromDict"
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

internal object GetDictLength : Function() {

    override val name = "len"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT),
    )

    override val resultType = EvaluableType.INTEGER
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val dict = args[0] as JSONObject
        return dict.length().toLong()
    }
}

internal object DictContainsKey : Function() {

    override val name = "containsKey"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT),
        FunctionArgument(type = EvaluableType.STRING)
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val dict = args[0] as JSONObject
        val key = args[1] as String
        return dict.has(key)
    }
}

internal object DictIsEmpty : Function() {
    override val name = "isEmpty"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT)
    )

    override val resultType = EvaluableType.BOOLEAN
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val dict = args[0] as JSONObject
        return dict.length() == 0
    }
}

internal abstract class GetKeysFromDict : Function() {

    open val isMethod = false

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT)
    )

    override val resultType = EvaluableType.ARRAY
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val dict = args[0] as JSONObject
        val keysList = JSONArray()
        dict.keys().forEach { key ->
            keysList.put(key)
        }
        return keysList
    }
}

internal object GetDictKeys: GetKeysFromDict() {
    override val name = "getDictKeys"
}

internal object GetKeys : GetKeysFromDict() {
    override val name = "getKeys"
    override val isMethod = true
}


internal abstract class GetValuesFromDict : Function() {

    open val isMethod = false

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DICT)
    )

    override val resultType = EvaluableType.ARRAY
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val dict = args[0] as JSONObject
        val names = JSONArray()
        dict.keys().forEach { key ->
            names.put(dict.get(key))
        }
        return names
    }
}

internal object GetDictValues: GetValuesFromDict() {
    override val name = "getDictValues"
}

internal object GetValues : GetValuesFromDict() {
    override val name = "getValues"
    override val isMethod = true
}


internal fun evaluateSafe(args: List<Any>, fallback: Any, defaultFallback: Boolean = false): Any? {
    val dictIndex = if (defaultFallback) 0 else 1
    var dict = args[dictIndex] as? JSONObject ?: return fallback
    for (i in dictIndex + 1 until args.size - 1) {
        dict = dict.optJSONObject(args[i] as String) ?: return fallback
    }
    return dict.opt(args.last() as String)
}

internal fun throwDictException(functionName: String, args: List<Any>, message: String): Nothing =
    throwException("dict", functionName, args, message)
