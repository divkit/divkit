import { pxToEm } from '../../src/utils/pxToEm';

describe('pxToEm', () => {
    test('numbers', () => {
        expect(pxToEm(1)).toBe('0.1em');
        expect(pxToEm(2)).toBe('0.2em');
        expect(pxToEm(10)).toBe('1em');
        expect(pxToEm(20)).toBe('2em');
    });

    test('strings', () => {
        // @ts-expect-error Прокидываем строку вместо числа
        expect(pxToEm('1')).toBe('0.1em');
        // @ts-expect-error Прокидываем строку вместо числа
        expect(pxToEm('2')).toBe('0.2em');
        // @ts-expect-error Прокидываем строку вместо числа
        expect(pxToEm('10')).toBe('1em');
        // @ts-expect-error Прокидываем строку вместо числа
        expect(pxToEm('20')).toBe('2em');
    });

    test('rounding', () => {
        expect(pxToEm(1 / 3)).toBe('0.0334em');
        expect(pxToEm(5 / 7)).toBe('0.0715em');
    });

    test('NaN', () => {
        // @ts-expect-error Прокидываем не число
        expect(pxToEm(null)).toBe('0');
        // @ts-expect-error Прокидываем не число
        expect(pxToEm(false)).toBe('0');
        // @ts-expect-error Прокидываем не число
        expect(pxToEm(true)).toBe('0');
        expect(pxToEm(NaN)).toBe('0');
        // @ts-expect-error Прокидываем не число
        expect(pxToEm(undefined)).toBe('0');
        // @ts-expect-error Прокидываем не число
        expect(pxToEm('')).toBe('0');
        // @ts-expect-error Прокидываем не число
        expect(pxToEm('abc')).toBe('0');
        // @ts-expect-error Прокидываем не число
        expect(pxToEm({})).toBe('0');
    });
});
