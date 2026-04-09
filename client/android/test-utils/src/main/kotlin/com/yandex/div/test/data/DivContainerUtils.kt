package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivBackground
import com.yandex.div2.DivContainer
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFunction
import com.yandex.div2.DivSize
import com.yandex.div2.DivVariable

fun container(
    accessibility: DivAccessibility? = null,
    backgrounds: List<DivBackground>? = null,
    functions: List<DivFunction>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    items: List<Div> = emptyList(),
    margins: DivEdgeInsets? = null,
    paddings: DivEdgeInsets? = null,
    variables: List<DivVariable>? = null
): Div {
    return Div.Container(
        value = DivContainer(
            accessibility = accessibility,
            background = backgrounds,
            functions = functions,
            height = height,
            id = id,
            items = items,
            margins = margins,
            paddings = paddings,
            variables = variables
        )
    )
}
