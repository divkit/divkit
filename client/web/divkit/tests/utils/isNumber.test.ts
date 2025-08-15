import {
    describe,
    expect,
    test
} from 'vitest';

import { isNumber } from '../../src/utils/isNumber';

describe('isNumber', () => {
    test('numbers', () => {
        expect(isNumber(1)).toBe(true);
        expect(isNumber(-1)).toBe(true);
        expect(isNumber(0)).toBe(true);
    });

    test('non-numbers', () => {
        // @ts-expect-error test wrong types
        expect(isNumber('1')).toBe(true);
        // @ts-expect-error test wrong types
        expect(isNumber(true)).toBe(false);
        // @ts-expect-error test wrong types
        expect(isNumber(null)).toBe(false);
        // @ts-expect-error test wrong types
        expect(isNumber([1])).toBe(false);
        expect(isNumber(undefined)).toBe(false);
    });
});
