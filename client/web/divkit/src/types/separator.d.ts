import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { Orientation } from './orientation';

export interface DelimiterStyle {
    color?: string;
    orientation?: Orientation;
}

export interface DivSeparatorData extends DivBaseData, DivActionableData {
    type: 'separator';

    delimiter_style?: DelimiterStyle;
}
