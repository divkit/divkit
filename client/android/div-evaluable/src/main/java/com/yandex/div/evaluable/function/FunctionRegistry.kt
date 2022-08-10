package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.FunctionProvider
import com.yandex.div.evaluable.toMessageFormat

internal class FunctionRegistry : FunctionProvider {

    private val knownFunctions = mutableMapOf<String, MutableList<Function>>()

    fun register(function: Function) {
        val overloadedFunctions = knownFunctions.getOrPut(key = function.name) { mutableListOf() }
        if (function !in overloadedFunctions) {
            overloadedFunctions += validateFunction(function, overloadedFunctions)
        }
    }

    private fun validateFunction(function: Function, overloadedFunctions: List<Function>): Function {
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
            when (val result = function.matchesArguments(args)) {
                is Function.MatchResult.Ok -> return function

                is Function.MatchResult.TooFewArguments -> {
                    throw EvaluableException("Too few arguments passed to function '$name': expected ${result.expected}, got ${result.actual}.")
                }

                is Function.MatchResult.TooManyArguments -> {
                    throw EvaluableException("Too many arguments passed to function '$name': expected ${result.expected}, got ${result.actual}.")
                }

                is Function.MatchResult.ArgTypeMismatch -> {
                    throw EvaluableException("Call of function '$name' has argument type mismatch: expected ${result.expected}, got ${result.actual}.")
                }
            }
        }

        return overloadedFunctions.find { function ->
            function.matchesArguments(args) == Function.MatchResult.Ok
        } ?: throw getFunctionArgumentsException(name, args)
    }

    private fun getFunctionArgumentsException(name: String, args: List<EvaluableType>): Exception {
        if (args.isEmpty()) {
            return EvaluableException("Non empty argument list is required for function '$name'.")
        }
        return EvaluableException("Function '${name}' has no matching override for given argument types: ${args.toMessageFormat()}.")
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
