package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivExtension
import com.yandex.div2.DivSize
import com.yandex.div2.DivState
import com.yandex.div2.DivTransitionSelector

fun state(
    defaultStateId: Expression<String>? = null,
    height: DivSize = wrapContent(),
    extensions: List<DivExtension>? = null,
    id: String? = null,
    stateIdVariable: String? = null,
    states: List<DivState.State>,
    transitionAnimationSelector: DivTransitionSelector = DivTransitionSelector.STATE_CHANGE,
    transitionOut: DivAppearanceTransition? = null,
    width: DivSize = matchParent(),
): Div.State {
    return Div.State(
        DivState(
            defaultStateId = defaultStateId,
            height = height,
            extensions = extensions,
            id = id,
            stateIdVariable = stateIdVariable,
            states = states,
            transitionAnimationSelector = constant(transitionAnimationSelector),
            transitionOut = transitionOut,
            width = width,
        )
    )
}

fun state(
    div: Div? = null,
    stateId: String,
): DivState.State {
    return DivState.State(
        div = div,
        stateId = stateId,
    )
}
