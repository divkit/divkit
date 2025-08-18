import type { EdgeInsets } from '../types/edgeInserts';
import { isNonNegativeNumber } from './isNonNegativeNumber';

export function correctEdgeInsertsObject<T extends EdgeInsets | null>(
    edgeInsets: EdgeInsets | undefined,
    defaultVal: T
): T {
    if (!edgeInsets || typeof edgeInsets !== 'object') {
        return defaultVal;
    }

    const list = [
        'top',
        'right',
        'bottom',
        'left'
    ] as const;

    for (let i = 0; i < list.length; ++i) {
        if (edgeInsets[list[i]] && !isNonNegativeNumber(edgeInsets[list[i]])) {
            return defaultVal;
        }
    }

    return edgeInsets as T;
}
