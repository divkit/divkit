package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_DIVISION_BY_ZERO
import com.yandex.div.evaluable.REASON_EMPTY_ARGUMENT_LIST
import com.yandex.div.evaluable.REASON_INTEGER_OVERFLOW
import com.yandex.div.evaluable.internal.Token
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

internal object IntegerSum : Function() {

    override val name = "sum"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return args.fold(initial = 0L) { sum, arg ->
            Evaluator.evalSum(Token.Operator.Binary.Sum.Plus, sum, arg) as Long
        }
    }
}

internal object IntegerSub : Function() {

    override val name = "sub"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return args.foldIndexed(initial = 0L) { index, acc, arg ->
            if (index == 0) {
                arg
            } else {
                Evaluator.evalSum(Token.Operator.Binary.Sum.Minus, acc, arg)
            } as Long
        }
    }
}

internal object IntegerMul : Function() {

    override val name = "mul"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return args.foldIndexed(initial = 0L) { index, acc, arg ->
            if (index == 0) {
                arg
            } else {
                Evaluator.evalFactor(Token.Operator.Binary.Factor.Multiplication, acc, arg)
            } as Long
        }
    }
}

internal object IntegerDiv : Function() {

    override val name = "div"

    override val declaredArgs =
        listOf(FunctionArgument(EvaluableType.INTEGER), FunctionArgument(EvaluableType.INTEGER))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val dividend = args.first() as Long
        val divisor = args.last() as Long
        if (divisor == 0L) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_DIVISION_BY_ZERO)
        }
        return dividend / divisor
    }
}

internal object IntegerMod : Function() {

    override val name = "mod"

    override val declaredArgs =
        listOf(FunctionArgument(EvaluableType.INTEGER), FunctionArgument(EvaluableType.INTEGER))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val dividend = args.first() as Long
        val divisor = args.last() as Long
        if (divisor == 0L) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_DIVISION_BY_ZERO)
        }
        return dividend % divisor
    }
}

internal object IntegerMaxValue : Function() {

    override val name = "maxInteger"

    override val declaredArgs = emptyList<FunctionArgument>()

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>) = Long.MAX_VALUE
}

internal object IntegerMinValue : Function() {

    override val name = "minInteger"

    override val declaredArgs = emptyList<FunctionArgument>()

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>) = Long.MIN_VALUE
}

internal object IntegerMax : Function() {

    override val name = "max"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        if (args.isEmpty()) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_EMPTY_ARGUMENT_LIST.format(name))
        }
        return args.fold(initial = Long.MIN_VALUE) { max, arg ->
           max(max, arg as Long)
        }
    }
}

internal object IntegerMin : Function() {

    override val name = "min"

    override val declaredArgs =
        listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        if (args.isEmpty()) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_EMPTY_ARGUMENT_LIST.format(name))
        }
        return args.fold(initial = Long.MAX_VALUE) { min, arg ->
            min(min, arg as Long)
        }
    }
}

internal object IntegerAbs : Function() {

    override val name = "abs"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val value = args.first() as Long
        if (value == Long.MIN_VALUE) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_INTEGER_OVERFLOW)
        }
        return abs(value)
    }
}

internal object IntegerSignum : Function() {

    override val name = "signum"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return (args.first() as Long).sign.toLong()
    }
}

internal object IntegerCopySign : Function() {

    override val name = "copySign"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER),
        FunctionArgument(type = EvaluableType.INTEGER)
    )

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val magnitude = args.first() as Long
        val sign = (args.last() as Long).sign
        if (sign == 0) {
            return magnitude
        }
        if (magnitude == Long.MIN_VALUE) {
            if (sign == -1) {
                return magnitude
            } else {
                throwExceptionOnFunctionEvaluationFailed(name, args, REASON_INTEGER_OVERFLOW)
            }
        }
        return abs(magnitude) * sign
    }
}
