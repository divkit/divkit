package com.yandex.div.test.data

import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivFunction
import com.yandex.div2.DivSize
import com.yandex.div2.DivText
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility
import com.yandex.div2.DivVisibilityAction

fun text(
    accessibility: DivAccessibility? = null,
    disappearActions: List<DivDisappearAction>? = null,
    id: String? = null,
    text: String,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    visibilityActions: List<DivVisibilityAction>? = null
): Div {
    return text(
        accessibility = accessibility,
        disappearActions = disappearActions,
        id = id,
        text = constant(text),
        triggers = triggers,
        variables = variables,
        visibility = visibility,
        visibilityActions = visibilityActions
    )
}

fun text(
    accessibility: DivAccessibility? = null,
    action: DivAction? = null,
    disappearActions: List<DivDisappearAction>? = null,
    fontSize: Long = 12,
    functions: List<DivFunction>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    text: Expression<String>,
    textColor: Color? = null,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    visibilityActions: List<DivVisibilityAction>? = null
): Div {
    return Div.Text(
        value = DivText(
            accessibility = accessibility,
            action = action,
            disappearActions = disappearActions,
            functions = functions,
            fontSize = constant(fontSize),
            height = height,
            id = id,
            text = text,
            textColor = textColor?.let { constant(it.value) } ?: constant(0xFF000000.toInt()),
            variables = variables,
            variableTriggers = triggers,
            visibility = visibility,
            visibilityActions = visibilityActions
        )
    )
}
