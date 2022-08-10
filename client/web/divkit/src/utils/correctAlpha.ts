import { isNonNegativeNumber } from './isNonNegativeNumber';

export function correctAlpha(val: number | undefined, defaultVal: number): number {
    if (!isNonNegativeNumber(val) || val === undefined || val > 1) {
        return defaultVal;
    }
    return Number(val);
}
