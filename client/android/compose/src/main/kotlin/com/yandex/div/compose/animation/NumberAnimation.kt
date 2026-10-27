package com.yandex.div.compose.animation

import androidx.compose.animation.core.Easing
import androidx.compose.runtime.withFrameNanos
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.utils.toEasing
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionAnimatorStart
import com.yandex.div2.DivAnimationDirection
import com.yandex.div2.DivCount
import com.yandex.div2.DivNumberAnimator
import com.yandex.div2.DivTypedValue
import kotlin.math.floor

internal class NumberAnimation(
    private val cancelActions: List<DivAction>?,
    private val direction: DivAnimationDirection,
    private val durationMillis: Long,
    private val easing: Easing,
    private val endActions: List<DivAction>?,
    private val fixedIterations: Long?,
    private val localComponent: DivLocalComponent,
    private val numberValues: NumberValues,
    private val durationScale: Float,
    private val startDelayMillis: Long,
) {
    private val scaledDurationMillis = durationMillis * durationScale.toDouble()
    private val scaledStartDelayMillis = startDelayMillis * durationScale.toDouble()

    fun start() {
        if (scaledStartDelayMillis == 0.0) {
            numberValues.captureStart()
            applyProgress(
                iteration = 0L,
                rawProgress = if (scaledDurationMillis == 0.0) 1f else 0f,
            )
        }
    }

    suspend fun run() {
        val startFrameNanos = withFrameNanos { it }
        if (scaledDurationMillis == 0.0) {
            awaitStartDelay(startFrameNanos)
            numberValues.captureStart()
            applyProgress(iteration = 0L, rawProgress = 1f)
            return
        }
        if (scaledStartDelayMillis == 0.0) {
            applyProgress(iteration = 0L, rawProgress = 0f)
        }

        var startCaptured = scaledStartDelayMillis == 0.0
        while (true) {
            val frameNanos = withFrameNanos { it }
            val elapsedMillis = (frameNanos - startFrameNanos) / NANOS_PER_MILLISECOND
            if (elapsedMillis < scaledStartDelayMillis) continue
            if (!startCaptured) {
                numberValues.captureStart()
                startCaptured = true
            }

            val animationMillis = elapsedMillis - scaledStartDelayMillis
            val iterations = fixedIterations
            if (iterations != null && animationMillis >= scaledDurationMillis * iterations) {
                applyProgress(iterations - 1, 1f)
                return
            }

            val overallProgress = animationMillis / scaledDurationMillis
            val iteration = floor(overallProgress).toLong()
            if (overallProgress > 0.0 && overallProgress == iteration.toDouble()) {
                applyProgress(iteration - 1, 1f)
            } else {
                applyProgress(iteration, (overallProgress - iteration).toFloat())
            }
        }
    }

    fun cancel() {
        localComponent.handleActions(cancelActions.orEmpty(), DivActionSource.ANIMATION)
        localComponent.handleActions(endActions.orEmpty(), DivActionSource.ANIMATION)
    }

    fun complete() {
        localComponent.handleActions(endActions.orEmpty(), DivActionSource.ANIMATION)
    }

    fun end() {
        val terminalIteration = fixedIterations?.minus(1) ?: 0L
        applyProgress(terminalIteration, 1f)
        complete()
    }

    private suspend fun awaitStartDelay(startFrameNanos: Long) {
        while (scaledStartDelayMillis > 0.0) {
            val frameNanos = withFrameNanos { it }
            val elapsedMillis = (frameNanos - startFrameNanos) / NANOS_PER_MILLISECOND
            if (elapsedMillis >= scaledStartDelayMillis) return
        }
    }

    private fun applyProgress(iteration: Long, rawProgress: Float) {
        var progress = rawProgress
        if (direction.isAlternated && iteration % 2L == 1L) {
            progress = 1f - progress
        }
        if (direction.isReversed) {
            progress = 1f - progress
        }
        numberValues.set(easing.transform(progress))
    }
}

internal sealed interface NumberValues {
    fun captureStart()

    fun set(fraction: Float)
}

internal class IntegerValues(
    private val variable: Variable.IntegerVariable,
    private val end: Int,
) : NumberValues {
    private var start: Int? = null

    override fun captureStart() {
        getOrCaptureStart()
    }

    override fun set(fraction: Float) {
        val start = getOrCaptureStart()
        val value = (start + fraction * (end - start)).toInt().toLong()
        if (variable.getValue() != value) {
            variable.setValueDirectly(value)
        }
    }

    private fun getOrCaptureStart(): Int = start
        ?: (variable.getValue() as Long).toInt().also { start = it }
}

internal class DoubleValues(
    private val variable: Variable.DoubleVariable,
    private val end: Float,
) : NumberValues {
    private var start: Float? = null

    override fun captureStart() {
        getOrCaptureStart()
    }

    override fun set(fraction: Float) {
        val start = getOrCaptureStart()
        val value = (start + fraction * (end - start)).toDouble()
        if (variable.getValue() != value) {
            variable.setValueDirectly(value)
        }
    }

    private fun getOrCaptureStart(): Float = start
        ?: (variable.getValue() as Double).toFloat().also { start = it }
}

internal fun buildNumberAnimation(
    localComponent: DivLocalComponent,
    animator: DivNumberAnimator,
    action: DivActionAnimatorStart,
    durationScale: Float,
    reporter: DivReporter,
): NumberAnimation? {
    val resolver = localComponent.expressionResolver
    val variable = resolver.getVariable(animator.variableName)
    if (variable == null) {
        reporter.reportError("Number animator variable not found: '${animator.variableName}'")
        return null
    }

    val values = when (variable) {
        is Variable.IntegerVariable -> integerValues(variable, animator, action, resolver)
        is Variable.DoubleVariable -> doubleValues(variable, animator, action, resolver)
        else -> {
            reporter.reportError("Number animator variable has incompatible type: '${animator.variableName}'")
            return null
        }
    }
    return NumberAnimation(
        direction = (action.direction ?: animator.direction).evaluate(resolver),
        durationMillis = (action.duration ?: animator.duration).evaluate(resolver),
        easing = (action.interpolator ?: animator.interpolator).evaluate(resolver).toEasing(),
        fixedIterations = (action.repeatCount ?: animator.repeatCount).fixedIterations(resolver),
        localComponent = localComponent,
        numberValues = values,
        durationScale = durationScale,
        startDelayMillis = (action.startDelay ?: animator.startDelay).evaluate(resolver),
        cancelActions = animator.cancelActions,
        endActions = animator.endActions,
    )
}

private fun integerValues(
    variable: Variable.IntegerVariable,
    animator: DivNumberAnimator,
    action: DivActionAnimatorStart,
    resolver: ExpressionResolver,
): NumberValues {
    val explicitStart = action.startValue?.integerValue(resolver)
        ?: animator.startValue?.evaluate(resolver)
    val end = (action.endValue?.integerValue(resolver)
        ?: animator.endValue.evaluate(resolver)).toInt()
    if (explicitStart != null) {
        variable.setValueDirectly(explicitStart)
    }
    return IntegerValues(variable, end)
}

private fun doubleValues(
    variable: Variable.DoubleVariable,
    animator: DivNumberAnimator,
    action: DivActionAnimatorStart,
    resolver: ExpressionResolver,
): NumberValues {
    val explicitStart = action.startValue?.doubleValue(resolver)
        ?: animator.startValue?.evaluate(resolver)
    val end = (action.endValue?.doubleValue(resolver)
        ?: animator.endValue.evaluate(resolver)).toFloat()
    if (explicitStart != null) {
        variable.setValueDirectly(explicitStart)
    }
    return DoubleValues(variable, end)
}

private fun DivCount.fixedIterations(resolver: ExpressionResolver): Long? = when (this) {
    is DivCount.Fixed -> (value.value.evaluate(resolver).toInt() - 1).coerceAtLeast(0).toLong() + 1
    is DivCount.Infinity -> null
}

private fun DivTypedValue.integerValue(resolver: ExpressionResolver): Long? = when (this) {
    is DivTypedValue.Integer -> value.value.evaluate(resolver)
    is DivTypedValue.Array,
    is DivTypedValue.Bool,
    is DivTypedValue.Color,
    is DivTypedValue.Dict,
    is DivTypedValue.Number,
    is DivTypedValue.Str,
    is DivTypedValue.Url -> null
}

private fun DivTypedValue.doubleValue(resolver: ExpressionResolver): Double? = when (this) {
    is DivTypedValue.Integer -> value.value.evaluate(resolver).toDouble()
    is DivTypedValue.Number -> value.value.evaluate(resolver)
    is DivTypedValue.Array,
    is DivTypedValue.Bool,
    is DivTypedValue.Color,
    is DivTypedValue.Dict,
    is DivTypedValue.Str,
    is DivTypedValue.Url -> null
}

private val DivAnimationDirection.isAlternated: Boolean
    get() = when (this) {
        DivAnimationDirection.ALTERNATE, DivAnimationDirection.ALTERNATE_REVERSE -> true
        DivAnimationDirection.NORMAL, DivAnimationDirection.REVERSE -> false
    }

private val DivAnimationDirection.isReversed: Boolean
    get() = when (this) {
        DivAnimationDirection.ALTERNATE_REVERSE, DivAnimationDirection.REVERSE -> true
        DivAnimationDirection.ALTERNATE, DivAnimationDirection.NORMAL -> false
    }

private const val NANOS_PER_MILLISECOND = 1_000_000.0
