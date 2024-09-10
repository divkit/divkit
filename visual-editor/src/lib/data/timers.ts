import type { Action } from '@divkitframework/divkit/typings/common';

export interface JsonTimer {
    id: string;
    duration?: string | number;
    tick_interval?: string | number;
    value_variable?: string;
    tick_actions?: Action[];
    end_actions?: Action[];
}

export interface Timer extends JsonTimer {
    __id: string;
}
