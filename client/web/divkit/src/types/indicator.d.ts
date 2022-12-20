import type { DivBaseData } from './base';
import type { FixedSize } from './sizes';
import type { Shape } from './shape';

// export type IndicatorAnimation = 'scale' | 'worm' | 'slider';

export interface DivIndicatorData extends DivBaseData {
    type: 'indicator';
    pager_id?: string;
    space_between_centers?: FixedSize;
    inactive_item_color?: string;
    active_item_color?: string;
    shape?: Shape;
    active_item_size?: number;
    // minimum_item_size?: number;
    // animation?: IndicatorAnimation;
}
