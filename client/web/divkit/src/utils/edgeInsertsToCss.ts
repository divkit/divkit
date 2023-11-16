import type { EdgeInsets } from '../types/edgeInserts';
import { pxToEm } from './pxToEm';

export function edgeInsertsToCss(edgeInsets: EdgeInsets): string {
    const top = edgeInsets.top || 0;
    const right = edgeInsets.right || 0;
    const bottom = edgeInsets.bottom || 0;
    const left = edgeInsets.left || 0;

    if (top === 0 && right === 0 && bottom === 0 && left === 0) {
        return '';
    }

    return pxToEm(top) +
        ' ' +
        pxToEm(right) +
        ' ' +
        pxToEm(bottom) +
        ' ' +
        pxToEm(left);
}
