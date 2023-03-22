package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.*
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.types.Color

internal class GetIntegerValue(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getIntegerValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.INTEGER), // fallback
    )

    override val resultType = EvaluableType.INTEGER

    override val isPure = false

    override fun evaluate(args: List<Any>): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as Int
        val variableValue = variableProvider.get(variableName) as? Int

        return  variableValue ?: fallbackValue
    }

}

internal class GetNumberValue(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getNumberValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.NUMBER), // fallback
    )

    override val resultType = EvaluableType.NUMBER

    override val isPure = false

    override fun evaluate(args: List<Any>): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as Number
        val variableValue = if (variableProvider.get(variableName) is Int)
            null
        else
            variableProvider.get(variableName) as? Number

        return  variableValue ?: fallbackValue
    }

}

internal class GetStringValue(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getStringValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override val resultType = EvaluableType.STRING

    override val isPure = false

    override fun evaluate(args: List<Any>): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as String
        val variableValue = variableProvider.get(variableName) as? String

        return  variableValue ?: fallbackValue
    }

}

internal class GetColorValueString(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getColorValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.STRING), // fallback
    )

    override val resultType = EvaluableType.COLOR

    override val isPure = false

    override fun evaluate(args: List<Any>): Any {
        val variableName = args[0] as String
        val fallbackValue = Color.parse(args[1] as String)
        val variableValue = variableProvider.get(variableName) as? Color

        return  variableValue ?: fallbackValue
    }

}

internal class GetColorValue(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getColorValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.COLOR), // fallback
    )

    override val resultType = EvaluableType.COLOR

    override val isPure = false

    override fun evaluate(args: List<Any>): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as Color
        val variableValue = variableProvider.get(variableName) as? Color

        return  variableValue ?: fallbackValue
    }

}

internal class GetBooleanValue(override val variableProvider: VariableProvider) : Function(variableProvider) {

    override val name = "getBooleanValue"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING), // variable name
            FunctionArgument(type = EvaluableType.BOOLEAN), // fallback
    )

    override val resultType = EvaluableType.BOOLEAN

    override val isPure = false

    override fun evaluate(args: List<Any>): Any {
        val variableName = args[0] as String
        val fallbackValue = args[1] as Boolean
        val variableValue = variableProvider.get(variableName) as? Boolean

        return  variableValue ?: fallbackValue
    }

}
