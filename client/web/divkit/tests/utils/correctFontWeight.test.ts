import { correctFontWeight } from '../../src/utils/correctFontWeight';

describe('correctFontWeight', () => {
    test('simple', () => {
        expect(correctFontWeight(undefined, undefined, 123)).toBe(123);
        expect(correctFontWeight('light', undefined, 123)).toBe(300);
        expect(correctFontWeight('regular', undefined, 123)).toBe(400);
        expect(correctFontWeight('medium', undefined, 123)).toBe(500);
        expect(correctFontWeight('bold', undefined, 123)).toBe(700);
        // @ts-expect-error wrong type
        expect(correctFontWeight('smth', undefined, 123)).toBe(123);
    });

    test('numeric', () => {
        expect(correctFontWeight(undefined, undefined, 123)).toBe(123);
        expect(correctFontWeight(undefined, 300, 123)).toBe(300);
        expect(correctFontWeight(undefined, 0, 123)).toBe(123);
        expect(correctFontWeight(undefined, -100, 123)).toBe(123);
    });

    test('mixed', () => {
        expect(correctFontWeight('light', 330, 123)).toBe(330);
    });
});
