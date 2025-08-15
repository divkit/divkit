import type { MaybeMissing } from '../expressions/json';
import type { TabDelimiterStyle } from '../types/tabs';
import { isPositiveNumber } from './isPositiveNumber';

export interface TabsDelimiter {
    url: string;
    width?: number;
    height?: number;
}

export function correctTabDelimiterStyle(
    style: MaybeMissing<TabDelimiterStyle> | undefined,
    defaultValue: TabsDelimiter | undefined
): TabsDelimiter | undefined {
    if (!style || !style.image_url || typeof style.image_url !== 'string') {
        return defaultValue;
    }

    const res: TabsDelimiter = {
        url: style.image_url
    };

    if (style.width?.type === 'fixed' && isPositiveNumber(style.width.value)) {
        res.width = style.width.value;
    }
    if (style.height?.type === 'fixed' && isPositiveNumber(style.height.value)) {
        res.height = style.height.value;
    }

    return res;
}
