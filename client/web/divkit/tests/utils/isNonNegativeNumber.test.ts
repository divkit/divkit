import {
    describe,
    expect,
    test
} from 'vitest';

import { isNonNegativeNumber } from '../../src/utils/isNonNegativeNumber';

describe('isNonNegativeNumber', () => {
    test('numbers', () => {
        expect(isNonNegativeNumber(1)).toBe(true);
        expect(isNonNegativeNumber(-1)).toBe(false);
        expect(isNonNegativeNumber(0)).toBe(true);
    });

    test('non-numbers', () => {
        // @ts-expect-error test wrong types
        expect(isNonNegativeNumber('1')).toBe(true);
        // @ts-expect-error test wrong types
        expect(isNonNegativeNumber(true)).toBe(false);
        // @ts-expect-error test wrong types
        expect(isNonNegativeNumber(null)).toBe(false);
        // @ts-expect-error test wrong types
        expect(isNonNegativeNumber([1])).toBe(false);
        expect(isNonNegativeNumber(undefined)).toBe(false);
    });
});
