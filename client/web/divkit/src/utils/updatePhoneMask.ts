import { PhoneInputMask } from './mask/phoneInputMask';
import type { LogError } from './wrapError';

export function updatePhoneMask(
    logError: LogError,
    oldValue?: PhoneInputMask | null
): PhoneInputMask | null {
    if (oldValue) {
        return oldValue;
    }
    return new PhoneInputMask(logError);
}
