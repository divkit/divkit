package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivSize
import com.yandex.div2.DivState

fun state(
    defaultStateId: Expression<String>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    stateIdVariable: String? = null,
    states: List<DivState.State>,
    width: DivSize = matchParent(),
): Div.State {
    return Div.State(
        DivState(
            defaultStateId = defaultStateId,
            height = height,
            id = id,
            stateIdVariable = stateIdVariable,
            states = states,
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
