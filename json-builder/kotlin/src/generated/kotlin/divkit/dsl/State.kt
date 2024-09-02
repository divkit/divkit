@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * It contains sets of states for visual elements and switches between them.
 * 
 * Can be created using the method [state].
 * 
 * Required parameters: `type, states`.
 */
@Generated
class State internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "state")
    )

    operator fun plus(additive: Properties): State = State(
        Properties(
            accessibility = additive.accessibility ?: properties.accessibility,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            defaultStateId = additive.defaultStateId ?: properties.defaultStateId,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            divId = additive.divId ?: properties.divId,
            extensions = additive.extensions ?: properties.extensions,
            focus = additive.focus ?: properties.focus,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
            layoutProvider = additive.layoutProvider ?: properties.layoutProvider,
            margins = additive.margins ?: properties.margins,
            paddings = additive.paddings ?: properties.paddings,
            reuseId = additive.reuseId ?: properties.reuseId,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            stateIdVariable = additive.stateIdVariable ?: properties.stateIdVariable,
            states = additive.states ?: properties.states,
            tooltips = additive.tooltips ?: properties.tooltips,
            transform = additive.transform ?: properties.transform,
            transitionAnimationSelector = additive.transitionAnimationSelector ?: properties.transitionAnimationSelector,
            transitionChange = additive.transitionChange ?: properties.transitionChange,
            transitionIn = additive.transitionIn ?: properties.transitionIn,
            transitionOut = additive.transitionOut ?: properties.transitionOut,
            transitionTriggers = additive.transitionTriggers ?: properties.transitionTriggers,
            variableTriggers = additive.variableTriggers ?: properties.variableTriggers,
            variables = additive.variables ?: properties.variables,
            visibility = additive.visibility ?: properties.visibility,
            visibilityAction = additive.visibilityAction ?: properties.visibilityAction,
            visibilityActions = additive.visibilityActions ?: properties.visibilityActions,
            width = additive.width ?: properties.width,
        )
    )

    class Properties internal constructor(
        /**
         * Accessibility settings.
         */
        val accessibility: Property<Accessibility>?,
        /**
         * Horizontal alignment of an element inside the parent element.
         */
        val alignmentHorizontal: Property<AlignmentHorizontal>?,
        /**
         * Vertical alignment of an element inside the parent element.
         */
        val alignmentVertical: Property<AlignmentVertical>?,
        /**
         * Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
         * Default value: `1.0`.
         */
        val alpha: Property<Double>?,
        /**
         * Element background. It can contain multiple layers.
         */
        val background: Property<List<Background>>?,
        /**
         * Element stroke.
         */
        val border: Property<Border>?,
        /**
         * Merges cells in a column of the [grid](div-grid.md) element.
         */
        val columnSpan: Property<Int>?,
        /**
         * ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
         */
        val defaultStateId: Property<String>?,
        /**
         * Actions when an element disappears from the screen.
         */
        val disappearActions: Property<List<DisappearAction>>?,
        /**
         * ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
         */
        @Deprecated("Marked as deprecated in the JSON schema ")
        val divId: Property<String>?,
        /**
         * Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
         */
        val extensions: Property<List<Extension>>?,
        /**
         * Parameters when focusing on an element or losing focus.
         */
        val focus: Property<Focus>?,
        /**
         * Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
         * Default value: `{"type": "wrap_content"}`.
         */
        val height: Property<Size>?,
        /**
         * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
         */
        val id: Property<String>?,
        /**
         * Provides element real size values after a layout cycle.
         */
        val layoutProvider: Property<LayoutProvider>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
        /**
         * Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
         */
        val reuseId: Property<String>?,
        /**
         * Merges cells in a string of the [grid](div-grid.md) element.
         */
        val rowSpan: Property<Int>?,
        /**
         * List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
         */
        val selectedActions: Property<List<Action>>?,
        /**
         * The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
         */
        val stateIdVariable: Property<String>?,
        /**
         * States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](../../interaction) of the [action](div-action.md) element.
         */
        val states: Property<List<Item>>?,
        /**
         * Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
         */
        val tooltips: Property<List<Tooltip>>?,
        /**
         * Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
         */
        val transform: Property<Transform>?,
        /**
         * It determines which events trigger transition animations.
         * Default value: `state_change`.
         */
        @Deprecated("Marked as deprecated in the JSON schema ")
        val transitionAnimationSelector: Property<TransitionSelector>?,
        /**
         * Change animation. It is played when the position or size of an element changes in the new layout.
         */
        val transitionChange: Property<ChangeTransition>?,
        /**
         * Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
         */
        val transitionIn: Property<AppearanceTransition>?,
        /**
         * Disappearance animation. It is played when an element disappears in the new layout.
         */
        val transitionOut: Property<AppearanceTransition>?,
        /**
         * Animation starting triggers. Default value: `[state_change, visibility_change]`.
         */
        val transitionTriggers: Property<List<ArrayElement<TransitionTrigger>>>?,
        /**
         * Triggers for changing variables within an element.
         */
        val variableTriggers: Property<List<Trigger>>?,
        /**
         * Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
         */
        val variables: Property<List<Variable>>?,
        /**
         * Element visibility.
         * Default value: `visible`.
         */
        val visibility: Property<Visibility>?,
        /**
         * Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
         */
        val visibilityAction: Property<VisibilityAction>?,
        /**
         * Actions when an element appears on the screen.
         */
        val visibilityActions: Property<List<VisibilityAction>>?,
        /**
         * Element width.
         * Default value: `{"type": "match_parent"}`.
         */
        val width: Property<Size>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("accessibility", accessibility)
            result.tryPutProperty("alignment_horizontal", alignmentHorizontal)
            result.tryPutProperty("alignment_vertical", alignmentVertical)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("default_state_id", defaultStateId)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("div_id", divId)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
            result.tryPutProperty("layout_provider", layoutProvider)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("reuse_id", reuseId)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("state_id_variable", stateIdVariable)
            result.tryPutProperty("states", states)
            result.tryPutProperty("tooltips", tooltips)
            result.tryPutProperty("transform", transform)
            result.tryPutProperty("transition_animation_selector", transitionAnimationSelector)
            result.tryPutProperty("transition_change", transitionChange)
            result.tryPutProperty("transition_in", transitionIn)
            result.tryPutProperty("transition_out", transitionOut)
            result.tryPutProperty("transition_triggers", transitionTriggers)
            result.tryPutProperty("variable_triggers", variableTriggers)
            result.tryPutProperty("variables", variables)
            result.tryPutProperty("visibility", visibility)
            result.tryPutProperty("visibility_action", visibilityAction)
            result.tryPutProperty("visibility_actions", visibilityActions)
            result.tryPutProperty("width", width)
            return result
        }
    }

    /**
     * Can be created using the method [stateItem].
     * 
     * Required parameters: `state_id`.
     */
    @Generated
    class Item internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Item = Item(
            Properties(
                animationIn = additive.animationIn ?: properties.animationIn,
                animationOut = additive.animationOut ?: properties.animationOut,
                div = additive.div ?: properties.div,
                stateId = additive.stateId ?: properties.stateId,
                swipeOutActions = additive.swipeOutActions ?: properties.swipeOutActions,
            )
        )

        class Properties internal constructor(
            /**
             * State appearance animation. Use `transition_in` instead.
             */
            @Deprecated("Marked as deprecated in the JSON schema ")
            val animationIn: Property<Animation>?,
            /**
             * State disappearance animation. Use `transition_out` instead.
             */
            @Deprecated("Marked as deprecated in the JSON schema ")
            val animationOut: Property<Animation>?,
            /**
             * Contents. If the parameter is missing, the state won't be displayed.
             */
            val div: Property<Div>?,
            /**
             * State ID. It must be unique at one hierarchy level.
             */
            val stateId: Property<String>?,
            /**
             * Actions when swiping the state horizontally.
             */
            @Deprecated("Marked as deprecated in the JSON schema ")
            val swipeOutActions: Property<List<Action>>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("animation_in", animationIn)
                result.tryPutProperty("animation_out", animationOut)
                result.tryPutProperty("div", div)
                result.tryPutProperty("state_id", stateId)
                result.tryPutProperty("swipe_out_actions", swipeOutActions)
                return result
            }
        }
    }

}

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param defaultStateId ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param divId ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param stateIdVariable The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
 * @param states States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](../../interaction) of the [action](div-action.md) element.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionAnimationSelector It determines which events trigger transition animations.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.state(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    defaultStateId: String? = null,
    disappearActions: List<DisappearAction>? = null,
    divId: String? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    stateIdVariable: String? = null,
    states: List<State.Item>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): State = State(
    State.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        defaultStateId = valueOrNull(defaultStateId),
        disappearActions = valueOrNull(disappearActions),
        divId = valueOrNull(divId),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        layoutProvider = valueOrNull(layoutProvider),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        stateIdVariable = valueOrNull(stateIdVariable),
        states = valueOrNull(states),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionAnimationSelector = valueOrNull(transitionAnimationSelector),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        variableTriggers = valueOrNull(variableTriggers),
        variables = valueOrNull(variables),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param defaultStateId ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param divId ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param stateIdVariable The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
 * @param states States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](../../interaction) of the [action](div-action.md) element.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionAnimationSelector It determines which events trigger transition animations.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.stateProps(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    defaultStateId: String? = null,
    disappearActions: List<DisappearAction>? = null,
    divId: String? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    stateIdVariable: String? = null,
    states: List<State.Item>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
) = State.Properties(
    accessibility = valueOrNull(accessibility),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnSpan = valueOrNull(columnSpan),
    defaultStateId = valueOrNull(defaultStateId),
    disappearActions = valueOrNull(disappearActions),
    divId = valueOrNull(divId),
    extensions = valueOrNull(extensions),
    focus = valueOrNull(focus),
    height = valueOrNull(height),
    id = valueOrNull(id),
    layoutProvider = valueOrNull(layoutProvider),
    margins = valueOrNull(margins),
    paddings = valueOrNull(paddings),
    reuseId = valueOrNull(reuseId),
    rowSpan = valueOrNull(rowSpan),
    selectedActions = valueOrNull(selectedActions),
    stateIdVariable = valueOrNull(stateIdVariable),
    states = valueOrNull(states),
    tooltips = valueOrNull(tooltips),
    transform = valueOrNull(transform),
    transitionAnimationSelector = valueOrNull(transitionAnimationSelector),
    transitionChange = valueOrNull(transitionChange),
    transitionIn = valueOrNull(transitionIn),
    transitionOut = valueOrNull(transitionOut),
    transitionTriggers = valueOrNull(transitionTriggers),
    variableTriggers = valueOrNull(variableTriggers),
    variables = valueOrNull(variables),
    visibility = valueOrNull(visibility),
    visibilityAction = valueOrNull(visibilityAction),
    visibilityActions = valueOrNull(visibilityActions),
    width = valueOrNull(width),
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param defaultStateId ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param divId ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param stateIdVariable The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
 * @param states States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](../../interaction) of the [action](div-action.md) element.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionAnimationSelector It determines which events trigger transition animations.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun TemplateScope.stateRefs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    defaultStateId: ReferenceProperty<String>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    divId: ReferenceProperty<String>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    stateIdVariable: ReferenceProperty<String>? = null,
    states: ReferenceProperty<List<State.Item>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionAnimationSelector: ReferenceProperty<TransitionSelector>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
) = State.Properties(
    accessibility = accessibility,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    background = background,
    border = border,
    columnSpan = columnSpan,
    defaultStateId = defaultStateId,
    disappearActions = disappearActions,
    divId = divId,
    extensions = extensions,
    focus = focus,
    height = height,
    id = id,
    layoutProvider = layoutProvider,
    margins = margins,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    stateIdVariable = stateIdVariable,
    states = states,
    tooltips = tooltips,
    transform = transform,
    transitionAnimationSelector = transitionAnimationSelector,
    transitionChange = transitionChange,
    transitionIn = transitionIn,
    transitionOut = transitionOut,
    transitionTriggers = transitionTriggers,
    variableTriggers = variableTriggers,
    variables = variables,
    visibility = visibility,
    visibilityAction = visibilityAction,
    visibilityActions = visibilityActions,
    width = width,
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param defaultStateId ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param divId ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param stateIdVariable The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
 * @param states States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](../../interaction) of the [action](div-action.md) element.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionAnimationSelector It determines which events trigger transition animations.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun State.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    defaultStateId: String? = null,
    disappearActions: List<DisappearAction>? = null,
    divId: String? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    stateIdVariable: String? = null,
    states: List<State.Item>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): State = State(
    State.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        defaultStateId = valueOrNull(defaultStateId) ?: properties.defaultStateId,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        divId = valueOrNull(divId) ?: properties.divId,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        focus = valueOrNull(focus) ?: properties.focus,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
        layoutProvider = valueOrNull(layoutProvider) ?: properties.layoutProvider,
        margins = valueOrNull(margins) ?: properties.margins,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        reuseId = valueOrNull(reuseId) ?: properties.reuseId,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        stateIdVariable = valueOrNull(stateIdVariable) ?: properties.stateIdVariable,
        states = valueOrNull(states) ?: properties.states,
        tooltips = valueOrNull(tooltips) ?: properties.tooltips,
        transform = valueOrNull(transform) ?: properties.transform,
        transitionAnimationSelector = valueOrNull(transitionAnimationSelector) ?: properties.transitionAnimationSelector,
        transitionChange = valueOrNull(transitionChange) ?: properties.transitionChange,
        transitionIn = valueOrNull(transitionIn) ?: properties.transitionIn,
        transitionOut = valueOrNull(transitionOut) ?: properties.transitionOut,
        transitionTriggers = valueOrNull(transitionTriggers) ?: properties.transitionTriggers,
        variableTriggers = valueOrNull(variableTriggers) ?: properties.variableTriggers,
        variables = valueOrNull(variables) ?: properties.variables,
        visibility = valueOrNull(visibility) ?: properties.visibility,
        visibilityAction = valueOrNull(visibilityAction) ?: properties.visibilityAction,
        visibilityActions = valueOrNull(visibilityActions) ?: properties.visibilityActions,
        width = valueOrNull(width) ?: properties.width,
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param defaultStateId ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param divId ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param stateIdVariable The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
 * @param states States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](../../interaction) of the [action](div-action.md) element.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionAnimationSelector It determines which events trigger transition animations.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun State.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    defaultStateId: ReferenceProperty<String>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    divId: ReferenceProperty<String>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    stateIdVariable: ReferenceProperty<String>? = null,
    states: ReferenceProperty<List<State.Item>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionAnimationSelector: ReferenceProperty<TransitionSelector>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): State = State(
    State.Properties(
        accessibility = accessibility ?: properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        defaultStateId = defaultStateId ?: properties.defaultStateId,
        disappearActions = disappearActions ?: properties.disappearActions,
        divId = divId ?: properties.divId,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        height = height ?: properties.height,
        id = id ?: properties.id,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        margins = margins ?: properties.margins,
        paddings = paddings ?: properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        stateIdVariable = stateIdVariable ?: properties.stateIdVariable,
        states = states ?: properties.states,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
        transitionAnimationSelector = transitionAnimationSelector ?: properties.transitionAnimationSelector,
        transitionChange = transitionChange ?: properties.transitionChange,
        transitionIn = transitionIn ?: properties.transitionIn,
        transitionOut = transitionOut ?: properties.transitionOut,
        transitionTriggers = transitionTriggers ?: properties.transitionTriggers,
        variableTriggers = variableTriggers ?: properties.variableTriggers,
        variables = variables ?: properties.variables,
        visibility = visibility ?: properties.visibility,
        visibilityAction = visibilityAction ?: properties.visibilityAction,
        visibilityActions = visibilityActions ?: properties.visibilityActions,
        width = width ?: properties.width,
    )
)

/**
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param defaultStateId ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param transitionAnimationSelector It determines which events trigger transition animations.
 * @param visibility Element visibility.
 */
@Generated
fun State.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    defaultStateId: ExpressionProperty<String>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    transitionAnimationSelector: ExpressionProperty<TransitionSelector>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): State = State(
    State.Properties(
        accessibility = properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = properties.background,
        border = properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        defaultStateId = defaultStateId ?: properties.defaultStateId,
        disappearActions = properties.disappearActions,
        divId = properties.divId,
        extensions = properties.extensions,
        focus = properties.focus,
        height = properties.height,
        id = properties.id,
        layoutProvider = properties.layoutProvider,
        margins = properties.margins,
        paddings = properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = properties.selectedActions,
        stateIdVariable = properties.stateIdVariable,
        states = properties.states,
        tooltips = properties.tooltips,
        transform = properties.transform,
        transitionAnimationSelector = transitionAnimationSelector ?: properties.transitionAnimationSelector,
        transitionChange = properties.transitionChange,
        transitionIn = properties.transitionIn,
        transitionOut = properties.transitionOut,
        transitionTriggers = properties.transitionTriggers,
        variableTriggers = properties.variableTriggers,
        variables = properties.variables,
        visibility = visibility ?: properties.visibility,
        visibilityAction = properties.visibilityAction,
        visibilityActions = properties.visibilityActions,
        width = properties.width,
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param defaultStateId ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param divId ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param stateIdVariable The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
 * @param states States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](../../interaction) of the [action](div-action.md) element.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionAnimationSelector It determines which events trigger transition animations.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<State>.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    defaultStateId: String? = null,
    disappearActions: List<DisappearAction>? = null,
    divId: String? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    stateIdVariable: String? = null,
    states: List<State.Item>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Component<State> = Component(
    template = template,
    properties = State.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        defaultStateId = valueOrNull(defaultStateId),
        disappearActions = valueOrNull(disappearActions),
        divId = valueOrNull(divId),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        layoutProvider = valueOrNull(layoutProvider),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        stateIdVariable = valueOrNull(stateIdVariable),
        states = valueOrNull(states),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionAnimationSelector = valueOrNull(transitionAnimationSelector),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        variableTriggers = valueOrNull(variableTriggers),
        variables = valueOrNull(variables),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    ).mergeWith(properties)
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param defaultStateId ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param divId ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param stateIdVariable The name of the variable that stores the ID for the current state. If the variable changes, the active state will also change. The variable is prioritized over the default_state_id parameter.
 * @param states States. Each element can have a few states with a different layout. Transition between states is performed using [special scheme](../../interaction) of the [action](div-action.md) element.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionAnimationSelector It determines which events trigger transition animations.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<State>.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    defaultStateId: ReferenceProperty<String>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    divId: ReferenceProperty<String>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    stateIdVariable: ReferenceProperty<String>? = null,
    states: ReferenceProperty<List<State.Item>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionAnimationSelector: ReferenceProperty<TransitionSelector>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Component<State> = Component(
    template = template,
    properties = State.Properties(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = background,
        border = border,
        columnSpan = columnSpan,
        defaultStateId = defaultStateId,
        disappearActions = disappearActions,
        divId = divId,
        extensions = extensions,
        focus = focus,
        height = height,
        id = id,
        layoutProvider = layoutProvider,
        margins = margins,
        paddings = paddings,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        stateIdVariable = stateIdVariable,
        states = states,
        tooltips = tooltips,
        transform = transform,
        transitionAnimationSelector = transitionAnimationSelector,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        variableTriggers = variableTriggers,
        variables = variables,
        visibility = visibility,
        visibilityAction = visibilityAction,
        visibilityActions = visibilityActions,
        width = width,
    ).mergeWith(properties)
)

/**
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param defaultStateId ID of the status that will be set by default. If the parameter isnt set, the first state of the `states` will be set.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param transitionAnimationSelector It determines which events trigger transition animations.
 * @param visibility Element visibility.
 */
@Generated
fun Component<State>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    defaultStateId: ExpressionProperty<String>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    transitionAnimationSelector: ExpressionProperty<TransitionSelector>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<State> = Component(
    template = template,
    properties = State.Properties(
        accessibility = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = null,
        border = null,
        columnSpan = columnSpan,
        defaultStateId = defaultStateId,
        disappearActions = null,
        divId = null,
        extensions = null,
        focus = null,
        height = null,
        id = null,
        layoutProvider = null,
        margins = null,
        paddings = null,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = null,
        stateIdVariable = null,
        states = null,
        tooltips = null,
        transform = null,
        transitionAnimationSelector = transitionAnimationSelector,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        variableTriggers = null,
        variables = null,
        visibility = visibility,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    ).mergeWith(properties)
)

@Generated
operator fun Component<State>.plus(additive: State.Properties): Component<State> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun State.asList() = listOf(this)

/**
 * @param animationIn State appearance animation. Use `transition_in` instead.
 * @param animationOut State disappearance animation. Use `transition_out` instead.
 * @param div Contents. If the parameter is missing, the state won't be displayed.
 * @param stateId State ID. It must be unique at one hierarchy level.
 * @param swipeOutActions Actions when swiping the state horizontally.
 */
@Generated
fun DivScope.stateItem(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    div: Div? = null,
    stateId: String? = null,
    swipeOutActions: List<Action>? = null,
): State.Item = State.Item(
    State.Item.Properties(
        animationIn = valueOrNull(animationIn),
        animationOut = valueOrNull(animationOut),
        div = valueOrNull(div),
        stateId = valueOrNull(stateId),
        swipeOutActions = valueOrNull(swipeOutActions),
    )
)

/**
 * @param animationIn State appearance animation. Use `transition_in` instead.
 * @param animationOut State disappearance animation. Use `transition_out` instead.
 * @param div Contents. If the parameter is missing, the state won't be displayed.
 * @param stateId State ID. It must be unique at one hierarchy level.
 * @param swipeOutActions Actions when swiping the state horizontally.
 */
@Generated
fun DivScope.stateItemProps(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    div: Div? = null,
    stateId: String? = null,
    swipeOutActions: List<Action>? = null,
) = State.Item.Properties(
    animationIn = valueOrNull(animationIn),
    animationOut = valueOrNull(animationOut),
    div = valueOrNull(div),
    stateId = valueOrNull(stateId),
    swipeOutActions = valueOrNull(swipeOutActions),
)

/**
 * @param animationIn State appearance animation. Use `transition_in` instead.
 * @param animationOut State disappearance animation. Use `transition_out` instead.
 * @param div Contents. If the parameter is missing, the state won't be displayed.
 * @param stateId State ID. It must be unique at one hierarchy level.
 * @param swipeOutActions Actions when swiping the state horizontally.
 */
@Generated
fun TemplateScope.stateItemRefs(
    `use named arguments`: Guard = Guard.instance,
    animationIn: ReferenceProperty<Animation>? = null,
    animationOut: ReferenceProperty<Animation>? = null,
    div: ReferenceProperty<Div>? = null,
    stateId: ReferenceProperty<String>? = null,
    swipeOutActions: ReferenceProperty<List<Action>>? = null,
) = State.Item.Properties(
    animationIn = animationIn,
    animationOut = animationOut,
    div = div,
    stateId = stateId,
    swipeOutActions = swipeOutActions,
)

/**
 * @param animationIn State appearance animation. Use `transition_in` instead.
 * @param animationOut State disappearance animation. Use `transition_out` instead.
 * @param div Contents. If the parameter is missing, the state won't be displayed.
 * @param stateId State ID. It must be unique at one hierarchy level.
 * @param swipeOutActions Actions when swiping the state horizontally.
 */
@Generated
fun State.Item.override(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    div: Div? = null,
    stateId: String? = null,
    swipeOutActions: List<Action>? = null,
): State.Item = State.Item(
    State.Item.Properties(
        animationIn = valueOrNull(animationIn) ?: properties.animationIn,
        animationOut = valueOrNull(animationOut) ?: properties.animationOut,
        div = valueOrNull(div) ?: properties.div,
        stateId = valueOrNull(stateId) ?: properties.stateId,
        swipeOutActions = valueOrNull(swipeOutActions) ?: properties.swipeOutActions,
    )
)

/**
 * @param animationIn State appearance animation. Use `transition_in` instead.
 * @param animationOut State disappearance animation. Use `transition_out` instead.
 * @param div Contents. If the parameter is missing, the state won't be displayed.
 * @param stateId State ID. It must be unique at one hierarchy level.
 * @param swipeOutActions Actions when swiping the state horizontally.
 */
@Generated
fun State.Item.defer(
    `use named arguments`: Guard = Guard.instance,
    animationIn: ReferenceProperty<Animation>? = null,
    animationOut: ReferenceProperty<Animation>? = null,
    div: ReferenceProperty<Div>? = null,
    stateId: ReferenceProperty<String>? = null,
    swipeOutActions: ReferenceProperty<List<Action>>? = null,
): State.Item = State.Item(
    State.Item.Properties(
        animationIn = animationIn ?: properties.animationIn,
        animationOut = animationOut ?: properties.animationOut,
        div = div ?: properties.div,
        stateId = stateId ?: properties.stateId,
        swipeOutActions = swipeOutActions ?: properties.swipeOutActions,
    )
)

@Generated
fun State.Item.asList() = listOf(this)
