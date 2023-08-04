import type { Background } from './background';
import type { Border } from './border';
import type { Action } from '../../typings/common';

export interface Focus {
    background?: Background[];
    border?: Border;
    on_focus?: Action[];
    on_blur?: Action[];
    // next_focus_ids
}
