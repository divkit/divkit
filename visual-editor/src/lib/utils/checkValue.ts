import type { StringValueFilter } from '../../lib';

export function checkStringValue(
    value: string | number | undefined,
    filter?: StringValueFilter
): boolean {
    if (!value) {
        return true;
    } else if (typeof filter === 'function') {
        return filter(String(value));
    } else if (filter instanceof RegExp) {
        return filter.test(String(value));
    }

    return true;
}
