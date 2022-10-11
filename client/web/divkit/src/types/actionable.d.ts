import type { Action } from '../../typings/common';
import type { Interpolation } from './base';

export interface ActionAnimation {
    name: 'fade' | 'scale' | 'set';
    duration?: number;
    start_delay?: number;
    start_value?: number;
    end_value?: number;
    interpolator: Interpolation;
    items?: ActionAnimation[];
}

export interface DivActionableData {
    action?: Action;
    actions?: Action[];
    longtap_actions?: Action[];
    doubletap_actions?: Action[];
    action_animation?: ActionAnimation;
}
