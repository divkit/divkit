package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.VariableProvider
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.throwExceptionOnEvaluationFailed
import com.yandex.div.evaluable.toMessageFormat
import com.yandex.div.evaluable.types.Color
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

    override fun evaluate(args: List<Any>) = evaluate(name, args).let {
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

    override fun evaluate(args: List<Any>): Any {
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

    override fun evaluate(args: List<Any>) = evaluate(name, args).let {
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

    override fun evaluate(args: List<Any>): Any {
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

    override fun evaluate(args: List<Any>) = evaluate(name, args).let {
        it as? String ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetArrayOptString(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.STRING) {

    override val name: String = "getArrayOptString"

    override fun evaluate(args: List<Any>): Any {
        val fallback = args[2] as String
        return evaluateSafe(name, args) as? String ?: fallback
    }
}

internal class GetArrayColor(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.COLOR) {

    override val name: String = "getArrayColor"

    override fun evaluate(args: List<Any>) = evaluate(name, args).let {
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

    override fun evaluate(args: List<Any>): Any {
        val fallback = args[2] as String
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? Color) ?: (evaluateSafe as? String)?.runCatching {
            Color.parse(this)
        }?.getOrNull() ?: Color.parse(fallback)
    }
}

internal class GetArrayOptColorWithColorFallback(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.COLOR) {

    override val name: String = "getArrayOptColor"

    override fun evaluate(args: List<Any>): Any {
        val fallback = args[2] as Color
        val evaluateSafe = evaluateSafe(name, args)
        return (evaluateSafe as? Color) ?: (evaluateSafe as? String)?.runCatching {
            Color.parse(this)
        }?.getOrNull() ?: fallback
    }
}

internal class GetArrayBoolean(
    override val variableProvider: VariableProvider
) : ArrayFunction(variableProvider, EvaluableType.BOOLEAN) {

    override val name: String = "getArrayBoolean"

    override fun evaluate(args: List<Any>) = evaluate(name, args).let {
        it as? Boolean ?: throwWrongTypeException(name, args, resultType, it)
    }
}

internal class GetArrayOptBoolean(
    override val variableProvider: VariableProvider
) : ArrayOptFunction(variableProvider, EvaluableType.BOOLEAN) {

    override val name: String = "getArrayOptBoolean"

    override fun evaluate(args: List<Any>): Any {
        val fallback = args[2] as Boolean
        return evaluateSafe(name, args) as? Boolean ?: fallback
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
    if (index > arraySize) {
        throwException(
            functionName = functionName,
            args = args,
            message = "Requested index (${index}) out of bounds array size ($arraySize)"
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
        "Incorrect value type: expected \"${expected.typeName.lowercase()}\", got \"$actualType\"")
}

private fun throwException(functionName: String, args: List<Any>, message: String): Nothing {
    val signature = args.subList(1, args.size)
        .joinToString(prefix = "${functionName}(<array>, ", postfix = ")") {
            it.toMessageFormat()
        }
    throwExceptionOnEvaluationFailed(signature, message)
}