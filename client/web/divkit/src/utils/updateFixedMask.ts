import type { LogError } from './wrapError';
import type { MaskData } from './mask/baseInputMask';
import type { MaybeMissing } from '../expressions/json';
import type { FixedLengthInputMask as FixedLengthInputMaskType } from '../types/input';
import { FixedLengthInputMask } from './mask/fixedLengthInputMask';

export function updateFixedMask(
    mask: MaybeMissing<FixedLengthInputMaskType>,
    logError: LogError,
    oldValue?: FixedLengthInputMask | null
): FixedLengthInputMask | null {
    if (
        typeof mask.pattern === 'string' && Array.isArray(mask.pattern_elements) &&
        mask.pattern_elements.every(it => it.key && typeof it.key === 'string')
    ) {
        const maskData: MaskData = {
            pattern: mask.pattern,
            alwaysVisible: Boolean(mask.always_visible),
            decoding: mask.pattern_elements.map(it => ({
                key: it.key as string,
                filter: it.regex && typeof it.regex === 'string' ? it.regex : undefined,
                placeholder: it.placeholder && typeof it.placeholder === 'string' ? it.placeholder : '_'
            }))
        };

        if (oldValue) {
            oldValue.updateMaskData(maskData);
            return oldValue;
        }
        return new FixedLengthInputMask(maskData, logError);
    }

    return oldValue || null;
}
