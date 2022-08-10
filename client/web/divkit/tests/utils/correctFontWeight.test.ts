import { correctFontWeight } from '../../src/utils/correctFontWeight';

describe('correctFontWeight', () => {
    test('simple', () => {
        expect(correctFontWeight(undefined, 123)).toBe(123);
        expect(correctFontWeight('light', 123)).toBe(300);
        expect(correctFontWeight('regular', 123)).toBe(400);
        expect(correctFontWeight('medium', 123)).toBe(500);
        expect(correctFontWeight('bold', 123)).toBe(700);
        // @ts-expect-error wrong type
        expect(correctFontWeight('smth', 123)).toBe(123);
    });
});
