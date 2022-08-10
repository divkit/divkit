import type { EdgeInsets } from '../types/edgeInserts';
import { isPositiveNumber } from './isPositiveNumber';
import { edgeInsertsToCss } from './edgeInsertsToCss';
import { isNonNegativeNumber } from './isNonNegativeNumber';

export function correctEdgeInserts(edgeInsets: EdgeInsets | undefined, defaultVal: string): string {
    if (!edgeInsets) {
        return defaultVal;
    }

    const list = [
        edgeInsets.top,
        edgeInsets.right,
        edgeInsets.bottom,
        edgeInsets.left
    ];

    for (let i = 0; i < list.length; ++i) {
        if (list[i] && !isNonNegativeNumber(list[i])) {
            return defaultVal;
        }
    }

    return edgeInsertsToCss(edgeInsets);
}
