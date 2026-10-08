package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivFocus
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputFilter
import com.yandex.div2.DivInputMask
import com.yandex.div2.DivVisibility

fun input(
    autocapitalization: Expression<DivInput.Autocapitalization> = constant(DivInput.Autocapitalization.AUTO),
    enterKeyType: Expression<DivInput.EnterKeyType> = constant(DivInput.EnterKeyType.DEFAULT),
    filters: List<DivInputFilter>? = null,
    focus: DivFocus? = null,
    id: String? = null,
    keyboardType: Expression<DivInput.KeyboardType> = constant(DivInput.KeyboardType.MULTI_LINE_TEXT),
    mask: DivInputMask? = null,
    maxLength: Expression<Long>? = null,
    textVariable: String,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
): Div = Div.Input(
    DivInput(
        autocapitalization = autocapitalization,
        enterKeyType = enterKeyType,
        filters = filters,
        focus = focus,
        id = id,
        keyboardType = keyboardType,
        mask = mask,
        maxLength = maxLength,
        textVariable = textVariable,
        visibility = visibility,
    )
)
