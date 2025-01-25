package com.yandex.div.evaluable

class LocalFunctionProvider(private val functions: List<Function>) : FunctionProvider {

    override fun get(name: String, args: List<EvaluableType>): Function {
        findFunction(name) { matchesArguments(args) }?.let { return it }
        findFunction(name) { matchesArgumentsWithCast(args) }?.let { return it }
        throw MissingLocalFunctionException(name, args)
    }

    override fun getMethod(name: String, args: List<EvaluableType>): Function {
        findFunction(name) { matchesArguments(args) }?.let { return it }
        findFunction(name) { matchesArgumentsWithCast(args) }?.let { return it }
        throw MissingLocalFunctionException(name, args)
    }

    private fun findFunction(name: String, matcher: Function.() -> Function.MatchResult): Function? {
        val localFunctions = functions.filter { it.name == name && it.matcher() == Function.MatchResult.Ok }
        return when (localFunctions.size) {
            0 -> null
            1 -> return localFunctions[0]
            else -> throw EvaluableException("Function ${localFunctions[0]} declared multiple times.")
        }
    }
}
