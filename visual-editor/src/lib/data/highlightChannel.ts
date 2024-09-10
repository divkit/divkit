import type { Range } from '../utils/stringifyWithLoc';
import type { TypedRange } from './editor';

export function combineRanges(highlightRanges: Range[] | null, selectedRanges: Range[] | null): TypedRange[] {
    const res: TypedRange[] = [];

    if (highlightRanges) {
        res.push(...highlightRanges.map(range => ({ type: 'highlight' as const, range })));
    }
    if (selectedRanges) {
        res.push(...selectedRanges.map(range => ({ type: 'select' as const, range })));
    }

    return res;
}
