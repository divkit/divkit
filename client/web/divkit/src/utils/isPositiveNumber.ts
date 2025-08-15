import { isNumber } from './isNumber';

export function isPositiveNumber(val: number | undefined): val is number {
    return isNumber(val) && val > 0;
}
