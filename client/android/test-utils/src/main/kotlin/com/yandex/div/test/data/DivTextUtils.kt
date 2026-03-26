package com.yandex.div.test.data

import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivFunction
import com.yandex.div2.DivText
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibilityAction

fun text(
    disappearActions: List<DivDisappearAction>? = null,
    id: String? = null,
    text: String,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibilityActions: List<DivVisibilityAction>? = null
): Div {
    return text(
        disappearActions = disappearActions,
        id = id,
        text = constant(text),
        triggers = triggers,
        variables = variables,
        visibilityActions = visibilityActions
    )
}

fun text(
    action: DivAction? = null,
    disappearActions: List<DivDisappearAction>? = null,
    fontSize: Long = 12,
    functions: List<DivFunction>? = null,
    id: String? = null,
    text: Expression<String>,
    textColor: Color? = null,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibilityActions: List<DivVisibilityAction>? = null
): Div {
    return Div.Text(
        value = DivText(
            action = action,
            disappearActions = disappearActions,
            functions = functions,
            fontSize = constant(fontSize),
            id = id,
            text = text,
            textColor = textColor?.let { constant(it.value) } ?: constant(0xFF000000.toInt()),
            variables = variables,
            variableTriggers = triggers,
            visibilityActions = visibilityActions
        )
    )
}
