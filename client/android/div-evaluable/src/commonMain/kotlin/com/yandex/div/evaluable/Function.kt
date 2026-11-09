package com.yandex.div.evaluable

import kotlin.jvm.JvmField

public abstract class Function {

    public abstract val name: String
    public abstract val declaredArgs: List<FunctionArgument>
    public abstract val resultType: EvaluableType
    public abstract val isPure: Boolean

    protected abstract fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any

    public operator fun invoke(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val result = evaluate(evaluationContext, expressionContext, args)
        if (EvaluableType.of(result) != resultType) {
            throw EvaluableException(
                "Function $this returned ${EvaluableType.of(result)}, but $resultType was expected."
            )
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
        val argumentMin = declaredArgs.size
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

    public companion object {
        @JvmField
        public val STUB: Function = object : Function() {

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
        data object Ok : MatchResult()
        class ArgCountMismatch(val expected: Int) : MatchResult()
        class ArgTypeMismatch(val expected: EvaluableType, val actual: EvaluableType) : MatchResult()
    }
}
