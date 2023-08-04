import type { Size } from './sizes';
import type { AlignmentHorizontal, AlignmentVertical } from './alignment';
import type { Border } from './border';
import type { Background } from './background';
import type { EdgeInsets } from './edgeInserts';
import type { DisappearAction, VisibilityAction } from './visibilityAction';
import type { Dimension } from './sizes';
import type { Action } from '../../typings/common';
import type { Focus } from './focus';

export interface Accessibility {
    description?: string;
    // type
    // state_description
    // hint
    // mode
}

export type Interpolation = 'linear' | 'ease' | 'ease_in' | 'ease_out' | 'ease_in_out' | 'spring';

export interface TransitionBase {
    start_delay?: number;
    duration?: number;
    interpolator?: Interpolation;
}

export interface FadeTransition extends TransitionBase {
    type: 'fade';
    alpha?: number;
}

export interface ScaleTransition extends TransitionBase {
    type: 'scale';
    scale?: number;
    pivot_x?: number;
    pivot_y?: number;
}

export type SlideTransitionEdge = 'left' | 'top' | 'right' | 'bottom';

export interface SlideTransition extends TransitionBase {
    type: 'slide';
    edge?: SlideTransitionEdge;
    distance?: Dimension;
}

export type AnyTransition = FadeTransition | ScaleTransition | SlideTransition;

interface TransitionSet {
    type: 'set';
    items: AppearanceTransition[];
}

export type AppearanceTransition = AnyTransition | TransitionSet;

export type Visibility = 'visible' | 'invisible' | 'gone';
export type TransitionTrigger = 'state_change' | 'visibility_change';

export interface ChangeBoundsTransition extends TransitionBase {
    type: 'change_bounds';
}

export type TransitionChange = ChangeBoundsTransition | {
    type: 'set';
    items: TransitionChange[];
}

export interface PivotFixedValue {
    type: 'pivot-fixed';
    value: number;
}

export interface PivotPercentageValue {
    type: 'pivot-percentage';
    value: number;
}

export type PivotValue = PivotFixedValue | PivotPercentageValue;

export interface Transform {
    pivot_x?: PivotValue;
    pivot_y?: PivotValue;
    rotation?: number;
}

export interface Extension {
    id: string;
    params?: object;
}

export interface DivBaseData {
    type: string;
    id?: string;
    border?: Border;
    width?: Size;
    height?: Size;
    background?: Background[];
    paddings?: EdgeInsets;
    margins?: EdgeInsets;
    alpha?: number;
    alignment_horizontal?: AlignmentHorizontal;
    alignment_vertical?: AlignmentVertical;
    row_span?: number;
    column_span?: number;
    visibility?: Visibility;
    visibility_action?: VisibilityAction;
    visibility_actions?: VisibilityAction[];
    disappear_actions?: DisappearAction[];
    // tooltips;
    accessibility?: Accessibility;
    extensions?: Extension[];
    transition_in?: AppearanceTransition;
    transition_change?: TransitionChange;
    transition_out?: AppearanceTransition;
    transition_triggers?: TransitionTrigger[];
    selected_actions?: Action[];
    focus?: Focus;
    transform?: Transform;
}
