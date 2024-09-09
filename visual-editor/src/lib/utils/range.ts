import { isEqual } from './isEqual';

export type FontWeight = 'light' | 'regular' | 'medium' | 'bold';

export type LineStyle = 'none' | 'single';

export interface TextStyles {
    font_size?: number;
    font_family?: string;
    // font_size_unit
    letter_spacing?: number;
    font_weight?: FontWeight;
    text_color?: string;
    underline?: LineStyle;
    strike?: LineStyle;
    line_height?: number;
}

export interface Action {
    log_id: string;
    url?: string;
}

export interface TextRange extends TextStyles {
    start: number;
    end: number;
    actions?: Action[];
    top_offset?: number;
    // border?: TextRangeBorder;
    // background?: SolidBackground;
}

export interface FixedSize {
    type: 'fixed';
    value: number;
}

export interface TextImage {
    start: number;
    url: string;
    width?: FixedSize;
    height?: FixedSize;
    tint_color?: string;
    // tint_mode?: TintMode;
}

export function rangeStyle(range: TextRange): Omit<TextRange, 'start' | 'end'> {
    const filteredRange: Omit<TextRange, 'start' | 'end'> = {};
    for (const key in range) {
        if (key !== 'start' && key !== 'end') {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            filteredRange[key as keyof typeof filteredRange] = range[key as keyof typeof range] as any;
        }
    }
    return filteredRange;
}

export function isRangeStyleEqual(range0: TextRange, range1: TextRange): boolean {
    const filteredRange0 = rangeStyle(range0);
    const filteredRange1 = rangeStyle(range1);

    return isEqual(filteredRange0, filteredRange1);
}
