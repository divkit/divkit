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
 * Two-state toggle that allows the user to control a Boolean variable. The element's look-and-feel varies by platform. The toggle has a fixed size in iOS.
 * 
 * Can be created using the method [switch].
 * 
 * Required parameters: `type, is_on_variable`.
 */
@Generated
data class Switch internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "switch")
    )

    operator fun plus(additive: Properties): Switch = Switch(
        Properties(
            accessibility = additive.accessibility ?: properties.accessibility,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            animators = additive.animators ?: properties.animators,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            extensions = additive.extensions ?: properties.extensions,
            focus = additive.focus ?: properties.focus,
            functions = additive.functions ?: properties.functions,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
            isEnabled = additive.isEnabled ?: properties.isEnabled,
            isOnVariable = additive.isOnVariable ?: properties.isOnVariable,
            layoutProvider = additive.layoutProvider ?: properties.layoutProvider,
            margins = additive.margins ?: properties.margins,
            onColor = additive.onColor ?: properties.onColor,
            paddings = additive.paddings ?: properties.paddings,
            reuseId = additive.reuseId ?: properties.reuseId,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            tooltips = additive.tooltips ?: properties.tooltips,
            transform = additive.transform ?: properties.transform,
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

    data class Properties internal constructor(
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
         * Declaration of animators that change variable values over time.
         */
        val animators: Property<List<Animator>>?,
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
         * Actions when an element disappears from the screen.
         */
        val disappearActions: Property<List<DisappearAction>>?,
        /**
         * Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
         */
        val extensions: Property<List<Extension>>?,
        /**
         * Parameters when focusing on an element or losing focus.
         */
        val focus: Property<Focus>?,
        /**
         * User functions.
         */
        val functions: Property<List<Function>>?,
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
         * Enables or disables the element's toggle functionality.
         * Default value: `true`.
         */
        val isEnabled: Property<Boolean>?,
        /**
         * Name of the Boolean variable assigned to the toggle.
         */
        val isOnVariable: Property<String>?,
        /**
         * Provides data on the actual size of the element.
         */
        val layoutProvider: Property<LayoutProvider>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Color of the toggle in the enabled state. If the color is omitted:
        : iOS standard system color is used
        : the color specified in `Div2Context` on Android is used.
         */
        val onColor: Property<Color>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
        /**
         * ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
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
         * Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
         */
        val tooltips: Property<List<Tooltip>>?,
        /**
         * Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
         */
        val transform: Property<Transform>?,
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
         * Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
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
            result.tryPutProperty("animators", animators)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("functions", functions)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
            result.tryPutProperty("is_enabled", isEnabled)
            result.tryPutProperty("is_on_variable", isOnVariable)
            result.tryPutProperty("layout_provider", layoutProvider)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("on_color", onColor)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("reuse_id", reuseId)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("tooltips", tooltips)
            result.tryPutProperty("transform", transform)
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
}

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param isOnVariable Name of the Boolean variable assigned to the toggle.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.switch(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    isEnabled: Boolean? = null,
    isOnVariable: String? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    onColor: Color? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
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
): Switch = Switch(
    Switch.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        animators = valueOrNull(animators),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        disappearActions = valueOrNull(disappearActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        id = valueOrNull(id),
        isEnabled = valueOrNull(isEnabled),
        isOnVariable = valueOrNull(isOnVariable),
        layoutProvider = valueOrNull(layoutProvider),
        margins = valueOrNull(margins),
        onColor = valueOrNull(onColor),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
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
 * @param animators Declaration of animators that change variable values over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param isOnVariable Name of the Boolean variable assigned to the toggle.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.switchProps(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    isEnabled: Boolean? = null,
    isOnVariable: String? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    onColor: Color? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
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
) = Switch.Properties(
    accessibility = valueOrNull(accessibility),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    animators = valueOrNull(animators),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnSpan = valueOrNull(columnSpan),
    disappearActions = valueOrNull(disappearActions),
    extensions = valueOrNull(extensions),
    focus = valueOrNull(focus),
    functions = valueOrNull(functions),
    height = valueOrNull(height),
    id = valueOrNull(id),
    isEnabled = valueOrNull(isEnabled),
    isOnVariable = valueOrNull(isOnVariable),
    layoutProvider = valueOrNull(layoutProvider),
    margins = valueOrNull(margins),
    onColor = valueOrNull(onColor),
    paddings = valueOrNull(paddings),
    reuseId = valueOrNull(reuseId),
    rowSpan = valueOrNull(rowSpan),
    selectedActions = valueOrNull(selectedActions),
    tooltips = valueOrNull(tooltips),
    transform = valueOrNull(transform),
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
 * @param animators Declaration of animators that change variable values over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param isOnVariable Name of the Boolean variable assigned to the toggle.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun TemplateScope.switchRefs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animators: ReferenceProperty<List<Animator>>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    isOnVariable: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    onColor: ReferenceProperty<Color>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
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
) = Switch.Properties(
    accessibility = accessibility,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animators = animators,
    background = background,
    border = border,
    columnSpan = columnSpan,
    disappearActions = disappearActions,
    extensions = extensions,
    focus = focus,
    functions = functions,
    height = height,
    id = id,
    isEnabled = isEnabled,
    isOnVariable = isOnVariable,
    layoutProvider = layoutProvider,
    margins = margins,
    onColor = onColor,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    tooltips = tooltips,
    transform = transform,
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
 * @param animators Declaration of animators that change variable values over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param isOnVariable Name of the Boolean variable assigned to the toggle.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Switch.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    isEnabled: Boolean? = null,
    isOnVariable: String? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    onColor: Color? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
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
): Switch = Switch(
    Switch.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        animators = valueOrNull(animators) ?: properties.animators,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        focus = valueOrNull(focus) ?: properties.focus,
        functions = valueOrNull(functions) ?: properties.functions,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
        isEnabled = valueOrNull(isEnabled) ?: properties.isEnabled,
        isOnVariable = valueOrNull(isOnVariable) ?: properties.isOnVariable,
        layoutProvider = valueOrNull(layoutProvider) ?: properties.layoutProvider,
        margins = valueOrNull(margins) ?: properties.margins,
        onColor = valueOrNull(onColor) ?: properties.onColor,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        reuseId = valueOrNull(reuseId) ?: properties.reuseId,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        tooltips = valueOrNull(tooltips) ?: properties.tooltips,
        transform = valueOrNull(transform) ?: properties.transform,
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
 * @param animators Declaration of animators that change variable values over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param isOnVariable Name of the Boolean variable assigned to the toggle.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Switch.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animators: ReferenceProperty<List<Animator>>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    isOnVariable: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    onColor: ReferenceProperty<Color>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
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
): Switch = Switch(
    Switch.Properties(
        accessibility = accessibility ?: properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animators = animators ?: properties.animators,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = disappearActions ?: properties.disappearActions,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        functions = functions ?: properties.functions,
        height = height ?: properties.height,
        id = id ?: properties.id,
        isEnabled = isEnabled ?: properties.isEnabled,
        isOnVariable = isOnVariable ?: properties.isOnVariable,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        margins = margins ?: properties.margins,
        onColor = onColor ?: properties.onColor,
        paddings = paddings ?: properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
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
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param isOnVariable Name of the Boolean variable assigned to the toggle.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Switch.modify(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Property<Accessibility>? = null,
    alignmentHorizontal: Property<AlignmentHorizontal>? = null,
    alignmentVertical: Property<AlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    animators: Property<List<Animator>>? = null,
    background: Property<List<Background>>? = null,
    border: Property<Border>? = null,
    columnSpan: Property<Int>? = null,
    disappearActions: Property<List<DisappearAction>>? = null,
    extensions: Property<List<Extension>>? = null,
    focus: Property<Focus>? = null,
    functions: Property<List<Function>>? = null,
    height: Property<Size>? = null,
    id: Property<String>? = null,
    isEnabled: Property<Boolean>? = null,
    isOnVariable: Property<String>? = null,
    layoutProvider: Property<LayoutProvider>? = null,
    margins: Property<EdgeInsets>? = null,
    onColor: Property<Color>? = null,
    paddings: Property<EdgeInsets>? = null,
    reuseId: Property<String>? = null,
    rowSpan: Property<Int>? = null,
    selectedActions: Property<List<Action>>? = null,
    tooltips: Property<List<Tooltip>>? = null,
    transform: Property<Transform>? = null,
    transitionChange: Property<ChangeTransition>? = null,
    transitionIn: Property<AppearanceTransition>? = null,
    transitionOut: Property<AppearanceTransition>? = null,
    transitionTriggers: Property<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: Property<List<Trigger>>? = null,
    variables: Property<List<Variable>>? = null,
    visibility: Property<Visibility>? = null,
    visibilityAction: Property<VisibilityAction>? = null,
    visibilityActions: Property<List<VisibilityAction>>? = null,
    width: Property<Size>? = null,
): Switch = Switch(
    Switch.Properties(
        accessibility = accessibility ?: properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animators = animators ?: properties.animators,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = disappearActions ?: properties.disappearActions,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        functions = functions ?: properties.functions,
        height = height ?: properties.height,
        id = id ?: properties.id,
        isEnabled = isEnabled ?: properties.isEnabled,
        isOnVariable = isOnVariable ?: properties.isOnVariable,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        margins = margins ?: properties.margins,
        onColor = onColor ?: properties.onColor,
        paddings = paddings ?: properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
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
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Switch.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    isEnabled: ExpressionProperty<Boolean>? = null,
    onColor: ExpressionProperty<Color>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Switch = Switch(
    Switch.Properties(
        accessibility = properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animators = properties.animators,
        background = properties.background,
        border = properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = properties.disappearActions,
        extensions = properties.extensions,
        focus = properties.focus,
        functions = properties.functions,
        height = properties.height,
        id = properties.id,
        isEnabled = isEnabled ?: properties.isEnabled,
        isOnVariable = properties.isOnVariable,
        layoutProvider = properties.layoutProvider,
        margins = properties.margins,
        onColor = onColor ?: properties.onColor,
        paddings = properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = properties.selectedActions,
        tooltips = properties.tooltips,
        transform = properties.transform,
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
 * @param animators Declaration of animators that change variable values over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param isOnVariable Name of the Boolean variable assigned to the toggle.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Switch>.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    isEnabled: Boolean? = null,
    isOnVariable: String? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    onColor: Color? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
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
): Component<Switch> = Component(
    template = template,
    properties = Switch.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        animators = valueOrNull(animators),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        disappearActions = valueOrNull(disappearActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        id = valueOrNull(id),
        isEnabled = valueOrNull(isEnabled),
        isOnVariable = valueOrNull(isOnVariable),
        layoutProvider = valueOrNull(layoutProvider),
        margins = valueOrNull(margins),
        onColor = valueOrNull(onColor),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
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
 * @param animators Declaration of animators that change variable values over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param isOnVariable Name of the Boolean variable assigned to the toggle.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Switch>.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animators: ReferenceProperty<List<Animator>>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    isOnVariable: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    onColor: ReferenceProperty<Color>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
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
): Component<Switch> = Component(
    template = template,
    properties = Switch.Properties(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animators = animators,
        background = background,
        border = border,
        columnSpan = columnSpan,
        disappearActions = disappearActions,
        extensions = extensions,
        focus = focus,
        functions = functions,
        height = height,
        id = id,
        isEnabled = isEnabled,
        isOnVariable = isOnVariable,
        layoutProvider = layoutProvider,
        margins = margins,
        onColor = onColor,
        paddings = paddings,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        tooltips = tooltips,
        transform = transform,
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
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Switch>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    isEnabled: ExpressionProperty<Boolean>? = null,
    onColor: ExpressionProperty<Color>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Switch> = Component(
    template = template,
    properties = Switch.Properties(
        accessibility = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animators = null,
        background = null,
        border = null,
        columnSpan = columnSpan,
        disappearActions = null,
        extensions = null,
        focus = null,
        functions = null,
        height = null,
        id = null,
        isEnabled = isEnabled,
        isOnVariable = null,
        layoutProvider = null,
        margins = null,
        onColor = onColor,
        paddings = null,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = null,
        tooltips = null,
        transform = null,
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

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables the element's toggle functionality.
 * @param isOnVariable Name of the Boolean variable assigned to the toggle.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param onColor Color of the toggle in the enabled state. If the color is omitted:
: iOS standard system color is used
: the color specified in `Div2Context` on Android is used.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Switch>.modify(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Property<Accessibility>? = null,
    alignmentHorizontal: Property<AlignmentHorizontal>? = null,
    alignmentVertical: Property<AlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    animators: Property<List<Animator>>? = null,
    background: Property<List<Background>>? = null,
    border: Property<Border>? = null,
    columnSpan: Property<Int>? = null,
    disappearActions: Property<List<DisappearAction>>? = null,
    extensions: Property<List<Extension>>? = null,
    focus: Property<Focus>? = null,
    functions: Property<List<Function>>? = null,
    height: Property<Size>? = null,
    id: Property<String>? = null,
    isEnabled: Property<Boolean>? = null,
    isOnVariable: Property<String>? = null,
    layoutProvider: Property<LayoutProvider>? = null,
    margins: Property<EdgeInsets>? = null,
    onColor: Property<Color>? = null,
    paddings: Property<EdgeInsets>? = null,
    reuseId: Property<String>? = null,
    rowSpan: Property<Int>? = null,
    selectedActions: Property<List<Action>>? = null,
    tooltips: Property<List<Tooltip>>? = null,
    transform: Property<Transform>? = null,
    transitionChange: Property<ChangeTransition>? = null,
    transitionIn: Property<AppearanceTransition>? = null,
    transitionOut: Property<AppearanceTransition>? = null,
    transitionTriggers: Property<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: Property<List<Trigger>>? = null,
    variables: Property<List<Variable>>? = null,
    visibility: Property<Visibility>? = null,
    visibilityAction: Property<VisibilityAction>? = null,
    visibilityActions: Property<List<VisibilityAction>>? = null,
    width: Property<Size>? = null,
): Component<Switch> = Component(
    template = template,
    properties = Switch.Properties(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animators = animators,
        background = background,
        border = border,
        columnSpan = columnSpan,
        disappearActions = disappearActions,
        extensions = extensions,
        focus = focus,
        functions = functions,
        height = height,
        id = id,
        isEnabled = isEnabled,
        isOnVariable = isOnVariable,
        layoutProvider = layoutProvider,
        margins = margins,
        onColor = onColor,
        paddings = paddings,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        tooltips = tooltips,
        transform = transform,
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

@Generated
operator fun Component<Switch>.plus(additive: Switch.Properties): Component<Switch> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Switch.asList() = listOf(this)
