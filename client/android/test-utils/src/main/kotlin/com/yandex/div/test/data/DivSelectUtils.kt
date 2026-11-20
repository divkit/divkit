package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivSelect
import com.yandex.div2.DivVisibility

fun select(
    accessibility: DivAccessibility? = null,
    hintText: String? = null,
    id: String? = null,
    options: List<DivSelect.Option>,
    transitionOut: DivAppearanceTransition? = null,
    valueVariable: String,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
): Div = Div.Select(
    DivSelect(
        accessibility = accessibility,
        hintText = hintText?.let { constant(it) },
        id = id,
        options = options,
        transitionOut = transitionOut,
        valueVariable = valueVariable,
        visibility = visibility,
    )
)

fun selectOption(value: String, text: String? = null): DivSelect.Option = DivSelect.Option(
    value = constant(value),
    text = text?.let { constant(it) },
)
