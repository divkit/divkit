package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivState

fun state(
    defaultStateId: Expression<String>? = null,
    id: String? = null,
    stateIdVariable: String? = null,
    states: List<DivState.State>,
): Div.State {
    return Div.State(
        DivState(
            defaultStateId = defaultStateId,
            id = id,
            stateIdVariable = stateIdVariable,
            states = states,
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
