package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.FunctionProvider

internal class FunctionRegistry : FunctionProvider {

    private val knownFunctions = mutableMapOf<String, MutableList<Function>>()
    private val knownMethods = mutableMapOf<String, MutableList<Function>>()
    val exposedFunctions: Map<String, MutableList<Function>>
        get() = knownFunctions
    val exposedMethods: Map<String, MutableList<Function>>
        get() = knownMethods

    fun register(function: Function) {
        val overloadedFunctions = knownFunctions.getOrPut(key = function.name) { mutableListOf() }
        if (function !in overloadedFunctions) {
            overloadedFunctions += validateFunction(function, overloadedFunctions)
        }
    }

    fun registerMethod(method: Function) {
        val overloadedMethods = knownMethods.getOrPut(key = method.name) { mutableListOf() }
        if (method !in overloadedMethods) {
            overloadedMethods += validateFunction(method, overloadedMethods)
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

    override fun get(name: String, args: List<EvaluableType>): Function = get(name, args, false)

    override fun getMethod(name: String, args: List<EvaluableType>): Function = get(name, args, true)

    fun ensureRegistered(name: String, args: List<FunctionArgument>, resultType: EvaluableType, isMethod: Boolean,) {
        val overloadedFunctions = if (isMethod) {
            knownMethods.getOrElse(name) {
                throw EvaluableException("Unknown method name: '$name'.")
            }
        } else {
            knownFunctions.getOrElse(name) {
                throw EvaluableException("Unknown function name: '$name'.")
            }
        }

        if (overloadedFunctions.none { it.declaredArgs == args }) {
            throw EvaluableException("Function with declared args is not registered.")
        }
        if (overloadedFunctions.none { it.resultType == resultType }) {
            throw EvaluableException("Function with specified result type is not registered.")
        }
    }

    private fun get(name: String, args: List<EvaluableType>, isMethod: Boolean): Function {
        val overloaded = if (isMethod) {
            knownMethods.getOrElse(name) { throw EvaluableException("Unknown method name: $name.") }
        } else {
            knownFunctions.getOrElse(name) { throw EvaluableException("Unknown function name: $name.") }
        }

        if (overloaded.size == 1) {
            val function = overloaded.first()
            return function.withArgumentsValidation(args)
        }

        val matched = overloaded.find { function ->
            function.matchesArguments(args) == Function.MatchResult.Ok
        }

        if (matched != null) return matched

        return overloaded.singleOrNull {
            it.matchesArgumentsWithCast(args) == Function.MatchResult.Ok
        } ?: throw getFunctionArgumentsException(name, args)
    }
}
