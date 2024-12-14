package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
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

internal object Tane : Function() {
    override val name = "tan"
    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
    override val resultType = EvaluableType.NUMBER
    override val isPure = true
    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>,
    ): Any {
        println("JAVA_LOG ${args.first()}  ${tan(args.first() as Double)}")
        return tan(args.first() as Double)
    }
}
