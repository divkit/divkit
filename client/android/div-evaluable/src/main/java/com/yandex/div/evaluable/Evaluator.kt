package com.yandex.div.evaluable

import com.yandex.div.evaluable.internal.Token
import com.yandex.div.evaluable.types.DateTime

class Evaluator(
    private val variableProvider: VariableProvider,
    private val functionProvider: FunctionProvider
) {

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
        return when (unary.token) {
            is Token.Operator.Unary.Plus -> {
                when (literal) {
                    is Int -> literal.unaryPlus()
                    is Double -> literal.unaryPlus()
                    else -> throwExceptionOnEvaluationFailed("+$literal", "A Number is expected after a unary plus.")
                }
            }
            is Token.Operator.Unary.Minus -> {
                when (literal) {
                    is Int -> literal.unaryMinus()
                    is Double -> literal.unaryMinus()
                    else -> throwExceptionOnEvaluationFailed("-$literal", "A Number is expected after a unary minus.")
                }
            }
            Token.Operator.Unary.Not -> {
                if (literal !is Boolean) {
                    throwExceptionOnEvaluationFailed("!$literal", "A Boolean is expected after a unary not.")
                }
                !literal
            }
            else -> throw EvaluableException("${unary.token} was incorrectly parsed as a unary operator.")
        }
    }

    internal fun evalBinary(binary: Evaluable.Binary): Any {
        val left: Any = eval(binary.left)

        // Logical
        if (binary.token is Token.Operator.Binary.Logical) {
            return evalLogical(binary.token, left) { eval(binary.right) }
        }

        val right: Any = eval(binary.right)
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
        if (left is Int && right is Int) {
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
            val left: Any by lazy { eval(ternary.secondExpression) }
            val right: Any by lazy { eval(ternary.thirdExpression) }

            if (condition !is Boolean) {
                throwExceptionOnEvaluationFailed(
                    "${condition.toMessageFormat()} ? ${left.toMessageFormat()} : ${right.toMessageFormat()}",
                    "Ternary must be called with a Boolean value as a condition."
                )
            }
            return if (condition) {
                left
            } else {
                right
            }
        } else {
            throwExceptionOnEvaluationFailed(ternary.rawExpr, "${ternary.token} was incorrectly parsed as a ternary operator.")
        }
    }

    internal fun evalFunctionCall(functionCall: Evaluable.FunctionCall): Any {
        val arguments = mutableListOf<Any>()
        for (arg in functionCall.arguments) {
            arguments.add(eval(arg))
        }
        val argTypes = arguments.map { arg ->
            EvaluableType.of(arg)
        }
        val function = try {
            functionProvider.get(functionCall.token.name, argTypes)
        } catch (e: EvaluableException) {
            throwExceptionOnFunctionEvaluationFailed(functionCall.token.name, arguments, e.message ?: "")
        }
        return function.invoke(arguments)
    }

    internal fun evalStringTemplate(stringTemplate: Evaluable.StringTemplate): String {
        val stringParts = mutableListOf<String>()
        for (arg in stringTemplate.arguments) {
            val value: String = eval<Any>(arg).toString()
            stringParts.add(value)
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
        return variableProvider.get(call.token.name)
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
                left is Int && right is Int -> {
                    when (operator) {
                        is Token.Operator.Binary.Sum.Plus -> left + right
                        is Token.Operator.Binary.Sum.Minus -> left - right
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
                left is Int && right is Int -> {
                    when (operator) {
                        is Token.Operator.Binary.Factor.Multiplication -> left * right
                        is Token.Operator.Binary.Factor.Division -> {
                            if (right == 0) {
                                throwExceptionOnEvaluationFailed("$left / $right", REASON_DIVISION_BY_ZERO)
                            }
                            left / right
                        }
                        is Token.Operator.Binary.Factor.Modulo -> {
                            if (right == 0) {
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
                            if (right == 0f) {
                                throwExceptionOnEvaluationFailed("$left / $right", REASON_DIVISION_BY_ZERO)
                            }
                            left / right
                        }
                        is Token.Operator.Binary.Factor.Modulo -> {
                            if (right == 0f) {
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
}
