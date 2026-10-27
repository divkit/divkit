package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimationDirection
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivAnimator
import com.yandex.div2.DivCount
import com.yandex.div2.DivNumberAnimator

fun numberAnimator(
    cancelActions: List<DivAction>? = null,
    direction: DivAnimationDirection = DivAnimationDirection.NORMAL,
    duration: Long,
    endActions: List<DivAction>? = null,
    endValue: Double,
    id: String,
    interpolator: DivAnimationInterpolator = DivAnimationInterpolator.LINEAR,
    repeatCount: DivCount = fixedCount(1),
    startDelay: Long = 0L,
    startValue: Double? = null,
    variableName: String,
): DivAnimator = numberAnimator(
    cancelActions = cancelActions,
    direction = direction,
    duration = duration,
    endActions = endActions,
    endValue = constant(endValue),
    id = id,
    interpolator = interpolator,
    repeatCount = repeatCount,
    startDelay = startDelay,
    startValue = startValue,
    variableName = variableName,
)

fun numberAnimator(
    cancelActions: List<DivAction>? = null,
    direction: DivAnimationDirection = DivAnimationDirection.NORMAL,
    duration: Long,
    endActions: List<DivAction>? = null,
    endValue: Expression<Double>,
    id: String,
    interpolator: DivAnimationInterpolator = DivAnimationInterpolator.LINEAR,
    repeatCount: DivCount = fixedCount(1),
    startDelay: Long = 0L,
    startValue: Double? = null,
    variableName: String,
): DivAnimator = DivAnimator.Number(
    DivNumberAnimator(
        cancelActions = cancelActions,
        direction = constant(direction),
        duration = constant(duration),
        endActions = endActions,
        endValue = endValue,
        id = id,
        interpolator = constant(interpolator),
        repeatCount = repeatCount,
        startDelay = constant(startDelay),
        startValue = startValue?.let { constant(it) },
        variableName = variableName,
    )
)
