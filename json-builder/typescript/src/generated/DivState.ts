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
    DivTransitionSelector,
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
 * It contains sets of states for visual elements and switches between them.
 */
export class DivState<T extends DivStateProps = DivStateProps> {
    readonly _props?: Exact<DivStateProps, T>;

    readonly type = 'state';
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
     * Merges cells in a column of the [grid](div-grid.md) element.
     */
    column_span?: Type<number> | DivExpression;
    /**
     * ID of the status that will be set by default. If the parameter isnt set, the first state of
     * the `states` will be set.
     */
    default_state_id?: Type<string> | DivExpression;
    /**
     * ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
     *
     * @deprecated
     */
    div_id?: Type<string>;
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
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number> | DivExpression;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * States. Each element can have a few states with a different layout. Transition between states
     * is performed using [special scheme](../../interaction.dita) of the [action](div-action.md)
     * element.
     */
    states: Type<NonEmptyArray<IDivStateState>>;
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
     * It determines which events trigger transition animations.
     *
     * @deprecated
     */
    transition_animation_selector?: Type<DivTransitionSelector> | DivExpression;
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

    constructor(props: Exact<DivStateProps, T>) {
        this.accessibility = props.accessibility;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.background = props.background;
        this.border = props.border;
        this.column_span = props.column_span;
        this.default_state_id = props.default_state_id;
        this.div_id = props.div_id;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.height = props.height;
        this.id = props.id;
        this.margins = props.margins;
        this.paddings = props.paddings;
        this.row_span = props.row_span;
        this.selected_actions = props.selected_actions;
        this.states = props.states;
        this.tooltips = props.tooltips;
        this.transform = props.transform;
        this.transition_animation_selector = props.transition_animation_selector;
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

export interface DivStateProps {
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
     * Merges cells in a column of the [grid](div-grid.md) element.
     */
    column_span?: Type<number> | DivExpression;
    /**
     * ID of the status that will be set by default. If the parameter isnt set, the first state of
     * the `states` will be set.
     */
    default_state_id?: Type<string> | DivExpression;
    /**
     * ID of an element to search in the hierarchy. The ID must be unique at one hierarchy level.
     *
     * @deprecated
     */
    div_id?: Type<string>;
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
     * External margins from the element stroke.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Internal margins from the element stroke.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Merges cells in a string of the [grid](div-grid.md) element.
     */
    row_span?: Type<number> | DivExpression;
    /**
     * List of [actions](div-action.md) to be executed when selecting an element in
     * [pager](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * States. Each element can have a few states with a different layout. Transition between states
     * is performed using [special scheme](../../interaction.dita) of the [action](div-action.md)
     * element.
     */
    states: Type<NonEmptyArray<IDivStateState>>;
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
     * It determines which events trigger transition animations.
     *
     * @deprecated
     */
    transition_animation_selector?: Type<DivTransitionSelector> | DivExpression;
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

export interface IDivStateState {
    /**
     * State appearance animation. Use `transition_in` instead.
     *
     * @deprecated
     */
    animation_in?: Type<IDivAnimation>;
    /**
     * State disappearance animation. Use `transition_out` instead.
     *
     * @deprecated
     */
    animation_out?: Type<IDivAnimation>;
    /**
     * Contents. If the parameter is missing, the state won't be displayed.
     */
    div?: Type<Div>;
    /**
     * State ID. It must be unique at one hierarchy level.
     */
    state_id: Type<string>;
    /**
     * Actions when swiping the state horizontally.
     *
     * @deprecated
     */
    swipe_out_actions?: Type<NonEmptyArray<IDivAction>>;
}
