export type ContentAlignmentVerticalMapped =
    'start' | 'center' | 'end' |
    'space-between' | 'space-around' | 'space-evenly' |
    'baseline';

export function correctContentAlignmentVertical(
    orientation: string | undefined,
    defaultVal: ContentAlignmentVerticalMapped
): ContentAlignmentVerticalMapped {
    if (
        orientation === 'top' ||
        orientation === 'center' ||
        orientation === 'bottom' ||
        orientation === 'baseline' ||
        orientation === 'space-between' ||
        orientation === 'space-around' ||
        orientation === 'space-evenly'
    ) {
        if (orientation === 'top') {
            return 'start';
        } else if (orientation === 'bottom') {
            return 'end';
        }
        return orientation;
    }

    return defaultVal;
}
