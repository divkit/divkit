import type { AlignmentVertical } from '../types/alignment';

export function correctAlignmentVertical(
    orientation: string | undefined,
    defaultVal: AlignmentVertical
): AlignmentVertical {
    if (
        orientation === 'top' ||
        orientation === 'center' ||
        orientation === 'bottom' ||
        orientation === 'baseline'
    ) {
        return orientation;
    }

    return defaultVal;
}
