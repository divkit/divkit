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
    DivFixedSize,
    DivShape,
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
 * Progress indicator for [pager](div-pager.md).
 */
export class DivIndicator<T extends DivIndicatorProps = DivIndicatorProps> {
    readonly _props?: Exact<DivIndicatorProps, T>;

    readonly type = 'indicator';
    /**
     * Accessibility for disabled people.
     */
    accessibility?: Type<IDivAccessibility>;
    /**
     * Active indicator color.
     */
    active_item_color?: Type<string | DivExpression>;
    /**
     * A size multiplier for an active indicator.
     */
    active_item_size?: Type<number | DivExpression>;
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
     * Animation of switching between indicators.
     */
    animation?: Type<DivIndicatorAnimation | DivExpression>;
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
     * Indicator color.
     */
    inactive_item_color?: Type<string | DivExpression>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * A size multiplier for a minimal indicator. It is used when the required number of indicators
     * don't fit on the screen.
     */
    minimum_item_size?: Type<number | DivExpression>;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * ID of the pager that is a data source for an indicator.
     */
    pager_id?: Type<string>;
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
     * Indicator shape.
     */
    shape?: Type<DivShape>;
    /**
     * Spacing between indicator centers.
     */
    space_between_centers?: Type<DivFixedSize>;
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

    constructor(props?: Exact<DivIndicatorProps, T>) {
        this.accessibility = props?.accessibility;
        this.active_item_color = props?.active_item_color;
        this.active_item_size = props?.active_item_size;
        this.alignment_horizontal = props?.alignment_horizontal;
        this.alignment_vertical = props?.alignment_vertical;
        this.alpha = props?.alpha;
        this.animation = props?.animation;
        this.background = props?.background;
        this.border = props?.border;
        this.column_span = props?.column_span;
        this.extensions = props?.extensions;
        this.focus = props?.focus;
        this.height = props?.height;
        this.id = props?.id;
        this.inactive_item_color = props?.inactive_item_color;
        this.margins = props?.margins;
        this.minimum_item_size = props?.minimum_item_size;
        this.paddings = props?.paddings;
        this.pager_id = props?.pager_id;
        this.row_span = props?.row_span;
        this.selected_actions = props?.selected_actions;
        this.shape = props?.shape;
        this.space_between_centers = props?.space_between_centers;
        this.tooltips = props?.tooltips;
        this.transform = props?.transform;
        this.transition_change = props?.transition_change;
        this.transition_in = props?.transition_in;
        this.transition_out = props?.transition_out;
        this.transition_triggers = props?.transition_triggers;
        this.visibility = props?.visibility;
        this.visibility_action = props?.visibility_action;
        this.visibility_actions = props?.visibility_actions;
        this.width = props?.width;
    }
}

export interface DivIndicatorProps {
    /**
     * Accessibility for disabled people.
     */
    accessibility?: Type<IDivAccessibility>;
    /**
     * Active indicator color.
     */
    active_item_color?: Type<string | DivExpression>;
    /**
     * A size multiplier for an active indicator.
     */
    active_item_size?: Type<number | DivExpression>;
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
     * Animation of switching between indicators.
     */
    animation?: Type<DivIndicatorAnimation | DivExpression>;
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
     * Indicator color.
     */
    inactive_item_color?: Type<string | DivExpression>;
    /**
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * A size multiplier for a minimal indicator. It is used when the required number of indicators
     * don't fit on the screen.
     */
    minimum_item_size?: Type<number | DivExpression>;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * ID of the pager that is a data source for an indicator.
     */
    pager_id?: Type<string>;
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
     * Indicator shape.
     */
    shape?: Type<DivShape>;
    /**
     * Spacing between indicator centers.
     */
    space_between_centers?: Type<DivFixedSize>;
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

export type DivIndicatorAnimation =
    | 'scale'
    | 'worm'
    | 'slider';
