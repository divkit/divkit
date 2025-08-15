package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_DIVISION_BY_ZERO
import com.yandex.div.evaluable.REASON_EMPTY_ARGUMENT_LIST
import com.yandex.div.evaluable.internal.Token
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.withSign

internal object DoubleSum : Function() {

    override val name = "sum"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.NUMBER, isVariadic = true))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return args.fold(initial = 0.0) { sum, arg ->
            Evaluator.evalSum(Token.Operator.Binary.Sum.Plus, sum, arg) as Double
        }
    }
}

internal object DoubleSub : Function() {

    override val name = "sub"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.NUMBER, isVariadic = true))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return args.foldIndexed(initial = 0.0) { index, acc, arg ->
            if (index == 0) {
                arg
            } else {
                Evaluator.evalSum(Token.Operator.Binary.Sum.Minus, acc, arg)
            } as Double
        }
    }
}

internal object DoubleMul : Function() {

    override val name = "mul"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.NUMBER, isVariadic = true))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return args.foldIndexed(initial = 0.0) { index, acc, arg ->
            if (index == 0) {
                arg
            } else {
                Evaluator.evalFactor(Token.Operator.Binary.Factor.Multiplication, acc, arg)
            } as Double
        }
    }
}

internal object DoubleDiv : Function() {

    override val name = "div"

    override val declaredArgs =
        listOf(FunctionArgument(EvaluableType.NUMBER), FunctionArgument(EvaluableType.NUMBER))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val dividend = args.first() as Double
        val divisor = args.last() as Double
        if (divisor == 0.0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_DIVISION_BY_ZERO)
        }
        return dividend / divisor
    }
}

internal object DoubleMod : Function() {

    override val name = "mod"

    override val declaredArgs =
        listOf(FunctionArgument(EvaluableType.NUMBER), FunctionArgument(EvaluableType.NUMBER))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val dividend = args.first() as Double
        val divisor = args.last() as Double
        if (divisor == 0.0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_DIVISION_BY_ZERO)
        }
        return dividend % divisor
    }
}

internal object DoubleMaxValue : Function() {

    override val name = "maxNumber"

    override val declaredArgs = emptyList<FunctionArgument>()

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = Double.MAX_VALUE
}

internal object DoubleMinValue : Function() {

    override val name = "minNumber"

    override val declaredArgs = emptyList<FunctionArgument>()

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = Double.MIN_VALUE
}

internal object DoubleMax : Function() {

    override val name = "max"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.NUMBER, isVariadic = true))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        if (args.isEmpty()) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_EMPTY_ARGUMENT_LIST)
        }
        return args.fold(initial = args.first()) { max, arg ->
            max(max as Double, arg as Double)
        }
    }
}

internal object DoubleMin : Function() {

    override val name = "min"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.NUMBER, isVariadic = true))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        if (args.isEmpty()) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_EMPTY_ARGUMENT_LIST)
        }
        return args.fold(initial = args.first()) { min, arg ->
            min(min as Double, arg as Double)
        }
    }
}

internal object DoubleAbs : Function() {

    override val name = "abs"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return abs(args.first() as Double)
    }
}

internal object DoubleRound : Function() {

    override val name = "round"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val value = args.first() as Double
        return value.sign * floor(abs(value) + 0.5)
    }
}

internal object DoubleFloor : Function() {

    override val name = "floor"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return floor(args.first() as Double)
    }
}

internal object DoubleCeil : Function() {

    override val name = "ceil"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return ceil(args.first() as Double)
    }
}

internal object DoubleSignum : Function() {

    override val name = "signum"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return (args.first() as Double).sign
    }
}

internal object DoubleCopySign : Function() {

    override val name = "copySign"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.NUMBER),
        FunctionArgument(type = EvaluableType.NUMBER)
    )

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val magnitude = args.first() as Double
        val sign = args.last() as Double
        return magnitude.withSign(sign)
    }
}
