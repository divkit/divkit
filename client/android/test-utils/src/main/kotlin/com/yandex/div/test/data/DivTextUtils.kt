package com.yandex.div.test.data

import androidx.core.net.toUri
import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivAnimator
import com.yandex.div2.DivBackground
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivExtension
import com.yandex.div2.DivFocus
import com.yandex.div2.DivFunction
import com.yandex.div2.DivLayoutProvider
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivText
import com.yandex.div2.DivTextGradient
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder
import com.yandex.div2.DivTextRangeMask
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTransform
import com.yandex.div2.DivTransitionTrigger
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility
import com.yandex.div2.DivVisibilityAction

fun text(
    accessibility: DivAccessibility? = null,
    action: DivAction? = null,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = null,
    alignmentVertical: Expression<DivAlignmentVertical>? = null,
    animators: List<DivAnimator>? = null,
    backgrounds: List<DivBackground>? = null,
    captureFocusOnAction: Expression<Boolean> = constant(true),
    disappearActions: List<DivDisappearAction>? = null,
    doubleTapActions: List<DivAction>? = null,
    extensions: List<DivExtension>? = null,
    focus: DivFocus? = null,
    hoverStartActions: List<DivAction>? = null,
    hoverEndActions: List<DivAction>? = null,
    id: String? = null,
    images: List<DivText.Image>? = null,
    longTapActions: List<DivAction>? = null,
    margins: DivEdgeInsets? = null,
    paddings: DivEdgeInsets? = null,
    pressStartActions: List<DivAction>? = null,
    pressEndActions: List<DivAction>? = null,
    selectedActions: List<DivAction>? = null,
    text: String,
    textGradient: DivTextGradient? = null,
    tooltips: List<DivTooltip>? = null,
    transform: DivTransform? = null,
    transitionIn: DivAppearanceTransition? = null,
    transitionOut: DivAppearanceTransition? = null,
    transitionTriggers: List<DivTransitionTrigger>? = null,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    visibilityActions: List<DivVisibilityAction>? = null
): Div {
    return text(
        accessibility = accessibility,
        action = action,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        animators = animators,
        backgrounds = backgrounds,
        captureFocusOnAction = captureFocusOnAction,
        disappearActions = disappearActions,
        doubleTapActions = doubleTapActions,
        extensions = extensions,
        focus = focus,
        hoverStartActions = hoverStartActions,
        hoverEndActions = hoverEndActions,
        id = id,
        images = images,
        longTapActions = longTapActions,
        margins = margins,
        paddings = paddings,
        pressStartActions = pressStartActions,
        pressEndActions = pressEndActions,
        selectedActions = selectedActions,
        text = constant(text),
        textGradient = textGradient,
        tooltips = tooltips,
        transform = transform,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
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
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = null,
    alignmentVertical: Expression<DivAlignmentVertical>? = null,
    animators: List<DivAnimator>? = null,
    backgrounds: List<DivBackground>? = null,
    captureFocusOnAction: Expression<Boolean> = constant(true),
    disappearActions: List<DivDisappearAction>? = null,
    doubleTapActions: List<DivAction>? = null,
    ellipsis: DivText.Ellipsis? = null,
    extensions: List<DivExtension>? = null,
    focus: DivFocus? = null,
    fontSize: Long = 12,
    functions: List<DivFunction>? = null,
    height: DivSize = wrapContent(),
    hoverStartActions: List<DivAction>? = null,
    hoverEndActions: List<DivAction>? = null,
    id: String? = null,
    images: List<DivText.Image>? = null,
    layoutProvider: DivLayoutProvider? = null,
    lineHeight: Long? = null,
    longTapActions: List<DivAction>? = null,
    margins: DivEdgeInsets? = null,
    maxLines: Long? = null,
    paddings: DivEdgeInsets? = null,
    ranges: List<DivText.Range>? = null,
    selectable: Boolean = false,
    pressStartActions: List<DivAction>? = null,
    pressEndActions: List<DivAction>? = null,
    selectedActions: List<DivAction>? = null,
    text: Expression<String>,
    textColor: Color? = null,
    textGradient: DivTextGradient? = null,
    truncatePolicy: DivText.TruncatePolicy = DivText.TruncatePolicy.GRAPHEME,
    tooltips: List<DivTooltip>? = null,
    transform: DivTransform? = null,
    transitionIn: DivAppearanceTransition? = null,
    transitionOut: DivAppearanceTransition? = null,
    transitionTriggers: List<DivTransitionTrigger>? = null,
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    visibilityActions: List<DivVisibilityAction>? = null,
    width: DivSize = matchParent(),
    fontSizeUnit: DivSizeUnit = DivSizeUnit.SP,
): Div {
    return Div.Text(
        value = DivText(
            accessibility = accessibility,
            action = action,
            actions = actions,
            alignmentHorizontal = alignmentHorizontal,
            alignmentVertical = alignmentVertical,
            animators = animators,
            background = backgrounds,
            captureFocusOnAction = captureFocusOnAction,
            disappearActions = disappearActions,
            doubletapActions = doubleTapActions,
            ellipsis = ellipsis,
            extensions = extensions,
            focus = focus,
            functions = functions,
            fontSize = constant(fontSize),
            fontSizeUnit = constant(fontSizeUnit),
            height = height,
            hoverStartActions = hoverStartActions,
            hoverEndActions = hoverEndActions,
            id = id,
            images = images,
            layoutProvider = layoutProvider,
            lineHeight = lineHeight?.let { constant(it) },
            longtapActions = longTapActions,
            margins = margins,
            maxLines = maxLines?.let { constant(it) },
            paddings = paddings,
            ranges = ranges,
            selectable = constant(selectable),
            pressStartActions = pressStartActions,
            pressEndActions = pressEndActions,
            selectedActions = selectedActions,
            text = text,
            textGradient = textGradient,
            truncatePolicy = constant(truncatePolicy),
            textColor = textColor?.let { constant(it.value) } ?: constant(0xFF000000.toInt()),
            tooltips = tooltips,
            transform = transform,
            transitionIn = transitionIn,
            transitionOut = transitionOut,
            transitionTriggers = transitionTriggers,
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
    start: Long = 0,
    end: Long? = null,
    actions: List<DivAction>? = null,
    background: DivTextRangeBackground? = null,
    border: DivTextRangeBorder? = null,
    mask: DivTextRangeMask? = null,
    fontSizeUnit: DivSizeUnit = DivSizeUnit.SP,
    lineHeight: Long? = null,
) = DivText.Range(
    actions = actions,
    background = background,
    border = border,
    end = end?.let { constant(it) },
    fontSizeUnit = constant(fontSizeUnit),
    lineHeight = lineHeight?.let { constant(it) },
    mask = mask,
    start = constant(start)
)
