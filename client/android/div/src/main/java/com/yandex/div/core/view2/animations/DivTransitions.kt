package com.yandex.div.core.view2.animations

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivState
import com.yandex.div2.DivTransitionSelector
import com.yandex.div2.DivTransitionTrigger

internal fun DivTransitionSelector.allowsTransitionsOnDataChange(): Boolean {
    return when (this) {
        DivTransitionSelector.DATA_CHANGE -> true
        DivTransitionSelector.ANY_CHANGE -> true
        else -> false
    }
}

internal fun DivTransitionSelector.allowsTransitionsOnStateChange(): Boolean {
    return when (this) {
        DivTransitionSelector.STATE_CHANGE -> true
        DivTransitionSelector.ANY_CHANGE -> true
        else -> false
    }
}

internal fun List<DivTransitionTrigger>.allowsTransitionsOnDataChange() =
    DivTransitionTrigger.DATA_CHANGE in this

internal fun List<DivTransitionTrigger>.allowsTransitionsOnStateChange() =
    DivTransitionTrigger.STATE_CHANGE in this

internal fun List<DivTransitionTrigger>.allowsTransitionsOnVisibilityChange() =
    DivTransitionTrigger.VISIBILITY_CHANGE in this

internal fun DivData.allowsTransitionsOnDataChange(resolver: ExpressionResolver): Boolean {
    return transitionAnimationSelector.evaluate(resolver).allowsTransitionsOnDataChange()
}

internal fun DivState.allowsTransitionsOnDataChange(resolver: ExpressionResolver): Boolean {
    return transitionAnimationSelector.evaluate(resolver).allowsTransitionsOnDataChange()
}

internal fun DivState.allowsTransitionsOnStateChange(resolver: ExpressionResolver): Boolean {
    return transitionAnimationSelector.evaluate(resolver).allowsTransitionsOnStateChange()
}
