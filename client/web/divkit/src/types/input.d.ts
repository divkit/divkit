import type { DivBaseData } from './base';
import type { FontWeight } from './text';
import type { Action, BooleanInt } from '../../typings/common';
import { AlignmentHorizontal, AlignmentVertical } from './alignment';

export type KeyboardType = 'single_line_text' | 'multi_line_text' | 'phone' | 'number' | 'email' | 'uri' | 'password';

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

export interface CurrencyInputMask extends MaskBase {
    type: 'currency';
    locale?: string;
}

export interface PhoneInputMask extends MaskBase {
    type: 'phone';
}

export type InputMask = FixedLengthInputMask | CurrencyInputMask | PhoneInputMask;

export type InputAutocapitalization = 'auto' | 'none' | 'words' | 'sentences' | 'all_characters';

export type InputEnterKeyType = 'default' | 'go' | 'search' | 'send' | 'done';

export interface InputValidatorBase {
    label_id: string;
    allow_empty?: BooleanInt;
    variable: string;
}

export interface InputValidatorRegex extends InputValidatorBase {
    type: 'regex';
    pattern: string;
}

export interface InputValidatorExpression extends InputValidatorBase {
    type: 'expression';
    condition: BooleanInt;
}

export type InputValidator = InputValidatorRegex | InputValidatorExpression;

export interface InputFilterRegex {
    type: 'regex';
    pattern: string;
}

export interface InputFilterExpression {
    type: 'expression';
    condition: BooleanInt;
}

export type InputFilter = InputFilterRegex | InputFilterExpression;

export interface DivInputData extends DivBaseData {
    type: 'input';

    font_size?: number;
    // font_size_unit
    font_family?: string;
    font_weight?: FontWeight;
    font_weight_value?: number;
    filters?: InputFilter[];
    text_color?: string;
    text_variable: string;
    text_alignment_horizontal?: AlignmentHorizontal;
    text_alignment_vertical?: AlignmentVertical;
    line_height?: number;
    max_visible_lines?: number;
    max_length?: number;
    letter_spacing?: number;
    hint_text?: string;
    hint_color?: string;
    highlight_color?: string;
    // native_interface
    enter_key_actions?: Action[];
    keyboard_type?: KeyboardType;
    mask?: InputMask;
    enter_key_type?: InputEnterKeyType;
    select_all_on_focus?: BooleanInt;
    is_enabled?: BooleanInt;
    autocapitalization?: InputAutocapitalization;
    validators?: InputValidator[];
}
