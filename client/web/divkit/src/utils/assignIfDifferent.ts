import { isDeepEqual } from './isDeepEqual';

export function assignIfDifferent<A, B>(newVal: A, defaultVal: B): A | B {
    if (isDeepEqual(newVal, defaultVal)) {
        return defaultVal;
    }
    return newVal;
}
