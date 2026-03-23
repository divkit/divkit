package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivVariable

fun container(
    items: List<Div> = emptyList(),
    variables: List<DivVariable>? = null
): Div {
    return Div.Container(
        value = DivContainer(
            items = items,
            variables = variables
        )
    )
}
