import type { DivBaseData } from './base';
import type { FontWeight } from './text';
import type { BooleanInt } from '../../typings/common';

export type KeyboardType = 'single_line_text' | 'multi_line_text' | 'phone' | 'number' | 'email' | 'uri';

export interface MaskBase {
    raw_text_variable: string;
}

export interface FixedLengthInputMaskPattern {
    key: string;
    regex?: string;
    placeholder?: string;
}

export interface FixedLengthInputMask extends MaskBase {
    type: 'fixed_length';
    pattern: string;
    pattern_elements: FixedLengthInputMaskPattern[];
    always_visible?: BooleanInt;
}

export type InputMask = FixedLengthInputMask;

export interface DivInputData extends DivBaseData {
    type: 'input';

    font_size?: number;
    // font_size_unit
    // font_family
    font_weight?: FontWeight;
    text_color?: string;
    text_variable: string;
    line_height?: number;
    max_visible_lines?: number;
    letter_spacing?: number;
    hint_text?: string;
    hint_color?: string;
    highlight_color?: string;
    // native_interface
    keyboard_type?: KeyboardType;
    mask?: InputMask;
    select_all_on_focus?: BooleanInt;
}
