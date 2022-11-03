import type { EdgeInsets } from '../types/edgeInserts';

export function sumEdgeInsets(a: EdgeInsets | null, b: EdgeInsets | null): EdgeInsets {
    if (!a && !b) {
        return {};
    }

    if (!b) {
        return a as EdgeInsets;
    }

    if (!a) {
        return b;
    }

    const res: EdgeInsets = {};

    ([
        'top',
        'right',
        'bottom',
        'left'
    ] as const).forEach(side => {
        const aVal = a[side];
        if (aVal) {
            res[side] = aVal;
        }
        const bVal = b[side];
        if (bVal) {
            res[side] = (res[side] || 0) + bVal;
        }
    });

    return res;
}
