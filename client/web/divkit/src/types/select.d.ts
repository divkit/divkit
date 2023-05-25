import type { DivBaseData } from './base';
import type { FontWeight } from './text';

export interface SelectItem {
    // text = value
    text?: string;
    value: string;
}

export interface DivSelectData extends DivBaseData {
    type: 'select';

    options: SelectItem[];
    value_variable: string;
    font_size?: number;
    // font_size_unit
    font_family?: string;
    font_weight?: FontWeight;
    hint_text?: string;
    hint_color?: string;
    line_height?: number;
    text_color?: string;
    letter_spacing?: number;
}
