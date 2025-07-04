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
 * Progress indicator for [pager](div-pager.md).
 * 
 * Can be created using the method [indicator].
 * 
 * Required parameters: `type`.
 */
@Generated
data class Indicator internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "indicator")
    )

    operator fun plus(additive: Properties): Indicator = Indicator(
        Properties(
            accessibility = additive.accessibility ?: properties.accessibility,
            activeItemColor = additive.activeItemColor ?: properties.activeItemColor,
            activeItemSize = additive.activeItemSize ?: properties.activeItemSize,
            activeShape = additive.activeShape ?: properties.activeShape,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            animation = additive.animation ?: properties.animation,
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
            inactiveItemColor = additive.inactiveItemColor ?: properties.inactiveItemColor,
            inactiveMinimumShape = additive.inactiveMinimumShape ?: properties.inactiveMinimumShape,
            inactiveShape = additive.inactiveShape ?: properties.inactiveShape,
            itemsPlacement = additive.itemsPlacement ?: properties.itemsPlacement,
            layoutProvider = additive.layoutProvider ?: properties.layoutProvider,
            margins = additive.margins ?: properties.margins,
            minimumItemSize = additive.minimumItemSize ?: properties.minimumItemSize,
            paddings = additive.paddings ?: properties.paddings,
            pagerId = additive.pagerId ?: properties.pagerId,
            reuseId = additive.reuseId ?: properties.reuseId,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            shape = additive.shape ?: properties.shape,
            spaceBetweenCenters = additive.spaceBetweenCenters ?: properties.spaceBetweenCenters,
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
         * Active indicator color.
         * Default value: `#ffdc60`.
         */
        @Deprecated("Marked as deprecated in the JSON schema ")
        val activeItemColor: Property<Color>?,
        /**
         * A size multiplier for an active indicator.
         * Default value: `1.3`.
         */
        @Deprecated("Marked as deprecated in the JSON schema ")
        val activeItemSize: Property<Double>?,
        /**
         * Active indicator shape.
         */
        val activeShape: Property<RoundedRectangleShape>?,
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
         * Animation of switching between indicators.
         * Default value: `scale`.
         */
        val animation: Property<Animation>?,
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
         * Indicator color.
         * Default value: `#33919cb5`.
         */
        @Deprecated("Marked as deprecated in the JSON schema ")
        val inactiveItemColor: Property<Color>?,
        /**
         * Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
         */
        val inactiveMinimumShape: Property<RoundedRectangleShape>?,
        /**
         * Indicator shape.
         */
        val inactiveShape: Property<RoundedRectangleShape>?,
        /**
         * Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
         */
        val itemsPlacement: Property<IndicatorItemPlacement>?,
        /**
         * Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
         */
        val layoutProvider: Property<LayoutProvider>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
         * Default value: `0.5`.
         */
        @Deprecated("Marked as deprecated in the JSON schema ")
        val minimumItemSize: Property<Double>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
        /**
         * ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
         */
        val pagerId: Property<String>?,
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
         * Indicator shape.
         * Default value: `{"type":"rounded_rectangle"}`.
         */
        @Deprecated("Marked as deprecated in the JSON schema ")
        val shape: Property<Shape>?,
        /**
         * Spacing between indicator centers.
         * Default value: `{"type": "fixed","value":15}`.
         */
        @Deprecated("Marked as deprecated in the JSON schema ")
        val spaceBetweenCenters: Property<FixedSize>?,
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
            result.tryPutProperty("active_item_color", activeItemColor)
            result.tryPutProperty("active_item_size", activeItemSize)
            result.tryPutProperty("active_shape", activeShape)
            result.tryPutProperty("alignment_horizontal", alignmentHorizontal)
            result.tryPutProperty("alignment_vertical", alignmentVertical)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("animation", animation)
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
            result.tryPutProperty("inactive_item_color", inactiveItemColor)
            result.tryPutProperty("inactive_minimum_shape", inactiveMinimumShape)
            result.tryPutProperty("inactive_shape", inactiveShape)
            result.tryPutProperty("items_placement", itemsPlacement)
            result.tryPutProperty("layout_provider", layoutProvider)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("minimum_item_size", minimumItemSize)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("pager_id", pagerId)
            result.tryPutProperty("reuse_id", reuseId)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("shape", shape)
            result.tryPutProperty("space_between_centers", spaceBetweenCenters)
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
     * Animation of switching between indicators.
     * 
     * Possible values: [scale], [worm], [slider].
     */
    @Generated
    sealed interface Animation
}

/**
 * @param accessibility Accessibility settings.
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param activeShape Active indicator shape.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
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
 * @param inactiveItemColor Indicator color.
 * @param inactiveMinimumShape Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
 * @param inactiveShape Indicator shape.
 * @param itemsPlacement Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param margins External margins from the element stroke.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param paddings Internal margins from the element stroke.
 * @param pagerId ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param shape Indicator shape.
 * @param spaceBetweenCenters Spacing between indicator centers.
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
fun DivScope.indicator(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    activeItemColor: Color? = null,
    activeItemSize: Double? = null,
    activeShape: RoundedRectangleShape? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animation: Indicator.Animation? = null,
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
    inactiveItemColor: Color? = null,
    inactiveMinimumShape: RoundedRectangleShape? = null,
    inactiveShape: RoundedRectangleShape? = null,
    itemsPlacement: IndicatorItemPlacement? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    minimumItemSize: Double? = null,
    paddings: EdgeInsets? = null,
    pagerId: String? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    shape: Shape? = null,
    spaceBetweenCenters: FixedSize? = null,
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
): Indicator = Indicator(
    Indicator.Properties(
        accessibility = valueOrNull(accessibility),
        activeItemColor = valueOrNull(activeItemColor),
        activeItemSize = valueOrNull(activeItemSize),
        activeShape = valueOrNull(activeShape),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        animation = valueOrNull(animation),
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
        inactiveItemColor = valueOrNull(inactiveItemColor),
        inactiveMinimumShape = valueOrNull(inactiveMinimumShape),
        inactiveShape = valueOrNull(inactiveShape),
        itemsPlacement = valueOrNull(itemsPlacement),
        layoutProvider = valueOrNull(layoutProvider),
        margins = valueOrNull(margins),
        minimumItemSize = valueOrNull(minimumItemSize),
        paddings = valueOrNull(paddings),
        pagerId = valueOrNull(pagerId),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        shape = valueOrNull(shape),
        spaceBetweenCenters = valueOrNull(spaceBetweenCenters),
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param activeShape Active indicator shape.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
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
 * @param inactiveItemColor Indicator color.
 * @param inactiveMinimumShape Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
 * @param inactiveShape Indicator shape.
 * @param itemsPlacement Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param margins External margins from the element stroke.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param paddings Internal margins from the element stroke.
 * @param pagerId ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param shape Indicator shape.
 * @param spaceBetweenCenters Spacing between indicator centers.
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
fun DivScope.indicatorProps(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    activeItemColor: Color? = null,
    activeItemSize: Double? = null,
    activeShape: RoundedRectangleShape? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animation: Indicator.Animation? = null,
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
    inactiveItemColor: Color? = null,
    inactiveMinimumShape: RoundedRectangleShape? = null,
    inactiveShape: RoundedRectangleShape? = null,
    itemsPlacement: IndicatorItemPlacement? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    minimumItemSize: Double? = null,
    paddings: EdgeInsets? = null,
    pagerId: String? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    shape: Shape? = null,
    spaceBetweenCenters: FixedSize? = null,
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
) = Indicator.Properties(
    accessibility = valueOrNull(accessibility),
    activeItemColor = valueOrNull(activeItemColor),
    activeItemSize = valueOrNull(activeItemSize),
    activeShape = valueOrNull(activeShape),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    animation = valueOrNull(animation),
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
    inactiveItemColor = valueOrNull(inactiveItemColor),
    inactiveMinimumShape = valueOrNull(inactiveMinimumShape),
    inactiveShape = valueOrNull(inactiveShape),
    itemsPlacement = valueOrNull(itemsPlacement),
    layoutProvider = valueOrNull(layoutProvider),
    margins = valueOrNull(margins),
    minimumItemSize = valueOrNull(minimumItemSize),
    paddings = valueOrNull(paddings),
    pagerId = valueOrNull(pagerId),
    reuseId = valueOrNull(reuseId),
    rowSpan = valueOrNull(rowSpan),
    selectedActions = valueOrNull(selectedActions),
    shape = valueOrNull(shape),
    spaceBetweenCenters = valueOrNull(spaceBetweenCenters),
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param activeShape Active indicator shape.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
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
 * @param inactiveItemColor Indicator color.
 * @param inactiveMinimumShape Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
 * @param inactiveShape Indicator shape.
 * @param itemsPlacement Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param margins External margins from the element stroke.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param paddings Internal margins from the element stroke.
 * @param pagerId ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param shape Indicator shape.
 * @param spaceBetweenCenters Spacing between indicator centers.
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
fun TemplateScope.indicatorRefs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    activeItemColor: ReferenceProperty<Color>? = null,
    activeItemSize: ReferenceProperty<Double>? = null,
    activeShape: ReferenceProperty<RoundedRectangleShape>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animation: ReferenceProperty<Indicator.Animation>? = null,
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
    inactiveItemColor: ReferenceProperty<Color>? = null,
    inactiveMinimumShape: ReferenceProperty<RoundedRectangleShape>? = null,
    inactiveShape: ReferenceProperty<RoundedRectangleShape>? = null,
    itemsPlacement: ReferenceProperty<IndicatorItemPlacement>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    minimumItemSize: ReferenceProperty<Double>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    pagerId: ReferenceProperty<String>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    shape: ReferenceProperty<Shape>? = null,
    spaceBetweenCenters: ReferenceProperty<FixedSize>? = null,
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
) = Indicator.Properties(
    accessibility = accessibility,
    activeItemColor = activeItemColor,
    activeItemSize = activeItemSize,
    activeShape = activeShape,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animation = animation,
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
    inactiveItemColor = inactiveItemColor,
    inactiveMinimumShape = inactiveMinimumShape,
    inactiveShape = inactiveShape,
    itemsPlacement = itemsPlacement,
    layoutProvider = layoutProvider,
    margins = margins,
    minimumItemSize = minimumItemSize,
    paddings = paddings,
    pagerId = pagerId,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    shape = shape,
    spaceBetweenCenters = spaceBetweenCenters,
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param activeShape Active indicator shape.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
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
 * @param inactiveItemColor Indicator color.
 * @param inactiveMinimumShape Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
 * @param inactiveShape Indicator shape.
 * @param itemsPlacement Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param margins External margins from the element stroke.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param paddings Internal margins from the element stroke.
 * @param pagerId ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param shape Indicator shape.
 * @param spaceBetweenCenters Spacing between indicator centers.
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
fun Indicator.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    activeItemColor: Color? = null,
    activeItemSize: Double? = null,
    activeShape: RoundedRectangleShape? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animation: Indicator.Animation? = null,
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
    inactiveItemColor: Color? = null,
    inactiveMinimumShape: RoundedRectangleShape? = null,
    inactiveShape: RoundedRectangleShape? = null,
    itemsPlacement: IndicatorItemPlacement? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    minimumItemSize: Double? = null,
    paddings: EdgeInsets? = null,
    pagerId: String? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    shape: Shape? = null,
    spaceBetweenCenters: FixedSize? = null,
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
): Indicator = Indicator(
    Indicator.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        activeItemColor = valueOrNull(activeItemColor) ?: properties.activeItemColor,
        activeItemSize = valueOrNull(activeItemSize) ?: properties.activeItemSize,
        activeShape = valueOrNull(activeShape) ?: properties.activeShape,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        animation = valueOrNull(animation) ?: properties.animation,
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
        inactiveItemColor = valueOrNull(inactiveItemColor) ?: properties.inactiveItemColor,
        inactiveMinimumShape = valueOrNull(inactiveMinimumShape) ?: properties.inactiveMinimumShape,
        inactiveShape = valueOrNull(inactiveShape) ?: properties.inactiveShape,
        itemsPlacement = valueOrNull(itemsPlacement) ?: properties.itemsPlacement,
        layoutProvider = valueOrNull(layoutProvider) ?: properties.layoutProvider,
        margins = valueOrNull(margins) ?: properties.margins,
        minimumItemSize = valueOrNull(minimumItemSize) ?: properties.minimumItemSize,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        pagerId = valueOrNull(pagerId) ?: properties.pagerId,
        reuseId = valueOrNull(reuseId) ?: properties.reuseId,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        shape = valueOrNull(shape) ?: properties.shape,
        spaceBetweenCenters = valueOrNull(spaceBetweenCenters) ?: properties.spaceBetweenCenters,
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param activeShape Active indicator shape.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
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
 * @param inactiveItemColor Indicator color.
 * @param inactiveMinimumShape Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
 * @param inactiveShape Indicator shape.
 * @param itemsPlacement Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param margins External margins from the element stroke.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param paddings Internal margins from the element stroke.
 * @param pagerId ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param shape Indicator shape.
 * @param spaceBetweenCenters Spacing between indicator centers.
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
fun Indicator.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    activeItemColor: ReferenceProperty<Color>? = null,
    activeItemSize: ReferenceProperty<Double>? = null,
    activeShape: ReferenceProperty<RoundedRectangleShape>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animation: ReferenceProperty<Indicator.Animation>? = null,
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
    inactiveItemColor: ReferenceProperty<Color>? = null,
    inactiveMinimumShape: ReferenceProperty<RoundedRectangleShape>? = null,
    inactiveShape: ReferenceProperty<RoundedRectangleShape>? = null,
    itemsPlacement: ReferenceProperty<IndicatorItemPlacement>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    minimumItemSize: ReferenceProperty<Double>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    pagerId: ReferenceProperty<String>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    shape: ReferenceProperty<Shape>? = null,
    spaceBetweenCenters: ReferenceProperty<FixedSize>? = null,
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
): Indicator = Indicator(
    Indicator.Properties(
        accessibility = accessibility ?: properties.accessibility,
        activeItemColor = activeItemColor ?: properties.activeItemColor,
        activeItemSize = activeItemSize ?: properties.activeItemSize,
        activeShape = activeShape ?: properties.activeShape,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animation = animation ?: properties.animation,
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
        inactiveItemColor = inactiveItemColor ?: properties.inactiveItemColor,
        inactiveMinimumShape = inactiveMinimumShape ?: properties.inactiveMinimumShape,
        inactiveShape = inactiveShape ?: properties.inactiveShape,
        itemsPlacement = itemsPlacement ?: properties.itemsPlacement,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        margins = margins ?: properties.margins,
        minimumItemSize = minimumItemSize ?: properties.minimumItemSize,
        paddings = paddings ?: properties.paddings,
        pagerId = pagerId ?: properties.pagerId,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        shape = shape ?: properties.shape,
        spaceBetweenCenters = spaceBetweenCenters ?: properties.spaceBetweenCenters,
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param activeShape Active indicator shape.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
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
 * @param inactiveItemColor Indicator color.
 * @param inactiveMinimumShape Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
 * @param inactiveShape Indicator shape.
 * @param itemsPlacement Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param margins External margins from the element stroke.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param paddings Internal margins from the element stroke.
 * @param pagerId ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param shape Indicator shape.
 * @param spaceBetweenCenters Spacing between indicator centers.
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
fun Indicator.modify(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Property<Accessibility>? = null,
    activeItemColor: Property<Color>? = null,
    activeItemSize: Property<Double>? = null,
    activeShape: Property<RoundedRectangleShape>? = null,
    alignmentHorizontal: Property<AlignmentHorizontal>? = null,
    alignmentVertical: Property<AlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    animation: Property<Indicator.Animation>? = null,
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
    inactiveItemColor: Property<Color>? = null,
    inactiveMinimumShape: Property<RoundedRectangleShape>? = null,
    inactiveShape: Property<RoundedRectangleShape>? = null,
    itemsPlacement: Property<IndicatorItemPlacement>? = null,
    layoutProvider: Property<LayoutProvider>? = null,
    margins: Property<EdgeInsets>? = null,
    minimumItemSize: Property<Double>? = null,
    paddings: Property<EdgeInsets>? = null,
    pagerId: Property<String>? = null,
    reuseId: Property<String>? = null,
    rowSpan: Property<Int>? = null,
    selectedActions: Property<List<Action>>? = null,
    shape: Property<Shape>? = null,
    spaceBetweenCenters: Property<FixedSize>? = null,
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
): Indicator = Indicator(
    Indicator.Properties(
        accessibility = accessibility ?: properties.accessibility,
        activeItemColor = activeItemColor ?: properties.activeItemColor,
        activeItemSize = activeItemSize ?: properties.activeItemSize,
        activeShape = activeShape ?: properties.activeShape,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animation = animation ?: properties.animation,
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
        inactiveItemColor = inactiveItemColor ?: properties.inactiveItemColor,
        inactiveMinimumShape = inactiveMinimumShape ?: properties.inactiveMinimumShape,
        inactiveShape = inactiveShape ?: properties.inactiveShape,
        itemsPlacement = itemsPlacement ?: properties.itemsPlacement,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        margins = margins ?: properties.margins,
        minimumItemSize = minimumItemSize ?: properties.minimumItemSize,
        paddings = paddings ?: properties.paddings,
        pagerId = pagerId ?: properties.pagerId,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        shape = shape ?: properties.shape,
        spaceBetweenCenters = spaceBetweenCenters ?: properties.spaceBetweenCenters,
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param inactiveItemColor Indicator color.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Indicator.evaluate(
    `use named arguments`: Guard = Guard.instance,
    activeItemColor: ExpressionProperty<Color>? = null,
    activeItemSize: ExpressionProperty<Double>? = null,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    animation: ExpressionProperty<Indicator.Animation>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    inactiveItemColor: ExpressionProperty<Color>? = null,
    minimumItemSize: ExpressionProperty<Double>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Indicator = Indicator(
    Indicator.Properties(
        accessibility = properties.accessibility,
        activeItemColor = activeItemColor ?: properties.activeItemColor,
        activeItemSize = activeItemSize ?: properties.activeItemSize,
        activeShape = properties.activeShape,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animation = animation ?: properties.animation,
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
        inactiveItemColor = inactiveItemColor ?: properties.inactiveItemColor,
        inactiveMinimumShape = properties.inactiveMinimumShape,
        inactiveShape = properties.inactiveShape,
        itemsPlacement = properties.itemsPlacement,
        layoutProvider = properties.layoutProvider,
        margins = properties.margins,
        minimumItemSize = minimumItemSize ?: properties.minimumItemSize,
        paddings = properties.paddings,
        pagerId = properties.pagerId,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = properties.selectedActions,
        shape = properties.shape,
        spaceBetweenCenters = properties.spaceBetweenCenters,
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param activeShape Active indicator shape.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
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
 * @param inactiveItemColor Indicator color.
 * @param inactiveMinimumShape Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
 * @param inactiveShape Indicator shape.
 * @param itemsPlacement Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param margins External margins from the element stroke.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param paddings Internal margins from the element stroke.
 * @param pagerId ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param shape Indicator shape.
 * @param spaceBetweenCenters Spacing between indicator centers.
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
fun Component<Indicator>.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    activeItemColor: Color? = null,
    activeItemSize: Double? = null,
    activeShape: RoundedRectangleShape? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animation: Indicator.Animation? = null,
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
    inactiveItemColor: Color? = null,
    inactiveMinimumShape: RoundedRectangleShape? = null,
    inactiveShape: RoundedRectangleShape? = null,
    itemsPlacement: IndicatorItemPlacement? = null,
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    minimumItemSize: Double? = null,
    paddings: EdgeInsets? = null,
    pagerId: String? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    shape: Shape? = null,
    spaceBetweenCenters: FixedSize? = null,
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
): Component<Indicator> = Component(
    template = template,
    properties = Indicator.Properties(
        accessibility = valueOrNull(accessibility),
        activeItemColor = valueOrNull(activeItemColor),
        activeItemSize = valueOrNull(activeItemSize),
        activeShape = valueOrNull(activeShape),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        animation = valueOrNull(animation),
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
        inactiveItemColor = valueOrNull(inactiveItemColor),
        inactiveMinimumShape = valueOrNull(inactiveMinimumShape),
        inactiveShape = valueOrNull(inactiveShape),
        itemsPlacement = valueOrNull(itemsPlacement),
        layoutProvider = valueOrNull(layoutProvider),
        margins = valueOrNull(margins),
        minimumItemSize = valueOrNull(minimumItemSize),
        paddings = valueOrNull(paddings),
        pagerId = valueOrNull(pagerId),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        shape = valueOrNull(shape),
        spaceBetweenCenters = valueOrNull(spaceBetweenCenters),
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param activeShape Active indicator shape.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
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
 * @param inactiveItemColor Indicator color.
 * @param inactiveMinimumShape Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
 * @param inactiveShape Indicator shape.
 * @param itemsPlacement Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param margins External margins from the element stroke.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param paddings Internal margins from the element stroke.
 * @param pagerId ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param shape Indicator shape.
 * @param spaceBetweenCenters Spacing between indicator centers.
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
fun Component<Indicator>.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    activeItemColor: ReferenceProperty<Color>? = null,
    activeItemSize: ReferenceProperty<Double>? = null,
    activeShape: ReferenceProperty<RoundedRectangleShape>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animation: ReferenceProperty<Indicator.Animation>? = null,
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
    inactiveItemColor: ReferenceProperty<Color>? = null,
    inactiveMinimumShape: ReferenceProperty<RoundedRectangleShape>? = null,
    inactiveShape: ReferenceProperty<RoundedRectangleShape>? = null,
    itemsPlacement: ReferenceProperty<IndicatorItemPlacement>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    minimumItemSize: ReferenceProperty<Double>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    pagerId: ReferenceProperty<String>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    shape: ReferenceProperty<Shape>? = null,
    spaceBetweenCenters: ReferenceProperty<FixedSize>? = null,
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
): Component<Indicator> = Component(
    template = template,
    properties = Indicator.Properties(
        accessibility = accessibility,
        activeItemColor = activeItemColor,
        activeItemSize = activeItemSize,
        activeShape = activeShape,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animation = animation,
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
        inactiveItemColor = inactiveItemColor,
        inactiveMinimumShape = inactiveMinimumShape,
        inactiveShape = inactiveShape,
        itemsPlacement = itemsPlacement,
        layoutProvider = layoutProvider,
        margins = margins,
        minimumItemSize = minimumItemSize,
        paddings = paddings,
        pagerId = pagerId,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        shape = shape,
        spaceBetweenCenters = spaceBetweenCenters,
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param inactiveItemColor Indicator color.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Indicator>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    activeItemColor: ExpressionProperty<Color>? = null,
    activeItemSize: ExpressionProperty<Double>? = null,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    animation: ExpressionProperty<Indicator.Animation>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    inactiveItemColor: ExpressionProperty<Color>? = null,
    minimumItemSize: ExpressionProperty<Double>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Indicator> = Component(
    template = template,
    properties = Indicator.Properties(
        accessibility = null,
        activeItemColor = activeItemColor,
        activeItemSize = activeItemSize,
        activeShape = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animation = animation,
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
        inactiveItemColor = inactiveItemColor,
        inactiveMinimumShape = null,
        inactiveShape = null,
        itemsPlacement = null,
        layoutProvider = null,
        margins = null,
        minimumItemSize = minimumItemSize,
        paddings = null,
        pagerId = null,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = null,
        shape = null,
        spaceBetweenCenters = null,
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
 * @param activeItemColor Active indicator color.
 * @param activeItemSize A size multiplier for an active indicator.
 * @param activeShape Active indicator shape.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param animation Animation of switching between indicators.
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
 * @param inactiveItemColor Indicator color.
 * @param inactiveMinimumShape Inactive indicator shape, minimum size. Used when all the indicators don't fit on the screen.
 * @param inactiveShape Indicator shape.
 * @param itemsPlacement Indicator items placement mode:<li>Default: Indicators' width is fixed and defined by the `shape` parameters.</li><li>Stretch: Indicators are expanded to fill the entire width.</li>
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param margins External margins from the element stroke.
 * @param minimumItemSize A size multiplier for a minimal indicator. It is used when the required number of indicators don't fit on the screen.
 * @param paddings Internal margins from the element stroke.
 * @param pagerId ID of the pager that is a data source for an indicator. If not specified, the indicator will automatically find the nearest pager.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param shape Indicator shape.
 * @param spaceBetweenCenters Spacing between indicator centers.
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
fun Component<Indicator>.modify(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Property<Accessibility>? = null,
    activeItemColor: Property<Color>? = null,
    activeItemSize: Property<Double>? = null,
    activeShape: Property<RoundedRectangleShape>? = null,
    alignmentHorizontal: Property<AlignmentHorizontal>? = null,
    alignmentVertical: Property<AlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    animation: Property<Indicator.Animation>? = null,
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
    inactiveItemColor: Property<Color>? = null,
    inactiveMinimumShape: Property<RoundedRectangleShape>? = null,
    inactiveShape: Property<RoundedRectangleShape>? = null,
    itemsPlacement: Property<IndicatorItemPlacement>? = null,
    layoutProvider: Property<LayoutProvider>? = null,
    margins: Property<EdgeInsets>? = null,
    minimumItemSize: Property<Double>? = null,
    paddings: Property<EdgeInsets>? = null,
    pagerId: Property<String>? = null,
    reuseId: Property<String>? = null,
    rowSpan: Property<Int>? = null,
    selectedActions: Property<List<Action>>? = null,
    shape: Property<Shape>? = null,
    spaceBetweenCenters: Property<FixedSize>? = null,
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
): Component<Indicator> = Component(
    template = template,
    properties = Indicator.Properties(
        accessibility = accessibility,
        activeItemColor = activeItemColor,
        activeItemSize = activeItemSize,
        activeShape = activeShape,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animation = animation,
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
        inactiveItemColor = inactiveItemColor,
        inactiveMinimumShape = inactiveMinimumShape,
        inactiveShape = inactiveShape,
        itemsPlacement = itemsPlacement,
        layoutProvider = layoutProvider,
        margins = margins,
        minimumItemSize = minimumItemSize,
        paddings = paddings,
        pagerId = pagerId,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        shape = shape,
        spaceBetweenCenters = spaceBetweenCenters,
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
operator fun Component<Indicator>.plus(additive: Indicator.Properties): Component<Indicator> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Indicator.asList() = listOf(this)

@Generated
fun Indicator.Animation.asList() = listOf(this)
