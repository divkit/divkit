package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.throwExceptionOnEvaluationFailed
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

internal object Pi : Function() {
    override val name = "pi"
    override val declaredArgs = emptyList<FunctionArgument>()
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ) = Math.PI
}

internal object RadiansToDegrees : Function() {
    override val name = "toDegrees"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        return Math.toDegrees(args.first() as Double)
    }
}

internal object DegreesToRadians : Function() {
    override val name = "toRadians"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        return Math.toRadians(args.first() as Double)
    }
}

internal object Sine : Function() {
    override val name = "sin"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        return sin(args.first() as Double)
    }
}

internal object Cos: Function() {
    override val name = "cos"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        return cos(args.first() as Double)
    }
}

internal object Tan: Function() {
    override val name = "tan"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        return tan(args.first() as Double)
    }
}

internal object Asin : Function() {
    override val name = "asin"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        val x = args.first() as Double
        val result = asin(x)
        return evaluateMathResult(result, name, x)
    }
}

internal object Acos : Function() {
    override val name = "acos"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        val x = args.first() as Double
        val result = acos(x)
        return evaluateMathResult(result, name, x)
    }
}

internal object Atan : Function() {
    override val name = "atan"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        return atan(args.first() as Double)
    }
}

/**
 * Returns the angle `theta` of the polar coordinates `(r, theta)` that correspond
 * to the rectangular coordinates `(x, y)` by computing the arc tangent of the value y / x;
 * the returned value is an angle in the range from `-PI` to `PI` radians.
 *
 * Special cases:
 *   - `atan2(0.0, 0.0)` is `0.0`
 *   - `atan2(0.0, x)` is  `0.0` for `x > 0` and `PI` for `x < 0`
 *   - `atan2(-0.0, x)` is `-0.0` for 'x > 0` and `-PI` for `x < 0`
 *   - `atan2(y, +Inf)` is `0.0` for `0 < y < +Inf` and `-0.0` for '-Inf < y < 0`
 *   - `atan2(y, -Inf)` is `PI` for `0 < y < +Inf` and `-PI` for `-Inf < y < 0`
 *   - `atan2(y, 0.0)` is `PI/2` for `y > 0` and `-PI/2` for `y < 0`
 *   - `atan2(+Inf, x)` is `PI/2` for finite `x`y
 *   - `atan2(-Inf, x)` is `-PI/2` for finite `x`
 *   - `atan2(NaN, x)` and `atan2(y, NaN)` is `NaN`
 */
internal object Atan2 : Function() {
    override val name = "atan2"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER),
        FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        return atan2(args[0] as Double, args[1] as Double)// (y, x)
    }
}

internal object Cot : Function() {
    override val name = "cot"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        val x = args.first() as Double
        val result = cos(x) / sin(x)
        return evaluateMathResult(result, name, x)
    }
}

private fun evaluateMathResult(result: Double, name: String, args: Double): Any {
    if (!isValidTrigonometricResult(result)) throwIncorrectMathValueException(name, args)
    return result
}


private fun isValidTrigonometricResult(value: Double, threshold: Double = 1e10): Boolean {
    return when {
        value.isNaN() -> false
        abs(value) > threshold -> false
        else -> true
    }
}

private fun String.toMathFunctionDisplayName() = when (this) {
    "cot" -> "Cotangent"
    "acos" -> "Arccosine"
    "asin" -> "Arcsine"
    else -> this
}

private fun throwIncorrectMathValueException(
    name: String,
    args: Double,
) {
    val displayName = name.toMathFunctionDisplayName()
    val exceptionMessage = "$displayName is undefined for the given value."
    val expression = "$name($args)"
    throwExceptionOnEvaluationFailed(expression, exceptionMessage)
}


