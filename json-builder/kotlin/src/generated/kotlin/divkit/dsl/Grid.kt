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
 * A grid with an option to merge cells vertically and horizontally.
 * 
 * Can be created using the method [grid].
 * 
 * Required parameters: `type, column_count`.
 */
@Generated
class Grid internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "grid")
    )

    operator fun plus(additive: Properties): Grid = Grid(
        Properties(
            accessibility = additive.accessibility ?: properties.accessibility,
            action = additive.action ?: properties.action,
            actionAnimation = additive.actionAnimation ?: properties.actionAnimation,
            actions = additive.actions ?: properties.actions,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            columnCount = additive.columnCount ?: properties.columnCount,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            contentAlignmentHorizontal = additive.contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
            contentAlignmentVertical = additive.contentAlignmentVertical ?: properties.contentAlignmentVertical,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            doubletapActions = additive.doubletapActions ?: properties.doubletapActions,
            extensions = additive.extensions ?: properties.extensions,
            focus = additive.focus ?: properties.focus,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
            items = additive.items ?: properties.items,
            longtapActions = additive.longtapActions ?: properties.longtapActions,
            margins = additive.margins ?: properties.margins,
            paddings = additive.paddings ?: properties.paddings,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            tooltips = additive.tooltips ?: properties.tooltips,
            transform = additive.transform ?: properties.transform,
            transitionChange = additive.transitionChange ?: properties.transitionChange,
            transitionIn = additive.transitionIn ?: properties.transitionIn,
            transitionOut = additive.transitionOut ?: properties.transitionOut,
            transitionTriggers = additive.transitionTriggers ?: properties.transitionTriggers,
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
         * One action when clicking on an element. Not used if the `actions` parameter is set.
         */
        val action: Property<Action>?,
        /**
         * Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
         * Default value: `{"name": "fade", "start_value": 1, "end_value": 0.6, "duration": 100 }`.
         */
        val actionAnimation: Property<Animation>?,
        /**
         * Multiple actions when clicking on an element.
         */
        val actions: Property<List<Action>>?,
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
         * Number of columns.
         */
        val columnCount: Property<Int>?,
        /**
         * Merges cells in a column of the [grid](div-grid.md) element.
         */
        val columnSpan: Property<Int>?,
        /**
         * Horizontal alignment of grid contents.
         * Default value: `start`.
         */
        val contentAlignmentHorizontal: Property<AlignmentHorizontal>?,
        /**
         * Vertical alignment of grid contents.
         * Default value: `top`.
         */
        val contentAlignmentVertical: Property<AlignmentVertical>?,
        /**
         * Actions when an element disappears from the screen.
         */
        val disappearActions: Property<List<DisappearAction>>?,
        /**
         * Action when double-clicking on an element.
         */
        val doubletapActions: Property<List<Action>>?,
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
         * Contents.
         */
        val items: Property<List<Div>>?,
        /**
         * Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
         */
        val longtapActions: Property<List<Action>>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
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
            result.tryPutProperty("action", action)
            result.tryPutProperty("action_animation", actionAnimation)
            result.tryPutProperty("actions", actions)
            result.tryPutProperty("alignment_horizontal", alignmentHorizontal)
            result.tryPutProperty("alignment_vertical", alignmentVertical)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("column_count", columnCount)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("content_alignment_horizontal", contentAlignmentHorizontal)
            result.tryPutProperty("content_alignment_vertical", contentAlignmentVertical)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("doubletap_actions", doubletapActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
            result.tryPutProperty("items", items)
            result.tryPutProperty("longtap_actions", longtapActions)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("tooltips", tooltips)
            result.tryPutProperty("transform", transform)
            result.tryPutProperty("transition_change", transitionChange)
            result.tryPutProperty("transition_in", transitionIn)
            result.tryPutProperty("transition_out", transitionOut)
            result.tryPutProperty("transition_triggers", transitionTriggers)
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
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal alignment of grid contents.
 * @param contentAlignmentVertical Vertical alignment of grid contents.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Contents.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.grid(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnCount: Int? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Div>? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Grid = Grid(
    Grid.Properties(
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnCount = valueOrNull(columnCount),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        items = valueOrNull(items),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        variables = valueOrNull(variables),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal alignment of grid contents.
 * @param contentAlignmentVertical Vertical alignment of grid contents.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Contents.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.gridProps(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnCount: Int? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Div>? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
) = Grid.Properties(
    accessibility = valueOrNull(accessibility),
    action = valueOrNull(action),
    actionAnimation = valueOrNull(actionAnimation),
    actions = valueOrNull(actions),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnCount = valueOrNull(columnCount),
    columnSpan = valueOrNull(columnSpan),
    contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
    contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
    disappearActions = valueOrNull(disappearActions),
    doubletapActions = valueOrNull(doubletapActions),
    extensions = valueOrNull(extensions),
    focus = valueOrNull(focus),
    height = valueOrNull(height),
    id = valueOrNull(id),
    items = valueOrNull(items),
    longtapActions = valueOrNull(longtapActions),
    margins = valueOrNull(margins),
    paddings = valueOrNull(paddings),
    rowSpan = valueOrNull(rowSpan),
    selectedActions = valueOrNull(selectedActions),
    tooltips = valueOrNull(tooltips),
    transform = valueOrNull(transform),
    transitionChange = valueOrNull(transitionChange),
    transitionIn = valueOrNull(transitionIn),
    transitionOut = valueOrNull(transitionOut),
    transitionTriggers = valueOrNull(transitionTriggers),
    variables = valueOrNull(variables),
    visibility = valueOrNull(visibility),
    visibilityAction = valueOrNull(visibilityAction),
    visibilityActions = valueOrNull(visibilityActions),
    width = valueOrNull(width),
)

/**
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal alignment of grid contents.
 * @param contentAlignmentVertical Vertical alignment of grid contents.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Contents.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun TemplateScope.gridRefs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnCount: ReferenceProperty<Int>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
) = Grid.Properties(
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    background = background,
    border = border,
    columnCount = columnCount,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    focus = focus,
    height = height,
    id = id,
    items = items,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    tooltips = tooltips,
    transform = transform,
    transitionChange = transitionChange,
    transitionIn = transitionIn,
    transitionOut = transitionOut,
    transitionTriggers = transitionTriggers,
    variables = variables,
    visibility = visibility,
    visibilityAction = visibilityAction,
    visibilityActions = visibilityActions,
    width = width,
)

/**
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal alignment of grid contents.
 * @param contentAlignmentVertical Vertical alignment of grid contents.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Contents.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Grid.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnCount: Int? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Div>? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Grid = Grid(
    Grid.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        action = valueOrNull(action) ?: properties.action,
        actionAnimation = valueOrNull(actionAnimation) ?: properties.actionAnimation,
        actions = valueOrNull(actions) ?: properties.actions,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnCount = valueOrNull(columnCount) ?: properties.columnCount,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal) ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical) ?: properties.contentAlignmentVertical,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        doubletapActions = valueOrNull(doubletapActions) ?: properties.doubletapActions,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        focus = valueOrNull(focus) ?: properties.focus,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
        items = valueOrNull(items) ?: properties.items,
        longtapActions = valueOrNull(longtapActions) ?: properties.longtapActions,
        margins = valueOrNull(margins) ?: properties.margins,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        tooltips = valueOrNull(tooltips) ?: properties.tooltips,
        transform = valueOrNull(transform) ?: properties.transform,
        transitionChange = valueOrNull(transitionChange) ?: properties.transitionChange,
        transitionIn = valueOrNull(transitionIn) ?: properties.transitionIn,
        transitionOut = valueOrNull(transitionOut) ?: properties.transitionOut,
        transitionTriggers = valueOrNull(transitionTriggers) ?: properties.transitionTriggers,
        variables = valueOrNull(variables) ?: properties.variables,
        visibility = valueOrNull(visibility) ?: properties.visibility,
        visibilityAction = valueOrNull(visibilityAction) ?: properties.visibilityAction,
        visibilityActions = valueOrNull(visibilityActions) ?: properties.visibilityActions,
        width = valueOrNull(width) ?: properties.width,
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal alignment of grid contents.
 * @param contentAlignmentVertical Vertical alignment of grid contents.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Contents.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Grid.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnCount: ReferenceProperty<Int>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Grid = Grid(
    Grid.Properties(
        accessibility = accessibility ?: properties.accessibility,
        action = action ?: properties.action,
        actionAnimation = actionAnimation ?: properties.actionAnimation,
        actions = actions ?: properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnCount = columnCount ?: properties.columnCount,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        disappearActions = disappearActions ?: properties.disappearActions,
        doubletapActions = doubletapActions ?: properties.doubletapActions,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        height = height ?: properties.height,
        id = id ?: properties.id,
        items = items ?: properties.items,
        longtapActions = longtapActions ?: properties.longtapActions,
        margins = margins ?: properties.margins,
        paddings = paddings ?: properties.paddings,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
        transitionChange = transitionChange ?: properties.transitionChange,
        transitionIn = transitionIn ?: properties.transitionIn,
        transitionOut = transitionOut ?: properties.transitionOut,
        transitionTriggers = transitionTriggers ?: properties.transitionTriggers,
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
 * @param columnCount Number of columns.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal alignment of grid contents.
 * @param contentAlignmentVertical Vertical alignment of grid contents.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Grid.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnCount: ExpressionProperty<Int>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    contentAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Grid = Grid(
    Grid.Properties(
        accessibility = properties.accessibility,
        action = properties.action,
        actionAnimation = properties.actionAnimation,
        actions = properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = properties.background,
        border = properties.border,
        columnCount = columnCount ?: properties.columnCount,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        disappearActions = properties.disappearActions,
        doubletapActions = properties.doubletapActions,
        extensions = properties.extensions,
        focus = properties.focus,
        height = properties.height,
        id = properties.id,
        items = properties.items,
        longtapActions = properties.longtapActions,
        margins = properties.margins,
        paddings = properties.paddings,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = properties.selectedActions,
        tooltips = properties.tooltips,
        transform = properties.transform,
        transitionChange = properties.transitionChange,
        transitionIn = properties.transitionIn,
        transitionOut = properties.transitionOut,
        transitionTriggers = properties.transitionTriggers,
        variables = properties.variables,
        visibility = visibility ?: properties.visibility,
        visibilityAction = properties.visibilityAction,
        visibilityActions = properties.visibilityActions,
        width = properties.width,
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal alignment of grid contents.
 * @param contentAlignmentVertical Vertical alignment of grid contents.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Contents.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Grid>.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnCount: Int? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Div>? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Component<Grid> = Component(
    template = template,
    properties = Grid.Properties(
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnCount = valueOrNull(columnCount),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        items = valueOrNull(items),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        variables = valueOrNull(variables),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    ).mergeWith(properties)
)

/**
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal alignment of grid contents.
 * @param contentAlignmentVertical Vertical alignment of grid contents.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Contents.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Grid>.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnCount: ReferenceProperty<Int>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Component<Grid> = Component(
    template = template,
    properties = Grid.Properties(
        accessibility = accessibility,
        action = action,
        actionAnimation = actionAnimation,
        actions = actions,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = background,
        border = border,
        columnCount = columnCount,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        disappearActions = disappearActions,
        doubletapActions = doubletapActions,
        extensions = extensions,
        focus = focus,
        height = height,
        id = id,
        items = items,
        longtapActions = longtapActions,
        margins = margins,
        paddings = paddings,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        tooltips = tooltips,
        transform = transform,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
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
 * @param columnCount Number of columns.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal alignment of grid contents.
 * @param contentAlignmentVertical Vertical alignment of grid contents.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Grid>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnCount: ExpressionProperty<Int>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    contentAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Grid> = Component(
    template = template,
    properties = Grid.Properties(
        accessibility = null,
        action = null,
        actionAnimation = null,
        actions = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = null,
        border = null,
        columnCount = columnCount,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        disappearActions = null,
        doubletapActions = null,
        extensions = null,
        focus = null,
        height = null,
        id = null,
        items = null,
        longtapActions = null,
        margins = null,
        paddings = null,
        rowSpan = rowSpan,
        selectedActions = null,
        tooltips = null,
        transform = null,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        variables = null,
        visibility = visibility,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Grid>.plus(additive: Grid.Properties): Component<Grid> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Grid.asList() = listOf(this)
