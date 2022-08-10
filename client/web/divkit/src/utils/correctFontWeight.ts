import type { FontWeight } from '../types/text';
import { fontWeightToCss } from './fontWeightToCss';

export function correctFontWeight(
    fontWeight: FontWeight | undefined,
    defaultVal: number | undefined
): number | undefined {
    return fontWeightToCss(fontWeight) || defaultVal;
}
