import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { AlignmentHorizontal, AlignmentVertical } from './alignment';

export type ImageScale = 'no_scale' | 'fill' | 'fit';

export interface DivAspect {
    ratio: number;
}

export interface DivImageData extends DivBaseData, DivActionableData {
    type: 'image' | 'gif';

    image_url?: string;
    gif_url?: string;
    placeholder_color?: string;
    scale?: ImageScale;
    content_alignment_horizontal?: AlignmentHorizontal;
    content_alignment_vertical?: AlignmentVertical;
    preview?: string;
    // preload_required
    aspect?: DivAspect;
    // high_priority_preview_show
    tint_color?: string;
}
