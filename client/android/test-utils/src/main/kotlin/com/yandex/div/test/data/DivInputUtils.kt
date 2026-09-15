package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputFilter

fun input(
    filters: List<DivInputFilter>? = null,
    maxLength: Expression<Long>? = null,
    textVariable: String,
): Div = Div.Input(
    DivInput(
        filters = filters,
        maxLength = maxLength,
        textVariable = textVariable,
    )
)
