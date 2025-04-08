import type { Action } from '../../typings/common';
import type { Animation } from './animation';

export interface DivActionableData {
    action?: Action;
    actions?: Action[];
    longtap_actions?: Action[];
    doubletap_actions?: Action[];
    action_animation?: Animation;
    hover_start_actions?: Action[];
    hover_end_actions?: Action[];
    press_start_actions?: Action[];
    press_end_actions?: Action[];
    capture_focus_on_action?: boolean;
}
