package com.yandex.div.test.data

import androidx.core.net.toUri
import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivBackground
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivExtension
import com.yandex.div2.DivFunction
import com.yandex.div2.DivSize
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTransform
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility
import com.yandex.div2.DivVisibilityAction

fun text(
    accessibility: DivAccessibility? = null,
    action: DivAction? = null,
    backgrounds: List<DivBackground>? = null,
    disappearActions: List<DivDisappearAction>? = null,
    doubleTapActions: List<DivAction>? = null,
    extensions: List<DivExtension>? = null,
    id: String? = null,
    images: List<DivText.Image>? = null,
    longTapActions: List<DivAction>? = null,
    margins: DivEdgeInsets? = null,
    paddings: DivEdgeInsets? = null,
    text: String,
    tooltips: List<DivTooltip>? = null,
    transform: DivTransform? = null,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    visibilityActions: List<DivVisibilityAction>? = null
): Div {
    return text(
        accessibility = accessibility,
        action = action,
        backgrounds = backgrounds,
        disappearActions = disappearActions,
        doubleTapActions = doubleTapActions,
        extensions = extensions,
        id = id,
        images = images,
        longTapActions = longTapActions,
        margins = margins,
        paddings = paddings,
        text = constant(text),
        tooltips = tooltips,
        transform = transform,
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
    backgrounds: List<DivBackground>? = null,
    disappearActions: List<DivDisappearAction>? = null,
    doubleTapActions: List<DivAction>? = null,
    ellipsis: DivText.Ellipsis? = null,
    extensions: List<DivExtension>? = null,
    fontSize: Long = 12,
    functions: List<DivFunction>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    images: List<DivText.Image>? = null,
    longTapActions: List<DivAction>? = null,
    margins: DivEdgeInsets? = null,
    maxLines: Long? = null,
    paddings: DivEdgeInsets? = null,
    ranges: List<DivText.Range>? = null,
    selectable: Boolean = false,
    text: Expression<String>,
    textColor: Color? = null,
    tooltips: List<DivTooltip>? = null,
    transform: DivTransform? = null,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    visibilityActions: List<DivVisibilityAction>? = null,
    width: DivSize = matchParent()
): Div {
    return Div.Text(
        value = DivText(
            accessibility = accessibility,
            action = action,
            actions = actions,
            background = backgrounds,
            disappearActions = disappearActions,
            doubletapActions = doubleTapActions,
            ellipsis = ellipsis,
            extensions = extensions,
            functions = functions,
            fontSize = constant(fontSize),
            height = height,
            id = id,
            images = images,
            longtapActions = longTapActions,
            margins = margins,
            maxLines = maxLines?.let { constant(it) },
            paddings = paddings,
            ranges = ranges,
            selectable = constant(selectable),
            text = text,
            textColor = textColor?.let { constant(it.value) } ?: constant(0xFF000000.toInt()),
            tooltips = tooltips,
            transform = transform,
            variables = variables,
            variableTriggers = triggers,
            visibility = visibility,
            visibilityActions = visibilityActions,
            width = width
        )
    )
}

fun textImage(
    url: String,
    start: Int = 0,
    preloadRequired: Boolean = false,
) = DivText.Image(
    preloadRequired = constant(preloadRequired),
    start = constant(start.toLong()),
    url = constant(url.toUri())
)

fun textRange(
    start: Int,
    end: Int,
    actions: List<DivAction>? = null
) = DivText.Range(
    actions = actions,
    end = constant(end.toLong()),
    start = constant(start.toLong())
)
