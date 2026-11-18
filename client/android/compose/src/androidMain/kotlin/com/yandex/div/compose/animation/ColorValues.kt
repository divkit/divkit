package com.yandex.div.compose.animation

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.core.internal.type.interpolateArgb
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionAnimatorStart
import com.yandex.div2.DivColorAnimator
import com.yandex.div2.DivTypedValue

internal class ColorValues(
    private val variable: Variable.ColorVariable,
    private val startValue: Color?,
    private val end: Color,
) : NumberValues {
    private var start: Color? = null

    override fun writeStart() {
        if (startValue != null) {
            variable.setValueDirectly(startValue)
        }
    }

    override fun captureStart() {
        getOrCaptureStart()
    }

    override fun set(fraction: Float) {
        val start = getOrCaptureStart()
        val value = Color(interpolateArgb(fraction, start.value, end.value))
        if (variable.getValue() != value) {
            variable.setValueDirectly(value)
        }
    }

    private fun getOrCaptureStart(): Color = start
        ?: (variable.getValue() as Color).also { start = it }
}

internal fun buildColorAnimation(
    localComponent: DivLocalComponent,
    animator: DivColorAnimator,
    action: DivActionAnimatorStart,
    durationScale: Float,
    reporter: DivReporter,
): NumberAnimation? {
    val resolver = localComponent.expressionResolver
    val variable = resolver.getVariable(animator.variableName)
    if (variable == null) {
        reporter.reportError("Color animator variable not found: '${animator.variableName}'")
        return null
    }
    if (variable !is Variable.ColorVariable) {
        reporter.reportError(
            "Color animator variable has incompatible type: '${animator.variableName}'"
        )
        return null
    }

    val startValue = action.startValue?.colorValue(resolver)
        ?: animator.startValue?.evaluate(resolver)
    val end = action.endValue?.colorValue(resolver)
        ?: animator.endValue.evaluate(resolver)
    val values = ColorValues(variable, startValue?.let(::Color), Color(end))
    return NumberAnimation(animator, action, localComponent, values, durationScale)
}

private fun DivTypedValue.colorValue(resolver: ExpressionResolver): Int? = when (this) {
    is DivTypedValue.Color -> value.value.evaluate(resolver)
    is DivTypedValue.Array,
    is DivTypedValue.Bool,
    is DivTypedValue.Dict,
    is DivTypedValue.Integer,
    is DivTypedValue.Number,
    is DivTypedValue.Str,
    is DivTypedValue.Url -> null
}
