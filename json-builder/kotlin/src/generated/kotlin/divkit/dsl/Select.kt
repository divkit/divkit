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
 * List of options with only one to be selected.
 * 
 * Can be created using the method [select].
 * 
 * Required parameters: `value_variable, type, options`.
 */
@Generated
class Select internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "select")
    )

    operator fun plus(additive: Properties): Select = Select(
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
            fontFamily = additive.fontFamily ?: properties.fontFamily,
            fontSize = additive.fontSize ?: properties.fontSize,
            fontSizeUnit = additive.fontSizeUnit ?: properties.fontSizeUnit,
            fontWeight = additive.fontWeight ?: properties.fontWeight,
            fontWeightValue = additive.fontWeightValue ?: properties.fontWeightValue,
            functions = additive.functions ?: properties.functions,
            height = additive.height ?: properties.height,
            hintColor = additive.hintColor ?: properties.hintColor,
            hintText = additive.hintText ?: properties.hintText,
            id = additive.id ?: properties.id,
            layoutProvider = additive.layoutProvider ?: properties.layoutProvider,
            letterSpacing = additive.letterSpacing ?: properties.letterSpacing,
            lineHeight = additive.lineHeight ?: properties.lineHeight,
            margins = additive.margins ?: properties.margins,
            options = additive.options ?: properties.options,
            paddings = additive.paddings ?: properties.paddings,
            reuseId = additive.reuseId ?: properties.reuseId,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            textColor = additive.textColor ?: properties.textColor,
            tooltips = additive.tooltips ?: properties.tooltips,
            transform = additive.transform ?: properties.transform,
            transitionChange = additive.transitionChange ?: properties.transitionChange,
            transitionIn = additive.transitionIn ?: properties.transitionIn,
            transitionOut = additive.transitionOut ?: properties.transitionOut,
            transitionTriggers = additive.transitionTriggers ?: properties.transitionTriggers,
            valueVariable = additive.valueVariable ?: properties.valueVariable,
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
         * Declaration of animators that can be used to change the value of variables over time.
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
         * Style.
         * Default value: `regular`.
         */
        val fontWeight: Property<FontWeight>?,
        /**
         * Style. Numeric value.
         */
        val fontWeightValue: Property<Int>?,
        /**
         * Custom functions.
         */
        val functions: Property<List<Function>>?,
        /**
         * Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
         * Default value: `{"type": "wrap_content"}`.
         */
        val height: Property<Size>?,
        /**
         * Hint color.
         * Default value: `#73000000`.
         */
        val hintColor: Property<Color>?,
        /**
         * Hint text.
         */
        val hintText: Property<String>?,
        /**
         * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
         */
        val id: Property<String>?,
        /**
         * Provides element real size values after a layout cycle.
         */
        val layoutProvider: Property<LayoutProvider>?,
        /**
         * Spacing between characters.
         * Default value: `0`.
         */
        val letterSpacing: Property<Double>?,
        /**
         * Line spacing of the text. Measured in units set in `font_size_unit`.
         */
        val lineHeight: Property<Int>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        val options: Property<List<Option>>?,
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
         * Text color.
         * Default value: `#FF000000`.
         */
        val textColor: Property<Color>?,
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
         * Name of the variable that stores the selected option value (`value`).
         */
        val valueVariable: Property<String>?,
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
            result.tryPutProperty("animators", animators)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("font_family", fontFamily)
            result.tryPutProperty("font_size", fontSize)
            result.tryPutProperty("font_size_unit", fontSizeUnit)
            result.tryPutProperty("font_weight", fontWeight)
            result.tryPutProperty("font_weight_value", fontWeightValue)
            result.tryPutProperty("functions", functions)
            result.tryPutProperty("height", height)
            result.tryPutProperty("hint_color", hintColor)
            result.tryPutProperty("hint_text", hintText)
            result.tryPutProperty("id", id)
            result.tryPutProperty("layout_provider", layoutProvider)
            result.tryPutProperty("letter_spacing", letterSpacing)
            result.tryPutProperty("line_height", lineHeight)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("options", options)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("reuse_id", reuseId)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("text_color", textColor)
            result.tryPutProperty("tooltips", tooltips)
            result.tryPutProperty("transform", transform)
            result.tryPutProperty("transition_change", transitionChange)
            result.tryPutProperty("transition_in", transitionIn)
            result.tryPutProperty("transition_out", transitionOut)
            result.tryPutProperty("transition_triggers", transitionTriggers)
            result.tryPutProperty("value_variable", valueVariable)
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
     * List option.
     * 
     * Can be created using the method [selectOption].
     * 
     * Required parameters: `value`.
     */
    @Generated
    class Option internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Option = Option(
            Properties(
                text = additive.text ?: properties.text,
                value = additive.value ?: properties.value,
            )
        )

        class Properties internal constructor(
            /**
             * Text description of the option displayed in the list. If not set, `value` is used instead.
             */
            val text: Property<String>?,
            /**
             * Value matching the option.
             */
            val value: Property<String>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("text", text)
                result.tryPutProperty("value", value)
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
 * @param animators Declaration of animators that can be used to change the value of variables over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions Custom functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param hintColor Hint color.
 * @param hintText Hint text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Measured in units set in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textColor Text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param valueVariable Name of the variable that stores the selected option value (`value`).
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.select(
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
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    hintColor: Color? = null,
    hintText: String? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    margins: EdgeInsets? = null,
    options: List<Select.Option>? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    textColor: Color? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    valueVariable: String? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Select = Select(
    Select.Properties(
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
        fontFamily = valueOrNull(fontFamily),
        fontSize = valueOrNull(fontSize),
        fontSizeUnit = valueOrNull(fontSizeUnit),
        fontWeight = valueOrNull(fontWeight),
        fontWeightValue = valueOrNull(fontWeightValue),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        hintColor = valueOrNull(hintColor),
        hintText = valueOrNull(hintText),
        id = valueOrNull(id),
        layoutProvider = valueOrNull(layoutProvider),
        letterSpacing = valueOrNull(letterSpacing),
        lineHeight = valueOrNull(lineHeight),
        margins = valueOrNull(margins),
        options = valueOrNull(options),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        textColor = valueOrNull(textColor),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        valueVariable = valueOrNull(valueVariable),
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
 * @param animators Declaration of animators that can be used to change the value of variables over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions Custom functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param hintColor Hint color.
 * @param hintText Hint text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Measured in units set in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textColor Text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param valueVariable Name of the variable that stores the selected option value (`value`).
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.selectProps(
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
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    hintColor: Color? = null,
    hintText: String? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    margins: EdgeInsets? = null,
    options: List<Select.Option>? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    textColor: Color? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    valueVariable: String? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
) = Select.Properties(
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
    fontFamily = valueOrNull(fontFamily),
    fontSize = valueOrNull(fontSize),
    fontSizeUnit = valueOrNull(fontSizeUnit),
    fontWeight = valueOrNull(fontWeight),
    fontWeightValue = valueOrNull(fontWeightValue),
    functions = valueOrNull(functions),
    height = valueOrNull(height),
    hintColor = valueOrNull(hintColor),
    hintText = valueOrNull(hintText),
    id = valueOrNull(id),
    layoutProvider = valueOrNull(layoutProvider),
    letterSpacing = valueOrNull(letterSpacing),
    lineHeight = valueOrNull(lineHeight),
    margins = valueOrNull(margins),
    options = valueOrNull(options),
    paddings = valueOrNull(paddings),
    reuseId = valueOrNull(reuseId),
    rowSpan = valueOrNull(rowSpan),
    selectedActions = valueOrNull(selectedActions),
    textColor = valueOrNull(textColor),
    tooltips = valueOrNull(tooltips),
    transform = valueOrNull(transform),
    transitionChange = valueOrNull(transitionChange),
    transitionIn = valueOrNull(transitionIn),
    transitionOut = valueOrNull(transitionOut),
    transitionTriggers = valueOrNull(transitionTriggers),
    valueVariable = valueOrNull(valueVariable),
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
 * @param animators Declaration of animators that can be used to change the value of variables over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions Custom functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param hintColor Hint color.
 * @param hintText Hint text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Measured in units set in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textColor Text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param valueVariable Name of the variable that stores the selected option value (`value`).
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun TemplateScope.selectRefs(
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
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    fontWeightValue: ReferenceProperty<Int>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    hintColor: ReferenceProperty<Color>? = null,
    hintText: ReferenceProperty<String>? = null,
    id: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    options: ReferenceProperty<List<Select.Option>>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    textColor: ReferenceProperty<Color>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    valueVariable: ReferenceProperty<String>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
) = Select.Properties(
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
    fontFamily = fontFamily,
    fontSize = fontSize,
    fontSizeUnit = fontSizeUnit,
    fontWeight = fontWeight,
    fontWeightValue = fontWeightValue,
    functions = functions,
    height = height,
    hintColor = hintColor,
    hintText = hintText,
    id = id,
    layoutProvider = layoutProvider,
    letterSpacing = letterSpacing,
    lineHeight = lineHeight,
    margins = margins,
    options = options,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectedActions = selectedActions,
    textColor = textColor,
    tooltips = tooltips,
    transform = transform,
    transitionChange = transitionChange,
    transitionIn = transitionIn,
    transitionOut = transitionOut,
    transitionTriggers = transitionTriggers,
    valueVariable = valueVariable,
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
 * @param animators Declaration of animators that can be used to change the value of variables over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions Custom functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param hintColor Hint color.
 * @param hintText Hint text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Measured in units set in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textColor Text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param valueVariable Name of the variable that stores the selected option value (`value`).
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Select.override(
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
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    hintColor: Color? = null,
    hintText: String? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    margins: EdgeInsets? = null,
    options: List<Select.Option>? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    textColor: Color? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    valueVariable: String? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Select = Select(
    Select.Properties(
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
        fontFamily = valueOrNull(fontFamily) ?: properties.fontFamily,
        fontSize = valueOrNull(fontSize) ?: properties.fontSize,
        fontSizeUnit = valueOrNull(fontSizeUnit) ?: properties.fontSizeUnit,
        fontWeight = valueOrNull(fontWeight) ?: properties.fontWeight,
        fontWeightValue = valueOrNull(fontWeightValue) ?: properties.fontWeightValue,
        functions = valueOrNull(functions) ?: properties.functions,
        height = valueOrNull(height) ?: properties.height,
        hintColor = valueOrNull(hintColor) ?: properties.hintColor,
        hintText = valueOrNull(hintText) ?: properties.hintText,
        id = valueOrNull(id) ?: properties.id,
        layoutProvider = valueOrNull(layoutProvider) ?: properties.layoutProvider,
        letterSpacing = valueOrNull(letterSpacing) ?: properties.letterSpacing,
        lineHeight = valueOrNull(lineHeight) ?: properties.lineHeight,
        margins = valueOrNull(margins) ?: properties.margins,
        options = valueOrNull(options) ?: properties.options,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        reuseId = valueOrNull(reuseId) ?: properties.reuseId,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        textColor = valueOrNull(textColor) ?: properties.textColor,
        tooltips = valueOrNull(tooltips) ?: properties.tooltips,
        transform = valueOrNull(transform) ?: properties.transform,
        transitionChange = valueOrNull(transitionChange) ?: properties.transitionChange,
        transitionIn = valueOrNull(transitionIn) ?: properties.transitionIn,
        transitionOut = valueOrNull(transitionOut) ?: properties.transitionOut,
        transitionTriggers = valueOrNull(transitionTriggers) ?: properties.transitionTriggers,
        valueVariable = valueOrNull(valueVariable) ?: properties.valueVariable,
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
 * @param animators Declaration of animators that can be used to change the value of variables over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions Custom functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param hintColor Hint color.
 * @param hintText Hint text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Measured in units set in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textColor Text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param valueVariable Name of the variable that stores the selected option value (`value`).
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Select.defer(
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
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    fontWeightValue: ReferenceProperty<Int>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    hintColor: ReferenceProperty<Color>? = null,
    hintText: ReferenceProperty<String>? = null,
    id: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    options: ReferenceProperty<List<Select.Option>>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    textColor: ReferenceProperty<Color>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    valueVariable: ReferenceProperty<String>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Select = Select(
    Select.Properties(
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
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontWeight = fontWeight ?: properties.fontWeight,
        fontWeightValue = fontWeightValue ?: properties.fontWeightValue,
        functions = functions ?: properties.functions,
        height = height ?: properties.height,
        hintColor = hintColor ?: properties.hintColor,
        hintText = hintText ?: properties.hintText,
        id = id ?: properties.id,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        margins = margins ?: properties.margins,
        options = options ?: properties.options,
        paddings = paddings ?: properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = selectedActions ?: properties.selectedActions,
        textColor = textColor ?: properties.textColor,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
        transitionChange = transitionChange ?: properties.transitionChange,
        transitionIn = transitionIn ?: properties.transitionIn,
        transitionOut = transitionOut ?: properties.transitionOut,
        transitionTriggers = transitionTriggers ?: properties.transitionTriggers,
        valueVariable = valueVariable ?: properties.valueVariable,
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
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param hintColor Hint color.
 * @param hintText Hint text.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Measured in units set in `font_size_unit`.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param textColor Text color.
 * @param visibility Element visibility.
 */
@Generated
fun Select.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    fontFamily: ExpressionProperty<String>? = null,
    fontSize: ExpressionProperty<Int>? = null,
    fontSizeUnit: ExpressionProperty<SizeUnit>? = null,
    fontWeight: ExpressionProperty<FontWeight>? = null,
    fontWeightValue: ExpressionProperty<Int>? = null,
    hintColor: ExpressionProperty<Color>? = null,
    hintText: ExpressionProperty<String>? = null,
    letterSpacing: ExpressionProperty<Double>? = null,
    lineHeight: ExpressionProperty<Int>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    textColor: ExpressionProperty<Color>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Select = Select(
    Select.Properties(
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
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontWeight = fontWeight ?: properties.fontWeight,
        fontWeightValue = fontWeightValue ?: properties.fontWeightValue,
        functions = properties.functions,
        height = properties.height,
        hintColor = hintColor ?: properties.hintColor,
        hintText = hintText ?: properties.hintText,
        id = properties.id,
        layoutProvider = properties.layoutProvider,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        margins = properties.margins,
        options = properties.options,
        paddings = properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectedActions = properties.selectedActions,
        textColor = textColor ?: properties.textColor,
        tooltips = properties.tooltips,
        transform = properties.transform,
        transitionChange = properties.transitionChange,
        transitionIn = properties.transitionIn,
        transitionOut = properties.transitionOut,
        transitionTriggers = properties.transitionTriggers,
        valueVariable = properties.valueVariable,
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
 * @param animators Declaration of animators that can be used to change the value of variables over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions Custom functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param hintColor Hint color.
 * @param hintText Hint text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Measured in units set in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textColor Text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param valueVariable Name of the variable that stores the selected option value (`value`).
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Select>.override(
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
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    hintColor: Color? = null,
    hintText: String? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    margins: EdgeInsets? = null,
    options: List<Select.Option>? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectedActions: List<Action>? = null,
    textColor: Color? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    valueVariable: String? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Component<Select> = Component(
    template = template,
    properties = Select.Properties(
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
        fontFamily = valueOrNull(fontFamily),
        fontSize = valueOrNull(fontSize),
        fontSizeUnit = valueOrNull(fontSizeUnit),
        fontWeight = valueOrNull(fontWeight),
        fontWeightValue = valueOrNull(fontWeightValue),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        hintColor = valueOrNull(hintColor),
        hintText = valueOrNull(hintText),
        id = valueOrNull(id),
        layoutProvider = valueOrNull(layoutProvider),
        letterSpacing = valueOrNull(letterSpacing),
        lineHeight = valueOrNull(lineHeight),
        margins = valueOrNull(margins),
        options = valueOrNull(options),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectedActions = valueOrNull(selectedActions),
        textColor = valueOrNull(textColor),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        valueVariable = valueOrNull(valueVariable),
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
 * @param animators Declaration of animators that can be used to change the value of variables over time.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions Custom functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param hintColor Hint color.
 * @param hintText Hint text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides element real size values after a layout cycle.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Measured in units set in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textColor Text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param valueVariable Name of the variable that stores the selected option value (`value`).
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Definition of variables that can be used within this element. These variables, defined in the array, can only be used inside this element and its children.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Select>.defer(
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
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    fontWeightValue: ReferenceProperty<Int>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    hintColor: ReferenceProperty<Color>? = null,
    hintText: ReferenceProperty<String>? = null,
    id: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    options: ReferenceProperty<List<Select.Option>>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    textColor: ReferenceProperty<Color>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    valueVariable: ReferenceProperty<String>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Component<Select> = Component(
    template = template,
    properties = Select.Properties(
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
        fontFamily = fontFamily,
        fontSize = fontSize,
        fontSizeUnit = fontSizeUnit,
        fontWeight = fontWeight,
        fontWeightValue = fontWeightValue,
        functions = functions,
        height = height,
        hintColor = hintColor,
        hintText = hintText,
        id = id,
        layoutProvider = layoutProvider,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        margins = margins,
        options = options,
        paddings = paddings,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = selectedActions,
        textColor = textColor,
        tooltips = tooltips,
        transform = transform,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        valueVariable = valueVariable,
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
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param hintColor Hint color.
 * @param hintText Hint text.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Measured in units set in `font_size_unit`.
 * @param reuseId Id for the div structure. Used for more optimal reuse of blocks. See [reusing blocks](../../reuse/reuse.md)
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param textColor Text color.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Select>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    fontFamily: ExpressionProperty<String>? = null,
    fontSize: ExpressionProperty<Int>? = null,
    fontSizeUnit: ExpressionProperty<SizeUnit>? = null,
    fontWeight: ExpressionProperty<FontWeight>? = null,
    fontWeightValue: ExpressionProperty<Int>? = null,
    hintColor: ExpressionProperty<Color>? = null,
    hintText: ExpressionProperty<String>? = null,
    letterSpacing: ExpressionProperty<Double>? = null,
    lineHeight: ExpressionProperty<Int>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    textColor: ExpressionProperty<Color>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Select> = Component(
    template = template,
    properties = Select.Properties(
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
        fontFamily = fontFamily,
        fontSize = fontSize,
        fontSizeUnit = fontSizeUnit,
        fontWeight = fontWeight,
        fontWeightValue = fontWeightValue,
        functions = null,
        height = null,
        hintColor = hintColor,
        hintText = hintText,
        id = null,
        layoutProvider = null,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        margins = null,
        options = null,
        paddings = null,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectedActions = null,
        textColor = textColor,
        tooltips = null,
        transform = null,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        valueVariable = null,
        variableTriggers = null,
        variables = null,
        visibility = visibility,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Select>.plus(additive: Select.Properties): Component<Select> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Select.asList() = listOf(this)

/**
 * @param text Text description of the option displayed in the list. If not set, `value` is used instead.
 * @param value Value matching the option.
 */
@Generated
fun DivScope.selectOption(
    `use named arguments`: Guard = Guard.instance,
    text: String? = null,
    value: String? = null,
): Select.Option = Select.Option(
    Select.Option.Properties(
        text = valueOrNull(text),
        value = valueOrNull(value),
    )
)

/**
 * @param text Text description of the option displayed in the list. If not set, `value` is used instead.
 * @param value Value matching the option.
 */
@Generated
fun DivScope.selectOptionProps(
    `use named arguments`: Guard = Guard.instance,
    text: String? = null,
    value: String? = null,
) = Select.Option.Properties(
    text = valueOrNull(text),
    value = valueOrNull(value),
)

/**
 * @param text Text description of the option displayed in the list. If not set, `value` is used instead.
 * @param value Value matching the option.
 */
@Generated
fun TemplateScope.selectOptionRefs(
    `use named arguments`: Guard = Guard.instance,
    text: ReferenceProperty<String>? = null,
    value: ReferenceProperty<String>? = null,
) = Select.Option.Properties(
    text = text,
    value = value,
)

/**
 * @param text Text description of the option displayed in the list. If not set, `value` is used instead.
 * @param value Value matching the option.
 */
@Generated
fun Select.Option.override(
    `use named arguments`: Guard = Guard.instance,
    text: String? = null,
    value: String? = null,
): Select.Option = Select.Option(
    Select.Option.Properties(
        text = valueOrNull(text) ?: properties.text,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param text Text description of the option displayed in the list. If not set, `value` is used instead.
 * @param value Value matching the option.
 */
@Generated
fun Select.Option.defer(
    `use named arguments`: Guard = Guard.instance,
    text: ReferenceProperty<String>? = null,
    value: ReferenceProperty<String>? = null,
): Select.Option = Select.Option(
    Select.Option.Properties(
        text = text ?: properties.text,
        value = value ?: properties.value,
    )
)

/**
 * @param text Text description of the option displayed in the list. If not set, `value` is used instead.
 * @param value Value matching the option.
 */
@Generated
fun Select.Option.evaluate(
    `use named arguments`: Guard = Guard.instance,
    text: ExpressionProperty<String>? = null,
    value: ExpressionProperty<String>? = null,
): Select.Option = Select.Option(
    Select.Option.Properties(
        text = text ?: properties.text,
        value = value ?: properties.value,
    )
)

@Generated
fun Select.Option.asList() = listOf(this)
