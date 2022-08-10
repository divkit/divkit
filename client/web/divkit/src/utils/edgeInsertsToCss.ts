import type { EdgeInsets } from '../types/edgeInserts';
import { pxToEm } from './pxToEm';

export function edgeInsertsToCss(edgeInsets: EdgeInsets): string {
    return pxToEm(edgeInsets.top || 0) +
        ' ' +
        pxToEm(edgeInsets.right || 0) +
        ' ' +
        pxToEm(edgeInsets.bottom || 0) +
        ' ' +
        pxToEm(edgeInsets.left || 0);
}
