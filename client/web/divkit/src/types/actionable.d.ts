import type { Action } from '../../typings/common';
import type { Interpolation } from './base';

export type AnimationType = 'fade' | 'scale' | 'native' | 'no_animation';

export interface AnyAnimation {
    name: AnimationType;
    duration?: number;
    start_delay?: number;
    start_value?: number;
    end_value?: number;
    interpolator?: Interpolation;
}

export interface AnimationSet {
    name: 'set';
    items: ActionAnimation[];
}

export type ActionAnimation = AnyAnimation | AnimationSet;

export interface DivActionableData {
    action?: Action;
    actions?: Action[];
    longtap_actions?: Action[];
    doubletap_actions?: Action[];
    action_animation?: ActionAnimation;
}
