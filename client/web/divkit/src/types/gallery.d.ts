import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { Orientation } from './orientation';
import type { BooleanInt } from '../../typings/common';
import type { CollectionItemBuilder } from './itemBuilder';

export type GalleryCrossAlignment = 'start' | 'center' | 'end';

export type GalleryScrollMode = 'default' | 'paging';

export type GalleryShowScrollbar = 'none' | 'auto';

export interface DivGalleryData extends DivBaseData, DivActionableData {
    type: 'gallery';
    cross_content_alignment?: GalleryCrossAlignment;
    column_count?: number;
    item_spacing?: number;
    cross_spacing?: number;
    scroll_mode?: GalleryScrollMode;
    items: DivBaseData[];
    orientation?: Orientation;
    restrict_parent_scroll?: BooleanInt;
    default_item?: number;
    scrollbar?: GalleryShowScrollbar;
    item_builder?: CollectionItemBuilder;
}
