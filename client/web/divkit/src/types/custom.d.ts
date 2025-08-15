import type { DivBaseData } from './base';

export interface DivCustomData extends DivBaseData {
    type: 'custom';

    items?: DivBaseData[];
    custom_type: string;
    custom_props?: object;
}
