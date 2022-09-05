// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAlignmentHorizontal,
    DivAlignmentVertical,
    DivAppearanceTransition,
    DivBackground,
    DivChangeTransition,
    DivFixedSize,
    DivFontFamily,
    DivFontWeight,
    DivGradientBackground,
    DivLineStyle,
    DivSize,
    DivSizeUnit,
    DivTransitionTrigger,
    DivVisibility,
    IDivAccessibility,
    IDivAction,
    IDivAnimation,
    IDivBorder,
    IDivEdgeInsets,
    IDivExtension,
    IDivFocus,
    IDivTooltip,
    IDivTransform,
    IDivVisibilityAction,
} from './';

/**
 * Text.
 */
export class DivText<T extends DivTextProps = DivTextProps> {
    readonly _props?: Exact<DivTextProps, T>;

    readonly type = 'text';
    /**
     * Accessibility for disabled people.
     */
    accessibility?: Type<IDivAccessibility>;
    /**
     * One action when clicking on an element. Not used if the `actions` parameter is set.
     */
    action?: Type<IDivAction>;
    /**
     * Action animation. Web supports `fade`, `scale` and `set` only.
     */
    action_animation?: Type<IDivAnimation>;
    /**
     * Multiple actions when clicking on an element.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Horizontal alignment of an element inside the parent element.
     */
    alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Vertical alignment of an element inside the parent element.
     */
    alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Automatic text cropping to fit the container size.
     */
    auto_ellipsize?: Type<IntBoolean> | DivExpression;
    /**
     * Element background. It can contain multiple layers.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Element stroke.
     */
    border?: Type<IDivBorder>;
    /**
     * Merges cells in a column of the [grid](div-grid.md) element.
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Action when double-clicking on an element.
     */
    doubletap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
     */
    ellipsis?: Type<IDivTextEllipsis>;
    /**
     * Extensions for additional processing of an element. The list of extensions is given in 
     * [DivExtension](../../extensions.dita).
     */
    extensions?: Type<NonEmptyArray<IDivExtension>>;
    /**
     * Parameters when focusing on an element or losing focus.
     */
    focus?: Type<IDivFocus>;
    /**
     * Text color when focusing on the element.
     */
    focused_text_color?: Type<string> | DivExpression;
    /**
     * Font family:`text` — a standard text font;`display` — a family of fonts with a large font
     * size.
     */
    font_family?: Type<DivFontFamily> | DivExpression;
    /**
     * Font size.
     */
    font_size?: Type<number> | DivExpression;
    font_size_unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Style.
     */
    font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Element height. For Android: if there is text in this or in a child element, specify height in
     * `sp` to scale the element together with the text. To learn more about units of size
     * measurement, see [Layout inside the card](../../layout.dita).
     */
    height?: Type<DivSize>;
    /**
     * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier`
     * on iOS.
     */
    id?: Type<string>;
    /**
     * Images embedded in text.
     */
    images?: Type<NonEmptyArray<IDivTextImage>>;
    /**
     * Spacing between characters.
     */
    letter_spacing?: Type<number> | DivExpression;
    /**
     * Line spacing of the text range. The count is taken from the font baseline.
     */
    line_height?: Type<number> | DivExpression;
    /**
     * Action when long-clicking on an element.
     */
    longtap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Maximum number of lines not to be cropped when breaking the limits.
     */
    max_lines?: Type<number> | DivExpression;
    /**
     * Minimum number of cropped lines when breaking the limits.
     */
    min_hidden_lines?: Type<number> | DivExpression;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * A character range in which additional style parameters can be set. Defined by mandatory
     * `start` and `end` fields.
     */
    ranges?: Type<NonEmptyArray<IDivTextRange>>;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Selecting and copying text.
     */
    selectable?: Type<IntBoolean> | DivExpression;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Strikethrough.
     */
    strike?: Type<DivLineStyle> | DivExpression;
    /**
     * Text.
     */
    text: Type<string> | DivExpression;
    /**
     * Horizontal text alignment.
     */
    text_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Vertical text alignment.
     */
    text_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Text color. Not used if the `text_gradient` parameter is set.
     */
    text_color?: Type<string> | DivExpression;
    /**
     * Gradient text color.
     */
    text_gradient?: Type<DivGradientBackground>;
    /**
     * Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`,
     * hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
     */
    tooltips?: Type<NonEmptyArray<IDivTooltip>>;
    /**
     * Transformation of the element. Applies the passed transform to the element. The content that
     * does not fit into the original view will be cut off.
     */
    transform?: Type<IDivTransform>;
    /**
     * Change animation. It is played when the position or size of an element changes in the new
     * layout.
     */
    transition_change?: Type<DivChangeTransition>;
    /**
     * Appearance animation. It is played when an element with a new ID appears. To learn more about
     * the concept of transitions, see [Animated
     * transitions](../../interaction.dita#animation/transition-animation).
     */
    transition_in?: Type<DivAppearanceTransition>;
    /**
     * Disappearance animation. It is played when an element disappears in the new layout.
     */
    transition_out?: Type<DivAppearanceTransition>;
    /**
     * Animation starting triggers. Default value: `[state_change, visibility_change]`.
     */
    transition_triggers?: Type<NonEmptyArray<DivTransitionTrigger>>;
    /**
     * Text cropping method. Use `ellipsis` instead.
     *
     * @deprecated
     */
    truncate?: Type<DivTextTruncate> | DivExpression;
    /**
     * Underline.
     */
    underline?: Type<DivLineStyle> | DivExpression;
    /**
     * Element visibility.
     */
    visibility?: Type<DivVisibility> | DivExpression;
    /**
     * Tracking visibility of a single element. Not used if the `visibility_actions` parameter is
     * set.
     */
    visibility_action?: Type<IDivVisibilityAction>;
    /**
     * Actions when an element appears on the screen.
     */
    visibility_actions?: Type<NonEmptyArray<IDivVisibilityAction>>;
    /**
     * Element width.
     */
    width?: Type<DivSize>;

    constructor(props: Exact<DivTextProps, T>) {
        this.accessibility = props.accessibility;
        this.action = props.action;
        this.action_animation = props.action_animation;
        this.actions = props.actions;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.auto_ellipsize = props.auto_ellipsize;
        this.background = props.background;
        this.border = props.border;
        this.column_span = props.column_span;
        this.doubletap_actions = props.doubletap_actions;
        this.ellipsis = props.ellipsis;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.focused_text_color = props.focused_text_color;
        this.font_family = props.font_family;
        this.font_size = props.font_size;
        this.font_size_unit = props.font_size_unit;
        this.font_weight = props.font_weight;
        this.height = props.height;
        this.id = props.id;
        this.images = props.images;
        this.letter_spacing = props.letter_spacing;
        this.line_height = props.line_height;
        this.longtap_actions = props.longtap_actions;
        this.margins = props.margins;
        this.max_lines = props.max_lines;
        this.min_hidden_lines = props.min_hidden_lines;
        this.paddings = props.paddings;
        this.ranges = props.ranges;
        this.row_span = props.row_span;
        this.selectable = props.selectable;
        this.selected_actions = props.selected_actions;
        this.strike = props.strike;
        this.text = props.text;
        this.text_alignment_horizontal = props.text_alignment_horizontal;
        this.text_alignment_vertical = props.text_alignment_vertical;
        this.text_color = props.text_color;
        this.text_gradient = props.text_gradient;
        this.tooltips = props.tooltips;
        this.transform = props.transform;
        this.transition_change = props.transition_change;
        this.transition_in = props.transition_in;
        this.transition_out = props.transition_out;
        this.transition_triggers = props.transition_triggers;
        this.truncate = props.truncate;
        this.underline = props.underline;
        this.visibility = props.visibility;
        this.visibility_action = props.visibility_action;
        this.visibility_actions = props.visibility_actions;
        this.width = props.width;
    }
}

interface DivTextProps {
    /**
     * Accessibility for disabled people.
     */
    accessibility?: Type<IDivAccessibility>;
    /**
     * One action when clicking on an element. Not used if the `actions` parameter is set.
     */
    action?: Type<IDivAction>;
    /**
     * Action animation. Web supports `fade`, `scale` and `set` only.
     */
    action_animation?: Type<IDivAnimation>;
    /**
     * Multiple actions when clicking on an element.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Horizontal alignment of an element inside the parent element.
     */
    alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Vertical alignment of an element inside the parent element.
     */
    alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Automatic text cropping to fit the container size.
     */
    auto_ellipsize?: Type<IntBoolean> | DivExpression;
    /**
     * Element background. It can contain multiple layers.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Element stroke.
     */
    border?: Type<IDivBorder>;
    /**
     * Merges cells in a column of the [grid](div-grid.md) element.
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Action when double-clicking on an element.
     */
    doubletap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
     */
    ellipsis?: Type<IDivTextEllipsis>;
    /**
     * Extensions for additional processing of an element. The list of extensions is given in 
     * [DivExtension](../../extensions.dita).
     */
    extensions?: Type<NonEmptyArray<IDivExtension>>;
    /**
     * Parameters when focusing on an element or losing focus.
     */
    focus?: Type<IDivFocus>;
    /**
     * Text color when focusing on the element.
     */
    focused_text_color?: Type<string> | DivExpression;
    /**
     * Font family:`text` — a standard text font;`display` — a family of fonts with a large font
     * size.
     */
    font_family?: Type<DivFontFamily> | DivExpression;
    /**
     * Font size.
     */
    font_size?: Type<number> | DivExpression;
    font_size_unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Style.
     */
    font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Element height. For Android: if there is text in this or in a child element, specify height in
     * `sp` to scale the element together with the text. To learn more about units of size
     * measurement, see [Layout inside the card](../../layout.dita).
     */
    height?: Type<DivSize>;
    /**
     * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier`
     * on iOS.
     */
    id?: Type<string>;
    /**
     * Images embedded in text.
     */
    images?: Type<NonEmptyArray<IDivTextImage>>;
    /**
     * Spacing between characters.
     */
    letter_spacing?: Type<number> | DivExpression;
    /**
     * Line spacing of the text range. The count is taken from the font baseline.
     */
    line_height?: Type<number> | DivExpression;
    /**
     * Action when long-clicking on an element.
     */
    longtap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Maximum number of lines not to be cropped when breaking the limits.
     */
    max_lines?: Type<number> | DivExpression;
    /**
     * Minimum number of cropped lines when breaking the limits.
     */
    min_hidden_lines?: Type<number> | DivExpression;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * A character range in which additional style parameters can be set. Defined by mandatory
     * `start` and `end` fields.
     */
    ranges?: Type<NonEmptyArray<IDivTextRange>>;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Selecting and copying text.
     */
    selectable?: Type<IntBoolean> | DivExpression;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Strikethrough.
     */
    strike?: Type<DivLineStyle> | DivExpression;
    /**
     * Text.
     */
    text: Type<string> | DivExpression;
    /**
     * Horizontal text alignment.
     */
    text_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Vertical text alignment.
     */
    text_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Text color. Not used if the `text_gradient` parameter is set.
     */
    text_color?: Type<string> | DivExpression;
    /**
     * Gradient text color.
     */
    text_gradient?: Type<DivGradientBackground>;
    /**
     * Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`,
     * hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
     */
    tooltips?: Type<NonEmptyArray<IDivTooltip>>;
    /**
     * Transformation of the element. Applies the passed transform to the element. The content that
     * does not fit into the original view will be cut off.
     */
    transform?: Type<IDivTransform>;
    /**
     * Change animation. It is played when the position or size of an element changes in the new
     * layout.
     */
    transition_change?: Type<DivChangeTransition>;
    /**
     * Appearance animation. It is played when an element with a new ID appears. To learn more about
     * the concept of transitions, see [Animated
     * transitions](../../interaction.dita#animation/transition-animation).
     */
    transition_in?: Type<DivAppearanceTransition>;
    /**
     * Disappearance animation. It is played when an element disappears in the new layout.
     */
    transition_out?: Type<DivAppearanceTransition>;
    /**
     * Animation starting triggers. Default value: `[state_change, visibility_change]`.
     */
    transition_triggers?: Type<NonEmptyArray<DivTransitionTrigger>>;
    /**
     * Text cropping method. Use `ellipsis` instead.
     *
     * @deprecated
     */
    truncate?: Type<DivTextTruncate> | DivExpression;
    /**
     * Underline.
     */
    underline?: Type<DivLineStyle> | DivExpression;
    /**
     * Element visibility.
     */
    visibility?: Type<DivVisibility> | DivExpression;
    /**
     * Tracking visibility of a single element. Not used if the `visibility_actions` parameter is
     * set.
     */
    visibility_action?: Type<IDivVisibilityAction>;
    /**
     * Actions when an element appears on the screen.
     */
    visibility_actions?: Type<NonEmptyArray<IDivVisibilityAction>>;
    /**
     * Element width.
     */
    width?: Type<DivSize>;
}

export type DivTextTruncate =
    | 'none'
    | 'start'
    | 'end'
    | 'middle';

/**
 * Text cropping marker. It is displayed when text size exceeds the limit on the number of lines.
 */
export interface IDivTextEllipsis {
    /**
     * Actions when clicking on a crop marker.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Images embedded in a crop marker.
     */
    images?: Type<NonEmptyArray<IDivTextImage>>;
    /**
     * Character ranges inside a crop marker with different text styles.
     */
    ranges?: Type<NonEmptyArray<IDivTextRange>>;
    /**
     * Marker text.
     */
    text: Type<string> | DivExpression;
}

/**
 * Image.
 */
export interface IDivTextImage {
    /**
     * Image height.
     */
    height?: Type<DivFixedSize>;
    /**
     * A symbol to insert prior to an image. To insert an image at the end of the text, specify the
     * number of the last character plus one.
     */
    start: Type<number> | DivExpression;
    /**
     * New color of a contour image.
     */
    tint_color?: Type<string> | DivExpression;
    /**
     * Image URL.
     */
    url: Type<string> | DivExpression;
    /**
     * Image width.
     */
    width?: Type<DivFixedSize>;
}

/**
 * Additional parameters of the character range.
 */
export interface IDivTextRange {
    /**
     * Action when clicking on text.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Ordinal number of the last character to be included in the range.
     */
    end: Type<number> | DivExpression;
    /**
     * Font family:`text` — a standard text font;`display` — a family of fonts with a large font
     * size.
     */
    font_family?: Type<DivFontFamily> | DivExpression;
    /**
     * Font size.
     */
    font_size?: Type<number> | DivExpression;
    /**
     * Unit of measurement:`px` — a physical pixel.`dp` — a logical pixel that doesn't depend on
     * screen density.`sp` — a logical pixel that depends on the font size on a device. Specify
     * height in `sp`. Only available on Android.
     */
    font_size_unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Style.
     */
    font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Spacing between characters.
     */
    letter_spacing?: Type<number> | DivExpression;
    /**
     * Line spacing of the text range. The count is taken from the font baseline. Measured in units
     * specified in `font_size_unit`.
     */
    line_height?: Type<number> | DivExpression;
    /**
     * Ordinal number of a character which the range begins from. The first character has a number
     * `0`.
     */
    start: Type<number> | DivExpression;
    /**
     * Strikethrough.
     */
    strike?: Type<DivLineStyle> | DivExpression;
    /**
     * Text color.
     */
    text_color?: Type<string> | DivExpression;
    /**
     * The top margin of the text range. Measured in units specified in `font_size_unit`.
     */
    top_offset?: Type<number> | DivExpression;
    /**
     * Underline.
     */
    underline?: Type<DivLineStyle> | DivExpression;
}
