// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    Div,
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
    IDivCornersRadius,
    IDivEdgeInsets,
    IDivExtension,
    IDivFocus,
    IDivTooltip,
    IDivTransform,
    IDivVisibilityAction,
} from './';

/**
 * Tabs. Height of the first tab is determined by its contents, and height of the remaining
 * [depends on the platform](../../location.dita#tabs).
 */
export class DivTabs<T extends DivTabsProps = DivTabsProps> {
    readonly _props?: Exact<DivTabsProps, T>;

    readonly type = 'tabs';
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
     * Updating height when changing the active element. In the browser, the value is always `true`.
     */
    dynamic_height?: Type<IntBoolean | DivExpression>;
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
     * A separating line between tabs and contents.
     */
    has_separator?: Type<IntBoolean | DivExpression>;
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
     * Tabs. Transition between tabs can be implemented
     * using:`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number
     * `item` inside an element, with the specified
     * `id`;`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an
     * element, with the specified `id`;`div-action://set_previous_item?id=[&overflow={clamp|ring}]`
     * — go to the previous tab inside an element, with the specified `id`.</p><p>The optional
     * `overflow` parameter is used to set navigation when the first or last element is
     * reached:`clamp` — transition will stop at the border element;`ring` — go to the beginning or
     * end, depending on the current element.</p><p>By default, `clamp`.
     */
    items: Type<NonEmptyArray<IDivTabsItem>>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
     */
    restrict_parent_scroll?: Type<IntBoolean | DivExpression>;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number | DivExpression>;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Ordinal number of the tab that will be opened by default.
     */
    selected_tab?: Type<number | DivExpression>;
    /**
     * Separator color.
     */
    separator_color?: Type<string | DivExpression>;
    /**
     * Indents from the separating line. Not used if `has_separator = false`.
     */
    separator_paddings?: Type<IDivEdgeInsets>;
    /**
     * Switching tabs by scrolling through the contents.
     */
    switch_tabs_by_content_swipe_enabled?: Type<IntBoolean | DivExpression>;
    /**
     * Design style of tab titles.
     */
    tab_title_style?: Type<IDivTabsTabTitleStyle>;
    /**
     * Indents in the tab name.
     */
    title_paddings?: Type<IDivEdgeInsets>;
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

    constructor(props: Exact<DivTabsProps, T>) {
        this.accessibility = props.accessibility;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.background = props.background;
        this.border = props.border;
        this.column_span = props.column_span;
        this.dynamic_height = props.dynamic_height;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.has_separator = props.has_separator;
        this.height = props.height;
        this.id = props.id;
        this.items = props.items;
        this.margins = props.margins;
        this.paddings = props.paddings;
        this.restrict_parent_scroll = props.restrict_parent_scroll;
        this.row_span = props.row_span;
        this.selected_actions = props.selected_actions;
        this.selected_tab = props.selected_tab;
        this.separator_color = props.separator_color;
        this.separator_paddings = props.separator_paddings;
        this.switch_tabs_by_content_swipe_enabled = props.switch_tabs_by_content_swipe_enabled;
        this.tab_title_style = props.tab_title_style;
        this.title_paddings = props.title_paddings;
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

export interface DivTabsProps {
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
     * Updating height when changing the active element. In the browser, the value is always `true`.
     */
    dynamic_height?: Type<IntBoolean | DivExpression>;
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
     * A separating line between tabs and contents.
     */
    has_separator?: Type<IntBoolean | DivExpression>;
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
     * Tabs. Transition between tabs can be implemented
     * using:`div-action://set_current_item?id=&item=` — set the current tab with an ordinal number
     * `item` inside an element, with the specified
     * `id`;`div-action://set_next_item?id=[&overflow={clamp|ring}]` — go to the next tab inside an
     * element, with the specified `id`;`div-action://set_previous_item?id=[&overflow={clamp|ring}]`
     * — go to the previous tab inside an element, with the specified `id`.</p><p>The optional
     * `overflow` parameter is used to set navigation when the first or last element is
     * reached:`clamp` — transition will stop at the border element;`ring` — go to the beginning or
     * end, depending on the current element.</p><p>By default, `clamp`.
     */
    items: Type<NonEmptyArray<IDivTabsItem>>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * If the parameter is enabled, tabs won't transmit the scroll gesture to the parent element.
     */
    restrict_parent_scroll?: Type<IntBoolean | DivExpression>;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number | DivExpression>;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Ordinal number of the tab that will be opened by default.
     */
    selected_tab?: Type<number | DivExpression>;
    /**
     * Separator color.
     */
    separator_color?: Type<string | DivExpression>;
    /**
     * Indents from the separating line. Not used if `has_separator = false`.
     */
    separator_paddings?: Type<IDivEdgeInsets>;
    /**
     * Switching tabs by scrolling through the contents.
     */
    switch_tabs_by_content_swipe_enabled?: Type<IntBoolean | DivExpression>;
    /**
     * Design style of tab titles.
     */
    tab_title_style?: Type<IDivTabsTabTitleStyle>;
    /**
     * Indents in the tab name.
     */
    title_paddings?: Type<IDivEdgeInsets>;
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

/**
 * Tab.
 */
export interface IDivTabsItem {
    /**
     * Tab contents.
     */
    div: Type<Div>;
    /**
     * Tab title.
     */
    title: Type<string | DivExpression>;
    /**
     * Action when clicking on the active tab title.
     */
    title_click_action?: Type<IDivAction>;
}

/**
 * Design style of tab titles.
 */
export interface IDivTabsTabTitleStyle {
    /**
     * Background color of the active tab title.
     */
    active_background_color?: Type<string | DivExpression>;
    /**
     * Active tab title style.
     */
    active_font_weight?: Type<DivFontWeight | DivExpression>;
    /**
     * Color of the active tab title text.
     */
    active_text_color?: Type<string | DivExpression>;
    /**
     * Duration of active title change animation.
     */
    animation_duration?: Type<number | DivExpression>;
    /**
     * Active title change animation.
     */
    animation_type?: Type<TabTitleStyleAnimationType | DivExpression>;
    /**
     * Title corner rounding radius. If the parameter isn't specified, the rounding is maximum (half
     * of the smallest size). Not used if the `corners_radius` parameter is set.
     */
    corner_radius?: Type<number | DivExpression>;
    /**
     * Rounding radii of corners of multiple titles. Empty values are replaced by `corner_radius`.
     */
    corners_radius?: Type<IDivCornersRadius>;
    /**
     * Font family:`text` — a standard text font;`display` — a family of fonts with a large font
     * size.
     */
    font_family?: Type<DivFontFamily | DivExpression>;
    /**
     * Title font size.
     */
    font_size?: Type<number | DivExpression>;
    /**
     * Units of title font size measurement.
     */
    font_size_unit?: Type<DivSizeUnit | DivExpression>;
    /**
     * Style. Use `active_font_weight` and `inactive_font_weight` instead.
     *
     * @deprecated
     */
    font_weight?: Type<DivFontWeight | DivExpression>;
    /**
     * Background color of the inactive tab title.
     */
    inactive_background_color?: Type<string | DivExpression>;
    /**
     * Inactive tab title style.
     */
    inactive_font_weight?: Type<DivFontWeight | DivExpression>;
    /**
     * Color of the inactive tab title text.
     */
    inactive_text_color?: Type<string | DivExpression>;
    /**
     * Spacing between neighbouring tab titles.
     */
    item_spacing?: Type<number | DivExpression>;
    /**
     * Spacing between title characters.
     */
    letter_spacing?: Type<number | DivExpression>;
    /**
     * Line spacing of the text range. The count is taken from the font baseline.
     */
    line_height?: Type<number | DivExpression>;
    /**
     * Indents around the tab title.
     */
    paddings?: Type<IDivEdgeInsets>;
}

export type TabTitleStyleAnimationType =
    | 'slide'
    | 'fade'
    | 'none';
