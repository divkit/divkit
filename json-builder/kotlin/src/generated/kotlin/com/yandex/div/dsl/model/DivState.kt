// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivState internal constructor(
    @JsonIgnore override val accessibility: Property<DivAccessibility>?,
    @JsonIgnore override val alignmentHorizontal: Property<DivAlignmentHorizontal>?,
    @JsonIgnore override val alignmentVertical: Property<DivAlignmentVertical>?,
    @JsonIgnore override val alpha: Property<Double>?,
    @JsonIgnore override val background: Property<List<DivBackground>>?,
    @JsonIgnore override val border: Property<DivBorder>?,
    @JsonIgnore override val columnSpan: Property<Int>?,
    @JsonIgnore val defaultStateId: Property<String>?,
    @JsonIgnore val divId: Property<String>?,
    @JsonIgnore override val extensions: Property<List<DivExtension>>?,
    @JsonIgnore override val focus: Property<DivFocus>?,
    @JsonIgnore override val height: Property<DivSize>?,
    @JsonIgnore override val id: Property<String>?,
    @JsonIgnore override val margins: Property<DivEdgeInsets>?,
    @JsonIgnore override val paddings: Property<DivEdgeInsets>?,
    @JsonIgnore override val rowSpan: Property<Int>?,
    @JsonIgnore override val selectedActions: Property<List<DivAction>>?,
    @JsonIgnore val states: Property<List<State>>?,
    @JsonIgnore override val tooltips: Property<List<DivTooltip>>?,
    @JsonIgnore val transitionAnimationSelector: Property<DivTransitionSelector>?,
    @JsonIgnore override val transitionChange: Property<DivChangeTransition>?,
    @JsonIgnore override val transitionIn: Property<DivAppearanceTransition>?,
    @JsonIgnore override val transitionOut: Property<DivAppearanceTransition>?,
    @JsonIgnore override val transitionTriggers: Property<List<DivTransitionTrigger>>?,
    @JsonIgnore override val visibility: Property<DivVisibility>?,
    @JsonIgnore override val visibilityAction: Property<DivVisibilityAction>?,
    @JsonIgnore override val visibilityActions: Property<List<DivVisibilityAction>>?,
    @JsonIgnore override val width: Property<DivSize>?,
) : Div(), DivBase {

    @JsonProperty("type") override val type = "state"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "accessibility" to accessibility,
            "alignment_horizontal" to alignmentHorizontal,
            "alignment_vertical" to alignmentVertical,
            "alpha" to alpha,
            "background" to background,
            "border" to border,
            "column_span" to columnSpan,
            "default_state_id" to defaultStateId,
            "div_id" to divId,
            "extensions" to extensions,
            "focus" to focus,
            "height" to height,
            "id" to id,
            "margins" to margins,
            "paddings" to paddings,
            "row_span" to rowSpan,
            "selected_actions" to selectedActions,
            "states" to states,
            "tooltips" to tooltips,
            "transition_animation_selector" to transitionAnimationSelector,
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

    class State internal constructor(
        @JsonIgnore val animationIn: Property<DivAnimation>?,
        @JsonIgnore val animationOut: Property<DivAnimation>?,
        @JsonIgnore val div: Property<Div>?,
        @JsonIgnore val stateId: Property<String>?,
        @JsonIgnore val swipeOutActions: Property<List<DivAction>>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "animation_in" to animationIn,
                "animation_out" to animationOut,
                "div" to div,
                "state_id" to stateId,
                "swipe_out_actions" to swipeOutActions,
            )
        }
    }
}

fun <T> TemplateContext<T>.divState(): LiteralProperty<DivState> {
    return value(DivState(
        accessibility = null,
        alignmentHorizontal = null,
        alignmentVertical = null,
        alpha = null,
        background = null,
        border = null,
        columnSpan = null,
        defaultStateId = null,
        divId = null,
        extensions = null,
        focus = null,
        height = null,
        id = null,
        margins = null,
        paddings = null,
        rowSpan = null,
        selectedActions = null,
        states = null,
        tooltips = null,
        transitionAnimationSelector = null,
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

fun <T> TemplateContext<T>.divState(
    states: Property<List<DivState.State>>? = null,
    accessibility: Property<DivAccessibility>? = null,
    alignmentHorizontal: Property<DivAlignmentHorizontal>? = null,
    alignmentVertical: Property<DivAlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    background: Property<List<DivBackground>>? = null,
    border: Property<DivBorder>? = null,
    columnSpan: Property<Int>? = null,
    defaultStateId: Property<String>? = null,
    divId: Property<String>? = null,
    extensions: Property<List<DivExtension>>? = null,
    focus: Property<DivFocus>? = null,
    height: Property<DivSize>? = null,
    id: Property<String>? = null,
    margins: Property<DivEdgeInsets>? = null,
    paddings: Property<DivEdgeInsets>? = null,
    rowSpan: Property<Int>? = null,
    selectedActions: Property<List<DivAction>>? = null,
    tooltips: Property<List<DivTooltip>>? = null,
    transitionAnimationSelector: Property<DivTransitionSelector>? = null,
    transitionChange: Property<DivChangeTransition>? = null,
    transitionIn: Property<DivAppearanceTransition>? = null,
    transitionOut: Property<DivAppearanceTransition>? = null,
    transitionTriggers: Property<List<DivTransitionTrigger>>? = null,
    visibility: Property<DivVisibility>? = null,
    visibilityAction: Property<DivVisibilityAction>? = null,
    visibilityActions: Property<List<DivVisibilityAction>>? = null,
    width: Property<DivSize>? = null,
): LiteralProperty<DivState> {
    return value(DivState(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = background,
        border = border,
        columnSpan = columnSpan,
        defaultStateId = defaultStateId,
        divId = divId,
        extensions = extensions,
        focus = focus,
        height = height,
        id = id,
        margins = margins,
        paddings = paddings,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        states = states,
        tooltips = tooltips,
        transitionAnimationSelector = transitionAnimationSelector,
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

fun <T> TemplateContext<T>.divState(
    states: List<DivState.State>? = null,
    accessibility: DivAccessibility? = null,
    alignmentHorizontal: DivAlignmentHorizontal? = null,
    alignmentVertical: DivAlignmentVertical? = null,
    alpha: Double? = null,
    background: List<DivBackground>? = null,
    border: DivBorder? = null,
    columnSpan: Int? = null,
    defaultStateId: String? = null,
    divId: String? = null,
    extensions: List<DivExtension>? = null,
    focus: DivFocus? = null,
    height: DivSize? = null,
    id: String? = null,
    margins: DivEdgeInsets? = null,
    paddings: DivEdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<DivAction>? = null,
    tooltips: List<DivTooltip>? = null,
    transitionAnimationSelector: DivTransitionSelector? = null,
    transitionChange: DivChangeTransition? = null,
    transitionIn: DivAppearanceTransition? = null,
    transitionOut: DivAppearanceTransition? = null,
    transitionTriggers: List<DivTransitionTrigger>? = null,
    visibility: DivVisibility? = null,
    visibilityAction: DivVisibilityAction? = null,
    visibilityActions: List<DivVisibilityAction>? = null,
    width: DivSize? = null,
): LiteralProperty<DivState> {
    return value(DivState(
        accessibility = optionalValue(accessibility),
        alignmentHorizontal = optionalValue(alignmentHorizontal),
        alignmentVertical = optionalValue(alignmentVertical),
        alpha = optionalValue(alpha),
        background = optionalValue(background),
        border = optionalValue(border),
        columnSpan = optionalValue(columnSpan),
        defaultStateId = optionalValue(defaultStateId),
        divId = optionalValue(divId),
        extensions = optionalValue(extensions),
        focus = optionalValue(focus),
        height = optionalValue(height),
        id = optionalValue(id),
        margins = optionalValue(margins),
        paddings = optionalValue(paddings),
        rowSpan = optionalValue(rowSpan),
        selectedActions = optionalValue(selectedActions),
        states = optionalValue(states),
        tooltips = optionalValue(tooltips),
        transitionAnimationSelector = optionalValue(transitionAnimationSelector),
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

fun <T> TemplateContext<T>.state(): LiteralProperty<DivState.State> {
    return value(DivState.State(
        animationIn = null,
        animationOut = null,
        div = null,
        stateId = null,
        swipeOutActions = null,
    ))
}

fun <T> TemplateContext<T>.state(
    stateId: Property<String>? = null,
    animationIn: Property<DivAnimation>? = null,
    animationOut: Property<DivAnimation>? = null,
    div: Property<Div>? = null,
    swipeOutActions: Property<List<DivAction>>? = null,
): LiteralProperty<DivState.State> {
    return value(DivState.State(
        animationIn = animationIn,
        animationOut = animationOut,
        div = div,
        stateId = stateId,
        swipeOutActions = swipeOutActions,
    ))
}

fun <T> TemplateContext<T>.state(
    stateId: String? = null,
    animationIn: DivAnimation? = null,
    animationOut: DivAnimation? = null,
    div: Div? = null,
    swipeOutActions: List<DivAction>? = null,
): LiteralProperty<DivState.State> {
    return value(DivState.State(
        animationIn = optionalValue(animationIn),
        animationOut = optionalValue(animationOut),
        div = optionalValue(div),
        stateId = optionalValue(stateId),
        swipeOutActions = optionalValue(swipeOutActions),
    ))
}

fun CardContext.divState(
    states: ValueProperty<List<DivState.State>>,
    accessibility: ValueProperty<DivAccessibility>? = null,
    alignmentHorizontal: ValueProperty<DivAlignmentHorizontal>? = null,
    alignmentVertical: ValueProperty<DivAlignmentVertical>? = null,
    alpha: ValueProperty<Double>? = null,
    background: ValueProperty<List<DivBackground>>? = null,
    border: ValueProperty<DivBorder>? = null,
    columnSpan: ValueProperty<Int>? = null,
    defaultStateId: ValueProperty<String>? = null,
    divId: ValueProperty<String>? = null,
    extensions: ValueProperty<List<DivExtension>>? = null,
    focus: ValueProperty<DivFocus>? = null,
    height: ValueProperty<DivSize>? = null,
    id: ValueProperty<String>? = null,
    margins: ValueProperty<DivEdgeInsets>? = null,
    paddings: ValueProperty<DivEdgeInsets>? = null,
    rowSpan: ValueProperty<Int>? = null,
    selectedActions: ValueProperty<List<DivAction>>? = null,
    tooltips: ValueProperty<List<DivTooltip>>? = null,
    transitionAnimationSelector: ValueProperty<DivTransitionSelector>? = null,
    transitionChange: ValueProperty<DivChangeTransition>? = null,
    transitionIn: ValueProperty<DivAppearanceTransition>? = null,
    transitionOut: ValueProperty<DivAppearanceTransition>? = null,
    transitionTriggers: ValueProperty<List<DivTransitionTrigger>>? = null,
    visibility: ValueProperty<DivVisibility>? = null,
    visibilityAction: ValueProperty<DivVisibilityAction>? = null,
    visibilityActions: ValueProperty<List<DivVisibilityAction>>? = null,
    width: ValueProperty<DivSize>? = null,
): DivState {
    return DivState(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = background,
        border = border,
        columnSpan = columnSpan,
        defaultStateId = defaultStateId,
        divId = divId,
        extensions = extensions,
        focus = focus,
        height = height,
        id = id,
        margins = margins,
        paddings = paddings,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        states = states,
        tooltips = tooltips,
        transitionAnimationSelector = transitionAnimationSelector,
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

fun CardContext.divState(
    states: List<DivState.State>,
    accessibility: DivAccessibility? = null,
    alignmentHorizontal: DivAlignmentHorizontal? = null,
    alignmentVertical: DivAlignmentVertical? = null,
    alpha: Double? = null,
    background: List<DivBackground>? = null,
    border: DivBorder? = null,
    columnSpan: Int? = null,
    defaultStateId: String? = null,
    divId: String? = null,
    extensions: List<DivExtension>? = null,
    focus: DivFocus? = null,
    height: DivSize? = null,
    id: String? = null,
    margins: DivEdgeInsets? = null,
    paddings: DivEdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<DivAction>? = null,
    tooltips: List<DivTooltip>? = null,
    transitionAnimationSelector: DivTransitionSelector? = null,
    transitionChange: DivChangeTransition? = null,
    transitionIn: DivAppearanceTransition? = null,
    transitionOut: DivAppearanceTransition? = null,
    transitionTriggers: List<DivTransitionTrigger>? = null,
    visibility: DivVisibility? = null,
    visibilityAction: DivVisibilityAction? = null,
    visibilityActions: List<DivVisibilityAction>? = null,
    width: DivSize? = null,
): DivState {
    return DivState(
        accessibility = optionalValue(accessibility),
        alignmentHorizontal = optionalValue(alignmentHorizontal),
        alignmentVertical = optionalValue(alignmentVertical),
        alpha = optionalValue(alpha),
        background = optionalValue(background),
        border = optionalValue(border),
        columnSpan = optionalValue(columnSpan),
        defaultStateId = optionalValue(defaultStateId),
        divId = optionalValue(divId),
        extensions = optionalValue(extensions),
        focus = optionalValue(focus),
        height = optionalValue(height),
        id = optionalValue(id),
        margins = optionalValue(margins),
        paddings = optionalValue(paddings),
        rowSpan = optionalValue(rowSpan),
        selectedActions = optionalValue(selectedActions),
        states = value(states),
        tooltips = optionalValue(tooltips),
        transitionAnimationSelector = optionalValue(transitionAnimationSelector),
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

fun CardContext.state(
    stateId: ValueProperty<String>,
    animationIn: ValueProperty<DivAnimation>? = null,
    animationOut: ValueProperty<DivAnimation>? = null,
    div: ValueProperty<Div>? = null,
    swipeOutActions: ValueProperty<List<DivAction>>? = null,
): DivState.State {
    return DivState.State(
        animationIn = animationIn,
        animationOut = animationOut,
        div = div,
        stateId = stateId,
        swipeOutActions = swipeOutActions,
    )
}

fun CardContext.state(
    stateId: String,
    animationIn: DivAnimation? = null,
    animationOut: DivAnimation? = null,
    div: Div? = null,
    swipeOutActions: List<DivAction>? = null,
): DivState.State {
    return DivState.State(
        animationIn = optionalValue(animationIn),
        animationOut = optionalValue(animationOut),
        div = optionalValue(div),
        stateId = value(stateId),
        swipeOutActions = optionalValue(swipeOutActions),
    )
}
