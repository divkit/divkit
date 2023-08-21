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
 * Tabs. Height of the first tab is determined by its contents, and height of the remaining [depends on the platform](../../location.dita#tabs).
 * 
 * Can be created using the method [tabs].
 * 
 * Required parameters: `type, items`.
 */
@Generated
class Tabs internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "tabs")
    )

    operator fun plus(additive: Properties): Tabs = Tabs(
        Properties(
            accessibility = additive.accessibility ?: properties.accessibility,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            dynamicHeight = additive.dynamicHeight ?: properties.dynamicHeight,
            extensions = additive.extensions ?: properties.extensions,
            focus = additive.focus ?: properties.focus,
            hasSeparator = additive.hasSeparator ?: properties.hasSeparator,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
            items = additive.items ?: properties.items,
            margins = additive.margins ?: properties.margins,
            paddings = additive.paddings ?: properties.paddings,
            restrictParentScroll = additive.restrictParentScroll ?: properties.restrictParentScroll,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            selectedTab = additive.selectedTab ?: properties.selectedTab,
            separatorColor = additive.separatorColor ?: properties.separatorColor,
            separatorPaddings = additive.separatorPaddings ?: properties.separatorPaddings,
            switchTabsByContentSwipeEnabled = additive.switchTabsByContentSwipeEnabled ?: properties.switchTabsByContentSwipeEnabled,
            tabTitleStyle = additive.tabTitleStyle ?: properties.tabTitleStyle,
            titlePaddings = additive.titlePaddings ?: properties.titlePaddings,
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
         * Merges cells in a column of the [grid](div-grid.md) element.
         */
        val columnSpan: Property<Int>?,
        /**
         * Actions when an element disappears from the screen.
         */
        val disappearActions: Property<List<DisappearAction>>?,
        /**
         * Updating height when changing the active element. In the browser, the value is always `true`.
         * Default value: `false`.
         */
        val dynamicHeight: Property<Boolean>?,
        /**
         * Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
         */
        val extensions: Property<List<Extension>>?,
        /**
         * Parameters when focusing on an element or losing focus.
         */
        val focus: Property<Focus>?,
        /**
         * A separating line between tabs and contents.
         * Default value: `false`.
         */
        val hasSeparator: Property<Boolean>?,
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
         * Tabs. Transition between tabs can be implemented using:<li>`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — go to the previous tab inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
         */
        val items: Property<List<Item>>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
        /**
         * If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
         * Default value: `false`.
         */
        val restrictParentScroll: Property<Boolean>?,
        /**
         * Merges cells in a string of the [grid](div-grid.md) element.
         */
        val rowSpan: Property<Int>?,
        /**
         * List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
         */
        val selectedActions: Property<List<Action>>?,
        /**
         * Ordinal number of the tab that will be opened by default.
         * Default value: `0`.
         */
        val selectedTab: Property<Int>?,
        /**
         * Separator color.
         * Default value: `#14000000`.
         */
        val separatorColor: Property<Color>?,
        /**
         * Indents from the separating line. Not used if `has_separator = false`.
         * Default value: `{"left": 12,"right": 12,"top": 0,"bottom": 0}`.
         */
        val separatorPaddings: Property<EdgeInsets>?,
        /**
         * Switching tabs by scrolling through the contents.
         * Default value: `true`.
         */
        val switchTabsByContentSwipeEnabled: Property<Boolean>?,
        /**
         * Design style of tab titles.
         */
        val tabTitleStyle: Property<TabTitleStyle>?,
        /**
         * Indents in the tab name.
         * Default value: `{"left": 12,"right": 12,"top": 0,"bottom": 8}`.
         */
        val titlePaddings: Property<EdgeInsets>?,
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
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("dynamic_height", dynamicHeight)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("has_separator", hasSeparator)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
            result.tryPutProperty("items", items)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("restrict_parent_scroll", restrictParentScroll)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("selected_tab", selectedTab)
            result.tryPutProperty("separator_color", separatorColor)
            result.tryPutProperty("separator_paddings", separatorPaddings)
            result.tryPutProperty("switch_tabs_by_content_swipe_enabled", switchTabsByContentSwipeEnabled)
            result.tryPutProperty("tab_title_style", tabTitleStyle)
            result.tryPutProperty("title_paddings", titlePaddings)
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
     * Tab.
     * 
     * Can be created using the method [tabsItem].
     * 
     * Required parameters: `title, div`.
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
                div = additive.div ?: properties.div,
                title = additive.title ?: properties.title,
                titleClickAction = additive.titleClickAction ?: properties.titleClickAction,
            )
        )

        class Properties internal constructor(
            /**
             * Tab contents.
             */
            val div: Property<Div>?,
            /**
             * Tab title.
             */
            val title: Property<String>?,
            /**
             * Action when clicking on the active tab title.
             */
            val titleClickAction: Property<Action>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("div", div)
                result.tryPutProperty("title", title)
                result.tryPutProperty("title_click_action", titleClickAction)
                return result
            }
        }
    }


    /**
     * Design style of tab titles.
     * 
     * Can be created using the method [tabsTabTitleStyle].
     */
    @Generated
    class TabTitleStyle internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): TabTitleStyle = TabTitleStyle(
            Properties(
                activeBackgroundColor = additive.activeBackgroundColor ?: properties.activeBackgroundColor,
                activeFontWeight = additive.activeFontWeight ?: properties.activeFontWeight,
                activeTextColor = additive.activeTextColor ?: properties.activeTextColor,
                animationDuration = additive.animationDuration ?: properties.animationDuration,
                animationType = additive.animationType ?: properties.animationType,
                cornerRadius = additive.cornerRadius ?: properties.cornerRadius,
                cornersRadius = additive.cornersRadius ?: properties.cornersRadius,
                fontFamily = additive.fontFamily ?: properties.fontFamily,
                fontSize = additive.fontSize ?: properties.fontSize,
                fontSizeUnit = additive.fontSizeUnit ?: properties.fontSizeUnit,
                fontWeight = additive.fontWeight ?: properties.fontWeight,
                inactiveBackgroundColor = additive.inactiveBackgroundColor ?: properties.inactiveBackgroundColor,
                inactiveFontWeight = additive.inactiveFontWeight ?: properties.inactiveFontWeight,
                inactiveTextColor = additive.inactiveTextColor ?: properties.inactiveTextColor,
                itemSpacing = additive.itemSpacing ?: properties.itemSpacing,
                letterSpacing = additive.letterSpacing ?: properties.letterSpacing,
                lineHeight = additive.lineHeight ?: properties.lineHeight,
                paddings = additive.paddings ?: properties.paddings,
            )
        )

        class Properties internal constructor(
            /**
             * Background color of the active tab title.
             * Default value: `#FFFFDC60`.
             */
            val activeBackgroundColor: Property<Color>?,
            /**
             * Active tab title style.
             */
            val activeFontWeight: Property<FontWeight>?,
            /**
             * Color of the active tab title text.
             * Default value: `#CC000000`.
             */
            val activeTextColor: Property<Color>?,
            /**
             * Duration of active title change animation.
             * Default value: `300`.
             */
            val animationDuration: Property<Int>?,
            /**
             * Active title change animation.
             * Default value: `slide`.
             */
            val animationType: Property<AnimationType>?,
            /**
             * Title corner rounding radius. If the parameter isn't specified, the rounding is maximum (half of the smallest size). Not used if the `corners_radius` parameter is set.
             */
            val cornerRadius: Property<Int>?,
            /**
             * Rounding radii of corners of multiple titles. Empty values are replaced by `corner_radius`.
             */
            val cornersRadius: Property<CornersRadius>?,
            /**
             * Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
             */
            val fontFamily: Property<String>?,
            /**
             * Title font size.
             * Default value: `12`.
             */
            val fontSize: Property<Int>?,
            /**
             * Units of title font size measurement.
             * Default value: `sp`.
             */
            val fontSizeUnit: Property<SizeUnit>?,
            /**
             * Style. Use `active_font_weight` and `inactive_font_weight` instead.
             * Default value: `regular`.
             */
            @Deprecated("Marked as deprecated in the JSON schema ")
            val fontWeight: Property<FontWeight>?,
            /**
             * Background color of the inactive tab title.
             */
            val inactiveBackgroundColor: Property<Color>?,
            /**
             * Inactive tab title style.
             */
            val inactiveFontWeight: Property<FontWeight>?,
            /**
             * Color of the inactive tab title text.
             * Default value: `#80000000`.
             */
            val inactiveTextColor: Property<Color>?,
            /**
             * Spacing between neighbouring tab titles.
             * Default value: `0`.
             */
            val itemSpacing: Property<Int>?,
            /**
             * Spacing between title characters.
             * Default value: `0`.
             */
            val letterSpacing: Property<Double>?,
            /**
             * Line spacing of the text.
             */
            val lineHeight: Property<Int>?,
            /**
             * Indents around the tab title.
             * Default value: `{"left": 8,"right": 8,"top": 6,"bottom": 6}`.
             */
            val paddings: Property<EdgeInsets>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("active_background_color", activeBackgroundColor)
                result.tryPutProperty("active_font_weight", activeFontWeight)
                result.tryPutProperty("active_text_color", activeTextColor)
                result.tryPutProperty("animation_duration", animationDuration)
                result.tryPutProperty("animation_type", animationType)
                result.tryPutProperty("corner_radius", cornerRadius)
                result.tryPutProperty("corners_radius", cornersRadius)
                result.tryPutProperty("font_family", fontFamily)
                result.tryPutProperty("font_size", fontSize)
                result.tryPutProperty("font_size_unit", fontSizeUnit)
                result.tryPutProperty("font_weight", fontWeight)
                result.tryPutProperty("inactive_background_color", inactiveBackgroundColor)
                result.tryPutProperty("inactive_font_weight", inactiveFontWeight)
                result.tryPutProperty("inactive_text_color", inactiveTextColor)
                result.tryPutProperty("item_spacing", itemSpacing)
                result.tryPutProperty("letter_spacing", letterSpacing)
                result.tryPutProperty("line_height", lineHeight)
                result.tryPutProperty("paddings", paddings)
                return result
            }
        }

        /**
         * Active title change animation.
         * 
         * Possible values: [slide, fade, none].
         */
        @Generated
        sealed interface AnimationType
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
 * @param disappearActions Actions when an element disappears from the screen.
 * @param dynamicHeight Updating height when changing the active element. In the browser, the value is always `true`.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param hasSeparator A separating line between tabs and contents.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Tabs. Transition between tabs can be implemented using:<li>`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — go to the previous tab inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param selectedTab Ordinal number of the tab that will be opened by default.
 * @param separatorColor Separator color.
 * @param separatorPaddings Indents from the separating line. Not used if `has_separator = false`.
 * @param switchTabsByContentSwipeEnabled Switching tabs by scrolling through the contents.
 * @param tabTitleStyle Design style of tab titles.
 * @param titlePaddings Indents in the tab name.
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
fun DivScope.tabs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    dynamicHeight: Boolean? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    hasSeparator: Boolean? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Tabs.Item>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    restrictParentScroll: Boolean? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    selectedTab: Int? = null,
    separatorColor: Color? = null,
    separatorPaddings: EdgeInsets? = null,
    switchTabsByContentSwipeEnabled: Boolean? = null,
    tabTitleStyle: Tabs.TabTitleStyle? = null,
    titlePaddings: EdgeInsets? = null,
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
): Tabs = Tabs(
    Tabs.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        disappearActions = valueOrNull(disappearActions),
        dynamicHeight = valueOrNull(dynamicHeight),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        hasSeparator = valueOrNull(hasSeparator),
        height = valueOrNull(height),
        id = valueOrNull(id),
        items = valueOrNull(items),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        restrictParentScroll = valueOrNull(restrictParentScroll),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        selectedTab = valueOrNull(selectedTab),
        separatorColor = valueOrNull(separatorColor),
        separatorPaddings = valueOrNull(separatorPaddings),
        switchTabsByContentSwipeEnabled = valueOrNull(switchTabsByContentSwipeEnabled),
        tabTitleStyle = valueOrNull(tabTitleStyle),
        titlePaddings = valueOrNull(titlePaddings),
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param dynamicHeight Updating height when changing the active element. In the browser, the value is always `true`.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param hasSeparator A separating line between tabs and contents.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Tabs. Transition between tabs can be implemented using:<li>`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — go to the previous tab inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param selectedTab Ordinal number of the tab that will be opened by default.
 * @param separatorColor Separator color.
 * @param separatorPaddings Indents from the separating line. Not used if `has_separator = false`.
 * @param switchTabsByContentSwipeEnabled Switching tabs by scrolling through the contents.
 * @param tabTitleStyle Design style of tab titles.
 * @param titlePaddings Indents in the tab name.
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
fun DivScope.tabsProps(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    dynamicHeight: Boolean? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    hasSeparator: Boolean? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Tabs.Item>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    restrictParentScroll: Boolean? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    selectedTab: Int? = null,
    separatorColor: Color? = null,
    separatorPaddings: EdgeInsets? = null,
    switchTabsByContentSwipeEnabled: Boolean? = null,
    tabTitleStyle: Tabs.TabTitleStyle? = null,
    titlePaddings: EdgeInsets? = null,
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
) = Tabs.Properties(
    accessibility = valueOrNull(accessibility),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnSpan = valueOrNull(columnSpan),
    disappearActions = valueOrNull(disappearActions),
    dynamicHeight = valueOrNull(dynamicHeight),
    extensions = valueOrNull(extensions),
    focus = valueOrNull(focus),
    hasSeparator = valueOrNull(hasSeparator),
    height = valueOrNull(height),
    id = valueOrNull(id),
    items = valueOrNull(items),
    margins = valueOrNull(margins),
    paddings = valueOrNull(paddings),
    restrictParentScroll = valueOrNull(restrictParentScroll),
    rowSpan = valueOrNull(rowSpan),
    selectedActions = valueOrNull(selectedActions),
    selectedTab = valueOrNull(selectedTab),
    separatorColor = valueOrNull(separatorColor),
    separatorPaddings = valueOrNull(separatorPaddings),
    switchTabsByContentSwipeEnabled = valueOrNull(switchTabsByContentSwipeEnabled),
    tabTitleStyle = valueOrNull(tabTitleStyle),
    titlePaddings = valueOrNull(titlePaddings),
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param dynamicHeight Updating height when changing the active element. In the browser, the value is always `true`.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param hasSeparator A separating line between tabs and contents.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Tabs. Transition between tabs can be implemented using:<li>`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — go to the previous tab inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param selectedTab Ordinal number of the tab that will be opened by default.
 * @param separatorColor Separator color.
 * @param separatorPaddings Indents from the separating line. Not used if `has_separator = false`.
 * @param switchTabsByContentSwipeEnabled Switching tabs by scrolling through the contents.
 * @param tabTitleStyle Design style of tab titles.
 * @param titlePaddings Indents in the tab name.
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
fun TemplateScope.tabsRefs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    dynamicHeight: ReferenceProperty<Boolean>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    hasSeparator: ReferenceProperty<Boolean>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Tabs.Item>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    restrictParentScroll: ReferenceProperty<Boolean>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    selectedTab: ReferenceProperty<Int>? = null,
    separatorColor: ReferenceProperty<Color>? = null,
    separatorPaddings: ReferenceProperty<EdgeInsets>? = null,
    switchTabsByContentSwipeEnabled: ReferenceProperty<Boolean>? = null,
    tabTitleStyle: ReferenceProperty<Tabs.TabTitleStyle>? = null,
    titlePaddings: ReferenceProperty<EdgeInsets>? = null,
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
) = Tabs.Properties(
    accessibility = accessibility,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    background = background,
    border = border,
    columnSpan = columnSpan,
    disappearActions = disappearActions,
    dynamicHeight = dynamicHeight,
    extensions = extensions,
    focus = focus,
    hasSeparator = hasSeparator,
    height = height,
    id = id,
    items = items,
    margins = margins,
    paddings = paddings,
    restrictParentScroll = restrictParentScroll,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    selectedTab = selectedTab,
    separatorColor = separatorColor,
    separatorPaddings = separatorPaddings,
    switchTabsByContentSwipeEnabled = switchTabsByContentSwipeEnabled,
    tabTitleStyle = tabTitleStyle,
    titlePaddings = titlePaddings,
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param dynamicHeight Updating height when changing the active element. In the browser, the value is always `true`.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param hasSeparator A separating line between tabs and contents.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Tabs. Transition between tabs can be implemented using:<li>`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — go to the previous tab inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param selectedTab Ordinal number of the tab that will be opened by default.
 * @param separatorColor Separator color.
 * @param separatorPaddings Indents from the separating line. Not used if `has_separator = false`.
 * @param switchTabsByContentSwipeEnabled Switching tabs by scrolling through the contents.
 * @param tabTitleStyle Design style of tab titles.
 * @param titlePaddings Indents in the tab name.
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
fun Tabs.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    dynamicHeight: Boolean? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    hasSeparator: Boolean? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Tabs.Item>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    restrictParentScroll: Boolean? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    selectedTab: Int? = null,
    separatorColor: Color? = null,
    separatorPaddings: EdgeInsets? = null,
    switchTabsByContentSwipeEnabled: Boolean? = null,
    tabTitleStyle: Tabs.TabTitleStyle? = null,
    titlePaddings: EdgeInsets? = null,
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
): Tabs = Tabs(
    Tabs.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        dynamicHeight = valueOrNull(dynamicHeight) ?: properties.dynamicHeight,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        focus = valueOrNull(focus) ?: properties.focus,
        hasSeparator = valueOrNull(hasSeparator) ?: properties.hasSeparator,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
        items = valueOrNull(items) ?: properties.items,
        margins = valueOrNull(margins) ?: properties.margins,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        restrictParentScroll = valueOrNull(restrictParentScroll) ?: properties.restrictParentScroll,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        selectedTab = valueOrNull(selectedTab) ?: properties.selectedTab,
        separatorColor = valueOrNull(separatorColor) ?: properties.separatorColor,
        separatorPaddings = valueOrNull(separatorPaddings) ?: properties.separatorPaddings,
        switchTabsByContentSwipeEnabled = valueOrNull(switchTabsByContentSwipeEnabled) ?: properties.switchTabsByContentSwipeEnabled,
        tabTitleStyle = valueOrNull(tabTitleStyle) ?: properties.tabTitleStyle,
        titlePaddings = valueOrNull(titlePaddings) ?: properties.titlePaddings,
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param dynamicHeight Updating height when changing the active element. In the browser, the value is always `true`.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param hasSeparator A separating line between tabs and contents.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Tabs. Transition between tabs can be implemented using:<li>`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — go to the previous tab inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param selectedTab Ordinal number of the tab that will be opened by default.
 * @param separatorColor Separator color.
 * @param separatorPaddings Indents from the separating line. Not used if `has_separator = false`.
 * @param switchTabsByContentSwipeEnabled Switching tabs by scrolling through the contents.
 * @param tabTitleStyle Design style of tab titles.
 * @param titlePaddings Indents in the tab name.
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
fun Tabs.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    dynamicHeight: ReferenceProperty<Boolean>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    hasSeparator: ReferenceProperty<Boolean>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Tabs.Item>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    restrictParentScroll: ReferenceProperty<Boolean>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    selectedTab: ReferenceProperty<Int>? = null,
    separatorColor: ReferenceProperty<Color>? = null,
    separatorPaddings: ReferenceProperty<EdgeInsets>? = null,
    switchTabsByContentSwipeEnabled: ReferenceProperty<Boolean>? = null,
    tabTitleStyle: ReferenceProperty<Tabs.TabTitleStyle>? = null,
    titlePaddings: ReferenceProperty<EdgeInsets>? = null,
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
): Tabs = Tabs(
    Tabs.Properties(
        accessibility = accessibility ?: properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = disappearActions ?: properties.disappearActions,
        dynamicHeight = dynamicHeight ?: properties.dynamicHeight,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        hasSeparator = hasSeparator ?: properties.hasSeparator,
        height = height ?: properties.height,
        id = id ?: properties.id,
        items = items ?: properties.items,
        margins = margins ?: properties.margins,
        paddings = paddings ?: properties.paddings,
        restrictParentScroll = restrictParentScroll ?: properties.restrictParentScroll,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        selectedTab = selectedTab ?: properties.selectedTab,
        separatorColor = separatorColor ?: properties.separatorColor,
        separatorPaddings = separatorPaddings ?: properties.separatorPaddings,
        switchTabsByContentSwipeEnabled = switchTabsByContentSwipeEnabled ?: properties.switchTabsByContentSwipeEnabled,
        tabTitleStyle = tabTitleStyle ?: properties.tabTitleStyle,
        titlePaddings = titlePaddings ?: properties.titlePaddings,
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
 * @param dynamicHeight Updating height when changing the active element. In the browser, the value is always `true`.
 * @param hasSeparator A separating line between tabs and contents.
 * @param restrictParentScroll If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedTab Ordinal number of the tab that will be opened by default.
 * @param separatorColor Separator color.
 * @param switchTabsByContentSwipeEnabled Switching tabs by scrolling through the contents.
 * @param visibility Element visibility.
 */
@Generated
fun Tabs.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    dynamicHeight: ExpressionProperty<Boolean>? = null,
    hasSeparator: ExpressionProperty<Boolean>? = null,
    restrictParentScroll: ExpressionProperty<Boolean>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    selectedTab: ExpressionProperty<Int>? = null,
    separatorColor: ExpressionProperty<Color>? = null,
    switchTabsByContentSwipeEnabled: ExpressionProperty<Boolean>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Tabs = Tabs(
    Tabs.Properties(
        accessibility = properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        background = properties.background,
        border = properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = properties.disappearActions,
        dynamicHeight = dynamicHeight ?: properties.dynamicHeight,
        extensions = properties.extensions,
        focus = properties.focus,
        hasSeparator = hasSeparator ?: properties.hasSeparator,
        height = properties.height,
        id = properties.id,
        items = properties.items,
        margins = properties.margins,
        paddings = properties.paddings,
        restrictParentScroll = restrictParentScroll ?: properties.restrictParentScroll,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = properties.selectedActions,
        selectedTab = selectedTab ?: properties.selectedTab,
        separatorColor = separatorColor ?: properties.separatorColor,
        separatorPaddings = properties.separatorPaddings,
        switchTabsByContentSwipeEnabled = switchTabsByContentSwipeEnabled ?: properties.switchTabsByContentSwipeEnabled,
        tabTitleStyle = properties.tabTitleStyle,
        titlePaddings = properties.titlePaddings,
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param dynamicHeight Updating height when changing the active element. In the browser, the value is always `true`.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param hasSeparator A separating line between tabs and contents.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Tabs. Transition between tabs can be implemented using:<li>`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — go to the previous tab inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param selectedTab Ordinal number of the tab that will be opened by default.
 * @param separatorColor Separator color.
 * @param separatorPaddings Indents from the separating line. Not used if `has_separator = false`.
 * @param switchTabsByContentSwipeEnabled Switching tabs by scrolling through the contents.
 * @param tabTitleStyle Design style of tab titles.
 * @param titlePaddings Indents in the tab name.
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
fun Component<Tabs>.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    dynamicHeight: Boolean? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    hasSeparator: Boolean? = null,
    height: Size? = null,
    id: String? = null,
    items: List<Tabs.Item>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    restrictParentScroll: Boolean? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    selectedTab: Int? = null,
    separatorColor: Color? = null,
    separatorPaddings: EdgeInsets? = null,
    switchTabsByContentSwipeEnabled: Boolean? = null,
    tabTitleStyle: Tabs.TabTitleStyle? = null,
    titlePaddings: EdgeInsets? = null,
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
): Component<Tabs> = Component(
    template = template,
    properties = Tabs.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        disappearActions = valueOrNull(disappearActions),
        dynamicHeight = valueOrNull(dynamicHeight),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        hasSeparator = valueOrNull(hasSeparator),
        height = valueOrNull(height),
        id = valueOrNull(id),
        items = valueOrNull(items),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        restrictParentScroll = valueOrNull(restrictParentScroll),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        selectedTab = valueOrNull(selectedTab),
        separatorColor = valueOrNull(separatorColor),
        separatorPaddings = valueOrNull(separatorPaddings),
        switchTabsByContentSwipeEnabled = valueOrNull(switchTabsByContentSwipeEnabled),
        tabTitleStyle = valueOrNull(tabTitleStyle),
        titlePaddings = valueOrNull(titlePaddings),
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
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param dynamicHeight Updating height when changing the active element. In the browser, the value is always `true`.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param hasSeparator A separating line between tabs and contents.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param items Tabs. Transition between tabs can be implemented using:<li>`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — go to the previous tab inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param restrictParentScroll If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param selectedTab Ordinal number of the tab that will be opened by default.
 * @param separatorColor Separator color.
 * @param separatorPaddings Indents from the separating line. Not used if `has_separator = false`.
 * @param switchTabsByContentSwipeEnabled Switching tabs by scrolling through the contents.
 * @param tabTitleStyle Design style of tab titles.
 * @param titlePaddings Indents in the tab name.
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
fun Component<Tabs>.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    dynamicHeight: ReferenceProperty<Boolean>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    hasSeparator: ReferenceProperty<Boolean>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Tabs.Item>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    restrictParentScroll: ReferenceProperty<Boolean>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    selectedTab: ReferenceProperty<Int>? = null,
    separatorColor: ReferenceProperty<Color>? = null,
    separatorPaddings: ReferenceProperty<EdgeInsets>? = null,
    switchTabsByContentSwipeEnabled: ReferenceProperty<Boolean>? = null,
    tabTitleStyle: ReferenceProperty<Tabs.TabTitleStyle>? = null,
    titlePaddings: ReferenceProperty<EdgeInsets>? = null,
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
): Component<Tabs> = Component(
    template = template,
    properties = Tabs.Properties(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = background,
        border = border,
        columnSpan = columnSpan,
        disappearActions = disappearActions,
        dynamicHeight = dynamicHeight,
        extensions = extensions,
        focus = focus,
        hasSeparator = hasSeparator,
        height = height,
        id = id,
        items = items,
        margins = margins,
        paddings = paddings,
        restrictParentScroll = restrictParentScroll,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        selectedTab = selectedTab,
        separatorColor = separatorColor,
        separatorPaddings = separatorPaddings,
        switchTabsByContentSwipeEnabled = switchTabsByContentSwipeEnabled,
        tabTitleStyle = tabTitleStyle,
        titlePaddings = titlePaddings,
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
 * @param dynamicHeight Updating height when changing the active element. In the browser, the value is always `true`.
 * @param hasSeparator A separating line between tabs and contents.
 * @param restrictParentScroll If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedTab Ordinal number of the tab that will be opened by default.
 * @param separatorColor Separator color.
 * @param switchTabsByContentSwipeEnabled Switching tabs by scrolling through the contents.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Tabs>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    dynamicHeight: ExpressionProperty<Boolean>? = null,
    hasSeparator: ExpressionProperty<Boolean>? = null,
    restrictParentScroll: ExpressionProperty<Boolean>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    selectedTab: ExpressionProperty<Int>? = null,
    separatorColor: ExpressionProperty<Color>? = null,
    switchTabsByContentSwipeEnabled: ExpressionProperty<Boolean>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Tabs> = Component(
    template = template,
    properties = Tabs.Properties(
        accessibility = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        background = null,
        border = null,
        columnSpan = columnSpan,
        disappearActions = null,
        dynamicHeight = dynamicHeight,
        extensions = null,
        focus = null,
        hasSeparator = hasSeparator,
        height = null,
        id = null,
        items = null,
        margins = null,
        paddings = null,
        restrictParentScroll = restrictParentScroll,
        rowSpan = rowSpan,
        selectedActions = null,
        selectedTab = selectedTab,
        separatorColor = separatorColor,
        separatorPaddings = null,
        switchTabsByContentSwipeEnabled = switchTabsByContentSwipeEnabled,
        tabTitleStyle = null,
        titlePaddings = null,
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
operator fun Component<Tabs>.plus(additive: Tabs.Properties): Component<Tabs> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Tabs.asList() = listOf(this)

/**
 * @param div Tab contents.
 * @param title Tab title.
 * @param titleClickAction Action when clicking on the active tab title.
 */
@Generated
fun DivScope.tabsItem(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    title: String? = null,
    titleClickAction: Action? = null,
): Tabs.Item = Tabs.Item(
    Tabs.Item.Properties(
        div = valueOrNull(div),
        title = valueOrNull(title),
        titleClickAction = valueOrNull(titleClickAction),
    )
)

/**
 * @param div Tab contents.
 * @param title Tab title.
 * @param titleClickAction Action when clicking on the active tab title.
 */
@Generated
fun DivScope.tabsItemProps(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    title: String? = null,
    titleClickAction: Action? = null,
) = Tabs.Item.Properties(
    div = valueOrNull(div),
    title = valueOrNull(title),
    titleClickAction = valueOrNull(titleClickAction),
)

/**
 * @param div Tab contents.
 * @param title Tab title.
 * @param titleClickAction Action when clicking on the active tab title.
 */
@Generated
fun TemplateScope.tabsItemRefs(
    `use named arguments`: Guard = Guard.instance,
    div: ReferenceProperty<Div>? = null,
    title: ReferenceProperty<String>? = null,
    titleClickAction: ReferenceProperty<Action>? = null,
) = Tabs.Item.Properties(
    div = div,
    title = title,
    titleClickAction = titleClickAction,
)

/**
 * @param div Tab contents.
 * @param title Tab title.
 * @param titleClickAction Action when clicking on the active tab title.
 */
@Generated
fun Tabs.Item.override(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    title: String? = null,
    titleClickAction: Action? = null,
): Tabs.Item = Tabs.Item(
    Tabs.Item.Properties(
        div = valueOrNull(div) ?: properties.div,
        title = valueOrNull(title) ?: properties.title,
        titleClickAction = valueOrNull(titleClickAction) ?: properties.titleClickAction,
    )
)

/**
 * @param div Tab contents.
 * @param title Tab title.
 * @param titleClickAction Action when clicking on the active tab title.
 */
@Generated
fun Tabs.Item.defer(
    `use named arguments`: Guard = Guard.instance,
    div: ReferenceProperty<Div>? = null,
    title: ReferenceProperty<String>? = null,
    titleClickAction: ReferenceProperty<Action>? = null,
): Tabs.Item = Tabs.Item(
    Tabs.Item.Properties(
        div = div ?: properties.div,
        title = title ?: properties.title,
        titleClickAction = titleClickAction ?: properties.titleClickAction,
    )
)

/**
 * @param title Tab title.
 */
@Generated
fun Tabs.Item.evaluate(
    `use named arguments`: Guard = Guard.instance,
    title: ExpressionProperty<String>? = null,
): Tabs.Item = Tabs.Item(
    Tabs.Item.Properties(
        div = properties.div,
        title = title ?: properties.title,
        titleClickAction = properties.titleClickAction,
    )
)

@Generated
fun Tabs.Item.asList() = listOf(this)

/**
 * @param activeBackgroundColor Background color of the active tab title.
 * @param activeFontWeight Active tab title style.
 * @param activeTextColor Color of the active tab title text.
 * @param animationDuration Duration of active title change animation.
 * @param animationType Active title change animation.
 * @param cornerRadius Title corner rounding radius. If the parameter isn't specified, the rounding is maximum (half of the smallest size). Not used if the `corners_radius` parameter is set.
 * @param cornersRadius Rounding radii of corners of multiple titles. Empty values are replaced by `corner_radius`.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Title font size.
 * @param fontSizeUnit Units of title font size measurement.
 * @param fontWeight Style. Use `active_font_weight` and `inactive_font_weight` instead.
 * @param inactiveBackgroundColor Background color of the inactive tab title.
 * @param inactiveFontWeight Inactive tab title style.
 * @param inactiveTextColor Color of the inactive tab title text.
 * @param itemSpacing Spacing between neighbouring tab titles.
 * @param letterSpacing Spacing between title characters.
 * @param lineHeight Line spacing of the text.
 * @param paddings Indents around the tab title.
 */
@Generated
fun DivScope.tabsTabTitleStyle(
    `use named arguments`: Guard = Guard.instance,
    activeBackgroundColor: Color? = null,
    activeFontWeight: FontWeight? = null,
    activeTextColor: Color? = null,
    animationDuration: Int? = null,
    animationType: Tabs.TabTitleStyle.AnimationType? = null,
    cornerRadius: Int? = null,
    cornersRadius: CornersRadius? = null,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    inactiveBackgroundColor: Color? = null,
    inactiveFontWeight: FontWeight? = null,
    inactiveTextColor: Color? = null,
    itemSpacing: Int? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    paddings: EdgeInsets? = null,
): Tabs.TabTitleStyle = Tabs.TabTitleStyle(
    Tabs.TabTitleStyle.Properties(
        activeBackgroundColor = valueOrNull(activeBackgroundColor),
        activeFontWeight = valueOrNull(activeFontWeight),
        activeTextColor = valueOrNull(activeTextColor),
        animationDuration = valueOrNull(animationDuration),
        animationType = valueOrNull(animationType),
        cornerRadius = valueOrNull(cornerRadius),
        cornersRadius = valueOrNull(cornersRadius),
        fontFamily = valueOrNull(fontFamily),
        fontSize = valueOrNull(fontSize),
        fontSizeUnit = valueOrNull(fontSizeUnit),
        fontWeight = valueOrNull(fontWeight),
        inactiveBackgroundColor = valueOrNull(inactiveBackgroundColor),
        inactiveFontWeight = valueOrNull(inactiveFontWeight),
        inactiveTextColor = valueOrNull(inactiveTextColor),
        itemSpacing = valueOrNull(itemSpacing),
        letterSpacing = valueOrNull(letterSpacing),
        lineHeight = valueOrNull(lineHeight),
        paddings = valueOrNull(paddings),
    )
)

/**
 * @param activeBackgroundColor Background color of the active tab title.
 * @param activeFontWeight Active tab title style.
 * @param activeTextColor Color of the active tab title text.
 * @param animationDuration Duration of active title change animation.
 * @param animationType Active title change animation.
 * @param cornerRadius Title corner rounding radius. If the parameter isn't specified, the rounding is maximum (half of the smallest size). Not used if the `corners_radius` parameter is set.
 * @param cornersRadius Rounding radii of corners of multiple titles. Empty values are replaced by `corner_radius`.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Title font size.
 * @param fontSizeUnit Units of title font size measurement.
 * @param fontWeight Style. Use `active_font_weight` and `inactive_font_weight` instead.
 * @param inactiveBackgroundColor Background color of the inactive tab title.
 * @param inactiveFontWeight Inactive tab title style.
 * @param inactiveTextColor Color of the inactive tab title text.
 * @param itemSpacing Spacing between neighbouring tab titles.
 * @param letterSpacing Spacing between title characters.
 * @param lineHeight Line spacing of the text.
 * @param paddings Indents around the tab title.
 */
@Generated
fun DivScope.tabsTabTitleStyleProps(
    `use named arguments`: Guard = Guard.instance,
    activeBackgroundColor: Color? = null,
    activeFontWeight: FontWeight? = null,
    activeTextColor: Color? = null,
    animationDuration: Int? = null,
    animationType: Tabs.TabTitleStyle.AnimationType? = null,
    cornerRadius: Int? = null,
    cornersRadius: CornersRadius? = null,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    inactiveBackgroundColor: Color? = null,
    inactiveFontWeight: FontWeight? = null,
    inactiveTextColor: Color? = null,
    itemSpacing: Int? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    paddings: EdgeInsets? = null,
) = Tabs.TabTitleStyle.Properties(
    activeBackgroundColor = valueOrNull(activeBackgroundColor),
    activeFontWeight = valueOrNull(activeFontWeight),
    activeTextColor = valueOrNull(activeTextColor),
    animationDuration = valueOrNull(animationDuration),
    animationType = valueOrNull(animationType),
    cornerRadius = valueOrNull(cornerRadius),
    cornersRadius = valueOrNull(cornersRadius),
    fontFamily = valueOrNull(fontFamily),
    fontSize = valueOrNull(fontSize),
    fontSizeUnit = valueOrNull(fontSizeUnit),
    fontWeight = valueOrNull(fontWeight),
    inactiveBackgroundColor = valueOrNull(inactiveBackgroundColor),
    inactiveFontWeight = valueOrNull(inactiveFontWeight),
    inactiveTextColor = valueOrNull(inactiveTextColor),
    itemSpacing = valueOrNull(itemSpacing),
    letterSpacing = valueOrNull(letterSpacing),
    lineHeight = valueOrNull(lineHeight),
    paddings = valueOrNull(paddings),
)

/**
 * @param activeBackgroundColor Background color of the active tab title.
 * @param activeFontWeight Active tab title style.
 * @param activeTextColor Color of the active tab title text.
 * @param animationDuration Duration of active title change animation.
 * @param animationType Active title change animation.
 * @param cornerRadius Title corner rounding radius. If the parameter isn't specified, the rounding is maximum (half of the smallest size). Not used if the `corners_radius` parameter is set.
 * @param cornersRadius Rounding radii of corners of multiple titles. Empty values are replaced by `corner_radius`.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Title font size.
 * @param fontSizeUnit Units of title font size measurement.
 * @param fontWeight Style. Use `active_font_weight` and `inactive_font_weight` instead.
 * @param inactiveBackgroundColor Background color of the inactive tab title.
 * @param inactiveFontWeight Inactive tab title style.
 * @param inactiveTextColor Color of the inactive tab title text.
 * @param itemSpacing Spacing between neighbouring tab titles.
 * @param letterSpacing Spacing between title characters.
 * @param lineHeight Line spacing of the text.
 * @param paddings Indents around the tab title.
 */
@Generated
fun TemplateScope.tabsTabTitleStyleRefs(
    `use named arguments`: Guard = Guard.instance,
    activeBackgroundColor: ReferenceProperty<Color>? = null,
    activeFontWeight: ReferenceProperty<FontWeight>? = null,
    activeTextColor: ReferenceProperty<Color>? = null,
    animationDuration: ReferenceProperty<Int>? = null,
    animationType: ReferenceProperty<Tabs.TabTitleStyle.AnimationType>? = null,
    cornerRadius: ReferenceProperty<Int>? = null,
    cornersRadius: ReferenceProperty<CornersRadius>? = null,
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    inactiveBackgroundColor: ReferenceProperty<Color>? = null,
    inactiveFontWeight: ReferenceProperty<FontWeight>? = null,
    inactiveTextColor: ReferenceProperty<Color>? = null,
    itemSpacing: ReferenceProperty<Int>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
) = Tabs.TabTitleStyle.Properties(
    activeBackgroundColor = activeBackgroundColor,
    activeFontWeight = activeFontWeight,
    activeTextColor = activeTextColor,
    animationDuration = animationDuration,
    animationType = animationType,
    cornerRadius = cornerRadius,
    cornersRadius = cornersRadius,
    fontFamily = fontFamily,
    fontSize = fontSize,
    fontSizeUnit = fontSizeUnit,
    fontWeight = fontWeight,
    inactiveBackgroundColor = inactiveBackgroundColor,
    inactiveFontWeight = inactiveFontWeight,
    inactiveTextColor = inactiveTextColor,
    itemSpacing = itemSpacing,
    letterSpacing = letterSpacing,
    lineHeight = lineHeight,
    paddings = paddings,
)

/**
 * @param activeBackgroundColor Background color of the active tab title.
 * @param activeFontWeight Active tab title style.
 * @param activeTextColor Color of the active tab title text.
 * @param animationDuration Duration of active title change animation.
 * @param animationType Active title change animation.
 * @param cornerRadius Title corner rounding radius. If the parameter isn't specified, the rounding is maximum (half of the smallest size). Not used if the `corners_radius` parameter is set.
 * @param cornersRadius Rounding radii of corners of multiple titles. Empty values are replaced by `corner_radius`.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Title font size.
 * @param fontSizeUnit Units of title font size measurement.
 * @param fontWeight Style. Use `active_font_weight` and `inactive_font_weight` instead.
 * @param inactiveBackgroundColor Background color of the inactive tab title.
 * @param inactiveFontWeight Inactive tab title style.
 * @param inactiveTextColor Color of the inactive tab title text.
 * @param itemSpacing Spacing between neighbouring tab titles.
 * @param letterSpacing Spacing between title characters.
 * @param lineHeight Line spacing of the text.
 * @param paddings Indents around the tab title.
 */
@Generated
fun Tabs.TabTitleStyle.override(
    `use named arguments`: Guard = Guard.instance,
    activeBackgroundColor: Color? = null,
    activeFontWeight: FontWeight? = null,
    activeTextColor: Color? = null,
    animationDuration: Int? = null,
    animationType: Tabs.TabTitleStyle.AnimationType? = null,
    cornerRadius: Int? = null,
    cornersRadius: CornersRadius? = null,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    inactiveBackgroundColor: Color? = null,
    inactiveFontWeight: FontWeight? = null,
    inactiveTextColor: Color? = null,
    itemSpacing: Int? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    paddings: EdgeInsets? = null,
): Tabs.TabTitleStyle = Tabs.TabTitleStyle(
    Tabs.TabTitleStyle.Properties(
        activeBackgroundColor = valueOrNull(activeBackgroundColor) ?: properties.activeBackgroundColor,
        activeFontWeight = valueOrNull(activeFontWeight) ?: properties.activeFontWeight,
        activeTextColor = valueOrNull(activeTextColor) ?: properties.activeTextColor,
        animationDuration = valueOrNull(animationDuration) ?: properties.animationDuration,
        animationType = valueOrNull(animationType) ?: properties.animationType,
        cornerRadius = valueOrNull(cornerRadius) ?: properties.cornerRadius,
        cornersRadius = valueOrNull(cornersRadius) ?: properties.cornersRadius,
        fontFamily = valueOrNull(fontFamily) ?: properties.fontFamily,
        fontSize = valueOrNull(fontSize) ?: properties.fontSize,
        fontSizeUnit = valueOrNull(fontSizeUnit) ?: properties.fontSizeUnit,
        fontWeight = valueOrNull(fontWeight) ?: properties.fontWeight,
        inactiveBackgroundColor = valueOrNull(inactiveBackgroundColor) ?: properties.inactiveBackgroundColor,
        inactiveFontWeight = valueOrNull(inactiveFontWeight) ?: properties.inactiveFontWeight,
        inactiveTextColor = valueOrNull(inactiveTextColor) ?: properties.inactiveTextColor,
        itemSpacing = valueOrNull(itemSpacing) ?: properties.itemSpacing,
        letterSpacing = valueOrNull(letterSpacing) ?: properties.letterSpacing,
        lineHeight = valueOrNull(lineHeight) ?: properties.lineHeight,
        paddings = valueOrNull(paddings) ?: properties.paddings,
    )
)

/**
 * @param activeBackgroundColor Background color of the active tab title.
 * @param activeFontWeight Active tab title style.
 * @param activeTextColor Color of the active tab title text.
 * @param animationDuration Duration of active title change animation.
 * @param animationType Active title change animation.
 * @param cornerRadius Title corner rounding radius. If the parameter isn't specified, the rounding is maximum (half of the smallest size). Not used if the `corners_radius` parameter is set.
 * @param cornersRadius Rounding radii of corners of multiple titles. Empty values are replaced by `corner_radius`.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Title font size.
 * @param fontSizeUnit Units of title font size measurement.
 * @param fontWeight Style. Use `active_font_weight` and `inactive_font_weight` instead.
 * @param inactiveBackgroundColor Background color of the inactive tab title.
 * @param inactiveFontWeight Inactive tab title style.
 * @param inactiveTextColor Color of the inactive tab title text.
 * @param itemSpacing Spacing between neighbouring tab titles.
 * @param letterSpacing Spacing between title characters.
 * @param lineHeight Line spacing of the text.
 * @param paddings Indents around the tab title.
 */
@Generated
fun Tabs.TabTitleStyle.defer(
    `use named arguments`: Guard = Guard.instance,
    activeBackgroundColor: ReferenceProperty<Color>? = null,
    activeFontWeight: ReferenceProperty<FontWeight>? = null,
    activeTextColor: ReferenceProperty<Color>? = null,
    animationDuration: ReferenceProperty<Int>? = null,
    animationType: ReferenceProperty<Tabs.TabTitleStyle.AnimationType>? = null,
    cornerRadius: ReferenceProperty<Int>? = null,
    cornersRadius: ReferenceProperty<CornersRadius>? = null,
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    inactiveBackgroundColor: ReferenceProperty<Color>? = null,
    inactiveFontWeight: ReferenceProperty<FontWeight>? = null,
    inactiveTextColor: ReferenceProperty<Color>? = null,
    itemSpacing: ReferenceProperty<Int>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
): Tabs.TabTitleStyle = Tabs.TabTitleStyle(
    Tabs.TabTitleStyle.Properties(
        activeBackgroundColor = activeBackgroundColor ?: properties.activeBackgroundColor,
        activeFontWeight = activeFontWeight ?: properties.activeFontWeight,
        activeTextColor = activeTextColor ?: properties.activeTextColor,
        animationDuration = animationDuration ?: properties.animationDuration,
        animationType = animationType ?: properties.animationType,
        cornerRadius = cornerRadius ?: properties.cornerRadius,
        cornersRadius = cornersRadius ?: properties.cornersRadius,
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontWeight = fontWeight ?: properties.fontWeight,
        inactiveBackgroundColor = inactiveBackgroundColor ?: properties.inactiveBackgroundColor,
        inactiveFontWeight = inactiveFontWeight ?: properties.inactiveFontWeight,
        inactiveTextColor = inactiveTextColor ?: properties.inactiveTextColor,
        itemSpacing = itemSpacing ?: properties.itemSpacing,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        paddings = paddings ?: properties.paddings,
    )
)

/**
 * @param activeBackgroundColor Background color of the active tab title.
 * @param activeFontWeight Active tab title style.
 * @param activeTextColor Color of the active tab title text.
 * @param animationDuration Duration of active title change animation.
 * @param animationType Active title change animation.
 * @param cornerRadius Title corner rounding radius. If the parameter isn't specified, the rounding is maximum (half of the smallest size). Not used if the `corners_radius` parameter is set.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Title font size.
 * @param fontSizeUnit Units of title font size measurement.
 * @param fontWeight Style. Use `active_font_weight` and `inactive_font_weight` instead.
 * @param inactiveBackgroundColor Background color of the inactive tab title.
 * @param inactiveFontWeight Inactive tab title style.
 * @param inactiveTextColor Color of the inactive tab title text.
 * @param itemSpacing Spacing between neighbouring tab titles.
 * @param letterSpacing Spacing between title characters.
 * @param lineHeight Line spacing of the text.
 */
@Generated
fun Tabs.TabTitleStyle.evaluate(
    `use named arguments`: Guard = Guard.instance,
    activeBackgroundColor: ExpressionProperty<Color>? = null,
    activeFontWeight: ExpressionProperty<FontWeight>? = null,
    activeTextColor: ExpressionProperty<Color>? = null,
    animationDuration: ExpressionProperty<Int>? = null,
    animationType: ExpressionProperty<Tabs.TabTitleStyle.AnimationType>? = null,
    cornerRadius: ExpressionProperty<Int>? = null,
    fontFamily: ExpressionProperty<String>? = null,
    fontSize: ExpressionProperty<Int>? = null,
    fontSizeUnit: ExpressionProperty<SizeUnit>? = null,
    fontWeight: ExpressionProperty<FontWeight>? = null,
    inactiveBackgroundColor: ExpressionProperty<Color>? = null,
    inactiveFontWeight: ExpressionProperty<FontWeight>? = null,
    inactiveTextColor: ExpressionProperty<Color>? = null,
    itemSpacing: ExpressionProperty<Int>? = null,
    letterSpacing: ExpressionProperty<Double>? = null,
    lineHeight: ExpressionProperty<Int>? = null,
): Tabs.TabTitleStyle = Tabs.TabTitleStyle(
    Tabs.TabTitleStyle.Properties(
        activeBackgroundColor = activeBackgroundColor ?: properties.activeBackgroundColor,
        activeFontWeight = activeFontWeight ?: properties.activeFontWeight,
        activeTextColor = activeTextColor ?: properties.activeTextColor,
        animationDuration = animationDuration ?: properties.animationDuration,
        animationType = animationType ?: properties.animationType,
        cornerRadius = cornerRadius ?: properties.cornerRadius,
        cornersRadius = properties.cornersRadius,
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontWeight = fontWeight ?: properties.fontWeight,
        inactiveBackgroundColor = inactiveBackgroundColor ?: properties.inactiveBackgroundColor,
        inactiveFontWeight = inactiveFontWeight ?: properties.inactiveFontWeight,
        inactiveTextColor = inactiveTextColor ?: properties.inactiveTextColor,
        itemSpacing = itemSpacing ?: properties.itemSpacing,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        paddings = properties.paddings,
    )
)

@Generated
fun Tabs.TabTitleStyle.asList() = listOf(this)
