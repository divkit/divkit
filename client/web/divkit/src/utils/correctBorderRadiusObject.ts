import type { CornersRadius } from '../types/border';
import { isNonNegativeNumber } from './isNonNegativeNumber';

export function correctBorderRadiusObject(
    cornersRadius: CornersRadius,
    defaultVal: CornersRadius
): CornersRadius {
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

    return cornersRadius;
}
