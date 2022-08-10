import { isPositiveNumber } from '../../src/utils/isPositiveNumber';

describe('isPositiveNumber', () => {
    test('numbers', () => {
        expect(isPositiveNumber(1)).toBe(true);
        expect(isPositiveNumber(-1)).toBe(false);
        expect(isPositiveNumber(0)).toBe(false);
    });

    test('non-numbers', () => {
        // @ts-expect-error test wrong types
        expect(isPositiveNumber('1')).toBe(true);
        // @ts-expect-error test wrong types
        expect(isPositiveNumber(true)).toBe(false);
        // @ts-expect-error test wrong types
        expect(isPositiveNumber(null)).toBe(false);
        // @ts-expect-error test wrong types
        expect(isPositiveNumber([1])).toBe(false);
        expect(isPositiveNumber(undefined)).toBe(false);
    });
});
