import type { Size } from './sizes';
import type { AlignmentHorizontal, AlignmentVertical } from './alignment';
import type { Border } from './border';
import type { Background } from './background';
import type { EdgeInsets } from './edgeInserts';
import type { Dimension } from './sizes';
import type { Action, AnimatorDirection, AnimatorRepeatCount, BooleanInt, DisappearAction, DivVariable, Interpolation, VariableTrigger, VisibilityAction } from '../../typings/common';
import type { Focus } from './focus';
import type { Animation } from './animation';
import type { EvalTypes } from '../expressions/eval';

export type AccessibilityType = 'none' | 'button' | 'image' | 'text' | 'edit_text' |
    'header' | 'tab_bar' | 'list' | 'select' | 'checkbox' | 'radio' | 'auto';

export interface Accessibility {
    description?: string;
    type?: AccessibilityType;
    // state_description
    // hint
    is_checked?: BooleanInt;
    // mode
}

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

export interface TooltipModeModal {
    type: 'modal';
}

export interface TooltipModeNonModal {
    type: 'non_modal';
}

export type TooltipMode = TooltipModeModal | TooltipModeNonModal;

export interface Tooltip {
    id: string;
    div: DivBaseData;
    position: 'left' | 'top-left' | 'top' | 'top-right' | 'right' |
        'bottom-right' | 'bottom' | 'bottom-left' | 'center';
    duration?: number;
    offset?: {
        x: {
            value: number;
            // unit;
        };
        y: {
            value: number;
            // unit;
        };
    };
    animation_in?: Animation;
    animation_out?: Animation;

    mode?: TooltipMode;
}

export interface DivLayoutProvider {
    width_variable_name?: string;
    height_variable_name?: string;
}

export interface AnimatorBase {
    id: string;
    variable_name: string;
    duration: number;
    start_delay?: number;
    interpolator?: Interpolation;
    direction?: AnimatorDirection;
    repeat_count?: AnimatorRepeatCount;
    end_actions?: Action[];
    cancel_actions?: Action[];
}

export interface NumberAnimator extends AnimatorBase {
    type: 'number_animator';
    start_value?: number;
    end_value: number;
}

export interface ColorAnimator extends AnimatorBase {
    type: 'color_animator';
    start_value?: string;
    end_value: string;
}

export type Animator = NumberAnimator | ColorAnimator;

export interface DivFunctionArgument {
    name: string;
    type: EvalTypes;
}

export interface DivFunction {
    name: string;
    body: string;
    return_type: EvalTypes;
    arguments: DivFunctionArgument[];
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
    tooltips?: Tooltip[];
    accessibility?: Accessibility;
    extensions?: Extension[];
    transition_in?: AppearanceTransition;
    transition_change?: TransitionChange;
    transition_out?: AppearanceTransition;
    transition_triggers?: TransitionTrigger[];
    selected_actions?: Action[];
    focus?: Focus;
    layout_provider?: DivLayoutProvider;
    transform?: Transform;
    variables?: DivVariable[];
    variable_triggers?: VariableTrigger[];
    animators?: Animator[];
    functions?: DivFunction[];
}
