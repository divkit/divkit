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
 * Required parameters: `type`.
 */
@Generated
data class Container internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "container")
    )

    operator fun plus(additive: Properties): Container = Container(
        Properties(
            orientation = additive.orientation ?: properties.orientation,
            accessibility = additive.accessibility ?: properties.accessibility,
            action = additive.action ?: properties.action,
            actionAnimation = additive.actionAnimation ?: properties.actionAnimation,
            actions = additive.actions ?: properties.actions,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            animators = additive.animators ?: properties.animators,
            aspect = additive.aspect ?: properties.aspect,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            clipToBounds = additive.clipToBounds ?: properties.clipToBounds,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            contentAlignmentHorizontal = additive.contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
            contentAlignmentVertical = additive.contentAlignmentVertical ?: properties.contentAlignmentVertical,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            doubletapActions = additive.doubletapActions ?: properties.doubletapActions,
            extensions = additive.extensions ?: properties.extensions,
            focus = additive.focus ?: properties.focus,
            functions = additive.functions ?: properties.functions,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
            itemBuilder = additive.itemBuilder ?: properties.itemBuilder,
            items = additive.items ?: properties.items,
            layoutMode = additive.layoutMode ?: properties.layoutMode,
            layoutProvider = additive.layoutProvider ?: properties.layoutProvider,
            lineSeparator = additive.lineSeparator ?: properties.lineSeparator,
            longtapActions = additive.longtapActions ?: properties.longtapActions,
            margins = additive.margins ?: properties.margins,
            paddings = additive.paddings ?: properties.paddings,
            reuseId = additive.reuseId ?: properties.reuseId,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            separator = additive.separator ?: properties.separator,
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
         * Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
         * Default value: `vertical`.
         */
        val orientation: Property<Orientation>?,
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
         * Declaration of animators that change variable values over time.
         */
        val animators: Property<List<Animator>>?,
        /**
         * Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
        On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
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
         * Enables the bounding of child elements by the parent's borders.
         * Default value: `true`.
         */
        val clipToBounds: Property<Boolean>?,
        /**
         * Merges cells in a column of the [grid](div-grid.md) element.
         */
        val columnSpan: Property<Int>?,
        /**
         * Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
         * Default value: `start`.
         */
        val contentAlignmentHorizontal: Property<ContentAlignmentHorizontal>?,
        /**
         * Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
         * Default value: `top`.
         */
        val contentAlignmentVertical: Property<ContentAlignmentVertical>?,
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
         * Sets collection elements dynamically using `data` and `prototypes`.
         */
        val itemBuilder: Property<CollectionItemBuilder>?,
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
         * Provides data on the actual size of the element.
         */
        val layoutProvider: Property<LayoutProvider>?,
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
            result.tryPutProperty("orientation", orientation)
            result.tryPutProperty("accessibility", accessibility)
            result.tryPutProperty("action", action)
            result.tryPutProperty("action_animation", actionAnimation)
            result.tryPutProperty("actions", actions)
            result.tryPutProperty("alignment_horizontal", alignmentHorizontal)
            result.tryPutProperty("alignment_vertical", alignmentVertical)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("animators", animators)
            result.tryPutProperty("aspect", aspect)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("clip_to_bounds", clipToBounds)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("content_alignment_horizontal", contentAlignmentHorizontal)
            result.tryPutProperty("content_alignment_vertical", contentAlignmentVertical)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("doubletap_actions", doubletapActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("functions", functions)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
            result.tryPutProperty("item_builder", itemBuilder)
            result.tryPutProperty("items", items)
            result.tryPutProperty("layout_mode", layoutMode)
            result.tryPutProperty("layout_provider", layoutProvider)
            result.tryPutProperty("line_separator", lineSeparator)
            result.tryPutProperty("longtap_actions", longtapActions)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("reuse_id", reuseId)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("separator", separator)
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

    /**
     * Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
     * 
     * Possible values: [no_wrap], [wrap].
     */
    @Generated
    sealed interface LayoutMode

    /**
     * Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
     * 
     * Possible values: [vertical], [horizontal], [overlap].
     */
    @Generated
    sealed interface Orientation

    /**
     * Can be created using the method [containerSeparator].
     * 
     * Required parameters: `style`.
     */
    @Generated
    data class Separator internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Separator = Separator(
            Properties(
                margins = additive.margins ?: properties.margins,
                showAtEnd = additive.showAtEnd ?: properties.showAtEnd,
                showAtStart = additive.showAtStart ?: properties.showAtStart,
                showBetween = additive.showBetween ?: properties.showBetween,
                style = additive.style ?: properties.style,
            )
        )

        data class Properties internal constructor(
            /**
             * External margins from the element stroke.
             */
            val margins: Property<EdgeInsets>?,
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
                result.tryPutProperty("margins", margins)
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
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.row(
    vararg items: Div,
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = container(
    orientation = horizontal,
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animators = animators,
    aspect = aspect,
    background = background,
    border = border,
    clipToBounds = clipToBounds,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    focus = focus,
    functions = functions,
    height = height,
    id = id,
    itemBuilder = itemBuilder,
    items = items.toList(),
    layoutMode = layoutMode,
    layoutProvider = layoutProvider,
    lineSeparator = lineSeparator,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    separator = separator,
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
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.row(
    items: List<Div>,
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = container(
    orientation = horizontal,
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animators = animators,
    aspect = aspect,
    background = background,
    border = border,
    clipToBounds = clipToBounds,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    focus = focus,
    functions = functions,
    height = height,
    id = id,
    itemBuilder = itemBuilder,
    items = items,
    layoutMode = layoutMode,
    layoutProvider = layoutProvider,
    lineSeparator = lineSeparator,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    separator = separator,
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
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.column(
    vararg items: Div,
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = container(
    orientation = vertical,
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animators = animators,
    aspect = aspect,
    background = background,
    border = border,
    clipToBounds = clipToBounds,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    focus = focus,
    functions = functions,
    height = height,
    id = id,
    itemBuilder = itemBuilder,
    items = items.toList(),
    layoutMode = layoutMode,
    layoutProvider = layoutProvider,
    lineSeparator = lineSeparator,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    separator = separator,
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
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.column(
    items: List<Div>,
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = container(
    orientation = vertical,
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animators = animators,
    aspect = aspect,
    background = background,
    border = border,
    clipToBounds = clipToBounds,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    focus = focus,
    functions = functions,
    height = height,
    id = id,
    itemBuilder = itemBuilder,
    items = items,
    layoutMode = layoutMode,
    layoutProvider = layoutProvider,
    lineSeparator = lineSeparator,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    separator = separator,
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
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.stack(
    vararg items: Div,
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = container(
    orientation = overlap,
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animators = animators,
    aspect = aspect,
    background = background,
    border = border,
    clipToBounds = clipToBounds,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    focus = focus,
    functions = functions,
    height = height,
    id = id,
    itemBuilder = itemBuilder,
    items = items.toList(),
    layoutMode = layoutMode,
    layoutProvider = layoutProvider,
    lineSeparator = lineSeparator,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    separator = separator,
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
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.stack(
    items: List<Div>,
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = container(
    orientation = overlap,
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animators = animators,
    aspect = aspect,
    background = background,
    border = border,
    clipToBounds = clipToBounds,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    focus = focus,
    functions = functions,
    height = height,
    id = id,
    itemBuilder = itemBuilder,
    items = items,
    layoutMode = layoutMode,
    layoutProvider = layoutProvider,
    lineSeparator = lineSeparator,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    separator = separator,
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
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.container(
    orientation: Container.Orientation? = null,
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    items: List<Div>? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = Container(
    Container.Properties(
        orientation = valueOrNull(orientation),
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        animators = valueOrNull(animators),
        aspect = valueOrNull(aspect),
        background = valueOrNull(background),
        border = valueOrNull(border),
        clipToBounds = valueOrNull(clipToBounds),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        id = valueOrNull(id),
        itemBuilder = valueOrNull(itemBuilder),
        items = valueOrNull(items),
        layoutMode = valueOrNull(layoutMode),
        layoutProvider = valueOrNull(layoutProvider),
        lineSeparator = valueOrNull(lineSeparator),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        separator = valueOrNull(separator),
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
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun DivScope.containerProps(
    `use named arguments`: Guard = Guard.instance,
    orientation: Container.Orientation? = null,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    items: List<Div>? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
) = Container.Properties(
    orientation = valueOrNull(orientation),
    accessibility = valueOrNull(accessibility),
    action = valueOrNull(action),
    actionAnimation = valueOrNull(actionAnimation),
    actions = valueOrNull(actions),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    animators = valueOrNull(animators),
    aspect = valueOrNull(aspect),
    background = valueOrNull(background),
    border = valueOrNull(border),
    clipToBounds = valueOrNull(clipToBounds),
    columnSpan = valueOrNull(columnSpan),
    contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
    contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
    disappearActions = valueOrNull(disappearActions),
    doubletapActions = valueOrNull(doubletapActions),
    extensions = valueOrNull(extensions),
    focus = valueOrNull(focus),
    functions = valueOrNull(functions),
    height = valueOrNull(height),
    id = valueOrNull(id),
    itemBuilder = valueOrNull(itemBuilder),
    items = valueOrNull(items),
    layoutMode = valueOrNull(layoutMode),
    layoutProvider = valueOrNull(layoutProvider),
    lineSeparator = valueOrNull(lineSeparator),
    longtapActions = valueOrNull(longtapActions),
    margins = valueOrNull(margins),
    paddings = valueOrNull(paddings),
    reuseId = valueOrNull(reuseId),
    rowSpan = valueOrNull(rowSpan),
    selectedActions = valueOrNull(selectedActions),
    separator = valueOrNull(separator),
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
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun TemplateScope.containerRefs(
    `use named arguments`: Guard = Guard.instance,
    orientation: ReferenceProperty<Container.Orientation>? = null,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animators: ReferenceProperty<List<Animator>>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    clipToBounds: ReferenceProperty<Boolean>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<ContentAlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<ContentAlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    itemBuilder: ReferenceProperty<CollectionItemBuilder>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    layoutMode: ReferenceProperty<Container.LayoutMode>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    lineSeparator: ReferenceProperty<Container.Separator>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    separator: ReferenceProperty<Container.Separator>? = null,
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
) = Container.Properties(
    orientation = orientation,
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animators = animators,
    aspect = aspect,
    background = background,
    border = border,
    clipToBounds = clipToBounds,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    focus = focus,
    functions = functions,
    height = height,
    id = id,
    itemBuilder = itemBuilder,
    items = items,
    layoutMode = layoutMode,
    layoutProvider = layoutProvider,
    lineSeparator = lineSeparator,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    separator = separator,
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
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun Container.override(
    `use named arguments`: Guard = Guard.instance,
    orientation: Container.Orientation? = null,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    items: List<Div>? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Container = Container(
    Container.Properties(
        orientation = valueOrNull(orientation) ?: properties.orientation,
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        action = valueOrNull(action) ?: properties.action,
        actionAnimation = valueOrNull(actionAnimation) ?: properties.actionAnimation,
        actions = valueOrNull(actions) ?: properties.actions,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        animators = valueOrNull(animators) ?: properties.animators,
        aspect = valueOrNull(aspect) ?: properties.aspect,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        clipToBounds = valueOrNull(clipToBounds) ?: properties.clipToBounds,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal) ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical) ?: properties.contentAlignmentVertical,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        doubletapActions = valueOrNull(doubletapActions) ?: properties.doubletapActions,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        focus = valueOrNull(focus) ?: properties.focus,
        functions = valueOrNull(functions) ?: properties.functions,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
        itemBuilder = valueOrNull(itemBuilder) ?: properties.itemBuilder,
        items = valueOrNull(items) ?: properties.items,
        layoutMode = valueOrNull(layoutMode) ?: properties.layoutMode,
        layoutProvider = valueOrNull(layoutProvider) ?: properties.layoutProvider,
        lineSeparator = valueOrNull(lineSeparator) ?: properties.lineSeparator,
        longtapActions = valueOrNull(longtapActions) ?: properties.longtapActions,
        margins = valueOrNull(margins) ?: properties.margins,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        reuseId = valueOrNull(reuseId) ?: properties.reuseId,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        separator = valueOrNull(separator) ?: properties.separator,
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
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun Container.defer(
    `use named arguments`: Guard = Guard.instance,
    orientation: ReferenceProperty<Container.Orientation>? = null,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animators: ReferenceProperty<List<Animator>>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    clipToBounds: ReferenceProperty<Boolean>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<ContentAlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<ContentAlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    itemBuilder: ReferenceProperty<CollectionItemBuilder>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    layoutMode: ReferenceProperty<Container.LayoutMode>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    lineSeparator: ReferenceProperty<Container.Separator>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    separator: ReferenceProperty<Container.Separator>? = null,
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
): Container = Container(
    Container.Properties(
        orientation = orientation ?: properties.orientation,
        accessibility = accessibility ?: properties.accessibility,
        action = action ?: properties.action,
        actionAnimation = actionAnimation ?: properties.actionAnimation,
        actions = actions ?: properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animators = animators ?: properties.animators,
        aspect = aspect ?: properties.aspect,
        background = background ?: properties.background,
        border = border ?: properties.border,
        clipToBounds = clipToBounds ?: properties.clipToBounds,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        disappearActions = disappearActions ?: properties.disappearActions,
        doubletapActions = doubletapActions ?: properties.doubletapActions,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        functions = functions ?: properties.functions,
        height = height ?: properties.height,
        id = id ?: properties.id,
        itemBuilder = itemBuilder ?: properties.itemBuilder,
        items = items ?: properties.items,
        layoutMode = layoutMode ?: properties.layoutMode,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        lineSeparator = lineSeparator ?: properties.lineSeparator,
        longtapActions = longtapActions ?: properties.longtapActions,
        margins = margins ?: properties.margins,
        paddings = paddings ?: properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        separator = separator ?: properties.separator,
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
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Container.evaluate(
    `use named arguments`: Guard = Guard.instance,
    orientation: ExpressionProperty<Container.Orientation>? = null,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    clipToBounds: ExpressionProperty<Boolean>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    contentAlignmentHorizontal: ExpressionProperty<ContentAlignmentHorizontal>? = null,
    contentAlignmentVertical: ExpressionProperty<ContentAlignmentVertical>? = null,
    layoutMode: ExpressionProperty<Container.LayoutMode>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Container = Container(
    Container.Properties(
        orientation = orientation ?: properties.orientation,
        accessibility = properties.accessibility,
        action = properties.action,
        actionAnimation = properties.actionAnimation,
        actions = properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animators = properties.animators,
        aspect = properties.aspect,
        background = properties.background,
        border = properties.border,
        clipToBounds = clipToBounds ?: properties.clipToBounds,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        disappearActions = properties.disappearActions,
        doubletapActions = properties.doubletapActions,
        extensions = properties.extensions,
        focus = properties.focus,
        functions = properties.functions,
        height = properties.height,
        id = properties.id,
        itemBuilder = properties.itemBuilder,
        items = properties.items,
        layoutMode = layoutMode ?: properties.layoutMode,
        layoutProvider = properties.layoutProvider,
        lineSeparator = properties.lineSeparator,
        longtapActions = properties.longtapActions,
        margins = properties.margins,
        paddings = properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = properties.selectedActions,
        separator = properties.separator,
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
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun Component<Container>.override(
    `use named arguments`: Guard = Guard.instance,
    orientation: Container.Orientation? = null,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    clipToBounds: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: ContentAlignmentHorizontal? = null,
    contentAlignmentVertical: ContentAlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    id: String? = null,
    itemBuilder: CollectionItemBuilder? = null,
    items: List<Div>? = null,
    layoutMode: Container.LayoutMode? = null,
    layoutProvider: LayoutProvider? = null,
    lineSeparator: Container.Separator? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    separator: Container.Separator? = null,
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
): Component<Container> = Component(
    template = template,
    properties = Container.Properties(
        orientation = valueOrNull(orientation),
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        animators = valueOrNull(animators),
        aspect = valueOrNull(aspect),
        background = valueOrNull(background),
        border = valueOrNull(border),
        clipToBounds = valueOrNull(clipToBounds),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        id = valueOrNull(id),
        itemBuilder = valueOrNull(itemBuilder),
        items = valueOrNull(items),
        layoutMode = valueOrNull(layoutMode),
        layoutProvider = valueOrNull(layoutProvider),
        lineSeparator = valueOrNull(lineSeparator),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        separator = valueOrNull(separator),
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
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animators Declaration of animators that change variable values over time.
 * @param aspect Fixed aspect ratio of the container. The element's height is calculated based on the width, ignoring the `height` parameter's value. 
On the web, support for the `aspect-ratio` CSS property is required to use this parameter.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param itemBuilder Sets collection elements dynamically using `data` and `prototypes`.
 * @param items Nested elements.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param layoutProvider Provides data on the actual size of the element.
 * @param lineSeparator Separator between elements along the cross axis. Not used if the `layout_mode` parameter is set to `no_wrap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param separator Separator between elements along the main axis. Not used if the `orientation` parameter is set to `overlap`. Only new browsers are supported on the web (the `gap` property must be supported for flex blocks).
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
fun Component<Container>.defer(
    `use named arguments`: Guard = Guard.instance,
    orientation: ReferenceProperty<Container.Orientation>? = null,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animators: ReferenceProperty<List<Animator>>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    clipToBounds: ReferenceProperty<Boolean>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<ContentAlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<ContentAlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    itemBuilder: ReferenceProperty<CollectionItemBuilder>? = null,
    items: ReferenceProperty<List<Div>>? = null,
    layoutMode: ReferenceProperty<Container.LayoutMode>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    lineSeparator: ReferenceProperty<Container.Separator>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    separator: ReferenceProperty<Container.Separator>? = null,
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
): Component<Container> = Component(
    template = template,
    properties = Container.Properties(
        orientation = orientation,
        accessibility = accessibility,
        action = action,
        actionAnimation = actionAnimation,
        actions = actions,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animators = animators,
        aspect = aspect,
        background = background,
        border = border,
        clipToBounds = clipToBounds,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        disappearActions = disappearActions,
        doubletapActions = doubletapActions,
        extensions = extensions,
        focus = focus,
        functions = functions,
        height = height,
        id = id,
        itemBuilder = itemBuilder,
        items = items,
        layoutMode = layoutMode,
        layoutProvider = layoutProvider,
        lineSeparator = lineSeparator,
        longtapActions = longtapActions,
        margins = margins,
        paddings = paddings,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        separator = separator,
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
 * @param orientation Location of elements. `overlap` value overlays elements on top of each other in the order of enumeration. The lowest is the zero element of an array.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param clipToBounds Enables the bounding of child elements by the parent's borders.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal element alignment. For child elements, it can be redefined using the `alignment_horizontal` property.
 * @param contentAlignmentVertical Vertical element alignment. The `baseline` value aligns elements along their own specified baseline (for text and other elements that have a baseline). Elements that don't have their baseline value specified are aligned along the top edge. For child elements, it can be redefined using the `alignment_vertical` property.
 * @param layoutMode Element placement method. The `wrap` value transfers elements to the next line if they don't fit in the previous one. If the `wrap` value is set:<li>A separate line is allocated for each element along the main axis with the size value set to `match_parent`.</li><li>Elements along the cross axis with the size value `match_parent` are ignored.</li>
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Container>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    orientation: ExpressionProperty<Container.Orientation>? = null,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    clipToBounds: ExpressionProperty<Boolean>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    contentAlignmentHorizontal: ExpressionProperty<ContentAlignmentHorizontal>? = null,
    contentAlignmentVertical: ExpressionProperty<ContentAlignmentVertical>? = null,
    layoutMode: ExpressionProperty<Container.LayoutMode>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Container> = Component(
    template = template,
    properties = Container.Properties(
        orientation = orientation,
        accessibility = null,
        action = null,
        actionAnimation = null,
        actions = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animators = null,
        aspect = null,
        background = null,
        border = null,
        clipToBounds = clipToBounds,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        disappearActions = null,
        doubletapActions = null,
        extensions = null,
        focus = null,
        functions = null,
        height = null,
        id = null,
        itemBuilder = null,
        items = null,
        layoutMode = layoutMode,
        layoutProvider = null,
        lineSeparator = null,
        longtapActions = null,
        margins = null,
        paddings = null,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = null,
        separator = null,
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

@Generated
operator fun Component<Container>.plus(additive: Container.Properties): Component<Container> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Container.asList() = listOf(this)

@Generated
fun Container.LayoutMode.asList() = listOf(this)

@Generated
fun Container.Orientation.asList() = listOf(this)

/**
 * @param margins External margins from the element stroke.
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun DivScope.containerSeparator(
    `use named arguments`: Guard = Guard.instance,
    margins: EdgeInsets? = null,
    showAtEnd: Boolean? = null,
    showAtStart: Boolean? = null,
    showBetween: Boolean? = null,
    style: Drawable? = null,
): Container.Separator = Container.Separator(
    Container.Separator.Properties(
        margins = valueOrNull(margins),
        showAtEnd = valueOrNull(showAtEnd),
        showAtStart = valueOrNull(showAtStart),
        showBetween = valueOrNull(showBetween),
        style = valueOrNull(style),
    )
)

/**
 * @param margins External margins from the element stroke.
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun DivScope.containerSeparatorProps(
    `use named arguments`: Guard = Guard.instance,
    margins: EdgeInsets? = null,
    showAtEnd: Boolean? = null,
    showAtStart: Boolean? = null,
    showBetween: Boolean? = null,
    style: Drawable? = null,
) = Container.Separator.Properties(
    margins = valueOrNull(margins),
    showAtEnd = valueOrNull(showAtEnd),
    showAtStart = valueOrNull(showAtStart),
    showBetween = valueOrNull(showBetween),
    style = valueOrNull(style),
)

/**
 * @param margins External margins from the element stroke.
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun TemplateScope.containerSeparatorRefs(
    `use named arguments`: Guard = Guard.instance,
    margins: ReferenceProperty<EdgeInsets>? = null,
    showAtEnd: ReferenceProperty<Boolean>? = null,
    showAtStart: ReferenceProperty<Boolean>? = null,
    showBetween: ReferenceProperty<Boolean>? = null,
    style: ReferenceProperty<Drawable>? = null,
) = Container.Separator.Properties(
    margins = margins,
    showAtEnd = showAtEnd,
    showAtStart = showAtStart,
    showBetween = showBetween,
    style = style,
)

/**
 * @param margins External margins from the element stroke.
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun Container.Separator.override(
    `use named arguments`: Guard = Guard.instance,
    margins: EdgeInsets? = null,
    showAtEnd: Boolean? = null,
    showAtStart: Boolean? = null,
    showBetween: Boolean? = null,
    style: Drawable? = null,
): Container.Separator = Container.Separator(
    Container.Separator.Properties(
        margins = valueOrNull(margins) ?: properties.margins,
        showAtEnd = valueOrNull(showAtEnd) ?: properties.showAtEnd,
        showAtStart = valueOrNull(showAtStart) ?: properties.showAtStart,
        showBetween = valueOrNull(showBetween) ?: properties.showBetween,
        style = valueOrNull(style) ?: properties.style,
    )
)

/**
 * @param margins External margins from the element stroke.
 * @param showAtEnd Enables displaying the separator after the last item.
 * @param showAtStart Enables displaying the separator before the first item.
 * @param showBetween Enables displaying the separator between items.
 * @param style Separator style.
 */
@Generated
fun Container.Separator.defer(
    `use named arguments`: Guard = Guard.instance,
    margins: ReferenceProperty<EdgeInsets>? = null,
    showAtEnd: ReferenceProperty<Boolean>? = null,
    showAtStart: ReferenceProperty<Boolean>? = null,
    showBetween: ReferenceProperty<Boolean>? = null,
    style: ReferenceProperty<Drawable>? = null,
): Container.Separator = Container.Separator(
    Container.Separator.Properties(
        margins = margins ?: properties.margins,
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
        margins = properties.margins,
        showAtEnd = showAtEnd ?: properties.showAtEnd,
        showAtStart = showAtStart ?: properties.showAtStart,
        showBetween = showBetween ?: properties.showBetween,
        style = properties.style,
    )
)

@Generated
fun Container.Separator.asList() = listOf(this)
