// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    Div,
    DivAlignmentHorizontal,
    DivAlignmentVertical,
    DivAppearanceTransition,
    DivBackground,
    DivChangeTransition,
    DivSize,
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
 * Gallery. It contains a horizontal or vertical set of cards that can be scrolled.
 */
export class DivGallery<T extends DivGalleryProps = DivGalleryProps> {
    readonly _props?: Exact<DivGalleryProps, T>;

    readonly type = 'gallery';
    /**
     * Accessibility for disabled people.
     */
    accessibility?: Type<IDivAccessibility>;
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
     * Element background. It can contain multiple layers.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Element stroke.
     */
    border?: Type<IDivBorder>;
    /**
     * Number of columns for block layout.
     */
    column_count?: Type<number> | DivExpression;
    /**
     * Merges cells in a column of the [grid](div-grid.md) element.
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Aligning elements in the direction perpendicular to the scroll direction. In horizontal
     * galleries:`start` — alignment to the top of the card;`center` — to the center;`end` — to the
     * bottom.</p><p>In vertical galleries:`start` — alignment to the left of the card;`center` — to
     * the center;`end` — to the right.
     */
    cross_content_alignment?: Type<DivGalleryCrossContentAlignment> | DivExpression;
    /**
     * Ordinal number of the gallery element to be scrolled to by default. For
     * `scroll_mode`:`default` — the scroll position is set to the beginning of the element, without
     * taking into account `item_spacing`;`paging` — the scroll position is set to the center of the
     * element.
     */
    default_item?: Type<number> | DivExpression;
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
     * Spacing between elements.
     */
    item_spacing?: Type<number> | DivExpression;
    /**
     * Gallery elements. Scrolling to elements can be implemented
     * using:`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal
     * number `item` inside an element, with the specified
     * `id`;`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element
     * inside an element, with the specified
     * `id`;`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous
     * element inside an element, with the specified `id`.</p><p>The optional `overflow` parameter is
     * used to set navigation when the first or last element is reached:`clamp` — transition will
     * stop at the border element;`ring` — go to the beginning or end, depending on the current
     * element.</p><p>By default, `clamp`.
     */
    items: Type<NonEmptyArray<Div>>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Gallery orientation.
     */
    orientation?: Type<DivGalleryOrientation> | DivExpression;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent
     * element.
     */
    restrict_parent_scroll?: Type<IntBoolean> | DivExpression;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Scroll type: `default` — continuous, `paging` — page-by-page.
     */
    scroll_mode?: Type<DivGalleryScrollMode> | DivExpression;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
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

    constructor(props: Exact<DivGalleryProps, T>) {
        this.accessibility = props.accessibility;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.background = props.background;
        this.border = props.border;
        this.column_count = props.column_count;
        this.column_span = props.column_span;
        this.cross_content_alignment = props.cross_content_alignment;
        this.default_item = props.default_item;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.height = props.height;
        this.id = props.id;
        this.item_spacing = props.item_spacing;
        this.items = props.items;
        this.margins = props.margins;
        this.orientation = props.orientation;
        this.paddings = props.paddings;
        this.restrict_parent_scroll = props.restrict_parent_scroll;
        this.row_span = props.row_span;
        this.scroll_mode = props.scroll_mode;
        this.selected_actions = props.selected_actions;
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

export interface DivGalleryProps {
    /**
     * Accessibility for disabled people.
     */
    accessibility?: Type<IDivAccessibility>;
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
     * Element background. It can contain multiple layers.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Element stroke.
     */
    border?: Type<IDivBorder>;
    /**
     * Number of columns for block layout.
     */
    column_count?: Type<number> | DivExpression;
    /**
     * Merges cells in a column of the [grid](div-grid.md) element.
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Aligning elements in the direction perpendicular to the scroll direction. In horizontal
     * galleries:`start` — alignment to the top of the card;`center` — to the center;`end` — to the
     * bottom.</p><p>In vertical galleries:`start` — alignment to the left of the card;`center` — to
     * the center;`end` — to the right.
     */
    cross_content_alignment?: Type<DivGalleryCrossContentAlignment> | DivExpression;
    /**
     * Ordinal number of the gallery element to be scrolled to by default. For
     * `scroll_mode`:`default` — the scroll position is set to the beginning of the element, without
     * taking into account `item_spacing`;`paging` — the scroll position is set to the center of the
     * element.
     */
    default_item?: Type<number> | DivExpression;
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
     * Spacing between elements.
     */
    item_spacing?: Type<number> | DivExpression;
    /**
     * Gallery elements. Scrolling to elements can be implemented
     * using:`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal
     * number `item` inside an element, with the specified
     * `id`;`div-action://set_next_item?id=[&overflow={clamp|ring}]` — scrolling to the next element
     * inside an element, with the specified
     * `id`;`div-action://set_previous_item?id=[&overflow={clamp|ring}]` — scrolling to the previous
     * element inside an element, with the specified `id`.</p><p>The optional `overflow` parameter is
     * used to set navigation when the first or last element is reached:`clamp` — transition will
     * stop at the border element;`ring` — go to the beginning or end, depending on the current
     * element.</p><p>By default, `clamp`.
     */
    items: Type<NonEmptyArray<Div>>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Gallery orientation.
     */
    orientation?: Type<DivGalleryOrientation> | DivExpression;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent
     * element.
     */
    restrict_parent_scroll?: Type<IntBoolean> | DivExpression;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Scroll type: `default` — continuous, `paging` — page-by-page.
     */
    scroll_mode?: Type<DivGalleryScrollMode> | DivExpression;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
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

export type DivGalleryCrossContentAlignment =
    | 'start'
    | 'center'
    | 'end';

export type DivGalleryOrientation =
    | 'horizontal'
    | 'vertical';

export type DivGalleryScrollMode =
    | 'paging'
    | 'default';
