import type { Action } from '../../typings/common';
import type { Animation } from './animation';

export interface DivActionableData {
    action?: Action;
    actions?: Action[];
    longtap_actions?: Action[];
    doubletap_actions?: Action[];
    action_animation?: Animation;
}
