import type { CornersRadius } from '../types/border';
import { borderRadius } from './borderRadius';
import { isNonNegativeNumber } from './isNonNegativeNumber';

export function correctBorderRadius(
    cornersRadius: CornersRadius,
    defaultRadius: number,
    fontSize: number,
    defaultVal: string
): string {
    const list = [
        cornersRadius['top-left'],
        cornersRadius['top-right'],
        cornersRadius['bottom-right'],
        cornersRadius['bottom-left']
    ];

    for (let i = 0; i < list.length; ++i) {
        if (list[i] && !isNonNegativeNumber(list[i])) {
            return defaultVal;
        }
    }

    return borderRadius(cornersRadius, defaultRadius, fontSize);
}
