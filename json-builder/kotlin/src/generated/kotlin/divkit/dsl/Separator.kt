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
 * A separating line between elements.
 * 
 * Can be created using the method [separator].
 * 
 * Required parameters: `type`.
 */
@Generated
class Separator internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "separator")
    )

    operator fun plus(additive: Properties): Separator = Separator(
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
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            delimiterStyle = additive.delimiterStyle ?: properties.delimiterStyle,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            doubletapActions = additive.doubletapActions ?: properties.doubletapActions,
            extensions = additive.extensions ?: properties.extensions,
            focus = additive.focus ?: properties.focus,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
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
         * Merges cells in a column of the [grid](div-grid.md) element.
         */
        val columnSpan: Property<Int>?,
        /**
         * Separator display settings.
         */
        val delimiterStyle: Property<DelimiterStyle>?,
        /**
         * Actions when an element disappears from the screen.
         */
        val disappearActions: Property<List<DisappearAction>>?,
        /**
         * Action when double-clicking on an element.
         */
        val doubletapActions: Property<List<Action>>?,
        /**
         * Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
         */
        val extensions: Property<List<Extension>>?,
        /**
         * Parameters when focusing on an element or losing focus.
         */
        val focus: Property<Focus>?,
        /**
         * Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
         * Default value: `{"type": "wrap_content"}`.
         */
        val height: Property<Size>?,
        /**
         * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
         */
        val id: Property<String>?,
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
         * Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
         */
        val transitionIn: Property<AppearanceTransition>?,
        /**
         * Disappearance animation. It is played when an element disappears in the new layout.
         */
        val transitionOut: Property<AppearanceTransition>?,
        /**
         * Animation starting triggers. Default value: `[state_change, visibility_change]`.
         */
        val transitionTriggers: Property<List<TransitionTrigger>>?,
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
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("delimiter_style", delimiterStyle)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("doubletap_actions", doubletapActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
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
            result.tryPutProperty("visibility", visibility)
            result.tryPutProperty("visibility_action", visibilityAction)
            result.tryPutProperty("visibility_actions", visibilityActions)
            result.tryPutProperty("width", width)
            return result
        }
    }

    /**
     * Separator display settings.
     * 
     * Can be created using the method [separatorDelimiterStyle].
     */
    @Generated
    class DelimiterStyle internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): DelimiterStyle = DelimiterStyle(
            Properties(
                color = additive.color ?: properties.color,
                orientation = additive.orientation ?: properties.orientation,
            )
        )

        class Properties internal constructor(
            /**
             * Separator color. To prevent the separator from being displayed, set transparency in the alpha channel. For example, `#00FFFFFF`.
             * Default value: `#14000000`.
             */
            val color: Property<Color>?,
            /**
             * Separator orientation:<li>`vertical` — vertical;</li><li>`horizontal` — horizontal.</li><
             * Default value: `horizontal`.
             */
            val orientation: Property<Orientation>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("color", color)
                result.tryPutProperty("orientation", orientation)
                return result
            }
        }

        /**
         * Separator orientation:<li>`vertical` — vertical;</li><li>`horizontal` — horizontal.</li><
         * 
         * Possible values: [vertical, horizontal].
         */
        @Generated
        sealed interface Orientation
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param delimiterStyle Separator display settings.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.separator(
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
    columnSpan: Int? = null,
    delimiterStyle: Separator.DelimiterStyle? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
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
    transitionTriggers: List<TransitionTrigger>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Separator = Separator(
    Separator.Properties(
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        delimiterStyle = valueOrNull(delimiterStyle),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param delimiterStyle Separator display settings.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.separatorProps(
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
    columnSpan: Int? = null,
    delimiterStyle: Separator.DelimiterStyle? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
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
    transitionTriggers: List<TransitionTrigger>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
) = Separator.Properties(
    accessibility = valueOrNull(accessibility),
    action = valueOrNull(action),
    actionAnimation = valueOrNull(actionAnimation),
    actions = valueOrNull(actions),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnSpan = valueOrNull(columnSpan),
    delimiterStyle = valueOrNull(delimiterStyle),
    disappearActions = valueOrNull(disappearActions),
    doubletapActions = valueOrNull(doubletapActions),
    extensions = valueOrNull(extensions),
    focus = valueOrNull(focus),
    height = valueOrNull(height),
    id = valueOrNull(id),
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param delimiterStyle Separator display settings.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun TemplateScope.separatorRefs(
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
    columnSpan: ReferenceProperty<Int>? = null,
    delimiterStyle: ReferenceProperty<Separator.DelimiterStyle>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
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
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
) = Separator.Properties(
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
    disappearActions = disappearActions,
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
    transform = transform,
    transitionChange = transitionChange,
    transitionIn = transitionIn,
    transitionOut = transitionOut,
    transitionTriggers = transitionTriggers,
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param delimiterStyle Separator display settings.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Separator.override(
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
    columnSpan: Int? = null,
    delimiterStyle: Separator.DelimiterStyle? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
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
    transitionTriggers: List<TransitionTrigger>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Separator = Separator(
    Separator.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        action = valueOrNull(action) ?: properties.action,
        actionAnimation = valueOrNull(actionAnimation) ?: properties.actionAnimation,
        actions = valueOrNull(actions) ?: properties.actions,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        delimiterStyle = valueOrNull(delimiterStyle) ?: properties.delimiterStyle,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        doubletapActions = valueOrNull(doubletapActions) ?: properties.doubletapActions,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        focus = valueOrNull(focus) ?: properties.focus,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param delimiterStyle Separator display settings.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Separator.defer(
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
    columnSpan: ReferenceProperty<Int>? = null,
    delimiterStyle: ReferenceProperty<Separator.DelimiterStyle>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
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
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Separator = Separator(
    Separator.Properties(
        accessibility = accessibility ?: properties.accessibility,
        action = action ?: properties.action,
        actionAnimation = actionAnimation ?: properties.actionAnimation,
        actions = actions ?: properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        delimiterStyle = delimiterStyle ?: properties.delimiterStyle,
        disappearActions = disappearActions ?: properties.disappearActions,
        doubletapActions = doubletapActions ?: properties.doubletapActions,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        height = height ?: properties.height,
        id = id ?: properties.id,
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
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Separator.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Separator = Separator(
    Separator.Properties(
        accessibility = properties.accessibility,
        action = properties.action,
        actionAnimation = properties.actionAnimation,
        actions = properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = properties.background,
        border = properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        delimiterStyle = properties.delimiterStyle,
        disappearActions = properties.disappearActions,
        doubletapActions = properties.doubletapActions,
        extensions = properties.extensions,
        focus = properties.focus,
        height = properties.height,
        id = properties.id,
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param delimiterStyle Separator display settings.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Separator>.override(
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
    columnSpan: Int? = null,
    delimiterStyle: Separator.DelimiterStyle? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
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
    transitionTriggers: List<TransitionTrigger>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Component<Separator> = Component(
    template = template,
    properties = Separator.Properties(
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        delimiterStyle = valueOrNull(delimiterStyle),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param delimiterStyle Separator display settings.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Separator>.defer(
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
    columnSpan: ReferenceProperty<Int>? = null,
    delimiterStyle: ReferenceProperty<Separator.DelimiterStyle>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
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
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Component<Separator> = Component(
    template = template,
    properties = Separator.Properties(
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
        disappearActions = disappearActions,
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
        transform = transform,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
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
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Separator>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Separator> = Component(
    template = template,
    properties = Separator.Properties(
        accessibility = null,
        action = null,
        actionAnimation = null,
        actions = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = null,
        border = null,
        columnSpan = columnSpan,
        delimiterStyle = null,
        disappearActions = null,
        doubletapActions = null,
        extensions = null,
        focus = null,
        height = null,
        id = null,
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
        visibility = visibility,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Separator>.plus(additive: Separator.Properties): Component<Separator> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Separator.asList() = listOf(this)

/**
 * @param color Separator color. To prevent the separator from being displayed, set transparency in the alpha channel. For example, `#00FFFFFF`.
 * @param orientation Separator orientation:<li>`vertical` — vertical;</li><li>`horizontal` — horizontal.</li><
 */
@Generated
fun DivScope.separatorDelimiterStyle(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    orientation: Separator.DelimiterStyle.Orientation? = null,
): Separator.DelimiterStyle = Separator.DelimiterStyle(
    Separator.DelimiterStyle.Properties(
        color = valueOrNull(color),
        orientation = valueOrNull(orientation),
    )
)

/**
 * @param color Separator color. To prevent the separator from being displayed, set transparency in the alpha channel. For example, `#00FFFFFF`.
 * @param orientation Separator orientation:<li>`vertical` — vertical;</li><li>`horizontal` — horizontal.</li><
 */
@Generated
fun DivScope.separatorDelimiterStyleProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    orientation: Separator.DelimiterStyle.Orientation? = null,
) = Separator.DelimiterStyle.Properties(
    color = valueOrNull(color),
    orientation = valueOrNull(orientation),
)

/**
 * @param color Separator color. To prevent the separator from being displayed, set transparency in the alpha channel. For example, `#00FFFFFF`.
 * @param orientation Separator orientation:<li>`vertical` — vertical;</li><li>`horizontal` — horizontal.</li><
 */
@Generated
fun TemplateScope.separatorDelimiterStyleRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    orientation: ReferenceProperty<Separator.DelimiterStyle.Orientation>? = null,
) = Separator.DelimiterStyle.Properties(
    color = color,
    orientation = orientation,
)

/**
 * @param color Separator color. To prevent the separator from being displayed, set transparency in the alpha channel. For example, `#00FFFFFF`.
 * @param orientation Separator orientation:<li>`vertical` — vertical;</li><li>`horizontal` — horizontal.</li><
 */
@Generated
fun Separator.DelimiterStyle.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    orientation: Separator.DelimiterStyle.Orientation? = null,
): Separator.DelimiterStyle = Separator.DelimiterStyle(
    Separator.DelimiterStyle.Properties(
        color = valueOrNull(color) ?: properties.color,
        orientation = valueOrNull(orientation) ?: properties.orientation,
    )
)

/**
 * @param color Separator color. To prevent the separator from being displayed, set transparency in the alpha channel. For example, `#00FFFFFF`.
 * @param orientation Separator orientation:<li>`vertical` — vertical;</li><li>`horizontal` — horizontal.</li><
 */
@Generated
fun Separator.DelimiterStyle.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    orientation: ReferenceProperty<Separator.DelimiterStyle.Orientation>? = null,
): Separator.DelimiterStyle = Separator.DelimiterStyle(
    Separator.DelimiterStyle.Properties(
        color = color ?: properties.color,
        orientation = orientation ?: properties.orientation,
    )
)

/**
 * @param color Separator color. To prevent the separator from being displayed, set transparency in the alpha channel. For example, `#00FFFFFF`.
 * @param orientation Separator orientation:<li>`vertical` — vertical;</li><li>`horizontal` — horizontal.</li><
 */
@Generated
fun Separator.DelimiterStyle.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
    orientation: ExpressionProperty<Separator.DelimiterStyle.Orientation>? = null,
): Separator.DelimiterStyle = Separator.DelimiterStyle(
    Separator.DelimiterStyle.Properties(
        color = color ?: properties.color,
        orientation = orientation ?: properties.orientation,
    )
)

@Generated
fun Separator.DelimiterStyle.asList() = listOf(this)
