// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivSeparator internal constructor(
    @JsonIgnore override val accessibility: Property<DivAccessibility>?,
    @JsonIgnore val action: Property<DivAction>?,
    @JsonIgnore val actionAnimation: Property<DivAnimation>?,
    @JsonIgnore val actions: Property<List<DivAction>>?,
    @JsonIgnore override val alignmentHorizontal: Property<DivAlignmentHorizontal>?,
    @JsonIgnore override val alignmentVertical: Property<DivAlignmentVertical>?,
    @JsonIgnore override val alpha: Property<Double>?,
    @JsonIgnore override val background: Property<List<DivBackground>>?,
    @JsonIgnore override val border: Property<DivBorder>?,
    @JsonIgnore override val columnSpan: Property<Int>?,
    @JsonIgnore val delimiterStyle: Property<DelimiterStyle>?,
    @JsonIgnore val doubletapActions: Property<List<DivAction>>?,
    @JsonIgnore override val extensions: Property<List<DivExtension>>?,
    @JsonIgnore override val focus: Property<DivFocus>?,
    @JsonIgnore override val height: Property<DivSize>?,
    @JsonIgnore override val id: Property<String>?,
    @JsonIgnore val longtapActions: Property<List<DivAction>>?,
    @JsonIgnore override val margins: Property<DivEdgeInsets>?,
    @JsonIgnore override val paddings: Property<DivEdgeInsets>?,
    @JsonIgnore override val rowSpan: Property<Int>?,
    @JsonIgnore override val selectedActions: Property<List<DivAction>>?,
    @JsonIgnore override val tooltips: Property<List<DivTooltip>>?,
    @JsonIgnore override val transitionChange: Property<DivChangeTransition>?,
    @JsonIgnore override val transitionIn: Property<DivAppearanceTransition>?,
    @JsonIgnore override val transitionOut: Property<DivAppearanceTransition>?,
    @JsonIgnore override val transitionTriggers: Property<List<DivTransitionTrigger>>?,
    @JsonIgnore override val visibility: Property<DivVisibility>?,
    @JsonIgnore override val visibilityAction: Property<DivVisibilityAction>?,
    @JsonIgnore override val visibilityActions: Property<List<DivVisibilityAction>>?,
    @JsonIgnore override val width: Property<DivSize>?,
) : Div(), DivBase {

    @JsonProperty("type") override val type = "separator"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "accessibility" to accessibility,
            "action" to action,
            "action_animation" to actionAnimation,
            "actions" to actions,
            "alignment_horizontal" to alignmentHorizontal,
            "alignment_vertical" to alignmentVertical,
            "alpha" to alpha,
            "background" to background,
            "border" to border,
            "column_span" to columnSpan,
            "delimiter_style" to delimiterStyle,
            "doubletap_actions" to doubletapActions,
            "extensions" to extensions,
            "focus" to focus,
            "height" to height,
            "id" to id,
            "longtap_actions" to longtapActions,
            "margins" to margins,
            "paddings" to paddings,
            "row_span" to rowSpan,
            "selected_actions" to selectedActions,
            "tooltips" to tooltips,
            "transition_change" to transitionChange,
            "transition_in" to transitionIn,
            "transition_out" to transitionOut,
            "transition_triggers" to transitionTriggers,
            "visibility" to visibility,
            "visibility_action" to visibilityAction,
            "visibility_actions" to visibilityActions,
            "width" to width,
        )
    }

    class DelimiterStyle internal constructor(
        @JsonIgnore val color: Property<Color>?,
        @JsonIgnore val orientation: Property<Orientation>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "color" to color,
                "orientation" to orientation,
            )
        }

        enum class Orientation(@JsonValue val value: String) {
            VERTICAL("vertical"),
            HORIZONTAL("horizontal"),
        }
    }
}

fun <T> TemplateContext<T>.divSeparator(): LiteralProperty<DivSeparator> {
    return value(DivSeparator(
        accessibility = null,
        action = null,
        actionAnimation = null,
        actions = null,
        alignmentHorizontal = null,
        alignmentVertical = null,
        alpha = null,
        background = null,
        border = null,
        columnSpan = null,
        delimiterStyle = null,
        doubletapActions = null,
        extensions = null,
        focus = null,
        height = null,
        id = null,
        longtapActions = null,
        margins = null,
        paddings = null,
        rowSpan = null,
        selectedActions = null,
        tooltips = null,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        visibility = null,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    ))
}

fun <T> TemplateContext<T>.divSeparator(
    accessibility: Property<DivAccessibility>? = null,
    action: Property<DivAction>? = null,
    actionAnimation: Property<DivAnimation>? = null,
    actions: Property<List<DivAction>>? = null,
    alignmentHorizontal: Property<DivAlignmentHorizontal>? = null,
    alignmentVertical: Property<DivAlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    background: Property<List<DivBackground>>? = null,
    border: Property<DivBorder>? = null,
    columnSpan: Property<Int>? = null,
    delimiterStyle: Property<DivSeparator.DelimiterStyle>? = null,
    doubletapActions: Property<List<DivAction>>? = null,
    extensions: Property<List<DivExtension>>? = null,
    focus: Property<DivFocus>? = null,
    height: Property<DivSize>? = null,
    id: Property<String>? = null,
    longtapActions: Property<List<DivAction>>? = null,
    margins: Property<DivEdgeInsets>? = null,
    paddings: Property<DivEdgeInsets>? = null,
    rowSpan: Property<Int>? = null,
    selectedActions: Property<List<DivAction>>? = null,
    tooltips: Property<List<DivTooltip>>? = null,
    transitionChange: Property<DivChangeTransition>? = null,
    transitionIn: Property<DivAppearanceTransition>? = null,
    transitionOut: Property<DivAppearanceTransition>? = null,
    transitionTriggers: Property<List<DivTransitionTrigger>>? = null,
    visibility: Property<DivVisibility>? = null,
    visibilityAction: Property<DivVisibilityAction>? = null,
    visibilityActions: Property<List<DivVisibilityAction>>? = null,
    width: Property<DivSize>? = null,
): LiteralProperty<DivSeparator> {
    return value(DivSeparator(
        accessibility = accessibility,
        action = action,
        actionAnimation = actionAnimation,
        actions = actions,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = background,
        border = border,
        columnSpan = columnSpan,
        delimiterStyle = delimiterStyle,
        doubletapActions = doubletapActions,
        extensions = extensions,
        focus = focus,
        height = height,
        id = id,
        longtapActions = longtapActions,
        margins = margins,
        paddings = paddings,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        tooltips = tooltips,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        visibility = visibility,
        visibilityAction = visibilityAction,
        visibilityActions = visibilityActions,
        width = width,
    ))
}

fun <T> TemplateContext<T>.divSeparator(
    accessibility: DivAccessibility? = null,
    action: DivAction? = null,
    actionAnimation: DivAnimation? = null,
    actions: List<DivAction>? = null,
    alignmentHorizontal: DivAlignmentHorizontal? = null,
    alignmentVertical: DivAlignmentVertical? = null,
    alpha: Double? = null,
    background: List<DivBackground>? = null,
    border: DivBorder? = null,
    columnSpan: Int? = null,
    delimiterStyle: DivSeparator.DelimiterStyle? = null,
    doubletapActions: List<DivAction>? = null,
    extensions: List<DivExtension>? = null,
    focus: DivFocus? = null,
    height: DivSize? = null,
    id: String? = null,
    longtapActions: List<DivAction>? = null,
    margins: DivEdgeInsets? = null,
    paddings: DivEdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<DivAction>? = null,
    tooltips: List<DivTooltip>? = null,
    transitionChange: DivChangeTransition? = null,
    transitionIn: DivAppearanceTransition? = null,
    transitionOut: DivAppearanceTransition? = null,
    transitionTriggers: List<DivTransitionTrigger>? = null,
    visibility: DivVisibility? = null,
    visibilityAction: DivVisibilityAction? = null,
    visibilityActions: List<DivVisibilityAction>? = null,
    width: DivSize? = null,
): LiteralProperty<DivSeparator> {
    return value(DivSeparator(
        accessibility = optionalValue(accessibility),
        action = optionalValue(action),
        actionAnimation = optionalValue(actionAnimation),
        actions = optionalValue(actions),
        alignmentHorizontal = optionalValue(alignmentHorizontal),
        alignmentVertical = optionalValue(alignmentVertical),
        alpha = optionalValue(alpha),
        background = optionalValue(background),
        border = optionalValue(border),
        columnSpan = optionalValue(columnSpan),
        delimiterStyle = optionalValue(delimiterStyle),
        doubletapActions = optionalValue(doubletapActions),
        extensions = optionalValue(extensions),
        focus = optionalValue(focus),
        height = optionalValue(height),
        id = optionalValue(id),
        longtapActions = optionalValue(longtapActions),
        margins = optionalValue(margins),
        paddings = optionalValue(paddings),
        rowSpan = optionalValue(rowSpan),
        selectedActions = optionalValue(selectedActions),
        tooltips = optionalValue(tooltips),
        transitionChange = optionalValue(transitionChange),
        transitionIn = optionalValue(transitionIn),
        transitionOut = optionalValue(transitionOut),
        transitionTriggers = optionalValue(transitionTriggers),
        visibility = optionalValue(visibility),
        visibilityAction = optionalValue(visibilityAction),
        visibilityActions = optionalValue(visibilityActions),
        width = optionalValue(width),
    ))
}

fun <T> TemplateContext<T>.delimiterStyle(): LiteralProperty<DivSeparator.DelimiterStyle> {
    return value(DivSeparator.DelimiterStyle(
        color = null,
        orientation = null,
    ))
}

fun <T> TemplateContext<T>.delimiterStyle(
    color: Property<Color>? = null,
    orientation: Property<DivSeparator.DelimiterStyle.Orientation>? = null,
): LiteralProperty<DivSeparator.DelimiterStyle> {
    return value(DivSeparator.DelimiterStyle(
        color = color,
        orientation = orientation,
    ))
}

fun <T> TemplateContext<T>.delimiterStyle(
    color: Color? = null,
    orientation: DivSeparator.DelimiterStyle.Orientation? = null,
): LiteralProperty<DivSeparator.DelimiterStyle> {
    return value(DivSeparator.DelimiterStyle(
        color = optionalValue(color),
        orientation = optionalValue(orientation),
    ))
}

fun CardContext.divSeparator(): DivSeparator {
    return DivSeparator(
        accessibility = null,
        action = null,
        actionAnimation = null,
        actions = null,
        alignmentHorizontal = null,
        alignmentVertical = null,
        alpha = null,
        background = null,
        border = null,
        columnSpan = null,
        delimiterStyle = null,
        doubletapActions = null,
        extensions = null,
        focus = null,
        height = null,
        id = null,
        longtapActions = null,
        margins = null,
        paddings = null,
        rowSpan = null,
        selectedActions = null,
        tooltips = null,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        visibility = null,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    )
}

fun CardContext.divSeparator(
    accessibility: ValueProperty<DivAccessibility>? = null,
    action: ValueProperty<DivAction>? = null,
    actionAnimation: ValueProperty<DivAnimation>? = null,
    actions: ValueProperty<List<DivAction>>? = null,
    alignmentHorizontal: ValueProperty<DivAlignmentHorizontal>? = null,
    alignmentVertical: ValueProperty<DivAlignmentVertical>? = null,
    alpha: ValueProperty<Double>? = null,
    background: ValueProperty<List<DivBackground>>? = null,
    border: ValueProperty<DivBorder>? = null,
    columnSpan: ValueProperty<Int>? = null,
    delimiterStyle: ValueProperty<DivSeparator.DelimiterStyle>? = null,
    doubletapActions: ValueProperty<List<DivAction>>? = null,
    extensions: ValueProperty<List<DivExtension>>? = null,
    focus: ValueProperty<DivFocus>? = null,
    height: ValueProperty<DivSize>? = null,
    id: ValueProperty<String>? = null,
    longtapActions: ValueProperty<List<DivAction>>? = null,
    margins: ValueProperty<DivEdgeInsets>? = null,
    paddings: ValueProperty<DivEdgeInsets>? = null,
    rowSpan: ValueProperty<Int>? = null,
    selectedActions: ValueProperty<List<DivAction>>? = null,
    tooltips: ValueProperty<List<DivTooltip>>? = null,
    transitionChange: ValueProperty<DivChangeTransition>? = null,
    transitionIn: ValueProperty<DivAppearanceTransition>? = null,
    transitionOut: ValueProperty<DivAppearanceTransition>? = null,
    transitionTriggers: ValueProperty<List<DivTransitionTrigger>>? = null,
    visibility: ValueProperty<DivVisibility>? = null,
    visibilityAction: ValueProperty<DivVisibilityAction>? = null,
    visibilityActions: ValueProperty<List<DivVisibilityAction>>? = null,
    width: ValueProperty<DivSize>? = null,
): DivSeparator {
    return DivSeparator(
        accessibility = accessibility,
        action = action,
        actionAnimation = actionAnimation,
        actions = actions,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = background,
        border = border,
        columnSpan = columnSpan,
        delimiterStyle = delimiterStyle,
        doubletapActions = doubletapActions,
        extensions = extensions,
        focus = focus,
        height = height,
        id = id,
        longtapActions = longtapActions,
        margins = margins,
        paddings = paddings,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        tooltips = tooltips,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        visibility = visibility,
        visibilityAction = visibilityAction,
        visibilityActions = visibilityActions,
        width = width,
    )
}

fun CardContext.divSeparator(
    accessibility: DivAccessibility? = null,
    action: DivAction? = null,
    actionAnimation: DivAnimation? = null,
    actions: List<DivAction>? = null,
    alignmentHorizontal: DivAlignmentHorizontal? = null,
    alignmentVertical: DivAlignmentVertical? = null,
    alpha: Double? = null,
    background: List<DivBackground>? = null,
    border: DivBorder? = null,
    columnSpan: Int? = null,
    delimiterStyle: DivSeparator.DelimiterStyle? = null,
    doubletapActions: List<DivAction>? = null,
    extensions: List<DivExtension>? = null,
    focus: DivFocus? = null,
    height: DivSize? = null,
    id: String? = null,
    longtapActions: List<DivAction>? = null,
    margins: DivEdgeInsets? = null,
    paddings: DivEdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<DivAction>? = null,
    tooltips: List<DivTooltip>? = null,
    transitionChange: DivChangeTransition? = null,
    transitionIn: DivAppearanceTransition? = null,
    transitionOut: DivAppearanceTransition? = null,
    transitionTriggers: List<DivTransitionTrigger>? = null,
    visibility: DivVisibility? = null,
    visibilityAction: DivVisibilityAction? = null,
    visibilityActions: List<DivVisibilityAction>? = null,
    width: DivSize? = null,
): DivSeparator {
    return DivSeparator(
        accessibility = optionalValue(accessibility),
        action = optionalValue(action),
        actionAnimation = optionalValue(actionAnimation),
        actions = optionalValue(actions),
        alignmentHorizontal = optionalValue(alignmentHorizontal),
        alignmentVertical = optionalValue(alignmentVertical),
        alpha = optionalValue(alpha),
        background = optionalValue(background),
        border = optionalValue(border),
        columnSpan = optionalValue(columnSpan),
        delimiterStyle = optionalValue(delimiterStyle),
        doubletapActions = optionalValue(doubletapActions),
        extensions = optionalValue(extensions),
        focus = optionalValue(focus),
        height = optionalValue(height),
        id = optionalValue(id),
        longtapActions = optionalValue(longtapActions),
        margins = optionalValue(margins),
        paddings = optionalValue(paddings),
        rowSpan = optionalValue(rowSpan),
        selectedActions = optionalValue(selectedActions),
        tooltips = optionalValue(tooltips),
        transitionChange = optionalValue(transitionChange),
        transitionIn = optionalValue(transitionIn),
        transitionOut = optionalValue(transitionOut),
        transitionTriggers = optionalValue(transitionTriggers),
        visibility = optionalValue(visibility),
        visibilityAction = optionalValue(visibilityAction),
        visibilityActions = optionalValue(visibilityActions),
        width = optionalValue(width),
    )
}

fun CardContext.delimiterStyle(): DivSeparator.DelimiterStyle {
    return DivSeparator.DelimiterStyle(
        color = null,
        orientation = null,
    )
}

fun CardContext.delimiterStyle(
    color: ValueProperty<Color>? = null,
    orientation: ValueProperty<DivSeparator.DelimiterStyle.Orientation>? = null,
): DivSeparator.DelimiterStyle {
    return DivSeparator.DelimiterStyle(
        color = color,
        orientation = orientation,
    )
}

fun CardContext.delimiterStyle(
    color: Color? = null,
    orientation: DivSeparator.DelimiterStyle.Orientation? = null,
): DivSeparator.DelimiterStyle {
    return DivSeparator.DelimiterStyle(
        color = optionalValue(color),
        orientation = optionalValue(orientation),
    )
}
