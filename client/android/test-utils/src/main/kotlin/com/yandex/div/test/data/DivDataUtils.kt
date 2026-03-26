package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivFunction
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable

fun data(
    content: Div,
    functions: List<DivFunction>? = null,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null
): DivData {
    return DivData(
        functions = functions,
        logId = "test",
        states = listOf(
            DivData.State(
                stateId = 0,
                div = content
            )
        ),
        variables = variables,
        variableTriggers = triggers,
    )
}
