import type { BooleanInt } from '../../typings/common';
import type { DivBaseData } from './base';

export interface DivSwitchData extends DivBaseData {
    type: 'switch';

    is_on_variable: string;
    is_enabled?: BooleanInt;
    on_color?: string;
}
