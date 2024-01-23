import { edgeInsertsToCss } from '../../src/utils/edgeInsertsToCss';

describe('edgeInsertsToCss', () => {
    test('simple', () => {
        expect(edgeInsertsToCss({
        }, 'ltr')).toBe('');

        expect(edgeInsertsToCss({
            top: 10
        }, 'ltr')).toBe('1em 0 0 0');

        expect(edgeInsertsToCss({
            top: 10,
            right: 20,
            bottom: 30,
            left: 40
        }, 'ltr')).toBe('1em 2em 3em 4em');
    });

    test('ltr', () => {
        expect(edgeInsertsToCss({
            top: 10,
            end: 30,
            right: 20
        }, 'ltr')).toBe('1em 3em 0 0');
    });

    test('rtl', () => {
        expect(edgeInsertsToCss({
            top: 10,
            end: 30,
            right: 20
        }, 'ltr')).toBe('1em 3em 0 0');
    });
});
