package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.*
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url

internal object GetStoredIntegerValue : Function() {

    override val name = "getStoredIntegerValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // stored value name
            FunctionArgument(type = EvaluableType.INTEGER), // fallback
    )

    override val resultType = EvaluableType.INTEGER

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String
        val fallbackValue = args[1] as Long
        val storedValue = evaluationContext.storedValueProvider.get(storedValueName) as? Long

        return storedValue ?: fallbackValue
    }

}

internal object GetStoredNumberValue : Function() {

    override val name = "getStoredNumberValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // stored value name
            FunctionArgument(type = EvaluableType.NUMBER), // fallback
    )

    override val resultType = EvaluableType.NUMBER

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String
        val fallbackValue = args[1] as Number
        val storedValue = if (evaluationContext.storedValueProvider.get(storedValueName) is Long)
            null
        else
            evaluationContext.storedValueProvider.get(storedValueName) as? Number

        return storedValue ?: fallbackValue
    }

}

internal object GetStoredStringValue : Function() {

    override val name = "getStoredStringValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // stored value name
            FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override val resultType = EvaluableType.STRING

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String
        val fallbackValue = args[1] as String
        val storedValue = evaluationContext.storedValueProvider.get(storedValueName) as? String

        return storedValue ?: fallbackValue
    }

}

internal object GetStoredColorValueString : Function() {

    override val name = "getStoredColorValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // stored value name
            FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override val resultType = EvaluableType.COLOR

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String
        val fallbackValue = Color.parse(args[1] as String)
        val storedValue = evaluationContext.storedValueProvider.get(storedValueName) as? Color

        return storedValue ?: fallbackValue
    }

}

internal object GetStoredColorValue : Function() {

    override val name = "getStoredColorValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // stored value name
            FunctionArgument(type = EvaluableType.COLOR), // fallback
    )

    override val resultType = EvaluableType.COLOR

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String
        val fallbackValue = args[1] as Color
        val storedValue = evaluationContext.storedValueProvider.get(storedValueName) as? Color

        return storedValue ?: fallbackValue
    }

}

internal object GetStoredBooleanValue : Function() {

    override val name = "getStoredBooleanValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // stored value name
            FunctionArgument(type = EvaluableType.BOOLEAN), // fallback
    )

    override val resultType = EvaluableType.BOOLEAN

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String
        val fallbackValue = args[1] as Boolean
        val storedValue = evaluationContext.storedValueProvider.get(storedValueName) as? Boolean

        return storedValue ?: fallbackValue
    }

}

internal object GetStoredUrlValueWithStringFallback : Function() {

    override val name = "getStoredUrlValue"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override val resultType = EvaluableType.URL

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String
        val urlString = args[1] as String
        val storedValue = evaluationContext.storedValueProvider.get(storedValueName) as? Url
        return storedValue
            ?: urlString.safeConvertToUrl()
            ?: throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_URL)
    }

}

internal object GetStoredUrlValueWithUrlFallback: Function() {

    override val name = "getStoredUrlValue"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // stored value name
        FunctionArgument(type = EvaluableType.URL), // fallback
    )

    override val resultType = EvaluableType.URL

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val storedValueName = args[0] as String
        val storedValue = evaluationContext.storedValueProvider.get(storedValueName) as? Url
        return storedValue ?: (args[1] as Url)
    }

}
