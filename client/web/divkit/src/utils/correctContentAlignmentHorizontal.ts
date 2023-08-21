import type { ContentAlignmentHorizontal } from '../types/alignment';

export function correctContentAlignmentHorizontal(
    orientation: string | undefined,
    defaultVal: ContentAlignmentHorizontal
): ContentAlignmentHorizontal {
    if (
        orientation === 'left' ||
        orientation === 'center' ||
        orientation === 'right' ||
        orientation === 'space-between' ||
        orientation === 'space-around' ||
        orientation === 'space-evenly'
    ) {
        return orientation;
    }

    return defaultVal;
}
