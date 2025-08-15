import type { MaybeMissing } from '../expressions/json';
import type { CurrencyInputMask as CurrencyInputMaskType } from '../types/input';
import { CurrencyInputMask } from './mask/currencyInputMask';
import type { LogError } from './wrapError';

export function updateCurrencyMask(
    mask: MaybeMissing<CurrencyInputMaskType>,
    logError: LogError,
    oldValue?: CurrencyInputMask | null
): CurrencyInputMask | null {
    if (oldValue) {
        oldValue.updateCurrencyParams(mask.locale);
        return oldValue;
    }
    return new CurrencyInputMask(mask.locale, logError);
}
