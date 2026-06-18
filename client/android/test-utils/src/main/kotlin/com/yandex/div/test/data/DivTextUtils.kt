package com.yandex.div.test.data

import android.net.Uri
import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivExtension
import com.yandex.div2.DivFunction
import com.yandex.div2.DivSize
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility
import com.yandex.div2.DivVisibilityAction

fun text(
    accessibility: DivAccessibility? = null,
    action: DivAction? = null,
    disappearActions: List<DivDisappearAction>? = null,
    doubleTapActions: List<DivAction>? = null,
    id: String? = null,
    images: List<DivText.Image>? = null,
    longTapActions: List<DivAction>? = null,
    text: String,
    tooltips: List<DivTooltip>? = null,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    visibilityActions: List<DivVisibilityAction>? = null
): Div {
    return text(
        accessibility = accessibility,
        action = action,
        disappearActions = disappearActions,
        doubleTapActions = doubleTapActions,
        id = id,
        images = images,
        longTapActions = longTapActions,
        text = constant(text),
        tooltips = tooltips,
        triggers = triggers,
        variables = variables,
        visibility = visibility,
        visibilityActions = visibilityActions
    )
}

fun text(
    accessibility: DivAccessibility? = null,
    action: DivAction? = null,
    actions: List<DivAction>? = null,
    disappearActions: List<DivDisappearAction>? = null,
    doubleTapActions: List<DivAction>? = null,
    extensions: List<DivExtension>? = null,
    fontSize: Long = 12,
    functions: List<DivFunction>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    images: List<DivText.Image>? = null,
    longTapActions: List<DivAction>? = null,
    text: Expression<String>,
    textColor: Color? = null,
    tooltips: List<DivTooltip>? = null,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    visibilityActions: List<DivVisibilityAction>? = null
): Div {
    return Div.Text(
        value = DivText(
            accessibility = accessibility,
            action = action,
            actions = actions,
            disappearActions = disappearActions,
            doubletapActions = doubleTapActions,
            extensions = extensions,
            functions = functions,
            fontSize = constant(fontSize),
            height = height,
            id = id,
            images = images,
            longtapActions = longTapActions,
            text = text,
            textColor = textColor?.let { constant(it.value) } ?: constant(0xFF000000.toInt()),
            tooltips = tooltips,
            variables = variables,
            variableTriggers = triggers,
            visibility = visibility,
            visibilityActions = visibilityActions
        )
    )
}

fun textImage(
    url: Expression<Uri>,
    start: Expression<Long> = constant(0L),
    preloadRequired: Expression<Boolean> = constant(false),
) = DivText.Image(
    url = url,
    start = start,
    preloadRequired = preloadRequired,
)
