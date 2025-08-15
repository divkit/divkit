import type { Orientation } from '../types/orientation';

export function correctGeneralOrientation(
    orientation: string | undefined,
    defaultVal: Orientation
): Orientation {
    if (
        orientation === 'vertical' ||
        orientation === 'horizontal'
    ) {
        return orientation;
    }

    return defaultVal;
}
