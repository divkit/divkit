package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivText
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibilityAction

fun text(
    action: DivAction? = null,
    disappearActions: List<DivDisappearAction>? = null,
    id: String? = null,
    text: Expression<String>,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibilityActions: List<DivVisibilityAction>? = null
): Div {
    return Div.Text(
        value = DivText(
            action = action,
            disappearActions = disappearActions,
            id = id,
            text = text,
            variables = variables,
            variableTriggers = triggers,
            visibilityActions = visibilityActions
        )
    )
}
