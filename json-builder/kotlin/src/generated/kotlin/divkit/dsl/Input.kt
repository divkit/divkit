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
 * Text input element.
 * 
 * Can be created using the method [input].
 * 
 * Required parameters: `type, text_variable`.
 */
@Generated
data class Input internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "input")
    )

    operator fun plus(additive: Properties): Input = Input(
        Properties(
            accessibility = additive.accessibility ?: properties.accessibility,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            animators = additive.animators ?: properties.animators,
            autocapitalization = additive.autocapitalization ?: properties.autocapitalization,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            enterKeyActions = additive.enterKeyActions ?: properties.enterKeyActions,
            enterKeyType = additive.enterKeyType ?: properties.enterKeyType,
            extensions = additive.extensions ?: properties.extensions,
            filters = additive.filters ?: properties.filters,
            focus = additive.focus ?: properties.focus,
            fontFamily = additive.fontFamily ?: properties.fontFamily,
            fontSize = additive.fontSize ?: properties.fontSize,
            fontSizeUnit = additive.fontSizeUnit ?: properties.fontSizeUnit,
            fontVariationSettings = additive.fontVariationSettings ?: properties.fontVariationSettings,
            fontWeight = additive.fontWeight ?: properties.fontWeight,
            fontWeightValue = additive.fontWeightValue ?: properties.fontWeightValue,
            functions = additive.functions ?: properties.functions,
            height = additive.height ?: properties.height,
            highlightColor = additive.highlightColor ?: properties.highlightColor,
            hintColor = additive.hintColor ?: properties.hintColor,
            hintText = additive.hintText ?: properties.hintText,
            id = additive.id ?: properties.id,
            isEnabled = additive.isEnabled ?: properties.isEnabled,
            keyboardType = additive.keyboardType ?: properties.keyboardType,
            layoutProvider = additive.layoutProvider ?: properties.layoutProvider,
            letterSpacing = additive.letterSpacing ?: properties.letterSpacing,
            lineHeight = additive.lineHeight ?: properties.lineHeight,
            margins = additive.margins ?: properties.margins,
            mask = additive.mask ?: properties.mask,
            maxLength = additive.maxLength ?: properties.maxLength,
            maxVisibleLines = additive.maxVisibleLines ?: properties.maxVisibleLines,
            nativeInterface = additive.nativeInterface ?: properties.nativeInterface,
            paddings = additive.paddings ?: properties.paddings,
            reuseId = additive.reuseId ?: properties.reuseId,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectAllOnFocus = additive.selectAllOnFocus ?: properties.selectAllOnFocus,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            textAlignmentHorizontal = additive.textAlignmentHorizontal ?: properties.textAlignmentHorizontal,
            textAlignmentVertical = additive.textAlignmentVertical ?: properties.textAlignmentVertical,
            textColor = additive.textColor ?: properties.textColor,
            textVariable = additive.textVariable ?: properties.textVariable,
            tooltips = additive.tooltips ?: properties.tooltips,
            transform = additive.transform ?: properties.transform,
            transitionChange = additive.transitionChange ?: properties.transitionChange,
            transitionIn = additive.transitionIn ?: properties.transitionIn,
            transitionOut = additive.transitionOut ?: properties.transitionOut,
            transitionTriggers = additive.transitionTriggers ?: properties.transitionTriggers,
            validators = additive.validators ?: properties.validators,
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
         * Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
         * Default value: `auto`.
         */
        val autocapitalization: Property<Autocapitalization>?,
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
         * Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
         */
        val enterKeyActions: Property<List<Action>>?,
        /**
         * 'Enter' key type.
         * Default value: `default`.
         */
        val enterKeyType: Property<EnterKeyType>?,
        /**
         * Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
         */
        val extensions: Property<List<Extension>>?,
        /**
         * Filter that prevents users from entering text that doesn't satisfy the specified conditions.
         */
        val filters: Property<List<InputFilter>>?,
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
         * User functions.
         */
        val functions: Property<List<Function>>?,
        /**
         * Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
         * Default value: `{"type": "wrap_content"}`.
         */
        val height: Property<Size>?,
        /**
         * Text highlight color. If the value isn't set, the color set in the client will be used instead.
         */
        val highlightColor: Property<Color>?,
        /**
         * Text color.
         * Default value: `#73000000`.
         */
        val hintColor: Property<Color>?,
        /**
         * Tooltip text.
         */
        val hintText: Property<String>?,
        /**
         * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
         */
        val id: Property<String>?,
        /**
         * Enables or disables text editing.
         * Default value: `true`.
         */
        val isEnabled: Property<Boolean>?,
        /**
         * Keyboard type.
         * Default value: `multi_line_text`.
         */
        val keyboardType: Property<KeyboardType>?,
        /**
         * Provides data on the actual size of the element.
         */
        val layoutProvider: Property<LayoutProvider>?,
        /**
         * Spacing between characters.
         * Default value: `0`.
         */
        val letterSpacing: Property<Double>?,
        /**
         * Line spacing of the text. Units specified in `font_size_unit`.
         */
        val lineHeight: Property<Int>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Mask for entering text based on the specified template.
         */
        val mask: Property<InputMask>?,
        /**
         * Maximum number of characters that can be entered in the input field.
         */
        val maxLength: Property<Int>?,
        /**
         * Maximum number of lines to be displayed in the input field.
         */
        val maxVisibleLines: Property<Int>?,
        /**
         * Text input line used in the native interface.
         */
        val nativeInterface: Property<NativeInterface>?,
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
         * Highlighting input text when focused.
         * Default value: `false`.
         */
        val selectAllOnFocus: Property<Boolean>?,
        /**
         * List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
         */
        val selectedActions: Property<List<Action>>?,
        /**
         * Horizontal text alignment.
         * Default value: `start`.
         */
        val textAlignmentHorizontal: Property<AlignmentHorizontal>?,
        /**
         * Vertical text alignment.
         * Default value: `center`.
         */
        val textAlignmentVertical: Property<AlignmentVertical>?,
        /**
         * Text color.
         * Default value: `#FF000000`.
         */
        val textColor: Property<Color>?,
        /**
         * Name of text storage variable.
         */
        val textVariable: Property<String>?,
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
         * Validator that checks that the field value meets the specified conditions.
         */
        val validators: Property<List<InputValidator>>?,
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
            result.tryPutProperty("autocapitalization", autocapitalization)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("enter_key_actions", enterKeyActions)
            result.tryPutProperty("enter_key_type", enterKeyType)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("filters", filters)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("font_family", fontFamily)
            result.tryPutProperty("font_size", fontSize)
            result.tryPutProperty("font_size_unit", fontSizeUnit)
            result.tryPutProperty("font_variation_settings", fontVariationSettings)
            result.tryPutProperty("font_weight", fontWeight)
            result.tryPutProperty("font_weight_value", fontWeightValue)
            result.tryPutProperty("functions", functions)
            result.tryPutProperty("height", height)
            result.tryPutProperty("highlight_color", highlightColor)
            result.tryPutProperty("hint_color", hintColor)
            result.tryPutProperty("hint_text", hintText)
            result.tryPutProperty("id", id)
            result.tryPutProperty("is_enabled", isEnabled)
            result.tryPutProperty("keyboard_type", keyboardType)
            result.tryPutProperty("layout_provider", layoutProvider)
            result.tryPutProperty("letter_spacing", letterSpacing)
            result.tryPutProperty("line_height", lineHeight)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("mask", mask)
            result.tryPutProperty("max_length", maxLength)
            result.tryPutProperty("max_visible_lines", maxVisibleLines)
            result.tryPutProperty("native_interface", nativeInterface)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("reuse_id", reuseId)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("select_all_on_focus", selectAllOnFocus)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("text_alignment_horizontal", textAlignmentHorizontal)
            result.tryPutProperty("text_alignment_vertical", textAlignmentVertical)
            result.tryPutProperty("text_color", textColor)
            result.tryPutProperty("text_variable", textVariable)
            result.tryPutProperty("tooltips", tooltips)
            result.tryPutProperty("transform", transform)
            result.tryPutProperty("transition_change", transitionChange)
            result.tryPutProperty("transition_in", transitionIn)
            result.tryPutProperty("transition_out", transitionOut)
            result.tryPutProperty("transition_triggers", transitionTriggers)
            result.tryPutProperty("validators", validators)
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
     * Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
     * 
     * Possible values: [auto], [none], [words], [sentences], [all_characters].
     */
    @Generated
    sealed interface Autocapitalization

    /**
     * 'Enter' key type.
     * 
     * Possible values: [default], [go], [search], [send], [done].
     */
    @Generated
    sealed interface EnterKeyType

    /**
     * Keyboard type.
     * 
     * Possible values: [single_line_text], [multi_line_text], [phone], [number], [email], [uri], [password].
     */
    @Generated
    sealed interface KeyboardType

    /**
     * Text input line used in the native interface.
     * 
     * Can be created using the method [inputNativeInterface].
     * 
     * Required parameters: `color`.
     */
    @Generated
    data class NativeInterface internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): NativeInterface = NativeInterface(
            Properties(
                color = additive.color ?: properties.color,
            )
        )

        data class Properties internal constructor(
            /**
             * Text input line color.
             */
            val color: Property<Color>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("color", color)
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param enterKeyActions Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
 * @param enterKeyType 'Enter' key type.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Filter that prevents users from entering text that doesn't satisfy the specified conditions.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param mask Mask for entering text based on the specified template.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param nativeInterface Text input line used in the native interface.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param textVariable Name of text storage variable.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param validators Validator that checks that the field value meets the specified conditions.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.input(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    autocapitalization: Input.Autocapitalization? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    enterKeyActions: List<Action>? = null,
    enterKeyType: Input.EnterKeyType? = null,
    extensions: List<Extension>? = null,
    filters: List<InputFilter>? = null,
    focus: Focus? = null,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontVariationSettings: Map<String, Any>? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    highlightColor: Color? = null,
    hintColor: Color? = null,
    hintText: String? = null,
    id: String? = null,
    isEnabled: Boolean? = null,
    keyboardType: Input.KeyboardType? = null,
    layoutProvider: LayoutProvider? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    margins: EdgeInsets? = null,
    mask: InputMask? = null,
    maxLength: Int? = null,
    maxVisibleLines: Int? = null,
    nativeInterface: Input.NativeInterface? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectAllOnFocus: Boolean? = null,
    selectedActions: List<Action>? = null,
    textAlignmentHorizontal: AlignmentHorizontal? = null,
    textAlignmentVertical: AlignmentVertical? = null,
    textColor: Color? = null,
    textVariable: String? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    validators: List<InputValidator>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Input = Input(
    Input.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        animators = valueOrNull(animators),
        autocapitalization = valueOrNull(autocapitalization),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        disappearActions = valueOrNull(disappearActions),
        enterKeyActions = valueOrNull(enterKeyActions),
        enterKeyType = valueOrNull(enterKeyType),
        extensions = valueOrNull(extensions),
        filters = valueOrNull(filters),
        focus = valueOrNull(focus),
        fontFamily = valueOrNull(fontFamily),
        fontSize = valueOrNull(fontSize),
        fontSizeUnit = valueOrNull(fontSizeUnit),
        fontVariationSettings = valueOrNull(fontVariationSettings),
        fontWeight = valueOrNull(fontWeight),
        fontWeightValue = valueOrNull(fontWeightValue),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        highlightColor = valueOrNull(highlightColor),
        hintColor = valueOrNull(hintColor),
        hintText = valueOrNull(hintText),
        id = valueOrNull(id),
        isEnabled = valueOrNull(isEnabled),
        keyboardType = valueOrNull(keyboardType),
        layoutProvider = valueOrNull(layoutProvider),
        letterSpacing = valueOrNull(letterSpacing),
        lineHeight = valueOrNull(lineHeight),
        margins = valueOrNull(margins),
        mask = valueOrNull(mask),
        maxLength = valueOrNull(maxLength),
        maxVisibleLines = valueOrNull(maxVisibleLines),
        nativeInterface = valueOrNull(nativeInterface),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectAllOnFocus = valueOrNull(selectAllOnFocus),
        selectedActions = valueOrNull(selectedActions),
        textAlignmentHorizontal = valueOrNull(textAlignmentHorizontal),
        textAlignmentVertical = valueOrNull(textAlignmentVertical),
        textColor = valueOrNull(textColor),
        textVariable = valueOrNull(textVariable),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        validators = valueOrNull(validators),
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param enterKeyActions Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
 * @param enterKeyType 'Enter' key type.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Filter that prevents users from entering text that doesn't satisfy the specified conditions.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param mask Mask for entering text based on the specified template.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param nativeInterface Text input line used in the native interface.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param textVariable Name of text storage variable.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param validators Validator that checks that the field value meets the specified conditions.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.inputProps(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    autocapitalization: Input.Autocapitalization? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    enterKeyActions: List<Action>? = null,
    enterKeyType: Input.EnterKeyType? = null,
    extensions: List<Extension>? = null,
    filters: List<InputFilter>? = null,
    focus: Focus? = null,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontVariationSettings: Map<String, Any>? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    highlightColor: Color? = null,
    hintColor: Color? = null,
    hintText: String? = null,
    id: String? = null,
    isEnabled: Boolean? = null,
    keyboardType: Input.KeyboardType? = null,
    layoutProvider: LayoutProvider? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    margins: EdgeInsets? = null,
    mask: InputMask? = null,
    maxLength: Int? = null,
    maxVisibleLines: Int? = null,
    nativeInterface: Input.NativeInterface? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectAllOnFocus: Boolean? = null,
    selectedActions: List<Action>? = null,
    textAlignmentHorizontal: AlignmentHorizontal? = null,
    textAlignmentVertical: AlignmentVertical? = null,
    textColor: Color? = null,
    textVariable: String? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    validators: List<InputValidator>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
) = Input.Properties(
    accessibility = valueOrNull(accessibility),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    animators = valueOrNull(animators),
    autocapitalization = valueOrNull(autocapitalization),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnSpan = valueOrNull(columnSpan),
    disappearActions = valueOrNull(disappearActions),
    enterKeyActions = valueOrNull(enterKeyActions),
    enterKeyType = valueOrNull(enterKeyType),
    extensions = valueOrNull(extensions),
    filters = valueOrNull(filters),
    focus = valueOrNull(focus),
    fontFamily = valueOrNull(fontFamily),
    fontSize = valueOrNull(fontSize),
    fontSizeUnit = valueOrNull(fontSizeUnit),
    fontVariationSettings = valueOrNull(fontVariationSettings),
    fontWeight = valueOrNull(fontWeight),
    fontWeightValue = valueOrNull(fontWeightValue),
    functions = valueOrNull(functions),
    height = valueOrNull(height),
    highlightColor = valueOrNull(highlightColor),
    hintColor = valueOrNull(hintColor),
    hintText = valueOrNull(hintText),
    id = valueOrNull(id),
    isEnabled = valueOrNull(isEnabled),
    keyboardType = valueOrNull(keyboardType),
    layoutProvider = valueOrNull(layoutProvider),
    letterSpacing = valueOrNull(letterSpacing),
    lineHeight = valueOrNull(lineHeight),
    margins = valueOrNull(margins),
    mask = valueOrNull(mask),
    maxLength = valueOrNull(maxLength),
    maxVisibleLines = valueOrNull(maxVisibleLines),
    nativeInterface = valueOrNull(nativeInterface),
    paddings = valueOrNull(paddings),
    reuseId = valueOrNull(reuseId),
    rowSpan = valueOrNull(rowSpan),
    selectAllOnFocus = valueOrNull(selectAllOnFocus),
    selectedActions = valueOrNull(selectedActions),
    textAlignmentHorizontal = valueOrNull(textAlignmentHorizontal),
    textAlignmentVertical = valueOrNull(textAlignmentVertical),
    textColor = valueOrNull(textColor),
    textVariable = valueOrNull(textVariable),
    tooltips = valueOrNull(tooltips),
    transform = valueOrNull(transform),
    transitionChange = valueOrNull(transitionChange),
    transitionIn = valueOrNull(transitionIn),
    transitionOut = valueOrNull(transitionOut),
    transitionTriggers = valueOrNull(transitionTriggers),
    validators = valueOrNull(validators),
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param enterKeyActions Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
 * @param enterKeyType 'Enter' key type.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Filter that prevents users from entering text that doesn't satisfy the specified conditions.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param mask Mask for entering text based on the specified template.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param nativeInterface Text input line used in the native interface.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param textVariable Name of text storage variable.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param validators Validator that checks that the field value meets the specified conditions.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun TemplateScope.inputRefs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animators: ReferenceProperty<List<Animator>>? = null,
    autocapitalization: ReferenceProperty<Input.Autocapitalization>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    enterKeyActions: ReferenceProperty<List<Action>>? = null,
    enterKeyType: ReferenceProperty<Input.EnterKeyType>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    filters: ReferenceProperty<List<InputFilter>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontVariationSettings: ReferenceProperty<Map<String, Any>>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    fontWeightValue: ReferenceProperty<Int>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    highlightColor: ReferenceProperty<Color>? = null,
    hintColor: ReferenceProperty<Color>? = null,
    hintText: ReferenceProperty<String>? = null,
    id: ReferenceProperty<String>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    keyboardType: ReferenceProperty<Input.KeyboardType>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    mask: ReferenceProperty<InputMask>? = null,
    maxLength: ReferenceProperty<Int>? = null,
    maxVisibleLines: ReferenceProperty<Int>? = null,
    nativeInterface: ReferenceProperty<Input.NativeInterface>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectAllOnFocus: ReferenceProperty<Boolean>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    textAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    textColor: ReferenceProperty<Color>? = null,
    textVariable: ReferenceProperty<String>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    validators: ReferenceProperty<List<InputValidator>>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
) = Input.Properties(
    accessibility = accessibility,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    animators = animators,
    autocapitalization = autocapitalization,
    background = background,
    border = border,
    columnSpan = columnSpan,
    disappearActions = disappearActions,
    enterKeyActions = enterKeyActions,
    enterKeyType = enterKeyType,
    extensions = extensions,
    filters = filters,
    focus = focus,
    fontFamily = fontFamily,
    fontSize = fontSize,
    fontSizeUnit = fontSizeUnit,
    fontVariationSettings = fontVariationSettings,
    fontWeight = fontWeight,
    fontWeightValue = fontWeightValue,
    functions = functions,
    height = height,
    highlightColor = highlightColor,
    hintColor = hintColor,
    hintText = hintText,
    id = id,
    isEnabled = isEnabled,
    keyboardType = keyboardType,
    layoutProvider = layoutProvider,
    letterSpacing = letterSpacing,
    lineHeight = lineHeight,
    margins = margins,
    mask = mask,
    maxLength = maxLength,
    maxVisibleLines = maxVisibleLines,
    nativeInterface = nativeInterface,
    paddings = paddings,
    reuseId = reuseId,
    rowSpan = rowSpan,
    selectAllOnFocus = selectAllOnFocus,
    selectedActions = selectedActions,
    textAlignmentHorizontal = textAlignmentHorizontal,
    textAlignmentVertical = textAlignmentVertical,
    textColor = textColor,
    textVariable = textVariable,
    tooltips = tooltips,
    transform = transform,
    transitionChange = transitionChange,
    transitionIn = transitionIn,
    transitionOut = transitionOut,
    transitionTriggers = transitionTriggers,
    validators = validators,
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param enterKeyActions Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
 * @param enterKeyType 'Enter' key type.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Filter that prevents users from entering text that doesn't satisfy the specified conditions.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param mask Mask for entering text based on the specified template.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param nativeInterface Text input line used in the native interface.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param textVariable Name of text storage variable.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param validators Validator that checks that the field value meets the specified conditions.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Input.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    autocapitalization: Input.Autocapitalization? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    enterKeyActions: List<Action>? = null,
    enterKeyType: Input.EnterKeyType? = null,
    extensions: List<Extension>? = null,
    filters: List<InputFilter>? = null,
    focus: Focus? = null,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontVariationSettings: Map<String, Any>? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    highlightColor: Color? = null,
    hintColor: Color? = null,
    hintText: String? = null,
    id: String? = null,
    isEnabled: Boolean? = null,
    keyboardType: Input.KeyboardType? = null,
    layoutProvider: LayoutProvider? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    margins: EdgeInsets? = null,
    mask: InputMask? = null,
    maxLength: Int? = null,
    maxVisibleLines: Int? = null,
    nativeInterface: Input.NativeInterface? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectAllOnFocus: Boolean? = null,
    selectedActions: List<Action>? = null,
    textAlignmentHorizontal: AlignmentHorizontal? = null,
    textAlignmentVertical: AlignmentVertical? = null,
    textColor: Color? = null,
    textVariable: String? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    validators: List<InputValidator>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Input = Input(
    Input.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        animators = valueOrNull(animators) ?: properties.animators,
        autocapitalization = valueOrNull(autocapitalization) ?: properties.autocapitalization,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        enterKeyActions = valueOrNull(enterKeyActions) ?: properties.enterKeyActions,
        enterKeyType = valueOrNull(enterKeyType) ?: properties.enterKeyType,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        filters = valueOrNull(filters) ?: properties.filters,
        focus = valueOrNull(focus) ?: properties.focus,
        fontFamily = valueOrNull(fontFamily) ?: properties.fontFamily,
        fontSize = valueOrNull(fontSize) ?: properties.fontSize,
        fontSizeUnit = valueOrNull(fontSizeUnit) ?: properties.fontSizeUnit,
        fontVariationSettings = valueOrNull(fontVariationSettings) ?: properties.fontVariationSettings,
        fontWeight = valueOrNull(fontWeight) ?: properties.fontWeight,
        fontWeightValue = valueOrNull(fontWeightValue) ?: properties.fontWeightValue,
        functions = valueOrNull(functions) ?: properties.functions,
        height = valueOrNull(height) ?: properties.height,
        highlightColor = valueOrNull(highlightColor) ?: properties.highlightColor,
        hintColor = valueOrNull(hintColor) ?: properties.hintColor,
        hintText = valueOrNull(hintText) ?: properties.hintText,
        id = valueOrNull(id) ?: properties.id,
        isEnabled = valueOrNull(isEnabled) ?: properties.isEnabled,
        keyboardType = valueOrNull(keyboardType) ?: properties.keyboardType,
        layoutProvider = valueOrNull(layoutProvider) ?: properties.layoutProvider,
        letterSpacing = valueOrNull(letterSpacing) ?: properties.letterSpacing,
        lineHeight = valueOrNull(lineHeight) ?: properties.lineHeight,
        margins = valueOrNull(margins) ?: properties.margins,
        mask = valueOrNull(mask) ?: properties.mask,
        maxLength = valueOrNull(maxLength) ?: properties.maxLength,
        maxVisibleLines = valueOrNull(maxVisibleLines) ?: properties.maxVisibleLines,
        nativeInterface = valueOrNull(nativeInterface) ?: properties.nativeInterface,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        reuseId = valueOrNull(reuseId) ?: properties.reuseId,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectAllOnFocus = valueOrNull(selectAllOnFocus) ?: properties.selectAllOnFocus,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        textAlignmentHorizontal = valueOrNull(textAlignmentHorizontal) ?: properties.textAlignmentHorizontal,
        textAlignmentVertical = valueOrNull(textAlignmentVertical) ?: properties.textAlignmentVertical,
        textColor = valueOrNull(textColor) ?: properties.textColor,
        textVariable = valueOrNull(textVariable) ?: properties.textVariable,
        tooltips = valueOrNull(tooltips) ?: properties.tooltips,
        transform = valueOrNull(transform) ?: properties.transform,
        transitionChange = valueOrNull(transitionChange) ?: properties.transitionChange,
        transitionIn = valueOrNull(transitionIn) ?: properties.transitionIn,
        transitionOut = valueOrNull(transitionOut) ?: properties.transitionOut,
        transitionTriggers = valueOrNull(transitionTriggers) ?: properties.transitionTriggers,
        validators = valueOrNull(validators) ?: properties.validators,
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param enterKeyActions Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
 * @param enterKeyType 'Enter' key type.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Filter that prevents users from entering text that doesn't satisfy the specified conditions.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param mask Mask for entering text based on the specified template.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param nativeInterface Text input line used in the native interface.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param textVariable Name of text storage variable.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param validators Validator that checks that the field value meets the specified conditions.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Input.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animators: ReferenceProperty<List<Animator>>? = null,
    autocapitalization: ReferenceProperty<Input.Autocapitalization>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    enterKeyActions: ReferenceProperty<List<Action>>? = null,
    enterKeyType: ReferenceProperty<Input.EnterKeyType>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    filters: ReferenceProperty<List<InputFilter>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontVariationSettings: ReferenceProperty<Map<String, Any>>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    fontWeightValue: ReferenceProperty<Int>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    highlightColor: ReferenceProperty<Color>? = null,
    hintColor: ReferenceProperty<Color>? = null,
    hintText: ReferenceProperty<String>? = null,
    id: ReferenceProperty<String>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    keyboardType: ReferenceProperty<Input.KeyboardType>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    mask: ReferenceProperty<InputMask>? = null,
    maxLength: ReferenceProperty<Int>? = null,
    maxVisibleLines: ReferenceProperty<Int>? = null,
    nativeInterface: ReferenceProperty<Input.NativeInterface>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectAllOnFocus: ReferenceProperty<Boolean>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    textAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    textColor: ReferenceProperty<Color>? = null,
    textVariable: ReferenceProperty<String>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    validators: ReferenceProperty<List<InputValidator>>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Input = Input(
    Input.Properties(
        accessibility = accessibility ?: properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animators = animators ?: properties.animators,
        autocapitalization = autocapitalization ?: properties.autocapitalization,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = disappearActions ?: properties.disappearActions,
        enterKeyActions = enterKeyActions ?: properties.enterKeyActions,
        enterKeyType = enterKeyType ?: properties.enterKeyType,
        extensions = extensions ?: properties.extensions,
        filters = filters ?: properties.filters,
        focus = focus ?: properties.focus,
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontVariationSettings = fontVariationSettings ?: properties.fontVariationSettings,
        fontWeight = fontWeight ?: properties.fontWeight,
        fontWeightValue = fontWeightValue ?: properties.fontWeightValue,
        functions = functions ?: properties.functions,
        height = height ?: properties.height,
        highlightColor = highlightColor ?: properties.highlightColor,
        hintColor = hintColor ?: properties.hintColor,
        hintText = hintText ?: properties.hintText,
        id = id ?: properties.id,
        isEnabled = isEnabled ?: properties.isEnabled,
        keyboardType = keyboardType ?: properties.keyboardType,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        margins = margins ?: properties.margins,
        mask = mask ?: properties.mask,
        maxLength = maxLength ?: properties.maxLength,
        maxVisibleLines = maxVisibleLines ?: properties.maxVisibleLines,
        nativeInterface = nativeInterface ?: properties.nativeInterface,
        paddings = paddings ?: properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectAllOnFocus = selectAllOnFocus ?: properties.selectAllOnFocus,
        selectedActions = selectedActions ?: properties.selectedActions,
        textAlignmentHorizontal = textAlignmentHorizontal ?: properties.textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical ?: properties.textAlignmentVertical,
        textColor = textColor ?: properties.textColor,
        textVariable = textVariable ?: properties.textVariable,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
        transitionChange = transitionChange ?: properties.transitionChange,
        transitionIn = transitionIn ?: properties.transitionIn,
        transitionOut = transitionOut ?: properties.transitionOut,
        transitionTriggers = transitionTriggers ?: properties.transitionTriggers,
        validators = validators ?: properties.validators,
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param enterKeyActions Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
 * @param enterKeyType 'Enter' key type.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Filter that prevents users from entering text that doesn't satisfy the specified conditions.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param mask Mask for entering text based on the specified template.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param nativeInterface Text input line used in the native interface.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param textVariable Name of text storage variable.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param validators Validator that checks that the field value meets the specified conditions.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Input.modify(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Property<Accessibility>? = null,
    alignmentHorizontal: Property<AlignmentHorizontal>? = null,
    alignmentVertical: Property<AlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    animators: Property<List<Animator>>? = null,
    autocapitalization: Property<Input.Autocapitalization>? = null,
    background: Property<List<Background>>? = null,
    border: Property<Border>? = null,
    columnSpan: Property<Int>? = null,
    disappearActions: Property<List<DisappearAction>>? = null,
    enterKeyActions: Property<List<Action>>? = null,
    enterKeyType: Property<Input.EnterKeyType>? = null,
    extensions: Property<List<Extension>>? = null,
    filters: Property<List<InputFilter>>? = null,
    focus: Property<Focus>? = null,
    fontFamily: Property<String>? = null,
    fontSize: Property<Int>? = null,
    fontSizeUnit: Property<SizeUnit>? = null,
    fontVariationSettings: Property<Map<String, Any>>? = null,
    fontWeight: Property<FontWeight>? = null,
    fontWeightValue: Property<Int>? = null,
    functions: Property<List<Function>>? = null,
    height: Property<Size>? = null,
    highlightColor: Property<Color>? = null,
    hintColor: Property<Color>? = null,
    hintText: Property<String>? = null,
    id: Property<String>? = null,
    isEnabled: Property<Boolean>? = null,
    keyboardType: Property<Input.KeyboardType>? = null,
    layoutProvider: Property<LayoutProvider>? = null,
    letterSpacing: Property<Double>? = null,
    lineHeight: Property<Int>? = null,
    margins: Property<EdgeInsets>? = null,
    mask: Property<InputMask>? = null,
    maxLength: Property<Int>? = null,
    maxVisibleLines: Property<Int>? = null,
    nativeInterface: Property<Input.NativeInterface>? = null,
    paddings: Property<EdgeInsets>? = null,
    reuseId: Property<String>? = null,
    rowSpan: Property<Int>? = null,
    selectAllOnFocus: Property<Boolean>? = null,
    selectedActions: Property<List<Action>>? = null,
    textAlignmentHorizontal: Property<AlignmentHorizontal>? = null,
    textAlignmentVertical: Property<AlignmentVertical>? = null,
    textColor: Property<Color>? = null,
    textVariable: Property<String>? = null,
    tooltips: Property<List<Tooltip>>? = null,
    transform: Property<Transform>? = null,
    transitionChange: Property<ChangeTransition>? = null,
    transitionIn: Property<AppearanceTransition>? = null,
    transitionOut: Property<AppearanceTransition>? = null,
    transitionTriggers: Property<List<ArrayElement<TransitionTrigger>>>? = null,
    validators: Property<List<InputValidator>>? = null,
    variableTriggers: Property<List<Trigger>>? = null,
    variables: Property<List<Variable>>? = null,
    visibility: Property<Visibility>? = null,
    visibilityAction: Property<VisibilityAction>? = null,
    visibilityActions: Property<List<VisibilityAction>>? = null,
    width: Property<Size>? = null,
): Input = Input(
    Input.Properties(
        accessibility = accessibility ?: properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animators = animators ?: properties.animators,
        autocapitalization = autocapitalization ?: properties.autocapitalization,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = disappearActions ?: properties.disappearActions,
        enterKeyActions = enterKeyActions ?: properties.enterKeyActions,
        enterKeyType = enterKeyType ?: properties.enterKeyType,
        extensions = extensions ?: properties.extensions,
        filters = filters ?: properties.filters,
        focus = focus ?: properties.focus,
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontVariationSettings = fontVariationSettings ?: properties.fontVariationSettings,
        fontWeight = fontWeight ?: properties.fontWeight,
        fontWeightValue = fontWeightValue ?: properties.fontWeightValue,
        functions = functions ?: properties.functions,
        height = height ?: properties.height,
        highlightColor = highlightColor ?: properties.highlightColor,
        hintColor = hintColor ?: properties.hintColor,
        hintText = hintText ?: properties.hintText,
        id = id ?: properties.id,
        isEnabled = isEnabled ?: properties.isEnabled,
        keyboardType = keyboardType ?: properties.keyboardType,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        margins = margins ?: properties.margins,
        mask = mask ?: properties.mask,
        maxLength = maxLength ?: properties.maxLength,
        maxVisibleLines = maxVisibleLines ?: properties.maxVisibleLines,
        nativeInterface = nativeInterface ?: properties.nativeInterface,
        paddings = paddings ?: properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectAllOnFocus = selectAllOnFocus ?: properties.selectAllOnFocus,
        selectedActions = selectedActions ?: properties.selectedActions,
        textAlignmentHorizontal = textAlignmentHorizontal ?: properties.textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical ?: properties.textAlignmentVertical,
        textColor = textColor ?: properties.textColor,
        textVariable = textVariable ?: properties.textVariable,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
        transitionChange = transitionChange ?: properties.transitionChange,
        transitionIn = transitionIn ?: properties.transitionIn,
        transitionOut = transitionOut ?: properties.transitionOut,
        transitionTriggers = transitionTriggers ?: properties.transitionTriggers,
        validators = validators ?: properties.validators,
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param enterKeyType 'Enter' key type.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param visibility Element visibility.
 */
@Generated
fun Input.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    autocapitalization: ExpressionProperty<Input.Autocapitalization>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    enterKeyType: ExpressionProperty<Input.EnterKeyType>? = null,
    fontFamily: ExpressionProperty<String>? = null,
    fontSize: ExpressionProperty<Int>? = null,
    fontSizeUnit: ExpressionProperty<SizeUnit>? = null,
    fontWeight: ExpressionProperty<FontWeight>? = null,
    fontWeightValue: ExpressionProperty<Int>? = null,
    highlightColor: ExpressionProperty<Color>? = null,
    hintColor: ExpressionProperty<Color>? = null,
    hintText: ExpressionProperty<String>? = null,
    isEnabled: ExpressionProperty<Boolean>? = null,
    keyboardType: ExpressionProperty<Input.KeyboardType>? = null,
    letterSpacing: ExpressionProperty<Double>? = null,
    lineHeight: ExpressionProperty<Int>? = null,
    maxLength: ExpressionProperty<Int>? = null,
    maxVisibleLines: ExpressionProperty<Int>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    selectAllOnFocus: ExpressionProperty<Boolean>? = null,
    textAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    textColor: ExpressionProperty<Color>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Input = Input(
    Input.Properties(
        accessibility = properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        animators = properties.animators,
        autocapitalization = autocapitalization ?: properties.autocapitalization,
        background = properties.background,
        border = properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = properties.disappearActions,
        enterKeyActions = properties.enterKeyActions,
        enterKeyType = enterKeyType ?: properties.enterKeyType,
        extensions = properties.extensions,
        filters = properties.filters,
        focus = properties.focus,
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontVariationSettings = properties.fontVariationSettings,
        fontWeight = fontWeight ?: properties.fontWeight,
        fontWeightValue = fontWeightValue ?: properties.fontWeightValue,
        functions = properties.functions,
        height = properties.height,
        highlightColor = highlightColor ?: properties.highlightColor,
        hintColor = hintColor ?: properties.hintColor,
        hintText = hintText ?: properties.hintText,
        id = properties.id,
        isEnabled = isEnabled ?: properties.isEnabled,
        keyboardType = keyboardType ?: properties.keyboardType,
        layoutProvider = properties.layoutProvider,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        margins = properties.margins,
        mask = properties.mask,
        maxLength = maxLength ?: properties.maxLength,
        maxVisibleLines = maxVisibleLines ?: properties.maxVisibleLines,
        nativeInterface = properties.nativeInterface,
        paddings = properties.paddings,
        reuseId = reuseId ?: properties.reuseId,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectAllOnFocus = selectAllOnFocus ?: properties.selectAllOnFocus,
        selectedActions = properties.selectedActions,
        textAlignmentHorizontal = textAlignmentHorizontal ?: properties.textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical ?: properties.textAlignmentVertical,
        textColor = textColor ?: properties.textColor,
        textVariable = properties.textVariable,
        tooltips = properties.tooltips,
        transform = properties.transform,
        transitionChange = properties.transitionChange,
        transitionIn = properties.transitionIn,
        transitionOut = properties.transitionOut,
        transitionTriggers = properties.transitionTriggers,
        validators = properties.validators,
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param enterKeyActions Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
 * @param enterKeyType 'Enter' key type.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Filter that prevents users from entering text that doesn't satisfy the specified conditions.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param mask Mask for entering text based on the specified template.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param nativeInterface Text input line used in the native interface.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param textVariable Name of text storage variable.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param validators Validator that checks that the field value meets the specified conditions.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Input>.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    animators: List<Animator>? = null,
    autocapitalization: Input.Autocapitalization? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    enterKeyActions: List<Action>? = null,
    enterKeyType: Input.EnterKeyType? = null,
    extensions: List<Extension>? = null,
    filters: List<InputFilter>? = null,
    focus: Focus? = null,
    fontFamily: String? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontVariationSettings: Map<String, Any>? = null,
    fontWeight: FontWeight? = null,
    fontWeightValue: Int? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    highlightColor: Color? = null,
    hintColor: Color? = null,
    hintText: String? = null,
    id: String? = null,
    isEnabled: Boolean? = null,
    keyboardType: Input.KeyboardType? = null,
    layoutProvider: LayoutProvider? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    margins: EdgeInsets? = null,
    mask: InputMask? = null,
    maxLength: Int? = null,
    maxVisibleLines: Int? = null,
    nativeInterface: Input.NativeInterface? = null,
    paddings: EdgeInsets? = null,
    reuseId: String? = null,
    rowSpan: Int? = null,
    selectAllOnFocus: Boolean? = null,
    selectedActions: List<Action>? = null,
    textAlignmentHorizontal: AlignmentHorizontal? = null,
    textAlignmentVertical: AlignmentVertical? = null,
    textColor: Color? = null,
    textVariable: String? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    validators: List<InputValidator>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Component<Input> = Component(
    template = template,
    properties = Input.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        animators = valueOrNull(animators),
        autocapitalization = valueOrNull(autocapitalization),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        disappearActions = valueOrNull(disappearActions),
        enterKeyActions = valueOrNull(enterKeyActions),
        enterKeyType = valueOrNull(enterKeyType),
        extensions = valueOrNull(extensions),
        filters = valueOrNull(filters),
        focus = valueOrNull(focus),
        fontFamily = valueOrNull(fontFamily),
        fontSize = valueOrNull(fontSize),
        fontSizeUnit = valueOrNull(fontSizeUnit),
        fontVariationSettings = valueOrNull(fontVariationSettings),
        fontWeight = valueOrNull(fontWeight),
        fontWeightValue = valueOrNull(fontWeightValue),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        highlightColor = valueOrNull(highlightColor),
        hintColor = valueOrNull(hintColor),
        hintText = valueOrNull(hintText),
        id = valueOrNull(id),
        isEnabled = valueOrNull(isEnabled),
        keyboardType = valueOrNull(keyboardType),
        layoutProvider = valueOrNull(layoutProvider),
        letterSpacing = valueOrNull(letterSpacing),
        lineHeight = valueOrNull(lineHeight),
        margins = valueOrNull(margins),
        mask = valueOrNull(mask),
        maxLength = valueOrNull(maxLength),
        maxVisibleLines = valueOrNull(maxVisibleLines),
        nativeInterface = valueOrNull(nativeInterface),
        paddings = valueOrNull(paddings),
        reuseId = valueOrNull(reuseId),
        rowSpan = valueOrNull(rowSpan),
        selectAllOnFocus = valueOrNull(selectAllOnFocus),
        selectedActions = valueOrNull(selectedActions),
        textAlignmentHorizontal = valueOrNull(textAlignmentHorizontal),
        textAlignmentVertical = valueOrNull(textAlignmentVertical),
        textColor = valueOrNull(textColor),
        textVariable = valueOrNull(textVariable),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        validators = valueOrNull(validators),
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param enterKeyActions Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
 * @param enterKeyType 'Enter' key type.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Filter that prevents users from entering text that doesn't satisfy the specified conditions.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param mask Mask for entering text based on the specified template.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param nativeInterface Text input line used in the native interface.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param textVariable Name of text storage variable.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param validators Validator that checks that the field value meets the specified conditions.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Input>.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    animators: ReferenceProperty<List<Animator>>? = null,
    autocapitalization: ReferenceProperty<Input.Autocapitalization>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    enterKeyActions: ReferenceProperty<List<Action>>? = null,
    enterKeyType: ReferenceProperty<Input.EnterKeyType>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    filters: ReferenceProperty<List<InputFilter>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    fontFamily: ReferenceProperty<String>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontVariationSettings: ReferenceProperty<Map<String, Any>>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    fontWeightValue: ReferenceProperty<Int>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    highlightColor: ReferenceProperty<Color>? = null,
    hintColor: ReferenceProperty<Color>? = null,
    hintText: ReferenceProperty<String>? = null,
    id: ReferenceProperty<String>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    keyboardType: ReferenceProperty<Input.KeyboardType>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    mask: ReferenceProperty<InputMask>? = null,
    maxLength: ReferenceProperty<Int>? = null,
    maxVisibleLines: ReferenceProperty<Int>? = null,
    nativeInterface: ReferenceProperty<Input.NativeInterface>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    reuseId: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectAllOnFocus: ReferenceProperty<Boolean>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    textAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    textColor: ReferenceProperty<Color>? = null,
    textVariable: ReferenceProperty<String>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    validators: ReferenceProperty<List<InputValidator>>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Component<Input> = Component(
    template = template,
    properties = Input.Properties(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animators = animators,
        autocapitalization = autocapitalization,
        background = background,
        border = border,
        columnSpan = columnSpan,
        disappearActions = disappearActions,
        enterKeyActions = enterKeyActions,
        enterKeyType = enterKeyType,
        extensions = extensions,
        filters = filters,
        focus = focus,
        fontFamily = fontFamily,
        fontSize = fontSize,
        fontSizeUnit = fontSizeUnit,
        fontVariationSettings = fontVariationSettings,
        fontWeight = fontWeight,
        fontWeightValue = fontWeightValue,
        functions = functions,
        height = height,
        highlightColor = highlightColor,
        hintColor = hintColor,
        hintText = hintText,
        id = id,
        isEnabled = isEnabled,
        keyboardType = keyboardType,
        layoutProvider = layoutProvider,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        margins = margins,
        mask = mask,
        maxLength = maxLength,
        maxVisibleLines = maxVisibleLines,
        nativeInterface = nativeInterface,
        paddings = paddings,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectAllOnFocus = selectAllOnFocus,
        selectedActions = selectedActions,
        textAlignmentHorizontal = textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical,
        textColor = textColor,
        textVariable = textVariable,
        tooltips = tooltips,
        transform = transform,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        validators = validators,
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param enterKeyType 'Enter' key type.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Input>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    autocapitalization: ExpressionProperty<Input.Autocapitalization>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    enterKeyType: ExpressionProperty<Input.EnterKeyType>? = null,
    fontFamily: ExpressionProperty<String>? = null,
    fontSize: ExpressionProperty<Int>? = null,
    fontSizeUnit: ExpressionProperty<SizeUnit>? = null,
    fontWeight: ExpressionProperty<FontWeight>? = null,
    fontWeightValue: ExpressionProperty<Int>? = null,
    highlightColor: ExpressionProperty<Color>? = null,
    hintColor: ExpressionProperty<Color>? = null,
    hintText: ExpressionProperty<String>? = null,
    isEnabled: ExpressionProperty<Boolean>? = null,
    keyboardType: ExpressionProperty<Input.KeyboardType>? = null,
    letterSpacing: ExpressionProperty<Double>? = null,
    lineHeight: ExpressionProperty<Int>? = null,
    maxLength: ExpressionProperty<Int>? = null,
    maxVisibleLines: ExpressionProperty<Int>? = null,
    reuseId: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    selectAllOnFocus: ExpressionProperty<Boolean>? = null,
    textAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    textColor: ExpressionProperty<Color>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Input> = Component(
    template = template,
    properties = Input.Properties(
        accessibility = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animators = null,
        autocapitalization = autocapitalization,
        background = null,
        border = null,
        columnSpan = columnSpan,
        disappearActions = null,
        enterKeyActions = null,
        enterKeyType = enterKeyType,
        extensions = null,
        filters = null,
        focus = null,
        fontFamily = fontFamily,
        fontSize = fontSize,
        fontSizeUnit = fontSizeUnit,
        fontVariationSettings = null,
        fontWeight = fontWeight,
        fontWeightValue = fontWeightValue,
        functions = null,
        height = null,
        highlightColor = highlightColor,
        hintColor = hintColor,
        hintText = hintText,
        id = null,
        isEnabled = isEnabled,
        keyboardType = keyboardType,
        layoutProvider = null,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        margins = null,
        mask = null,
        maxLength = maxLength,
        maxVisibleLines = maxVisibleLines,
        nativeInterface = null,
        paddings = null,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectAllOnFocus = selectAllOnFocus,
        selectedActions = null,
        textAlignmentHorizontal = textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical,
        textColor = textColor,
        textVariable = null,
        tooltips = null,
        transform = null,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        validators = null,
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
 * @param autocapitalization Text auto-capitalization type. <li>By default: `auto` - default behavior of the platform;</li><li>`none' - automatic capitalization is not applied;</li><li>`words` - capitalization of each word;</li><li>`sentences` - capitalization at the beginning of a sentence;</li><li>`all_characters' - capitalization of each character.</li>
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param enterKeyActions Actions when pressing the 'Enter' key. Actions (if any) override the default behavior.
 * @param enterKeyType 'Enter' key type.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Filter that prevents users from entering text that doesn't satisfy the specified conditions.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontVariationSettings List of TrueType/OpenType font features. The object is constructed from pairs of axis tag and style values. The axis tag must contain four ASCII characters.
 * @param fontWeight Style.
 * @param fontWeightValue Style. Numeric value.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highlightColor Text highlight color. If the value isn't set, the color set in the client will be used instead.
 * @param hintColor Text color.
 * @param hintText Tooltip text.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param isEnabled Enables or disables text editing.
 * @param keyboardType Keyboard type.
 * @param layoutProvider Provides data on the actual size of the element.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param margins External margins from the element stroke.
 * @param mask Mask for entering text based on the specified template.
 * @param maxLength Maximum number of characters that can be entered in the input field.
 * @param maxVisibleLines Maximum number of lines to be displayed in the input field.
 * @param nativeInterface Text input line used in the native interface.
 * @param paddings Internal margins from the element stroke.
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectAllOnFocus Highlighting input text when focused.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color.
 * @param textVariable Name of text storage variable.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param validators Validator that checks that the field value meets the specified conditions.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Input>.modify(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Property<Accessibility>? = null,
    alignmentHorizontal: Property<AlignmentHorizontal>? = null,
    alignmentVertical: Property<AlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    animators: Property<List<Animator>>? = null,
    autocapitalization: Property<Input.Autocapitalization>? = null,
    background: Property<List<Background>>? = null,
    border: Property<Border>? = null,
    columnSpan: Property<Int>? = null,
    disappearActions: Property<List<DisappearAction>>? = null,
    enterKeyActions: Property<List<Action>>? = null,
    enterKeyType: Property<Input.EnterKeyType>? = null,
    extensions: Property<List<Extension>>? = null,
    filters: Property<List<InputFilter>>? = null,
    focus: Property<Focus>? = null,
    fontFamily: Property<String>? = null,
    fontSize: Property<Int>? = null,
    fontSizeUnit: Property<SizeUnit>? = null,
    fontVariationSettings: Property<Map<String, Any>>? = null,
    fontWeight: Property<FontWeight>? = null,
    fontWeightValue: Property<Int>? = null,
    functions: Property<List<Function>>? = null,
    height: Property<Size>? = null,
    highlightColor: Property<Color>? = null,
    hintColor: Property<Color>? = null,
    hintText: Property<String>? = null,
    id: Property<String>? = null,
    isEnabled: Property<Boolean>? = null,
    keyboardType: Property<Input.KeyboardType>? = null,
    layoutProvider: Property<LayoutProvider>? = null,
    letterSpacing: Property<Double>? = null,
    lineHeight: Property<Int>? = null,
    margins: Property<EdgeInsets>? = null,
    mask: Property<InputMask>? = null,
    maxLength: Property<Int>? = null,
    maxVisibleLines: Property<Int>? = null,
    nativeInterface: Property<Input.NativeInterface>? = null,
    paddings: Property<EdgeInsets>? = null,
    reuseId: Property<String>? = null,
    rowSpan: Property<Int>? = null,
    selectAllOnFocus: Property<Boolean>? = null,
    selectedActions: Property<List<Action>>? = null,
    textAlignmentHorizontal: Property<AlignmentHorizontal>? = null,
    textAlignmentVertical: Property<AlignmentVertical>? = null,
    textColor: Property<Color>? = null,
    textVariable: Property<String>? = null,
    tooltips: Property<List<Tooltip>>? = null,
    transform: Property<Transform>? = null,
    transitionChange: Property<ChangeTransition>? = null,
    transitionIn: Property<AppearanceTransition>? = null,
    transitionOut: Property<AppearanceTransition>? = null,
    transitionTriggers: Property<List<ArrayElement<TransitionTrigger>>>? = null,
    validators: Property<List<InputValidator>>? = null,
    variableTriggers: Property<List<Trigger>>? = null,
    variables: Property<List<Variable>>? = null,
    visibility: Property<Visibility>? = null,
    visibilityAction: Property<VisibilityAction>? = null,
    visibilityActions: Property<List<VisibilityAction>>? = null,
    width: Property<Size>? = null,
): Component<Input> = Component(
    template = template,
    properties = Input.Properties(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        animators = animators,
        autocapitalization = autocapitalization,
        background = background,
        border = border,
        columnSpan = columnSpan,
        disappearActions = disappearActions,
        enterKeyActions = enterKeyActions,
        enterKeyType = enterKeyType,
        extensions = extensions,
        filters = filters,
        focus = focus,
        fontFamily = fontFamily,
        fontSize = fontSize,
        fontSizeUnit = fontSizeUnit,
        fontVariationSettings = fontVariationSettings,
        fontWeight = fontWeight,
        fontWeightValue = fontWeightValue,
        functions = functions,
        height = height,
        highlightColor = highlightColor,
        hintColor = hintColor,
        hintText = hintText,
        id = id,
        isEnabled = isEnabled,
        keyboardType = keyboardType,
        layoutProvider = layoutProvider,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        margins = margins,
        mask = mask,
        maxLength = maxLength,
        maxVisibleLines = maxVisibleLines,
        nativeInterface = nativeInterface,
        paddings = paddings,
        reuseId = reuseId,
        rowSpan = rowSpan,
        selectAllOnFocus = selectAllOnFocus,
        selectedActions = selectedActions,
        textAlignmentHorizontal = textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical,
        textColor = textColor,
        textVariable = textVariable,
        tooltips = tooltips,
        transform = transform,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        validators = validators,
        variableTriggers = variableTriggers,
        variables = variables,
        visibility = visibility,
        visibilityAction = visibilityAction,
        visibilityActions = visibilityActions,
        width = width,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Input>.plus(additive: Input.Properties): Component<Input> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Input.asList() = listOf(this)

@Generated
fun Input.Autocapitalization.asList() = listOf(this)

@Generated
fun Input.EnterKeyType.asList() = listOf(this)

@Generated
fun Input.KeyboardType.asList() = listOf(this)

/**
 * @param color Text input line color.
 */
@Generated
fun DivScope.inputNativeInterface(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
): Input.NativeInterface = Input.NativeInterface(
    Input.NativeInterface.Properties(
        color = valueOrNull(color),
    )
)

/**
 * @param color Text input line color.
 */
@Generated
fun DivScope.inputNativeInterfaceProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
) = Input.NativeInterface.Properties(
    color = valueOrNull(color),
)

/**
 * @param color Text input line color.
 */
@Generated
fun TemplateScope.inputNativeInterfaceRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
) = Input.NativeInterface.Properties(
    color = color,
)

/**
 * @param color Text input line color.
 */
@Generated
fun Input.NativeInterface.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
): Input.NativeInterface = Input.NativeInterface(
    Input.NativeInterface.Properties(
        color = valueOrNull(color) ?: properties.color,
    )
)

/**
 * @param color Text input line color.
 */
@Generated
fun Input.NativeInterface.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
): Input.NativeInterface = Input.NativeInterface(
    Input.NativeInterface.Properties(
        color = color ?: properties.color,
    )
)

/**
 * @param color Text input line color.
 */
@Generated
fun Input.NativeInterface.modify(
    `use named arguments`: Guard = Guard.instance,
    color: Property<Color>? = null,
): Input.NativeInterface = Input.NativeInterface(
    Input.NativeInterface.Properties(
        color = color ?: properties.color,
    )
)

/**
 * @param color Text input line color.
 */
@Generated
fun Input.NativeInterface.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
): Input.NativeInterface = Input.NativeInterface(
    Input.NativeInterface.Properties(
        color = color ?: properties.color,
    )
)

@Generated
fun Input.NativeInterface.asList() = listOf(this)
