package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivTimer

fun timer(
    duration: Expression<Long> = constant(0),
    id: String,
    tickInterval: Expression<Long>? = null,
    valueVariable: String? = null
): DivTimer {
    return DivTimer(
        duration = duration,
        id = id,
        tickInterval = tickInterval,
        valueVariable = valueVariable
    )
}

fun timer(
    duration: Int = 0,
    endActions: List<DivAction>? = null,
    id: String,
    tickActions: List<DivAction>? = null,
    tickInterval: Int? = null,
    valueVariable: String? = null
): DivTimer {
    return DivTimer(
        duration = constant(duration.toLong()),
        endActions = endActions,
        id = id,
        tickActions = tickActions,
        tickInterval = tickInterval?.let { constant(it.toLong()) },
        valueVariable = valueVariable
    )
}
