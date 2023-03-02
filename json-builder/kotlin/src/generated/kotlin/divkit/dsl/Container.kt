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
 * Container. It contains other elements and is responsible for their location. It is used to arrange elements vertically, horizontally, and with an overlay in a certain order, simulating the third dimension.
 * 
 * Can be created using the method [container].
 * 
 * Required properties: `type, items`.
 */
@Generated
class Container internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "container")
    )

    operator fun plus(additive: Properties): Container = Container(
        Properties(
            accessibility = additive.accessibility ?: properties.accessibility,
            action = additive.action ?: properties.action,
            actionAnimation = additive.actionAnimation ?: properties.actionAnimation,
            actions = additive.actions ?: properties.actions,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            aspect = additive.aspect ?: properties.aspect,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            contentAlignmentHorizontal = additive.contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
            contentAlignmentVertical = additive.contentAlignmentVertical ?: properties.contentAlignmentVertical,
            doubletapActions = additive.doubletapActions ?: properties.doubletapActions,
            extensions = additive.extensions ?: properties.extensions,
            focus = additive.focus ?: properties.focus,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
            items = additive.items ?: properties.items,
            layoutMode = additive.layoutMode ?: properties.layoutMode,
            lineSeparator = additive.lineSeparator ?: properties.lineSeparator,
            longtapActions = additive.longtapActions ?: properties.longtapActions,
            margins = additive.margins ?: properties.margins,
            orientation = additive.orientation ?: properties.orientation,
            paddings = additive.paddings ?: properties.paddings,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            separator = additive.separator ?: properties.separator,
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
         * Click animation. The web only supports the following values: `fade`, `scale`, and `set`.
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
         * Size with a fixed aspect ratio. It counts height from width and ignores `height` value. The web requires the `aspect-ratio` css property for work.
         */
        val aspect: Property<Aspect>?,
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
         * Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
         * Default value: `left`.
         */
        val contentAlignmentHorizontal: Property<AlignmentHorizontal>?,
        /**
         * Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
         * Default value: `top`.
         */
        val contentAlignmentVertical: Property<AlignmentVertical>?,
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
         * Nested elements.
         */
        val items: Property<List<Div>>?,
        /**
         * Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
         * Default value: `no_wrap`.
         */
        val layoutMode: Property<LayoutMode>?,
        /**
         * Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
         */
        val lineSeparator: Property<Separator>?,
        /**
         * Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
         */
        val longtapActions: Property<List<Action>>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
         * Default value: `vertical`.
         */
        val orientation: Property<Orientation>?,
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
         * Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
         */
        val separator: Property<Separator>?,
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
            result.tryPutProperty("aspect", aspect)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("content_alignment_horizontal", contentAlignmentHorizontal)
            result.tryPutProperty("content_alignment_vertical", contentAlignmentVertical)
            result.tryPutProperty("doubletap_actions", doubletapActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
            result.tryPutProperty("items", items)
            result.tryPutProperty("layout_mode", layoutMode)
            result.tryPutProperty("line_separator", lineSeparator)
            result.tryPutProperty("longtap_actions", longtapActions)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("orientation", orientation)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("separator", separator)
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
     * Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
     * 
     * Possible values: [no_wrap, wrap].
     */
    @Generated
    sealed interface LayoutMode

    fun LayoutMode.asList() = listOf(this)

    /**
     * Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
     * 
     * Possible values: [vertical, horizontal, overlap].
     */
    @Generated
    sealed interface Orientation

    fun Orientation.asList() = listOf(this)

    /**
     * Can be created using the method [containerSeparator].
     * 
     * Required properties: `style`.
     */
    @Generated
    class Separator internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Separator = Separator(
            Properties(
                showAtEnd = additive.showAtEnd ?: properties.showAtEnd,
                showAtStart = additive.showAtStart ?: properties.showAtStart,
                showBetween = additive.showBetween ?: properties.showBetween,
                style = additive.style ?: properties.style,
            )
        )

        class Properties internal constructor(
            /**
             * Enables displaying the separator after the last item.
             * Default value: `false`.
             */
            val showAtEnd: Property<Boolean>?,
            /**
             * Enables displaying the separator before the first item.
             * Default value: `false`.
             */
            val showAtStart: Property<Boolean>?,
            /**
             * Enables displaying the separator between items.
             * Default value: `true`.
             */
            val showBetween: Property<Boolean>?,
            /**
             * Separator style.
             */
            val style: Property<Drawable>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("show_at_end", showAtEnd)
                result.tryPutProperty("show_at_start", showAtStart)
                result.tryPutProperty("show_between", showBetween)
                result.tryPutProperty("style", style)
                return result
            }
        }
    }

}

/**
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Size with a fixed aspect ratio. It counts height from width and ignores `height` value. The web requires the `aspect-ratio` css property for work.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.container(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Div>,
    layoutMode: Container.LayoutMode? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    orientation: Container.Orientation? = null,
    paddings: EdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = Container(
    Container.Properties(
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        aspect = valueOrNull(aspect),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        items = valueOrNull(items),
        layoutMode = valueOrNull(layoutMode),
        lineSeparator = valueOrNull(lineSeparator),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        orientation = valueOrNull(orientation),
        paddings = valueOrNull(paddings),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        separator = valueOrNull(separator),
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
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Size with a fixed aspect ratio. It counts height from width and ignores `height` value. The web requires the `aspect-ratio` css property for work.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.containerProps(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Div>? = null,
    layoutMode: Container.LayoutMode? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    orientation: Container.Orientation? = null,
    paddings: EdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
) = Container.Properties(
    accessibility = valueOrNull(accessibility),
    action = valueOrNull(action),
    actionAnimation = valueOrNull(actionAnimation),
    actions = valueOrNull(actions),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    aspect = valueOrNull(aspect),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnSpan = valueOrNull(columnSpan),
    contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
    contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
    doubletapActions = valueOrNull(doubletapActions),
    extensions = valueOrNull(extensions),
    focus = valueOrNull(focus),
    height = valueOrNull(height),
    id = valueOrNull(id),
    items = valueOrNull(items),
    layoutMode = valueOrNull(layoutMode),
    lineSeparator = valueOrNull(lineSeparator),
    longtapActions = valueOrNull(longtapActions),
    margins = valueOrNull(margins),
    orientation = valueOrNull(orientation),
    paddings = valueOrNull(paddings),
    rowSpan = valueOrNull(rowSpan),
    selectedActions = valueOrNull(selectedActions),
    separator = valueOrNull(separator),
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
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Size with a fixed aspect ratio. It counts height from width and ignores `height` value. The web requires the `aspect-ratio` css property for work.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun TemplateScope.containerRefs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    layoutMode: ReferenceProperty<Container.LayoutMode>? = null,
    lineSeparator: ReferenceProperty<Container.Separator>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    orientation: ReferenceProperty<Container.Orientation>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    separator: ReferenceProperty<Container.Separator>? = null,
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
) = Container.Properties(
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    aspect = aspect,
    background = background,
    border = border,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    doubletapActions = doubletapActions,
    extensions = extensions,
    focus = focus,
    height = height,
    id = id,
    items = items,
    layoutMode = layoutMode,
    lineSeparator = lineSeparator,
    longtapActions = longtapActions,
    margins = margins,
    orientation = orientation,
    paddings = paddings,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    separator = separator,
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
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Size with a fixed aspect ratio. It counts height from width and ignores `height` value. The web requires the `aspect-ratio` css property for work.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun Container.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Div>? = null,
    layoutMode: Container.LayoutMode? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    orientation: Container.Orientation? = null,
    paddings: EdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = Container(
    Container.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        action = valueOrNull(action) ?: properties.action,
        actionAnimation = valueOrNull(actionAnimation) ?: properties.actionAnimation,
        actions = valueOrNull(actions) ?: properties.actions,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        aspect = valueOrNull(aspect) ?: properties.aspect,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal) ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical) ?: properties.contentAlignmentVertical,
        doubletapActions = valueOrNull(doubletapActions) ?: properties.doubletapActions,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        focus = valueOrNull(focus) ?: properties.focus,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
        items = valueOrNull(items) ?: properties.items,
        layoutMode = valueOrNull(layoutMode) ?: properties.layoutMode,
        lineSeparator = valueOrNull(lineSeparator) ?: properties.lineSeparator,
        longtapActions = valueOrNull(longtapActions) ?: properties.longtapActions,
        margins = valueOrNull(margins) ?: properties.margins,
        orientation = valueOrNull(orientation) ?: properties.orientation,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        separator = valueOrNull(separator) ?: properties.separator,
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
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Size with a fixed aspect ratio. It counts height from width and ignores `height` value. The web requires the `aspect-ratio` css property for work.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun Container.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    layoutMode: ReferenceProperty<Container.LayoutMode>? = null,
    lineSeparator: ReferenceProperty<Container.Separator>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    orientation: ReferenceProperty<Container.Orientation>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    separator: ReferenceProperty<Container.Separator>? = null,
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
): Container = Container(
    Container.Properties(
        accessibility = accessibility ?: properties.accessibility,
        action = action ?: properties.action,
        actionAnimation = actionAnimation ?: properties.actionAnimation,
        actions = actions ?: properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        aspect = aspect ?: properties.aspect,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        doubletapActions = doubletapActions ?: properties.doubletapActions,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        height = height ?: properties.height,
        id = id ?: properties.id,
        items = items ?: properties.items,
        layoutMode = layoutMode ?: properties.layoutMode,
        lineSeparator = lineSeparator ?: properties.lineSeparator,
        longtapActions = longtapActions ?: properties.longtapActions,
        margins = margins ?: properties.margins,
        orientation = orientation ?: properties.orientation,
        paddings = paddings ?: properties.paddings,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        separator = separator ?: properties.separator,
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
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 */
@Generated
fun Container.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
): Container = Container(
    Container.Properties(
        accessibility = properties.accessibility,
        action = properties.action,
        actionAnimation = properties.actionAnimation,
        actions = properties.actions,
        alignmentHorizontal = properties.alignmentHorizontal,
        alignmentVertical = properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        aspect = properties.aspect,
        background = properties.background,
        border = properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = properties.contentAlignmentHorizontal,
        contentAlignmentVertical = properties.contentAlignmentVertical,
        doubletapActions = properties.doubletapActions,
        extensions = properties.extensions,
        focus = properties.focus,
        height = properties.height,
        id = properties.id,
        items = properties.items,
        layoutMode = properties.layoutMode,
        lineSeparator = properties.lineSeparator,
        longtapActions = properties.longtapActions,
        margins = properties.margins,
        orientation = properties.orientation,
        paddings = properties.paddings,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = properties.selectedActions,
        separator = properties.separator,
        tooltips = properties.tooltips,
        transform = properties.transform,
        transitionChange = properties.transitionChange,
        transitionIn = properties.transitionIn,
        transitionOut = properties.transitionOut,
        transitionTriggers = properties.transitionTriggers,
        visibility = properties.visibility,
        visibilityAction = properties.visibilityAction,
        visibilityActions = properties.visibilityActions,
        width = properties.width,
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Size with a fixed aspect ratio. It counts height from width and ignores `height` value. The web requires the `aspect-ratio` css property for work.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun Component<Container>.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Div>? = null,
    layoutMode: Container.LayoutMode? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    orientation: Container.Orientation? = null,
    paddings: EdgeInsets? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Component<Container> = Component(
    template = template,
    properties = Container.Properties(
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        aspect = valueOrNull(aspect),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        items = valueOrNull(items),
        layoutMode = valueOrNull(layoutMode),
        lineSeparator = valueOrNull(lineSeparator),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        orientation = valueOrNull(orientation),
        paddings = valueOrNull(paddings),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        separator = valueOrNull(separator),
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
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Size with a fixed aspect ratio. It counts height from width and ignores `height` value. The web requires the `aspect-ratio` css property for work.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param paddings Internal margins from the element stroke.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun Component<Container>.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    layoutMode: ReferenceProperty<Container.LayoutMode>? = null,
    lineSeparator: ReferenceProperty<Container.Separator>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    orientation: ReferenceProperty<Container.Orientation>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    separator: ReferenceProperty<Container.Separator>? = null,
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
): Component<Container> = Component(
    template = template,
    properties = Container.Properties(
        accessibility = accessibility,
        action = action,
        actionAnimation = actionAnimation,
        actions = actions,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        aspect = aspect,
        background = background,
        border = border,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        doubletapActions = doubletapActions,
        extensions = extensions,
        focus = focus,
        height = height,
        id = id,
        items = items,
        layoutMode = layoutMode,
        lineSeparator = lineSeparator,
        longtapActions = longtapActions,
        margins = margins,
        orientation = orientation,
        paddings = paddings,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        separator = separator,
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
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 */
@Generated
fun Component<Container>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
): Component<Container> = Component(
    template = template,
    properties = Container.Properties(
        accessibility = null,
        action = null,
        actionAnimation = null,
        actions = null,
        alignmentHorizontal = null,
        alignmentVertical = null,
        alpha = alpha,
        aspect = null,
        background = null,
        border = null,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = null,
        contentAlignmentVertical = null,
        doubletapActions = null,
        extensions = null,
        focus = null,
        height = null,
        id = null,
        items = null,
        layoutMode = null,
        lineSeparator = null,
        longtapActions = null,
        margins = null,
        orientation = null,
        paddings = null,
        rowSpan = rowSpan,
        selectedActions = null,
        separator = null,
        tooltips = null,
        transform = null,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        visibility = null,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Container>.plus(additive: Container.Properties): Component<Container> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Container.asList() = listOf(this)

/**
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun DivScope.containerSeparator(
    `use named arguments`: Guard = Guard.instance,
    showAtEnd: Boolean? = null,
    showAtStart: Boolean? = null,
    showBetween: Boolean? = null,
    style: Drawable,
): Container.Separator = Container.Separator(
    Container.Separator.Properties(
        showAtEnd = valueOrNull(showAtEnd),
        showAtStart = valueOrNull(showAtStart),
        showBetween = valueOrNull(showBetween),
        style = valueOrNull(style),
    )
)

/**
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun DivScope.containerSeparatorProps(
    `use named arguments`: Guard = Guard.instance,
    showAtEnd: Boolean? = null,
    showAtStart: Boolean? = null,
    showBetween: Boolean? = null,
    style: Drawable? = null,
) = Container.Separator.Properties(
    showAtEnd = valueOrNull(showAtEnd),
    showAtStart = valueOrNull(showAtStart),
    showBetween = valueOrNull(showBetween),
    style = valueOrNull(style),
)

/**
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun TemplateScope.containerSeparatorRefs(
    `use named arguments`: Guard = Guard.instance,
    showAtEnd: ReferenceProperty<Boolean>? = null,
    showAtStart: ReferenceProperty<Boolean>? = null,
    showBetween: ReferenceProperty<Boolean>? = null,
    style: ReferenceProperty<Drawable>? = null,
) = Container.Separator.Properties(
    showAtEnd = showAtEnd,
    showAtStart = showAtStart,
    showBetween = showBetween,
    style = style,
)

/**
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun Container.Separator.override(
    `use named arguments`: Guard = Guard.instance,
    showAtEnd: Boolean? = null,
    showAtStart: Boolean? = null,
    showBetween: Boolean? = null,
    style: Drawable? = null,
): Container.Separator = Container.Separator(
    Container.Separator.Properties(
        showAtEnd = valueOrNull(showAtEnd) ?: properties.showAtEnd,
        showAtStart = valueOrNull(showAtStart) ?: properties.showAtStart,
        showBetween = valueOrNull(showBetween) ?: properties.showBetween,
        style = valueOrNull(style) ?: properties.style,
    )
)

/**
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun Container.Separator.defer(
    `use named arguments`: Guard = Guard.instance,
    showAtEnd: ReferenceProperty<Boolean>? = null,
    showAtStart: ReferenceProperty<Boolean>? = null,
    showBetween: ReferenceProperty<Boolean>? = null,
    style: ReferenceProperty<Drawable>? = null,
): Container.Separator = Container.Separator(
    Container.Separator.Properties(
        showAtEnd = showAtEnd ?: properties.showAtEnd,
        showAtStart = showAtStart ?: properties.showAtStart,
        showBetween = showBetween ?: properties.showBetween,
        style = style ?: properties.style,
    )
)

/**
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 */
@Generated
fun Container.Separator.evaluate(
    `use named arguments`: Guard = Guard.instance,
    showAtEnd: ExpressionProperty<Boolean>? = null,
    showAtStart: ExpressionProperty<Boolean>? = null,
    showBetween: ExpressionProperty<Boolean>? = null,
): Container.Separator = Container.Separator(
    Container.Separator.Properties(
        showAtEnd = showAtEnd ?: properties.showAtEnd,
        showAtStart = showAtStart ?: properties.showAtStart,
        showBetween = showBetween ?: properties.showBetween,
        style = properties.style,
    )
)

@Generated
fun Container.Separator.asList() = listOf(this)
