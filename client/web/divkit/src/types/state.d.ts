import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';

export interface State {
    state_id: string;
    div?: DivBaseData;
}

export interface DivStateData extends DivBaseData, DivActionableData {
    type: 'state';
    states: State[];
    state_id_variable?: string;
    /** @deprecated */
    div_id?: string;
    default_state_id?: string;
    // transition_animation_selector
}
