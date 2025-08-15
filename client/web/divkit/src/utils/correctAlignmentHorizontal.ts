import type { Direction } from '../../typings/common';
import type { AlignmentHorizontal } from '../types/alignment';

export function correctAlignmentHorizontal(
    orientation: string | undefined,
    direction: Direction,
    defaultVal: AlignmentHorizontal
): AlignmentHorizontal {
    if (
        orientation === 'left' ||
        orientation === 'center' ||
        orientation === 'right' ||
        orientation === 'start' ||
        orientation === 'end'
    ) {
        if (orientation === 'left') {
            return direction === 'ltr' ? 'start' : 'end';
        } else if (orientation === 'right') {
            return direction === 'ltr' ? 'end' : 'start';
        }
        return orientation;
    }

    return defaultVal;
}
