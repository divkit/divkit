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
 * Text.
 * 
 * Can be created using the method [text].
 * 
 * Required parameters: `type, text`.
 */
@Generated
class Text internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "text")
    )

    operator fun plus(additive: Properties): Text = Text(
        Properties(
            text = additive.text ?: properties.text,
            accessibility = additive.accessibility ?: properties.accessibility,
            action = additive.action ?: properties.action,
            actionAnimation = additive.actionAnimation ?: properties.actionAnimation,
            actions = additive.actions ?: properties.actions,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            autoEllipsize = additive.autoEllipsize ?: properties.autoEllipsize,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            doubletapActions = additive.doubletapActions ?: properties.doubletapActions,
            ellipsis = additive.ellipsis ?: properties.ellipsis,
            extensions = additive.extensions ?: properties.extensions,
            focus = additive.focus ?: properties.focus,
            focusedTextColor = additive.focusedTextColor ?: properties.focusedTextColor,
            fontFamily = additive.fontFamily ?: properties.fontFamily,
            fontSize = additive.fontSize ?: properties.fontSize,
            fontSizeUnit = additive.fontSizeUnit ?: properties.fontSizeUnit,
            fontWeight = additive.fontWeight ?: properties.fontWeight,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
            images = additive.images ?: properties.images,
            letterSpacing = additive.letterSpacing ?: properties.letterSpacing,
            lineHeight = additive.lineHeight ?: properties.lineHeight,
            longtapActions = additive.longtapActions ?: properties.longtapActions,
            margins = additive.margins ?: properties.margins,
            maxLines = additive.maxLines ?: properties.maxLines,
            minHiddenLines = additive.minHiddenLines ?: properties.minHiddenLines,
            paddings = additive.paddings ?: properties.paddings,
            ranges = additive.ranges ?: properties.ranges,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            selectable = additive.selectable ?: properties.selectable,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            strike = additive.strike ?: properties.strike,
            textAlignmentHorizontal = additive.textAlignmentHorizontal ?: properties.textAlignmentHorizontal,
            textAlignmentVertical = additive.textAlignmentVertical ?: properties.textAlignmentVertical,
            textColor = additive.textColor ?: properties.textColor,
            textGradient = additive.textGradient ?: properties.textGradient,
            tooltips = additive.tooltips ?: properties.tooltips,
            transform = additive.transform ?: properties.transform,
            transitionChange = additive.transitionChange ?: properties.transitionChange,
            transitionIn = additive.transitionIn ?: properties.transitionIn,
            transitionOut = additive.transitionOut ?: properties.transitionOut,
            transitionTriggers = additive.transitionTriggers ?: properties.transitionTriggers,
            truncate = additive.truncate ?: properties.truncate,
            underline = additive.underline ?: properties.underline,
            visibility = additive.visibility ?: properties.visibility,
            visibilityAction = additive.visibilityAction ?: properties.visibilityAction,
            visibilityActions = additive.visibilityActions ?: properties.visibilityActions,
            width = additive.width ?: properties.width,
        )
    )

    class Properties internal constructor(
        /**
         * Text.
         */
        val text: Property<String>?,
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
         * Automatic text cropping to fit the container size.
         */
        val autoEllipsize: Property<Boolean>?,
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
         * Action when double-clicking on an element.
         */
        val doubletapActions: Property<List<Action>>?,
        /**
         * Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
         */
        val ellipsis: Property<Ellipsis>?,
        /**
         * Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
         */
        val extensions: Property<List<Extension>>?,
        /**
         * Parameters when focusing on an element or losing focus.
         */
        val focus: Property<Focus>?,
        /**
         * Text color when focusing on the element.
         */
        val focusedTextColor: Property<Color>?,
        /**
         * Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
         * Default value: `text`.
         */
        val fontFamily: Property<FontFamily>?,
        /**
         * Font size.
         * Default value: `12`.
         */
        val fontSize: Property<Int>?,
        /**
         * Default value: `sp`.
         */
        val fontSizeUnit: Property<SizeUnit>?,
        /**
         * Style.
         * Default value: `regular`.
         */
        val fontWeight: Property<FontWeight>?,
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
         * Images embedded in text.
         */
        val images: Property<List<Image>>?,
        /**
         * Spacing between characters.
         * Default value: `0`.
         */
        val letterSpacing: Property<Double>?,
        /**
         * Line spacing of the text.
         */
        val lineHeight: Property<Int>?,
        /**
         * Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
         */
        val longtapActions: Property<List<Action>>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Maximum number of lines not to be cropped when breaking the limits.
         */
        val maxLines: Property<Int>?,
        /**
         * Minimum number of cropped lines when breaking the limits.
         */
        val minHiddenLines: Property<Int>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
        /**
         * A character range in which additional style parameters can be set. Defined by mandatory `start` and `end` fields.
         */
        val ranges: Property<List<Range>>?,
        /**
         * Merges cells in a string of the [grid](div-grid.md) element.
         */
        val rowSpan: Property<Int>?,
        /**
         * Ability to select and copy text.
         * Default value: `false`.
         */
        val selectable: Property<Boolean>?,
        /**
         * List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
         */
        val selectedActions: Property<List<Action>>?,
        /**
         * Strikethrough.
         * Default value: `none`.
         */
        val strike: Property<LineStyle>?,
        /**
         * Horizontal text alignment.
         * Default value: `left`.
         */
        val textAlignmentHorizontal: Property<AlignmentHorizontal>?,
        /**
         * Vertical text alignment.
         * Default value: `top`.
         */
        val textAlignmentVertical: Property<AlignmentVertical>?,
        /**
         * Text color. Not used if the `text_gradient` parameter is set.
         * Default value: `#FF000000`.
         */
        val textColor: Property<Color>?,
        /**
         * Gradient text color.
         */
        val textGradient: Property<TextGradient>?,
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
         * Text cropping method. Use `ellipsis` instead.
         * Default value: `end`.
         */
        @Deprecated("Marked as deprecated in json schema")
        val truncate: Property<Truncate>?,
        /**
         * Underline.
         * Default value: `none`.
         */
        val underline: Property<LineStyle>?,
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
            result.tryPutProperty("text", text)
            result.tryPutProperty("accessibility", accessibility)
            result.tryPutProperty("action", action)
            result.tryPutProperty("action_animation", actionAnimation)
            result.tryPutProperty("actions", actions)
            result.tryPutProperty("alignment_horizontal", alignmentHorizontal)
            result.tryPutProperty("alignment_vertical", alignmentVertical)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("auto_ellipsize", autoEllipsize)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("doubletap_actions", doubletapActions)
            result.tryPutProperty("ellipsis", ellipsis)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("focused_text_color", focusedTextColor)
            result.tryPutProperty("font_family", fontFamily)
            result.tryPutProperty("font_size", fontSize)
            result.tryPutProperty("font_size_unit", fontSizeUnit)
            result.tryPutProperty("font_weight", fontWeight)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
            result.tryPutProperty("images", images)
            result.tryPutProperty("letter_spacing", letterSpacing)
            result.tryPutProperty("line_height", lineHeight)
            result.tryPutProperty("longtap_actions", longtapActions)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("max_lines", maxLines)
            result.tryPutProperty("min_hidden_lines", minHiddenLines)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("ranges", ranges)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("selectable", selectable)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("strike", strike)
            result.tryPutProperty("text_alignment_horizontal", textAlignmentHorizontal)
            result.tryPutProperty("text_alignment_vertical", textAlignmentVertical)
            result.tryPutProperty("text_color", textColor)
            result.tryPutProperty("text_gradient", textGradient)
            result.tryPutProperty("tooltips", tooltips)
            result.tryPutProperty("transform", transform)
            result.tryPutProperty("transition_change", transitionChange)
            result.tryPutProperty("transition_in", transitionIn)
            result.tryPutProperty("transition_out", transitionOut)
            result.tryPutProperty("transition_triggers", transitionTriggers)
            result.tryPutProperty("truncate", truncate)
            result.tryPutProperty("underline", underline)
            result.tryPutProperty("visibility", visibility)
            result.tryPutProperty("visibility_action", visibilityAction)
            result.tryPutProperty("visibility_actions", visibilityActions)
            result.tryPutProperty("width", width)
            return result
        }
    }

    /**
     * Text cropping method. Use `ellipsis` instead.
     * 
     * Possible values: [none, start, end, middle].
     */
    @Generated
    sealed interface Truncate

    /**
     * Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
     * 
     * Can be created using the method [textEllipsis].
     * 
     * Required parameters: `text`.
     */
    @Generated
    class Ellipsis internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Ellipsis = Ellipsis(
            Properties(
                actions = additive.actions ?: properties.actions,
                images = additive.images ?: properties.images,
                ranges = additive.ranges ?: properties.ranges,
                text = additive.text ?: properties.text,
            )
        )

        class Properties internal constructor(
            /**
             * Actions when clicking on a crop marker.
             */
            val actions: Property<List<Action>>?,
            /**
             * Images embedded in a crop marker.
             */
            val images: Property<List<Image>>?,
            /**
             * Character ranges inside a crop marker with different text styles.
             */
            val ranges: Property<List<Range>>?,
            /**
             * Marker text.
             */
            val text: Property<String>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("actions", actions)
                result.tryPutProperty("images", images)
                result.tryPutProperty("ranges", ranges)
                result.tryPutProperty("text", text)
                return result
            }
        }
    }


    /**
     * Image.
     * 
     * Can be created using the method [textImage].
     * 
     * Required parameters: `url, start`.
     */
    @Generated
    class Image internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Image = Image(
            Properties(
                height = additive.height ?: properties.height,
                start = additive.start ?: properties.start,
                tintColor = additive.tintColor ?: properties.tintColor,
                tintMode = additive.tintMode ?: properties.tintMode,
                url = additive.url ?: properties.url,
                width = additive.width ?: properties.width,
            )
        )

        class Properties internal constructor(
            /**
             * Image height.
             * Default value: `{"type": "fixed","value":20}`.
             */
            val height: Property<FixedSize>?,
            /**
             * A symbol to insert prior to an image. To insert an image at the end of the text, specify the number of the last character plus one.
             */
            val start: Property<Int>?,
            /**
             * New color of a contour image.
             */
            val tintColor: Property<Color>?,
            /**
             * Blend mode of the color specified in `tint_color`.
             * Default value: `source_in`.
             */
            val tintMode: Property<BlendMode>?,
            /**
             * Image URL.
             */
            val url: Property<Url>?,
            /**
             * Image width.
             * Default value: `{"type": "fixed","value":20}`.
             */
            val width: Property<FixedSize>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("height", height)
                result.tryPutProperty("start", start)
                result.tryPutProperty("tint_color", tintColor)
                result.tryPutProperty("tint_mode", tintMode)
                result.tryPutProperty("url", url)
                result.tryPutProperty("width", width)
                return result
            }
        }
    }


    /**
     * Additional parameters of the character range.
     * 
     * Can be created using the method [textRange].
     * 
     * Required parameters: `start, end`.
     */
    @Generated
    class Range internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Range = Range(
            Properties(
                actions = additive.actions ?: properties.actions,
                background = additive.background ?: properties.background,
                border = additive.border ?: properties.border,
                end = additive.end ?: properties.end,
                fontFamily = additive.fontFamily ?: properties.fontFamily,
                fontSize = additive.fontSize ?: properties.fontSize,
                fontSizeUnit = additive.fontSizeUnit ?: properties.fontSizeUnit,
                fontWeight = additive.fontWeight ?: properties.fontWeight,
                letterSpacing = additive.letterSpacing ?: properties.letterSpacing,
                lineHeight = additive.lineHeight ?: properties.lineHeight,
                start = additive.start ?: properties.start,
                strike = additive.strike ?: properties.strike,
                textColor = additive.textColor ?: properties.textColor,
                topOffset = additive.topOffset ?: properties.topOffset,
                underline = additive.underline ?: properties.underline,
            )
        )

        class Properties internal constructor(
            /**
             * Action when clicking on text.
             */
            val actions: Property<List<Action>>?,
            /**
             * Character range background.
             */
            val background: Property<TextRangeBackground>?,
            /**
             * Character range border.
             */
            val border: Property<TextRangeBorder>?,
            /**
             * Ordinal number of the last character to be included in the range.
             */
            val end: Property<Int>?,
            /**
             * Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
             */
            val fontFamily: Property<FontFamily>?,
            /**
             * Font size.
             */
            val fontSize: Property<Int>?,
            /**
             * Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
             * Default value: `sp`.
             */
            val fontSizeUnit: Property<SizeUnit>?,
            /**
             * Style.
             */
            val fontWeight: Property<FontWeight>?,
            /**
             * Spacing between characters.
             */
            val letterSpacing: Property<Double>?,
            /**
             * Line spacing of the text. Units specified in `font_size_unit`.
             */
            val lineHeight: Property<Int>?,
            /**
             * Ordinal number of a character which the range begins from. The first character has a number `0`.
             */
            val start: Property<Int>?,
            /**
             * Strikethrough.
             */
            val strike: Property<LineStyle>?,
            /**
             * Text color.
             */
            val textColor: Property<Color>?,
            /**
             * Top margin of the character range. Units specified in `font_size_unit`.
             */
            val topOffset: Property<Int>?,
            /**
             * Underline.
             */
            val underline: Property<LineStyle>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("actions", actions)
                result.tryPutProperty("background", background)
                result.tryPutProperty("border", border)
                result.tryPutProperty("end", end)
                result.tryPutProperty("font_family", fontFamily)
                result.tryPutProperty("font_size", fontSize)
                result.tryPutProperty("font_size_unit", fontSizeUnit)
                result.tryPutProperty("font_weight", fontWeight)
                result.tryPutProperty("letter_spacing", letterSpacing)
                result.tryPutProperty("line_height", lineHeight)
                result.tryPutProperty("start", start)
                result.tryPutProperty("strike", strike)
                result.tryPutProperty("text_color", textColor)
                result.tryPutProperty("top_offset", topOffset)
                result.tryPutProperty("underline", underline)
                return result
            }
        }
    }

}

/**
 * @param text Text.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autoEllipsize Automatic text cropping to fit the container size.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param doubletapActions Action when double-clicking on an element.
 * @param ellipsis Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param focusedTextColor Text color when focusing on the element.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontWeight Style.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param images Images embedded in text.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param maxLines Maximum number of lines not to be cropped when breaking the limits.
 * @param minHiddenLines Minimum number of cropped lines when breaking the limits.
 * @param paddings Internal margins from the element stroke.
 * @param ranges A character range in which additional style parameters can be set. Defined by mandatory `start` and `end` fields.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectable Ability to select and copy text.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param strike Strikethrough.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color. Not used if the `text_gradient` parameter is set.
 * @param textGradient Gradient text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param truncate Text cropping method. Use `ellipsis` instead.
 * @param underline Underline.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.text(
    text: String? = null,
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    autoEllipsize: Boolean? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    doubletapActions: List<Action>? = null,
    ellipsis: Text.Ellipsis? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    focusedTextColor: Color? = null,
    fontFamily: FontFamily? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    height: Size? = null,
    id: String? = null,
    images: List<Text.Image>? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    maxLines: Int? = null,
    minHiddenLines: Int? = null,
    paddings: EdgeInsets? = null,
    ranges: List<Text.Range>? = null,
    rowSpan: Int? = null,
    selectable: Boolean? = null,
    selectedActions: List<Action>? = null,
    strike: LineStyle? = null,
    textAlignmentHorizontal: AlignmentHorizontal? = null,
    textAlignmentVertical: AlignmentVertical? = null,
    textColor: Color? = null,
    textGradient: TextGradient? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    truncate: Text.Truncate? = null,
    underline: LineStyle? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Text = Text(
    Text.Properties(
        text = valueOrNull(text),
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        autoEllipsize = valueOrNull(autoEllipsize),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        doubletapActions = valueOrNull(doubletapActions),
        ellipsis = valueOrNull(ellipsis),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        focusedTextColor = valueOrNull(focusedTextColor),
        fontFamily = valueOrNull(fontFamily),
        fontSize = valueOrNull(fontSize),
        fontSizeUnit = valueOrNull(fontSizeUnit),
        fontWeight = valueOrNull(fontWeight),
        height = valueOrNull(height),
        id = valueOrNull(id),
        images = valueOrNull(images),
        letterSpacing = valueOrNull(letterSpacing),
        lineHeight = valueOrNull(lineHeight),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        maxLines = valueOrNull(maxLines),
        minHiddenLines = valueOrNull(minHiddenLines),
        paddings = valueOrNull(paddings),
        ranges = valueOrNull(ranges),
        rowSpan = valueOrNull(rowSpan),
        selectable = valueOrNull(selectable),
        selectedActions = valueOrNull(selectedActions),
        strike = valueOrNull(strike),
        textAlignmentHorizontal = valueOrNull(textAlignmentHorizontal),
        textAlignmentVertical = valueOrNull(textAlignmentVertical),
        textColor = valueOrNull(textColor),
        textGradient = valueOrNull(textGradient),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        truncate = valueOrNull(truncate),
        underline = valueOrNull(underline),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    )
)

/**
 * @param text Text.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autoEllipsize Automatic text cropping to fit the container size.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param doubletapActions Action when double-clicking on an element.
 * @param ellipsis Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param focusedTextColor Text color when focusing on the element.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontWeight Style.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param images Images embedded in text.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param maxLines Maximum number of lines not to be cropped when breaking the limits.
 * @param minHiddenLines Minimum number of cropped lines when breaking the limits.
 * @param paddings Internal margins from the element stroke.
 * @param ranges A character range in which additional style parameters can be set. Defined by mandatory `start` and `end` fields.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectable Ability to select and copy text.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param strike Strikethrough.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color. Not used if the `text_gradient` parameter is set.
 * @param textGradient Gradient text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param truncate Text cropping method. Use `ellipsis` instead.
 * @param underline Underline.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.textProps(
    `use named arguments`: Guard = Guard.instance,
    text: String? = null,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    autoEllipsize: Boolean? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    doubletapActions: List<Action>? = null,
    ellipsis: Text.Ellipsis? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    focusedTextColor: Color? = null,
    fontFamily: FontFamily? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    height: Size? = null,
    id: String? = null,
    images: List<Text.Image>? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    maxLines: Int? = null,
    minHiddenLines: Int? = null,
    paddings: EdgeInsets? = null,
    ranges: List<Text.Range>? = null,
    rowSpan: Int? = null,
    selectable: Boolean? = null,
    selectedActions: List<Action>? = null,
    strike: LineStyle? = null,
    textAlignmentHorizontal: AlignmentHorizontal? = null,
    textAlignmentVertical: AlignmentVertical? = null,
    textColor: Color? = null,
    textGradient: TextGradient? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    truncate: Text.Truncate? = null,
    underline: LineStyle? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
) = Text.Properties(
    text = valueOrNull(text),
    accessibility = valueOrNull(accessibility),
    action = valueOrNull(action),
    actionAnimation = valueOrNull(actionAnimation),
    actions = valueOrNull(actions),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    autoEllipsize = valueOrNull(autoEllipsize),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnSpan = valueOrNull(columnSpan),
    doubletapActions = valueOrNull(doubletapActions),
    ellipsis = valueOrNull(ellipsis),
    extensions = valueOrNull(extensions),
    focus = valueOrNull(focus),
    focusedTextColor = valueOrNull(focusedTextColor),
    fontFamily = valueOrNull(fontFamily),
    fontSize = valueOrNull(fontSize),
    fontSizeUnit = valueOrNull(fontSizeUnit),
    fontWeight = valueOrNull(fontWeight),
    height = valueOrNull(height),
    id = valueOrNull(id),
    images = valueOrNull(images),
    letterSpacing = valueOrNull(letterSpacing),
    lineHeight = valueOrNull(lineHeight),
    longtapActions = valueOrNull(longtapActions),
    margins = valueOrNull(margins),
    maxLines = valueOrNull(maxLines),
    minHiddenLines = valueOrNull(minHiddenLines),
    paddings = valueOrNull(paddings),
    ranges = valueOrNull(ranges),
    rowSpan = valueOrNull(rowSpan),
    selectable = valueOrNull(selectable),
    selectedActions = valueOrNull(selectedActions),
    strike = valueOrNull(strike),
    textAlignmentHorizontal = valueOrNull(textAlignmentHorizontal),
    textAlignmentVertical = valueOrNull(textAlignmentVertical),
    textColor = valueOrNull(textColor),
    textGradient = valueOrNull(textGradient),
    tooltips = valueOrNull(tooltips),
    transform = valueOrNull(transform),
    transitionChange = valueOrNull(transitionChange),
    transitionIn = valueOrNull(transitionIn),
    transitionOut = valueOrNull(transitionOut),
    transitionTriggers = valueOrNull(transitionTriggers),
    truncate = valueOrNull(truncate),
    underline = valueOrNull(underline),
    visibility = valueOrNull(visibility),
    visibilityAction = valueOrNull(visibilityAction),
    visibilityActions = valueOrNull(visibilityActions),
    width = valueOrNull(width),
)

/**
 * @param text Text.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autoEllipsize Automatic text cropping to fit the container size.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param doubletapActions Action when double-clicking on an element.
 * @param ellipsis Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param focusedTextColor Text color when focusing on the element.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontWeight Style.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param images Images embedded in text.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param maxLines Maximum number of lines not to be cropped when breaking the limits.
 * @param minHiddenLines Minimum number of cropped lines when breaking the limits.
 * @param paddings Internal margins from the element stroke.
 * @param ranges A character range in which additional style parameters can be set. Defined by mandatory `start` and `end` fields.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectable Ability to select and copy text.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param strike Strikethrough.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color. Not used if the `text_gradient` parameter is set.
 * @param textGradient Gradient text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param truncate Text cropping method. Use `ellipsis` instead.
 * @param underline Underline.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun TemplateScope.textRefs(
    `use named arguments`: Guard = Guard.instance,
    text: ReferenceProperty<String>? = null,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    autoEllipsize: ReferenceProperty<Boolean>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    ellipsis: ReferenceProperty<Text.Ellipsis>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    focusedTextColor: ReferenceProperty<Color>? = null,
    fontFamily: ReferenceProperty<FontFamily>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    images: ReferenceProperty<List<Text.Image>>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    maxLines: ReferenceProperty<Int>? = null,
    minHiddenLines: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    ranges: ReferenceProperty<List<Text.Range>>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectable: ReferenceProperty<Boolean>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    strike: ReferenceProperty<LineStyle>? = null,
    textAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    textColor: ReferenceProperty<Color>? = null,
    textGradient: ReferenceProperty<TextGradient>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    truncate: ReferenceProperty<Text.Truncate>? = null,
    underline: ReferenceProperty<LineStyle>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
) = Text.Properties(
    text = text,
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    autoEllipsize = autoEllipsize,
    background = background,
    border = border,
    columnSpan = columnSpan,
    doubletapActions = doubletapActions,
    ellipsis = ellipsis,
    extensions = extensions,
    focus = focus,
    focusedTextColor = focusedTextColor,
    fontFamily = fontFamily,
    fontSize = fontSize,
    fontSizeUnit = fontSizeUnit,
    fontWeight = fontWeight,
    height = height,
    id = id,
    images = images,
    letterSpacing = letterSpacing,
    lineHeight = lineHeight,
    longtapActions = longtapActions,
    margins = margins,
    maxLines = maxLines,
    minHiddenLines = minHiddenLines,
    paddings = paddings,
    ranges = ranges,
    rowSpan = rowSpan,
    selectable = selectable,
    selectedActions = selectedActions,
    strike = strike,
    textAlignmentHorizontal = textAlignmentHorizontal,
    textAlignmentVertical = textAlignmentVertical,
    textColor = textColor,
    textGradient = textGradient,
    tooltips = tooltips,
    transform = transform,
    transitionChange = transitionChange,
    transitionIn = transitionIn,
    transitionOut = transitionOut,
    transitionTriggers = transitionTriggers,
    truncate = truncate,
    underline = underline,
    visibility = visibility,
    visibilityAction = visibilityAction,
    visibilityActions = visibilityActions,
    width = width,
)

/**
 * @param text Text.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autoEllipsize Automatic text cropping to fit the container size.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param doubletapActions Action when double-clicking on an element.
 * @param ellipsis Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param focusedTextColor Text color when focusing on the element.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontWeight Style.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param images Images embedded in text.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param maxLines Maximum number of lines not to be cropped when breaking the limits.
 * @param minHiddenLines Minimum number of cropped lines when breaking the limits.
 * @param paddings Internal margins from the element stroke.
 * @param ranges A character range in which additional style parameters can be set. Defined by mandatory `start` and `end` fields.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectable Ability to select and copy text.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param strike Strikethrough.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color. Not used if the `text_gradient` parameter is set.
 * @param textGradient Gradient text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param truncate Text cropping method. Use `ellipsis` instead.
 * @param underline Underline.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Text.override(
    `use named arguments`: Guard = Guard.instance,
    text: String? = null,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    autoEllipsize: Boolean? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    doubletapActions: List<Action>? = null,
    ellipsis: Text.Ellipsis? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    focusedTextColor: Color? = null,
    fontFamily: FontFamily? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    height: Size? = null,
    id: String? = null,
    images: List<Text.Image>? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    maxLines: Int? = null,
    minHiddenLines: Int? = null,
    paddings: EdgeInsets? = null,
    ranges: List<Text.Range>? = null,
    rowSpan: Int? = null,
    selectable: Boolean? = null,
    selectedActions: List<Action>? = null,
    strike: LineStyle? = null,
    textAlignmentHorizontal: AlignmentHorizontal? = null,
    textAlignmentVertical: AlignmentVertical? = null,
    textColor: Color? = null,
    textGradient: TextGradient? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    truncate: Text.Truncate? = null,
    underline: LineStyle? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Text = Text(
    Text.Properties(
        text = valueOrNull(text) ?: properties.text,
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        action = valueOrNull(action) ?: properties.action,
        actionAnimation = valueOrNull(actionAnimation) ?: properties.actionAnimation,
        actions = valueOrNull(actions) ?: properties.actions,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        autoEllipsize = valueOrNull(autoEllipsize) ?: properties.autoEllipsize,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        doubletapActions = valueOrNull(doubletapActions) ?: properties.doubletapActions,
        ellipsis = valueOrNull(ellipsis) ?: properties.ellipsis,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        focus = valueOrNull(focus) ?: properties.focus,
        focusedTextColor = valueOrNull(focusedTextColor) ?: properties.focusedTextColor,
        fontFamily = valueOrNull(fontFamily) ?: properties.fontFamily,
        fontSize = valueOrNull(fontSize) ?: properties.fontSize,
        fontSizeUnit = valueOrNull(fontSizeUnit) ?: properties.fontSizeUnit,
        fontWeight = valueOrNull(fontWeight) ?: properties.fontWeight,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
        images = valueOrNull(images) ?: properties.images,
        letterSpacing = valueOrNull(letterSpacing) ?: properties.letterSpacing,
        lineHeight = valueOrNull(lineHeight) ?: properties.lineHeight,
        longtapActions = valueOrNull(longtapActions) ?: properties.longtapActions,
        margins = valueOrNull(margins) ?: properties.margins,
        maxLines = valueOrNull(maxLines) ?: properties.maxLines,
        minHiddenLines = valueOrNull(minHiddenLines) ?: properties.minHiddenLines,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        ranges = valueOrNull(ranges) ?: properties.ranges,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        selectable = valueOrNull(selectable) ?: properties.selectable,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        strike = valueOrNull(strike) ?: properties.strike,
        textAlignmentHorizontal = valueOrNull(textAlignmentHorizontal) ?: properties.textAlignmentHorizontal,
        textAlignmentVertical = valueOrNull(textAlignmentVertical) ?: properties.textAlignmentVertical,
        textColor = valueOrNull(textColor) ?: properties.textColor,
        textGradient = valueOrNull(textGradient) ?: properties.textGradient,
        tooltips = valueOrNull(tooltips) ?: properties.tooltips,
        transform = valueOrNull(transform) ?: properties.transform,
        transitionChange = valueOrNull(transitionChange) ?: properties.transitionChange,
        transitionIn = valueOrNull(transitionIn) ?: properties.transitionIn,
        transitionOut = valueOrNull(transitionOut) ?: properties.transitionOut,
        transitionTriggers = valueOrNull(transitionTriggers) ?: properties.transitionTriggers,
        truncate = valueOrNull(truncate) ?: properties.truncate,
        underline = valueOrNull(underline) ?: properties.underline,
        visibility = valueOrNull(visibility) ?: properties.visibility,
        visibilityAction = valueOrNull(visibilityAction) ?: properties.visibilityAction,
        visibilityActions = valueOrNull(visibilityActions) ?: properties.visibilityActions,
        width = valueOrNull(width) ?: properties.width,
    )
)

/**
 * @param text Text.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autoEllipsize Automatic text cropping to fit the container size.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param doubletapActions Action when double-clicking on an element.
 * @param ellipsis Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param focusedTextColor Text color when focusing on the element.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontWeight Style.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param images Images embedded in text.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param maxLines Maximum number of lines not to be cropped when breaking the limits.
 * @param minHiddenLines Minimum number of cropped lines when breaking the limits.
 * @param paddings Internal margins from the element stroke.
 * @param ranges A character range in which additional style parameters can be set. Defined by mandatory `start` and `end` fields.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectable Ability to select and copy text.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param strike Strikethrough.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color. Not used if the `text_gradient` parameter is set.
 * @param textGradient Gradient text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param truncate Text cropping method. Use `ellipsis` instead.
 * @param underline Underline.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Text.defer(
    `use named arguments`: Guard = Guard.instance,
    text: ReferenceProperty<String>? = null,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    autoEllipsize: ReferenceProperty<Boolean>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    ellipsis: ReferenceProperty<Text.Ellipsis>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    focusedTextColor: ReferenceProperty<Color>? = null,
    fontFamily: ReferenceProperty<FontFamily>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    images: ReferenceProperty<List<Text.Image>>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    maxLines: ReferenceProperty<Int>? = null,
    minHiddenLines: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    ranges: ReferenceProperty<List<Text.Range>>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectable: ReferenceProperty<Boolean>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    strike: ReferenceProperty<LineStyle>? = null,
    textAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    textColor: ReferenceProperty<Color>? = null,
    textGradient: ReferenceProperty<TextGradient>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    truncate: ReferenceProperty<Text.Truncate>? = null,
    underline: ReferenceProperty<LineStyle>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Text = Text(
    Text.Properties(
        text = text ?: properties.text,
        accessibility = accessibility ?: properties.accessibility,
        action = action ?: properties.action,
        actionAnimation = actionAnimation ?: properties.actionAnimation,
        actions = actions ?: properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        autoEllipsize = autoEllipsize ?: properties.autoEllipsize,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        doubletapActions = doubletapActions ?: properties.doubletapActions,
        ellipsis = ellipsis ?: properties.ellipsis,
        extensions = extensions ?: properties.extensions,
        focus = focus ?: properties.focus,
        focusedTextColor = focusedTextColor ?: properties.focusedTextColor,
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontWeight = fontWeight ?: properties.fontWeight,
        height = height ?: properties.height,
        id = id ?: properties.id,
        images = images ?: properties.images,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        longtapActions = longtapActions ?: properties.longtapActions,
        margins = margins ?: properties.margins,
        maxLines = maxLines ?: properties.maxLines,
        minHiddenLines = minHiddenLines ?: properties.minHiddenLines,
        paddings = paddings ?: properties.paddings,
        ranges = ranges ?: properties.ranges,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectable = selectable ?: properties.selectable,
        selectedActions = selectedActions ?: properties.selectedActions,
        strike = strike ?: properties.strike,
        textAlignmentHorizontal = textAlignmentHorizontal ?: properties.textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical ?: properties.textAlignmentVertical,
        textColor = textColor ?: properties.textColor,
        textGradient = textGradient ?: properties.textGradient,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
        transitionChange = transitionChange ?: properties.transitionChange,
        transitionIn = transitionIn ?: properties.transitionIn,
        transitionOut = transitionOut ?: properties.transitionOut,
        transitionTriggers = transitionTriggers ?: properties.transitionTriggers,
        truncate = truncate ?: properties.truncate,
        underline = underline ?: properties.underline,
        visibility = visibility ?: properties.visibility,
        visibilityAction = visibilityAction ?: properties.visibilityAction,
        visibilityActions = visibilityActions ?: properties.visibilityActions,
        width = width ?: properties.width,
    )
)

/**
 * @param text Text.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autoEllipsize Automatic text cropping to fit the container size.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param focusedTextColor Text color when focusing on the element.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontWeight Style.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text.
 * @param maxLines Maximum number of lines not to be cropped when breaking the limits.
 * @param minHiddenLines Minimum number of cropped lines when breaking the limits.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectable Ability to select and copy text.
 * @param strike Strikethrough.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color. Not used if the `text_gradient` parameter is set.
 * @param truncate Text cropping method. Use `ellipsis` instead.
 * @param underline Underline.
 * @param visibility Element visibility.
 */
@Generated
fun Text.evaluate(
    `use named arguments`: Guard = Guard.instance,
    text: ExpressionProperty<String>? = null,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    autoEllipsize: ExpressionProperty<Boolean>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    focusedTextColor: ExpressionProperty<Color>? = null,
    fontFamily: ExpressionProperty<FontFamily>? = null,
    fontSize: ExpressionProperty<Int>? = null,
    fontSizeUnit: ExpressionProperty<SizeUnit>? = null,
    fontWeight: ExpressionProperty<FontWeight>? = null,
    letterSpacing: ExpressionProperty<Double>? = null,
    lineHeight: ExpressionProperty<Int>? = null,
    maxLines: ExpressionProperty<Int>? = null,
    minHiddenLines: ExpressionProperty<Int>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    selectable: ExpressionProperty<Boolean>? = null,
    strike: ExpressionProperty<LineStyle>? = null,
    textAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    textColor: ExpressionProperty<Color>? = null,
    truncate: ExpressionProperty<Text.Truncate>? = null,
    underline: ExpressionProperty<LineStyle>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Text = Text(
    Text.Properties(
        text = text ?: properties.text,
        accessibility = properties.accessibility,
        action = properties.action,
        actionAnimation = properties.actionAnimation,
        actions = properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        autoEllipsize = autoEllipsize ?: properties.autoEllipsize,
        background = properties.background,
        border = properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        doubletapActions = properties.doubletapActions,
        ellipsis = properties.ellipsis,
        extensions = properties.extensions,
        focus = properties.focus,
        focusedTextColor = focusedTextColor ?: properties.focusedTextColor,
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontWeight = fontWeight ?: properties.fontWeight,
        height = properties.height,
        id = properties.id,
        images = properties.images,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        longtapActions = properties.longtapActions,
        margins = properties.margins,
        maxLines = maxLines ?: properties.maxLines,
        minHiddenLines = minHiddenLines ?: properties.minHiddenLines,
        paddings = properties.paddings,
        ranges = properties.ranges,
        rowSpan = rowSpan ?: properties.rowSpan,
        selectable = selectable ?: properties.selectable,
        selectedActions = properties.selectedActions,
        strike = strike ?: properties.strike,
        textAlignmentHorizontal = textAlignmentHorizontal ?: properties.textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical ?: properties.textAlignmentVertical,
        textColor = textColor ?: properties.textColor,
        textGradient = properties.textGradient,
        tooltips = properties.tooltips,
        transform = properties.transform,
        transitionChange = properties.transitionChange,
        transitionIn = properties.transitionIn,
        transitionOut = properties.transitionOut,
        transitionTriggers = properties.transitionTriggers,
        truncate = truncate ?: properties.truncate,
        underline = underline ?: properties.underline,
        visibility = visibility ?: properties.visibility,
        visibilityAction = properties.visibilityAction,
        visibilityActions = properties.visibilityActions,
        width = properties.width,
    )
)

/**
 * @param text Text.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autoEllipsize Automatic text cropping to fit the container size.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param doubletapActions Action when double-clicking on an element.
 * @param ellipsis Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param focusedTextColor Text color when focusing on the element.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontWeight Style.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param images Images embedded in text.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param maxLines Maximum number of lines not to be cropped when breaking the limits.
 * @param minHiddenLines Minimum number of cropped lines when breaking the limits.
 * @param paddings Internal margins from the element stroke.
 * @param ranges A character range in which additional style parameters can be set. Defined by mandatory `start` and `end` fields.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectable Ability to select and copy text.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param strike Strikethrough.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color. Not used if the `text_gradient` parameter is set.
 * @param textGradient Gradient text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param truncate Text cropping method. Use `ellipsis` instead.
 * @param underline Underline.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Text>.override(
    `use named arguments`: Guard = Guard.instance,
    text: String? = null,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    autoEllipsize: Boolean? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    doubletapActions: List<Action>? = null,
    ellipsis: Text.Ellipsis? = null,
    extensions: List<Extension>? = null,
    focus: Focus? = null,
    focusedTextColor: Color? = null,
    fontFamily: FontFamily? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    height: Size? = null,
    id: String? = null,
    images: List<Text.Image>? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    maxLines: Int? = null,
    minHiddenLines: Int? = null,
    paddings: EdgeInsets? = null,
    ranges: List<Text.Range>? = null,
    rowSpan: Int? = null,
    selectable: Boolean? = null,
    selectedActions: List<Action>? = null,
    strike: LineStyle? = null,
    textAlignmentHorizontal: AlignmentHorizontal? = null,
    textAlignmentVertical: AlignmentVertical? = null,
    textColor: Color? = null,
    textGradient: TextGradient? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    truncate: Text.Truncate? = null,
    underline: LineStyle? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Component<Text> = Component(
    template = template,
    properties = Text.Properties(
        text = valueOrNull(text),
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        autoEllipsize = valueOrNull(autoEllipsize),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        doubletapActions = valueOrNull(doubletapActions),
        ellipsis = valueOrNull(ellipsis),
        extensions = valueOrNull(extensions),
        focus = valueOrNull(focus),
        focusedTextColor = valueOrNull(focusedTextColor),
        fontFamily = valueOrNull(fontFamily),
        fontSize = valueOrNull(fontSize),
        fontSizeUnit = valueOrNull(fontSizeUnit),
        fontWeight = valueOrNull(fontWeight),
        height = valueOrNull(height),
        id = valueOrNull(id),
        images = valueOrNull(images),
        letterSpacing = valueOrNull(letterSpacing),
        lineHeight = valueOrNull(lineHeight),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        maxLines = valueOrNull(maxLines),
        minHiddenLines = valueOrNull(minHiddenLines),
        paddings = valueOrNull(paddings),
        ranges = valueOrNull(ranges),
        rowSpan = valueOrNull(rowSpan),
        selectable = valueOrNull(selectable),
        selectedActions = valueOrNull(selectedActions),
        strike = valueOrNull(strike),
        textAlignmentHorizontal = valueOrNull(textAlignmentHorizontal),
        textAlignmentVertical = valueOrNull(textAlignmentVertical),
        textColor = valueOrNull(textColor),
        textGradient = valueOrNull(textGradient),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        truncate = valueOrNull(truncate),
        underline = valueOrNull(underline),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    ).mergeWith(properties)
)

/**
 * @param text Text.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autoEllipsize Automatic text cropping to fit the container size.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param doubletapActions Action when double-clicking on an element.
 * @param ellipsis Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param focus Parameters when focusing on an element or losing focus.
 * @param focusedTextColor Text color when focusing on the element.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontWeight Style.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param images Images embedded in text.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param maxLines Maximum number of lines not to be cropped when breaking the limits.
 * @param minHiddenLines Minimum number of cropped lines when breaking the limits.
 * @param paddings Internal margins from the element stroke.
 * @param ranges A character range in which additional style parameters can be set. Defined by mandatory `start` and `end` fields.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectable Ability to select and copy text.
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param strike Strikethrough.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color. Not used if the `text_gradient` parameter is set.
 * @param textGradient Gradient text color.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param truncate Text cropping method. Use `ellipsis` instead.
 * @param underline Underline.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Text>.defer(
    `use named arguments`: Guard = Guard.instance,
    text: ReferenceProperty<String>? = null,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    autoEllipsize: ReferenceProperty<Boolean>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    ellipsis: ReferenceProperty<Text.Ellipsis>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    focusedTextColor: ReferenceProperty<Color>? = null,
    fontFamily: ReferenceProperty<FontFamily>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    images: ReferenceProperty<List<Text.Image>>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    maxLines: ReferenceProperty<Int>? = null,
    minHiddenLines: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    ranges: ReferenceProperty<List<Text.Range>>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    selectable: ReferenceProperty<Boolean>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    strike: ReferenceProperty<LineStyle>? = null,
    textAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    textColor: ReferenceProperty<Color>? = null,
    textGradient: ReferenceProperty<TextGradient>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    truncate: ReferenceProperty<Text.Truncate>? = null,
    underline: ReferenceProperty<LineStyle>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Component<Text> = Component(
    template = template,
    properties = Text.Properties(
        text = text,
        accessibility = accessibility,
        action = action,
        actionAnimation = actionAnimation,
        actions = actions,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        autoEllipsize = autoEllipsize,
        background = background,
        border = border,
        columnSpan = columnSpan,
        doubletapActions = doubletapActions,
        ellipsis = ellipsis,
        extensions = extensions,
        focus = focus,
        focusedTextColor = focusedTextColor,
        fontFamily = fontFamily,
        fontSize = fontSize,
        fontSizeUnit = fontSizeUnit,
        fontWeight = fontWeight,
        height = height,
        id = id,
        images = images,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        longtapActions = longtapActions,
        margins = margins,
        maxLines = maxLines,
        minHiddenLines = minHiddenLines,
        paddings = paddings,
        ranges = ranges,
        rowSpan = rowSpan,
        selectable = selectable,
        selectedActions = selectedActions,
        strike = strike,
        textAlignmentHorizontal = textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical,
        textColor = textColor,
        textGradient = textGradient,
        tooltips = tooltips,
        transform = transform,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        truncate = truncate,
        underline = underline,
        visibility = visibility,
        visibilityAction = visibilityAction,
        visibilityActions = visibilityActions,
        width = width,
    ).mergeWith(properties)
)

/**
 * @param text Text.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autoEllipsize Automatic text cropping to fit the container size.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param focusedTextColor Text color when focusing on the element.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontWeight Style.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text.
 * @param maxLines Maximum number of lines not to be cropped when breaking the limits.
 * @param minHiddenLines Minimum number of cropped lines when breaking the limits.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param selectable Ability to select and copy text.
 * @param strike Strikethrough.
 * @param textAlignmentHorizontal Horizontal text alignment.
 * @param textAlignmentVertical Vertical text alignment.
 * @param textColor Text color. Not used if the `text_gradient` parameter is set.
 * @param truncate Text cropping method. Use `ellipsis` instead.
 * @param underline Underline.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Text>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    text: ExpressionProperty<String>? = null,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    autoEllipsize: ExpressionProperty<Boolean>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    focusedTextColor: ExpressionProperty<Color>? = null,
    fontFamily: ExpressionProperty<FontFamily>? = null,
    fontSize: ExpressionProperty<Int>? = null,
    fontSizeUnit: ExpressionProperty<SizeUnit>? = null,
    fontWeight: ExpressionProperty<FontWeight>? = null,
    letterSpacing: ExpressionProperty<Double>? = null,
    lineHeight: ExpressionProperty<Int>? = null,
    maxLines: ExpressionProperty<Int>? = null,
    minHiddenLines: ExpressionProperty<Int>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    selectable: ExpressionProperty<Boolean>? = null,
    strike: ExpressionProperty<LineStyle>? = null,
    textAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    textAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    textColor: ExpressionProperty<Color>? = null,
    truncate: ExpressionProperty<Text.Truncate>? = null,
    underline: ExpressionProperty<LineStyle>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Text> = Component(
    template = template,
    properties = Text.Properties(
        text = text,
        accessibility = null,
        action = null,
        actionAnimation = null,
        actions = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        autoEllipsize = autoEllipsize,
        background = null,
        border = null,
        columnSpan = columnSpan,
        doubletapActions = null,
        ellipsis = null,
        extensions = null,
        focus = null,
        focusedTextColor = focusedTextColor,
        fontFamily = fontFamily,
        fontSize = fontSize,
        fontSizeUnit = fontSizeUnit,
        fontWeight = fontWeight,
        height = null,
        id = null,
        images = null,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        longtapActions = null,
        margins = null,
        maxLines = maxLines,
        minHiddenLines = minHiddenLines,
        paddings = null,
        ranges = null,
        rowSpan = rowSpan,
        selectable = selectable,
        selectedActions = null,
        strike = strike,
        textAlignmentHorizontal = textAlignmentHorizontal,
        textAlignmentVertical = textAlignmentVertical,
        textColor = textColor,
        textGradient = null,
        tooltips = null,
        transform = null,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        truncate = truncate,
        underline = underline,
        visibility = visibility,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Text>.plus(additive: Text.Properties): Component<Text> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Text.asList() = listOf(this)

/**
 * @param actions Actions when clicking on a crop marker.
 * @param images Images embedded in a crop marker.
 * @param ranges Character ranges inside a crop marker with different text styles.
 * @param text Marker text.
 */
@Generated
fun DivScope.textEllipsis(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    images: List<Text.Image>? = null,
    ranges: List<Text.Range>? = null,
    text: String? = null,
): Text.Ellipsis = Text.Ellipsis(
    Text.Ellipsis.Properties(
        actions = valueOrNull(actions),
        images = valueOrNull(images),
        ranges = valueOrNull(ranges),
        text = valueOrNull(text),
    )
)

/**
 * @param actions Actions when clicking on a crop marker.
 * @param images Images embedded in a crop marker.
 * @param ranges Character ranges inside a crop marker with different text styles.
 * @param text Marker text.
 */
@Generated
fun DivScope.textEllipsisProps(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    images: List<Text.Image>? = null,
    ranges: List<Text.Range>? = null,
    text: String? = null,
) = Text.Ellipsis.Properties(
    actions = valueOrNull(actions),
    images = valueOrNull(images),
    ranges = valueOrNull(ranges),
    text = valueOrNull(text),
)

/**
 * @param actions Actions when clicking on a crop marker.
 * @param images Images embedded in a crop marker.
 * @param ranges Character ranges inside a crop marker with different text styles.
 * @param text Marker text.
 */
@Generated
fun TemplateScope.textEllipsisRefs(
    `use named arguments`: Guard = Guard.instance,
    actions: ReferenceProperty<List<Action>>? = null,
    images: ReferenceProperty<List<Text.Image>>? = null,
    ranges: ReferenceProperty<List<Text.Range>>? = null,
    text: ReferenceProperty<String>? = null,
) = Text.Ellipsis.Properties(
    actions = actions,
    images = images,
    ranges = ranges,
    text = text,
)

/**
 * @param actions Actions when clicking on a crop marker.
 * @param images Images embedded in a crop marker.
 * @param ranges Character ranges inside a crop marker with different text styles.
 * @param text Marker text.
 */
@Generated
fun Text.Ellipsis.override(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    images: List<Text.Image>? = null,
    ranges: List<Text.Range>? = null,
    text: String? = null,
): Text.Ellipsis = Text.Ellipsis(
    Text.Ellipsis.Properties(
        actions = valueOrNull(actions) ?: properties.actions,
        images = valueOrNull(images) ?: properties.images,
        ranges = valueOrNull(ranges) ?: properties.ranges,
        text = valueOrNull(text) ?: properties.text,
    )
)

/**
 * @param actions Actions when clicking on a crop marker.
 * @param images Images embedded in a crop marker.
 * @param ranges Character ranges inside a crop marker with different text styles.
 * @param text Marker text.
 */
@Generated
fun Text.Ellipsis.defer(
    `use named arguments`: Guard = Guard.instance,
    actions: ReferenceProperty<List<Action>>? = null,
    images: ReferenceProperty<List<Text.Image>>? = null,
    ranges: ReferenceProperty<List<Text.Range>>? = null,
    text: ReferenceProperty<String>? = null,
): Text.Ellipsis = Text.Ellipsis(
    Text.Ellipsis.Properties(
        actions = actions ?: properties.actions,
        images = images ?: properties.images,
        ranges = ranges ?: properties.ranges,
        text = text ?: properties.text,
    )
)

/**
 * @param text Marker text.
 */
@Generated
fun Text.Ellipsis.evaluate(
    `use named arguments`: Guard = Guard.instance,
    text: ExpressionProperty<String>? = null,
): Text.Ellipsis = Text.Ellipsis(
    Text.Ellipsis.Properties(
        actions = properties.actions,
        images = properties.images,
        ranges = properties.ranges,
        text = text ?: properties.text,
    )
)

@Generated
fun Text.Ellipsis.asList() = listOf(this)

/**
 * @param height Image height.
 * @param start A symbol to insert prior to an image. To insert an image at the end of the text, specify the number of the last character plus one.
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param url Image URL.
 * @param width Image width.
 */
@Generated
fun DivScope.textImage(
    `use named arguments`: Guard = Guard.instance,
    height: FixedSize? = null,
    start: Int? = null,
    tintColor: Color? = null,
    tintMode: BlendMode? = null,
    url: Url? = null,
    width: FixedSize? = null,
): Text.Image = Text.Image(
    Text.Image.Properties(
        height = valueOrNull(height),
        start = valueOrNull(start),
        tintColor = valueOrNull(tintColor),
        tintMode = valueOrNull(tintMode),
        url = valueOrNull(url),
        width = valueOrNull(width),
    )
)

/**
 * @param height Image height.
 * @param start A symbol to insert prior to an image. To insert an image at the end of the text, specify the number of the last character plus one.
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param url Image URL.
 * @param width Image width.
 */
@Generated
fun DivScope.textImageProps(
    `use named arguments`: Guard = Guard.instance,
    height: FixedSize? = null,
    start: Int? = null,
    tintColor: Color? = null,
    tintMode: BlendMode? = null,
    url: Url? = null,
    width: FixedSize? = null,
) = Text.Image.Properties(
    height = valueOrNull(height),
    start = valueOrNull(start),
    tintColor = valueOrNull(tintColor),
    tintMode = valueOrNull(tintMode),
    url = valueOrNull(url),
    width = valueOrNull(width),
)

/**
 * @param height Image height.
 * @param start A symbol to insert prior to an image. To insert an image at the end of the text, specify the number of the last character plus one.
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param url Image URL.
 * @param width Image width.
 */
@Generated
fun TemplateScope.textImageRefs(
    `use named arguments`: Guard = Guard.instance,
    height: ReferenceProperty<FixedSize>? = null,
    start: ReferenceProperty<Int>? = null,
    tintColor: ReferenceProperty<Color>? = null,
    tintMode: ReferenceProperty<BlendMode>? = null,
    url: ReferenceProperty<Url>? = null,
    width: ReferenceProperty<FixedSize>? = null,
) = Text.Image.Properties(
    height = height,
    start = start,
    tintColor = tintColor,
    tintMode = tintMode,
    url = url,
    width = width,
)

/**
 * @param height Image height.
 * @param start A symbol to insert prior to an image. To insert an image at the end of the text, specify the number of the last character plus one.
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param url Image URL.
 * @param width Image width.
 */
@Generated
fun Text.Image.override(
    `use named arguments`: Guard = Guard.instance,
    height: FixedSize? = null,
    start: Int? = null,
    tintColor: Color? = null,
    tintMode: BlendMode? = null,
    url: Url? = null,
    width: FixedSize? = null,
): Text.Image = Text.Image(
    Text.Image.Properties(
        height = valueOrNull(height) ?: properties.height,
        start = valueOrNull(start) ?: properties.start,
        tintColor = valueOrNull(tintColor) ?: properties.tintColor,
        tintMode = valueOrNull(tintMode) ?: properties.tintMode,
        url = valueOrNull(url) ?: properties.url,
        width = valueOrNull(width) ?: properties.width,
    )
)

/**
 * @param height Image height.
 * @param start A symbol to insert prior to an image. To insert an image at the end of the text, specify the number of the last character plus one.
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param url Image URL.
 * @param width Image width.
 */
@Generated
fun Text.Image.defer(
    `use named arguments`: Guard = Guard.instance,
    height: ReferenceProperty<FixedSize>? = null,
    start: ReferenceProperty<Int>? = null,
    tintColor: ReferenceProperty<Color>? = null,
    tintMode: ReferenceProperty<BlendMode>? = null,
    url: ReferenceProperty<Url>? = null,
    width: ReferenceProperty<FixedSize>? = null,
): Text.Image = Text.Image(
    Text.Image.Properties(
        height = height ?: properties.height,
        start = start ?: properties.start,
        tintColor = tintColor ?: properties.tintColor,
        tintMode = tintMode ?: properties.tintMode,
        url = url ?: properties.url,
        width = width ?: properties.width,
    )
)

/**
 * @param start A symbol to insert prior to an image. To insert an image at the end of the text, specify the number of the last character plus one.
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param url Image URL.
 */
@Generated
fun Text.Image.evaluate(
    `use named arguments`: Guard = Guard.instance,
    start: ExpressionProperty<Int>? = null,
    tintColor: ExpressionProperty<Color>? = null,
    tintMode: ExpressionProperty<BlendMode>? = null,
    url: ExpressionProperty<Url>? = null,
): Text.Image = Text.Image(
    Text.Image.Properties(
        height = properties.height,
        start = start ?: properties.start,
        tintColor = tintColor ?: properties.tintColor,
        tintMode = tintMode ?: properties.tintMode,
        url = url ?: properties.url,
        width = properties.width,
    )
)

@Generated
fun Text.Image.asList() = listOf(this)

/**
 * @param actions Action when clicking on text.
 * @param background Character range background.
 * @param border Character range border.
 * @param end Ordinal number of the last character to be included in the range.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param start Ordinal number of a character which the range begins from. The first character has a number `0`.
 * @param strike Strikethrough.
 * @param textColor Text color.
 * @param topOffset Top margin of the character range. Units specified in `font_size_unit`.
 * @param underline Underline.
 */
@Generated
fun DivScope.textRange(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    background: TextRangeBackground? = null,
    border: TextRangeBorder? = null,
    end: Int? = null,
    fontFamily: FontFamily? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    start: Int? = null,
    strike: LineStyle? = null,
    textColor: Color? = null,
    topOffset: Int? = null,
    underline: LineStyle? = null,
): Text.Range = Text.Range(
    Text.Range.Properties(
        actions = valueOrNull(actions),
        background = valueOrNull(background),
        border = valueOrNull(border),
        end = valueOrNull(end),
        fontFamily = valueOrNull(fontFamily),
        fontSize = valueOrNull(fontSize),
        fontSizeUnit = valueOrNull(fontSizeUnit),
        fontWeight = valueOrNull(fontWeight),
        letterSpacing = valueOrNull(letterSpacing),
        lineHeight = valueOrNull(lineHeight),
        start = valueOrNull(start),
        strike = valueOrNull(strike),
        textColor = valueOrNull(textColor),
        topOffset = valueOrNull(topOffset),
        underline = valueOrNull(underline),
    )
)

/**
 * @param actions Action when clicking on text.
 * @param background Character range background.
 * @param border Character range border.
 * @param end Ordinal number of the last character to be included in the range.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param start Ordinal number of a character which the range begins from. The first character has a number `0`.
 * @param strike Strikethrough.
 * @param textColor Text color.
 * @param topOffset Top margin of the character range. Units specified in `font_size_unit`.
 * @param underline Underline.
 */
@Generated
fun DivScope.textRangeProps(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    background: TextRangeBackground? = null,
    border: TextRangeBorder? = null,
    end: Int? = null,
    fontFamily: FontFamily? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    start: Int? = null,
    strike: LineStyle? = null,
    textColor: Color? = null,
    topOffset: Int? = null,
    underline: LineStyle? = null,
) = Text.Range.Properties(
    actions = valueOrNull(actions),
    background = valueOrNull(background),
    border = valueOrNull(border),
    end = valueOrNull(end),
    fontFamily = valueOrNull(fontFamily),
    fontSize = valueOrNull(fontSize),
    fontSizeUnit = valueOrNull(fontSizeUnit),
    fontWeight = valueOrNull(fontWeight),
    letterSpacing = valueOrNull(letterSpacing),
    lineHeight = valueOrNull(lineHeight),
    start = valueOrNull(start),
    strike = valueOrNull(strike),
    textColor = valueOrNull(textColor),
    topOffset = valueOrNull(topOffset),
    underline = valueOrNull(underline),
)

/**
 * @param actions Action when clicking on text.
 * @param background Character range background.
 * @param border Character range border.
 * @param end Ordinal number of the last character to be included in the range.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param start Ordinal number of a character which the range begins from. The first character has a number `0`.
 * @param strike Strikethrough.
 * @param textColor Text color.
 * @param topOffset Top margin of the character range. Units specified in `font_size_unit`.
 * @param underline Underline.
 */
@Generated
fun TemplateScope.textRangeRefs(
    `use named arguments`: Guard = Guard.instance,
    actions: ReferenceProperty<List<Action>>? = null,
    background: ReferenceProperty<TextRangeBackground>? = null,
    border: ReferenceProperty<TextRangeBorder>? = null,
    end: ReferenceProperty<Int>? = null,
    fontFamily: ReferenceProperty<FontFamily>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    start: ReferenceProperty<Int>? = null,
    strike: ReferenceProperty<LineStyle>? = null,
    textColor: ReferenceProperty<Color>? = null,
    topOffset: ReferenceProperty<Int>? = null,
    underline: ReferenceProperty<LineStyle>? = null,
) = Text.Range.Properties(
    actions = actions,
    background = background,
    border = border,
    end = end,
    fontFamily = fontFamily,
    fontSize = fontSize,
    fontSizeUnit = fontSizeUnit,
    fontWeight = fontWeight,
    letterSpacing = letterSpacing,
    lineHeight = lineHeight,
    start = start,
    strike = strike,
    textColor = textColor,
    topOffset = topOffset,
    underline = underline,
)

/**
 * @param actions Action when clicking on text.
 * @param background Character range background.
 * @param border Character range border.
 * @param end Ordinal number of the last character to be included in the range.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param start Ordinal number of a character which the range begins from. The first character has a number `0`.
 * @param strike Strikethrough.
 * @param textColor Text color.
 * @param topOffset Top margin of the character range. Units specified in `font_size_unit`.
 * @param underline Underline.
 */
@Generated
fun Text.Range.override(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    background: TextRangeBackground? = null,
    border: TextRangeBorder? = null,
    end: Int? = null,
    fontFamily: FontFamily? = null,
    fontSize: Int? = null,
    fontSizeUnit: SizeUnit? = null,
    fontWeight: FontWeight? = null,
    letterSpacing: Double? = null,
    lineHeight: Int? = null,
    start: Int? = null,
    strike: LineStyle? = null,
    textColor: Color? = null,
    topOffset: Int? = null,
    underline: LineStyle? = null,
): Text.Range = Text.Range(
    Text.Range.Properties(
        actions = valueOrNull(actions) ?: properties.actions,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        end = valueOrNull(end) ?: properties.end,
        fontFamily = valueOrNull(fontFamily) ?: properties.fontFamily,
        fontSize = valueOrNull(fontSize) ?: properties.fontSize,
        fontSizeUnit = valueOrNull(fontSizeUnit) ?: properties.fontSizeUnit,
        fontWeight = valueOrNull(fontWeight) ?: properties.fontWeight,
        letterSpacing = valueOrNull(letterSpacing) ?: properties.letterSpacing,
        lineHeight = valueOrNull(lineHeight) ?: properties.lineHeight,
        start = valueOrNull(start) ?: properties.start,
        strike = valueOrNull(strike) ?: properties.strike,
        textColor = valueOrNull(textColor) ?: properties.textColor,
        topOffset = valueOrNull(topOffset) ?: properties.topOffset,
        underline = valueOrNull(underline) ?: properties.underline,
    )
)

/**
 * @param actions Action when clicking on text.
 * @param background Character range background.
 * @param border Character range border.
 * @param end Ordinal number of the last character to be included in the range.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param start Ordinal number of a character which the range begins from. The first character has a number `0`.
 * @param strike Strikethrough.
 * @param textColor Text color.
 * @param topOffset Top margin of the character range. Units specified in `font_size_unit`.
 * @param underline Underline.
 */
@Generated
fun Text.Range.defer(
    `use named arguments`: Guard = Guard.instance,
    actions: ReferenceProperty<List<Action>>? = null,
    background: ReferenceProperty<TextRangeBackground>? = null,
    border: ReferenceProperty<TextRangeBorder>? = null,
    end: ReferenceProperty<Int>? = null,
    fontFamily: ReferenceProperty<FontFamily>? = null,
    fontSize: ReferenceProperty<Int>? = null,
    fontSizeUnit: ReferenceProperty<SizeUnit>? = null,
    fontWeight: ReferenceProperty<FontWeight>? = null,
    letterSpacing: ReferenceProperty<Double>? = null,
    lineHeight: ReferenceProperty<Int>? = null,
    start: ReferenceProperty<Int>? = null,
    strike: ReferenceProperty<LineStyle>? = null,
    textColor: ReferenceProperty<Color>? = null,
    topOffset: ReferenceProperty<Int>? = null,
    underline: ReferenceProperty<LineStyle>? = null,
): Text.Range = Text.Range(
    Text.Range.Properties(
        actions = actions ?: properties.actions,
        background = background ?: properties.background,
        border = border ?: properties.border,
        end = end ?: properties.end,
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontWeight = fontWeight ?: properties.fontWeight,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        start = start ?: properties.start,
        strike = strike ?: properties.strike,
        textColor = textColor ?: properties.textColor,
        topOffset = topOffset ?: properties.topOffset,
        underline = underline ?: properties.underline,
    )
)

/**
 * @param end Ordinal number of the last character to be included in the range.
 * @param fontFamily Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * @param fontSize Font size.
 * @param fontSizeUnit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param fontWeight Style.
 * @param letterSpacing Spacing between characters.
 * @param lineHeight Line spacing of the text. Units specified in `font_size_unit`.
 * @param start Ordinal number of a character which the range begins from. The first character has a number `0`.
 * @param strike Strikethrough.
 * @param textColor Text color.
 * @param topOffset Top margin of the character range. Units specified in `font_size_unit`.
 * @param underline Underline.
 */
@Generated
fun Text.Range.evaluate(
    `use named arguments`: Guard = Guard.instance,
    end: ExpressionProperty<Int>? = null,
    fontFamily: ExpressionProperty<FontFamily>? = null,
    fontSize: ExpressionProperty<Int>? = null,
    fontSizeUnit: ExpressionProperty<SizeUnit>? = null,
    fontWeight: ExpressionProperty<FontWeight>? = null,
    letterSpacing: ExpressionProperty<Double>? = null,
    lineHeight: ExpressionProperty<Int>? = null,
    start: ExpressionProperty<Int>? = null,
    strike: ExpressionProperty<LineStyle>? = null,
    textColor: ExpressionProperty<Color>? = null,
    topOffset: ExpressionProperty<Int>? = null,
    underline: ExpressionProperty<LineStyle>? = null,
): Text.Range = Text.Range(
    Text.Range.Properties(
        actions = properties.actions,
        background = properties.background,
        border = properties.border,
        end = end ?: properties.end,
        fontFamily = fontFamily ?: properties.fontFamily,
        fontSize = fontSize ?: properties.fontSize,
        fontSizeUnit = fontSizeUnit ?: properties.fontSizeUnit,
        fontWeight = fontWeight ?: properties.fontWeight,
        letterSpacing = letterSpacing ?: properties.letterSpacing,
        lineHeight = lineHeight ?: properties.lineHeight,
        start = start ?: properties.start,
        strike = strike ?: properties.strike,
        textColor = textColor ?: properties.textColor,
        topOffset = topOffset ?: properties.topOffset,
        underline = underline ?: properties.underline,
    )
)

@Generated
fun Text.Range.asList() = listOf(this)

@Generated
fun Text.Truncate.asList() = listOf(this)
