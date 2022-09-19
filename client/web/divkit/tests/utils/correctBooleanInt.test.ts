import { correctBooleanInt } from '../../src/utils/correctBooleanInt';

describe('correctBooleanInt', () => {
    test('simple', () => {
        expect(correctBooleanInt(2, false)).toBe(false);
        expect(correctBooleanInt(2, true)).toBe(true);
        expect(correctBooleanInt(1, false)).toBe(true);
        expect(correctBooleanInt(1, true)).toBe(true);
        expect(correctBooleanInt(0, false)).toBe(false);
        expect(correctBooleanInt(0, true)).toBe(false);
        expect(correctBooleanInt(true, false)).toBe(true);
        expect(correctBooleanInt(true, true)).toBe(true);
        expect(correctBooleanInt(false, false)).toBe(false);
        expect(correctBooleanInt(false, true)).toBe(false);
        expect(correctBooleanInt(undefined, false)).toBe(false);
        expect(correctBooleanInt(undefined, true)).toBe(true);
    });
});
