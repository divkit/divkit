package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivFocus
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputFilter
import com.yandex.div2.DivVisibility

fun input(
    filters: List<DivInputFilter>? = null,
    focus: DivFocus? = null,
    id: String? = null,
    maxLength: Expression<Long>? = null,
    textVariable: String,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
): Div = Div.Input(
    DivInput(
        filters = filters,
        focus = focus,
        id = id,
        maxLength = maxLength,
        textVariable = textVariable,
        visibility = visibility,
    )
)
