import {
    describe,
    expect,
    test
} from 'vitest';

import { correctPositiveNumber } from '../../src/utils/correctPositiveNumber';

describe('correctPositiveNumber', () => {
    test('simple', () => {
        expect(correctPositiveNumber(123, 456)).toBe(123);
        expect(correctPositiveNumber(-123, 456)).toBe(456);
        expect(correctPositiveNumber(0, 456)).toBe(456);
        expect(correctPositiveNumber(undefined, 456)).toBe(456);
    });
});
