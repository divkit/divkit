import type { MaybeMissing } from '../expressions/json';
import type { EdgeInsets } from '../types/edgeInserts';

export function edgeInsertsMultiply(
    edgeInsets: MaybeMissing<EdgeInsets>,
    multiply: number
): MaybeMissing<EdgeInsets> {
    if (!edgeInsets) {
        return {};
    }

    const res: EdgeInsets = {};

    for (const key of ['left', 'top', 'right', 'bottom', 'start', 'end'] as const) {
        const val = edgeInsets[key];
        if (val) {
            res[key] = val * multiply;
        }
    }

    return res;
}
