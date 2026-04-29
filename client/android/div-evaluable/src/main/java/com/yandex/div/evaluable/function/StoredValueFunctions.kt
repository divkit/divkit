package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_CONVERT_TO_URL
import com.yandex.div.evaluable.ScopedStoredValueProvider
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal

internal abstract class GetStoredIntegerValueBase : GetStoredValue<Long>() {
    override val name = "getStoredIntegerValue"
    override val resultType = EvaluableType.INTEGER
}

internal object GetStoredIntegerValue : GetStoredIntegerValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.INTEGER), // fallback
    )
}

internal object GetScopedStoredIntegerValue : GetStoredIntegerValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // scope
        FunctionArgument(type = EvaluableType.INTEGER), // fallback
    )
    override val hasScope = true
}

internal abstract class GetStoredNumberValueBase : GetStoredValue<Number>() {
    override val name = "getStoredNumberValue"
    override val resultType = EvaluableType.NUMBER
    override fun Any?.convert(fallback: () -> Number) = (if (this is Long) null else this as? Number) ?: fallback()
}

internal object GetStoredNumberValue : GetStoredNumberValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.NUMBER), // fallback
    )
}

internal object GetScopedStoredNumberValue : GetStoredNumberValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // scope
        FunctionArgument(type = EvaluableType.NUMBER), // fallback
    )
    override val hasScope = true
}

internal abstract class GetStoredStringValueBase : GetStoredValue<String>() {
    override val name = "getStoredStringValue"
    override val resultType = EvaluableType.STRING
}

internal object GetStoredStringValue : GetStoredStringValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // fallback
    )
}

internal object GetScopedStoredStringValue : GetStoredStringValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // scope
        FunctionArgument(type = EvaluableType.STRING), // fallback
    )
    override val hasScope = true
}

internal abstract class GetStoredColorValueBase : GetStoredValue<Color>() {
    override val name = "getStoredColorValue"
    override val resultType = EvaluableType.COLOR
}

internal object GetStoredColorValueString : GetStoredColorValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // fallback
    )
    override fun List<Any>.getFallback() = Color.parse(get(1) as String)
}

internal object GetScopedStoredColorValueString : GetStoredColorValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // scope
        FunctionArgument(type = EvaluableType.STRING), // fallback
    )
    override val hasScope = true
    override fun List<Any>.getFallback() = Color.parse(get(2) as String)
}

internal object GetStoredColorValue : GetStoredColorValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.COLOR), // fallback
    )
}

internal object GetScopedStoredColorValue : GetStoredColorValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // scope
        FunctionArgument(type = EvaluableType.COLOR), // fallback
    )
    override val hasScope = true
}

internal abstract class GetStoredBooleanValueBase : GetStoredValue<Boolean>() {
    override val name = "getStoredBooleanValue"
    override val resultType = EvaluableType.BOOLEAN
}

internal object GetStoredBooleanValue : GetStoredBooleanValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.BOOLEAN), // fallback
    )
}

internal object GetScopedStoredBooleanValue : GetStoredBooleanValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // scope
        FunctionArgument(type = EvaluableType.BOOLEAN), // fallback
    )
    override val hasScope = true
}

internal abstract class GetStoredUrlValueBase : GetStoredValue<Url>() {
    override val name = "getStoredUrlValue"
    override val resultType = EvaluableType.URL
}

internal object GetStoredUrlValueWithStringFallback : GetStoredUrlValueBase() {

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override fun List<Any>.getFallback(): Url {
        return (get(1) as String).safeConvertToUrl()
            ?: throwExceptionOnFunctionEvaluationFailed(name, this, REASON_CONVERT_TO_URL)
    }
}

internal object GetScopedStoredUrlValueWithStringFallback : GetStoredUrlValueBase() {

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // scope
        FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override val hasScope = true

    override fun List<Any>.getFallback(): Url {
        return (get(2) as String).safeConvertToUrl()
            ?: throwExceptionOnFunctionEvaluationFailed(name, this, REASON_CONVERT_TO_URL)
    }
}

internal object GetStoredUrlValueWithUrlFallback: GetStoredUrlValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.URL), // fallback
    )
}

internal object GetScopedStoredUrlValueWithUrlFallback : GetStoredUrlValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // scope
        FunctionArgument(type = EvaluableType.URL), // fallback
    )
    override val hasScope = true
}

internal abstract class GetStoredComplexValue<T : Any> : Function() {
    override val isPure = false
    open val hasScope = false

    @Suppress("UNCHECKED_CAST")
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String

        val storedValue = if (hasScope && evaluationContext.storedValueProvider is ScopedStoredValueProvider) {
            val scope = args[1] as String
            evaluationContext.storedValueProvider.get(storedValueName, scope)
        } else {
            evaluationContext.storedValueProvider.get(storedValueName)
        } ?: throwExceptionOnFunctionEvaluationFailed(name, args, "Missing value.")

        return storedValue as? T
            ?: throwWrongTypeException(name, args, resultType, storedValue)
    }

    private fun throwWrongTypeException(functionName: String, args: List<Any>, expected: EvaluableType, actual: Any): Nothing {
        val actualType = when (actual) {
            is Int, is Double, is BigDecimal -> "Number"
            is JSONObject -> "Dict"
            is JSONArray -> "Array"
            else -> actual.javaClass.simpleName
        }
        throwExceptionOnFunctionEvaluationFailed(functionName, args,
            "Incorrect value type: expected ${expected.typeName}, got $actualType.")
    }
}

internal abstract class GetStoredArrayValueBase : GetStoredComplexValue<JSONArray>() {
    override val name = "getStoredArrayValue"
    override val resultType = EvaluableType.ARRAY
}

internal object GetStoredArrayValue : GetStoredArrayValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING),
    )
}

internal object GetScopedStoredArrayValue: GetStoredArrayValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING),
        FunctionArgument(type = EvaluableType.STRING),
    )
    override val hasScope = true
}

internal abstract class GetStoredDictValueBase : GetStoredComplexValue<JSONObject>() {
    override val name = "getStoredDictValue"
    override val resultType = EvaluableType.DICT
}

internal object GetStoredDictValue : GetStoredDictValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING),
    )
}

internal object GetScopedStoredDictValue : GetStoredDictValueBase() {
    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING),
        FunctionArgument(type = EvaluableType.STRING),
    )
    override val hasScope = true
}

@Suppress("UNCHECKED_CAST")
internal abstract class GetStoredValue<T: Any>: Function() {

    override val isPure = false

    protected open val hasScope: Boolean = false
    protected val fallbackIndex = if (hasScope) 2 else 1

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String
        val fallback = { args.getFallback() }

        if (!hasScope || evaluationContext.storedValueProvider !is ScopedStoredValueProvider) {
            return evaluationContext.storedValueProvider.get(storedValueName).convert(fallback)
        }

        val scope = args[2] as String
        return evaluationContext.storedValueProvider.get(storedValueName, scope).convert(fallback)
    }

    protected open fun Any?.convert(fallback: () -> T): T = this as? T ?: fallback()

    protected open fun List<Any>.getFallback() = get(fallbackIndex) as T
}
