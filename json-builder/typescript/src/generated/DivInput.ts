// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivAlignmentHorizontal,
    DivAlignmentVertical,
    DivAppearanceTransition,
    DivBackground,
    DivChangeTransition,
    DivFontFamily,
    DivFontWeight,
    DivSize,
    DivSizeUnit,
    DivTransitionTrigger,
    DivVisibility,
    IDivAccessibility,
    IDivAction,
    IDivBorder,
    IDivEdgeInsets,
    IDivExtension,
    IDivFocus,
    IDivTooltip,
    IDivTransform,
    IDivVisibilityAction,
} from './';

/**
 * Text input element.
 */
export class DivInput<T extends DivInputProps = DivInputProps> {
    readonly _props?: Exact<DivInputProps, T>;

    readonly type = 'input';
    /**
     * Accessibility for disabled people.
     */
    accessibility?: Type<IDivAccessibility>;
    /**
     * Horizontal alignment of an element inside the parent element.
     */
    alignment_horizontal?: Type<DivAlignmentHorizontal | DivExpression>;
    /**
     * Vertical alignment of an element inside the parent element.
     */
    alignment_vertical?: Type<DivAlignmentVertical | DivExpression>;
    /**
     * Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
     */
    alpha?: Type<number | DivExpression>;
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
    column_span?: Type<number | DivExpression>;
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
     * Font family:`text` — a standard text font;`display` — a family of fonts with a large font
     * size.
     */
    font_family?: Type<DivFontFamily | DivExpression>;
    /**
     * Font size.
     */
    font_size?: Type<number | DivExpression>;
    /**
     * Unit of measurement:`px` — a physical pixel.`dp` — a logical pixel that doesn't depend on
     * screen density.`sp` — a logical pixel that depends on the font size on a device. Specify
     * height in `sp`. Only available on Android.
     */
    font_size_unit?: Type<DivSizeUnit | DivExpression>;
    /**
     * Style.
     */
    font_weight?: Type<DivFontWeight | DivExpression>;
    /**
     * Element height. For Android: if there is text in this or in a child element, specify height in
     * `sp` to scale the element together with the text. To learn more about units of size
     * measurement, see [Layout inside the card](../../layout.dita).
     */
    height?: Type<DivSize>;
    /**
     * Text highlight color. If the value isn't set, the color set in the client will be used
     * instead.
     */
    highlight_color?: Type<string | DivExpression>;
    /**
     * Text color.
     */
    hint_color?: Type<string | DivExpression>;
    /**
     * Tooltip text.
     */
    hint_text?: Type<string | DivExpression>;
    /**
     * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier`
     * on iOS.
     */
    id?: Type<string>;
    /**
     * Keyboard type.
     */
    keyboard_type?: Type<DivInputKeyboardType | DivExpression>;
    /**
     * Spacing between characters.
     */
    letter_spacing?: Type<number | DivExpression>;
    /**
     * Line spacing of the text range. The count is taken from the font baseline. Measured in units
     * specified in `font_size_unit`.
     */
    line_height?: Type<number | DivExpression>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Maximum number of lines that will be visible in the input view.
     */
    max_visible_lines?: Type<number | DivExpression>;
    /**
     * Text input line used in the native interface.
     */
    native_interface?: Type<IDivInputNativeInterface>;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number | DivExpression>;
    /**
     * Highlighting input text when focused.
     */
    select_all_on_focus?: Type<IntBoolean | DivExpression>;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Text color.
     */
    text_color?: Type<string | DivExpression>;
    /**
     * Name of text storage variable.
     */
    text_variable: Type<string>;
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
     * Element visibility.
     */
    visibility?: Type<DivVisibility | DivExpression>;
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

    constructor(props: Exact<DivInputProps, T>) {
        this.accessibility = props.accessibility;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.background = props.background;
        this.border = props.border;
        this.column_span = props.column_span;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.font_family = props.font_family;
        this.font_size = props.font_size;
        this.font_size_unit = props.font_size_unit;
        this.font_weight = props.font_weight;
        this.height = props.height;
        this.highlight_color = props.highlight_color;
        this.hint_color = props.hint_color;
        this.hint_text = props.hint_text;
        this.id = props.id;
        this.keyboard_type = props.keyboard_type;
        this.letter_spacing = props.letter_spacing;
        this.line_height = props.line_height;
        this.margins = props.margins;
        this.max_visible_lines = props.max_visible_lines;
        this.native_interface = props.native_interface;
        this.paddings = props.paddings;
        this.row_span = props.row_span;
        this.select_all_on_focus = props.select_all_on_focus;
        this.selected_actions = props.selected_actions;
        this.text_color = props.text_color;
        this.text_variable = props.text_variable;
        this.tooltips = props.tooltips;
        this.transform = props.transform;
        this.transition_change = props.transition_change;
        this.transition_in = props.transition_in;
        this.transition_out = props.transition_out;
        this.transition_triggers = props.transition_triggers;
        this.visibility = props.visibility;
        this.visibility_action = props.visibility_action;
        this.visibility_actions = props.visibility_actions;
        this.width = props.width;
    }
}

export interface DivInputProps {
    /**
     * Accessibility for disabled people.
     */
    accessibility?: Type<IDivAccessibility>;
    /**
     * Horizontal alignment of an element inside the parent element.
     */
    alignment_horizontal?: Type<DivAlignmentHorizontal | DivExpression>;
    /**
     * Vertical alignment of an element inside the parent element.
     */
    alignment_vertical?: Type<DivAlignmentVertical | DivExpression>;
    /**
     * Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
     */
    alpha?: Type<number | DivExpression>;
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
    column_span?: Type<number | DivExpression>;
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
     * Font family:`text` — a standard text font;`display` — a family of fonts with a large font
     * size.
     */
    font_family?: Type<DivFontFamily | DivExpression>;
    /**
     * Font size.
     */
    font_size?: Type<number | DivExpression>;
    /**
     * Unit of measurement:`px` — a physical pixel.`dp` — a logical pixel that doesn't depend on
     * screen density.`sp` — a logical pixel that depends on the font size on a device. Specify
     * height in `sp`. Only available on Android.
     */
    font_size_unit?: Type<DivSizeUnit | DivExpression>;
    /**
     * Style.
     */
    font_weight?: Type<DivFontWeight | DivExpression>;
    /**
     * Element height. For Android: if there is text in this or in a child element, specify height in
     * `sp` to scale the element together with the text. To learn more about units of size
     * measurement, see [Layout inside the card](../../layout.dita).
     */
    height?: Type<DivSize>;
    /**
     * Text highlight color. If the value isn't set, the color set in the client will be used
     * instead.
     */
    highlight_color?: Type<string | DivExpression>;
    /**
     * Text color.
     */
    hint_color?: Type<string | DivExpression>;
    /**
     * Tooltip text.
     */
    hint_text?: Type<string | DivExpression>;
    /**
     * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier`
     * on iOS.
     */
    id?: Type<string>;
    /**
     * Keyboard type.
     */
    keyboard_type?: Type<DivInputKeyboardType | DivExpression>;
    /**
     * Spacing between characters.
     */
    letter_spacing?: Type<number | DivExpression>;
    /**
     * Line spacing of the text range. The count is taken from the font baseline. Measured in units
     * specified in `font_size_unit`.
     */
    line_height?: Type<number | DivExpression>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Maximum number of lines that will be visible in the input view.
     */
    max_visible_lines?: Type<number | DivExpression>;
    /**
     * Text input line used in the native interface.
     */
    native_interface?: Type<IDivInputNativeInterface>;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number | DivExpression>;
    /**
     * Highlighting input text when focused.
     */
    select_all_on_focus?: Type<IntBoolean | DivExpression>;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Text color.
     */
    text_color?: Type<string | DivExpression>;
    /**
     * Name of text storage variable.
     */
    text_variable: Type<string>;
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
     * Element visibility.
     */
    visibility?: Type<DivVisibility | DivExpression>;
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

export type DivInputKeyboardType =
    | 'single_line_text'
    | 'multi_line_text'
    | 'phone'
    | 'number'
    | 'email'
    | 'uri';

/**
 * Text input line used in the native interface.
 */
export interface IDivInputNativeInterface {
    /**
     * Text input line color.
     */
    color: Type<string | DivExpression>;
}
