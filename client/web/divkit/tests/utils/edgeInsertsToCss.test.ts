import { edgeInsertsToCss } from '../../src/utils/edgeInsertsToCss';

describe('edgeInsertsToCss', () => {
    test('simple', () => {
        expect(edgeInsertsToCss({
        })).toBe('0 0 0 0');

        expect(edgeInsertsToCss({
            top: 10
        })).toBe('1em 0 0 0');

        expect(edgeInsertsToCss({
            top: 10,
            right: 20,
            bottom: 30,
            left: 40
        })).toBe('1em 2em 3em 4em');
    });
});
