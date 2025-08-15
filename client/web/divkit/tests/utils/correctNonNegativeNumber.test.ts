import {
    describe,
    expect,
    test
} from 'vitest';

import { correctNonNegativeNumber } from '../../src/utils/correctNonNegativeNumber';

describe('correctNonNegativeNumber', () => {
    test('simple', () => {
        expect(correctNonNegativeNumber(123, 456)).toBe(123);
        expect(correctNonNegativeNumber(-123, 456)).toBe(456);
        expect(correctNonNegativeNumber(0, 456)).toBe(0);
        expect(correctNonNegativeNumber(undefined, 456)).toBe(456);
    });
});
