export type AlignmentVerticalMapped = 'start' | 'end' | 'center' | 'baseline';

export function correctAlignmentVertical(
    orientation: string | undefined,
    defaultVal: AlignmentVerticalMapped
): AlignmentVerticalMapped {
    if (
        orientation === 'top' ||
        orientation === 'center' ||
        orientation === 'bottom' ||
        orientation === 'baseline'
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
