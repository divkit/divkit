package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.FunctionProvider

internal class FunctionRegistry : FunctionProvider {

    private val knownFunctions = mutableMapOf<String, MutableList<Function>>()
    val exposedFunctions: Map<String, MutableList<Function>>
        get() = knownFunctions

    fun register(function: Function) {
        val overloadedFunctions = knownFunctions.getOrPut(key = function.name) { mutableListOf() }
        if (function !in overloadedFunctions) {
            overloadedFunctions += validateFunction(function, overloadedFunctions)
        }
    }

    private fun validateFunction(
        function: Function,
        overloadedFunctions: List<Function>
    ): Function {
        return FunctionValidator.validateOverloading(
            FunctionValidator.validateFunction(function),
            overloadedFunctions
        )
    }

    override fun get(name: String, args: List<EvaluableType>): Function {
        val overloadedFunctions = knownFunctions.getOrElse(name) {
            throw EvaluableException("Unknown function name: $name.")
        }

        if (overloadedFunctions.size == 1) {
            val function = overloadedFunctions.first()
            return function.withArgumentsValidation(args)
        }

        val matched = overloadedFunctions.find { function ->
            function.matchesArguments(args) == Function.MatchResult.Ok
        }

        if (matched != null) return matched

        return overloadedFunctions.singleOrNull {
            it.matchesArgumentsWithCast(args) == Function.MatchResult.Ok
        } ?: throw getFunctionArgumentsException(name, args)
    }

    fun ensureRegistered(name: String, args: List<FunctionArgument>, resultType: EvaluableType) {
        val overloadedFunctions = knownFunctions.getOrElse(name) {
            throw EvaluableException("Unknown function name: '$name'.")
        }
        if (overloadedFunctions.none { it.declaredArgs == args }) {
            throw EvaluableException("Function with declared args is not registered.")
        }
        if (overloadedFunctions.none { it.resultType == resultType }) {
            throw EvaluableException("Function with specified result type is not registered.")
        }
    }
}
