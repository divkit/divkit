import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { Action } from '../../typings/common';
import type { FixedSize } from './sizes';
import type { AlignmentHorizontal, AlignmentVertical } from './alignment';
import type { GradientBackground, SolidBackground } from './background';
import type { BooleanInt } from '../../typings/common';
import type { TintMode } from './image';
import type { Shadow, Stroke } from './border';

export type FontWeight = 'light' | 'regular' | 'medium' | 'bold';

export type LineStyle = 'none' | 'single';

export type Truncate = 'none' | 'end';

export interface TextStyles {
    font_size?: number;
    font_family?: string;
    // font_size_unit
    font_feature_settings?: string;
    letter_spacing?: number;
    font_weight?: FontWeight;
    font_weight_value?: number;
    text_color?: string;
    underline?: LineStyle;
    strike?: LineStyle;
    line_height?: number;
    text_shadow?: Shadow;
}

export interface TextRangeBorder {
    stroke?: Stroke;
    corner_radius?: number;
}

export interface TextRange extends TextStyles {
    start: number;
    end: number;
    actions?: Action[];
    top_offset?: number;
    border?: TextRangeBorder;
    background?: SolidBackground;
}

export interface TextImage {
    start: number;
    url: string;
    width?: FixedSize;
    height?: FixedSize;
    tint_color?: string;
    tint_mode?: TintMode;
    preload_required?: BooleanInt;
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface TextEllipsis {
    // text
    // actions
    // ranges
    // images
}

export interface DivTextData extends DivBaseData, DivActionableData, TextStyles {
    type: 'text';
    // font_size_unit;
    max_lines?: number;
    // min_hidden_lines;
    auto_ellipsize?: BooleanInt;
    text_alignment_horizontal?: AlignmentHorizontal;
    text_alignment_vertical?: AlignmentVertical;
    focused_text_color?: string;
    text_gradient?: GradientBackground;
    text: string;
    ranges?: TextRange[];
    images?: TextImage[];
    // ellipsis;
    selectable?: BooleanInt;
    truncate?: Truncate;
}
