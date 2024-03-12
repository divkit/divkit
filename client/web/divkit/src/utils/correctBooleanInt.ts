import { previewValue } from './previewValue';
import { LogError, wrapError } from './wrapError';

export function correctBooleanInt(
    val: unknown,
    defaultVal: boolean,
    logError: LogError
): boolean {
    if (val === 1 || val === 0 || val === false || val === true) {
        return Boolean(val);
    }
    if (val !== undefined) {
        logError(wrapError(new Error(`Invalid value: ${previewValue(val)}. Expression<Bool> expected.`)));
    }
    return defaultVal;
}
