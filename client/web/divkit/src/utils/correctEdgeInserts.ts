import type { Direction } from '../../typings/common';
import type { EdgeInsets } from '../types/edgeInserts';
import { edgeInsertsToCss } from './edgeInsertsToCss';
import { isNonNegativeNumber } from './isNonNegativeNumber';

export function correctEdgeInserts(
    edgeInsets: EdgeInsets | undefined,
    direction: Direction,
    defaultVal: string
): string {
    if (!edgeInsets) {
        return defaultVal;
    }

    const list = [
        edgeInsets.top,
        (direction === 'ltr' ? edgeInsets.end : edgeInsets.start) ?? edgeInsets.right,
        edgeInsets.bottom,
        (direction === 'ltr' ? edgeInsets.start : edgeInsets.end) ?? edgeInsets.left
    ];

    for (let i = 0; i < list.length; ++i) {
        if (list[i] && !isNonNegativeNumber(list[i])) {
            return defaultVal;
        }
    }

    return edgeInsertsToCss(edgeInsets, direction);
}
