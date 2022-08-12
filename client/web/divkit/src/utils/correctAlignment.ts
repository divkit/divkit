import type { Align } from '../types/layoutParams';

export function correctAlignment(
    orientation: string | undefined,
    defaultVal: Align
): Align {
    if (
        orientation === 'start' ||
        orientation === 'center' ||
        orientation === 'end'
    ) {
        return orientation;
    }

    return defaultVal;
}
