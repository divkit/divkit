package com.yandex.div.core.animation

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.actions.colorIntValue
import com.yandex.div.core.actions.doubleValue
import com.yandex.div.core.actions.logError
import com.yandex.div.core.actions.longValue
import com.yandex.div.core.expression.local.variableController
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.isAlternated
import com.yandex.div.core.util.isReversed
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.MissingVariableException
import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionAnimatorStart
import com.yandex.div2.DivAnimator
import com.yandex.div2.DivAnimatorBase
import com.yandex.div2.DivColorAnimator
import com.yandex.div2.DivCount
import com.yandex.div2.DivNumberAnimator

internal object DivVariableAnimatorBuilder {

    fun build(
        divView: Div2View,
        animator: DivAnimator,
        startAction: DivActionAnimatorStart,
        expressionResolver: ExpressionResolver
    ): Animator? {
        return when (animator) {
            is DivAnimator.Number -> {
                buildNumberAnimator(divView, animator.value, startAction, expressionResolver)
            }

            is DivAnimator.Color -> {
                buildColorAnimator(divView, animator.value, startAction, expressionResolver)
            }
        }
    }

    private fun buildNumberAnimator(
        divView: Div2View,
        animator: DivNumberAnimator,
        startAction: DivActionAnimatorStart,
        resolver: ExpressionResolver
    ): Animator? {
        return when (val variable = findVariable<Variable>(animator.variableName, resolver)) {
            is Variable.IntegerVariable -> buildIntegerAnimator(divView, animator, startAction, resolver, variable)
            is Variable.DoubleVariable -> buildDoubleAnimator(divView, animator, startAction, resolver, variable)

            else ->  {
                divView.logError(
                    MissingVariableException("Unable to find number variable with name '${animator.variableName}'")
                )
                null
            }
        }
    }

    private fun buildIntegerAnimator(
        divView: Div2View,
        animator: DivNumberAnimator,
        startAction: DivActionAnimatorStart,
        resolver: ExpressionResolver,
        variable: Variable.IntegerVariable
    ): Animator {
        val startValue = startAction.startValue?.longValue(resolver) ?: animator.startValue?.evaluate(resolver)
        val endValue = startAction.endValue?.longValue(resolver) ?: animator.endValue.evaluate(resolver)

        if (startValue != null) {
            variable.setValueDirectly(startValue)
        }
        return ObjectAnimator.ofInt(variable, IntegerValueProperty, endValue.toInt())
            .configure(divView, animator, startAction, resolver)
    }

    private fun buildDoubleAnimator(
        divView: Div2View,
        animator: DivNumberAnimator,
        startAction: DivActionAnimatorStart,
        resolver: ExpressionResolver,
        variable: Variable.DoubleVariable
    ): Animator {
        val startValue = startAction.startValue?.doubleValue(resolver) ?: animator.startValue?.evaluate(resolver)
        val endValue = startAction.endValue?.doubleValue(resolver) ?: animator.endValue.evaluate(resolver)

        if (startValue != null) {
            variable.setValueDirectly(startValue)
        }
        return ObjectAnimator.ofFloat(variable, NumberValueProperty, endValue.toFloat())
            .configure(divView, animator, startAction, resolver)
    }

    private fun buildColorAnimator(
        divView: Div2View,
        animator: DivColorAnimator,
        startAction: DivActionAnimatorStart,
        resolver: ExpressionResolver
    ): Animator? {
        val variable = findVariable<Variable.ColorVariable>(animator.variableName, resolver)
        if (variable == null) {
            divView.logError(MissingVariableException("Unable to find color variable with name '${animator.variableName}'"))
            return null
        }

        val startValue = startAction.startValue?.colorIntValue(resolver) ?: animator.startValue?.evaluate(resolver)
        val endValue = startAction.endValue?.colorIntValue(resolver) ?: animator.endValue.evaluate(resolver)

        if (startValue != null) {
            variable.setValueDirectly(Color(startValue))
        }
        return ObjectAnimator.ofArgb(variable, ColorIntValueProperty, endValue)
            .configure(divView, animator, startAction, resolver)
    }

    private fun ObjectAnimator.configure(
        divView: Div2View,
        animator: DivAnimatorBase,
        startAction: DivActionAnimatorStart,
        resolver: ExpressionResolver
    ): ObjectAnimator {
        val direction = startAction.direction?.evaluate(resolver) ?: animator.direction.evaluate(resolver)
        duration = startAction.duration?.evaluate(resolver) ?: animator.duration.evaluate(resolver)
        startDelay = startAction.startDelay?.evaluate(resolver) ?: animator.startDelay.evaluate(resolver)
        interpolator = (startAction.interpolator?.evaluate(resolver) ?: animator.interpolator.evaluate(resolver))
            .androidInterpolator(reverse = direction.isReversed)
        repeatCount = when (val count = startAction.repeatCount ?: animator.repeatCount) {
            is DivCount.Fixed -> (count.value.value.evaluate(resolver).toInt() - 1).coerceAtLeast(0)
            is DivCount.Infinity -> ValueAnimator.INFINITE
        }
        repeatMode = if (direction.isAlternated) ValueAnimator.REVERSE else ValueAnimator.RESTART
        animator.endActions?.let { endActions ->
            doOnEnd {
                endActions.forEach { action ->
                    divView.handleAction(action, DivActionReason.ANIMATION_END, resolver)
                }
            }
        }
        animator.cancelActions?.let { cancelActions ->
            doOnCancel {
                cancelActions.forEach { action ->
                    divView.handleAction(action, DivActionReason.ANIMATION_CANCEL, resolver)
                }
            }
        }
        return this
    }

    private inline  fun <reified T : Variable> findVariable(name: String, resolver: ExpressionResolver) =
        resolver.variableController?.getMutableVariable(name) as? T
}
