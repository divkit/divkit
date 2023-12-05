package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.*
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url

internal object GetIntegerValue : Function() {

    override val name = "getIntegerValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.INTEGER), // fallback
    )

    override val resultType = EvaluableType.INTEGER

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as Long
        val variableValue = evaluationContext.variableProvider.get(variableName) as? Long

        return variableValue ?: fallbackValue
    }

}

internal object GetNumberValue : Function() {

    override val name = "getNumberValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.NUMBER), // fallback
    )

    override val resultType = EvaluableType.NUMBER

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as Number
        val variableValue = if (evaluationContext.variableProvider.get(variableName) is Long)
            null
        else
            evaluationContext.variableProvider.get(variableName) as? Number

        return  variableValue ?: fallbackValue
    }

}

internal object GetStringValue : Function() {

    override val name = "getStringValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override val resultType = EvaluableType.STRING

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as String
        val variableValue = evaluationContext.variableProvider.get(variableName) as? String

        return  variableValue ?: fallbackValue
    }

}

internal object GetColorValueString : Function() {

    override val name = "getColorValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override val resultType = EvaluableType.COLOR

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val variableName = args[0] as String
        val fallbackValue = Color.parse(args[1] as String)
        val variableValue = evaluationContext.variableProvider.get(variableName) as? Color

        return  variableValue ?: fallbackValue
    }

}

internal object GetColorValue : Function() {

    override val name = "getColorValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.COLOR), // fallback
    )

    override val resultType = EvaluableType.COLOR

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as Color
        val variableValue = evaluationContext.variableProvider.get(variableName) as? Color

        return  variableValue ?: fallbackValue
    }

}

internal object GetUrlValueWithStringFallback : Function() {

    override val name = "getUrlValue"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // variable name
        FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override val resultType = EvaluableType.URL

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val variableName = args[0] as String
        val urlString = args[1] as String
        val variableValue = evaluationContext.variableProvider.get(variableName) as? Url
        return variableValue ?:
            urlString.safeConvertToUrl() ?:
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_URL)
    }

}

internal object GetUrlValueWithUrlFallback : Function() {

    override val name = "getUrlValue"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // variable name
        FunctionArgument(type = EvaluableType.URL), // fallback
    )

    override val resultType = EvaluableType.URL

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val variableName = args[0] as String
        val variableValue = evaluationContext.variableProvider.get(variableName) as? Url
        return variableValue ?: (args[1] as Url)
    }

}

internal object GetBooleanValue : Function() {

    override val name = "getBooleanValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.BOOLEAN), // fallback
    )

    override val resultType = EvaluableType.BOOLEAN

    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as Boolean
        val variableValue = evaluationContext.variableProvider.get(variableName) as? Boolean

        return  variableValue ?: fallbackValue
    }

}
