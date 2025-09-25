package com.yandex.div.evaluable

import com.yandex.div.evaluable.internal.Mockable
import com.yandex.div.evaluable.internal.Token
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
import kotlin.math.abs

@Mockable
class Evaluator(val evaluationContext: EvaluationContext) {

    @Throws(EvaluableException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> eval(expr: Evaluable): T {
        return try {
            expr.eval(this) as T
        } catch (e: EvaluableException) {
            throw e
        } catch (e: Exception) {
            throw EvaluableException(e.message.orEmpty(), e)
        }
    }

    internal fun evalUnary(unary: Evaluable.Unary): Any {
        val literal: Any = eval(unary.expression)
        unary.updateIsCacheable(unary.expression.checkIsCacheable())
        return when (unary.token) {
            is Token.Operator.Unary.Plus -> {
                when (literal) {
                    is Long -> literal.unaryPlus()
                    is Double -> literal.unaryPlus()
                    else -> throwExceptionOnEvaluationFailed("+$literal", "A Number is expected after a unary plus.")
                }
            }
            is Token.Operator.Unary.Minus -> {
                when (literal) {
                    is Long -> literal.unaryMinus()
                    is Double -> literal.unaryMinus()
                    else -> throwExceptionOnEvaluationFailed("-$literal", "A Number is expected after a unary minus.")
                }
            }
            Token.Operator.Unary.Not -> {
                if (literal !is Boolean) {
                    val quote = if (literal is String) "'" else ""
                    throwExceptionOnEvaluationFailed(
                        "!$quote$literal$quote",
                        "A Boolean is expected after a unary not."
                    )
                }
                !literal
            }
            else -> throw EvaluableException("${unary.token} was incorrectly parsed as a unary operator.")
        }
    }

    internal fun evalBinary(binary: Evaluable.Binary): Any {
        val rawLeft: Any = eval(binary.left)
        binary.updateIsCacheable(binary.left.checkIsCacheable())
        // Logical
        if (binary.token is Token.Operator.Binary.Logical) {
            return evalLogical(binary.token, rawLeft) {
                val res : Any = eval(binary.right)
                binary.updateIsCacheable(binary.right.checkIsCacheable())
                res
            }
        }

        val rawRight: Any = eval(binary.right)
        binary.updateIsCacheable(binary.right.checkIsCacheable())
        val (left, right) = castArgumentsIfNeeded(rawLeft, rawRight)
        if (left.javaClass != right.javaClass) {
            throwExceptionOnEvaluationFailed(binary.token, left, right)
        }

        return when (binary.token) {
            is Token.Operator.Binary.Equality -> evalEquality(binary.token, left, right)
            is Token.Operator.Binary.Sum -> evalSum(binary.token, left, right)
            is Token.Operator.Binary.Factor -> evalFactor(binary.token, left, right)
            is Token.Operator.Binary.Comparison -> evalComparison(binary.token, left, right)
            else -> throwExceptionOnEvaluationFailed(binary.token, left, right)
        }
    }

    private fun evalLogical(
        operator: Token.Operator.Binary.Logical,
        left: Any,
        rightEvaluator: () -> Any
    ): Any {
        if (left !is Boolean) {
            throwExceptionOnEvaluationFailed("$left $operator ...","'$operator' must be called with boolean operands.")
        }
        if (operator is Token.Operator.Binary.Logical.Or && left) {
            return left
        }
        if (operator is Token.Operator.Binary.Logical.And && !left) {
            return left
        }
        val right: Any = rightEvaluator()
        if (right !is Boolean) {
            throwExceptionOnEvaluationFailed(operator, left, right)
        }
        return if (operator is Token.Operator.Binary.Logical.Or) {
            left || right
        } else {
            left && right
        }
    }

    private fun evalEquality(operator: Token.Operator.Binary.Equality, left: Any, right: Any): Any {
        return when (operator) {
            is Token.Operator.Binary.Equality.Equal -> left == right
            is Token.Operator.Binary.Equality.NotEqual -> left != right
        }
    }

    private fun evalComparison(
        operator: Token.Operator.Binary.Comparison,
        left: Any,
        right: Any
    ): Any {
        if (left is Double && right is Double) {
            return evalComparableTypes(operator, left, right)
        }
        if (left is Long && right is Long) {
            return evalComparableTypes(operator, left, right)
        }
        if (left is DateTime && right is DateTime) {
            return evalComparableTypes(operator, left, right)
        }
        throwExceptionOnEvaluationFailed(operator, left, right)
    }

    private fun <T : Comparable<T>> evalComparableTypes(
        operator: Token.Operator.Binary.Comparison,
        left: T, right: T
    ): Any {
        return when (operator) {
            is Token.Operator.Binary.Comparison.Less -> left < right
            is Token.Operator.Binary.Comparison.LessOrEqual -> left <= right
            is Token.Operator.Binary.Comparison.GreaterOrEqual -> left >= right
            is Token.Operator.Binary.Comparison.Greater -> left > right
        }
    }

    internal fun evalTernary(ternary: Evaluable.Ternary): Any {
        if (ternary.token is Token.Operator.TernaryIfElse) {
            val condition: Any = eval(ternary.firstExpression)
            ternary.updateIsCacheable(ternary.firstExpression.checkIsCacheable())
            if (condition !is Boolean) {
                throwExceptionOnEvaluationFailed(
                    "${ternary.firstExpression} ? ${ternary.secondExpression} : ${ternary.thirdExpression}",
                    "Ternary must be called with a Boolean value as a condition."
                )
            }
            return if (condition) {
                val res : Any = eval(ternary.secondExpression)
                ternary.updateIsCacheable(ternary.secondExpression.checkIsCacheable())
                res
            } else {
                val res : Any = eval(ternary.thirdExpression)
                ternary.updateIsCacheable(ternary.thirdExpression.checkIsCacheable())
                res
            }
        } else {
            throwExceptionOnEvaluationFailed(ternary.rawExpr, "${ternary.token} was incorrectly parsed as a ternary operator.")
        }
    }

    internal fun evalTry(tryEvaluable: Evaluable.Try): Any {
        return runCatching {
            eval<Any>(tryEvaluable.tryExpression).also {
                tryEvaluable.updateIsCacheable(tryEvaluable.tryExpression.checkIsCacheable())
            }
        }.getOrElse {
            eval<Any>(tryEvaluable.fallbackExpression).also {
                tryEvaluable.updateIsCacheable(tryEvaluable.fallbackExpression.checkIsCacheable())
            }
        }
    }

    internal fun evalMethodCall(methodCall: Evaluable.MethodCall): Any {
        val arguments = mutableListOf<Any>()
        for (arg in methodCall.arguments) {
            arguments.add(eval(arg))
            methodCall.updateIsCacheable(arg.checkIsCacheable())
        }
        val argTypes = arguments.map { arg ->
            EvaluableType.of(arg)
        }
        val function = try {
            evaluationContext.functionProvider.getMethod(methodCall.token.name, argTypes)
        } catch (e: EvaluableException) {
            throwExceptionOnMethodEvaluationFailed(methodCall.token.name, arguments, e.message ?: "", e)
        }

        val expressionContext = ExpressionContext(methodCall)

        methodCall.updateIsCacheable(function.isPure)
        return function.invoke(evaluationContext, expressionContext, castEvalArgumentsIfNeeded(function, arguments))
    }

    internal fun evalFunctionCall(functionCall: Evaluable.FunctionCall): Any {
        val arguments = mutableListOf<Any>()
        for (arg in functionCall.arguments) {
            arguments.add(eval(arg))
            functionCall.updateIsCacheable(arg.checkIsCacheable())
        }
        val argTypes = arguments.map { arg ->
            EvaluableType.of(arg)
        }
        val function = try {
            evaluationContext.functionProvider.get(functionCall.token.name, argTypes)
        } catch (e: EvaluableException) {
            throwExceptionOnFunctionEvaluationFailed(functionCall.token.name, arguments, e.message ?: "")
        }

        val expressionContext = ExpressionContext(functionCall)

        functionCall.updateIsCacheable(function.isPure)
        val castedArguments = castEvalArgumentsIfNeeded(function, arguments)
        try {
            return function.invoke(evaluationContext, expressionContext, castedArguments)
        } catch (_: IntegerOverflow) {
            throw IntegerOverflow(functionToMessageFormat(function.name, arguments))
        }
    }

    internal fun evalStringTemplate(stringTemplate: Evaluable.StringTemplate, rawExpression: String): String {
        val stringParts = mutableListOf<String>()
        val needEncode = rawExpression.contains("://")
        for (arg in stringTemplate.arguments) {
            val value: String = eval<Any>(arg).let {
                if (needEncode && it is Color) it.toEncodedString() else it.toString()
            }
            stringParts.add(value)
            stringTemplate.updateIsCacheable(arg.checkIsCacheable())
        }
        return stringParts.joinToString(separator = "")
    }

    internal fun evalValue(call: Evaluable.Value): Any {
        return when (val token = call.token) {
            is Token.Operand.Literal.Num -> token.value
            is Token.Operand.Literal.Bool -> token.value
            is Token.Operand.Literal.Str -> token.value
        }
    }

    internal fun evalVariable(call: Evaluable.Variable): Any {
        return evaluationContext.variableProvider.get(call.token.name)
            ?: throw MissingVariableException(variableName = call.token.name)
    }

    companion object {
        internal fun evalSum(operator: Token.Operator.Binary.Sum, left: Any, right: Any): Any {
            return when {
                left is String && right is String -> {
                    // Concatenation
                    when (operator) {
                        is Token.Operator.Binary.Sum.Plus -> "$left$right"
                        else -> throwExceptionOnEvaluationFailed(operator, left, right)
                    }
                }
                left is Long && right is Long -> {
                    when (operator) {
                        is Token.Operator.Binary.Sum.Plus -> {
                            val result = left + right
                            return if (left xor result and (right xor result) < 0L) {
                                throw IntegerOverflow("$left + $right")
                            } else {
                                result
                            }
                        }
                        is Token.Operator.Binary.Sum.Minus -> {
                            val result = left - right
                            return if (left xor right and (left xor result) < 0L) {
                                throw IntegerOverflow("$left - $right")
                            } else {
                                result
                            }
                        }
                    }
                }
                left is Double && right is Double -> {
                    when (operator) {
                        is Token.Operator.Binary.Sum.Plus -> left + right
                        is Token.Operator.Binary.Sum.Minus -> left - right
                    }
                }
                else -> throwExceptionOnEvaluationFailed(operator, left, right)
            }
        }

        internal fun evalFactor(
            operator: Token.Operator.Binary.Factor,
            left: Any,
            right: Any
        ): Any {
            return when {
                left is Long && right is Long -> {
                    when (operator) {
                        is Token.Operator.Binary.Factor.Multiplication -> {
                            val result = left * right
                            val absLeft: Long = abs(left)
                            val absRight: Long = abs(right)
                            return if (absLeft or absRight ushr 31 == 0L || (right == 0L || result / right == left) && (left != Long.MIN_VALUE || right != -1L)) {
                                result
                            } else {
                                throw IntegerOverflow("$left * $right")
                            }
                        }
                        is Token.Operator.Binary.Factor.Division -> {
                            if (right == 0L) {
                                throwExceptionOnEvaluationFailed("$left / $right", REASON_DIVISION_BY_ZERO)
                            }
                            left / right
                        }
                        is Token.Operator.Binary.Factor.Modulo -> {
                            if (right == 0L) {
                                throwExceptionOnEvaluationFailed("$left % $right", REASON_DIVISION_BY_ZERO)
                            }
                            left % right
                        }
                    }
                }
                left is Double && right is Double -> {
                    when (operator) {
                        is Token.Operator.Binary.Factor.Multiplication -> left * right
                        is Token.Operator.Binary.Factor.Division -> {
                            if (right == 0.0) {
                                throwExceptionOnEvaluationFailed("$left / $right", REASON_DIVISION_BY_ZERO)
                            }
                            left / right
                        }
                        is Token.Operator.Binary.Factor.Modulo -> {
                            if (right == 0.0) {
                                throwExceptionOnEvaluationFailed("$left % $right", REASON_DIVISION_BY_ZERO)
                            }
                            left % right
                        }
                    }
                }
                else -> throwExceptionOnEvaluationFailed(operator, left, right)
            }
        }
    }

    private fun castArgumentsIfNeeded(left: Any, right: Any): Pair<Any, Any> = when {
            left.javaClass == right.javaClass -> left to right
            left is Long && right is Double -> left.toDouble() to right
            left is Double && right is Long -> left to right.toDouble()
            else -> left to right
        }

    private fun castEvalArgumentsIfNeeded(function: Function, args: List<Any>): List<Any> {
        val declaredArgs = function.declaredArgs
        return args.mapIndexed { index, arg ->
            val declaredType = declaredArgs[index.coerceAtMost(declaredArgs.lastIndex)].type
            if (declaredType != EvaluableType.of(arg)) arg.castIfPossible(declaredType) else arg
        }
    }

    private fun Any.castIfPossible(type: EvaluableType) = when (this) {
        is Long -> when (type) {
            EvaluableType.NUMBER -> this.toDouble()
            else -> this
        }
        else -> this
    }
}
