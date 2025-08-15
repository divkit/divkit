import type { FontWeight } from '../types/text';
import { fontWeightToCss } from './fontWeightToCss';

export function correctFontWeight(
    fontWeight: FontWeight | undefined,
    fontWeightValue: number | undefined,
    defaultVal: number | undefined
): number | undefined {
    if (typeof fontWeightValue === 'number' && fontWeightValue > 0) {
        return fontWeightValue;
    }
    return fontWeightToCss(fontWeight) || defaultVal;
}
