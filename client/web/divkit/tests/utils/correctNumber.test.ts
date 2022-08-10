import { correctNumber } from '../../src/utils/correctNumber';

describe('correctNumber', () => {
    test('simple', () => {
        expect(correctNumber(123, 456)).toBe(123);
        expect(correctNumber(-123, 456)).toBe(-123);
        expect(correctNumber(0, 456)).toBe(0);
        expect(correctNumber(undefined, 456)).toBe(456);
    });
});
