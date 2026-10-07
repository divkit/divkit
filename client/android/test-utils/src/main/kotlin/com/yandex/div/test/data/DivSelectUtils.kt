package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivSelect

fun select(
    accessibility: DivAccessibility? = null,
    hintText: String? = null,
    id: String? = null,
    options: List<DivSelect.Option>,
    valueVariable: String,
): Div = Div.Select(
    DivSelect(
        accessibility = accessibility,
        hintText = hintText?.let { constant(it) },
        id = id,
        options = options,
        valueVariable = valueVariable,
    )
)

fun selectOption(value: String, text: String? = null): DivSelect.Option = DivSelect.Option(
    value = constant(value),
    text = text?.let { constant(it) },
)
