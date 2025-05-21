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
 * Slider for selecting a value in the range.
 * 
 * Can be created using the method [slider].
 * 
 * Required parameters: `type, track_inactive_style, track_active_style, thumb_style`.
 */
@Generated
data class Slider internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "slider")
    )

    operator fun plus(additive: Properties): Slider = Slider(
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
            layoutProvider = additive.layoutProvider ?: properties.layoutProvider,
            margins = additive.margins ?: properties.margins,
            maxValue = additive.maxValue ?: properties.maxValue,
            minValue = additive.minValue ?: properties.minValue,
            paddings = additive.paddings ?: properties.paddings,
            ranges = additive.ranges ?: properties.ranges,
            reuseId = additive.reuseId ?: properties.reuseId,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            secondaryValueAccessibility = additive.secondaryValueAccessibility ?: properties.secondaryValueAccessibility,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            thumbSecondaryStyle = additive.thumbSecondaryStyle ?: properties.thumbSecondaryStyle,
            thumbSecondaryTextStyle = additive.thumbSecondaryTextStyle ?: properties.thumbSecondaryTextStyle,
            thumbSecondaryValueVariable = additive.thumbSecondaryValueVariable ?: properties.thumbSecondaryValueVariable,
            thumbStyle = additive.thumbStyle ?: properties.thumbStyle,
            thumbTextStyle = additive.thumbTextStyle ?: properties.thumbTextStyle,
            thumbValueVariable = additive.thumbValueVariable ?: properties.thumbValueVariable,
            tickMarkActiveStyle = additive.tickMarkActiveStyle ?: properties.tickMarkActiveStyle,
            tickMarkInactiveStyle = additive.tickMarkInactiveStyle ?: properties.tickMarkInactiveStyle,
            tooltips = additive.tooltips ?: properties.tooltips,
            trackActiveStyle = additive.trackActiveStyle ?: properties.trackActiveStyle,
            trackInactiveStyle = additive.trackInactiveStyle ?: properties.trackInactiveStyle,
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
         * Enables or disables toggling values by clicking or swiping.
         * Default value: `true`.
         */
        val isEnabled: Property<Boolean>?,
        /**
         * Provides data on the actual size of the element.
         */
        val layoutProvider: Property<LayoutProvider>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Maximum value. It must be greater than the minimum value.
         * Default value: `100`.
         */
        val maxValue: Property<Int>?,
        /**
         * Minimum value.
         * Default value: `0`.
         */
        val minValue: Property<Int>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
        /**
         * Section style.
         */
        val ranges: Property<List<Range>>?,
        /**
         * ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
         */
        val reuseId: Property<String>?,
        /**
         * Merges cells in a string of the [grid](div-grid.md) element.
         */
        val rowSpan: Property<Int>?,
        /**
         * Accessibility settings for the second pointer.
         */
        val secondaryValueAccessibility: Property<Accessibility>?,
        /**
         * List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
         */
        val selectedActions: Property<List<Action>>?,
        /**
         * Style of the second pointer.
         */
        val thumbSecondaryStyle: Property<Drawable>?,
        /**
         * Text style in the second pointer.
         */
        val thumbSecondaryTextStyle: Property<TextStyle>?,
        /**
         * Name of the variable to store the second pointer's current value.
         */
        val thumbSecondaryValueVariable: Property<String>?,
        /**
         * Style of the first pointer.
         */
        val thumbStyle: Property<Drawable>?,
        /**
         * Text style in the first pointer.
         */
        val thumbTextStyle: Property<TextStyle>?,
        /**
         * Name of the variable to store the pointer's current value.
         */
        val thumbValueVariable: Property<String>?,
        /**
         * Style of active serifs.
         */
        val tickMarkActiveStyle: Property<Drawable>?,
        /**
         * Style of inactive serifs.
         */
        val tickMarkInactiveStyle: Property<Drawable>?,
        /**
         * Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
         */
        val tooltips: Property<List<Tooltip>>?,
        /**
         * Style of the active part of a scale.
         */
        val trackActiveStyle: Property<Drawable>?,
        /**
         * Style of the inactive part of a scale.
         */
        val trackInactiveStyle: Property<Drawable>?,
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
            result.tryPutProperty("layout_provider", layoutProvider)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("max_value", maxValue)
            result.tryPutProperty("min_value", minValue)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("ranges", ranges)
            result.tryPutProperty("reuse_id", reuseId)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("secondary_value_accessibility", secondaryValueAccessibility)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("thumb_secondary_style", thumbSecondaryStyle)
            result.tryPutProperty("thumb_secondary_text_style", thumbSecondaryTextStyle)
            result.tryPutProperty("thumb_secondary_value_variable", thumbSecondaryValueVariable)
            result.tryPutProperty("thumb_style", thumbStyle)
            result.tryPutProperty("thumb_text_style", thumbTextStyle)
            result.tryPutProperty("thumb_value_variable", thumbValueVariable)
            result.tryPutProperty("tick_mark_active_style", tickMarkActiveStyle)
            result.tryPutProperty("tick_mark_inactive_style", tickMarkInactiveStyle)
            result.tryPutProperty("tooltips", tooltips)
            result.tryPutProperty("track_active_style", trackActiveStyle)
            result.tryPutProperty("track_inactive_style", trackInactiveStyle)
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
     * Can be created using the method [sliderRange].
     */
    @Generated
    data class Range internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Range = Range(
            Properties(
                end = additive.end ?: properties.end,
                margins = additive.margins ?: properties.margins,
                start = additive.start ?: properties.start,
                trackActiveStyle = additive.trackActiveStyle ?: properties.trackActiveStyle,
                trackInactiveStyle = additive.trackInactiveStyle ?: properties.trackInactiveStyle,
            )
        )

        data class Properties internal constructor(
            /**
             * End of section.
             */
            val end: Property<Int>?,
            /**
             * Section margins. Only uses horizontal margins.
             */
            val margins: Property<EdgeInsets>?,
            /**
             * Start of section.
             */
            val start: Property<Int>?,
            /**
             * Style of the active part of a scale.
             */
            val trackActiveStyle: Property<Drawable>?,
            /**
             * Style of the inactive part of a scale.
             */
            val trackInactiveStyle: Property<Drawable>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("end", end)
                result.tryPutProperty("margins", margins)
                result.tryPutProperty("start", start)
                result.tryPutProperty("track_active_style", trackActiveStyle)
                result.tryPutProperty("track_inactive_style", trackInactiveStyle)
                return result
            }
        }
    }


    /**
     * Can be created using the method [sliderTextStyle].
     * 
     * Required parameters: `font_size`.
     */
    @Generated
    data class TextStyle internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): TextStyle = TextStyle(
            Properties(
                fontFamily = additive.fontFamily ?: properties.fontFamily,
                fontSize = additive.fontSize ?: properties.fontSize,
                fontSizeUnit = additive.fontSizeUnit ?: properties.fontSizeUnit,
                fontVariationSettings = additive.fontVariationSettings ?: properties.fontVariationSettings,
                fontWeight = additive.fontWeight ?: properties.fontWeight,
                fontWeightValue = additive.fontWeightValue ?: properties.fontWeightValue,
                letterSpacing = additive.letterSpacing ?: properties.letterSpacing,
                offset = additive.offset ?: properties.offset,
                textColor = additive.textColor ?: properties.textColor,
            )
        )

        data class Properties internal constructor(
            /**
             * Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
             */
            val fontFamily: Property<String>?,
            /**
             * Font size.
             * Default value: `12`.
             */
            val fontSize: Property<Int>?,
            /**
             * Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
             * Default value: `sp`.
             */
            val fontSizeUnit: Property<SizeUnit>?,
            /**
             * List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
             */
            val fontVariationSettings: Property<Map<String, Any>>?,
            /**
             * Style.
             * Default value: `regular`.
             */
            val fontWeight: Property<FontWeight>?,
            /**
             * Style. Numeric value.
             */
            val fontWeightValue: Property<Int>?,
            /**
             * Spacing between characters.
             * Default value: `0`.
             */
            val letterSpacing: Property<Double>?,
            /**
             * Shift relative to the center.
             */
            val offset: Property<Point>?,
            /**
             * Text color.
             * Default value: `#FF000000`.
             */
            val textColor: Property<Color>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("font_family", fontFamily)
                result.tryPutProperty("font_size", fontSize)
                result.tryPutProperty("font_size_unit", fontSizeUnit)
                result.tryPutProperty("font_variation_settings", fontVariationSettings)
                result.tryPutProperty("font_weight", fontWeight)
                result.tryPutProperty("font_weight_value", fontWeightValue)
                result.tryPutProperty("letter_spacing", letterSpacing)
                result.tryPutProperty("offset", offset)
                result.tryPutProperty("text_color", textColor)
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
 * @param isEnabled Enables or disables toggling values by clicking or swiping.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param maxValue Maximum value. It must be greater than the minimum value.
 * @param minValue Minimum value.
 * @param paddings Internal margins from the element stroke.
 * @param ranges Section style.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param secondaryValueAccessibility Accessibility settings for the second pointer.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param thumbSecondaryStyle Style of the second pointer.
 * @param thumbSecondaryTextStyle Text style in the second pointer.
 * @param thumbSecondaryValueVariable Name of the variable to store the second pointer's current value.
 * @param thumbStyle Style of the first pointer.
 * @param thumbTextStyle Text style in the first pointer.
 * @param thumbValueVariable Name of the variable to store the pointer's current value.
 * @param tickMarkActiveStyle Style of active serifs.
 * @param tickMarkInactiveStyle Style of inactive serifs.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
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
fun DivScope.slider(
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
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    maxValue: Int? = null,
    minValue: Int? = null,
    paddings: EdgeInsets? = null,
    ranges: List<Slider.Range>? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    secondaryValueAccessibility: Accessibility? = null,
    selectedActions: List<Action>? = null,
    thumbSecondaryStyle: Drawable? = null,
    thumbSecondaryTextStyle: Slider.TextStyle? = null,
    thumbSecondaryValueVariable: String? = null,
    thumbStyle: Drawable? = null,
    thumbTextStyle: Slider.TextStyle? = null,
    thumbValueVariable: String? = null,
    tickMarkActiveStyle: Drawable? = null,
    tickMarkInactiveStyle: Drawable? = null,
    tooltips: List<Tooltip>? = null,
    trackActiveStyle: Drawable? = null,
    trackInactiveStyle: Drawable? = null,
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
): Slider = Slider(
    Slider.Properties(
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
        layoutProvider = valueOrNull(layoutProvider),
        margins = valueOrNull(margins),
        maxValue = valueOrNull(maxValue),
        minValue = valueOrNull(minValue),
        paddings = valueOrNull(paddings),
        ranges = valueOrNull(ranges),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        secondaryValueAccessibility = valueOrNull(secondaryValueAccessibility),
        selectedActions = valueOrNull(selectedActions),
        thumbSecondaryStyle = valueOrNull(thumbSecondaryStyle),
        thumbSecondaryTextStyle = valueOrNull(thumbSecondaryTextStyle),
        thumbSecondaryValueVariable = valueOrNull(thumbSecondaryValueVariable),
        thumbStyle = valueOrNull(thumbStyle),
        thumbTextStyle = valueOrNull(thumbTextStyle),
        thumbValueVariable = valueOrNull(thumbValueVariable),
        tickMarkActiveStyle = valueOrNull(tickMarkActiveStyle),
        tickMarkInactiveStyle = valueOrNull(tickMarkInactiveStyle),
        tooltips = valueOrNull(tooltips),
        trackActiveStyle = valueOrNull(trackActiveStyle),
        trackInactiveStyle = valueOrNull(trackInactiveStyle),
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
 * @param isEnabled Enables or disables toggling values by clicking or swiping.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param maxValue Maximum value. It must be greater than the minimum value.
 * @param minValue Minimum value.
 * @param paddings Internal margins from the element stroke.
 * @param ranges Section style.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param secondaryValueAccessibility Accessibility settings for the second pointer.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param thumbSecondaryStyle Style of the second pointer.
 * @param thumbSecondaryTextStyle Text style in the second pointer.
 * @param thumbSecondaryValueVariable Name of the variable to store the second pointer's current value.
 * @param thumbStyle Style of the first pointer.
 * @param thumbTextStyle Text style in the first pointer.
 * @param thumbValueVariable Name of the variable to store the pointer's current value.
 * @param tickMarkActiveStyle Style of active serifs.
 * @param tickMarkInactiveStyle Style of inactive serifs.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
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
fun DivScope.sliderProps(
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
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    maxValue: Int? = null,
    minValue: Int? = null,
    paddings: EdgeInsets? = null,
    ranges: List<Slider.Range>? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    secondaryValueAccessibility: Accessibility? = null,
    selectedActions: List<Action>? = null,
    thumbSecondaryStyle: Drawable? = null,
    thumbSecondaryTextStyle: Slider.TextStyle? = null,
    thumbSecondaryValueVariable: String? = null,
    thumbStyle: Drawable? = null,
    thumbTextStyle: Slider.TextStyle? = null,
    thumbValueVariable: String? = null,
    tickMarkActiveStyle: Drawable? = null,
    tickMarkInactiveStyle: Drawable? = null,
    tooltips: List<Tooltip>? = null,
    trackActiveStyle: Drawable? = null,
    trackInactiveStyle: Drawable? = null,
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
) = Slider.Properties(
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
    layoutProvider = valueOrNull(layoutProvider),
    margins = valueOrNull(margins),
    maxValue = valueOrNull(maxValue),
    minValue = valueOrNull(minValue),
    paddings = valueOrNull(paddings),
    ranges = valueOrNull(ranges),
    reuseId = valueOrNull(reuseId),
    rowSpan = valueOrNull(rowSpan),
    secondaryValueAccessibility = valueOrNull(secondaryValueAccessibility),
    selectedActions = valueOrNull(selectedActions),
    thumbSecondaryStyle = valueOrNull(thumbSecondaryStyle),
    thumbSecondaryTextStyle = valueOrNull(thumbSecondaryTextStyle),
    thumbSecondaryValueVariable = valueOrNull(thumbSecondaryValueVariable),
    thumbStyle = valueOrNull(thumbStyle),
    thumbTextStyle = valueOrNull(thumbTextStyle),
    thumbValueVariable = valueOrNull(thumbValueVariable),
    tickMarkActiveStyle = valueOrNull(tickMarkActiveStyle),
    tickMarkInactiveStyle = valueOrNull(tickMarkInactiveStyle),
    tooltips = valueOrNull(tooltips),
    trackActiveStyle = valueOrNull(trackActiveStyle),
    trackInactiveStyle = valueOrNull(trackInactiveStyle),
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
 * @param isEnabled Enables or disables toggling values by clicking or swiping.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param maxValue Maximum value. It must be greater than the minimum value.
 * @param minValue Minimum value.
 * @param paddings Internal margins from the element stroke.
 * @param ranges Section style.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param secondaryValueAccessibility Accessibility settings for the second pointer.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param thumbSecondaryStyle Style of the second pointer.
 * @param thumbSecondaryTextStyle Text style in the second pointer.
 * @param thumbSecondaryValueVariable Name of the variable to store the second pointer's current value.
 * @param thumbStyle Style of the first pointer.
 * @param thumbTextStyle Text style in the first pointer.
 * @param thumbValueVariable Name of the variable to store the pointer's current value.
 * @param tickMarkActiveStyle Style of active serifs.
 * @param tickMarkInactiveStyle Style of inactive serifs.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
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
fun TemplateScope.sliderRefs(
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
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    maxValue: ReferenceProperty<Int>? = null,
    minValue: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    ranges: ReferenceProperty<List<Slider.Range>>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    secondaryValueAccessibility: ReferenceProperty<Accessibility>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    thumbSecondaryStyle: ReferenceProperty<Drawable>? = null,
    thumbSecondaryTextStyle: ReferenceProperty<Slider.TextStyle>? = null,
    thumbSecondaryValueVariable: ReferenceProperty<String>? = null,
    thumbStyle: ReferenceProperty<Drawable>? = null,
    thumbTextStyle: ReferenceProperty<Slider.TextStyle>? = null,
    thumbValueVariable: ReferenceProperty<String>? = null,
    tickMarkActiveStyle: ReferenceProperty<Drawable>? = null,
    tickMarkInactiveStyle: ReferenceProperty<Drawable>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    trackActiveStyle: ReferenceProperty<Drawable>? = null,
    trackInactiveStyle: ReferenceProperty<Drawable>? = null,
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
) = Slider.Properties(
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
    layoutProvider = layoutProvider,
    margins = margins,
    maxValue = maxValue,
    minValue = minValue,
    paddings = paddings,
    ranges = ranges,
    reuseId = reuseId,
    rowSpan = rowSpan,
    secondaryValueAccessibility = secondaryValueAccessibility,
    selectedActions = selectedActions,
    thumbSecondaryStyle = thumbSecondaryStyle,
    thumbSecondaryTextStyle = thumbSecondaryTextStyle,
    thumbSecondaryValueVariable = thumbSecondaryValueVariable,
    thumbStyle = thumbStyle,
    thumbTextStyle = thumbTextStyle,
    thumbValueVariable = thumbValueVariable,
    tickMarkActiveStyle = tickMarkActiveStyle,
    tickMarkInactiveStyle = tickMarkInactiveStyle,
    tooltips = tooltips,
    trackActiveStyle = trackActiveStyle,
    trackInactiveStyle = trackInactiveStyle,
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
 * @param isEnabled Enables or disables toggling values by clicking or swiping.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param maxValue Maximum value. It must be greater than the minimum value.
 * @param minValue Minimum value.
 * @param paddings Internal margins from the element stroke.
 * @param ranges Section style.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param secondaryValueAccessibility Accessibility settings for the second pointer.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param thumbSecondaryStyle Style of the second pointer.
 * @param thumbSecondaryTextStyle Text style in the second pointer.
 * @param thumbSecondaryValueVariable Name of the variable to store the second pointer's current value.
 * @param thumbStyle Style of the first pointer.
 * @param thumbTextStyle Text style in the first pointer.
 * @param thumbValueVariable Name of the variable to store the pointer's current value.
 * @param tickMarkActiveStyle Style of active serifs.
 * @param tickMarkInactiveStyle Style of inactive serifs.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
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
fun Slider.override(
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
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    maxValue: Int? = null,
    minValue: Int? = null,
    paddings: EdgeInsets? = null,
    ranges: List<Slider.Range>? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    secondaryValueAccessibility: Accessibility? = null,
    selectedActions: List<Action>? = null,
    thumbSecondaryStyle: Drawable? = null,
    thumbSecondaryTextStyle: Slider.TextStyle? = null,
    thumbSecondaryValueVariable: String? = null,
    thumbStyle: Drawable? = null,
    thumbTextStyle: Slider.TextStyle? = null,
    thumbValueVariable: String? = null,
    tickMarkActiveStyle: Drawable? = null,
    tickMarkInactiveStyle: Drawable? = null,
    tooltips: List<Tooltip>? = null,
    trackActiveStyle: Drawable? = null,
    trackInactiveStyle: Drawable? = null,
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
): Slider = Slider(
    Slider.Properties(
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
        layoutProvider = valueOrNull(layoutProvider) ?: properties.layoutProvider,
        margins = valueOrNull(margins) ?: properties.margins,
        maxValue = valueOrNull(maxValue) ?: properties.maxValue,
        minValue = valueOrNull(minValue) ?: properties.minValue,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        ranges = valueOrNull(ranges) ?: properties.ranges,
        reuseId = valueOrNull(reuseId) ?: properties.reuseId,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        secondaryValueAccessibility = valueOrNull(secondaryValueAccessibility) ?: properties.secondaryValueAccessibility,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        thumbSecondaryStyle = valueOrNull(thumbSecondaryStyle) ?: properties.thumbSecondaryStyle,
        thumbSecondaryTextStyle = valueOrNull(thumbSecondaryTextStyle) ?: properties.thumbSecondaryTextStyle,
        thumbSecondaryValueVariable = valueOrNull(thumbSecondaryValueVariable) ?: properties.thumbSecondaryValueVariable,
        thumbStyle = valueOrNull(thumbStyle) ?: properties.thumbStyle,
        thumbTextStyle = valueOrNull(thumbTextStyle) ?: properties.thumbTextStyle,
        thumbValueVariable = valueOrNull(thumbValueVariable) ?: properties.thumbValueVariable,
        tickMarkActiveStyle = valueOrNull(tickMarkActiveStyle) ?: properties.tickMarkActiveStyle,
        tickMarkInactiveStyle = valueOrNull(tickMarkInactiveStyle) ?: properties.tickMarkInactiveStyle,
        tooltips = valueOrNull(tooltips) ?: properties.tooltips,
        trackActiveStyle = valueOrNull(trackActiveStyle) ?: properties.trackActiveStyle,
        trackInactiveStyle = valueOrNull(trackInactiveStyle) ?: properties.trackInactiveStyle,
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
 * @param isEnabled Enables or disables toggling values by clicking or swiping.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param maxValue Maximum value. It must be greater than the minimum value.
 * @param minValue Minimum value.
 * @param paddings Internal margins from the element stroke.
 * @param ranges Section style.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param secondaryValueAccessibility Accessibility settings for the second pointer.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param thumbSecondaryStyle Style of the second pointer.
 * @param thumbSecondaryTextStyle Text style in the second pointer.
 * @param thumbSecondaryValueVariable Name of the variable to store the second pointer's current value.
 * @param thumbStyle Style of the first pointer.
 * @param thumbTextStyle Text style in the first pointer.
 * @param thumbValueVariable Name of the variable to store the pointer's current value.
 * @param tickMarkActiveStyle Style of active serifs.
 * @param tickMarkInactiveStyle Style of inactive serifs.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
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
fun Slider.defer(
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
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    maxValue: ReferenceProperty<Int>? = null,
    minValue: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    ranges: ReferenceProperty<List<Slider.Range>>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    secondaryValueAccessibility: ReferenceProperty<Accessibility>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    thumbSecondaryStyle: ReferenceProperty<Drawable>? = null,
    thumbSecondaryTextStyle: ReferenceProperty<Slider.TextStyle>? = null,
    thumbSecondaryValueVariable: ReferenceProperty<String>? = null,
    thumbStyle: ReferenceProperty<Drawable>? = null,
    thumbTextStyle: ReferenceProperty<Slider.TextStyle>? = null,
    thumbValueVariable: ReferenceProperty<String>? = null,
    tickMarkActiveStyle: ReferenceProperty<Drawable>? = null,
    tickMarkInactiveStyle: ReferenceProperty<Drawable>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    trackActiveStyle: ReferenceProperty<Drawable>? = null,
    trackInactiveStyle: ReferenceProperty<Drawable>? = null,
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
): Slider = Slider(
    Slider.Properties(
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
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        margins = margins ?: properties.margins,
        maxValue = maxValue ?: properties.maxValue,
        minValue = minValue ?: properties.minValue,
        paddings = paddings ?: properties.paddings,
        ranges = ranges ?: properties.ranges,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        secondaryValueAccessibility = secondaryValueAccessibility ?: properties.secondaryValueAccessibility,
        selectedActions = selectedActions ?: properties.selectedActions,
        thumbSecondaryStyle = thumbSecondaryStyle ?: properties.thumbSecondaryStyle,
        thumbSecondaryTextStyle = thumbSecondaryTextStyle ?: properties.thumbSecondaryTextStyle,
        thumbSecondaryValueVariable = thumbSecondaryValueVariable ?: properties.thumbSecondaryValueVariable,
        thumbStyle = thumbStyle ?: properties.thumbStyle,
        thumbTextStyle = thumbTextStyle ?: properties.thumbTextStyle,
        thumbValueVariable = thumbValueVariable ?: properties.thumbValueVariable,
        tickMarkActiveStyle = tickMarkActiveStyle ?: properties.tickMarkActiveStyle,
        tickMarkInactiveStyle = tickMarkInactiveStyle ?: properties.tickMarkInactiveStyle,
        tooltips = tooltips ?: properties.tooltips,
        trackActiveStyle = trackActiveStyle ?: properties.trackActiveStyle,
        trackInactiveStyle = trackInactiveStyle ?: properties.trackInactiveStyle,
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
 * @param isEnabled Enables or disables toggling values by clicking or swiping.
 * @param maxValue Maximum value. It must be greater than the minimum value.
 * @param minValue Minimum value.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Slider.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    isEnabled: ExpressionProperty<Boolean>? = null,
    maxValue: ExpressionProperty<Int>? = null,
    minValue: ExpressionProperty<Int>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Slider = Slider(
    Slider.Properties(
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
        layoutProvider = properties.layoutProvider,
        margins = properties.margins,
        maxValue = maxValue ?: properties.maxValue,
        minValue = minValue ?: properties.minValue,
        paddings = properties.paddings,
        ranges = properties.ranges,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        secondaryValueAccessibility = properties.secondaryValueAccessibility,
        selectedActions = properties.selectedActions,
        thumbSecondaryStyle = properties.thumbSecondaryStyle,
        thumbSecondaryTextStyle = properties.thumbSecondaryTextStyle,
        thumbSecondaryValueVariable = properties.thumbSecondaryValueVariable,
        thumbStyle = properties.thumbStyle,
        thumbTextStyle = properties.thumbTextStyle,
        thumbValueVariable = properties.thumbValueVariable,
        tickMarkActiveStyle = properties.tickMarkActiveStyle,
        tickMarkInactiveStyle = properties.tickMarkInactiveStyle,
        tooltips = properties.tooltips,
        trackActiveStyle = properties.trackActiveStyle,
        trackInactiveStyle = properties.trackInactiveStyle,
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
 * @param isEnabled Enables or disables toggling values by clicking or swiping.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param maxValue Maximum value. It must be greater than the minimum value.
 * @param minValue Minimum value.
 * @param paddings Internal margins from the element stroke.
 * @param ranges Section style.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param secondaryValueAccessibility Accessibility settings for the second pointer.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param thumbSecondaryStyle Style of the second pointer.
 * @param thumbSecondaryTextStyle Text style in the second pointer.
 * @param thumbSecondaryValueVariable Name of the variable to store the second pointer's current value.
 * @param thumbStyle Style of the first pointer.
 * @param thumbTextStyle Text style in the first pointer.
 * @param thumbValueVariable Name of the variable to store the pointer's current value.
 * @param tickMarkActiveStyle Style of active serifs.
 * @param tickMarkInactiveStyle Style of inactive serifs.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
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
fun Component<Slider>.override(
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
    layoutProvider: LayoutProvider? = null,
    margins: EdgeInsets? = null,
    maxValue: Int? = null,
    minValue: Int? = null,
    paddings: EdgeInsets? = null,
    ranges: List<Slider.Range>? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    secondaryValueAccessibility: Accessibility? = null,
    selectedActions: List<Action>? = null,
    thumbSecondaryStyle: Drawable? = null,
    thumbSecondaryTextStyle: Slider.TextStyle? = null,
    thumbSecondaryValueVariable: String? = null,
    thumbStyle: Drawable? = null,
    thumbTextStyle: Slider.TextStyle? = null,
    thumbValueVariable: String? = null,
    tickMarkActiveStyle: Drawable? = null,
    tickMarkInactiveStyle: Drawable? = null,
    tooltips: List<Tooltip>? = null,
    trackActiveStyle: Drawable? = null,
    trackInactiveStyle: Drawable? = null,
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
): Component<Slider> = Component(
    template = template,
    properties = Slider.Properties(
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
        layoutProvider = valueOrNull(layoutProvider),
        margins = valueOrNull(margins),
        maxValue = valueOrNull(maxValue),
        minValue = valueOrNull(minValue),
        paddings = valueOrNull(paddings),
        ranges = valueOrNull(ranges),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        secondaryValueAccessibility = valueOrNull(secondaryValueAccessibility),
        selectedActions = valueOrNull(selectedActions),
        thumbSecondaryStyle = valueOrNull(thumbSecondaryStyle),
        thumbSecondaryTextStyle = valueOrNull(thumbSecondaryTextStyle),
        thumbSecondaryValueVariable = valueOrNull(thumbSecondaryValueVariable),
        thumbStyle = valueOrNull(thumbStyle),
        thumbTextStyle = valueOrNull(thumbTextStyle),
        thumbValueVariable = valueOrNull(thumbValueVariable),
        tickMarkActiveStyle = valueOrNull(tickMarkActiveStyle),
        tickMarkInactiveStyle = valueOrNull(tickMarkInactiveStyle),
        tooltips = valueOrNull(tooltips),
        trackActiveStyle = valueOrNull(trackActiveStyle),
        trackInactiveStyle = valueOrNull(trackInactiveStyle),
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
 * @param isEnabled Enables or disables toggling values by clicking or swiping.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param margins External margins from the element stroke.
 * @param maxValue Maximum value. It must be greater than the minimum value.
 * @param minValue Minimum value.
 * @param paddings Internal margins from the element stroke.
 * @param ranges Section style.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param secondaryValueAccessibility Accessibility settings for the second pointer.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param thumbSecondaryStyle Style of the second pointer.
 * @param thumbSecondaryTextStyle Text style in the second pointer.
 * @param thumbSecondaryValueVariable Name of the variable to store the second pointer's current value.
 * @param thumbStyle Style of the first pointer.
 * @param thumbTextStyle Text style in the first pointer.
 * @param thumbValueVariable Name of the variable to store the pointer's current value.
 * @param tickMarkActiveStyle Style of active serifs.
 * @param tickMarkInactiveStyle Style of inactive serifs.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
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
fun Component<Slider>.defer(
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
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    maxValue: ReferenceProperty<Int>? = null,
    minValue: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    ranges: ReferenceProperty<List<Slider.Range>>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    secondaryValueAccessibility: ReferenceProperty<Accessibility>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    thumbSecondaryStyle: ReferenceProperty<Drawable>? = null,
    thumbSecondaryTextStyle: ReferenceProperty<Slider.TextStyle>? = null,
    thumbSecondaryValueVariable: ReferenceProperty<String>? = null,
    thumbStyle: ReferenceProperty<Drawable>? = null,
    thumbTextStyle: ReferenceProperty<Slider.TextStyle>? = null,
    thumbValueVariable: ReferenceProperty<String>? = null,
    tickMarkActiveStyle: ReferenceProperty<Drawable>? = null,
    tickMarkInactiveStyle: ReferenceProperty<Drawable>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    trackActiveStyle: ReferenceProperty<Drawable>? = null,
    trackInactiveStyle: ReferenceProperty<Drawable>? = null,
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
): Component<Slider> = Component(
    template = template,
    properties = Slider.Properties(
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
        layoutProvider = layoutProvider,
        margins = margins,
        maxValue = maxValue,
        minValue = minValue,
        paddings = paddings,
        ranges = ranges,
        reuseId = reuseId,
        rowSpan = rowSpan,
        secondaryValueAccessibility = secondaryValueAccessibility,
        selectedActions = selectedActions,
        thumbSecondaryStyle = thumbSecondaryStyle,
        thumbSecondaryTextStyle = thumbSecondaryTextStyle,
        thumbSecondaryValueVariable = thumbSecondaryValueVariable,
        thumbStyle = thumbStyle,
        thumbTextStyle = thumbTextStyle,
        thumbValueVariable = thumbValueVariable,
        tickMarkActiveStyle = tickMarkActiveStyle,
        tickMarkInactiveStyle = tickMarkInactiveStyle,
        tooltips = tooltips,
        trackActiveStyle = trackActiveStyle,
        trackInactiveStyle = trackInactiveStyle,
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
 * @param isEnabled Enables or disables toggling values by clicking or swiping.
 * @param maxValue Maximum value. It must be greater than the minimum value.
 * @param minValue Minimum value.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Slider>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    isEnabled: ExpressionProperty<Boolean>? = null,
    maxValue: ExpressionProperty<Int>? = null,
    minValue: ExpressionProperty<Int>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Slider> = Component(
    template = template,
    properties = Slider.Properties(
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
        layoutProvider = null,
        margins = null,
        maxValue = maxValue,
        minValue = minValue,
        paddings = null,
        ranges = null,
        reuseId = reuseId,
        rowSpan = rowSpan,
        secondaryValueAccessibility = null,
        selectedActions = null,
        thumbSecondaryStyle = null,
        thumbSecondaryTextStyle = null,
        thumbSecondaryValueVariable = null,
        thumbStyle = null,
        thumbTextStyle = null,
        thumbValueVariable = null,
        tickMarkActiveStyle = null,
        tickMarkInactiveStyle = null,
        tooltips = null,
        trackActiveStyle = null,
        trackInactiveStyle = null,
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
operator fun Component<Slider>.plus(additive: Slider.Properties): Component<Slider> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Slider.asList() = listOf(this)

/**
 * @param end End of section.
 * @param margins Section margins. Only uses horizontal margins.
 * @param start Start of section.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
 */
@Generated
fun DivScope.sliderRange(
    `use named arguments`: Guard = Guard.instance,
    end: Int? = null,
    margins: EdgeInsets? = null,
    start: Int? = null,
    trackActiveStyle: Drawable? = null,
    trackInactiveStyle: Drawable? = null,
): Slider.Range = Slider.Range(
    Slider.Range.Properties(
        end = valueOrNull(end),
        margins = valueOrNull(margins),
        start = valueOrNull(start),
        trackActiveStyle = valueOrNull(trackActiveStyle),
        trackInactiveStyle = valueOrNull(trackInactiveStyle),
    )
)

/**
 * @param end End of section.
 * @param margins Section margins. Only uses horizontal margins.
 * @param start Start of section.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
 */
@Generated
fun DivScope.sliderRangeProps(
    `use named arguments`: Guard = Guard.instance,
    end: Int? = null,
    margins: EdgeInsets? = null,
    start: Int? = null,
    trackActiveStyle: Drawable? = null,
    trackInactiveStyle: Drawable? = null,
) = Slider.Range.Properties(
    end = valueOrNull(end),
    margins = valueOrNull(margins),
    start = valueOrNull(start),
    trackActiveStyle = valueOrNull(trackActiveStyle),
    trackInactiveStyle = valueOrNull(trackInactiveStyle),
)

/**
 * @param end End of section.
 * @param margins Section margins. Only uses horizontal margins.
 * @param start Start of section.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
 */
@Generated
fun TemplateScope.sliderRangeRefs(
    `use named arguments`: Guard = Guard.instance,
    end: ReferenceProperty<Int>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    start: ReferenceProperty<Int>? = null,
    trackActiveStyle: ReferenceProperty<Drawable>? = null,
    trackInactiveStyle: ReferenceProperty<Drawable>? = null,
) = Slider.Range.Properties(
    end = end,
    margins = margins,
    start = start,
    trackActiveStyle = trackActiveStyle,
    trackInactiveStyle = trackInactiveStyle,
)

/**
 * @param end End of section.
 * @param margins Section margins. Only uses horizontal margins.
 * @param start Start of section.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
 */
@Generated
fun Slider.Range.override(
    `use named arguments`: Guard = Guard.instance,
    end: Int? = null,
    margins: EdgeInsets? = null,
    start: Int? = null,
    trackActiveStyle: Drawable? = null,
    trackInactiveStyle: Drawable? = null,
): Slider.Range = Slider.Range(
    Slider.Range.Properties(
        end = valueOrNull(end) ?: properties.end,
        margins = valueOrNull(margins) ?: properties.margins,
        start = valueOrNull(start) ?: properties.start,
        trackActiveStyle = valueOrNull(trackActiveStyle) ?: properties.trackActiveStyle,
        trackInactiveStyle = valueOrNull(trackInactiveStyle) ?: properties.trackInactiveStyle,
    )
)

/**
 * @param end End of section.
 * @param margins Section margins. Only uses horizontal margins.
 * @param start Start of section.
 * @param trackActiveStyle Style of the active part of a scale.
 * @param trackInactiveStyle Style of the inactive part of a scale.
 */
@Generated
fun Slider.Range.defer(
    `use named arguments`: Guard = Guard.instance,
    end: ReferenceProperty<Int>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    start: ReferenceProperty<Int>? = null,
    trackActiveStyle: ReferenceProperty<Drawable>? = null,
    trackInactiveStyle: ReferenceProperty<Drawable>? = null,
): Slider.Range = Slider.Range(
    Slider.Range.Properties(
        end = end ?: properties.end,
        margins = margins ?: properties.margins,
        start = start ?: properties.start,
        trackActiveStyle = trackActiveStyle ?: properties.trackActiveStyle,
        trackInactiveStyle = trackInactiveStyle ?: properties.trackInactiveStyle,
    )
)

/**
 * @param end End of section.
 * @param start Start of section.
 */
@Generated
fun Slider.Range.evaluate(
    `use named arguments`: Guard = Guard.instance,
    end: ExpressionProperty<Int>? = null,
    start: ExpressionProperty<Int>? = null,
): Slider.Range = Slider.Range(
    Slider.Range.Properties(
        end = end ?: properties.end,
        margins = properties.margins,
        start = start ?: properties.start,
        trackActiveStyle = properties.trackActiveStyle,
        trackInactiveStyle = properties.trackInactiveStyle,
    )
)

@Generated
fun Slider.Range.asList() = listOf(this)

/**
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param letterSpacing Spacing between characters.
 * @param offset Shift relative to the center.
 * @param textColor Text color.
 */
@Generated
fun DivScope.sliderTextStyle(
    `use named arguments`: Guard = Guard.instance,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontVariationSettings: Map<String, Any>? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    letterSpacing: Double? = null,
    offset: Point? = null,
    textColor: Color? = null,
): Slider.TextStyle = Slider.TextStyle(
    Slider.TextStyle.Properties(
        fontFamily = valueOrNull(fontFamily),
        fontSize = valueOrNull(fontSize),
        fontSizeUnit = valueOrNull(fontSizeUnit),
        fontVariationSettings = valueOrNull(fontVariationSettings),
        fontWeight = valueOrNull(fontWeight),
        fontWeightValue = valueOrNull(fontWeightValue),
        letterSpacing = valueOrNull(letterSpacing),
        offset = valueOrNull(offset),
        textColor = valueOrNull(textColor),
    )
)

/**
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param letterSpacing Spacing between characters.
 * @param offset Shift relative to the center.
 * @param textColor Text color.
 */
@Generated
fun DivScope.sliderTextStyleProps(
    `use named arguments`: Guard = Guard.instance,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontVariationSettings: Map<String, Any>? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    letterSpacing: Double? = null,
    offset: Point? = null,
    textColor: Color? = null,
) = Slider.TextStyle.Properties(
    fontFamily = valueOrNull(fontFamily),
    fontSize = valueOrNull(fontSize),
    fontSizeUnit = valueOrNull(fontSizeUnit),
    fontVariationSettings = valueOrNull(fontVariationSettings),
    fontWeight = valueOrNull(fontWeight),
    fontWeightValue = valueOrNull(fontWeightValue),
    letterSpacing = valueOrNull(letterSpacing),
    offset = valueOrNull(offset),
    textColor = valueOrNull(textColor),
)

/**
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param letterSpacing Spacing between characters.
 * @param offset Shift relative to the center.
 * @param textColor Text color.
 */
@Generated
fun TemplateScope.sliderTextStyleRefs(
    `use named arguments`: Guard = Guard.instance,
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontVariationSettings: ReferenceProperty<Map<String, Any>>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    fontWeightValue: ReferenceProperty<Int>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    offset: ReferenceProperty<Point>? = null,
    textColor: ReferenceProperty<Color>? = null,
) = Slider.TextStyle.Properties(
    fontFamily = fontFamily,
    fontSize = fontSize,
    fontSizeUnit = fontSizeUnit,
    fontVariationSettings = fontVariationSettings,
    fontWeight = fontWeight,
    fontWeightValue = fontWeightValue,
    letterSpacing = letterSpacing,
    offset = offset,
    textColor = textColor,
)

/**
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param letterSpacing Spacing between characters.
 * @param offset Shift relative to the center.
 * @param textColor Text color.
 */
@Generated
fun Slider.TextStyle.override(
    `use named arguments`: Guard = Guard.instance,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontVariationSettings: Map<String, Any>? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    letterSpacing: Double? = null,
    offset: Point? = null,
    textColor: Color? = null,
): Slider.TextStyle = Slider.TextStyle(
    Slider.TextStyle.Properties(
        fontFamily = valueOrNull(fontFamily) ?: properties.fontFamily,
        fontSize = valueOrNull(fontSize) ?: properties.fontSize,
        fontSizeUnit = valueOrNull(fontSizeUnit) ?: properties.fontSizeUnit,
        fontVariationSettings = valueOrNull(fontVariationSettings) ?: properties.fontVariationSettings,
        fontWeight = valueOrNull(fontWeight) ?: properties.fontWeight,
        fontWeightValue = valueOrNull(fontWeightValue) ?: properties.fontWeightValue,
        letterSpacing = valueOrNull(letterSpacing) ?: properties.letterSpacing,
        offset = valueOrNull(offset) ?: properties.offset,
        textColor = valueOrNull(textColor) ?: properties.textColor,
    )
)

/**
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param letterSpacing Spacing between characters.
 * @param offset Shift relative to the center.
 * @param textColor Text color.
 */
@Generated
fun Slider.TextStyle.defer(
    `use named arguments`: Guard = Guard.instance,
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontVariationSettings: ReferenceProperty<Map<String, Any>>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    fontWeightValue: ReferenceProperty<Int>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    offset: ReferenceProperty<Point>? = null,
    textColor: ReferenceProperty<Color>? = null,
): Slider.TextStyle = Slider.TextStyle(
    Slider.TextStyle.Properties(
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontVariationSettings = fontVariationSettings ?: properties.fontVariationSettings,
        fontWeight = fontWeight ?: properties.fontWeight,
        fontWeightValue = fontWeightValue ?: properties.fontWeightValue,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        offset = offset ?: properties.offset,
        textColor = textColor ?: properties.textColor,
    )
)

/**
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param letterSpacing Spacing between characters.
 * @param textColor Text color.
 */
@Generated
fun Slider.TextStyle.evaluate(
    `use named arguments`: Guard = Guard.instance,
    fontFamily: ExpressionProperty<String>? = null,
    fontSize: ExpressionProperty<Int>? = null,
    fontSizeUnit: ExpressionProperty<SizeUnit>? = null,
    fontWeight: ExpressionProperty<FontWeight>? = null,
    fontWeightValue: ExpressionProperty<Int>? = null,
    letterSpacing: ExpressionProperty<Double>? = null,
    textColor: ExpressionProperty<Color>? = null,
): Slider.TextStyle = Slider.TextStyle(
    Slider.TextStyle.Properties(
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontVariationSettings = properties.fontVariationSettings,
        fontWeight = fontWeight ?: properties.fontWeight,
        fontWeightValue = fontWeightValue ?: properties.fontWeightValue,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        offset = properties.offset,
        textColor = textColor ?: properties.textColor,
    )
)

@Generated
fun Slider.TextStyle.asList() = listOf(this)
