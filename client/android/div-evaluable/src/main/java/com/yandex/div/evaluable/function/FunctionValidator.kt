package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.toMessageFormat

internal object FunctionValidator {

    fun validateFunction(function: Function): Function {
        val declaredArgs = function.declaredArgs
        for (index in 0 until declaredArgs.lastIndex) {
            val argument = declaredArgs[index]
            if (argument.isVariadic) throw EvaluableException("Variadic argument allowed at the end of list only")
        }
        return function
    }

    fun validateOverloading(nonValidatedFunction: Function, overloadedFunctions: List<Function>): Function {
        overloadedFunctions.forEach { function ->
            if (nonValidatedFunction.conflictsWith(function)) {
                throw EvaluableException("Function $function has conflict with $function")
            }
        }
        return nonValidatedFunction
    }
}

private fun Function.conflictsWith(other: Function): Boolean {
    if (name != other.name) {
        return false
    }

    val shorterArgumentList = if (declaredArgs.size < other.declaredArgs.size) declaredArgs else other.declaredArgs
    val longerArgumentList = if (shorterArgumentList == declaredArgs) other.declaredArgs else declaredArgs


    if (shorterArgumentList.isEmpty()) {
        if (longerArgumentList.firstOrNull()?.isVariadic == true) {
            return true
        }
        return false
    }

    for (index in 0 until shorterArgumentList.lastIndex) {
        if (shorterArgumentList[index].type != longerArgumentList[index].type) {
            return false
        }
    }

    if (shorterArgumentList.last().isVariadic) {
        val varargType = shorterArgumentList.last().type
        for (index in shorterArgumentList.lastIndex until longerArgumentList.size) {
            val argument = longerArgumentList[index]
            if (argument.type != varargType) {
                return false
            }
        }
        return true
    } else if (shorterArgumentList.size == longerArgumentList.size) {
        return shorterArgumentList.last().type == longerArgumentList.last().type
    } else if (longerArgumentList.size == shorterArgumentList.size + 1) {
        return !longerArgumentList.last().isVariadic
    } else {
        return false
    }
}

internal fun Function.withArgumentsValidation(args: List<EvaluableType>): Function {
    when (val result = matchesArguments(args)) {
        is Function.MatchResult.Ok -> return this

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

internal fun getFunctionArgumentsException(name: String, args: List<EvaluableType>): Exception {
    if (args.isEmpty()) {
        return EvaluableException("Non empty argument list is required for function '$name'.")
    }
    return EvaluableException("Function '${name}' has no matching override for given argument types: ${args.toMessageFormat()}.")
}
