import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { AlignmentHorizontal, AlignmentVertical } from './alignment';
import type { FadeTransition } from './base';
import type { Filter } from './filter';
import type { ImageScale } from './imageScale';
import type { BooleanInt } from '../../typings/common';

export type TintMode = 'source_in' | 'source_atop' | 'darken' | 'lighten' | 'multiply' | 'screen';

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
    preload_required?: BooleanInt;
    aspect?: DivAspect;
    high_priority_preview_show?: BooleanInt;
    tint_color?: string;
    tint_mode?: TintMode;
    appearance_animation?: FadeTransition;
    filters?: Filter[];
}
