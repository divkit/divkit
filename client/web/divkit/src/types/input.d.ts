import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { FontWeight } from './text';
import type { BooleanInt } from '../../typings/common';

export type KeyboardType = 'text' | 'phone' | 'number' | 'email' | 'uri';

export interface DivInputData extends DivBaseData, DivActionableData {
    type: 'type';

    font_size?: number;
    // font_size_unit
    // font_family
    font_weight?: FontWeight;
    text_color?: string;
    text_variable: string;
    line_height?: number;
    max_lines?: number;
    letter_spacing?: number;
    hint_text?: string;
    hint_color?: string;
    highlight_color?: string;
    // native_interface
    keyboard_type?: KeyboardType;
    select_all_on_focus?: BooleanInt;
}
