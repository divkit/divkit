import type { FontWeight } from '../types/text';

export function fontWeightToCss(fontWeight?: FontWeight | undefined): number | undefined {
    if (
        fontWeight === 'light' ||
        fontWeight === 'medium' ||
        fontWeight === 'bold' ||
        fontWeight === 'regular'
    ) {
        if (fontWeight === 'medium') {
            return 500;
        } else if (fontWeight === 'bold') {
            return 700;
        } else if (fontWeight === 'light') {
            return 300;
        }

        return 400;
    }
}
