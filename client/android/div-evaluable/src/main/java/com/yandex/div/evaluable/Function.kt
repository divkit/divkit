package com.yandex.div.evaluable

abstract class Function(open val variableProvider: VariableProvider? = null) {

    abstract val name: String
    abstract val declaredArgs: List<FunctionArgument>
    abstract val resultType: EvaluableType
    abstract val isPure: Boolean

    protected abstract fun evaluate(args: List<Any>): Any

    operator fun invoke(args: List<Any>): Any {
        val result = evaluate(args)
        if (EvaluableType.of(result) != resultType) {
            throw EvaluableException("Function returned ${EvaluableType.of(result)}, but  $resultType was expected")
        }
        return result
    }

    internal fun matchesArguments(argTypes: List<EvaluableType>): MatchResult {
        val argumentMin: Int
        val argumentMax: Int
        if (declaredArgs.isEmpty()) {
            argumentMin = 0
            argumentMax = 0
        } else {
            val hasVarArg = declaredArgs.last().isVariadic
            argumentMin = if (hasVarArg) declaredArgs.size - 1 else declaredArgs.size
            argumentMax = if (hasVarArg) Int.MAX_VALUE else declaredArgs.size
        }

        if (argTypes.size < argumentMin) {
            return MatchResult.TooFewArguments(expected = argumentMin, actual = argTypes.size)
        }
        if (argTypes.size > argumentMax) {
            return MatchResult.TooManyArguments(expected = argumentMax, actual = argTypes.size)
        }

        for (index in argTypes.indices) {
            val declaredArg = declaredArgs[index.coerceAtMost(declaredArgs.lastIndex)]
            if (argTypes[index] != declaredArg.type) {
                return MatchResult.ArgTypeMismatch(expected = declaredArg.type, actual = argTypes[index])
            }
        }
        return MatchResult.Ok
    }

    override fun toString(): String {
        return declaredArgs.joinToString(prefix = "${name}(", postfix = ")") { arg ->
            if (arg.isVariadic) "vararg ${arg.type}" else arg.type.toString()
        }
    }

    companion object {
        @JvmField
        val STUB = object : Function() {

            override val name = "stub"
            override val declaredArgs = emptyList<FunctionArgument>()
            override val resultType = EvaluableType.BOOLEAN
            override val isPure = true

            override fun evaluate(args: List<Any>): Any = true
        }
    }

    internal sealed class MatchResult {
        object Ok : MatchResult()
        class TooFewArguments(val expected: Int, val actual: Int) : MatchResult()
        class TooManyArguments(val expected: Int, val actual: Int) : MatchResult()
        class ArgTypeMismatch(val expected: EvaluableType, val actual: EvaluableType) : MatchResult()
    }
}
