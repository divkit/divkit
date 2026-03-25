package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivBackground
import com.yandex.div2.DivContainer
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivVariable

fun container(
    backgrounds: List<DivBackground>? = null,
    items: List<Div> = emptyList(),
    margins: DivEdgeInsets? = null,
    paddings: DivEdgeInsets? = null,
    variables: List<DivVariable>? = null
): Div {
    return Div.Container(
        value = DivContainer(
            background = backgrounds,
            items = items,
            margins = margins,
            paddings = paddings,
            variables = variables
        )
    )
}
