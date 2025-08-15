import type { Accessibility, DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { Dimension } from './sizes';
import type { FontWeight } from './text';
import type { Drawable } from './drawable';
import type { BooleanInt } from '../../typings/common';
import type { EdgeInsets } from './edgeInserts';

export interface Point {
    x: Dimension;
    y: Dimension;
}

export interface SliderTextStyle {
    font_size: number;
    // font_size_unit;
    font_weight?: FontWeight;
    font_weight_value?: number;
    text_color?: string;
    offset?: Point;
}

export interface SliderRange {
    start?: number;
    end?: number;
    track_active_style?: Drawable;
    track_inactive_style?: Drawable;
    margins?: EdgeInsets;
}

export interface DivSliderData extends DivBaseData, DivActionableData {
    type: 'slider';

    min_value?: number;
    max_value?: number;
    thumb_value_variable?: string;
    thumb_secondary_value_variable?: string;
    thumb_style: Drawable;
    thumb_secondary_style?: Drawable;
    thumb_text_style?: SliderTextStyle;
    thumb_secondary_text_style?: SliderTextStyle;
    track_active_style: Drawable;
    tick_mark_active_style?: Drawable;
    track_inactive_style: Drawable;
    tick_mark_inactive_style?: Drawable;
    ranges?: SliderRange[];
    secondary_value_accessibility?: Accessibility;
    is_enabled?: BooleanInt;
}
