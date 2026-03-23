package com.yandex.div.test.data

import com.yandex.div2.DivAction
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivTrigger.Mode

fun trigger(
    action: DivAction,
    condition: String,
    mode: Mode = Mode.ON_CONDITION
): DivTrigger {
    return DivTrigger(
        actions = listOf(action),
        condition = booleanExpression(condition),
        mode = constant(mode)
    )
}
