import type { ContentAlignmentVertical } from '../types/alignment';

export function correctContentAlignmentVertical(
    orientation: string | undefined,
    defaultVal: ContentAlignmentVertical
): ContentAlignmentVertical {
    if (
        orientation === 'top' ||
        orientation === 'center' ||
        orientation === 'bottom' ||
        orientation === 'baseline' ||
        orientation === 'space-between' ||
        orientation === 'space-around' ||
        orientation === 'space-evenly'
    ) {
        return orientation;
    }

    return defaultVal;
}
