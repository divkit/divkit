import type { DivBaseData } from './base';
import type { FixedSize } from './sizes';
import type { Shape } from './shape';

// export type IndicatorAnimation = 'scale' | 'worm' | 'slider';

export interface DivIndicatorDefaultItemPlacement {
    type: 'default';
    space_between_centers?: FixedSize;
}

export interface DivIndicatorStretchItemPlacement {
    type: 'stretch';
    item_spacing?: FixedSize;
    max_visible_items?: number;
}

export type DivIndicatorItemsPlacement = DivIndicatorDefaultItemPlacement | DivIndicatorStretchItemPlacement;

export interface DivIndicatorData extends DivBaseData {
    type: 'indicator';
    pager_id?: string;
    /** @deprecated */
    space_between_centers?: FixedSize;
    inactive_item_color?: string;
    active_item_color?: string;
    /** @deprecated */
    shape?: Shape;
    active_shape?: Shape;
    inactive_shape?: Shape;
    /** @deprecated */
    active_item_size?: number;
    // minimum_item_size?: number;
    // animation?: IndicatorAnimation;
    items_placement?: DivIndicatorItemsPlacement;
}
