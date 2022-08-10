import { isNumber } from './isNumber';

export function isNonNegativeNumber(val: number | undefined): val is number {
    return isNumber(val) && val >= 0;
}
