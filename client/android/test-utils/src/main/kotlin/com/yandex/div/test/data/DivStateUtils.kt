package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivState

fun state(
    states: List<DivState.State>,
    defaultStateId: Expression<String>? = null,
    stateIdVariable: String? = null,
    id: String? = null,
) = DivState(
    states = states,
    defaultStateId = defaultStateId,
    stateIdVariable = stateIdVariable,
    id = id,
)
