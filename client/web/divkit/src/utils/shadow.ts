import type { MaybeMissing } from '../expressions/json';
import type { Shadow } from '../types/border';
import { correctColor } from './correctColor';
import { pxToEm } from './pxToEm';

/**
 * Converts to box-shadow
 * @param shadow
 * @returns css style
 */
export function shadowToCssBoxShadow(shadow: MaybeMissing<Shadow>): string {
    return pxToEm(shadow.offset?.x?.value || 0) + ' ' +
        pxToEm(shadow.offset?.y?.value || 0) + ' ' +
        pxToEm(shadow.blur ?? 2) + ' ' +
        correctColor(shadow.color || '#000000', shadow.alpha ?? 0.19);
}

/**
 * Converts to filter drop-shadow
 * @param shadow
 * @param fontSize
 * @returns css style
 */
export function shadowToCssFilter(shadow: MaybeMissing<Shadow>, fontSize: number): string {
    return 'drop-shadow(' +
        correctColor(shadow.color || '#000000', shadow.alpha ?? 0.19) + ' ' +
        pxToEm((shadow.offset?.x?.value || 0) * 10 / fontSize) + ' ' +
        pxToEm((shadow.offset?.y?.value || 0) * 10 / fontSize) + ' ' +
        pxToEm((shadow.blur ?? 2) * 10 / fontSize) + ')';
}
