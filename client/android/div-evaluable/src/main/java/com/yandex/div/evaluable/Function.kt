package com.yandex.div.evaluable

abstract class Function {

    abstract val name: String
    abstract val declaredArgs: List<FunctionArgument>
    abstract val resultType: EvaluableType
    abstract val isPure: Boolean

    protected abstract fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any

    operator fun invoke(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluate(evaluationContext, expressionContext, args)
        if (EvaluableType.of(result) != resultType) {
            throw EvaluableException("Function returned ${EvaluableType.of(result)}, but $resultType was expected")
        }
        return result
    }

    internal fun matchesArguments(argTypes: List<EvaluableType>): MatchResult {
        return matchesArguments(argTypes) { type, declaredType -> type == declaredType }
    }

    internal fun matchesArgumentsWithCast(argTypes: List<EvaluableType>): MatchResult {
        return matchesArguments(argTypes) { type, declaredType ->
            type == declaredType || type.canCastTo(declaredType)
        }
    }

    private fun matchesArguments(
        argTypes: List<EvaluableType>,
        matches: (type: EvaluableType, declaredType: EvaluableType) -> Boolean
    ): MatchResult {
        val argumentMin = if (hasVarArg) declaredArgs.size - 1 else declaredArgs.size
        val argumentMax = if (hasVarArg) Int.MAX_VALUE else declaredArgs.size
        if (argTypes.size < argumentMin || argTypes.size > argumentMax) {
            return MatchResult.ArgCountMismatch(expected = argumentMin)
        }
        for (index in argTypes.indices) {
            val declaredArgType = declaredArgs[index.coerceAtMost(declaredArgs.lastIndex)].type
            if (!matches(argTypes[index], declaredArgType)) {
                return MatchResult.ArgTypeMismatch(expected = declaredArgType, actual = argTypes[index])
            }
        }
        return MatchResult.Ok
    }

    internal val hasVarArg: Boolean
        get() = declaredArgs.lastOrNull()?.isVariadic ?: false

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

            override fun evaluate(
                evaluationContext: EvaluationContext,
                expressionContext: ExpressionContext,
                args: List<Any>
            ): Any = true
        }
    }

    private fun EvaluableType.canCastTo(type: EvaluableType) = when {
        this == EvaluableType.INTEGER -> when (type) {
            EvaluableType.NUMBER -> true
            else -> false
        }
        else -> false
    }

    internal sealed class MatchResult {
        object Ok : MatchResult()
        class ArgCountMismatch(val expected: Int) : MatchResult()
        class ArgTypeMismatch(val expected: EvaluableType, val actual: EvaluableType) : MatchResult()
    }
}
