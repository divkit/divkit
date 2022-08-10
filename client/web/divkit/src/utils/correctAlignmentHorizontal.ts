import type { AlignmentHorizontal } from '../types/alignment';

export function correctAlignmentHorizontal(
    orientation: string | undefined,
    defaultVal: AlignmentHorizontal
): AlignmentHorizontal {
    if (
        orientation === 'left' ||
        orientation === 'center' ||
        orientation === 'right'
    ) {
        return orientation;
    }

    return defaultVal;
}
