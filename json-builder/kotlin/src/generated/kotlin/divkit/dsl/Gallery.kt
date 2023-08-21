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
 * Gallery. It contains a horizontal or vertical set of cards that can be scrolled.
 * 
 * Can be created using the method [gallery].
 * 
 * Required parameters: `type, items`.
 */
@Generated
class Gallery internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "gallery")
    )

    operator fun plus(additive: Properties): Gallery = Gallery(
        Properties(
            accessibility = additive.accessibility ?: properties.accessibility,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            columnCount = additive.columnCount ?: properties.columnCount,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            crossContentAlignment = additive.crossContentAlignment ?: properties.crossContentAlignment,
            crossSpacing = additive.crossSpacing ?: properties.crossSpacing,
            defaultItem = additive.defaultItem ?: properties.defaultItem,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            extensions = additive.extensions ?: properties.extensions,
            focus = additive.focus ?: properties.focus,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
            itemSpacing = additive.itemSpacing ?: properties.itemSpacing,
            items = additive.items ?: properties.items,
            margins = additive.margins ?: properties.margins,
            orientation = additive.orientation ?: properties.orientation,
            paddings = additive.paddings ?: properties.paddings,
            restrictParentScroll = additive.restrictParentScroll ?: properties.restrictParentScroll,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            scrollMode = additive.scrollMode ?: properties.scrollMode,
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
         * Number of columns for block layout.
         */
        val columnCount: Property<Int>?,
        /**
         * Merges cells in a column of the [grid](div-grid.md) element.
         */
        val columnSpan: Property<Int>?,
        /**
         * Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
         * Default value: `start`.
         */
        val crossContentAlignment: Property<CrossContentAlignment>?,
        /**
         * Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
         */
        val crossSpacing: Property<Int>?,
        /**
         * Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
         * Default value: `0`.
         */
        val defaultItem: Property<Int>?,
        /**
         * Actions when an element disappears from the screen.
         */
        val disappearActions: Property<List<DisappearAction>>?,
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
         * Spacing between elements.
         * Default value: `8`.
         */
        val itemSpacing: Property<Int>?,
        /**
         * Gallery elements. Scrolling to elements can be implemented using:<li>`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
         */
        val items: Property<List<Div>>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Gallery orientation.
         * Default value: `horizontal`.
         */
        val orientation: Property<Orientation>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
        /**
         * If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
         * Default value: `false`.
         */
        val restrictParentScroll: Property<Boolean>?,
        /**
         * Merges cells in a string of the [grid](div-grid.md) element.
         */
        val rowSpan: Property<Int>?,
        /**
         * Scroll type: `default` — continuous, `paging` — page-by-page.
         * Default value: `default`.
         */
        val scrollMode: Property<ScrollMode>?,
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
            result.tryPutProperty("alignment_horizontal", alignmentHorizontal)
            result.tryPutProperty("alignment_vertical", alignmentVertical)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("column_count", columnCount)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("cross_content_alignment", crossContentAlignment)
            result.tryPutProperty("cross_spacing", crossSpacing)
            result.tryPutProperty("default_item", defaultItem)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
            result.tryPutProperty("item_spacing", itemSpacing)
            result.tryPutProperty("items", items)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("orientation", orientation)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("restrict_parent_scroll", restrictParentScroll)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("scroll_mode", scrollMode)
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
     * Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
     * 
     * Possible values: [start, center, end].
     */
    @Generated
    sealed interface CrossContentAlignment

    /**
     * Gallery orientation.
     * 
     * Possible values: [horizontal, vertical].
     */
    @Generated
    sealed interface Orientation

    /**
     * Scroll type: `default` — continuous, `paging` — page-by-page.
     * 
     * Possible values: [paging, default].
     */
    @Generated
    sealed interface ScrollMode
}

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns for block layout.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param crossContentAlignment Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
 * @param crossSpacing Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
 * @param defaultItem Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemSpacing Spacing between elements.
 * @param items Gallery elements. Scrolling to elements can be implemented using:<li>`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param orientation Gallery orientation.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scrollMode Scroll type: `default` — continuous, `paging` — page-by-page.
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
fun DivScope.gallery(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnCount: Int? = null,
    columnSpan: Int? = null,
    crossContentAlignment: Gallery.CrossContentAlignment? = null,
    crossSpacing: Int? = null,
    defaultItem: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    itemSpacing: Int? = null,
    items: List<Div>? = null,
    margins: EdgeInsets? = null,
    orientation: Gallery.Orientation? = null,
    paddings: EdgeInsets? = null,
    restrictParentScroll: Boolean? = null,
    rowSpan: Int? = null,
    scrollMode: Gallery.ScrollMode? = null,
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
): Gallery = Gallery(
    Gallery.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnCount = valueOrNull(columnCount),
        columnSpan = valueOrNull(columnSpan),
        crossContentAlignment = valueOrNull(crossContentAlignment),
        crossSpacing = valueOrNull(crossSpacing),
        defaultItem = valueOrNull(defaultItem),
        disappearActions = valueOrNull(disappearActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        itemSpacing = valueOrNull(itemSpacing),
        items = valueOrNull(items),
        margins = valueOrNull(margins),
        orientation = valueOrNull(orientation),
        paddings = valueOrNull(paddings),
        restrictParentScroll = valueOrNull(restrictParentScroll),
        rowSpan = valueOrNull(rowSpan),
        scrollMode = valueOrNull(scrollMode),
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
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns for block layout.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param crossContentAlignment Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
 * @param crossSpacing Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
 * @param defaultItem Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemSpacing Spacing between elements.
 * @param items Gallery elements. Scrolling to elements can be implemented using:<li>`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param orientation Gallery orientation.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scrollMode Scroll type: `default` — continuous, `paging` — page-by-page.
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
fun DivScope.galleryProps(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnCount: Int? = null,
    columnSpan: Int? = null,
    crossContentAlignment: Gallery.CrossContentAlignment? = null,
    crossSpacing: Int? = null,
    defaultItem: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    itemSpacing: Int? = null,
    items: List<Div>? = null,
    margins: EdgeInsets? = null,
    orientation: Gallery.Orientation? = null,
    paddings: EdgeInsets? = null,
    restrictParentScroll: Boolean? = null,
    rowSpan: Int? = null,
    scrollMode: Gallery.ScrollMode? = null,
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
) = Gallery.Properties(
    accessibility = valueOrNull(accessibility),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnCount = valueOrNull(columnCount),
    columnSpan = valueOrNull(columnSpan),
    crossContentAlignment = valueOrNull(crossContentAlignment),
    crossSpacing = valueOrNull(crossSpacing),
    defaultItem = valueOrNull(defaultItem),
    disappearActions = valueOrNull(disappearActions),
    extensions = valueOrNull(extensions),
    focus = valueOrNull(focus),
    height = valueOrNull(height),
    id = valueOrNull(id),
    itemSpacing = valueOrNull(itemSpacing),
    items = valueOrNull(items),
    margins = valueOrNull(margins),
    orientation = valueOrNull(orientation),
    paddings = valueOrNull(paddings),
    restrictParentScroll = valueOrNull(restrictParentScroll),
    rowSpan = valueOrNull(rowSpan),
    scrollMode = valueOrNull(scrollMode),
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
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns for block layout.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param crossContentAlignment Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
 * @param crossSpacing Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
 * @param defaultItem Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemSpacing Spacing between elements.
 * @param items Gallery elements. Scrolling to elements can be implemented using:<li>`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param orientation Gallery orientation.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scrollMode Scroll type: `default` — continuous, `paging` — page-by-page.
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
fun TemplateScope.galleryRefs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnCount: ReferenceProperty<Int>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    crossContentAlignment: ReferenceProperty<Gallery.CrossContentAlignment>? = null,
    crossSpacing: ReferenceProperty<Int>? = null,
    defaultItem: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    itemSpacing: ReferenceProperty<Int>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    orientation: ReferenceProperty<Gallery.Orientation>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    restrictParentScroll: ReferenceProperty<Boolean>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    scrollMode: ReferenceProperty<Gallery.ScrollMode>? = null,
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
) = Gallery.Properties(
    accessibility = accessibility,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    background = background,
    border = border,
    columnCount = columnCount,
    columnSpan = columnSpan,
    crossContentAlignment = crossContentAlignment,
    crossSpacing = crossSpacing,
    defaultItem = defaultItem,
    disappearActions = disappearActions,
    extensions = extensions,
    focus = focus,
    height = height,
    id = id,
    itemSpacing = itemSpacing,
    items = items,
    margins = margins,
    orientation = orientation,
    paddings = paddings,
    restrictParentScroll = restrictParentScroll,
    rowSpan = rowSpan,
    scrollMode = scrollMode,
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
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns for block layout.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param crossContentAlignment Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
 * @param crossSpacing Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
 * @param defaultItem Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemSpacing Spacing between elements.
 * @param items Gallery elements. Scrolling to elements can be implemented using:<li>`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param orientation Gallery orientation.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scrollMode Scroll type: `default` — continuous, `paging` — page-by-page.
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
fun Gallery.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnCount: Int? = null,
    columnSpan: Int? = null,
    crossContentAlignment: Gallery.CrossContentAlignment? = null,
    crossSpacing: Int? = null,
    defaultItem: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    itemSpacing: Int? = null,
    items: List<Div>? = null,
    margins: EdgeInsets? = null,
    orientation: Gallery.Orientation? = null,
    paddings: EdgeInsets? = null,
    restrictParentScroll: Boolean? = null,
    rowSpan: Int? = null,
    scrollMode: Gallery.ScrollMode? = null,
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
): Gallery = Gallery(
    Gallery.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnCount = valueOrNull(columnCount) ?: properties.columnCount,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        crossContentAlignment = valueOrNull(crossContentAlignment) ?: properties.crossContentAlignment,
        crossSpacing = valueOrNull(crossSpacing) ?: properties.crossSpacing,
        defaultItem = valueOrNull(defaultItem) ?: properties.defaultItem,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        focus = valueOrNull(focus) ?: properties.focus,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
        itemSpacing = valueOrNull(itemSpacing) ?: properties.itemSpacing,
        items = valueOrNull(items) ?: properties.items,
        margins = valueOrNull(margins) ?: properties.margins,
        orientation = valueOrNull(orientation) ?: properties.orientation,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        restrictParentScroll = valueOrNull(restrictParentScroll) ?: properties.restrictParentScroll,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        scrollMode = valueOrNull(scrollMode) ?: properties.scrollMode,
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
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns for block layout.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param crossContentAlignment Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
 * @param crossSpacing Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
 * @param defaultItem Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemSpacing Spacing between elements.
 * @param items Gallery elements. Scrolling to elements can be implemented using:<li>`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param orientation Gallery orientation.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scrollMode Scroll type: `default` — continuous, `paging` — page-by-page.
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
fun Gallery.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnCount: ReferenceProperty<Int>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    crossContentAlignment: ReferenceProperty<Gallery.CrossContentAlignment>? = null,
    crossSpacing: ReferenceProperty<Int>? = null,
    defaultItem: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    itemSpacing: ReferenceProperty<Int>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    orientation: ReferenceProperty<Gallery.Orientation>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    restrictParentScroll: ReferenceProperty<Boolean>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    scrollMode: ReferenceProperty<Gallery.ScrollMode>? = null,
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
): Gallery = Gallery(
    Gallery.Properties(
        accessibility = accessibility ?: properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnCount = columnCount ?: properties.columnCount,
        columnSpan = columnSpan ?: properties.columnSpan,
        crossContentAlignment = crossContentAlignment ?: properties.crossContentAlignment,
        crossSpacing = crossSpacing ?: properties.crossSpacing,
        defaultItem = defaultItem ?: properties.defaultItem,
        disappearActions = disappearActions ?: properties.disappearActions,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        height = height ?: properties.height,
        id = id ?: properties.id,
        itemSpacing = itemSpacing ?: properties.itemSpacing,
        items = items ?: properties.items,
        margins = margins ?: properties.margins,
        orientation = orientation ?: properties.orientation,
        paddings = paddings ?: properties.paddings,
        restrictParentScroll = restrictParentScroll ?: properties.restrictParentScroll,
        rowSpan = rowSpan ?: properties.rowSpan,
        scrollMode = scrollMode ?: properties.scrollMode,
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
 * @param columnCount Number of columns for block layout.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param crossContentAlignment Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
 * @param crossSpacing Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
 * @param defaultItem Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
 * @param itemSpacing Spacing between elements.
 * @param orientation Gallery orientation.
 * @param restrictParentScroll If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scrollMode Scroll type: `default` — continuous, `paging` — page-by-page.
 * @param visibility Element visibility.
 */
@Generated
fun Gallery.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnCount: ExpressionProperty<Int>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    crossContentAlignment: ExpressionProperty<Gallery.CrossContentAlignment>? = null,
    crossSpacing: ExpressionProperty<Int>? = null,
    defaultItem: ExpressionProperty<Int>? = null,
    itemSpacing: ExpressionProperty<Int>? = null,
    orientation: ExpressionProperty<Gallery.Orientation>? = null,
    restrictParentScroll: ExpressionProperty<Boolean>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    scrollMode: ExpressionProperty<Gallery.ScrollMode>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Gallery = Gallery(
    Gallery.Properties(
        accessibility = properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = properties.background,
        border = properties.border,
        columnCount = columnCount ?: properties.columnCount,
        columnSpan = columnSpan ?: properties.columnSpan,
        crossContentAlignment = crossContentAlignment ?: properties.crossContentAlignment,
        crossSpacing = crossSpacing ?: properties.crossSpacing,
        defaultItem = defaultItem ?: properties.defaultItem,
        disappearActions = properties.disappearActions,
        extensions = properties.extensions,
        focus = properties.focus,
        height = properties.height,
        id = properties.id,
        itemSpacing = itemSpacing ?: properties.itemSpacing,
        items = properties.items,
        margins = properties.margins,
        orientation = orientation ?: properties.orientation,
        paddings = properties.paddings,
        restrictParentScroll = restrictParentScroll ?: properties.restrictParentScroll,
        rowSpan = rowSpan ?: properties.rowSpan,
        scrollMode = scrollMode ?: properties.scrollMode,
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
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns for block layout.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param crossContentAlignment Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
 * @param crossSpacing Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
 * @param defaultItem Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemSpacing Spacing between elements.
 * @param items Gallery elements. Scrolling to elements can be implemented using:<li>`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param orientation Gallery orientation.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scrollMode Scroll type: `default` — continuous, `paging` — page-by-page.
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
fun Component<Gallery>.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnCount: Int? = null,
    columnSpan: Int? = null,
    crossContentAlignment: Gallery.CrossContentAlignment? = null,
    crossSpacing: Int? = null,
    defaultItem: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    itemSpacing: Int? = null,
    items: List<Div>? = null,
    margins: EdgeInsets? = null,
    orientation: Gallery.Orientation? = null,
    paddings: EdgeInsets? = null,
    restrictParentScroll: Boolean? = null,
    rowSpan: Int? = null,
    scrollMode: Gallery.ScrollMode? = null,
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
): Component<Gallery> = Component(
    template = template,
    properties = Gallery.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnCount = valueOrNull(columnCount),
        columnSpan = valueOrNull(columnSpan),
        crossContentAlignment = valueOrNull(crossContentAlignment),
        crossSpacing = valueOrNull(crossSpacing),
        defaultItem = valueOrNull(defaultItem),
        disappearActions = valueOrNull(disappearActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        itemSpacing = valueOrNull(itemSpacing),
        items = valueOrNull(items),
        margins = valueOrNull(margins),
        orientation = valueOrNull(orientation),
        paddings = valueOrNull(paddings),
        restrictParentScroll = valueOrNull(restrictParentScroll),
        rowSpan = valueOrNull(rowSpan),
        scrollMode = valueOrNull(scrollMode),
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
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnCount Number of columns for block layout.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param crossContentAlignment Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
 * @param crossSpacing Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
 * @param defaultItem Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemSpacing Spacing between elements.
 * @param items Gallery elements. Scrolling to elements can be implemented using:<li>`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param orientation Gallery orientation.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scrollMode Scroll type: `default` — continuous, `paging` — page-by-page.
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
fun Component<Gallery>.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnCount: ReferenceProperty<Int>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    crossContentAlignment: ReferenceProperty<Gallery.CrossContentAlignment>? = null,
    crossSpacing: ReferenceProperty<Int>? = null,
    defaultItem: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    itemSpacing: ReferenceProperty<Int>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    orientation: ReferenceProperty<Gallery.Orientation>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    restrictParentScroll: ReferenceProperty<Boolean>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    scrollMode: ReferenceProperty<Gallery.ScrollMode>? = null,
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
): Component<Gallery> = Component(
    template = template,
    properties = Gallery.Properties(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = background,
        border = border,
        columnCount = columnCount,
        columnSpan = columnSpan,
        crossContentAlignment = crossContentAlignment,
        crossSpacing = crossSpacing,
        defaultItem = defaultItem,
        disappearActions = disappearActions,
        extensions = extensions,
        focus = focus,
        height = height,
        id = id,
        itemSpacing = itemSpacing,
        items = items,
        margins = margins,
        orientation = orientation,
        paddings = paddings,
        restrictParentScroll = restrictParentScroll,
        rowSpan = rowSpan,
        scrollMode = scrollMode,
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
 * @param columnCount Number of columns for block layout.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param crossContentAlignment Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>
 * @param crossSpacing Spacing between elements across the scroll axis. By default, the value set to `item_spacing`.
 * @param defaultItem Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>
 * @param itemSpacing Spacing between elements.
 * @param orientation Gallery orientation.
 * @param restrictParentScroll If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scrollMode Scroll type: `default` — continuous, `paging` — page-by-page.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Gallery>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnCount: ExpressionProperty<Int>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    crossContentAlignment: ExpressionProperty<Gallery.CrossContentAlignment>? = null,
    crossSpacing: ExpressionProperty<Int>? = null,
    defaultItem: ExpressionProperty<Int>? = null,
    itemSpacing: ExpressionProperty<Int>? = null,
    orientation: ExpressionProperty<Gallery.Orientation>? = null,
    restrictParentScroll: ExpressionProperty<Boolean>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    scrollMode: ExpressionProperty<Gallery.ScrollMode>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Gallery> = Component(
    template = template,
    properties = Gallery.Properties(
        accessibility = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = null,
        border = null,
        columnCount = columnCount,
        columnSpan = columnSpan,
        crossContentAlignment = crossContentAlignment,
        crossSpacing = crossSpacing,
        defaultItem = defaultItem,
        disappearActions = null,
        extensions = null,
        focus = null,
        height = null,
        id = null,
        itemSpacing = itemSpacing,
        items = null,
        margins = null,
        orientation = orientation,
        paddings = null,
        restrictParentScroll = restrictParentScroll,
        rowSpan = rowSpan,
        scrollMode = scrollMode,
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
operator fun Component<Gallery>.plus(additive: Gallery.Properties): Component<Gallery> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Gallery.asList() = listOf(this)

@Generated
fun Gallery.CrossContentAlignment.asList() = listOf(this)

@Generated
fun Gallery.Orientation.asList() = listOf(this)

@Generated
fun Gallery.ScrollMode.asList() = listOf(this)
