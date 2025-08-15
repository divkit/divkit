import type { Direction } from '../../typings/common';

export type ContentAlignmentHorizontalMapped =
    /* 'left' |  */'center' | /* 'right' | */
    'start' | 'end' |
    'space-between' | 'space-around' | 'space-evenly';

export function correctContentAlignmentHorizontal(
    orientation: string | undefined,
    direction: Direction,
    defaultVal: ContentAlignmentHorizontalMapped
): ContentAlignmentHorizontalMapped {
    if (
        orientation === 'left' ||
        orientation === 'center' ||
        orientation === 'right' ||
        orientation === 'space-between' ||
        orientation === 'space-around' ||
        orientation === 'space-evenly' ||
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
