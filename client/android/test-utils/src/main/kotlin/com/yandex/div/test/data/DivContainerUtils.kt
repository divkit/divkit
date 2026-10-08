package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivBackground
import com.yandex.div2.DivBorder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivExtension
import com.yandex.div2.DivFocus
import com.yandex.div2.DivFunction
import com.yandex.div2.DivSize
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility

fun container(
    accessibility: DivAccessibility? = null,
    action: DivAction? = null,
    backgrounds: List<DivBackground>? = null,
    border: DivBorder? = null,
    clipToBounds: Expression<Boolean> = constant(true),
    extensions: List<DivExtension>? = null,
    focus: DivFocus? = null,
    functions: List<DivFunction>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    items: List<Div> = emptyList(),
    margins: DivEdgeInsets? = null,
    paddings: DivEdgeInsets? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    width: DivSize = matchParent()
): Div {
    return Div.Container(
        value = DivContainer(
            accessibility = accessibility,
            action = action,
            background = backgrounds,
            border = border,
            clipToBounds = clipToBounds,
            extensions = extensions,
            focus = focus,
            functions = functions,
            height = height,
            id = id,
            items = items,
            margins = margins,
            paddings = paddings,
            variables = variables,
            visibility = visibility,
            width = width
        )
    )
}
